package iisc.edu.pll.analysis.assertions;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.lattice.TFunction;

public class LCPConstantCheck extends ConditionCheck{
	
	
	
	
	public LCPConstantCheck(VarUseInfo vuse) {
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
				totalUses++;
				if (val.isNotConstantFor(var)) {					
					numOfNonConst++;
				}
				
			}

		}
		
		if(numOfNonConst == totalUses )
			{
			//System.out.println("check failed");
			return true;}

		
	//	System.out.println("check passed");
		return false;
	}
	

}
