package edu.cmu.cs.lti.relationFilter;


public class Entry {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println("Started");
		String inFile = args[0];
		String outFile = args[1];
		String propsFile = args[2];
			
		//String s = "(token1-1 token2-2, rel1-3 rrr-6, sss-10 xxx-8)";
		//Relation r = new Relation(s);
		//System.out.println(r.toString());
		
		//RelationFilter filter = new RelationFilter(inFile,outFile,propsFile);
		//filter.filter();
		
		CandidateGenerator gen = new CandidateGenerator(inFile,outFile);
		gen.generate();
	}

}
