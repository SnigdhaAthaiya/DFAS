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
