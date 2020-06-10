package iisc.edu.pll.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import iisc.edu.pll.data.lattice.TFunction;

public class ModuleGraph {

	Vector<ModuleNode> nodes;
	HashSet<ModuleEdge> edges;
	ModuleNode startNode;
	ModuleNode endNode;

	public Vector<ModuleNode> getNodes() {
		return nodes;
	}

	public void setNodes(Vector<ModuleNode> nodes) {
		this.nodes = nodes;
	}

	public HashSet<ModuleEdge> getEdges() {
		return edges;
	}

	public void setEdges(HashSet<ModuleEdge> edges) {
		this.edges = edges;
	}

	public ModuleNode getEndNode() {
		return endNode;
	}

	public void setEndNode(ModuleNode endNode) {
		this.endNode = endNode;
	}

	public ModuleNode getStartNode() {
		return startNode;
	}

	public void setStartNode(ModuleNode startNode) {
		this.startNode = startNode;
	}

	public ModuleGraph() {
		this.nodes = new Vector<>();
		this.edges = new HashSet<>();
		startNode = null;
		endNode = null;
	}

	public ModuleGraph(ModuleNode snode, HashSet<ModuleNode> nodes, HashSet<ModuleEdge> edges, ModuleNode eNode) {
		this.nodes = new Vector<>();
		this.edges = new HashSet<>();
		
		
		this.nodes.addAll(nodes);
		this.edges.addAll(edges);
		startNode = snode;
		endNode = eNode;
		this.nodes.add(eNode);
		this.nodes.add(snode);
	}

	public ModuleGraph(ModuleNode snode, ModuleNode eNode) {
		this.nodes = new Vector<>();
		this.edges = new HashSet<>();
		startNode = snode;
		endNode = eNode;
		nodes.add(snode);
		nodes.add(eNode);
	}
	public ModuleGraph(ModuleNode node) {
		this.nodes = new Vector<>();
		this.edges = new HashSet<>();
		startNode = node;
		endNode = node;
		nodes.add(node);
		
	}

	public void addNode(ModuleNode n) {
		nodes.add(n);
	}

	public void addEdge(ModuleEdge e) {

		/* Does not need to be here
		 * check whether it exists
		// invariant - one edge between two nodes
		HashSet<GEdge> outs = new HashSet<>(e.getFrom().getOutgoing());
		for (GEdge eOut : outs) {
			if (eOut.getFrom().getId() == e.getFrom().getId() && eOut.getTo().getId() == e.getTo().getId())
				return;
		}*/
//		/System.out.println("adding edge :" + e);
		edges.add(e);
		ModuleNode fromNode = e.getFrom();
		ModuleNode toNode = e.getTo();
		HashSet<ModuleEdge> fromNodeOutSet = new HashSet<>(fromNode.getOutgoing());
		fromNodeOutSet.add(e);
		fromNode.setOutgoing(fromNodeOutSet); // reset the set

		HashSet<ModuleEdge> toNodeInSet = new HashSet<>(toNode.getIncoming());
		toNodeInSet.add(e);
		toNode.setIncoming(toNodeInSet);

	}

	public void addEdge(ModuleNode from, ModuleNode to, TFunction f, HashMap<String, Integer> d) {
		ModuleEdge e = new ModuleEdge(from, to, f, d);
		/* check whether it exists
		// invariant - one edge between two nodes
		HashSet<GEdge> outs = new HashSet<>(from.getOutgoing());
		for (GEdge eOut : outs) {
			if (eOut.getFrom().getId() == from.getId() && eOut.getTo().getId() == to.getId())
				return;
		}*/
		//System.out.println("adding edge :" + e);
		edges.add(e);
		HashSet<ModuleEdge> fromNodeOutSet = new HashSet<>(from.getOutgoing());
		fromNodeOutSet.add(e);
		from.setOutgoing(fromNodeOutSet);
		HashSet<ModuleEdge> toNodeInSet = new HashSet<>(to.getIncoming());
		toNodeInSet.add(e);
		to.setIncoming(toNodeInSet);

	}

