package tests.iisc.edu.pll.lattice.cp;

import java.util.ArrayList;
import java.util.HashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.AbstractValueFactory;
import iisc.edu.pll.lattice.cp.CPFunction;
import iisc.edu.pll.lattice.cp.CPFunctionFactory;
import iisc.edu.pll.lattice.cp.ConstantPropagation;

public class CPAssignTest {

	public static void main(String[] args) {
		Globals.varNum = new HashMap<>();
		Globals.varNum.put("a",0);
		Globals.varNum.put("b",1);
		Globals.varNum.put("c",2);
		
		Globals.numberOfVariables = Globals.varNum.size();
		
		ArrayList<String> expr = new ArrayList<>();
		expr.add("a");
		expr.add("2,b");
		expr.add("+");
		expr.add("-1,-1");
		
		ArrayList<String> expr1 = new ArrayList<>();
		expr1.add("b");
		expr1.add("2,1");
		
		CPFunction func = CPFunctionFactory.createFunction(Statement.ASSIGN, expr);
		System.out.println(func);
		
		CPFunction func2 = CPFunctionFactory.createFunction(Statement.CONSTASSIGN, expr1);
		System.out.println(func2);
		
		
		ConstantPropagation val = (ConstantPropagation)AbstractValueFactory.createAbstractValue("cp", true, false,0);
		System.out.println(val);
		
		//val.setIsBot(false);
		//val.setIsTop(true);
		ConstantPropagation val2 =(ConstantPropagation) func2.apply(val);
		System.out.println(val2);

		ConstantPropagation val3 =(ConstantPropagation) func.apply(val2);
		System.out.println(val3);




	}

}
