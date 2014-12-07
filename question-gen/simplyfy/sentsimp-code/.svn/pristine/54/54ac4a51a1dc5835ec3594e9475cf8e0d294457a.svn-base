package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;
import edu.stanford.nlp.trees.*;

public class SentenceLikeBoundaryFeature implements FilterFunction {
	public SentenceLikeBoundaryFeature(){
		
	}

	public boolean filter(SentenceData s, Relation r){
		Tree s_parent=null;
		
		Tree e1   = GetFirstTreeNode(r._e1,s);
		Tree e2   = GetFirstTreeNode(r._e2,s);		
		Tree anc1 = Tree.leastCommonAncestor(e1, e2);
		
		if ( (anc1.label().toString().equals("S"))  && (!isCrossOverSemiColon(s, r)) ){ return true; }
		
	
		ArrayList<Token> all = new ArrayList<Token>();
		r.copyTo(all, r._e1);

		int m = all.size();
		int lpost= r._e1[m-1]._position;

		//System.out.println("DEBUG lpost = " + s.leafAt(lpost).label() + " POS tag = " +  s.leafAt(lpost).getParent().label() );
		Tree lpost_parent = s.leafAt(lpost).getParent();
		if ( lpost_parent.label().toString().equals("WP") || lpost_parent.label().toString().equals("WDT") || lpost_parent.label().toString().equals("WP$") ) {
			m = m - 1;
		}

		for(int i=0; i < m; i++){
			int pos = all.get(i)._position;
			Tree s_current = null;

			if(pos!=-1){
				try{
					Tree n = s.leafAt(pos);
					if (isCrossOverSBAR(n) == true) {
						//System.out.println("DEBUG SBAR  cross SBAR at : "+ n.label());
						return false; 
					}
					if (isCrossOverSemiColon(s, r) == true) {
						//System.out.println("DEBUG SBAR  cross semi colon at : "+ n.label());
						return false; 
					}
				}
				catch(Exception e){
					System.out.println("DEBUG **** ERROR in SentenceLikeBoundaryFeature ****");
					System.out.println(pos);
					System.out.println(s.toString());
					System.exit(-1);
				}
			}
		}
		
		return true;
	}
	
	private boolean isCrossOverSBAR(Tree node){
		String label = node.label().toString();
		
		//System.out.println("DEBUG current node = " + label);

		if (node == null) {
			return false;
		}

		if (label.equals("SBAR")){
			return true;
		}else{
			if ( (node.getParent() == null) || (label.equals("ROOT")) ) { 
				return false;
			}else{
				return isCrossOverSBAR(node.getParent());
			}
		}
	}
	
	private boolean isCrossOverSemiColon(SentenceData s, Relation r){
		int begin = Math.min(r._e1[0]._position, r._e2[ r._e2.length - 1 ]._position);		
		int end   = Math.max(r._e1[0]._position, r._e2[ r._e2.length - 1 ]._position);
		
		//System.out.format("DEBUG begin, end = %d, %d ; r = %s%n",begin, end, r.toString());
		if (begin == 0){ begin = 1; }
		for (int i = begin; i<= end; i++){			
			if ( s.POSTagAt(i).equals(":") && ( i > 5)){
				if (!s.leafAt(i).toString().equals("-")) {
					//System.out.println(" i = "+i + "  "+ s.POSTagAt(i) + "  " + s.leafAt(i) + "  " + s.leafAt(i).toString().equals("-"));
					return true;
				}
			}			
		}		
		return false;
	}
	
	private Tree GetFirstTreeNode(Token[] tokens,SentenceData s){
		for(int i=0; i<tokens.length;i++){
			if( (tokens[i]._position!=-1) && ( !s.POSTagAt(tokens[i]._position).equals("IN")  ) ){
				//System.out.println("DEBUG tok = "+ tokens[i]._token + " ; position = " + tokens[i]._position );
				return s.leafAt(tokens[i]._position);
			}
		}
		return null;
	}	

}
