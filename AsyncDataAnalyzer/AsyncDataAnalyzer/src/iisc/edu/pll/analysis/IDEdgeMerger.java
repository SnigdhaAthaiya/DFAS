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
package iisc.edu.pll.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;

public class IDEdgeMerger {

	public IDEdgeMerger() {

	}

	public ModuleGraph mergeIDEdges(ModuleGraph graph, Set<ModuleNode> leaders, HashMap<ModuleNode, String> varUseInfo,
			HashMap<ModuleNode, String> assertExpressions) {

		for (ModuleNode leader : leaders) {
			//System.out.println("leader node :" + leader);
			for (ModuleNode lSucc : graph.getSuccessors(leader)) {

				ModuleNode current = lSucc;
				while (!leaders.contains(current)) {
					ModuleEdge in = null;
					ModuleEdge out = null;
					// it will have only one incoming edge
					for (ModuleEdge inc : graph.getInEdges(current)) {
						in = inc;
						break;
					}

					// will have only one out edge
					for (ModuleEdge outg : graph.getOutEdges(current)) {
						out = outg;
						break;
					}
					
					

					ModuleNode source = in.getFrom();
					ModuleNode target = out.getTo();
					
					if(in == null)
					{
						System.out.println("found issue");
					}
					if(in.gettFunc() == null )
					{
						System.out.println("found issue");
					}
					
					if (in.gettFunc().isID() && isZeroDelta(in.getDelta())) {
						//modify the usage infos
						if(varUseInfo.containsKey(current))
						{
							if(varUseInfo.containsKey(source)) {
								
								//append to the existing use string, try to avoid duplicates
								String sUse = varUseInfo.get(source);
								String currUse = varUseInfo.get(current);
								
								String newUse = sUse+":" + currUse;
								
								varUseInfo.replace(source, newUse);
								
							}
							else {
								varUseInfo.put(source, varUseInfo.get(current));								
							}
							
							varUseInfo.remove(current);
						//	System.out.println("replaced var use at node " + current+ " with node " + source);
						}
						
						if(assertExpressions.containsKey(current))
						{
							if(assertExpressions.containsKey(source)) {
								
								//append to the existing use string, try to avoid duplicates
								String sExpr = assertExpressions.get(source);
								String currExpr = assertExpressions.get(current);
								
								String newExpr = sExpr +":" + currExpr;
								
								assertExpressions.replace(source, newExpr);
								
							}
							else {
								assertExpressions.put(source, assertExpressions.get(current));								
							}
							
							assertExpressions.remove(current);
							//System.out.println("replaced assertions at node " + current+ " with node " + source);
						}
						
						
						ModuleEdge newEdge = new ModuleEdge(source, target, out.gettFunc(), out.getDelta());
						graph.addEdge(newEdge);
						graph.removeEdge(in);
						graph.removeEdge(out);
						graph.removeNode(current);
						//System.out.println("removing node :" + current);

					}

					current = target;

				}

			}
		}
		return graph;
	}


	public ModuleGraph mergeIDEdgesWithoutInfo(ModuleGraph graph, Set<ModuleNode> leaders) {

		int count=0;
		for (ModuleNode leader : leaders) {
			//System.out.println("leader node :" + leader);
			ModuleNode leaderCopy = graph.getNode(leader.getId());
			for (ModuleNode lSucc : graph.getSuccessors(leaderCopy)) {

				ModuleNode current = lSucc;
				while (!leaders.contains(current)) {
					ModuleEdge in = null;
					ModuleEdge out = null;
					// it will have only one incoming edge
					for (ModuleEdge inc : graph.getInEdges(current)) {
						in = inc;
						break;
					}

					// will have only one out edge
					for (ModuleEdge outg : graph.getOutEdges(current)) {
						out = outg;
						break;
					}
					
					

					ModuleNode source = in.getFrom();
					ModuleNode target = out.getTo();
					
					
					
					if (in.gettFunc().isID() && isZeroDelta(in.getDelta())) {
						//modify the usage infos
						ModuleEdge newEdge = new ModuleEdge(source, target, out.gettFunc(), out.getDelta());
						graph.addEdge(newEdge);
						graph.removeEdge(in);
						graph.removeEdge(out);
						graph.removeNode(current);
					//	System.out.println("removing node :" + current);
						//System.out.println(++count + " nodes removed");
						++count;

					}

					current = target;

				}

			}
		}
		return graph;
	}


	
	private boolean isZeroDelta(HashMap<String, Integer> delta) {

		for (String msg : delta.keySet())
			if (delta.get(msg) != 0)
				return false;

		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
