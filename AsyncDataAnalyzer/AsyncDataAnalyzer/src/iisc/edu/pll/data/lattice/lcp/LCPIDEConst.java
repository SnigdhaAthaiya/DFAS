package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public class LCPIDEConst extends LCPIDEFunction {

	private String def;
	
	public LCPIDEConst(ArrayList<String> args) {
		super();
		def = "";
		if (args.size() < 2)
			return;

		// TODO add sanity checks later
		String varname = args.get(0);
		def=varname;
		String[] constTerm = args.get(1).split(",");
		int value = Integer.parseInt(constTerm[1].trim());
		VarPair pair = new VarPair(Globals.lambda, varname);

		LCPRep func = new LCPRep(0, value, new LCPValues(0, false, true));
		funcEdges.put(pair, func);
		ArrayList<String> vars = new ArrayList<>(Globals.DVars);

		vars.remove(varname); //remove the ID edge for the variable being written into

		for (String var : vars) {
			VarPair idpair = new VarPair(var, var);
			LCPRep func1 = new LCPRep(1, 0, new LCPValues(0, false, true)); // id
			funcEdges.put(idpair, func1);
		}

	}
	
	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return def;
	}


}
