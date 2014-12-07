package edu.cmu.cs.lti.relationFilter;


public class Generator {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println("Started");
        
		if (args.length < 2){
			System.err.println("Given an input is a POS-tagged file, this program generate candidate relations");
			System.err.println("Usage: java Generator input-file output-file");
			System.exit(0);
		}

		String inFile = args[0];
		String outFile = args[1];
			
		//String s = "(token1-1 token2-2, rel1-3 rrr-6, sss-10 xxx-8)";
		//Relation r = new Relation(s);
		//System.out.println(r.toString());
		
		//RelationFilter filter = new RelationFilter(inFile,outFile,propsFile);
		//filter.filter();
		
		CandidateGenerator gen = new CandidateGenerator(inFile,outFile);
		gen.generate();
	}

}
