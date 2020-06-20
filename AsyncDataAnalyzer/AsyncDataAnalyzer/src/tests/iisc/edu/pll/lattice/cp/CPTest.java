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
