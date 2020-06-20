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
package tests.iisc.edu.pll.data.lattice.rd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.reachingdef.RDFunctionFactory;
import iisc.edu.pll.data.lattice.reachingdef.RDSingle;
import iisc.edu.pll.data.lattice.reachingdef.RDValue;
import iisc.edu.pll.data.lattice.reachingdef.VarRHS;

public class RDConstTest {

	public static void main(String[] args) {
		Globals.rdmap = new HashMap<>();
		Globals.rdmap.put("a",1);
		Globals.rdmap.put("b",2);
		Globals.numberOfQueryVar = 1;
		
		RDSingle v1 = new RDSingle("a", new VarRHS("b"));
		LinkedList<RDSingle> st= new LinkedList<>();
		st.add(v1);
		LinkedList<RDSingle>[] val = new LinkedList[1];
		val[0] = st;
		RDValue rval = new RDValue(val);
		
		System.out.println(v1);
		System.out.println(rval);
		
		ArrayList<String> expr1 =  new ArrayList<>();
		expr1.add("b");
		expr1.add("5");
		//expr1.add("+");
		//expr1.add("1");
		TFunction f1 = RDFunctionFactory.createFunction(Statement.ASSIGN, expr1);
		
		ArrayList<String> expr2 =  new ArrayList<>();
		expr2.add("b");
		expr2.add("5");
		//expr2.add("+");
		//expr2.add("1");
		TFunction f2 = RDFunctionFactory.createFunction(Statement.ASSIGN, expr2);
		
		
		System.out.println(f1);
		
		System.out.println(f2);
		
		TFunction f3 =  f1.compose(rval);
		TFunction f4 =  f2.compose(rval);
		
		System.out.println("result: "+f3);
		System.out.println("result:" + f4);
		System.out.println("join : " + f3.join(f4));
		
		

	}

}
