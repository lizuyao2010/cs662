package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class perform feature extraction for simplified hypothesis
 * 
 **/

public class Features {
	public Hypothesis _hyp;
	public SentenceData _sent;
	
	HashMap<String, Integer> _dependencyType = new HashMap<String, Integer>();
	
	public Features(){		
	}
	
	public Features(Hypothesis h, SentenceData s){
		_hyp  = h;
		_sent = s;
	}

	public ArrayList<Float> getFeaturesVector() {
		ArrayList<Float> hf                  = new ArrayList<Float>();
		ArrayList<Float> relational_features = new ArrayList<Float>(); 
		ArrayList<Float> np_coverage         = new ArrayList<Float>();
		ArrayList<Float> vp_coverage         = new ArrayList<Float>();
		ArrayList<Float> np1_cross_sent      = new ArrayList<Float>();
		ArrayList<Float> readability         = new ArrayList<Float>();
		ArrayList<Float> hypsize             = new ArrayList<Float>();
		
		ArrayList<Float> sent1               = new ArrayList<Float>();
		ArrayList<Float> sent2               = new ArrayList<Float>();
		
		ArrayList<Float> sent1_dtype         = new ArrayList<Float>();
		ArrayList<Float> sent2_dtype         = new ArrayList<Float>();
		
		
		np_coverage                    = getNP_Coverage();
		vp_coverage                    = getVP_Coverage();
		np1_cross_sent                 = getNP1_CrossSent();
		
		readability                    = getReadability();
		
		hypsize                        = getHypSizeBinary();
		
		float normalized_ed1           = getNormalized_EditDistance();
		float normalized_wordcount     = getNormalized_WordCount();
		float normalized_missing_np    = getNormalized_MisingNP();		
		
		sent1_dtype                    = getSentDType(_hyp.get(0));
		sent2_dtype                    = getSentDType(_hyp.get(1));		
		
		sent1                          = getSentFeat(_hyp.get(0));
		sent2                          = getSentFeat(_hyp.get(1));
		relational_features            = getAverageSentFeat();
		
		hf.add( (float) _hyp.size() );  //1
		hf.addAll( hypsize );           //5
		
		hf.addAll(np_coverage);         //4
		hf.addAll(vp_coverage);         //4
		hf.addAll(np1_cross_sent);      //2
		hf.addAll(readability);         //6
		
		hf.add(normalized_ed1);		    //1
		hf.add(normalized_wordcount);	//1	
		hf.add(normalized_missing_np);  //1
		
		hf.addAll(sent1_dtype);         //46
		hf.addAll(sent2_dtype);         //46
		
		hf.addAll(sent1);               //20
		hf.addAll(sent2);               //20		
		hf.addAll(relational_features); //20
		
		return hf;
	}

	//binary feature for hyp size
	//size [2,3,4,5, other]
	private ArrayList<Float> getHypSizeBinary() {
		ArrayList<Float> f = new ArrayList<Float>();
		for(int i=0; i< 5; i++){
			f.add(i, 0.0f);			
		}
		
		if (_hyp.size() == 2){ f.set(0, 1.0f); }
		if (_hyp.size() == 3){ f.set(1, 1.0f); }
		if (_hyp.size() == 4){ f.set(2, 1.0f); }
		if (_hyp.size() == 5){ f.set(3, 1.0f); }
		if (_hyp.size() > 5) { f.set(4, 1.0f); }
		
		return f;
	}

