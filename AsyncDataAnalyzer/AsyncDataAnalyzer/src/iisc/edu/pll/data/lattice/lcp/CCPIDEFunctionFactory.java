package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;

import iisc.edu.pll.data.Statement;

public class CCPIDEFunctionFactory {
	
	public static LCPIDEFunction createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new LCPIDEConst(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new CCPIDEAssign(args);
		if(stType.equals(Statement.ID))
			return new LCPIDEID(args);
		if(stType.equals(Statement.EMPTY))
			return new LCPEmpty();
		
		return new LCPIDEID(args);
	}



}
