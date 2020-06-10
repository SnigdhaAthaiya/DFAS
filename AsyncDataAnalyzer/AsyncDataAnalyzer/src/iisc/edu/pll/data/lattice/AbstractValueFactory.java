package iisc.edu.pll.data.lattice;

import java.util.ArrayList;

import iisc.edu.pll.lattice.cp.ConstantPropagation;


public class AbstractValueFactory {
	
	public static AbstractValue createAbstractValue(String type, boolean isTop, boolean isConst, int val)
	{
		if(type.equalsIgnoreCase("cp")){
			return new ConstantPropagation(isTop, isConst, val);
		}
		return null;
	}

}
