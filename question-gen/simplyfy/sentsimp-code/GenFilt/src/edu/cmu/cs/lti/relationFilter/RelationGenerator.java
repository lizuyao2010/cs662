package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.util.StringUtils;

public class RelationGenerator {
	
	private ArrayList <ArrayList<Token>> NP = new ArrayList <ArrayList<Token>> ();
	private ArrayList <ArrayList<Token>> VP = new ArrayList <ArrayList<Token>> ();

	public ArrayList <ArrayList<Token>> getBaseNP(){ return NP; }
	
	public ArrayList <ArrayList<Token>> getBaseVP(){ return VP; }
	
	static int MAX = 10000;
	
	public ArrayList<Relation> GenerateRelations(String sentence, int verbosity){
		ArrayList<Relation> relations = new ArrayList<Relation>();
		
		Sentence s = new Sentence(sentence, verbosity);

		//clean-up NP and VP
		s.tuneChunks();

		if(verbosity > 1){
			System.out.println("\nDEBUG\nNP chunks after merging and cleaning\n");
			for(int i=0; i < s.chunksNP.size(); i++){
				Tuple mnp = s.chunksNP.get(i);
	
				for (int j= mnp.start; j <= mnp.end; j++ ){
					System.out.print(s.tokens.get(j) + " ");
				}
				System.out.println();
			}
			System.out.println("\nVP chunks after merging and cleaning\n");
			for(int i=0; i < s.chunksVP.size(); i++){
				Tuple mnp = s.chunksVP.get(i);
	
				for (int j= mnp.start; j <= mnp.end; j++ ){
					System.out.print(s.tokens.get(j) + " ");
				}
				System.out.println();
			}
			System.out.println ("\nEND DEBUG NP, VP list\n");
		}
		
		for(int e1 = 0; e1 < s.chunksNP.size(); e1++){
			Tuple np = s.chunksNP.get(e1);
			
			if (np.isBase){
				ArrayList<Token> l1 = new ArrayList<Token>();
				for(int i = np.start; i<=np.end; i++){
					Token token = new Token(s.tokens.get(i),i+1);
					l1.add(token);
				}
				if (verbosity > 2){ System.out.println("DEBUG NP = "+ l1); }
				NP.add(l1);
			}				
		}		
		
		//NULL object
//		Tuple null_object = new Tuple(MAX, MAX, false, true);
//		s.chunksNP.add(null_object);
		
		for(int e1 = 0; e1 < s.chunksVP.size(); e1++){
			Tuple vp = s.chunksVP.get(e1);
			if (vp.isBase){
				ArrayList<Token> l1 = new ArrayList<Token>();
				for(int i = vp.start; i<=vp.end; i++){
					Token token = new Token(s.tokens.get(i),i+1);
					l1.add(token);
				}
				if (verbosity > 2){ System.out.println("DEBUG VP = "+ l1); }
				VP.add(l1);	
			}			
		}
		
		for(int e1 = 0; e1 < s.chunksNP.size(); e1++){
			//check if not starting with preposition - in this case it can not be the subject
			Tuple np1 = s.chunksNP.get(e1);
			
//			if (!np1.isNull){
//				System.out.print("NP1 = ");
//				for(int i = np1.start; i<=np1.end; i++){
//					Token token = new Token(s.tokens.get(i),i+1);
//					System.out.print(token._token + " ");
//				}
//				System.out.println();
//			}	
			
			if (np1.start == MAX){continue;} //null object
			if ((np1.start == np1.end) && (s.tags.get(np1.start).equals("CD")) ){continue;}			

			for(int r = 0; r<s.chunksVP.size();r++){
				Tuple vp = s.chunksVP.get(r);

				//for(int e2 = e1+1; e2<s.chunksNP.size();e2++){
				for(int e2 = 0; e2 < s.chunksNP.size(); e2++){
					Tuple np2 = s.chunksNP.get(e2);
					
//					if (!np2.isNull){
//						System.out.print("NP2 = ");
//						for(int i = np2.start; i<=np2.end; i++){
//							Token token = new Token(s.tokens.get(i),i+1);
//							System.out.print(token._token + " ");
//						}
//						System.out.println();
//					}
					
					if (np2.start == MAX){ //NP2 is a null object
						if (vp.start > np1.end ){
							Relation rel = CreateRelation(np1,vp,np2,s);
							rel.setFeatures(getFeatures(s,e1,e2,r, rel));
							relations.add(rel);
						}
						continue;
					}
					
					if (s.tags.get(np2.end).equals("PRP") ) { continue;	}
					//System.out.println("DEBUG np2.end = " + s.tokens.get(np2.end));

					if (vp.start>np1.end && vp.end<np2.start && (np1.start != np2.start)){
						
						Relation rel = CreateRelation(np1,vp,np2,s);
						rel.setFeatures(getFeatures(s,e1,e2,r, rel));
						relations.add(rel);
					}
					//handle cases when sentence start by a preposition
					else if ( (vp.start > np1.end) && (vp.start > np2.end) &&  (np1.start != np2.start) &&							
						    (s.tags.get(0).equals("IN") || (s.tags.get(0).contains("NN") && s.tags.get(1).equals("CD")) ) ){
						
						//if ( s.tags.get(np1.end).equals("PRP") ) {continue;}
						
						boolean new_rel = true;
						//System.out.println("DEBUG np1 = "+np1.start+", "+np1.end + " np2 = "+np2.start+", "+np2.end  );
						for(int i = np1.start; i<=np1.end; i++){
							for(int j = np2.start; j<=np2.end; j++){
								String np1_i = s.tokens.get(i);
								String np2_j = s.tokens.get(j);
								if ( np1_i.equals(np2_j) ){									
									new_rel = false;
									break;								
								}
							}
						}
						//non subject-object overlapping relation
						if (new_rel){
							Relation rel = CreateRelation(np2,vp,np1,s);
							rel.setFeatures(getFeatures(s,e1,e2,r, rel));
							relations.add(rel);
						}						
					}
				}
			}
		}
		
		return relations;		
	}
	
