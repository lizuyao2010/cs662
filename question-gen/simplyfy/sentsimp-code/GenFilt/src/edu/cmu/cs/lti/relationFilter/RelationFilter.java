package edu.cmu.cs.lti.relationFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.cs.lti.relationFilter.Readability;
import edu.stanford.nlp.util.StringUtils;

public class RelationFilter {
	InputDataReader _inData;  //input file
	InputDataReader _refData; //reference file
	
	DataFileWriter _outData;
	
	Properties _props;
	
	Filter fS;	

	int nbest_size         = 100;	
	int max_hyp_stack_size = 200;
	int max_subj_length    = 9;	
	int verbosity          = 0;
	
	String prune_strategy  = "WordCoverage";
	
	ArrayList<Double> weights = new ArrayList<Double>();
	
	String train_ref_file;
	int train_iterations;
	int mOracle = 5;
	Double C = 0.1;
	long FeatureSize;
	String InitWeightsRamdom = "yes";
	String LossFunction = "Ngram";	
	Loss loss = null;

	public ArrayList<String> hyp_features;
	
	public static boolean generateOnly = true;
	
	/**
	 * Loading parameter file
	 **/
	public RelationFilter(String inFile, String outFile, String propsFile){
		_inData = new InputDataReader(inFile);
		_outData = new DataFileWriter(outFile);
		_props = new Properties();		
		
		try{
			_props.load(new FileInputStream(propsFile));
			
			//for debugging
			if ( _props.getProperty("Verbosity") != null ){
				verbosity = Integer.parseInt(_props.getProperty("Verbosity"));
				if(verbosity > 0){ System.out.format("Debugging level = %d%n", verbosity); }
			}

			//maximum number of simplified sets
			if ( _props.getProperty("NBest") != null ){
				nbest_size = Integer.parseInt(_props.getProperty("NBest"));
				if(verbosity > 0){ System.out.format("N-Best simplified candidate = %d%n", nbest_size); }
			}
			
			//max elements in stack
			if ( _props.getProperty("MaxHypothesisStackSize") != null ){
				max_hyp_stack_size = Integer.parseInt(_props.getProperty("MaxHypothesisStackSize"));
				if(verbosity > 0){ System.out.format("Max number of elements in stack = %d%n", max_hyp_stack_size); }
			}
			
			//max number of word in a subject
			if ( _props.getProperty("MaxSubjectLength") != null ){
				max_subj_length = Integer.parseInt(_props.getProperty("MaxSubjectLength"));
				if(verbosity > 0){ System.out.format("Max number of word in a subject = %d%n", max_subj_length); }
			}
			
			//reading weights
			if ( _props.getProperty("DecoderWeights") != null ){
				String[] w =_props.getProperty("DecoderWeights").split(" ");
				for(int i=0; i< w.length; i++){
					weights.add( Double.parseDouble(w[i]) );					
				}				
				if(verbosity > 0){ System.out.println("Decoder weights = "+ Arrays.toString(w)); }
			}
			
			//decoder prunning strategy  
			if ( _props.getProperty("PrunningStrategy") != null ){
				prune_strategy = _props.getProperty("PrunningStrategy");			
				if (!prune_strategy.equalsIgnoreCase("ModelScore") && !prune_strategy.equalsIgnoreCase("WordCoverageModelScore")){ 
					prune_strategy = "WordCoverage";
				}
				if(verbosity > 0){ System.out.println("Prunning strategy       = "+ prune_strategy); }
			}
			
		}catch(IOException e){
			System.err.println("problems reading filtering properties file");
			System.exit(-1);
		}		
		//load  hard filter properties
		//fS = new Filter(_props, verbosity);
		
		//load soft filter features
		fS = new Filter(verbosity);
	}
	
	/**
	 * training decoder weights
	 **/
	public void train(){
		getTrainingParams();		
		
		//reading references
		_refData = new InputDataReader(train_ref_file);		
		ArrayList<ArrayList<String>> reference =  new ArrayList<ArrayList<String>> ();		
		String current = _refData.NextRefSentence();
		while(current != null){
			
			ArrayList<String> sents = new ArrayList<String>();						
		    StringTokenizer tokens = new StringTokenizer(current, "\n");
		    while(tokens.hasMoreTokens()){
		    	sents.add((String) tokens.nextElement());
		    }			
			reference.add(sents);
			
			current = _refData.NextRefSentence();
		}
		if (verbosity > 3){
			int n = reference.size();
			for (int i=0 ; i<n; i++){ System.out.print(reference.get(i)+"\n\n"); }
		}
		
		//reading training instances
		ArrayList<SentenceData> traindata = new ArrayList<SentenceData>(); 
		SentenceRawData currentRawSentence = _inData.NextSentence();
		while(currentRawSentence != null){
			SentenceData currSent = new SentenceData(currentRawSentence, verbosity);
			traindata.add(currSent);
			
			//add filter rules as features
			currSent = fS.addSoftFilterFeature(currSent);
			if (verbosity > 0) { _outData.write(currSent.toString()); }
			
			currentRawSentence = _inData.NextSentence();
		}
		if(verbosity > 3){
			int n = traindata.size();
			for (int i=0 ; i<n; i++){ System.out.print(traindata.get(i).getRawSentenceString() + "\n\n"); }
		}
		
		if (reference.size() != traindata.size()) { 
			System.out.println("Missmatch!!! training data = " + traindata.size()+ " reference = "+ reference.size());
			System.exit(0);
		}
		
		// do MIRA now
		if (InitWeightsRamdom.equals("yes")){
			System.out.print("Random weights          = ");
			weights = new ArrayList<Double>();
			for(int i=0; i< FeatureSize; i++){
				double val = Math.random();				
				weights.add(FourDecimal(val));
				System.out.format("%.4f ", val);				
			}
			System.out.println();
		}
		
		weights = MIRA(train_iterations, traindata, reference, weights, mOracle);		

	}
	
