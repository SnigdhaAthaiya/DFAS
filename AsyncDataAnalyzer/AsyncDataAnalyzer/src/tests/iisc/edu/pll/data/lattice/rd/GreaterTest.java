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
import iisc.edu.pll.data.lattice.reachingdef.OtherRHS;
import iisc.edu.pll.data.lattice.reachingdef.RDFunctionFactory;
import iisc.edu.pll.data.lattice.reachingdef.RDSingle;
import iisc.edu.pll.data.lattice.reachingdef.RDValue;
import iisc.edu.pll.data.lattice.reachingdef.VarRHS;

public class GreaterTest {

	public static void main(String[] args) {
		Globals.rdmap = new HashMap<>();
		Globals.rdmap.put("a", 0);
		//Globals.rdmap.put("b", 1);
		Globals.numberOfQueryVar = 1;
		Globals.other = new RDSingle("dummy", new OtherRHS());

	//	RDSingle v1 = new RDSingle("a", new VarRHS("b"));
		RDSingle v1 = new RDSingle("a", new OtherRHS());
		LinkedList<RDSingle> st1 = new LinkedList<>();
		st1.add(v1);
		LinkedList<RDSingle>[] val1 = new LinkedList[1];
		val1[0] = st1;
		RDValue rval1 = new RDValue(val1);

		System.out.println(v1);
		System.out.println(rval1);
		
		//RDSingle v2 = new RDSingle("a", new VarRHS("b"));
		RDSingle v2 = new RDSingle("a", new OtherRHS());
		LinkedList<RDSingle> st2= new LinkedList<>();
		st2.add(v2);
		LinkedList<RDSingle>[] val2 = new LinkedList[1];
		val2[0] = st2;
		RDValue rval2 = new RDValue(val2);
		
		System.out.println(v2);
		System.out.println(rval2);
		
		System.out.println(rval2.isGreater(rval1));
		

	}

}
