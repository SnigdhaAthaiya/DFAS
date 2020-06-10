package iisc.edu.pll.analysis.assertions;



import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.analysis.assertions.LCPConstantCheck;

public class ConditionCheckFactory {
	
	//assertion matrix for ara
	public static ConditionCheck createCheck(VarUseInfo vuse, String type, boolean isAssert){
		 if(type.equalsIgnoreCase("lcp") && !isAssert)
		 {
			 return new LCPConstantCheck(vuse);
			 
		 }
		 if(type.equalsIgnoreCase("lcp") && isAssert)
		 {
			 return new LCPAssertionCheck(vuse);
			 
		 }
		 if(type.equalsIgnoreCase("ara"))
		 {
			 return new ARAAssertCheck(vuse);
		 } 
		 if(type.equalsIgnoreCase("rd"))
		 {
			 return new RDCheck(vuse);
			 
		 }
		
			 
		 
		 return null; 
		
	}

}
