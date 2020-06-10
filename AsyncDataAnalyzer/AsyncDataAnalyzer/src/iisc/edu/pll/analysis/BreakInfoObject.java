package iisc.edu.pll.analysis;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;

public class BreakInfoObject {
	
	ModuleEdge breakEdge;
	ModuleNode doStartNode;
	public ModuleEdge getBreakEdge() {
		return breakEdge;
	}
	public void setBreakEdge(ModuleEdge breakEdge) {
		this.breakEdge = breakEdge;
	}
	public ModuleNode getDoStartNode() {
		return doStartNode;
	}
	public void setDoStartNode(ModuleNode doStartNode) {
		this.doStartNode = doStartNode;
	}
	public BreakInfoObject(ModuleEdge breakEdge, ModuleNode doStartNode) {
		super();
		this.breakEdge = breakEdge;
		this.doStartNode = doStartNode;
	}

	
}
