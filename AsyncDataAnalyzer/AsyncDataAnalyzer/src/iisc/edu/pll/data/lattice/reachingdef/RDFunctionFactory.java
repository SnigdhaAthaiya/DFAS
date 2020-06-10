package iisc.edu.pll.data.lattice.reachingdef;

import java.util.ArrayList;

import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.lcp.LCPEmpty;
import iisc.edu.pll.data.lattice.lcp.LCPIDEAssign;
import iisc.edu.pll.data.lattice.lcp.LCPIDEConst;
import iisc.edu.pll.data.lattice.lcp.LCPIDEID;

public class RDFunctionFactory {
	
	public static TFunction createFunction(String stType,ArrayList<String> args)
	{
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new RDConstAssign(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new RDAssign(args);
		if(stType.equals(Statement.ID))
			return new RDIDFunction(args);
		if(stType.equals(Statement.EMPTY)) //this is called only in the context of values
			return new RDValue(); //new RD Value with empty sets
		
		return new RDIDFunction(args);

	}

}
