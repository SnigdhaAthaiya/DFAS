package iisc.edu.pll.analysis.assertions;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat;

public class ARANoCheck extends ConditionCheck {
	

	
	//capable of checking assertion, but fucntionality not implemented yet
		public ARANoCheck(VarUseInfo vuse) {
			varuse = vuse;
			
		}
		
		@Override
		public boolean check( TFunction val){
			return false;
			
		
		}

}
