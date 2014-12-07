package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class Relation {
	public boolean _label;
	public Token[] _e1;
	public Token[] _e2;
	public Token[] _r;
	
	public ArrayList<String> _features;
	
	public Relation(){
	}

	public Relation(ArrayList e1, ArrayList r, ArrayList e2){
		_label = true;
		_e1 = new Token[e1.size()];
		_e2 = new Token[e2.size()];
		_r = new Token[r.size()];
		e1.toArray(_e1);
		e2.toArray(_e2);
		r.toArray(_r);
	}
	
	
	public Relation(String relation){
		try{
		_label = true;
		
		int pos1 = relation.indexOf(", ", 0);
		int pos2 = relation.indexOf(", ",pos1+1);
				
		String e1 = relation.substring(1,pos1);
		_e1 = tokenizeString(e1);
		//System.out.println(e1);
		
		String r = relation.substring(pos1+1,pos2);
		_r = tokenizeString(r);
		//System.out.println(r);
		
		String e2 = relation.substring(pos2+1,relation.length()-1);
		_e2 = tokenizeString(e2);
		//System.out.println(e2);
		}catch(Exception e){
			System.out.println(relation);
			System.exit(-1);
		}
	}
	
	/* Feature list
	 *
	 * the number of NPs between e1 and r
	 * the number of NPs between r and e2
	 * the number of VPs between e1 and r
	 * the number of VPs between r and e2
	 * 
	 * length of e1
	 * length of e2
	 * length of r
	 * how many proper nouns does e1 contain?
	 * how many proper nouns does e2 contain?	 
	 * 
	 * POS tags sequence of e1, r, e2 
	 * 
	 * Dependency distance features are added in DependencyDistanceFilter. 
	 * min distance of e1 and e2
	 * max distance of e1 and e2
	 * min distance of e1 and r
	 * max distance of e1 and r
	 * min distance of e2 and r
	 * max distance of e2 and r  
	 */	

	public void setFeatures(ArrayList<String> features){
		_features = features;
	}
	
	public void addFeature( String f){
		_features.add(f);
	}
	public ArrayList<Token> allTokens(){
		ArrayList<Token> tokens = new ArrayList<Token>();
		
		copyTo(tokens,_e1);
		copyTo(tokens,_r);
		copyTo(tokens,_e2);
			
		return tokens;
	}
	
	private Token[] tokenizeString(String s){

		StringTokenizer t = new StringTokenizer(s," ");
		int count = t.countTokens();
		Token[] result = new Token[count];
		for(int i=0;i<count;i++){
			result[i] = new Token(t.nextToken());
		}
		return result;

	}
	
	public void copyTo(ArrayList<Token> al, Token[] ts){
		for(int i=0;i<ts.length;i++){
			al.add(ts[i]);
		}
	}
	
	public String toString(){
		return toString(true);
	}

	public String e1toString(){
		return toString(_e1).toString();
	}

	public String e2toString(){
		return toString(_e2).toString();
	}

	public String rtoString(){
		return toString(_r).toString();
	}
	
	public String toRawString(Token[] toks){
		String current = "";
		for(int i=0; i < toks.length - 1; i++){
			current += toks[i]._token + " ";
		}
		current +=  toks[toks.length - 1]._token ; 
		return current;
	}
	
	public String toOneString(){
		return toRawString(_e1)+ " " + toRawString(_r) + " " + toRawString(_e2) ;
	}


	public String toString(boolean printLabel){
		StringBuffer res = new StringBuffer();
		//res.append('(');
		if(printLabel){
			int label = (_label)?1:0;
			res.append(label);
			res.append(" ||| ");
		}
		res.append(toString(_e1));
		res.append(" ||| ");
		res.append(toString(_r));
		res.append(" ||| ");
		res.append(toString(_e2));
		res.append(" ||| ");
		if(_features!=null){
			for(int i=0; i<_features.size() - 1; i++){
				res.append(_features.get(i));
				res.append(',');
			}
			res.append(_features.get(_features.size() - 1));
		}
		return res.toString();
	}
	
	private StringBuffer toString(Token[] tokens){
		StringBuffer result = new StringBuffer();
		if( tokens[0]._position != -1 ){
			for(int i=0;i<tokens.length; i++){
				result.append(tokens[i].toString());
				if(i!=tokens.length-1)
					result.append(' ');
			}
		}else{
			result.append("");
		}
		
		return result;
	}

}