	/**
	 * Read training parameters from properties file
	 */
	private void getTrainingParams() {
		//number of training iterations  
		if ( _props.getProperty("TrainingIterations") != null ){
			train_iterations = Integer.parseInt(_props.getProperty("TrainingIterations"));								
			System.out.println("Training iteration      = "+ train_iterations);
		}
		
		//m-oracle  
		if ( _props.getProperty("mOracle") != null ){
			mOracle = Integer.parseInt(_props.getProperty("mOracle"));								
			System.out.println("Training with m-oracle  = "+ mOracle);
		}
		
		//initialize weights randomly  
		if ( _props.getProperty("InitWeightsRandom") != null ){
			InitWeightsRamdom = _props.getProperty("InitWeightsRandom");			
			if (InitWeightsRamdom.equals("yes")){ 
				System.out.println("Weights will be initialized ramdomly. Remember to specify the number of features.");
			}
		}
		
		//number of features  
		if ( _props.getProperty("FeatureSize") != null ){
			FeatureSize = Long.parseLong(_props.getProperty("FeatureSize"));
			System.out.println("The number of features  = "+ FeatureSize);
		}
		
		//loss function  
		if ( _props.getProperty("LossFunction") != null ){
			LossFunction = _props.getProperty("LossFunction");			
			if (LossFunction.equals("Ngram")){ 
				loss = new NgramLoss(); 
			}			
			System.out.println("Training loss function  = "+ LossFunction);
		}
		
		//C  
		if ( _props.getProperty("C") != null ){
			C = Double.parseDouble(_props.getProperty("C"));								
			System.out.println("Training constant C     = "+ C);
		}
				
		//reference file for training 
		if ( _props.getProperty("TrainingReferenceFile") != null ){
			train_ref_file =_props.getProperty("TrainingReferenceFile");								
			System.out.println("Reference file          = "+ train_ref_file);
		}		
		
	}

	/**
	 * perform MIRA training for decoder weights
	 **/
	private ArrayList<Double> MIRA(int N, ArrayList<SentenceData> traindata, ArrayList<ArrayList<String>> reference, 
			ArrayList<Double> w0, int m) {
		
		ArrayList<String> report = new ArrayList<String>();
		
		ArrayList<Double> best_weights = new ArrayList<Double>();
		double best_averageLoss = -9999999;
		
		ArrayList< ArrayList<Double> > w = new ArrayList< ArrayList<Double> >(); 
		w.add(w0);
				
		int i = 0;
		int skip = 0;
		for(int n=1; n<=N ; n++){
			skip = 0;
			System.out.println("\nIteration "+ n+":\n-------------\nEstimating current loss ...\n");
			
			ArrayList<Double> normalized_w = Normalize_Weight_Vector(w);
			double averageLoss = ComputeAverageLoss(traindata, reference, normalized_w);			
			System.out.format("Average loss before iteration %d: %.4f when using weights = %s%n", n, averageLoss, normalized_w.toString());
			String results = "Average Loss = " + FourDecimal(averageLoss) + " Weigths = " + normalized_w.toString() ;
			report.add(results);
			if (averageLoss > best_averageLoss){
				best_averageLoss = averageLoss;
				best_weights = normalized_w;
			}
			
			int T = traindata.size();
			for(int t=0; t<T; t++){
				
				ArrayList<Hypothesis> H = get_K_Best(traindata.get(t), w.get(i), nbest_size, verbosity);
				
				if (!H.isEmpty()){
					ArrayList<Hypothesis> O = get_M_Oracle(H, reference.get(t), m);		
					
					ArrayList<Double> w_new = UpdateWeight(w.get(i), H, O, reference.get(t));
					
					ArrayList<Hypothesis> H_new = get_K_Best(traindata.get(t), w_new, 1, 0);					
					double new_loss = loss.calculate(reference.get(t), H_new.get(0));
					System.out.println("After updating weights = " + w_new);
					System.out.format("Updated Best: loss = %.4f ; score = %.5f : %s%n", new_loss, H_new.get(0)._score, H_new.get(0).toOneString());
					
					w.add(w_new);				
					i++;
				}else{
					skip++;
					System.out.println("This training instance is quite simple or hard! The trainer will skip it.");
				}				
			}			
		}
		//normalizing weight vectors of the last iteration
		ArrayList<Double> last_weights = Normalize_Weight_Vector(w);
		double averageLoss = ComputeAverageLoss(traindata, reference, last_weights);		
		String results = "Average Loss = " + FourDecimal(averageLoss) + " Weights = " + last_weights.toString() ;
		report.add(results);
		if (averageLoss > best_averageLoss){
			best_averageLoss = averageLoss;
			best_weights = last_weights;
		}
		
		//print report
		System.out.println("\n-----------\nTraining report\n");
		for(int j = 0; j< report.size(); j++){
			System.out.println("Iteration " + j + ": " + report.get(j));
		}		
		
		System.out.println("\nNumber of updated weights vectors = " + w.size() + " ; Skip instances = " + skip);				
		System.out.print("Best weights = ");
		for(int j =0; j< best_weights.size(); j++){
			System.out.print(best_weights.get(j).toString()+ " ");
		}					
		System.out.format("\nAverage loss with optimized weights: %.4f%n", best_averageLoss);
		
		return best_weights;
	}

