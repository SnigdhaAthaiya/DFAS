package tests.iisc.edu.pll.lattice.cp;

import java.util.ArrayList;
import java.util.HashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.AbstractValueFactory;
import iisc.edu.pll.lattice.cp.CPFunction;
import iisc.edu.pll.lattice.cp.CPFunctionFactory;
import iisc.edu.pll.lattice.cp.ConstantPropagation;

public class CPGreaterTest {

	public static void main(String[] args) {
		Globals.varNum = new HashMap<>();
		Globals.varNum.put("a",0);
		Globals.varNum.put("b",1);
		Globals.varNum.put("c",2);
		
		Globals.numberOfVariables = Globals.varNum.size();
		Globals.botVal = AbstractValueFactory.createAbstractValue("cp", false, false, 0);
		
		ArrayList<String> expr1 = new ArrayList<>();
		expr1.add("b");
		expr1.add("1,5");
		
		
		ArrayList<String> expr2 = new ArrayList<>();
		expr2.add("a");
		expr2.add("1,3");
		
		ArrayList<String> expr3 = new ArrayList<>();
		expr3.add("a");
		expr3.add("1,c");
		
		
		CPFunction func1 = CPFunctionFactory.createFunction(Statement.CONSTASSIGN, expr1);
		
		System.out.println(func1);
		CPFunction func2 = CPFunctionFactory.createFunction(Statement.CONSTASSIGN, expr2);
		System.out.println(func2);
		
		CPFunction func3 = CPFunctionFactory.createFunction(Statement.GREATERTHANTRUE, expr3);
		System.out.println(func3);
		
		
		ConstantPropagation val = (ConstantPropagation)AbstractValueFactory.createAbstractValue("cp", true, false,0);
		System.out.println(val);
		
		//val.setIsBot(false);
		//val.setIsTop(true);
		ConstantPropagation val1 =(ConstantPropagation) func1.apply(val);
		System.out.println(val1);

		ConstantPropagation val2 = (ConstantPropagation) func2.apply(val1);
		System.out.println(val2);
		ConstantPropagation val3 =(ConstantPropagation) func3.apply(val2);
		System.out.println(val3);







	}

}
