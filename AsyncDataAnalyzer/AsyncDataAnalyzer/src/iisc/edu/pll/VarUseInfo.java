package iisc.edu.pll;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import iisc.edu.pll.data.ModuleNode;

public class VarUseInfo {
	
	ModuleNode node;
	int modIndex;
	String varUse;
	String label;
	
	
	public VarUseInfo(ModuleNode node, int modIndex, String varUse, String label) {
		super();
		this.node = node;
		this.modIndex = modIndex;
		this.varUse = varUse;
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public ModuleNode getNode() {
		return node;
	}
	public void setNode(ModuleNode node) {
		this.node = node;
	}
	public int getModIndex() {
		return modIndex;
	}
	public void setModIndex(int modIndex) {
		this.modIndex = modIndex;
	}
	public String getVarUse() {
		return varUse;
	}
	public void setVarUse(String varUse) {
		this.varUse = varUse;
	}
	
	@Override
	public String toString(){
		return ("[VarUseInfo :"+ label+ ", "  + modIndex+ " , " + varUse +" ]");
	}
	
	@Override
	public int hashCode(){
		return (node.hashCode()+ modIndex + varUse.length() + label.length()+ 23);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj==this)
			return true;
		
		if(obj==null || !(obj instanceof VarUseInfo))
			return false;
		
		VarUseInfo v = (VarUseInfo)obj;
		
		if(node.equals(v.node) && modIndex == v.modIndex && varUse.equals(v.varUse) && label.equals(v.label))
			return true;
		
		return false;
					
					
	}
	

}