	/**
	 * Return average weight vector
	 **/
	private ArrayList<Double> Normalize_Weight_Vector(ArrayList<ArrayList<Double>> w) {
		ArrayList<Double> final_weights = new ArrayList<Double>();
		for(int j=0; j< w.get(0).size(); j++){ final_weights.add(0.0); }
		
		for (int j=0;  j< w.size(); j++){
			for (int k =0; k< w.get(j).size(); k++){
				double val = w.get(j).get(k);
				if (final_weights.get(k) == 0.0 ){ 
					final_weights.set(k, val);
				}else{
					final_weights.set(k, final_weights.get(k) + val);
				}								
			}			
		}
		for(int k =0; k< final_weights.size(); k++){
			double normalize = final_weights.get(k) / w.size();
			normalize = FourDecimal(normalize);
			final_weights.set(k, normalize );			
		}		
		
		return final_weights;
	}

	/**
	 * Compute average loss per sentence in training data
	 **/
	private double ComputeAverageLoss(ArrayList<SentenceData> traindata,
			ArrayList<ArrayList<String>> reference, ArrayList<Double> w) {
		double avg = 0.0;		
		fS.setVerbosity(0);
		
		int T = traindata.size();
		for(int t=0; t<T; t++){			
			ArrayList<Hypothesis> H = get_K_Best(traindata.get(t), w, 1, 0);
			//System.out.println(t + " : Ref = " + reference.get(t)); 
			
			if (!H.isEmpty()) { 
				//System.out.println("     Hyp = " + H.get(0).toOneString());
				avg +=  loss.calculate(reference.get(t), H.get(0));								
			} else{				
				//System.out.println("     Hyp = EMPTY");				
			}
			
			//System.out.println("     avg = " + avg);			
		}
		
		return avg/T;
	}

	/**
	 * update weight according to a loss function
	 **/
	private ArrayList<Double> UpdateWeight(ArrayList<Double> wi, ArrayList<Hypothesis> H, ArrayList<Hypothesis> O, ArrayList<String> ref) {		
		ArrayList<Double> update = new ArrayList<Double>();
		for(int i=0; i< wi.size(); i++){ update.add(0.0); }
		
		for (int o = 0; o < O.size(); o++){
			for(int h =0; h < H.size(); h++){
				ArrayList<Double> diff = FeatureDiff(O.get(o)._hypFeatures, H.get(h)._hypFeatures);
				
				double alpha = getAlpha(O.get(o), H.get(h), ref);				
				
				ArrayList<Double> alpha_time_diff = new ArrayList<Double>();
				for(int i=0; i < diff.size(); i++){
					double val = alpha * diff.get(i);
					alpha_time_diff.add(val);
				}
				
				for(int i=0; i < alpha_time_diff.size(); i++){
					double newval = update.get(i) + alpha_time_diff.get(i);					
					update.set(i, newval);
				}
			}
		} 
		
		int normalize = O.size() * H.size(); 
			
		ArrayList<Double> wnew = new ArrayList<Double>();
		for(int i=0; i< wi.size(); i++){
			double val = wi.get(i) + update.get(i)/normalize;			
			val = FourDecimal(val);			
			wnew.add(val);
		}
		
		return wnew;
	}

	/**
	 * alpha is the step size of updated quantities
	 **/
	private double getAlpha(Hypothesis o, Hypothesis h, ArrayList<String> ref) {
		
		double loss_O_Ref = loss.calculate(ref, o);
		double loss_H_Ref = loss.calculate(ref, h);
		double L = loss_O_Ref - loss_H_Ref;
		
		double scorediff = o._score - h._score;
		
		double nominator = L - scorediff;
		
		//L2 norm
		double denominator = 0.0;
		for (int i = 0; i < o.size(); i++) {			
			double val = o._hypFeatures.get(i) - h._hypFeatures.get(i) ;
			denominator += Math.pow(val, 2);
		}
		
		double delta =  nominator/denominator;
		if (Double.isNaN(delta)) delta = 0.0;
					
		double alpha = Math.max(0, Math.min(C, delta)); 
		
		return alpha;
	}

	/**
	 * return the difference between oracle and hypothesis feature vector 
	 **/
	private ArrayList<Double> FeatureDiff(ArrayList<Float> o, ArrayList<Float> h) {
		ArrayList<Double> diff = new ArrayList<Double>();

		for (int i = 0; i < o.size(); i++) {			
			double val = o.get(i) - h.get(i) ;			
			diff.add(i, val);
		}
		return diff;
	}

	/**
	 * extract m-oracle candidates from K-best list
	 **/
	private ArrayList<Hypothesis> get_M_Oracle(ArrayList<Hypothesis> h,	ArrayList<String> ref, int m) {
		ArrayList<Hypothesis> orac = new ArrayList<Hypothesis>();
				
		for(int i =0; i< h.size(); i++){
			h.get(i)._loss = loss.calculate(ref, h.get(i));						
		}
		
		System.out.format("Original Best: loss = %.4f ; score = %.5f : %s%n", h.get(0)._loss, h.get(0)._score, h.get(0).toOneString());
		
		//sort hyp according to loss function
		Collections.sort( h, new Comparator<Hypothesis>() {			
			public int compare(Hypothesis o1, Hypothesis o2 ){
				if ( o1._loss > o2._loss) {return -1;} 
				else if  ( o1._loss < o2._loss) {return 1;}
				else {return 0;}
			}});		
		
		//checking if reference appear in the k-best
		Pattern p = Pattern.compile("\\p{Punct}");	    
		
		String reference = StringUtils.join(ref, " ").trim();
		Matcher match = p.matcher(reference);			
		String ref_clean = match.replaceAll("");			
		ref_clean = ref_clean.replaceAll("\\s+", " ");
				
		//System.out.println("REF CLEAN = |||" + ref_clean + "|||\n");
		
		for(int i =0; i< h.size(); i++){	
			String hypothesis = h.get(i).toOneNiceString(" ").trim();
			
			match = p.matcher(hypothesis);			
			String hyp_clean = match.replaceAll("");			
			hyp_clean = hyp_clean.replaceAll("\\s+", " ");
		    
		    //System.out.println("HYP CLEAN = |||"+ hyp_clean + "|||");
			
			if (hyp_clean.equalsIgnoreCase(ref_clean)){
				if (verbosity > 0) { System.out.println("EXACT match!!! Found reference in k-best list, entry = " + i); }				
				orac.add(h.get(i)); //forcing this hypothesis into oracle
				m--;
			}
		}
		
		//get m-oracle
		m = Math.min(m, h.size());
		for(int i=0; i< m; i++){			
			orac.add(h.get(i));
		}
		System.out.format("Oracle: rank = %d ; loss = %.4f ; score = %.5f : %s%n", orac.get(0)._rank, h.get(0)._loss, h.get(0)._score, h.get(0).toOneString());
		
		return orac;
	}

