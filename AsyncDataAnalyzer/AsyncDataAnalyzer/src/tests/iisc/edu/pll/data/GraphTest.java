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
package tests.iisc.edu.pll.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class GraphTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		ModuleNode n1 = new ModuleNode();
		ModuleNode n2 = new ModuleNode();
		ModuleGraph g = new ModuleGraph(n1, n2);
		
		HashSet<ModuleEdge> set = new HashSet<>();
		
		ArrayList<String> vars = new ArrayList<>();
		vars.add("a");
		ModuleEdge e1 = new ModuleEdge(n1, n2, TFunctionFactory.createFunction("lcp", Statement.ID, vars, new ArrayList<>()),
				new HashMap<>());
		
		ModuleEdge e2 = new ModuleEdge(n1, n2, TFunctionFactory.createFunction("lcp", Statement.ID, vars, new ArrayList<>()),
				new HashMap<>());
		g.addEdge(e1);

		set.add(e1);
		System.out.println(set.contains(e2));

		g.addEdge(e2);
		
		System.out.println(e1.equals(e2));
		g.printGraph();
	}

}
