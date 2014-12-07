package edu.cmu.cs.lti.relationFilter;

import java.util.*;

public class Dependency {
	private Token _t1;
	private Token _t2;
	private String _type; 
	
	public Dependency(){		
	}
	
	public Dependency(String s){
		try{
		int pos1 = s.indexOf('(');
		int pos2 = s.indexOf(",");
		int pos3 = s.length()-1;
		_type = s.substring(0,pos1);
		_t1 = new Token(s.substring(pos1+1, pos2));
		_t2 = new Token(s.substring(pos2+1, pos3));
		}catch(Exception e){
			System.out.println(s);
			System.exit(-1);
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(_type);
		sb.append('(');
		sb.append(_t1);
		sb.append(",");
		sb.append(_t2);
		sb.append(')');
		return sb.toString();
	}
	
	public Dependency initTypedDependency(String s){
		try{
		int pos1 = s.indexOf('(');
		int pos2 = s.indexOf(", "); //output of typedDependencies in Stanford parser is split by ", "
		int pos3 = s.length()-1;
		_type = s.substring(0,pos1);
		_t1 = new Token(s.substring(pos1+1, pos2));
		_t2 = new Token(s.substring(pos2+1, pos3));
		}catch(Exception e){
			System.out.println(s);
			System.exit(-1);
		}
		return this;
	}	
	
	public Token first(){
		return _t1;
	}
	
	public Token second(){
		return _t2;
	}
	
	public String type(){
		return _type;
	}

}
