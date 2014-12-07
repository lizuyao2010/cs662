package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Evaluation {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	Loss loss = null;
    	
        // parameters check
        if (args.length < 3){        	       	
            System.err.println("Input parameters: reference_file candidate_file metric");
            
            System.err.println("\nMetric:\nAveF-n: Average F score of ngram co-occurence from 2 to n ; by default n = 10; n can be set in range of [5:10]");
            System.err.println("ROUGE-n: ROUGE-N score; by default n = 4; n can be set in range of [2:6]");
            System.err.println("Readability: source readbility grade level of source, reference, and hypothesis ");
            System.err.println("Example:\nEvaluation reference your_output AveF-10\n");
            
            System.exit(1);
        }

        // init metric
        if( args[2].equalsIgnoreCase("AveF-10") ){ loss = new NgramLoss();  }
        if( args[2].equalsIgnoreCase("AveF-9") ) { loss = new NgramLoss(9); }
        if( args[2].equalsIgnoreCase("AveF-8") ) { loss = new NgramLoss(8); }
        if( args[2].equalsIgnoreCase("AveF-7") ) { loss = new NgramLoss(7); }
        if( args[2].equalsIgnoreCase("AveF-6") ) { loss = new NgramLoss(6); }
        if( args[2].equalsIgnoreCase("AveF-5") ) { loss = new NgramLoss(5); }
        
        if( args[2].equalsIgnoreCase("ROUGE-6") ) { loss = new RougeN(6); }
        if( args[2].equalsIgnoreCase("ROUGE-5") ) { loss = new RougeN(5); }
        if( args[2].equalsIgnoreCase("ROUGE-4") ) { loss = new RougeN(4); }
        if( args[2].equalsIgnoreCase("ROUGE-3") ) { loss = new RougeN(3); }
        if( args[2].equalsIgnoreCase("ROUGE-2") ) { loss = new RougeN(2); }

        // read reference and hypothesis files        
        ArrayList<ArrayList<String>> ref = readFile(args[0]);
        ArrayList<ArrayList<String>> hyp = readFile(args[1]);
		
		if(ref.size() != hyp.size()){
			System.err.println("Mismatch between reference and candidate file!!! The number of references and candidates must be equal.");
			System.exit(1);
		}
		
		System.out.println("Metric = " + args[2]);		
		
		if(args[2].equalsIgnoreCase("Readability")){
			
			if (args.length != 4){
				System.err.println("Need to provid the original English text in order to compute source readbility grade level");
				System.exit(1);
			}
			ArrayList<ArrayList<String>> src = readFile(args[3]);
			
			EvalReadability(src, hyp, ref);
			
		}else{			
			Eval(loss, ref, hyp);			
        }
    }

	private static void EvalReadability(ArrayList<ArrayList<String>> src, ArrayList<ArrayList<String>> hyp, ArrayList<ArrayList<String>> ref) {
		ArrayList<Double> rSrc = new ArrayList<Double>();
		ArrayList<Double> rHyp = new ArrayList<Double>();
		ArrayList<Double> rRef = new ArrayList<Double>();
					
		double s = 0.0;
		double r = 0.0;
		double h = 0.0;
		
		for(int i = 0; i< ref.size(); i++){				
			rSrc = getReadability(src.get(i));
			rHyp = getReadability(hyp.get(i));
			rRef = getReadability(ref.get(i));				
			System.out.format("%d: %.1f %.1f %.1f %n", i, rSrc.get(2), rRef.get(2), rHyp.get(2));
			
			s += rSrc.get(2);
			r += rRef.get(2);
			h += rHyp.get(2);				
		}			
		s = s / hyp.size();
		r = r / hyp.size();
		h = h / hyp.size();
		System.out.format("Average: %.1f %.1f %.1f %n", s, r, h);		
	}

	private static void Eval(Loss loss, ArrayList<ArrayList<String>> ref, ArrayList<ArrayList<String>> hyp) {		
		double f = 0.0;
		double p = 0.0;
		double r = 0.0;
		for(int i = 0; i< ref.size(); i++){
			//System.out.println("REF = "+ ref.get(i));
			//System.out.println("HYP = "+ hyp.get(i));
			
			double score = loss.calculate(ref.get(i), hyp.get(i));
			System.out.format("%d: %.3f %.3f %.3f %n",i, loss.precision(), loss.recall(), score);			
			
			f += score;
			p += loss.precision();
			r += loss.recall();
		}
		f = f / hyp.size();		
		p = p / hyp.size();
		r = r / hyp.size();
		
        System.out.format("Average: %.3f %.3f %.3f%n", p, r, f);		
	}

	private static ArrayList<Double> getReadability( ArrayList<String> hyp) {
		ArrayList<Double> f = new ArrayList<Double>();
		String hypString = "";
		
		for(int j=0; j< hyp.size(); j++){
			hypString = hypString + "\n" + hyp.get(j);
		}
		
		Fathom.Stats FatStat = Fathom.analyze(hypString);
		
		double nsyl          = FatStat.getNumSyllables();
		double nword         = FatStat.getNumWords();
		double nsent         = FatStat.getNumSentences();
		double ncomplexword  = FatStat.getNumComplexWords();
		double npolysyl      = FatStat.getNumPolySyllables();
		double nletter       = FatStat.getNumLetters(); 
		
		double words_per_sentence    = nword / nsent;		
		double syllables_per_word    = nsyl / nword;
		double percent_complex_words = (ncomplexword / nword) * 100;
		double poly_per_sentence     = npolysyl / nsent;		
		double letter_per_word       = nletter / nword;
		
		double flesh      = 206.835 - (1.015 * words_per_sentence )  - (84.6 * syllables_per_word) ;		 
		double fog        = ( words_per_sentence + percent_complex_words ) * 0.4;
		double kincaid    = (11.8 * syllables_per_word) + (0.39 * words_per_sentence) - 15.59;		
		double smog       = 1.043 * Math.sqrt(30 * poly_per_sentence) + 3.1291 ;		
		double ari        = 4.71 * letter_per_word + 0.5 * words_per_sentence - 21.43;
		
		double average = (flesh + fog + kincaid + smog + ari)/5;
		
		f.add(flesh);
		f.add(fog);
		f.add(kincaid);
		f.add(smog);
		f.add(ari);
		f.add(average);
		
		return f;

	}

	private static ArrayList<ArrayList<String>> readFile(String fname) {
        InputDataReader _data = new InputDataReader(fname);
        
        ArrayList<ArrayList<String>> input =  new ArrayList<ArrayList<String>> ();
        
		String current = _data.NextRefSentence();
		while(current != null){			
			ArrayList<String> sents = new ArrayList<String>();						
		    StringTokenizer tokens = new StringTokenizer(current, "\n");
		    while(tokens.hasMoreTokens()){
		    	sents.add((String) tokens.nextElement());
		    }			
			input.add(sents);
			
			current = _data.NextRefSentence();
		}

		return input;
	}
}
