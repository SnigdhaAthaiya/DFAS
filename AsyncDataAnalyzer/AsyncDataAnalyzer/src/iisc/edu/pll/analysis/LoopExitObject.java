package iisc.edu.pll.analysis;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;

public class LoopExitObject {
	
	ModuleNode exitNode;
	long startNodeId ;
	
	
	public LoopExitObject(ModuleNode exitEdge, long startNodeId) {
		
		this.exitNode = exitEdge;
		this.startNodeId = startNodeId;
	}
	public ModuleNode getExitNode() {
		return exitNode;
	}
	public void setExitNode(ModuleNode exitEdge) {
		this.exitNode = exitEdge;
	}
	public long getStartNodeId() {
		return startNodeId;
	}
	public void setStartNodeId(long startNodeId) {
		this.startNodeId = startNodeId;
	}

}
