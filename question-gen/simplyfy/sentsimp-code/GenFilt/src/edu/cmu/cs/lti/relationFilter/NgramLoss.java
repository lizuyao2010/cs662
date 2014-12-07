package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.util.StringUtils;

/***
 * This class implements n-gram co-occurrence Loss function
 * Average F score of ngram co-occurrence 
 */

public class NgramLoss implements Loss {
	
	int max_ngram = 10;
	
	double precision = 0.0;
	double recall    = 0.0;
	
	public NgramLoss() {	
	}

	public NgramLoss(int i) {
		max_ngram = i;
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
	
	public double calculate(ArrayList<String> reference, ArrayList<String> hyp) {
		double result = doAveF(reference, hyp); 
		return result;
	}	
	
	private double doAveF(ArrayList<String> reference, ArrayList<String> hyp) {
		double result = 0;		
		
		double f_ngram = 0.0; 
		double p = 0.0;
		double r = 0.0;
		
		for (int l = 2; l < max_ngram; l++){
			Map<String, Double> HypCounts = count_ngram(hyp, l);
			Map<String, Double> RefCounts = count_ngram(reference, l);
			
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
            
            double rec    = matched_ngram / ref_ngram_total;
            double prec   = matched_ngram / hyp_ngram_total;
            double f      = 2*rec*prec/(rec + prec);
            
            if (!Double.isNaN(f))    { f_ngram  += f; }
            if (!Double.isNaN(rec))  { r  += rec; }
            if (!Double.isNaN(prec)) { p  += prec; }
		}		
		
		result    = f_ngram /max_ngram;
		
		recall    = r/max_ngram;
		precision = p/max_ngram;
		
		return result;
		
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

	@Override
	public double precision() {		
		return precision;
	}

	@Override
	public double recall() {
		return recall;
	}

}
