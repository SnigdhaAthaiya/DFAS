package iisc.edu.pll.lattice.cp;

public class VarValPair {

	String var;
	ConstantPropagationSingle value;
	public VarValPair(String var, ConstantPropagationSingle value) {
		super();
		this.var = var;
		this.value = value;
	}
	public String getVar() {
		return var;
	}
	public void setVar(String var) {
		this.var = var;
	}
	public ConstantPropagationSingle getValue() {
		return value;
	}
	public void setValue(ConstantPropagationSingle value) {
		this.value = value;
	}
	
	
}
