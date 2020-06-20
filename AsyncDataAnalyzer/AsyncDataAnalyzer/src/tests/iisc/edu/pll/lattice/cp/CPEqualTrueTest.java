/*******************************************************************************
 * Copyright (C) 2020 Snigdha Athaiya
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package tests.iisc.edu.pll.lattice.cp;

import java.util.ArrayList;
import java.util.HashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.AbstractValueFactory;
import iisc.edu.pll.lattice.cp.CPFunction;
import iisc.edu.pll.lattice.cp.CPFunctionFactory;
import iisc.edu.pll.lattice.cp.ConstantPropagation;

public class CPEqualTrueTest {

	public static void main(String[] args) {
		Globals.varNum = new HashMap<>();
		Globals.varNum.put("a",0);
		Globals.varNum.put("b",1);
		Globals.varNum.put("c",2);
		
		Globals.numberOfVariables = Globals.varNum.size();
		Globals.botVal = AbstractValueFactory.createAbstractValue("cp", false, false, 0);
		
		ArrayList<String> expr = new ArrayList<>();
		expr.add("c");
		expr.add("1,b");
		
		
		ArrayList<String> expr1 = new ArrayList<>();
		expr1.add("a");
		expr1.add("4,2");
		
		ArrayList<String> expr2 = new ArrayList<>();
		expr2.add("b");
		expr2.add("1,2");
		
		CPFunction func = CPFunctionFactory.createFunction(Statement.EQUALSTRUE, expr);
		System.out.println(func);
		
		CPFunction func1 = CPFunctionFactory.createFunction(Statement.CONSTASSIGN, expr1);
		
		System.out.println(func1);
		CPFunction func2 = CPFunctionFactory.createFunction(Statement.CONSTASSIGN, expr2);
		System.out.println(func2);
		
		
		ConstantPropagation val = (ConstantPropagation)AbstractValueFactory.createAbstractValue("cp", true, false,0);
		System.out.println(val);
		
		//val.setIsBot(false);
		//val.setIsTop(true);
		ConstantPropagation val1 =(ConstantPropagation) func1.apply(val);
		System.out.println(val1);

		ConstantPropagation val2 = (ConstantPropagation) func2.apply(val1);
		System.out.println(val2);
		ConstantPropagation val3 =(ConstantPropagation) func.apply(val2);
		System.out.println(val3);




	}

}
