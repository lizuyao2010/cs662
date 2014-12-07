package edu.cmu.cs.lti.relationFilter;


public class Decoder {
	

	/**
	 * Perform sentence simplification task
	 */
	public static void main(String[] args) {
		if (args.length < 3){
			System.err.println("Given an input is a relation file, this program filters unreasonable candidate relations");
			System.err.println("Usage: java Decoder input-file output-file property-file");
			System.exit(0);
		}
   
		//System.out.println("Started");
		String inFile = args[0];
		String outFile = args[1];
		String propsFile = args[2];			
			
		RelationFilter filter = new RelationFilter(inFile,outFile,propsFile);
		filter.decode();
		
	}

}