	/**
	 * Construct simplified candidates from a set of simplified sentences
	 * A simplified candidate contains at least 2 simplified sentences 
	 * This method used stack decoding algorithm with pruning strategies.
	 * return K-best simplified candidates
	 * @param sent
	 * @param w
	 * @param K
	 * @return
	 */
	 
	@SuppressWarnings("unchecked")
	private ArrayList<Hypothesis> get_K_Best( SentenceData currSent, ArrayList<Double> w, int K, int debug ) {		
		if (debug > 0) {System.out.println("\nSENTENCE: " + currSent.getRawSentenceString()+"\n" );}
		
		//do hard filtering
		//currSent = fS.filterSentence(currSent);
		//if (debug > 0) { _outData.write(currSent.toString()); }		
		
		Object[] Obj = getObjTable(currSent);		
		Map<String, ArrayList<Relation>> ObjTable = (Map<String, ArrayList<Relation>>) Obj[0];
		Relation r1 =  (Relation) Obj[1];
		
		ArrayList< Stack<Hypothesis> > hypoStack = new ArrayList < Stack<Hypothesis> >();
		Stack <Hypothesis> h = new Stack< Hypothesis >();
		
		ArrayList<Hypothesis> hypothesis = new ArrayList<Hypothesis>();				
		
		h = Init_HypStack_By_First_VP(ObjTable, r1); 

		hypoStack.add(h);
		
		//determine number of simplified sentence dynamically
		int max_simp_sent = currSent.VP.size() + 1;
		if (debug > 0) { System.out.format("Max number of simplified sentences per candidate = %d%n", max_simp_sent); }
		
		//hypothesis expansion
		for (int i =0; i< max_simp_sent - 1; i++){
			Stack < Hypothesis > expand_h = new Stack< Hypothesis >();
			Stack < Hypothesis > cp_expand_h = new Stack< Hypothesis >();

			while ( ! hypoStack.get(i).empty() ){
				Hypothesis r = hypoStack.get(i).pop();
				expand_h = Expanding_Hypothesis(r, ObjTable, expand_h, currSent);
			}
			
			expand_h = Prune_Hyp(expand_h, max_hyp_stack_size, prune_strategy, currSent, w);
			
			//expand_h = Prune_Hyp_By_Word_Covered(expand_h, max_hyp_stack_size);			
			
			cp_expand_h = (Stack < Hypothesis >) expand_h.clone();
			
			hypoStack.add(i+1, expand_h);			
			
			while ( ! cp_expand_h.empty() ){
				Hypothesis r = cp_expand_h.pop();
				hypothesis.add(r);
			}
		}
		
		ArrayList< Hypothesis > unique_hypothesis   = Unique_Hypothesis(hypothesis);		
		//if (debug > 0){System.out.println("Hyp size = " + hypothesis.size() + "  unique = " + unique_hypothesis.size());}
		
		//unique_hypothesis = computeHypScore(unique_hypothesis, currSent, w); 
		unique_hypothesis = computeHypExpScore(unique_hypothesis, currSent, w);
		
		unique_hypothesis = Sort_Hyp(unique_hypothesis, currSent);
		
		if (debug > 0) { System.out.println("Hypothesis size = " + unique_hypothesis.size()); }

		//assign rank
		for (int i =0; i< unique_hypothesis.size(); i++){ unique_hypothesis.get(i)._rank = i; }
		
		ArrayList< Hypothesis > kbest = new ArrayList<Hypothesis>();
		
		int top = Math.min( nbest_size, unique_hypothesis.size()) ;		
		if(unique_hypothesis.size() == 0){ 
			if (debug > 0){System.out.println("No candidate has been found"); }
		}
		int i = 0;
		while ( i < top ){
			kbest.add(unique_hypothesis.get(i));
			i++;
		}		
		
		return kbest;
	}

	/**
	 * Call different prunning strategies
	 **/
	private Stack<Hypothesis> Prune_Hyp(Stack<Hypothesis> expandH,
			int maxHypStackSize, String pruneStrategy, SentenceData currSent, ArrayList<Double> w) {
		
		Stack < Hypothesis > sorted_HypStack = new Stack < Hypothesis > ();
		
		if (pruneStrategy.equalsIgnoreCase("WordCoverage")){
			sorted_HypStack = Prune_Hyp_By_Word_Covered(expandH, maxHypStackSize);
		}
		else if (pruneStrategy.equalsIgnoreCase("ModelScore")){
			sorted_HypStack = Prune_Hyp_By_Model_Score(expandH, maxHypStackSize, currSent, w);
		}		
		else if (pruneStrategy.equalsIgnoreCase("WordCoverageModelScore")){
			sorted_HypStack = Prune_Hyp_By_WCMS(expandH, maxHypStackSize, currSent, w);
		}
		return sorted_HypStack;
	}

