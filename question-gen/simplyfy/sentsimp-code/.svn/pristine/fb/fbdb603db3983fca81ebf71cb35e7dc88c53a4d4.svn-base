package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.util.StringUtils;

/***
 * This class implements ROUGE-N Loss function
 * non-case sensitive  
 */

public class RougeN implements Loss {
	
	int ngram = 4;
	
	double precision = 0.0;
	double recall    = 0.0;	
	
	public RougeN() {	
	}

	public RougeN(int i) {
		ngram = i;
	}

	/***
	 * normalize f-score of ngrams co-occurrence  
	 **/
	public double calculate(ArrayList<String> reference, Hypothesis guess) {
		double result = 0;
		
		if (guess == null ){return result;}
		
		ArrayList<String> hyp = guess.toStringArray();
		
		result = doAveF(reference, hyp);
			
		return result;	
	}
	
	public double precision(){
		return precision;
	}
	
	public double recall(){
		return recall;
	}
	
	public double calculate(ArrayList<String> reference, ArrayList<String> hyp) {
		double result = doAveF(reference, hyp); 
		return result;
	}	
	
	private double doAveF(ArrayList<String> reference, ArrayList<String> hyp) {
		
		Map<String, Double> HypCounts = count_ngram(hyp, ngram);
		Map<String, Double> RefCounts = count_ngram(reference, ngram);
		
		double ref_ngram_total = 0.0;
		double hyp_ngram_total = 0.0;
        double matched_ngram   = 0.0;
        
        for (String ls : RefCounts.keySet()) {            	            	
        	ref_ngram_total += RefCounts.get(ls);
        }
        
        for (String ls : HypCounts.keySet()) {            	          	
        	hyp_ngram_total += HypCounts.get(ls);
        	if (RefCounts.containsKey(ls)){            		          		
        		matched_ngram += RefCounts.get(ls);
        	}
        }
        
        recall     = matched_ngram / ref_ngram_total;
        precision  = matched_ngram / hyp_ngram_total;
        double f   = 2*recall*precision/(recall + precision);
        
        if (Double.isNaN(f))         { f         = 0.0; }
        if (Double.isNaN(recall))    { recall    = 0.0; }
        if (Double.isNaN(precision)) { precision = 0.0; }
        
        
		return f;		
	}	

	private Map<String, Double> count_ngram(ArrayList<String> hyp, int len) {
		Map<String, Double> counts = new HashMap<String, Double>();
		
		for(int j=0; j< hyp.size(); j++){
			String[] words = hyp.get(j).split(" ");
			for (int i = 0; i <= (words.length - len); i++){
				String[] ngr = Arrays.copyOfRange(words, i, i+len);
				
				String ngram = StringUtils.join(ngr, " ").toLowerCase();				
				
				Double ct = counts.get(ngram); 
				
				if( ct == null){ ct = 0.0; }
				
				// weighted count, double count if it is a long ngram
				if (len<6){	counts.put(ngram, ct + 1.0); }
				else { counts.put(ngram, ct + 2.0); } 
			}			
		}
		
		return counts;
	}

}