	static Relation CreateRelation(Tuple np1, Tuple vp, Tuple np2, Sentence s){
			
			ArrayList<Token> l1 = new ArrayList<Token>();
			if (!np1.isNull){
				for(int i = np1.start; i<=np1.end; i++){
					Token token = new Token(s.tokens.get(i),i+1);
					l1.add(token);
				}
			}			
			
			//create r from VB
			ArrayList<Token> l2 = new ArrayList<Token>();
			for(int i = vp.start; i<=vp.end; i++){
				Token token = new Token(s.tokens.get(i),i+1);
				l2.add(token);
			}
			
			//attach preposition before the second NP to the verb
			ArrayList<Token> l3 = new ArrayList<Token>();
			if(np2.start != MAX){
				if ((np2.start > 0) && !(s.tags.get(np2.start).equals("IN")) && !(s.tags.get(np2.start).equals("TO")) ){
					int pos = np2.start-1;
					String tag = s.tags.get(pos);
					
					int last_vp_pos = vp.end;
					
					if (tag.equals("IN") && (last_vp_pos != pos) ) {
						Token token = new Token(s.tokens.get(pos),pos+1);
						l2.add(token);
					}
				}										
				for(int i = np2.start; i<=np2.end; i++){
					Token token = new Token(s.tokens.get(i),i+1);
					l3.add(token);				
				}
			}
			else{
				Token token = new Token("", MAX);
				l3.add(token);
			}
			
			return new Relation(l1,l2,l3);
	}
	
