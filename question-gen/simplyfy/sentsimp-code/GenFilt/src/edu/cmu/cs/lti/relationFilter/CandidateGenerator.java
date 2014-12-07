package edu.cmu.cs.lti.relationFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;

import mark.chunking.*;

public class CandidateGenerator {
	String _inFile;
	String _outFile;
	
	public CandidateGenerator(String inFile, String outFile){
		_inFile = inFile;
		_outFile = outFile;
	}
	
	public void generate(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(_inFile));
			File outFile = new File(_outFile);
	        FileWriter fwriter = new FileWriter(outFile);
			String line = br.readLine();
			int badSentences = 0;
			int sentid = 0;
			while(line!=null){
				try{
					String chunked = Chunker.instance().processSentence(line);

					sentid++;
					//System.out.format("Finish chunking sentence %d%n%nCHUNKED = %s%nINPUT = |||%s|||%n%n", sentid, chunked, line);
				
					RelationGenerator rg = new RelationGenerator();
					ArrayList<Relation> rels = rg.GenerateRelations(chunked, 0);

					//System.out.println("DEBUG finish generating candidates");

					fwriter.write(line);
					fwriter.write("\n\n");
					for(int i=0; i<rels.size(); i++){
						fwriter.write(rels.get(i).toString(false));
						fwriter.write("\n");
					}
					fwriter.write("\n");
					line = br.readLine();
				}catch(Exception e){
					fwriter.write(line);
					fwriter.write("\n\n\n");
					System.out.println(e.getMessage());
					System.out.println(line);
					badSentences++;
					line = br.readLine();
				}
			}
			System.out.println("Skipped: "+badSentences);
			
			fwriter.close();
			br.close();
			
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
