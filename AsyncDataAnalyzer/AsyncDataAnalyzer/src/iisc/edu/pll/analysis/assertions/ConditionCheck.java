package iisc.edu.pll.analysis.assertions;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.lattice.TFunction;

public abstract class ConditionCheck {
	
	VarUseInfo varuse;
	
	public ConditionCheck(){
		varuse = null;
	}
	
	public ConditionCheck(VarUseInfo vuse){
		varuse = vuse;
	}
	
	public VarUseInfo getVaruse() {
		return varuse;
	}

	public void setVaruse(VarUseInfo varuse) {
		this.varuse = varuse;
	}

	
	//receives the function value of the initial node and zero vector and checks the assertion
	public boolean check(TFunction val){
		return false;
	}
	

}