	public HashSet<ModuleNode> getSuccessors(ModuleNode n) {
		HashSet<ModuleNode> succ = new HashSet<>();
		HashSet<ModuleEdge> outs = n.getOutgoing();
		for (ModuleEdge e : outs) {
			succ.add(e.getTo());
		}
		return succ;
	}

	public HashSet<ModuleNode> getPredecessor(ModuleNode n) {
		HashSet<ModuleNode> pred = new HashSet<>();
		HashSet<ModuleEdge> ins = n.getIncoming();
		for (ModuleEdge e : ins) {

			pred.add(e.getFrom());

		}
		return pred;

	}

	public void addNodes(Collection<ModuleNode> inNodes) {
		nodes.addAll(inNodes);
	}

	public void addEdges(HashSet<ModuleEdge> inEdges) {

		for (ModuleEdge e : inEdges)
			addEdge(e);

	}

	public void printGraph() {
		printNodes();
		printEdges();
	}

	public void removeEdge(ModuleEdge e) {

	//	System.out.println("remove called");
		edges.remove(e);
		HashSet<ModuleEdge> outFromFrom = new HashSet<>(e.getFrom().getOutgoing());
		outFromFrom.remove(e);
		e.getFrom().setOutgoing(outFromFrom);

		HashSet<ModuleEdge> inToTo = new HashSet<>(e.getTo().getIncoming());
		inToTo.remove(e);
		e.getTo().setIncoming(inToTo);

	}

	public void removeEdges(HashSet<ModuleEdge> inEdges) {

		for (ModuleEdge e : inEdges)
			removeEdge(e);
	}

	public void printEdges() {
		System.out.println("print all edges");
		for (ModuleEdge e : edges) {
			System.out.println("---------------------");
			// System.out.println(e.getFrom().getId() + "--->" +
			// e.getTo().getId() + " : \n" + e.gettFunc());
			System.out.println(e);
			System.out.println("---------------------");
		}

	}

	public void printNodes() {
		System.out.println("all nodes in the graph");
		for (ModuleNode n : nodes) {
			System.out.println("---------------------");
			System.out.println(n.getId());
			System.out.println("---------------------");

		}

	}

	public void writeGraphToFile(String filename) {
		try {
			FileWriter writer = new FileWriter(filename);
			BufferedWriter bwriter = new BufferedWriter(writer);
			StringBuilder content = new StringBuilder();
			bwriter.write("All " + nodes.size() + " nodes in the graph \n");
			for (ModuleNode n : nodes) {
				content.append("---------------------\n");
				content.append(n.getId() + "\n");
				content.append("---------------------\n");
			}
			content.append("Start Node :" + startNode + "\n");

			content.append("All " + edges.size() + " edges in the graph\n");
			for (ModuleEdge e : edges) {
				content.append("---------------------\n");
				// System.out.println(e.getFrom().getId() + "--->" +
				// e.getTo().getId() + " : \n" + e.gettFunc());
				content.append(e.toString() + "\n");
				content.append("---------------------\n");
			}
			bwriter.write(content.toString());
			bwriter.close();
			writer.close();

		} catch (IOException ioe) {
			System.out.println("Exception while writing to file " + filename);
		}

	}

	public HashSet<ModuleEdge> getOutEdges(ModuleNode current) {
		// TODO Auto-generated method stub
		return current.getOutgoing();
	}
	
	public HashSet<ModuleEdge> getInEdges(ModuleNode current)
	{
		return current.getIncoming();
	}

	//this is just for testing purposes
	public ModuleNode getNode(long id){
		
		for(ModuleNode node : nodes)
			if(id == node.getId())
				return node;
		
		return null;
	}
	
	public void removeNodes(Collection<ModuleNode> nodes){
		for(ModuleNode node: nodes)
		{
			removeNode(node);
		}
	}
	public void removeNode(ModuleNode node) {
		
		for(ModuleEdge in :  node.incoming)
		{
			ModuleNode from = in.getFrom();
			from.outgoing.remove(in);
		}
		
		for(ModuleEdge out : node.outgoing)
		{
			ModuleNode to = out.getTo();
			to.incoming.remove(out);
		}
		removeEdges(node.incoming);
		removeEdges(node.outgoing);
		nodes.remove(node);
		
	}
}