	/* Extract features of relations
	 *
	 * the number of NPs between e1 and r
	 * the number of NPs between r and e2
	 * the number of VPs between e1 and r
	 * the number of VPs between r and e2
	 * 
	 * length of e1
	 * length of e2
	 * length of r
	 * how many proper nouns does e1 contain?
	 * how many proper nouns does e2 contain?	 
	 * 
	 * POS tags sequence of e1, r, e2 
	 * 
	 * Dependency distances are added in DependencyDistanceFilter.
	 */	
	static ArrayList<String> getFeatures(Sentence s, int e1, int e2, int r, Relation rel){
		ArrayList<String> feat =  new ArrayList<String> ();
		int np1count = 0;
		int np2count = 0;
		int vp1count = 0;
		int vp2count = 0;
		int e1nnpcount = 0;
		int e2nnpcount = 0;
		
		String rPOS = "";
				
		int e1start = s.chunksNP.get(e1).start;
		int e2start = s.chunksNP.get(e2).start;
		int rstart = s.chunksVP.get(r).start;
		
		int e1end = s.chunksNP.get(e1).end;
		int e2end = s.chunksNP.get(e2).end;
		//int rend  = s.chunksNP.get(r).end;
		
		int e1length = rel._e1.length;		
		int e2length = rel._e2.length;
		int rlength  = rel._r.length;
		
		if (e2start != MAX){
			for(int i = e1+1; i < e2; i++){
				if(s.chunksNP.get(i).start<rstart) np1count++;
				else np2count++;
			}
		}
		
		for(int j = 0; j < s.chunksVP.size(); j++){
			int vpstart = s.chunksVP.get(j).start;
			if(e1start < vpstart && vpstart<rstart) vp1count++;
			if(e2start != MAX && rstart < vpstart && vpstart <e2start) vp2count++;
			if(vpstart>e2start) break;
		}
		
		for(int j = e1start; j <= e1end; j++){
			if	( s.tags.get(j).equals("NNP"))  e1nnpcount++;
		}
		
		if (e2start != MAX){
			for(int j = e2start; j <= e2end; j++){
				if	( s.tags.get(j).equals("NNP"))  e2nnpcount++;
			}
		}		
		
//		for(int j = 0; j < rel._r.length - 1; j++){
//			rPOS += s.tags.get( rel._r[j]._position - 1 ) + " ";			
//		}
//		rPOS += s.tags.get( rel._r[rel._r.length - 1]._position - 1 );		
		
		feat.add( Integer.toString(np1count));
		feat.add( Integer.toString(np2count));
		feat.add( Integer.toString(vp1count));
		feat.add( Integer.toString(vp2count));
		
		feat.add( Integer.toString(e1length));
		feat.add( Integer.toString(e2length));
		feat.add( Integer.toString(rlength));
		feat.add( Integer.toString(e1nnpcount));
		feat.add( Integer.toString(e2nnpcount));
		
//		feat.add( getPOSsequence(s, e1start, e1end) ); 
//		feat.add( rPOS );
//		feat.add( getPOSsequence(s, e2start, e2end) );
		
		return feat;
	}
	
	private static String getPOSsequence(Sentence s, int start, int end){
		String POS = "";
		for(int j = start; j < end; j++){
			 POS += s.tags.get(j) + " ";
		}
		POS += s.tags.get(end);
		return POS;
	}
	
	
	private static class Sentence{
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		
		ArrayList<Tuple> chunksNP = new ArrayList<Tuple>();
		ArrayList<Tuple> chunksVP = new ArrayList<Tuple>();

		ArrayList<Tuple> consec_chunksNP = new ArrayList<Tuple>();		
		ArrayList<Tuple> consec_chunksVP = new ArrayList<Tuple>();
		
		int verbosity = 0;

