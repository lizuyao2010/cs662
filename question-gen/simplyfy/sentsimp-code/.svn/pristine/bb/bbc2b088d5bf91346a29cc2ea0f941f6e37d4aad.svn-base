package edu.cmu.cs.lti.relationFilter;

import edu.stanford.nlp.trees.*;

public class PPAttachmentFeature implements FilterFunction {
	public PPAttachmentFeature(){
		
	}

	public boolean filter(SentenceData s, Relation r) {
		//System.out.println("DEBUG r = "+ r.toString());
		int pos = r._e2[0]._position -1;				
		
		if  ((pos == 1) || (pos == 0)){			
			return true;
		}else {
			Tree tag = s.leafAt(pos).getParent(); //preceding token's tag
			if(tag.label().toString().equals("IN") &&  
					tag.getParent()!=null && tag.getParent().label().toString().equals("PP") &&
					tag.getParent().getParent() !=null &&
					!tag.getParent().getParent().label().toString().equals("VP")){
				return false;	
			}
		} 
		
		return true;
	}

}
