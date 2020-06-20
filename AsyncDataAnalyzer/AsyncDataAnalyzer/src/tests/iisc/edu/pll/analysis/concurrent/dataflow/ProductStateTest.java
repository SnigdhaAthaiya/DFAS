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

import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.data.ModuleNode;

public class ProductStateTest {

	public static void main(String[] args) {
		ModuleNode n1 = new ModuleNode();
		ModuleNode n2 = new ModuleNode();
		ModuleNode n3 = new ModuleNode();
		ModuleNode[] arr = {n1,n2};
		ModuleNode[] arr1 = {n1,n2};
		ProductState s1 = new ProductState(arr);
		ProductState s2 = new ProductState(arr1);
		System.out.println(s1.equals(s2));
		System.out.println(s2.equals(s1));
		System.out.println(s1.equals(s1));
		System.out.println(arr.hashCode());
		System.out.println(arr1.hashCode());

	}

}
