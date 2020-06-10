package iisc.edu.pll.analysis.assertions;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.reachingdef.RDValue;

public class RDCheck extends ConditionCheck {

	
	public RDCheck(VarUseInfo vuse){
		varuse = vuse;
	}
	@Override
	public boolean check(TFunction val) {
		RDValue v = (RDValue) val;
		if(v.isNotConstant())
			return true;
		else
			return false;
		
	}
}