	/**
	 * Dependency type feature vector for a sentence
	 * 46 features
	 **/
	private ArrayList<Float> getSentDType(Relation rel) {
		ArrayList<Float> f = new ArrayList<Float>();
		
		init_dependency_type();
		
		ArrayList<Dependency> tdtree = _sent.getTypedDependencies();
		
		HashMap<String, String> td = new HashMap<String, String>();
		
		for(int i =0; i< tdtree.size(); i++){
			Token t1    = tdtree.get(i).first();
			Token t2    = tdtree.get(i).second();
			String type = tdtree.get(i).type();
			
			String t1t2 = t1.toString() +  t2.toString();
			String t2t1 = t2.toString() +  t1.toString();
			
			//System.out.println("pair = |||" + t1t2 + "||| type = " + type);
			
			td.put(t1t2, type);
			td.put(t2t1, type);
		}		
		
		getDependenciesType(rel._r, rel._e1, td);
		getDependenciesType(rel._r, rel._e2, td);
		getDependenciesType(rel._e1, rel._e1, td);
		getDependenciesType(rel._e2, rel._e2, td);
		getDependenciesType(rel._e1, rel._e2, td);		
		
		for(Entry<String, Integer> e : _dependencyType.entrySet()){			
			//System.out.println(e.getKey() + ": " + e.getValue());
			float val = e.getValue();
			f.add(val);
		}
		
//		System.out.println("size  of f = " + f.size() + "\n"+f);
		return f;
	}

	private void getDependenciesType(Token[] tok1, Token[] tok2, HashMap<String, String> td) {
		for(int i =0; i< tok1.length; i++ ){
			for(int j =0; j< tok2.length; j++ ){
				
				String dt = getDepType( tok1[i], tok2[j], td);
				
				if (!dt.isEmpty()){
					try{
						int val = _dependencyType.get(dt);
						_dependencyType.put(dt, val + 1);
					}catch(Exception e){
						System.out.println("ERROR!!!DT = "+dt +"\nSENT = "+ _sent.toString() + "\nHYP = "+ _hyp.toOneString());
					}
					
					
				}				
			}
		}		
	}

	//undirected edge
	private String getDepType(Token t1, Token t2, HashMap<String, String> td) {
		String type = "";
		
		String t1t2 = t1.toString() + " " + t2.toString();
		String t2t1 = t2.toString() + " " + t1.toString();
		//System.out.println("|||"+ t1t2 +"|||");

		if (td.containsKey(t1t2)){
			type = td.get(t1t2);			
		}else if (td.containsKey(t2t1) ){
			type = td.get(t2t1);			
		}
		
		return type;
	}

	/**
	 * Readability scores
	 * Compute Flesch, Fog, Kincaid, SMOG, Automatic Reading Index, and average all scores for a hypothesis
	 **/
	private ArrayList<Float> getReadability() {
		ArrayList<Float> f = new ArrayList<Float>();
		
		Fathom.Stats FatStat = Fathom.analyze(_hyp.toOneNiceString());
		
		double nsyl          = FatStat.getNumSyllables();
		double nword         = FatStat.getNumWords();
		double nsent         = FatStat.getNumSentences();
		double ncomplexword  = FatStat.getNumComplexWords();
		double npolysyl      = FatStat.getNumPolySyllables();
		double nletter       = FatStat.getNumLetters(); 
		
		double words_per_sentence    = nword / nsent;		
		double syllables_per_word    = nsyl / nword;
		double percent_complex_words = (ncomplexword / nword) * 100;
		double poly_per_sentence     = npolysyl / nsent;		
		double letter_per_word       = nletter / nword;
		
		float flesh   = (float) (206.835 - (1.015 * words_per_sentence )  - (84.6 * syllables_per_word)) ;  
		float fog     = (float) (( words_per_sentence + percent_complex_words ) * 0.4);
		float kincaid = (float) ((11.8 * syllables_per_word) + (0.39 * words_per_sentence) - 15.59);		
		float smog    = (float) (1.043 * Math.sqrt(30 * poly_per_sentence) + 3.1291 );		
		float ari     = (float) (4.71 * letter_per_word + 0.5 * words_per_sentence - 21.43);
		
		float average = (flesh + fog + kincaid + smog + ari)/5;
		
		f.add(flesh);
		f.add(fog);
		f.add(kincaid);
		f.add(smog);
		f.add(ari);
		f.add(average);
		
		return f;
	}

