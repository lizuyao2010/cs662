package edu.cmu.cs.lti.relationFilter;

import java.util.ArrayList;


public class DependencyDistanceFilter implements FilterFunction {
	private int _maxDist;
	private int _minDist;
	private int _spanDist;
	
	private int _minDistOfNP1andNP2 = 99999;
	private int _maxDistOfNP1andNP2 = 0;

	private int _minDistOfNP1andVP = 99999;
	private int _maxDistOfNP1andVP = 0;

	private int _minDistOfNP2andVP = 99999;
	private int _maxDistOfNP2andVP = 0;

	
	public DependencyDistanceFilter(int minDist, int maxDist, int spanDist){
		_minDist = minDist;
		_maxDist = maxDist;
		_spanDist = spanDist;		
	}

	public boolean filter(SentenceData s, Relation r) {
		
		DependencyTree tree = s.dependTree();
		if(tree==null){
			return false;
		}     
		
//		_minDistOfNP1andNP2 = getMinDistance(tree, r._e1, r._e2);
//		_maxDistOfNP1andNP2 = getMaxDistance(tree, r._e1, r._e2);
//		
//		_minDistOfNP1andVP = getMinDistance(tree, r._e1, r._r);
//		_maxDistOfNP1andVP = getMaxDistance(tree, r._e1, r._r);
//		
//		_minDistOfNP2andVP = getMinDistance(tree, r._e2, r._r);
//		_maxDistOfNP2andVP = getMaxDistance(tree, r._e2, r._r);
		
		
		_minDistOfNP1andNP2 = Integer.parseInt(r._features.get(9));
		_maxDistOfNP1andNP2 = Integer.parseInt(r._features.get(10));
		
		_minDistOfNP1andVP = Integer.parseInt(r._features.get(11));
		_maxDistOfNP1andVP = Integer.parseInt(r._features.get(12));
		
		_minDistOfNP2andVP = Integer.parseInt(r._features.get(13));
		_maxDistOfNP2andVP = Integer.parseInt(r._features.get(14));		
		
		if (( _minDistOfNP1andNP2 > _minDist ) || 
			(_maxDistOfNP1andNP2 > _maxDist) || 
			(_maxDistOfNP1andNP2 - _minDistOfNP1andNP2 > _spanDist)	|| 
			(_maxDistOfNP1andVP - _minDistOfNP1andVP > _spanDist) ){
			//(_maxDistOfNP2andVP - _minDistOfNP2andVP > _spanDist) ) {
			return false;
		}else {
			return true;
		}		
	}
	
	private int getMinDistance(DependencyTree tree, Token[] e1, Token[] e2){
		int minDist = 99999;
		for(int i=0; i < e1.length; i++){
			for(int j=0; j < e2.length; j++){
				Token t1 = e1[i];
				Token t2 = e2[j];
				if(t1._position!=-1 && t2._position!=-1){
					int distance = tree.distance(t1, t2);
					//System.out.format("DEBUG t1= %s-%d ; t2 = %s-%d ; distance = %d%n", t1._token, t1._position, t2._token, t2._position, distance);
					if (minDist > distance) {
						minDist = distance;
					}
				}
			}		
		}
		return minDist;
	}
	
	private int getMaxDistance(DependencyTree tree, Token[] e1, Token[] e2){
		int maxDist = 0;
		for(int i=0; i < e1.length; i++){
			for(int j=0; j < e2.length; j++){
				Token t1 = e1[i];
				Token t2 = e2[j];
				if(t1._position!=-1 && t2._position!=-1){
					int distance = tree.distance(t1, t2);					
					if (maxDist < distance) {
						maxDist = distance;
					}					
				}
			}		
		}
		return maxDist;
	}

}
