package edu.cmu.cs.lti.relationFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InputDataReader {
	private BufferedReader br;
	
	public InputDataReader(String filename){
		try{
			br = new BufferedReader(new FileReader(filename));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void Close(){
		try{
			if(br!=null){
				br.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public SentenceRawData NextSentence(){		
		SentenceRawData result = new SentenceRawData();
		
		result._sentWithPOS = nextSection();
		
		if(result._sentWithPOS==null) return null;
		
		result._parseTree = nextSection();
		result._dependencyParse = nextSection();
		result._typedDependencyParse = nextSection();
		//result._candidateRelations = nextSection();
		
		return result;
	}
	
	public String NextRefSentence(){		
		return nextSection();
	}
	
	private String nextSection(){
		try{
			StringBuffer data = new StringBuffer();
			String line = br.readLine();
		
			while( line != null && !line.trim().equals("")){
				data.append(line);
				data.append("\n");
				line = br.readLine();
			}
			if(data.length()!=0){
				return data.toString();
			}
			else{
				return null;
			}
			
		}catch(IOException e){
			System.out.println(e.toString());
			return null;
		}
	
	}
}
