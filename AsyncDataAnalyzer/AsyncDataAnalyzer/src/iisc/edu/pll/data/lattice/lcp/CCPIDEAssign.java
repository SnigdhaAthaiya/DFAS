package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public class CCPIDEAssign extends LCPIDEFunction {

private String def="";
	
	private HashSet<String> uses ;
	/**
	 * The order of arguments is as follows for the statement a = b+ c, where c
	 * is a const args[0] = a args[1] = b args[2] = op args[3] = const c
	 * 
	 * for assignment a = c args[0] = a args[1] = c
	 */
	public CCPIDEAssign(ArrayList<String> args) {
		super();

		if (args.size() < 2)
			return;

		def="";
		
		 uses = new HashSet<>();
		String varname = args.get(0);
		
		def = varname;
		
		if (args.size() == 2) {
			String[] rhs = args.get(1).split(",");
			String coeff = rhs[0];
			String rhsVar = rhs[1];

			VarPair pair = new VarPair(rhsVar, varname);
			uses.add(rhsVar);
			LCPRep func = new LCPRep(Integer.parseInt(coeff), 0, new LCPValues(0, false, true));
			funcEdges.put(pair, func);

			ArrayList<String> vars = new ArrayList<>(Globals.DVars);

		//	if(Globals.filename.contains("receive1")){
				vars.remove(varname);
		//	}
		//	else{
		//		if (varname.equals(rhsVar))
		//			vars.remove(varname);
		//	}
			

			for (String var : vars) {
				VarPair idpair = new VarPair(var, var);
				LCPRep func1 = new LCPRep(1, 0, new LCPValues(0, false, true)); // id
				funcEdges.put(idpair, func1);
			}
		} else {
			
			//havoc it based on the variable
			String[] rhsOperand1 = args.get(1).split(",");
			String source = rhsOperand1[1];
			VarPair pair = new VarPair(source, varname);

			uses.add(source);
			LCPRep func = new LCPRep(1, 0, new LCPValues(0, true, false)); //make it bot
			funcEdges.put(pair, func);

			ArrayList<String> vars = new ArrayList<>(Globals.DVars);

			vars.remove(varname);

			for (String var : vars) {
				VarPair idpair = new VarPair(var, var);
				LCPRep func1 = new LCPRep(1, 0, new LCPValues(0, false, true)); // id
				funcEdges.put(idpair, func1);
			}
		}
		// TODO add sanity checks later

	}
	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return uses;
	}
	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return def;
	}
}