		Sentence(String sentence, int debug){
			verbosity = debug;
				
			String s_clean = sentence.replaceAll("\\[", " \\[ ");
			s_clean = s_clean.replaceAll("\\]"," \\] ");
			
			if(verbosity > 5){ System.out.println("CHUNKed = " + s_clean);}
			
			String[] toks = sentence.split(" ");						
			
			for(int k=0; k<toks.length; k++){
				if ( toks[k].equals("/") ){ // chunker removed CC tag from /
					toks[k] = "/CC";
				}
			}
			
			getTokesAndTag(toks);			
			
			chunksNP = getBaseNP(s_clean);			
			
			chunksVP = getBaseVP(s_clean);			
			
			mergeConsecutiveVP();
		}

		/**
		 * get tokens and tags from a chunked sentence
		 */
		private void getTokesAndTag(String[] toks) {			
			for (int i = 0 ; i < toks.length ; ++i)	{
				if (!toks[i].equals("[") && !toks[i].equals("]") && !toks[i].isEmpty() ){
					String tok = toks[i];
					String[] data = new String[2];
					
					int j = tok.length() -1 ;
					while (j > -1){
						if (tok.charAt(j)=='/'){							
							data[0]=tok.substring(0,j);
							data[1]=tok.substring(j+1);							
						}
						j--;
					}
					tokens.add(data[0]);
					tags.add(data[1]);
				}
			}
		}

		/**
		 * get base VPs from a chunked sentence
		 */
		private ArrayList<Tuple> getBaseVP(String s) {
			String[] st = s.split(" ") ;
			ArrayList<String> sClean = new ArrayList<String>();
			for(int i = 0; i< st.length; i++){
				if (!st[i].isEmpty()) {sClean.add(st[i]);}				
			}			
			
			ArrayList<Tuple> chunk = new ArrayList<Tuple>();
			Tuple vp=null;			
			int bracket = 0;			
			boolean inNP = false;
			boolean inVP = false;
			String vpstring="";
			
			for(int i =0; i < sClean.size(); i++ ){
				if (sClean.get(i).equals("[")){
					bracket++;
					inNP     = true;
					inVP     = false;					
					
					if (i>0 && !sClean.get(i-1).equals("]")) { 
						vp.end = i - bracket;
						//System.out.println("VP start = " + vp.start + " ; end = " + vp.end  + " vp = " + vpstring);
						if (vpstring.contains("/VB")) {
							int vp_skipforward = 0;
							int vp_skipbackward = 0;
							int vpcore =0;
							String[] vps = vpstring.split(" ");
							for (int k=0; k< vps.length; k++){
								if( vps[k].contains("/VB")){ vpcore = k;}
							}
							for (int k=0; k< vpcore; k++){
								if( vps[k].contains("/,")){
									vp_skipforward = k + 1;									
								}else if( vps[k].contains("/CC") || vps[k].contains("/WDT") || vps[k].contains("/WP") || vps[k].contains("/WP$") || vps[k].contains("/WDT") ){
									vp_skipforward++;
								}
							}
							for (int k=vps.length - 1; k> vpcore; k--){
								if( vps[k].contains("/,")){
									vp_skipbackward = vps.length - k;									
								}else if( vps[k].contains("/CC") || vps[k].contains("/WDT") || vps[k].contains("/WP") || vps[k].contains("/WP$") || vps[k].contains("/WDT") ){
									vp_skipbackward++;
								}
							}
							
							vp.start =  vp.start + vp_skipforward;
							vp.end   =  vp.end   - vp_skipbackward;
							chunk.add(vp);		
						}
						inVP = false;
						vpstring = "";
					}					
				}else if (sClean.get(i).equals("]")  ){
					bracket++;
					inNP = false;
					vpstring = "";
				}else if (inNP == false) {
					if (inVP == false){
						vp = new Tuple();
						vp.start = i - bracket;
						vp.isBase = true;
						inVP = true;						
					}
					vpstring += sClean.get(i) + " ";								
				}				
			}
			
			if (verbosity > 5){
				System.out.println("\nBase VPs from chunked sentence");
				for(int i=0; i < chunksVP.size(); i++){
					Tuple mnp = chunksVP.get(i);	
					for (int j= mnp.start; j <= mnp.end; j++ ){
						System.out.print(tokens.get(j) + " ");
					}
					System.out.println();
				}
			}
			
			return chunk;
		}
		
