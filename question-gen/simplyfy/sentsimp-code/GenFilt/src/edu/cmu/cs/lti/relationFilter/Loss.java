package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;

/***
 * This class defines the contract we have for loss function
 */

public interface Loss {
	
	double calculate(ArrayList<String> reference, Hypothesis guess);
	
	double calculate(ArrayList<String> reference, ArrayList<String> hyp);
	
	double precision();
	double recall();

}
