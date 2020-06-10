package iisc.edu.pll.data;

import java.util.HashSet;

public class ModuleNode {

	private static long GlobalId = 0;
	private long id;
	HashSet<ModuleEdge> incoming;
	HashSet<ModuleEdge> outgoing;
	
	
	

	public ModuleNode() {
		id = GlobalId++;	
		incoming = new HashSet<>();
		outgoing = new HashSet<>();
	}
	
	public ModuleNode(ModuleNode node){
		id = node.id;
		incoming = new HashSet<>();
		outgoing = new HashSet<>();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	@Override
	public String toString() {
		
		return ("id : " + id); 
	}
	
	
	

	public HashSet<ModuleEdge> getIncoming() {
		return incoming;
	}


	public void setIncoming(HashSet<ModuleEdge> incoming) {
		this.incoming = incoming;
	}


	public HashSet<ModuleEdge> getOutgoing() {
		return outgoing;
	}


	public void setOutgoing(HashSet<ModuleEdge> outgoing) {
		this.outgoing = outgoing;
	}


	@Override
	public int hashCode() {
		int seed = 17;
		int result = 3 * (seed + (int)id);
		return  result;
	}


	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		
		if(obj==null || !(obj instanceof ModuleNode))
			return false;
		
		
		ModuleNode n = (ModuleNode) obj;
		return (id == n.id);
	}
	
	

}
