package edu.cmu.cs.lti.relationFilter;

/**
 * This class store all sentence information we get from the input file 
 * */

import java.io.IOException;
import java.io.StringReader;
import java.util.*;


import edu.stanford.nlp.trees.*;
import mark.chunking.Chunker;

public class SentenceData {
	
	private SentenceRawData _rawData;
	private String _chunked;

	private Tree _parseTree;
	private TreeGraphNode _parseTreeGraph;

	//private ArrayList<Tree> _parseTreeLeaves;
	private List<Tree> _parseTreeLeaves;

	private ArrayList<Relation> _relations;
	private ArrayList<Dependency> _dependencies;
	private ArrayList<Dependency> _typedDependencies;
	private DependencyTree _dependTree;	
	
	private int _minDistOfNP1andNP2 = 9999;
	private int _maxDistOfNP1andNP2 = 0;

	private int _minDistOfNP1andVP = 9999;
	private int _maxDistOfNP1andVP = 0;

	private int _minDistOfNP2andVP = 9999;
	private int _maxDistOfNP2andVP = 0;	
	
	public ArrayList <ArrayList<Token>> NP = new ArrayList <ArrayList<Token>> ();
	public ArrayList <ArrayList<Token>> VP = new ArrayList <ArrayList<Token>> ();	
	
	public SentenceData(SentenceRawData rawData, int verbosity){
		_rawData = rawData;		
		processRawData(verbosity);
	}
	
	public int size(){
		String data = _rawData._sentWithPOS;
		String[] toks = data.split(" ");
		return toks.length;
	}
	
	public String getRawSentenceString(){
		String data = _rawData._sentWithPOS;
		String[] toks = data.split(" ");
		String current = "";
		for(int k=0; k < toks.length; k++){
			int pos = toks[k].lastIndexOf("/");
			String token = toks[k].substring(0, pos);
			current += token + " "; 
		}
		return current;
	}
	
	public ArrayList <String> NPtoString (){		
		ArrayList<String> NPlist = new ArrayList<String> ();		
		for (int i = 0; i< NP.size(); i++){			
			String _np = "";
			for (int j=0; j< NP.get(i).size() - 1; j++){
				//System.out.println("DEBUG np = " + NP.get(i).get(j)._token);
				_np += NP.get(i).get(j).toString() + " ";				
			}
			_np += NP.get(i).get( NP.get(i).size() - 1  ).toString();
			NPlist.add(_np);			
		}
		return NPlist;
	}
	
	public ArrayList <String> VPtoString (){
		ArrayList<String> VPlist = new ArrayList<String> ();
		for (int i = 0; i< VP.size(); i++){
			String _vp = "";
			for (int j=0; j< VP.get(i).size() - 1; j++){
				_vp += VP.get(i).get(j).toString() + " ";				
			}
			_vp += VP.get(i).get( VP.get(i).size() - 1  ).toString();
			VPlist.add(_vp);
		}
		return VPlist;
	} 
	
	private void processRawData(int verbosity){
		chunkSentence();
		initParseTree();
		initRelations2(verbosity);
		initDependencies();
		initTypedDependencies();
	}
	