	/**
	 * get feature from a simplified sentence
	 **/
	private ArrayList<Float> getSentFeat(Relation rel) {
		ArrayList<Float> f = new ArrayList<Float>();
		for(int i=0; i< _hyp.get(0)._features.size(); i++){ f.add(new Float(0)); }
		
		for (int i=0; i< rel._features.size(); i++){
			 float val = Float.parseFloat(rel._features.get(i));
			 f.set(i, f.get(i) + val);
		}
		
		return f;
	}

	/**
	 * Overlapping between Objects and Subject of sentence 1 and 2
	 **/
	private ArrayList<Float> getNP1_CrossSent() {
		ArrayList<Float> f = new ArrayList<Float>();
		
		String np1s1 = _hyp.get(0).e1toString();
		String np2s1 = _hyp.get(0).e2toString();
		
		String np1s2 = _hyp.get(1).e1toString();
		
		float ed1 = EditDistance(np1s1, np1s2);
		float ed2 = EditDistance(np2s1, np1s2);
		
		f.add(ed1);
		f.add(ed2);
		
		return f;
	}

	/**
	 * how many base NPs are covered by this hypothesis
	 * 
	 * how many base NPs are covered by the 1st sentence
	 * how many base NPs are covered by the 2nd sentence
	 **/
	private ArrayList<Float> getNP_Coverage() {
		ArrayList<Float> npc = new ArrayList<Float>();
		
		ArrayList<String> NPlist = _sent.NPtoString(); // base NPs list
		
		Map<String, Integer> CoverNP = new HashMap<String, Integer>();
		for(int i =0; i< NPlist.size(); i++){
			CoverNP.put(NPlist.get(i), 0);
		}

		ArrayList<String> hyp_NPlist = new ArrayList<String> (); // NPs list from hypothesis			
			
		for (int j=0; j< _hyp.size(); j++){
			hyp_NPlist.add( _hyp.get(j).e1toString() );
			hyp_NPlist.add( _hyp.get(j).e2toString() );							
		}	
		
		for (int k=0; k< NPlist.size(); k++){			
			for (int m=0; m< hyp_NPlist.size(); m++){
				if ( hyp_NPlist.get(m).contains(NPlist.get(k)) ) { 	
					CoverNP.put(NPlist.get(k), 1);					
				}
			}			
		}			
		
		float np_coverage = 0;
		for(int i =0; i< NPlist.size(); i++){
			np_coverage += CoverNP.get(NPlist.get(i));
		}		
		
		float np_coverage_s1 = 0;
		String s1e1 = _hyp.get(0).e1toString();
		String s1e2 = _hyp.get(0).e2toString();		
		for(int i =0; i< NPlist.size(); i++){			
			if ( s1e1.contains(NPlist.get(i)) || s1e2.contains(NPlist.get(i)) ) { 	
				np_coverage_s1 ++;					
			}
		}
		
		float np_coverage_s2 = 0;
		String s2e1 = _hyp.get(1).e1toString();
		String s2e2 = _hyp.get(1).e2toString();		
		for(int i =0; i< NPlist.size(); i++){			
			if ( s2e1.contains(NPlist.get(i)) || s2e2.contains(NPlist.get(i)) ) { 	
				np_coverage_s2 ++;					
			}
		}
		
		float normalize_np_c = np_coverage / _hyp.size();  
		
		npc.add(np_coverage);
		npc.add(normalize_np_c);
		npc.add(np_coverage_s1);
		npc.add(np_coverage_s2);
		
		return npc;
	}

