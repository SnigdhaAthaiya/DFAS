package tests.iisc.edu.pll.lattice.cp;

import java.util.ArrayList;
import java.util.HashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.lattice.cp.CPFunction;
import iisc.edu.pll.lattice.cp.CPFunctionFactory;

public class CPTest {

	public static void main(String[] args) {
		Globals.varNum = new HashMap<>();
		Globals.varNum.put("a",0);
		Globals.varNum.put("b",1);
		Globals.varNum.put("c",2);
		
		Globals.numberOfVariables = Globals.varNum.size();
		
		ArrayList<String> expr = new ArrayList<>();
		expr.add("a");
		expr.add("-2,b");
		expr.add("+");
		expr.add("-2,-2");
		
		CPFunction func = CPFunctionFactory.createFunction(Statement.ASSIGN, expr);
		System.out.println(func);

	}

}
