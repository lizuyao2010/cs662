package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;

/** 
 * A class for simplified hypotheses
 **/

public class Hypothesis {	
	private ArrayList<Relation> _hyp;
	public ArrayList<Float> _hypFeatures;
	public double _score;
	
	public double _loss; //use for training
	public Integer _rank; //use for training
	
	public Hypothesis(){
		_hyp = new ArrayList<Relation>();
		_hypFeatures = new ArrayList<Float>();
	}
	
	public void setHypScore(double s){
		_score = s;		
	}

	public void add(Relation t) {
		this._hyp.add(t);
	}

	public int size() {		
		return this._hyp.size();
	}

	public Relation get(int i) {		
		return this._hyp.get(i);
	}

	public void addAll(Hypothesis partialHyp) {
		this._hyp.addAll(partialHyp._hyp);		
	}
	
	public String toOneString(){
		String s = "";		
		for (int j=0; j< _hyp.size(); j++){			
			s += _hyp.get(j).toOneString() + " ||| ";
		}
		return s;
	}
	
	public String toOneNiceString(){
		String s = "";		
		for (int j=0; j< _hyp.size(); j++){			
			s += _hyp.get(j).toOneString().trim() + "\n";
		}
		return s;
	}
	
	//glue simple sentences with a separator
	public String toOneNiceString(String separator){
		String s = "";		
		for (int j=0; j< _hyp.size(); j++){			
			s += _hyp.get(j).toOneString().trim() + separator;
		}
		return s;
	}
	
	public ArrayList<String> toStringArray() {		
		ArrayList<String> h = new ArrayList<String>();
		for (int j=0; j< _hyp.size(); j++){			
			h.add(_hyp.get(j).toOneString() );
		}
		return h;
	}
}