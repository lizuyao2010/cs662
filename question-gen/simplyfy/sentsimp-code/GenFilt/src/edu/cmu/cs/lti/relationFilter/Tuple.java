package edu.cmu.cs.lti.relationFilter;

/**
 * This class keeps chunk's information
 */
public class Tuple{
	int start;
	int end;
	boolean isBase; //base NP or VP
	boolean isNull = false; // null-object
	
	public Tuple(){
		
	}
	public Tuple(int s, int e, boolean iB){
		start  = s;
		end    = e;
		isBase = iB;		
	}
	
	public Tuple(int s, int e, boolean iB, boolean iN){
		start  = s;
		end    = e;
		isBase = iB;
		isNull = iN;
	}
}