package edu.cmu.cs.lti.relationFilter;

import edu.stanford.nlp.trees.*;

public class RelationContainsVBFeature implements FilterFunction {

	public boolean filter(SentenceData s, Relation r) {
		//System.out.println("Filtering with VB is used.");
		if(r==null && r._r== null) return false;
		
		for(int i=0; i<r._r.length; i++){
			Token t = r._r[i];
			if(t._position!=-1){
					if(s.POSTagAt(t._position).startsWith("VB"))
					return true;
			}
		}
		
		return false;
	}

}
