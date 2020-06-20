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

import java.util.Collections;
import java.util.Vector;

public class DemandTest {
	
	public static void main(String args[])
	{
		int size = 5;
		int[] a1 = new int[size];
		int[] a2 = new int[size];
		
		System.out.println(a1.equals(a2));
		
		Vector<Integer> v1 = new Vector<>(size);
		Collections.fill(v1, new Integer(1));
		Vector<Integer> v2 = new Vector<>(size);
		Collections.fill(v2, new Integer(1));
		System.out.println(v1.equals(v2));
		System.out.println(v1.size());
		System.out.println(v2.size());
		
		System.out.println(v1);
		v1.addElement(1);
		System.out.println(v1);
		
	}

}
