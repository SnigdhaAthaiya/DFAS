package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;

public class LCPIDEFunctionFactory {
	
	public static LCPIDEFunction createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new LCPIDEConst(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new LCPIDEAssign(args);
		if(stType.equals(Statement.ID))
			return new LCPIDEID(args);
		if(stType.equals(Statement.EMPTY))
			return new LCPEmpty();
		if(stType.equals("test13"))
			return get13();
		
		return new LCPIDEID(args);
	}

	private static LCPIDEFunction get13() {
		LCPIDEID fun = new LCPIDEID(new ArrayList<>());
		for(String var : Globals.DVars)
		{
			fun.funcEdges.put(new VarPair(var, var), new LCPRep(1,0, new LCPValues(0, false, true)));
			fun.funcEdges.put(new VarPair(Globals.lambda, var), new LCPRep(1,0, new LCPValues(0, false, true)));
		}
		
		System.out.println("added edges: "+ fun.funcEdges.size() );
		return fun;
	}

}