	/**
	 * Pruning by WordCoverage and Model Scores
	 */
	private Stack<Hypothesis> Prune_Hyp_By_WCMS(Stack<Hypothesis> HypStack,
			int maxHypStackSize, SentenceData currSent, ArrayList<Double> w) {
		
		Stack <Hypothesis> sorted_HypStack = new Stack <Hypothesis> ();
		
		//first quickly prune hyp by word coverage
		//we set prunned hyp stack 10 time bigger than the max hyp stack size
		int maxHypSize = maxHypStackSize * 10; 
		HypStack = Prune_Hyp_By_Word_Covered(HypStack, maxHypSize);		
		
		//now start prunning with model score 
		ArrayList<Hypothesis> sorted_Hyp = new ArrayList<Hypothesis>();		
		ArrayList <Hypothesis> hyp = new ArrayList  <Hypothesis> ();		
		while ( ! HypStack.empty() ){
			Hypothesis r = HypStack.pop();
			hyp.add(r);		
		}
		
		sorted_Hyp = computeHypExpScore(hyp, currSent, w);
		sorted_Hyp = Sort_Hyp(sorted_Hyp, currSent);
		
		int i = 0;
		int top = Math.min( maxHypStackSize, hyp.size()) ;
		while ( i < top ){
			sorted_HypStack.push( hyp.get(i) );
			i++;
		}		
		
		return sorted_HypStack;
	}

	/**
	 * Prune partial hypothesis stack according to theirs current model scores
	 * We do not take into account future cost estimation  
	 **/
	private Stack<Hypothesis> Prune_Hyp_By_Model_Score(
			Stack<Hypothesis> HypStack, int maxHypStackSize, SentenceData currSent, ArrayList<Double> w) {
		
		Stack <Hypothesis> sorted_HypStack = new Stack <Hypothesis> ();
		
		ArrayList<Hypothesis> sorted_Hyp = new ArrayList<Hypothesis>();
		
		ArrayList <Hypothesis> hyp = new ArrayList  <Hypothesis> ();		
		while ( ! HypStack.empty() ){
			Hypothesis r = HypStack.pop();
			hyp.add(r);		
		}
		
		sorted_Hyp = computeHypExpScore(hyp, currSent, w);
		sorted_Hyp = Sort_Hyp(sorted_Hyp, currSent);
		
		int i = 0;
		int top = Math.min( maxHypStackSize, hyp.size()) ;
		while ( i < top ){
			sorted_HypStack.push( hyp.get(i) );
			i++;
		}		
		
		return sorted_HypStack;
	}

	/***
	 * compute hyp feature and score: linear sum of weight*feature
	 * this can be used to rank hyp 
	 **/
	private ArrayList<Hypothesis> computeHypScore(ArrayList<Hypothesis> hypothesis, SentenceData sent, ArrayList<Double> w) {		
		for (int j=0; j< hypothesis.size(); j++){
			Hypothesis c = hypothesis.get(j);
			c._hypFeatures = getHypFeatures(c, sent);
			c._score       = getHypScore(c._hypFeatures, w);			
		}		
		return hypothesis;
	}
	
	/***
	 * compute hyp feature and score with the exponential model	 
	 * to avoid infinity when compute log sum of exp use this trick
	 * log \sum (exp (xi) ) = m + log \sum ( exp(xi - m) )	  
	 * m = max of xi
	 **/
	private ArrayList<Hypothesis> computeHypExpScore(ArrayList<Hypothesis> hypothesis, SentenceData sent, ArrayList<Double> w) {
		double m = -9999999;
		double Z = 0.0; // partition function
		
		for (int j=0; j< hypothesis.size(); j++){
			Hypothesis c    = hypothesis.get(j);
			c._hypFeatures  = getHypFeatures(c, sent);
			double rawscore = getHypScore(c._hypFeatures, w); 
			if (m < rawscore ) { m = rawscore;}			
		}
		
		for (int j=0; j< hypothesis.size(); j++){
			Hypothesis c    = hypothesis.get(j);
			double rawscore = getHypScore(c._hypFeatures, w);
			Z += Math.exp( rawscore - m );			
		}
		
		Z = m + Math.log(Z);
		
		for (int j=0; j< hypothesis.size(); j++){
			Hypothesis c  = hypothesis.get(j);
			double cscore = m + Math.log( Math.exp(getHypScore(c._hypFeatures, w) - m) );
			c._score      = cscore - Z;			
		}		
		return hypothesis;
	}

	/**
	 * each object will have different simplified sentences depending on its object
	 * @param currSent
	 * @return
	 */		
	@SuppressWarnings("unchecked")
	private Object[] getObjTable(SentenceData sent) {
		Object[] ObjTable = new Object[2];
		ObjTable[0] = new HashMap<String, ArrayList<Relation>>();
		ObjTable[1] = new Relation();		
		
		//scan over possible simplified sentences and create a multi-map
		int min_post = 99999;
		
		for(int j=0; j < sent.relationCount(); j++){
			Relation r = sent.relationAt(j);
			if (r._label == true){ // only look at good candidates

				if ( min_post >= r._e2[ r._e2.length - 1 ]._position ) {
					ObjTable[1] = r;
					min_post = r._e2[ r._e2.length - 1 ]._position ;
				}

				String np2 = r.e2toString();
				//System.out.format("DEBUG np2 = %s ; min position = %d%n", np2, min_post);

				ArrayList<Relation> l = ((Map<String, ArrayList<Relation>>) ObjTable[0]).get(np2);
				if (l == null) {	
					((Map<String, ArrayList<Relation>>) ObjTable[0]).put(np2, l=new ArrayList<Relation>() );
				}
				l.add(r);
			}
		}	
		/*for (String key : ObjTable.keySet()){
			System.out.println("DEBUG MultiMap  key = "+ key + "  ; values = "+ ObjTable.get(key));
		}*/	
		return ObjTable;
	}

