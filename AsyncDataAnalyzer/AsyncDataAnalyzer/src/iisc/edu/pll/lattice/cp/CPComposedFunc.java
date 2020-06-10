package iisc.edu.pll.lattice.cp;

import java.util.ArrayList;

import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.AbstractValueFactory;

public class CPComposedFunc extends CPFunction {

	ArrayList<CPFunction> compFunc;
	public CPComposedFunc(ArrayList<CPFunction> compFunc) {
		super();
		this.compFunc = compFunc;
	}

	public ArrayList<CPFunction> getCompFunc() {
		return compFunc;
	}

	public void setCompFunc(ArrayList<CPFunction> compFunc) {
		this.compFunc = compFunc;
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		AbstractValue result = new ConstantPropagation((ConstantPropagation)v);
		for(CPFunction func :  compFunc)
		{
			result = func.apply(result);
		}
		return result;
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		boolean isID = true;
		for(CPFunction func : compFunc)
			isID = isID && func.isID();
		return isID;
	}

	@Override
	public String toString() {
		return "CPComposedFunc [compFunc=" + compFunc + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compFunc == null) ? 0 : compFunc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CPComposedFunc other = (CPComposedFunc) obj;
		if (compFunc == null) {
			if (other.compFunc != null)
				return false;
		} else if (!compFunc.equals(other.compFunc))
			return false;
		return true;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
