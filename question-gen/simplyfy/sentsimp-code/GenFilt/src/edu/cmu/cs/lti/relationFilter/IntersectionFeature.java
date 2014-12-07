package edu.cmu.cs.lti.relationFilter;

import java.util.HashMap;
import java.util.Iterator;

public class IntersectionFeature implements FilterFunction {

	public boolean filter(SentenceData s, Relation r) {
		HashMap map = new HashMap();
		Iterator<Token> it = r.allTokens().iterator();
		while(it.hasNext()){
			Token t = it.next();
			if(t._position!=-1){
				if(map.containsKey(t._position)){
					return false;
				}
				else{
					map.put(t._position, null);
				}
			}
		}
		return true;		
	}

}
