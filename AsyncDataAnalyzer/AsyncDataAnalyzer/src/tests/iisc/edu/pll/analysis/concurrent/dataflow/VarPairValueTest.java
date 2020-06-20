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
package tests.iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.HashSet;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;
import iisc.edu.pll.data.lattice.lcp.VarPairValue;

public class VarPairValueTest {
	
	public static void main(String args[])
	{
		VarPair p1 = new VarPair("a", "b");
		VarPair p2 = new VarPair("a", "b");
		
		LCPRep v1 = new LCPRep(0, 0, new LCPValues(0, false, true));
		LCPRep v2 = new LCPRep(0, 0, new LCPValues(0, false, true));
		
		VarPairValue pair1 = new VarPairValue(p1, v1);
		VarPairValue pair2 = new VarPairValue(p2, v2);
		
		System.out.println(p1.equals(p2));
		System.out.println(v1.equals(v2));
		System.out.println(pair1.equals(pair2));
		
		HashSet<VarPairValue> set = new HashSet<>();
		set.add(pair1);
		set.add(pair2);
		System.out.println(set.size());
	}

}
