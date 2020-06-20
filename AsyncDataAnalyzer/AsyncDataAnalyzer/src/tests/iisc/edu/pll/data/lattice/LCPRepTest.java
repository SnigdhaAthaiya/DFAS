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

import java.util.HashMap;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPRepID;
import iisc.edu.pll.data.lattice.lcp.LCPValues;

public class LCPRepTest {

	// the composition of non-const to const will never happen as no edge goes
	// to lambda
	public static void main(String args[]) {
		LCPRep l1 = new LCPRep(1, 5, new LCPValues(0, false, true));
		LCPRep l2 = new LCPRep(0, 3, new LCPValues(0, false, true));

		System.out.println(l1.isGreater(l2));
		System.out.println(l2.isGreater(l1));

		LCPRep l3 = new LCPRep(1, 1, new LCPValues(0, false, true));

		System.out.println(l1.compose(l2));
		System.out.println(l2.compose(l1));

		System.out.println(l1.join(l2));
		
		

	}

}