	/**
	 * how many base VPs are covered by this hypothesis
	 * 
	 * how many base VPs are covered by the 1st sentence
	 * how many base VPs are covered by the 2nd sentence
	 **/
	private ArrayList<Float> getVP_Coverage() {
		ArrayList<Float> vpc = new ArrayList<Float>();
		
		ArrayList<String> VPlist = _sent.VPtoString(); // base NPs list
		
		Map<String, Integer> CoverVP = new HashMap<String, Integer>();
		for(int i =0; i< VPlist.size(); i++){
			CoverVP.put(VPlist.get(i), 0);
		}

		ArrayList<String> hyp_VPlist = new ArrayList<String> (); // NPs list from hypothesis			
			
		for (int j=0; j< _hyp.size(); j++){
			hyp_VPlist.add( _hyp.get(j).rtoString() );										
		}	
		
		for (int k=0; k< VPlist.size(); k++){			
			for (int m=0; m< hyp_VPlist.size(); m++){
				if ( hyp_VPlist.get(m).contains(VPlist.get(k)) ) { 	
					CoverVP.put(VPlist.get(k), 1);					
				}
			}			
		}			
		
		float vp_coverage = 0;
		for(int i =0; i< VPlist.size(); i++){
			vp_coverage += CoverVP.get(VPlist.get(i));
		}		
		
		float vp_coverage_s1 = 0;
		String s1r = _hyp.get(0).rtoString();		
		for(int i =0; i< VPlist.size(); i++){			
			if ( s1r.contains(VPlist.get(i))) { 	
				vp_coverage_s1 ++;					
			}
		}
		
		float vp_coverage_s2 = 0;
		String s2r = _hyp.get(1).rtoString();		
		for(int i =0; i< VPlist.size(); i++){			
			if ( s2r.contains(VPlist.get(i))) { 	
				vp_coverage_s2 ++;					
			}
		}
		
		float normalize_vp_c = vp_coverage / _hyp.size();
		
		vpc.add(vp_coverage);
		vpc.add(normalize_vp_c);
		vpc.add(vp_coverage_s1);
		vpc.add(vp_coverage_s2);
		
		return vpc;
	}
	
	
	/**
	 * Set of features from each individual simplified sentence 
	 **/
	private ArrayList<Float> getAverageSentFeat() {
		ArrayList<Float> f =  new ArrayList<Float>();
		for(int i=0; i< _hyp.get(0)._features.size(); i++){ f.add(new Float(0)); }
		
		for (int j=0; j< _hyp.size(); j++){
			Relation rel = _hyp.get(j);
			for (int i=0; i< rel._features.size(); i++){
				 float val = Float.parseFloat(rel._features.get(i));
				 f.set(i, f.get(i) + val);
			}
		}
		
		//normalize by number of simplified sentences used to generate the hypothesis
		for (int j=0; j< f.size(); j++){
			f.set(j, f.get(j)/_hyp.size());
		}
		
		return f;
	}	

	/**
	 * On average how many NPs a simplified sentence are missed
	 **/
	private float getNormalized_MisingNP() {		
		ArrayList<String> NPlist = _sent.NPtoString();		
		float o1_missing_np = NPlist.size();
		float normalized_missing_np = NPlist.size();
		
		HashMap <String, Integer> o1_NPlist = new HashMap <String, Integer> ();				
		for (int j=0; j< _hyp.size(); j++){
			o1_NPlist.put( _hyp.get(j).e1toString(), 1);
			o1_NPlist.put( _hyp.get(j).e2toString(), 1);							
		}		
		for(int i = 0; i < NPlist.size(); i++ ){
			if (o1_NPlist.containsKey(NPlist.get(i))){
				o1_missing_np --;
			}							
		}
		normalized_missing_np = o1_missing_np / _hyp.size();
		
		return normalized_missing_np;
	}

	private float getNormalized_WordCount() {		
		float h_wordcount = 0;		
		float normalized_wordcount = 0;
		
		for (int j=0; j< _hyp.size(); j++){
			h_wordcount += _hyp.get(j)._e1.length +  _hyp.get(j)._e2.length +  _hyp.get(j)._r.length;
		}		
		normalized_wordcount = h_wordcount / _hyp.size();
		return normalized_wordcount;
	}