	/**
	 * The main method performs decoding	
	 */
	public void decode(){
		int sentencesProcessed = 0;
		
		SentenceRawData currentRawSentence = _inData.NextSentence();
		
		while(currentRawSentence!=null){			
			
			SentenceData currentSentence = new SentenceData(currentRawSentence, verbosity);
			
			//add filter rules as features
			currentSentence = fS.addSoftFilterFeature(currentSentence);
			if (verbosity > 0) { _outData.write(currentSentence.toString()); }
						
			ArrayList< Hypothesis > hypothesis  = get_K_Best(currentSentence, weights, nbest_size, verbosity);
			
			//print out top n hyp
			printHypothesis(hypothesis,currentSentence, verbosity);			

			currentRawSentence = _inData.NextSentence();
			
			sentencesProcessed++;
			if(sentencesProcessed%100 == 0){
				if (verbosity > 0) { System.out.println("Processed: "+sentencesProcessed+" sentences"); }
			}
		}
		_outData.close();
		
	}	
	
	private void printHypothesis(ArrayList<Hypothesis> unique_hypothesis, SentenceData sent, int debug) {
		int i = 0;
		int top = Math.min( nbest_size, unique_hypothesis.size()) ;
		
		if(unique_hypothesis.size() == 0){
			System.out.println("We have no suggestion to simplify your sentence.\n");
		}else{
			if (debug == 0){
				while ( i < top ){
					String nice_output = unique_hypothesis.get(i).toOneNiceString();
					System.out.println(nice_output);					
					i++;
				}
			}else{			
				while ( i < top ){			
					ArrayList<Float> hf = unique_hypothesis.get(i)._hypFeatures;
								
					String result = "HYP " + Integer.toString(i) +" [ " 
					+ "Size = "+ Float.toString(hf.get(0))
					+ "; score = "+ String.format("%.5f", unique_hypothesis.get(i)._score)			
					+ " ]\n" ;
					
					String result_one_line = "1-LINE-HYP: "+ Float.toString(hf.get(0)) + " ||| " + unique_hypothesis.get(i).toOneString() + "\n\n"; // for offline debugging
					
					for (int j=0; j< unique_hypothesis.get(i).size(); j++){
						//System.out.format("DEBUG i = %d ; j = %d ; s = %s%n", i, j, hypothesis.get(i).get(j).toString());				
						result +=  unique_hypothesis.get(i).get(j).toString() + "\n";
					}
					result += "\n";
					_outData.write(result);			
											
					if (verbosity > 1) { _outData.write(result_one_line); }	
					
					i++;
				}		
				_outData.write("#END#\n\n");
			}						
		}

	}

	/**
	 * unique hypothesis list
	 **/
	private ArrayList<Hypothesis> Unique_Hypothesis( ArrayList<Hypothesis> hypothesis) {
		
		ArrayList<Hypothesis> unq    =  new ArrayList<Hypothesis>();		
		HashMap <String, Integer> hm = new HashMap <String, Integer>();
		
		for (int j=0; j< hypothesis.size(); j++){
			Hypothesis c = hypothesis.get(j);
			
			if (!hm.containsKey(c.toOneString())){
				//System.out.println("DEBUG c = " + c.toOneString());
				hm.put(c.toString(), 1); 
				unq.add(c);
			}
		}		
		return unq;
	}	

	/***
	 * computer Hyp score with weights, currently we have 3 feature weights
	 * @param hypFeatures
	 * @param weight vector
	 * @return
	 */
	private double getHypScore(ArrayList<Float> hf, ArrayList<Double> w) {
		double score = 0.0;
		
		for(int i=0; i < hf.size(); i++){
			score += w.get(i) * hf.get(i);
		}				
		
		return score;				
	}

	/**
	 * Compute feature for each hypothesis and score the hypothesis
	 **/
	private ArrayList<Float> getHypFeatures(Hypothesis o1, SentenceData sent){
		ArrayList<Float> hf = new ArrayList<Float>();
		
		Features F = new Features(o1, sent);
		hf = F.getFeaturesVector();
		
		return hf;
	}
	
	/**
	 * Sort hypothesis stack according to their score
	 **/
	private ArrayList<Hypothesis> Sort_Hyp( ArrayList<Hypothesis> hypothesis, SentenceData sent) {		
		Collections.sort( hypothesis, new Comparator<Hypothesis>() {			
			public int compare(Hypothesis o1, Hypothesis o2 ){
				if ( o1._score > o2._score) {return -1;} 
				else if  ( o1._score < o2._score) {return 1;}
				else {return 0;}
			}});
		
		return hypothesis;
	}

