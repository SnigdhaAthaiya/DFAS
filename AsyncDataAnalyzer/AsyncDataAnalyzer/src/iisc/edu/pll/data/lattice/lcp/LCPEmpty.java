package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import iisc.edu.pll.analysis.Globals;

public class LCPEmpty extends LCPIDEFunction {

	public LCPEmpty() {
		super();
		VarPair pair = new VarPair(Globals.lambda, Globals.lambda);
		LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true)); //ID function
		funcEdges.put(pair, value);		
	}

	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return "";
	}

}
