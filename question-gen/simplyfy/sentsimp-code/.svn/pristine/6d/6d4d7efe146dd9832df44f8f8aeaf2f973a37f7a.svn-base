package edu.cmu.cs.lti.relationFilter;

import java.io.*;

public class DataFileWriter {
	private FileWriter _fwriter = null;
	
	public DataFileWriter(String filename){
		try{
		File outFile = new File(filename);
        _fwriter = new FileWriter(outFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void write(String s){
		try{
			_fwriter.write(s);
			}catch(IOException e){
				e.printStackTrace();
			}
	}
	
	public void close(){
		try{
			if(_fwriter!=null)
				_fwriter.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
