package iisc.edu.pll.analysis.assertions;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.lattice.TFunction;

public class LCPAssertionCheck extends ConditionCheck{
	
	//called only when the node is not the result of merging variables

	public LCPAssertionCheck(VarUseInfo vuse) {
		varuse = vuse;
	
	}
	
	@Override
	public boolean check( TFunction val){
		if (val == null) {
			return false;
		}
		
		String[] uses = varuse.getVarUse().split(";");
		int totalUses =0;
		int numOfNonConst = 0;
		
		for (int i = 0; i < uses.length; i++) {
			String vuse = uses[i];
			
			String[] vuseArr = vuse.split(",");
			for (String var : vuseArr) {
				//System.out.println("checking for use :" + vuse);
				totalUses++;
				if (val.isNotConstantFor(var)) {					
					numOfNonConst++;
				}
				
			}

		}
		
		if(numOfNonConst > 0 )
			{//System.out.println("check failed");
			return true;
			}

	//	System.out.println("check passed");
		
		return false;
	}

}
