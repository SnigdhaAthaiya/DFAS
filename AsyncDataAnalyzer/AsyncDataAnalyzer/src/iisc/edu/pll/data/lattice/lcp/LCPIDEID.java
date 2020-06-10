package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import iisc.edu.pll.analysis.Globals;

public class LCPIDEID extends LCPIDEFunction {
	
	
	public LCPIDEID(ArrayList<String> params)
	{
		super();
		
		//ID will have no edges
	/*	for(String var : Globals.DVars)
		{
			VarPair pair= new VarPair(var, var);
			LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
			funcEdges.addElement(new VarPairValue(pair, value));
		}
		*/
	}

	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return new HashSet<String>();
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return "";
	}

}
