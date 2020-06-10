package iisc.edu.pll.analysis.assertions;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat;

public class ARAAssertCheck extends ConditionCheck {
	
	
	
	//capable of checking assertion, but fucntionality not implemented yet
		public ARAAssertCheck(VarUseInfo vuse) {
			varuse = vuse;
			
		}
		
		@Override
		public boolean check( TFunction val){
			if (val == null ) {
				return false;
			}
			
			
			String[] labels = varuse.getLabel().split(";");
			int totalAsserts =0;
			int numOfAssertFails = 0;
			
			
			
			for (int i = 0; i < labels.length; i++) {
				String label = labels[i];
				if(Globals.labelVectorsHashMap.containsKey(label))
				{
					totalAsserts++;					
					if(!ARAIDEFunctionShortMat.resultCheck(val, Globals.labelVectorsHashMap.get(label))){
						numOfAssertFails++;
						//System.out.println("check failed");
					}
				}

			}
			
			if(totalAsserts>0 &&  numOfAssertFails>0 )
				return true;
			
			//System.out.println("check passed");
			return false;

			
		
		}
		


}
