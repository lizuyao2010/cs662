package edu.cmu.cs.lti.relationFilter;

/** 
 * A token is a word with its position in the input sentence.
 **/

public class Token {
	public String _token;
	public int _position;
	public Token(String token, int position){
		_token = token;
		_position = position;
	}
	
	public Token(String fullToken){
		//System.out.println(fullToken);
		int index = fullToken.lastIndexOf("-");
		if(index!=-1){
			_token = fullToken.substring(0, index);
			String pos = fullToken.substring(index+1,fullToken.length());
			_position = Integer.parseInt(pos);
		}
		else{
			_token = fullToken;
			_position = -1;
		}
	}
	
	public String toString(){
		if(_position!=-1){
			return _token+"-"+_position;
		}
		else{
			return _token;
		}
	}
}
