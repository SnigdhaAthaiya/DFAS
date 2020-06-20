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

import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;

public class LCPRepVarPairTest {

	public static void main(String[] args) {
		VarPair p1 = new VarPair("a", "b");
		VarPair p2 = new VarPair("a", "b");
		
		LCPRep val = new LCPRep(0, 3, new LCPValues(0, false, true));
		
		ConcurrentHashMap<VarPair, LCPRep> map  = new ConcurrentHashMap<VarPair, LCPRep>();
		
		map.put(p1, val);
		
		System.out.println(map.get(p2));

	}

}