	private float getNormalized_EditDistance() {
		String rawSentence = _sent.getRawSentenceString();		
		String o1_vp_np2 = "";		

		//Edit distance: only compute for VP and NP2
		for (int j=0; j< _hyp.size(); j++){
			o1_vp_np2 += _hyp.get(j).toRawString(_hyp.get(j)._r) + " " + _hyp.get(j).toRawString(_hyp.get(j)._e2) + " ";
		}
		
		float ed1 = EditDistance(o1_vp_np2, rawSentence);
		float normalized_ed1 = ed1 / _hyp.size();

		return normalized_ed1;
	}
	
	private int EditDistance(String str1, String str2){		
		String[] s1 = str1.split(" ");
		String[] s2 = str2.split(" ");
		
		int m = s1.length;
		int n = s2.length;
		int cost;
		
		// d is a table with m+1 rows and n+1 columns declare		
		int d[][] = new int[m+1][n+1];
				
		for (int i = 0; i <= m; i++) { d[i][0] = i; }
		for (int j = 0; j <= n; j++) { d[0][j] = j; }		
		
		for (int j = 1; j <= n; j++ ){ 
			for (int i = 1; i <= m; i++) {		
				if ( s1[i-1].equals(s2[j-1]) ) {					
					cost = 0;
				}  
				else { cost = 1; }
				
				d[i][j] = minimum ( d[i-1][j] + 1, d[i][j-1] + 1, d[i-1][j-1] + cost);								
			} 
		}
		
		//System.out.format("DEBUG EditDistance = %d , str1 = %s%n", d[m][n], str1 );
		return d[m][n];
	}
	
	/**
	* Get minimum of three values 
	**/
	private int minimum (int a, int b, int c) { 
		int mi; 
		mi = a; 
		if (b < mi) { mi = b; } 
		if (c < mi) { mi = c; } 
		return mi; 
	}
	
	/**
	 * Initialize typed dependencies list
	 */
	private void init_dependency_type(){
		_dependencyType.clear();
		
		_dependencyType.put("abbrev", 0);
		_dependencyType.put("acomp", 0);
		_dependencyType.put("advcl", 0);
		_dependencyType.put("advmod", 0);
		_dependencyType.put("amod", 0);
		_dependencyType.put("appos", 0);
		_dependencyType.put("attr", 0);
		_dependencyType.put("aux", 0);
		_dependencyType.put("auxpass", 0);
		_dependencyType.put("cc", 0);
		_dependencyType.put("ccomp", 0);
		_dependencyType.put("complm", 0);
		_dependencyType.put("conj", 0);
		_dependencyType.put("cop", 0);
		_dependencyType.put("csubj", 0);
		_dependencyType.put("csubjpass", 0);
		_dependencyType.put("dep", 0);
		_dependencyType.put("det", 0);
		_dependencyType.put("dobj", 0);
		_dependencyType.put("expl", 0);
		_dependencyType.put("infmod", 0);
		_dependencyType.put("iobj", 0);
		_dependencyType.put("mark", 0);
		_dependencyType.put("measure", 0);
		_dependencyType.put("neg", 0);
		_dependencyType.put("nn", 0);
		_dependencyType.put("nsubj", 0);
		_dependencyType.put("nsubjpass", 0);
		_dependencyType.put("num", 0);
		_dependencyType.put("number", 0);
		_dependencyType.put("parataxis", 0);
		_dependencyType.put("partmod", 0);
		_dependencyType.put("pcomp", 0);
		_dependencyType.put("pobj", 0);
		_dependencyType.put("poss", 0);
		_dependencyType.put("possessive", 0);
		_dependencyType.put("preconj", 0);
		_dependencyType.put("predet", 0);
		_dependencyType.put("prep", 0);
		_dependencyType.put("prt", 0);
		_dependencyType.put("purpcl", 0);
		_dependencyType.put("quantmod", 0);
		_dependencyType.put("rcmod", 0);
		_dependencyType.put("rel", 0);
		_dependencyType.put("tmod", 0);
		_dependencyType.put("xcomp", 0);		
	}


}