		/**
		 * get base NPs from a chunked sentence
		 */
		private ArrayList<Tuple> getBaseNP(String s) {
			String[] st = s.split(" ") ;
			ArrayList<String> sClean = new ArrayList<String>();
			for(int i = 0; i< st.length; i++){
				if (!st[i].isEmpty()) {sClean.add(st[i]);}				
			}
			
			ArrayList<Tuple> chunk = new ArrayList<Tuple>();
			Tuple np=null;			
			int bracket = 0;
			
			for(int i =0; i < sClean.size(); i++ ){
				if (sClean.get(i).equals("[")){
					np = new Tuple();
					np.start = i - bracket;
					np.isBase = true;
					bracket++;
				}else if (sClean.get(i).equals("]")  ){
					bracket++;
					np.end = i - bracket;
					np.isBase = true;
					//System.out.println("NP start = " + np.start + " ; end = " + np.end  );
					chunk.add(np);					
				}
			}
			
			if (verbosity > 5){
				System.out.println("\nBase NPs from chunked sentence");
				for(int i=0; i < chunk.size(); i++){
					Tuple mnp = chunk.get(i);	
					for (int j= mnp.start; j <= mnp.end; j++ ){
						System.out.print(tokens.get(j) + " ");
					}
					System.out.println();
				}
			}
			
			return chunk;
		}

		/**
		 * merging consecutive base VPs
		 * ie: [come] and [see] -> [come and see]
		 */
		private void mergeConsecutiveVP() {
			//merge consecutive VPs and add them as additional VPs
			for (int i=0; i < chunksVP.size(); i++){				
				Tuple vp1   = chunksVP.get(i);
				int c_start = vp1.start;
				int c_end   = vp1.end;

				Tuple new_vp = null;
				for(int j=0; j < chunksVP.size(); j++){
					Tuple vp2 = chunksVP.get(j);

					if (new_vp != null){
						c_start = new_vp.start;
						c_end   = new_vp.end;
					}
					//merge VPs if there is no Noun in between them
					boolean no_noun = true;
					int k = vp1.end + 1; 
					if (k < vp2.start){
						while (k < vp2.start){
							if ( tags.get(k).startsWith("NN") ){
								no_noun = false;
								break;
							}
							k++;
						}
						if (no_noun == true){
							new_vp = new Tuple();
							new_vp.start = c_start;
							new_vp.end   = vp2.end;
							new_vp.isBase = false;

							consec_chunksVP.add(new_vp);
							
							//System.out.format("DEBUG new_vp  start = %d, end = %d%n", new_vp.start, new_vp.end);
						}						
					}
				}
			}
			
			//merge base NP list with consecutive NP list
			chunksVP.addAll(consec_chunksVP);
			
			if (verbosity > 5){
				System.out.println("\nDEBUG chunks VP \n");
	
				for(int i=0; i < chunksVP.size();i++){
					Tuple mnp = chunksVP.get(i);
	
					for (int j= mnp.start; j <= mnp.end; j++ ){
						System.out.print(tokens.get(j) + " ");
					}
					System.out.println();
				}
				System.out.println ("\nEND DEBUG VP\n");
			}			
		}
		
