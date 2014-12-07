package edu.cmu.cs.lti.relationFilter;
import java.util.*;

public class DependencyTree {
	private ArrayList<DependencyTree> _children = new ArrayList<DependencyTree>();
	private DependencyTree _parent=null;
	private String _type = null;
	private Token _token = null;
	private HashMap<String, DependencyTree> _entries = null;
	private int _depth=0;
	
	public static int NO_PATH = 1000000;
	
	public DependencyTree(String type, Token token, DependencyTree parent){
		_type = type;
		_token = token;
		_parent = parent;
	}
	
	public void addChild(DependencyTree child){
		_children.add(child);
	}
	
	private void setType(String type){
		if(_type!=null){
			System.err.println("ERROR DEPEND");
		}
		_type = type;
	}
	
	private void setParent(DependencyTree parent){
		if(_parent!=null){
			System.err.println("ERROR DEPEND");
		}
		_parent = parent;
	}
	
	private void setEntriesMap(HashMap<String, DependencyTree> map){
		_entries = map;
	}
	
	public DependencyTree parent(){
		return _parent;
	}
	
	public Token token(){
		return _token;
	}
	
	public String type(){
		return _type;
	}
	
	public DependencyTree findNode(Token t){
		return _entries.get(t.toString());
	}
	
	public int distance(Token t1, Token t2){
		DependencyTree node1 = findNode(t1);
		DependencyTree node2 = findNode(t2);
		
		if(node1!=null && node2!=null){
			return distance(node1,node2);
		}
		else{
			return NO_PATH;
		}
	}
	
	public int distance(DependencyTree node1, DependencyTree node2){
		DependencyTree anc = leastCommonAncestor(node1,node2);
		if(anc!=null){
			return(node1._depth - anc._depth + node2._depth - anc._depth);
		}
		else{
			return NO_PATH;
		}
	}
	
	public DependencyTree leastCommonAncestor(Token t1, Token t2){
		DependencyTree node1 = findNode(t1);
		DependencyTree node2 = findNode(t2);
		
		if(node1!=null && node2!=null){
			return leastCommonAncestor(node1,node2);
		}
		else{
			return null;
		}
	}
	
	public DependencyTree leastCommonAncestor(DependencyTree node1, DependencyTree node2){
		DependencyTree[] path1 = node1.pathFromRoot();
		DependencyTree[] path2 = node2.pathFromRoot();
		
		int leastDepth = Math.min(node1._depth, node2._depth);
		for(int depth = leastDepth; depth>=0; depth--){
			if(path1[depth]==path2[depth]){
				return path1[depth];
			}
		}
		return null;
	}
	
	public DependencyTree[] pathFromRoot(){
		DependencyTree[] result = new DependencyTree[_depth+1];
		
		DependencyTree current = this;
		
		while(current!=null){
			result[current._depth] = current;
			current = current.parent();
		}
		
		return result;
	}

	
	public static DependencyTree load(ArrayList<Dependency> deps){
		HashMap<String,DependencyTree> map = new HashMap<String,DependencyTree>();
		for(int i=0; i<deps.size(); i++){
			Dependency d = deps.get(i);
			
			String t1 = d.first().toString();
			DependencyTree dt1 = map.get(t1);
			if(dt1 == null){
				dt1 = new DependencyTree(null,d.first(),null);
				dt1.setEntriesMap(map);
				map.put(t1, dt1);				
			}
			
			String t2 = d.second().toString();
			DependencyTree dt2 = map.get(t2);
			if(dt2 == null){
				dt2 = new DependencyTree(null,d.second(),null);
				dt2.setEntriesMap(map);
				map.put(t2, dt2);
			}
			
			//System.out.println(dt1.token()+"\t"+dt2.token());
			dt1.addChild(dt2);			
			
			dt2.setParent(dt1);
			dt2.setType(d.type());
		}
		
		//find root
		DependencyTree root = null;
		
		Iterator<DependencyTree> it = map.values().iterator();
		while(it.hasNext()){
			DependencyTree current = it.next();
			if(current.parent()==null){
				root = current;
				root.populateDepth(1);
				break;
			}
		}
		//System.out.println("DEBUG dependency root: token = "+ root.token());
		return root;	
		
	}
	
	private void populateDepth(int depth){
		_depth = depth;
		for(int i=0; i<_children.size();i++){
			_children.get(i).populateDepth(depth+1);
		}
	}
	
}
