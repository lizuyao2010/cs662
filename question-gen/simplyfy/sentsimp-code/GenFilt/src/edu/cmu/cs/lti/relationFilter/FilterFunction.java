package edu.cmu.cs.lti.relationFilter;


public interface FilterFunction {
	public boolean filter(SentenceData s, Relation r);
}