		/**
		 * after merging and tuning NP chunks we only keep base NPs and longest merged NPs
		 */
		private ArrayList<Tuple> removeEmbeddedNP( ArrayList<Tuple> consecChunksNP) {
			ArrayList<Tuple> np = consecChunksNP;
			ArrayList<Integer> toRemove = new ArrayList<Integer>();
			HashMap<Integer, Integer> seen = new HashMap<Integer, Integer>();
			
			for(int i=0; i < np.size() - 1; i++){
				for(int j=i+1; j < np.size(); j++){
					Tuple np1 = np.get(i);
					Tuple np2 = np.get(j);
					if ( np1.start == np2.start ){
						if (np1.end <= np2.end){
							if (!seen.containsKey(i)){ toRemove.add(i); seen.put(i, 1);}
						} else { 
							if (!seen.containsKey(j)){ toRemove.add(j); seen.put(j, 1);}
						}
					}
					else if ( np1.end == np2.end ) {
						if (np1.start <= np2.start){
							if (!seen.containsKey(j)){ toRemove.add(j); seen.put(j, 1);}
						} else { 
							if (!seen.containsKey(i)){ toRemove.add(i); seen.put(i, 1);}
						}
					}
					else{
						continue;
					}					
				}
			}
			Collections.sort(toRemove);
			for(int i=toRemove.size()-1;i>=0;i--){
				np.remove(toRemove.get(i).intValue());
			}			
			return np;
		}

		/* 
		 * Clean-up the NP to remove junk NPs
		 * Adjust chunks according to specific rules
		 */
		
