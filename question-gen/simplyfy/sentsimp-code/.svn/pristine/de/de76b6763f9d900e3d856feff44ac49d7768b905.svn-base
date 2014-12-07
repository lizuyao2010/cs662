package edu.cmu.cs.lti.relationFilter;

public class SinglePronounFeature implements FilterFunction {
	public SinglePronounFeature(){
		
	}

	public boolean filter(SentenceData s, Relation r) {
	
		if(r._e1.length == 1){
			Token t = r._e1[0];
			if(s.leafAt(t._position).getParent().label().equals("PRP"))
				return false;
		}
		if(r._e2.length == 1){
			Token t = r._e2[0];
			if(s.leafAt(t._position).getParent().label().equals("PRP"))
				return false;
		}
		return true;
	}
}
