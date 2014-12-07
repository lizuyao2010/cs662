package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.Properties;

public class Filter {
	ArrayList<FilterFunction> _filters = new ArrayList<FilterFunction>();
	long _totalRel=0;
	long _posRel=0;
	int verbosity = 0;
	
	/**
	 * Soft filtering rules: turn them into features
	 */
	public Filter( int debug ){
		_filters.add(new RelationContainsVBFeature());
		_filters.add(new PPAttachmentFeature());
		_filters.add(new SinglePronounFeature());
		_filters.add(new SentenceLikeBoundaryFeature());
		_filters.add(new ObjectInTheSameVP()); 
	}
	
	/**
	 * add soft filter features to sentence 
	 **/
	public SentenceData addSoftFilterFeature(SentenceData sent){
		for(int i=0; i<_filters.size(); i++){
			
			FilterFunction filter = (FilterFunction)_filters.get(i);
			
			for(int j=0; j<sent.relationCount(); j++){
				
				Relation r = sent.relationAt(j);				

				try{						
					boolean value = filter.filter(sent, r);
					if(value == false){						
						r._features.add("0");
					}else{
						r._features.add("1");
					}
					
				}catch(Exception e){
					System.out.println("DEBUG ***** ERROR in applying filtering rules " +j +" *****");
					System.out.println("Got an IOException: " + e.getMessage());
					
					System.out.println(sent.toString());
					System.out.println(r.toString());
					System.exit(-1);
				}
			}
		}		
		
		return sent;
	}

	
	/**
	 * Hard filtering rules are called here
	 */
	public Filter( Properties _props, int debug ){
		verbosity = debug;
		
		//filters.add(new F()); test feature
		_filters.add(new RelationContainsVBFeature());
		
		if ( _props.getProperty("FilterPPAttachment").equals("true") ){
			_filters.add(new PPAttachmentFeature());
			if( verbosity > 0) { System.out.println("PPAttachment filter created"); }
		}
		
		// NP1 and NP2 do not consist solely the pronoun
		if ( _props.getProperty("FilterSinglePronoun").equals("true") ){
			_filters.add(new SinglePronounFeature());
			if( verbosity > 0) { System.out.println("Single Pronoun filter created"); }
		}
		
		//filters.add(new IntersectionFeature());
		
		//NP1 does not cross over SBAR
		if ( _props.getProperty("FilterSentenceLikeBoundary").equals("true") ){
			_filters.add(new SentenceLikeBoundaryFeature());
			if( verbosity > 0) { System.out.println("SentenceLikeBoundary filter created"); }
		}
		
		// r and e2 must have a common VP
		if ( _props.getProperty("FilterObjectInTheSameVP").equals("true") ){
			_filters.add(new ObjectInTheSameVP());
			if( verbosity > 0) { System.out.println("ObjectInTheSameVP filter created"); }
		}
		
		//distance between e1 and e2 in a dependency tree less than maxDist
		if ( _props.getProperty("FilterDependencyDistance").equals("true") ) {
			int maxDist = Integer.parseInt(_props.getProperty("MaximumDependencyDistance", "5"));
			int minDist = Integer.parseInt(_props.getProperty("MinimumDependencyDistance", "10"));
			int spanDist = Integer.parseInt(_props.getProperty("SpanDependencyDistance", "6"));
			_filters.add(new DependencyDistanceFilter(minDist, maxDist, spanDist));
			if( verbosity > 0) { System.out.println("Dependency Distance filter created, minDist=" + minDist + " ; maxDist=" + maxDist + " ; spanDist=" + spanDist); }
		}
		
		if( verbosity > 0) { System.out.println(""); }		
	}	

	/**
	 * Apply filtering rules for each simplified sentence 
	 **/
	public SentenceData filterSentence(SentenceData sent){

		for(int i=0; i<_filters.size(); i++){

			FilterFunction filter = (FilterFunction)_filters.get(i);
			
			for(int j=0; j<sent.relationCount(); j++){
				Relation r = sent.relationAt(j);
				
				if (r.e2toString().equals("-10000")){
					r._label = true;
					continue;
				}
				
				//System.out.println("DEBUG " + sent.toString());				

				if(r._label){
					try{						
						boolean value = filter.filter(sent, r);
						if(value == false){						
							r._label = false;
							//System.out.println("Filter " + i + " : " + r.toString());
						}						
					}catch(Exception e){
						System.out.println("DEBUG ***** ERROR in applying filtering rules " +j +" *****");
						System.out.println("Got an IOException: " + e.getMessage());
						
						System.out.println(sent.toString());
						System.out.println(r.toString());
						System.exit(-1);
					}
				}
			}
		}
		
		for(int j=0;j<sent.relationCount();j++){
			Relation r = sent.relationAt(j);
			_totalRel++;
			if(r._label){
				_posRel++;
			}
		}
		
		if (verbosity > 0){
			System.out.println("Extracted simplified sentences: "+_totalRel);
			System.out.println("After filtering               : "+_posRel);
		}

		return sent;
	}

	public void setVerbosity(int i) {		
		verbosity = i;
	}
	
	

}