		void tuneChunks(){
			ArrayList<Integer> toRemove = new ArrayList<Integer>();
			int lastRemoved = -1;
			
			//System.out.println("DEBUG start merging consecutive NPs");
			
			mergeConsecutiveNP();

			//tweak NPs by additional rules
			ArrayList<Tuple> new_chunksNP = new ArrayList<Tuple>();
			
			for(int i=0; i<chunksNP.size();i++){
				Tuple np = chunksNP.get(i);
				
				// RB JJ IN [NP] TO VB
				// too complex for students to learn
				if ( (np.start > 3) && (np.end < tokens.size()-2) ){				
					int new_start = np.start - 3;
					int new_end   = np.end + 2;
					if  (tags.get(new_start).equals("RB") && (tags.get(new_start+1).equals("JJ") ) ){						
						chunksNP.get(i).start = new_start + 1;						
					}
					if  (tags.get(new_end).contains("VB") && (tags.get(new_end-1).equals("TO") ) ){
						chunksNP.get(i).end = new_end;
					}
				}
				
				// [NP] RBR JJ
				// X more likely
				if ( np.end < tokens.size()-2 ){					
					int new_end   = np.end + 2;					
					if  (tags.get(new_end).contains("JJ") && (tags.get(new_end-1).equals("RBR") ) ){
						chunksNP.get(i).end = new_end;
					}
				}
				
				// [VP] JJ TO [NP]
				if ( (np.start > 3) && (np.end < tokens.size()-2) ){				
					int new_start = np.start - 2;
					int new_end   = np.end;
					if  (tags.get(new_start).equals("JJ") && (tags.get(new_start+1).equals("TO") ) ){						
						chunksNP.get(i).start = new_start;
						//System.out.println("DEBUG new np start = " + chunksNP.get(i).start + " end = " + chunksNP.get(i).end + " token = " + tokens.get(chunksNP.get(i).start));						
					}
				}
				
				// in 2008 he ...
				if ( ( np.start > 1) && (tags.get(np.start - 1).equals("IN")) && (tags.get(np.start).equals("CD")) ){
					chunksNP.get(i).start = np.start - 1;
				}
				
				//^VB IN NP  or ^VB NP
				if ( (( np.start - 1 ==0)  || (np.start - 2 ==0) ) && (tags.get(0).contains("VB"))){
					chunksNP.get(i).start = 0;
				}
				
				//^ ... NP				
				Tuple new_np = new Tuple();
				if ( np.start > 0) {
					boolean isNew_np = true;
					for(int k =0; k<= np.start; k++){
						if (tags.get(k).contains("VB")){
							isNew_np = false;
						}						
					}
					if (isNew_np) {						
						new_np.start  = 0;
						new_np.end    = chunksNP.get(i).end;
						new_np.isBase = false;
						new_chunksNP.add(new_np);
						//System.out.println("DEBUG new np end : " + new_np.end);
					}
				}				
			
				//NP which has only one word
				if(np.start == np.end){
					String tag =  tags.get(np.start);
					//if an NP starts with
					if ( //tag.equals("PRP") ||
						 tag.equals("WDT") || 
						 tag.equals("DT") ||
						 tag.equals("IN") ||
						 //tag.equals("EX") || 
						 tag.equals("WP")){
					        	toRemove.add(new Integer(i));
								lastRemoved = i;
						 }
				}

				//NP which starts with possessive 
				if((tokens.get(np.start).equals("'s") || (tokens.get(np.start).equals("'")))
						&& tags.get(np.start).equals("POS")){
					np.start = np.start+1;
					if(i!=0 && chunksNP.get(i-1).end == np.start-2){
						if(lastRemoved != i-1){
							toRemove.add(new Integer(i-1));
							lastRemoved = i-1;
						}
					}
					if(np.start>np.end){
						toRemove.add(new Integer(i));
						lastRemoved = i;
					}
				}
			}
			
			for(int i=toRemove.size()-1;i>=0;i--){
				chunksNP.remove(toRemove.get(i).intValue());
			}
			
			consec_chunksNP = removeEmbeddedNP(consec_chunksNP);
			new_chunksNP.addAll(consec_chunksNP);
			new_chunksNP = removeEmbeddedNP(new_chunksNP);
			chunksNP.addAll(new_chunksNP);

			//tune VP
			for(int i=chunksVP.size()-1; i>=0;i--){
				Tuple vp = (Tuple)chunksVP.get(i);
				/*if(tags.get(vp.end).equals("TO")){
					vp.end--;
				}*/
				if(tags.get(vp.start).contains("VB") && vp.start!=0 && tags.get(vp.start-1).equals("TO")){
					chunksVP.remove(i);
				}				
				
				if( (vp.start > 2) && (!tags.get(vp.end+1).contains("VB")) && (tags.get(vp.start).equals("VBG")) && ( tags.get(vp.start - 2).contains("NN") && ( tags.get(vp.start - 1).equals(","))  )  ){
					chunksVP.remove(i);
				}
				if( (vp.start > 2) && (vp.end+2 < tokens.size()) && 
					(tags.get(vp.end+2).contains("NN"))&& (tags.get(vp.end+1).contains("IN")) && 
					(tags.get(vp.start).equals("VBG")) && ( tags.get(vp.start - 2).contains("NN") && ( tags.get(vp.start - 1).equals("IN"))  )  ){					
					chunksVP.remove(i);
				}
				
				if( (vp.start > 1) && (!tags.get(vp.start+1).contains("VB")) && (tags.get(vp.start).equals("VBG")) && ( tags.get(vp.start - 1).contains("NN")  )  ){
					chunksVP.remove(i);
				}
				
				if( (vp.end > 1) &&  (vp.end < tokens.size() - 2) && (tags.get(vp.end + 1).equals("JJ")) && ( !tags.get(vp.end + 2).contains("NN")  )  ){
					Tuple new_np = new Tuple();
					new_np.start  = vp.end + 1;
					new_np.end    = vp.end + 1;
					new_np.isBase = false;
					chunksNP.add(new_np);					
				}
				if( (vp.end > 1) &&  (vp.end < tokens.size() - 1) && (tags.get(vp.end + 1).equals("RP")) ){
					Tuple new_vp = new Tuple();
					new_vp.start  = vp.start;
					new_vp.end    = vp.end + 1;
					new_vp.isBase = false;
					chunksVP.add(new_vp);					
				}
			}
			
			//add NULL object NP: pseudo NP which serve as an object of intransitive verbs
		}

