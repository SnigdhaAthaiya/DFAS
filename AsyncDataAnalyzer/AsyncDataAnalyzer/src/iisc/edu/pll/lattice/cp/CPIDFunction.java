package iisc.edu.pll.lattice.cp;

import iisc.edu.pll.data.lattice.AbstractValue;

public class CPIDFunction extends CPFunction {

	public CPIDFunction(){
		
	}
	@Override
	public AbstractValue apply(AbstractValue v) {
		//ConstantPropagation v1 = (ConstantPropagation)v;
		return v;
	}
	
	@Override
	public String toString(){
		return "ID function";
	}
	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

}
