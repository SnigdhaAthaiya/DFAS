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
package tests.iisc.edu.pll.data.lattice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.LCPIDEAssign;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunction;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.LCPIDEID;

public class LCPIDEFunctionTest {

	public static void main(String[] args) {
		ArrayList<String> vars = new ArrayList<>();
		vars.add("a");
		
		Globals.DVars = new ArrayList<>();
		Globals.DVars.add(Globals.lambda);
		Globals.DVars.addAll(vars);
		
		ArrayList<String> arguments = new ArrayList<>();
		arguments.add("a");
		arguments.add("1,0");
		
		
		ArrayList<String> arguments2 = new ArrayList<>();
		arguments2.add("a");
		arguments2.add("-1,a");
		arguments2.add("+");
		arguments2.add("1,1");
		
		ArrayList<String> arguments3 = new ArrayList<>();
		arguments3.add("a");
		arguments3.add("1,3");
		
		LCPIDEFunction f1 = LCPIDEFunctionFactory.createFunction(Statement.CONSTASSIGN, arguments);
		LCPIDEFunction f2 = LCPIDEFunctionFactory.createFunction(Statement.ASSIGN, arguments2);
	
		
		System.out.println(f1.compose(f2));
		System.out.println(f2.compose(f1));
		
		
		
		
		
		
		
		
		
		
		LCPIDEFunction f3 = LCPIDEFunctionFactory.createFunction(Statement.CONSTASSIGN, arguments3);
		
		LCPIDEFunction f4 = LCPIDEFunctionFactory.createFunction(Statement.ID, new ArrayList<>());
		LCPIDEFunction f5 = LCPIDEFunctionFactory.createFunction(Statement.ID, new ArrayList<>());
		
		
		
		System.out.println(f1);
		//System.out.println(f2);
	//	System.out.println(f3);
	
		TFunction f = f2.join(f3);
		System.out.println("f2 join f3 :" + f);
		
		System.out.println(f.isGreater(f2));
		
		System.out.println(f2.isGreater(f));
		f =  f1.compose(f);
		System.out.println("f1.(f2 join f3) : " + f);
		
		System.out.println(f1.isConstantFor("a"));
		
		
		
		
	}
	
	

}
