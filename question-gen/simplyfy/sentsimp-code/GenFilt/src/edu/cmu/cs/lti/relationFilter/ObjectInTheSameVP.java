package edu.cmu.cs.lti.relationFilter;

import edu.stanford.nlp.trees.Tree;

public class ObjectInTheSameVP implements FilterFunction {
	public ObjectInTheSameVP(){
		
	}

	public boolean filter(SentenceData s, Relation r) {
		if  ( r._e2[0]._position == 1) return true;
		
	
		Tree e1 = GetFirstTreeNode(r._e1,s);
		Tree e2 = GetFirstTreeNode(r._e2,s);
		Tree rel = GetFirstTreeNode(r._r,s);

		Tree anc1 = Tree.leastCommonAncestor(rel, e2);
		//System.out.println("DEBUG ObjectInTheSameVP:  e1 = " + e1.toString() + " ; e2 = " + e2.toString() + " ; rel = " + rel.toString() + " ; anc1 = " + anc1.toString());

		Tree vp = getVPparent(anc1);		

		if ( (vp==null) && (!s.POSTagAt(1).equals("IN"))) return false;
		
		//Tree anc2 = Tree.leastCommonAncestor(vp, e1);
		//if(anc2==vp) return false;		
		
		return true;
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
	
	private Tree getVPparent(Tree t){
		if(t==null){
			return null;
		}
		if(t.label().toString().equals("VP")){
			return t;
		}
		else{
			return getVPparent(t.getParent());
		}
	}

}