	/***
	 * Find hypotheses which can cover all base NPs
	 * return 2 list: one with all NPs are covered the other is not
	 * */
	private ArrayList <ArrayList<Hypothesis>> Filter_Hyp_By_NP_Coverage(ArrayList<Hypothesis> hypothesis, SentenceData sent) {
		
		ArrayList<Hypothesis> h  = new ArrayList<Hypothesis>();		
		ArrayList<Hypothesis> nh  = new ArrayList<Hypothesis>();
		
		ArrayList <ArrayList<Hypothesis>> hyplist = new ArrayList<ArrayList<Hypothesis>>();  
		
		ArrayList<String> NPlist = sent.NPtoString();		

		//a hyp must cover all NPs
		for (int i=0; i< hypothesis.size(); i++){
			ArrayList<String> hyp_NPlist = new ArrayList<String> ();			
			
			for (int j=0; j< hypothesis.get(i).size(); j++){
				hyp_NPlist.add( hypothesis.get(i).get(j).e1toString() );
				hyp_NPlist.add( hypothesis.get(i).get(j).e2toString() );							
			}
			
			boolean isGoodHyp = true;
			for (int k=0; k< NPlist.size(); k++){
				boolean found_np = false;
				
				for (int m=0; m< hyp_NPlist.size(); m++){
					if ( hyp_NPlist.get(m).contains(NPlist.get(k)) ) { 	
						found_np = true;
					}
				}
				if (found_np == false) {
					//System.out.println("DEBUG sent = " + sent.getRawSentenceString());
					//System.out.println("DEBUG not found np = " + NPlist.get(k) + "\n"+ Hyp2String(hypothesis.get(i)) );
					isGoodHyp = false;
					break;
				}
			}			
			if (isGoodHyp == true) { 
				h.add(hypothesis.get(i));
			}else{
				nh.add(hypothesis.get(i));
			}
		}
		hyplist.add(h);
		hyplist.add(nh);
		
		return hyplist;
	}
	
	/**
	 * initialize hypothesis stacks by the first vp
	 **/
	private Stack < Hypothesis > Init_HypStack_By_First_VP(Map<String, ArrayList<Relation>> ObjT, Relation r1){		
		Stack < Hypothesis > h = new Stack< Hypothesis >();
		for (String key : ObjT.keySet()){
			for (Relation t : ObjT.get(key)){
			
				int vp_position = t._r[0]._position;			
				int r1_vp_position = r1._r[0]._position;
			
				if (vp_position == r1_vp_position){				
					Hypothesis ph = new Hypothesis();
					ph.add(t);
					h.push(ph);
				}
			}			
		}
		return h;
	}

	/**
	 * initialize hypothesis stacks by the first object
	 **/
	private Stack < Hypothesis > Init_Stack_By_First_Object(Map<String, ArrayList<Relation>> ObjT, int min_post){
		Stack < Hypothesis > h = new Stack< Hypothesis >();
		for (String key : ObjT.keySet()){		
			Relation tmp = ObjT.get(key).get(0);
			int npobj = tmp._e2[ tmp._e2.length - 1 ]._position;
			//System.out.format("DEBUG MultiMap  key = %s ; value[0] = %s ; last position = %d%n", key, tmp.toString(), npobj);

			if (npobj == min_post){
				for (Relation t : ObjT.get(key)){
					Hypothesis ph = new Hypothesis();
					ph.add(t);
					h.push(ph);
				}
			}			
		}
		return h;		
	}
	
	/**
	 * initialize hypothesis stacks by the all possible sentences 
	 **/
	private Stack <Hypothesis > Init_Stack_By_All( SentenceData sent ){
		Stack < Hypothesis > h = new Stack< Hypothesis >();
		
		for(int j=0; j < sent.relationCount(); j++){
			Relation r = sent.relationAt(j);
			if (r._label == true){ // only look at good candidates
				Hypothesis ph = new Hypothesis();
				if ( r._e1[ r._e1.length - 1 ]._position < max_subj_length){
					ph.add(r);
					h.push(ph);
				}
			}
		}
		return h;
	}
	
	/**
	 * Expand partial hypotheses 
	 **/
	private Stack <Hypothesis> Expanding_Hypothesis( Hypothesis partial_hyp ,  
			Map<String, ArrayList<Relation>> ot, Stack < Hypothesis > hyp_expansion, SentenceData sent ) {	

		Map<String, Integer> covered_obj = new HashMap<String, Integer>();

		for(int i=0; i < partial_hyp.size(); i++){
			Relation r = partial_hyp.get(i);
			String np2 = r.e2toString();
			covered_obj.put(np2, 1);
		}
		
		for (String obj : ot.keySet()){
			//if an object has not been expanded			
			if ( ! covered_obj.containsKey(obj)  ){
				if (verbosity > 5) {System.out.println("DEBUG trying to expand object = "+ obj);}
				for (Relation t : ot.get(obj)){
					Hypothesis new_hyp = new Hypothesis();
					
					new_hyp.addAll(partial_hyp);
					
					boolean isNewHyp = true;
					for (int k =0; k < partial_hyp.size(); k++){
						String np1 = partial_hyp.get(k).e1toString();
						String vp  = partial_hyp.get(k).rtoString();
						String np2 = partial_hyp.get(k).e2toString();						
						
						//only expand if non-overlapping object
						if (( t.rtoString().equals(vp) ) || (isVPwithPPAttachment(t._r, partial_hyp.get(k)._r))) {
							for (int i =0; i< partial_hyp.get(k)._e2.length; i++ ){
								String tok = partial_hyp.get(k)._e2[i].toString();								
								if (t.e2toString().contains(tok)) {	
									isNewHyp = false;
									//System.out.println("DEBUG ExpandHYP\n curr = "+ vp + " ; "+ np2 + "\n t_vp = " + t.rtoString() + " ; "+ t.e2toString() 
									//		+ "\n isNewHyp = " + isNewHyp + " ; tok = " + tok) ;
									break;
								}								
							}
						}
						//non-overlapping subject if no relative clause is attached
						
						for (int i =0; i< partial_hyp.get(k)._e2.length; i++ ){
							String tok = partial_hyp.get(k)._e2[i].toString();
							int lp = t._e1[ t._e1.length - 1]._position;
							
							if  ( t.e1toString().contains(tok) && (lp < sent.size() -2 )){
								if ( !(sent.POSTagAt(lp+1).equals("WDT") || sent.POSTagAt(lp+1).equals("WP") ||  sent.POSTagAt(lp+1).equals("WP$") ) && 
								     !( sent.POSTagAt(lp+1).equals(",") || sent.POSTagAt(lp+2).equals("WDT") || sent.POSTagAt(lp+2).equals("WP") &&  sent.POSTagAt(lp+2).equals("WP$") )  ) {
									if (verbosity > 5) { System.out.println("DEBUG POS = " + sent.POSTagAt(lp) + " " + sent.POSTagAt(lp+1) +" " + sent.POSTagAt(lp+2) );}
									isNewHyp = false;								
									break;
								} 	
							}								  
						}						
					}
					if (isNewHyp){
						new_hyp.add(t);
						hyp_expansion.push(new_hyp);
					}else{
						if (verbosity > 5) { System.out.println("DEBUG cannot expand with t = " + t.toString());}
					}
				}
			} else {
				if (verbosity > 5) { System.out.println("DEBUG obj ::: "+ obj + " ::: is covered"); }
			}
		}

		return hyp_expansion;
	}	