		private void mergeConsecutiveNP() {			
			//merge consecutive NPs and add them as additional NPs
			for (int i=0; i < chunksNP.size(); i++){				
				Tuple np1   = chunksNP.get(i);
				int c_start = np1.start;
				int c_end   = np1.end;
				
				Tuple new_np = null;
				for(int j=0; j < chunksNP.size(); j++){					
					Tuple np2 = chunksNP.get(j);					
				
					if (new_np != null){
						c_start = new_np.start;
						c_end   = new_np.end;
					}
					
					//System.out.println("DEBUG np2 = " + tokens.get(np2.start) + " ; " + tokens.get(np2.end) + "  np1 = " + tokens.get(np1.start) + " ; " + tokens.get(np1.end));

					if ( ( c_end == np2.start-1 ) ||   // NP1 NP2
						 ( ( c_end == np2.start-2 ) && ( tags.get(np2.start-1).equals("IN") || tags.get(np2.start-1).equals("TO")) ) ||	 // NP1 preposition/TO NP2	
						 ( ( c_end == np2.start-3 ) && ( tags.get(np2.start-2).equals("TO") && tags.get(np2.start-1).contains("VB")) ) ||	 // NP1 TO VB NP2
						 ( ( c_end == np2.start-3 ) && ( tags.get(np2.start-2).equals("JJ") ) && ( tags.get(np2.start-1).equals("IN") || tags.get(np2.start-1).equals("TO")) ) ||	 // NP1 JJ preposition/TO NP2
						 ( ( c_end == np2.start-3 ) && ( tags.get(np2.start-2).equals("IN")) && ( tags.get(np2.start-1).equals("IN")) ) || // NP1 IN IN NP2
						 ( ( c_end == np2.start-2 ) && ( tags.get(np2.start-1).equals("VBG") || tags.get(np2.end-1).equals("VBG") ) ) || // NP1 VBG NP2	
						 ( ( c_end == np2.start-3 ) && ( tags.get(np2.start-2).equals(",")) &&( tags.get(np2.start-1).equals("VBG") || tags.get(np2.end-1).equals("VBG") ) ) || // NP1 , VBG NP2
						 ( ( c_end == np2.start-3 ) && ( tags.get(np2.start-2).equals(",")) &&( tags.get(np2.start-1).equals("IN") || tags.get(np2.end-1).equals("IN") ) ) || // NP1 , IN NP2
						 ( ( c_end == np2.start-3 ) && ( tags.get(np2.start-2).equals("IN")) &&( tags.get(np2.start-1).equals("VBG") || tags.get(np2.end-1).equals("VBG") ) ) || // NP1 IN VBG NP2						 
						 ( ( c_end == np2.start-4 ) && ( tags.get(np2.start-3).equals("IN")) &&( tags.get(np2.start-1).equals("IN")) && ( tags.get(np2.start-2).equals("VBG") || tags.get(np2.end-2).equals("VBG") ) ) || // NP1 IN VBG IN NP2
						 ( ( c_end == np2.start-2 ) && ( tags.get(np2.start-1).equals("CC") || tags.get(np2.end-1).equals("CC") )  && !tags.get(np2.end +1).contains("VB") ) || // NP1 CC NP2	
						 ( ( c_end == np2.start-2 ) && ( tags.get(np2.start-1).equals(",") || tags.get(np2.end-1).equals(",") ) &&   // NP1 , NP2
					        !tags.get(np2.start).equals("WP") &&  !tags.get(np2.start).equals("WDT") && !tags.get(np2.start).equals("WP$") && !tags.get(np2.end +1).contains("VB") ) 

				   	   ){
							new_np = new Tuple();
							new_np.start  = c_start;
							new_np.end    = np2.end;
							new_np.isBase = false;

							//System.out.format("DEBUG found!!! current NP [%d-%d] ; NP2[%d-%d] ; new merged NP [%d-%d]%n", c_start, c_end, np2.start, np2.end, new_np.start, new_np.end);

							consec_chunksNP.add(new_np);
						}					
				}				
			}
		}		
	}
	
}