	private void initParseTree(){
		if(_rawData == null) return;
		
		try{
			_parseTree = Tree.valueOf(_rawData._parseTree);
			
			_parseTree.initParent();
			
			_parseTreeLeaves = _parseTree.getLeaves();

			/*_parseTreeLeaves = new ArrayList<Tree>();
			Iterator<Tree> it = l.iterator();
			while (it.hasNext()){
				_parseTreeLeaves.add(it.next());
			}*/

			
			//System.out.println("DEBUG\n");
			//_parseTree.pennPrint();		

			/*System.out.format("Size of leaves list = %d%n", l.size());
			for (int i = 0; i< l.size(); i++){
				System.out.println("Leaves = "+l.get(i).label().toString());
			}
			System.out.println("\nEND of DEBUG\n");*/
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private void initDependencies(){
		_dependencies = new ArrayList();
		if(_rawData._dependencyParse!=null){
			StringTokenizer t = new StringTokenizer(_rawData._dependencyParse,"\n");
			while(t.hasMoreElements()){			
				String a =  t.nextToken();
				//System.out.println("DEBUG read dependency " + a);
				_dependencies.add(new Dependency(a));
			}		
		}
		
		_dependTree = DependencyTree.load(_dependencies);
		
		//compute dependency features
		for(int i=0; i < _relations.size(); i++){
			Relation rel = _relations.get(i);
			ArrayList<String> depfeat =	getDependencyFeature(rel , _dependTree);
			rel._features.addAll(depfeat);
		}
	}	

	private void initTypedDependencies(){
		_typedDependencies = new ArrayList();
		if(_rawData._typedDependencyParse!=null){
			StringTokenizer t = new StringTokenizer(_rawData._typedDependencyParse,"\n");
			while(t.hasMoreElements()){			
				String a =  t.nextToken();
				//System.out.println("DEBUG read typed dependency " + a);
				_typedDependencies.add(new Dependency().initTypedDependency(a));
			}		
		}
	}	
	
	private void initRelations(){
		_relations = new ArrayList();
		if(_rawData._candidateRelations!=null){
			StringTokenizer t = new StringTokenizer(_rawData._candidateRelations,"\n");
			while(t.hasMoreElements()){
				_relations.add(new Relation(t.nextToken()));
			}		
		}
	}
	
	private void initRelations2(int verbosity){
		if(_chunked!=null){
			RelationGenerator rg = new RelationGenerator();
			_relations = rg.GenerateRelations(_chunked, verbosity);
			
			NP = new ArrayList <ArrayList<Token>> ();
			VP = new ArrayList <ArrayList<Token>> ();
			
			NP = rg.getBaseNP();
			VP = rg.getBaseVP();
		}
	}
	
	private ArrayList<String> getDependencyFeature(Relation r, DependencyTree tree) {		
		if(tree==null){	return null;	}     
		
		ArrayList<String> f = new ArrayList<String>();
		
		_minDistOfNP1andNP2 = getMinDistance(tree, r._e1, r._e2);
		_maxDistOfNP1andNP2 = getMaxDistance(tree, r._e1, r._e2);
		
		_minDistOfNP1andVP = getMinDistance(tree, r._e1, r._r);
		_maxDistOfNP1andVP = getMaxDistance(tree, r._e1, r._r);
		
		_minDistOfNP2andVP = getMinDistance(tree, r._e2, r._r);
		_maxDistOfNP2andVP = getMaxDistance(tree, r._e2, r._r);		
		
		if (r.e2toString().equals("-10000")){
			_minDistOfNP1andNP2 = 0;
			_maxDistOfNP1andNP2 = 0;
			_minDistOfNP2andVP  = 0;
			_maxDistOfNP2andVP  = 0;
		}
		
		f.add( Integer.toString(_minDistOfNP1andNP2)  );
		f.add( Integer.toString(_maxDistOfNP1andNP2)  );
		
		f.add( Integer.toString(_minDistOfNP1andVP)  );
		f.add( Integer.toString(_maxDistOfNP1andVP)  );
		
		f.add( Integer.toString(_minDistOfNP2andVP)  );
		f.add( Integer.toString(_maxDistOfNP2andVP)  );
		
		return f;
	}
	
	private int getMinDistance(DependencyTree tree, Token[] e1, Token[] e2){
		int minDist = 9999;
		for(int i=0; i < e1.length; i++){
			for(int j=0; j < e2.length; j++){
				Token t1 = e1[i];
				Token t2 = e2[j];
				if(t1._position!=-1 && t2._position!=-1){
					int distance = tree.distance(t1, t2);
					//System.out.format("DEBUG t1= %s-%d ; t2 = %s-%d ; distance = %d%n", t1._token, t1._position, t2._token, t2._position, distance);
					if (distance == 1000000){						
						return -1;
					}else if (minDist > distance){
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
					if (distance == 1000000){
						return -1;
					} else if (maxDist < distance) {
						maxDist = distance;
					}					
				}
			}		
		}
		return maxDist;
	}

	private void chunkSentence(){
		String data = _rawData._sentWithPOS;
		data = data.substring(0,data.length()-1);
		_chunked = Chunker.instance().processSentence(data);
	}

	public String toString(){
		StringBuffer result = new StringBuffer();
		if(_rawData!=null){
			if(_rawData._sentWithPOS!=null)
				result.append(_rawData._sentWithPOS);
			result.append("\n");
			/*
			result.append(_chunked);
			result.append("\n");*/
			if(_rawData._parseTree!=null)
				result.append(_rawData._parseTree);
			result.append("\n");
			if(_rawData._dependencyParse!=null)
				result.append(_rawData._dependencyParse);
			result.append("\n");
			
			//output relations
			int relCount = _relations.size();
			for(int i=0; i<relCount; i++){
				//if(_relations.get(i)._label){
					result.append(_relations.get(i).toString());
					result.append("\n");
				//}
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	public Relation relationAt(int index){
		if(index<_relations.size()){
			return (Relation)_relations.get(index);
		}
		else{
			return null;
		}
	}
	
	public int relationCount(){
		return _relations.size();
	}
	
	public Tree leafAt(int index){
		//System.out.format("DEBUG SentenceData::leafAt  _parseTreeLeaves.size = %d%n", _parseTreeLeaves.size());

		index = index-1;

		if(index<_parseTreeLeaves.size()){
			return _parseTreeLeaves.get(index);
		}else{
			return null;
		}
	}
	public String POSTagAt(int index){
		index = index - 1;

		List POStag = _parseTree.preTerminalYield();

		if ( index < POStag.size()){
			return POStag.get(index).toString();
		}else{
			return null;
		}
	}

	public DependencyTree dependTree(){
		return _dependTree;
	}
	
	public ArrayList<Dependency> getTypedDependencies(){
		return _typedDependencies;
	}

}
