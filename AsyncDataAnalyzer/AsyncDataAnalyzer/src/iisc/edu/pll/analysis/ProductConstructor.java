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

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.ModuleGraph;

public class ProductConstructor {

	ArrayList<ModuleGraph> graphs;
	HashMap<ModuleNode, ArrayList<ModuleNode>> nodeMap;
	
	

	public ProductConstructor(ArrayList<ModuleGraph> inp) {
		graphs = new ArrayList<>(inp);
		nodeMap = new HashMap<>();

	}

	public ModuleGraph constructProduct(){
		
		ModuleGraph mainGraph = new ModuleGraph();
		HashSet<ArrayList<ModuleNode>> setOfNodes = new HashSet<>();
		setOfNodes.add(new ArrayList<>());
		
		HashSet<ArrayList<ModuleNode>> toRemove= new HashSet<>();
		HashSet<ArrayList<ModuleNode>> toAdd = new HashSet<>();
		//construct nodes
		//assumes unique IDS for all graphs
		for(ModuleGraph g : graphs){
			for (ArrayList<ModuleNode> nodes : setOfNodes)
			{
				for(ModuleNode node : g.getNodes())
				{
					ArrayList<ModuleNode> temp = new ArrayList(nodes);
					temp.add(node);
					toAdd.add(temp);
					
				}				
				toRemove.add(nodes);
			}
			setOfNodes.addAll(toAdd);
			setOfNodes.removeAll(toRemove);
			toRemove= new HashSet<>();
			toAdd = new HashSet<>();			
		}
		
		System.out.println("size of set : " + setOfNodes.size());
		
		/*for(ArrayList<GNode> nodes : setOfNodes)
		{
			System.out.println("set of nodes");
			for(GNode node : nodes)
				System.out.println(node.getId());
		}*/
		
		//add nodes
		for(ArrayList< ModuleNode> nodes : setOfNodes)
		{
			ModuleNode node = new ModuleNode();
			mainGraph.addNode(node);
			nodeMap.put(node, nodes);
		//	System.out.println("current node :" + node);
			//System.out.println("set of nodes");
			//for(GNode node1 : nodes)
				//System.out.println(node1.getId());
			
		}
		
		addStartAndEndNodes(mainGraph, nodeMap);
		
		//add edges
		for(ModuleNode node1 : mainGraph.getNodes())
		{
			for(ModuleNode node2 : mainGraph.getNodes())
			{
				ArrayList<ModuleNode> list1 = nodeMap.get(node1);
				ArrayList<ModuleNode> list2 = nodeMap.get(node2);
				for(int i =0 ; i< list1.size(); i++)
				{
					ModuleNode subNode1 = list1.get(i);
					ModuleNode subNode2 = list2.get(i);
					ModuleEdge edge = getEdge(subNode1,subNode2, graphs.get(i));
					if(edge!=null)
					{
						mainGraph.addEdge(new ModuleEdge(node1, node2, edge.gettFunc(), edge.getDelta()));
						//System.out.println("adding edge : " + edge + " between nodes " + node1 + " and " + node2);
					}
					
				}
				
			}
		}
		
		
		System.out.println("number of added edges : " + mainGraph.getEdges().size());
		
		return mainGraph;
		
	}

	
	//here we assume that in the nodes there is only a single edge between any two nodes
	private ModuleEdge getEdge(ModuleNode subNode1, ModuleNode subNode2, ModuleGraph graph) {
		ModuleEdge edge = null;
		
		for(ModuleEdge e : graph.getEdges())
		{
			if(e.getFrom().getId()== subNode1.getId() && e.getTo().getId()== subNode2.getId())
			{
				edge = e;
				break;
			}
		}
		return edge;
	}

	private void addStartAndEndNodes(ModuleGraph mainGraph, HashMap<ModuleNode, ArrayList<ModuleNode>> nodeMap2) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
