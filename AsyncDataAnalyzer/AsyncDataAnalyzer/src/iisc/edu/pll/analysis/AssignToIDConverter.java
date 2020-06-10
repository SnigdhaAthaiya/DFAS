package iisc.edu.pll.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class AssignToIDConverter {

	public ModuleGraph convertAssign(ModuleGraph graph, HashMap<String, HashSet<String>> dataIndep,
			HashMap<String, HashSet<ModuleEdge>> defMap, ModuleNode target, ArrayList<String> finalUses, String latticeType,
			ArrayList<String> variables) {
		HashSet<ModuleNode> nodes = new HashSet<>(graph.getNodes());
		HashSet<ModuleEdge> edges = new HashSet<>(graph.getEdges());
		ModuleGraph newGraph = copyGraph(graph);
	

		int count=0;
		
			try {
			for (int i= 0 ; i< finalUses.size(); i++) {
				String use = finalUses.get(i).trim();
			//	System.out.println("for use :" + use + "data independence info : " + dataIndep.get(use.trim()));
				if(use ==null){
					System.out.println("use is null");
					continue;
				}
				if(dataIndep.get(use) == null)
				{
					System.out.println("no uses for use : " + use);
					continue;
				}
				ArrayList<String> indeps = new ArrayList<>(dataIndep.get(use));
				for (int j=0; j< indeps.size(); j++) {
					String indep = indeps.get(j);
					
					// for all independent variables, convert assigns to ID

					for (ModuleEdge edge : newGraph.getEdges()) {
						
						if (!edge.getTo().equals(target) && edge.gettFunc()!= null && edge.gettFunc().getDef()!=null 
								&& edge.gettFunc().getDef().equals(indep)) {
						//	System.out.println("edge from " + edge.getFrom() + " to " + edge.getTo() + " converted to ID" );
						//	System.out.println(++count + " edges converted");
							edge.settFunc(TFunctionFactory.createFunction(latticeType, Statement.ID, variables,
									new ArrayList<>()));
							
							/*ModuleEdge edge2 = new ModuleEdge(edge.getFrom(), edge.getTo(), , edge.getDelta() );
							System.out.println("adding edge :" + edge2);
							edges.add(edge2);
							ModuleNode fromNode = new ModuleNode(edge.getFrom());
							ModuleNode toNode = new ModuleNode(edge.getTo());
							
							if(fromNode.getOutgoing().size()>1 || toNode.getIncoming().size()>1)
								System.out.println("we have problem");
							HashSet<ModuleEdge> outFromFromNode = new HashSet<>();
							HashSet<ModuleEdge> inToToNode = new HashSet<>();
							outFromFromNode.add(edge2);
							inToToNode.add(edge2);
							
							fromNode.setOutgoing(outFromFromNode);
							toNode.setIncoming(inToToNode);
							nodes.add(fromNode);
							nodes.add(fromNode);*/
							//have kept the rest of the edges same, subsequent iterations may change them if needed
							
							
						}
					
					}
				}
			}
		} catch (NullPointerException npe) {
			System.out.println("caught NPE" + npe.getMessage());
			npe.printStackTrace();
			System.out.println("uses :" + finalUses);
			System.out.println("Node :" + target);
		}

		
		return newGraph;
	}

	private ModuleGraph copyGraph(ModuleGraph graph) {
		ModuleGraph newGraph = new ModuleGraph();
		
		ModuleNode snode = new ModuleNode(graph.getStartNode());
		ModuleNode enode = new ModuleNode(graph.getEndNode());
		
		newGraph.setStartNode(snode);
		newGraph.setEndNode(enode);
		HashSet<ModuleNode> nodes = new HashSet<>();
		nodes.add(snode);
		nodes.add(enode);
		
		
		for(ModuleNode node : graph.getNodes())
		{
			nodes.add(new ModuleNode(node));
		}
		
		newGraph.setNodes(new Vector<>(nodes));
		
		for(ModuleEdge edge : graph.getEdges())
		{
			ModuleEdge newEdge = new ModuleEdge(newGraph.getNode(edge.getFrom().getId()), newGraph.getNode(edge.getTo().getId()) , edge.gettFunc(), edge.getDelta());
			newGraph.addEdge(newEdge);
		}
		
		
		return newGraph;
	}

}
