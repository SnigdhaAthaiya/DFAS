package iisc.edu.pll.data;

public class NodePair {
	
	ModuleNode source;
	ModuleNode target;
	
	
	
	public NodePair(ModuleNode source, ModuleNode target) {
		this.source = source;
		this.target = target;
	}
	public ModuleNode getSource() {
		return source;
	}
	public void setSource(ModuleNode source) {
		this.source = source;
	}
	public ModuleNode getTarget() {
		return target;
	}
	public void setTarget(ModuleNode target) {
		this.target = target;
	}
	@Override
	public String toString() {
		return "NodePair [source=" + source + ", target=" + target + "]";
	}

}