	/**
	 * prune hypothesis stack by the number of words each hyp covered
	 * sort stack by covered word, only keep top K hyps
	 **/	
	private Stack <Hypothesis> Prune_Hyp_By_Word_Covered( Stack < Hypothesis > HypStack, int max_size){

		Stack < Hypothesis > sorted_HypStack = new Stack < Hypothesis > ();

		ArrayList  < Hypothesis > hyp = new ArrayList  < Hypothesis > ();
		
		while ( ! HypStack.empty() ){
			Hypothesis r = HypStack.pop();
			hyp.add(r);		
		}
		
		Collections.sort( hyp, new Comparator<Hypothesis>() {
			public int compare(Hypothesis o1, Hypothesis o2 ){
				int o1_count = 0;
				int o2_count = 0;

				//System.out.format("DEBUG o1.size = %d ; o2.size = %d %n", o1.size(), o2.size());

				for (int j=0; j< o1.size(); j++){
					o1_count += o1.get(j)._e1.length +  o1.get(j)._e2.length +  o1.get(j)._r.length;
				}

				for (int j=0; j< o2.size(); j++){
					o2_count += o2.get(j)._e1.length +  o2.get(j)._e2.length +  o2.get(j)._r.length;
				}

				return o2_count - o1_count;
			}});

		if (verbosity > 5){
			for(Hypothesis r : hyp){
				for (int j=0; j< r.size(); j++){
					System.out.format("DEBUG j = %d ; ph = %s%n", j, r.get(j).toString());
				}
			}
		}

		int i = 0;
		int top = Math.min( max_size, hyp.size()) ;

		while ( i < top ){
			sorted_HypStack.push( hyp.get(i) );
			i++;
		}		
		return sorted_HypStack;
	}	

	/**
	 * check if VP contains PP
	 */
	private boolean isVPwithPPAttachment(Token[] r, Token[] r2) {
		if ( Math.abs(r.length-r2.length) > 1) return false;
		
		int s = Math.max(r.length, r2.length);
		for(int i =0; i< s-1; i++){
			if (r[i]._position != r2[i]._position ) return false;
		}
		if ((r.length > r2.length) &&  ( !isPreposition(r[s-1]._token)) ) return false;
		if ((r2.length > r.length) &&  ( !isPreposition(r2[s-1]._token)) ) return false;
		
		return true;
	}
	
	private double FourDecimal(double val) {		
		DecimalFormat fourDec = new DecimalFormat("0.0000");
		val = Double.parseDouble(fourDec.format(val));
		return val;
	}
	
	/**
	 * is the word a preposition? 
	 **/
	private boolean isPreposition(String token) {
		HashMap<String, String> _prepositions=null;
		if(_prepositions == null){
			_prepositions = new HashMap<String, String>();
			_prepositions.put("aboard","x");
			_prepositions.put("about","x");
			_prepositions.put("above","x");
			_prepositions.put("across","x");
			_prepositions.put("against","x");
			_prepositions.put("along","x");
			_prepositions.put("alongside","x");
			_prepositions.put("among","x");
			_prepositions.put("amongst","x");
			_prepositions.put("around","x");
			_prepositions.put("at","x");
			_prepositions.put("behind","x");
			_prepositions.put("below","x");
			_prepositions.put("between","x");
			_prepositions.put("beyond","x");
			_prepositions.put("by","x");
			_prepositions.put("down","x");
			_prepositions.put("during","x");
			_prepositions.put("except","x");
			_prepositions.put("for","x");
			_prepositions.put("from","x");
			_prepositions.put("in","x");
			_prepositions.put("including","x");
			_prepositions.put("inside","x");
			_prepositions.put("into","x");
			_prepositions.put("near","x");
			_prepositions.put("of","x");
			_prepositions.put("off","x");
			_prepositions.put("on","x");
			_prepositions.put("onto","x");
			_prepositions.put("out","x");
			_prepositions.put("outside","x");
			_prepositions.put("over","x");
			_prepositions.put("past","x");
			_prepositions.put("per","x");
			_prepositions.put("throughout","x");
			_prepositions.put("toward","x");
			_prepositions.put("towards","x");
			_prepositions.put("under","x");
			_prepositions.put("underneath","x");
			_prepositions.put("up","x");
			_prepositions.put("upon","x");
			_prepositions.put("with","x");
			_prepositions.put("within","x");
			_prepositions.put("without","x");
		}
		return _prepositions.containsKey(token);		
	}	

}
