package edu.cmu.cs.lti.relationFilter;


public class Train {
	

	/**
	 * Train decoder weights
	 */
	public static void main(String[] args) {
		if (args.length < 3){
			System.err.println("This program is used to optimize decoder weights ");
			System.err.println("Usage: java Train input-file output-file property-file");
			System.exit(0);
		}
   
		//System.out.println("Started");
		String inFile = args[0];
		String outFile = args[1];
		String propsFile = args[2];		
		
		RelationFilter filter = new RelationFilter(inFile, outFile, propsFile);
		filter.train();
	}

}
