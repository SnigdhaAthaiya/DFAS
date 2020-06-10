package iisc.edu.pll.lattice.cp;

import java.util.ArrayList;

import iisc.edu.pll.data.Statement;

public class CPFunctionFactory {

	public static CPFunction createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.ASSIGN))
			return new CPAssignFunction(args);
		if(stType.equals(Statement.CONSTASSIGN))
			return new CPConstAssignFunction(args);
		if(stType.equals(Statement.EQUALSTRUE))
			return new CPSimpleEqualTrue(args);
		if(stType.equals(Statement.EQUALSFALSE))
			return new CPSimpleEqualFalse(args);
		if(stType.equals(Statement.ID))
			return new CPIDFunction();
		if(stType.equals(Statement.EQUALSSWITCHFALSE))
			return new CPSwitchDefault(args);
		if(stType.equals(Statement.GREATERTHANTRUE))
			return new CPSimpleGreaterThanTrue(args);
		if(stType.equals(Statement.GREATERTHANFALSE))
			return new CPSimpleGreaterThanFalse(args);
		return new CPAssignFunction(args);
	}

}
