package iisc.edu.pll.data.lattice;

import java.util.ArrayList;

import iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionFactoryShortOb;
import iisc.edu.pll.data.lattice.lcp.CCPIDEFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunctionFactory;
import iisc.edu.pll.data.lattice.reachingdef.RDFunctionFactory;
import iisc.edu.pll.lattice.cp.CPFunctionFactory;

public class TFunctionFactory {
	
	
	public static TFunction createFunction(String type, String stType, ArrayList<String> vars, ArrayList<String> args){
		 if(type.equalsIgnoreCase("lcp"))
		 {
			 return LCPIDEFunctionFactory.createFunction(stType, args);
			 
		 }
			 
		 if(type.equalsIgnoreCase("ara"))
		 {
			 return ARAIDEFunctionFactoryShortOb.createFunction(stType, args);
			 
		 }
		 
		 if(type.equalsIgnoreCase("cp"))
		 {
			 return CPFunctionFactory.createFunction(stType, args);
		 }
		 
		 if(type.equalsIgnoreCase("rd"))
		 {
			 return RDFunctionFactory.createFunction(stType, args);
		 }
		 
		 if(type.equalsIgnoreCase("ccp"))
		 {
			 return CCPIDEFunctionFactory.createFunction(stType, args);
		 }
		 return null;
		
	}

}
