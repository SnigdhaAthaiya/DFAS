package iisc.edu.pll.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.crypto.CipherOutputStream;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;

import iisc.edu.pll.data.CounterHandler;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.lattice.*;
import iisc.edu.pll.data.lattice.lcp.LCPIDEAssign;
import iisc.edu.pll.data.lattice.lcp.LCPIDEConst;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.NodeType;
import iisc.edu.pll.data.Statement;

public class ForwardGraphExtractor {

	Document doc;

	HashSet<String> messageSet;
	ArrayList<String> variables;
	String latticeType;
	ArrayList<ModuleNode> doExits;
	Vector<LoopExitObject> exitEdgesToRemove;
	HashMap<String, ModuleNode> labeledNodes;
	HashMap<ModuleNode, String> gotoTargets;

	HashMap<ModuleNode, String> varUseInfo;
	HashMap<ModuleNode, String> assertExpressions;
	HashMap<String, HashSet<String>> dataDependenceClosure;
	HashSet<String> condVars ;

	int exitEdgeListSize = 5;

	ModuleNode lastDoEntry;
	HashMap<ModuleNode, BreakInfoObject> breakInfo;

	HashSet<ModuleNode> leaders;
	ArrayList<ModuleNode> controlEnds;

	HashMap<String, HashSet<ModuleEdge>> defMap;

	public HashMap<String, HashSet<ModuleEdge>> getDefMap() {
		return defMap;
	}

	public void setDefMap(HashMap<String, HashSet<ModuleEdge>> defMap) {
		this.defMap = defMap;
	}

	public ForwardGraphExtractor() {
		messageSet = new HashSet<>();
		exitEdgesToRemove = new Vector<>(exitEdgeListSize);
		labeledNodes = new HashMap<>();
		gotoTargets = new HashMap<>();

		varUseInfo = new HashMap<>();
		assertExpressions = new HashMap<>();

		leaders = new HashSet<>();
		controlEnds = new ArrayList<>();

		dataDependenceClosure = new HashMap<>();
		defMap = new HashMap<>();
		condVars = new HashSet<>();

	}

	public HashMap<String, HashSet<String>> getDataDependenceClosure() {
		return dataDependenceClosure;
	}

	public void setDataDependenceClosure(HashMap<String, HashSet<String>> dataDependenceClosure) {
		this.dataDependenceClosure = dataDependenceClosure;
	}

	/**
	 * takes as input a module name (_init for init module) returns the
	 * transition system for that module
	 */
	public ModuleGraph getModuleGraph(String modName) {
		ModuleGraph graph = new ModuleGraph();
		doExits = new ArrayList<>();
		lastDoEntry = null;
		breakInfo = new HashMap<>();
		computeMessages();
		variables = computeVariables();

		String name = "PROCTYPE";
		if (modName.equals("_init"))
			name = "INIT";

		NodeList modules = doc.getElementsByTagName(name);
		if (modules.getLength() < 1)
			return graph;

		Node modNode;
		if (name.equals("INIT"))
			modNode = modules.item(0);
		else
			modNode = getNode(name, modName);

		Node seq = getFirstChild(modNode, "SEQUENCE");

		ModuleNode gn = new ModuleNode();

		ModuleNode en = new ModuleNode();

		graph = new ModuleGraph(gn, en);

		leaders.add(gn);
		leaders.add(en);

		for (String var : variables) {
			dataDependenceClosure.put(var, new HashSet<>());
			defMap.put(var, new HashSet<>());
		}

		// TODO - process graph here
		ModuleGraph childGraph = processSeq(seq);

		// adding edges
		graph.addNodes(childGraph.getNodes());
		graph.addEdges(childGraph.getEdges());

		TFunction fun1 = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());
		TFunction fun2 = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		HashMap<String, Integer> del1 = CounterHandler.getNoChange(messageSet);
		HashMap<String, Integer> del2 = CounterHandler.getNoChange(messageSet);

		graph.addEdge(gn, childGraph.getStartNode(), fun1, del1);
		graph.addEdge(childGraph.getEndNode(), en, fun2, del2);

		correctBreakEdges(graph);
		correctDoEdges(graph);
		addGotoEdges(graph);
		addExitLeaders(graph);

		IDEdgeMerger idMerge = new IDEdgeMerger();
		graph = idMerge.mergeIDEdges(graph, leaders, varUseInfo, assertExpressions);
		emptyStructures();

		//add conditional variables as dependence to all vars
		
		System.out.println("all the conditional vars :" + condVars);
	//	condVars =new HashSet<>();
		for(String var :  dataDependenceClosure.keySet())
		{
			HashSet<String> val = dataDependenceClosure.get(var);
			val.addAll(condVars);
			dataDependenceClosure.put(var, val);
		}
		return graph;

	}

	private void addExitLeaders(ModuleGraph graph) {

		for (ModuleNode exit : controlEnds) {
			leaders.addAll(graph.getSuccessors(exit));
		}

		controlEnds.clear();
	}

	private void addGotoEdges(ModuleGraph graph) {
		if (gotoTargets.isEmpty())
			return;
		if (labeledNodes.isEmpty()) {
			System.out.println("cannot process goto, no labels present");
			return;
		}
		for (ModuleNode snode : gotoTargets.keySet()) {
			// remove successors of goto
			HashSet<ModuleEdge> succ = snode.getOutgoing();
			for (ModuleEdge e : succ)
				graph.removeEdge(e);

			// add the goto edge
			String label = gotoTargets.get(snode);
			ModuleNode tNode = labeledNodes.get(label);
			TFunction f = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());
			ModuleEdge edge = new ModuleEdge(snode, tNode, f, CounterHandler.getNoChange(messageSet));
			graph.addEdge(edge);
			leaders.add(tNode);
		}

		System.out.println("goto done");

	}

	private void emptyStructures() {
		exitEdgesToRemove = null;
		labeledNodes = null;
		breakInfo = null;
		lastDoEntry = null;
		gotoTargets = null;

	}

	private void correctDoEdges(ModuleGraph graph) {
		if (exitEdgesToRemove.isEmpty())
			return;

		for (LoopExitObject leObj : exitEdgesToRemove) {
			ModuleNode endNode = leObj.getExitNode();
			long startNodeID = leObj.getStartNodeId();
			ModuleEdge exitEdge = null;
			for (ModuleEdge e : endNode.getOutgoing()) {
				if (e.getTo().getId() != startNodeID) {
					exitEdge = e;
					break;
				}
			}
			if (exitEdge == null) {
				System.out.println("error in do correction");
			} else {

				graph.removeEdge(exitEdge);
			}
		}

	}

	private void correctBreakEdges(ModuleGraph graph) {
		if (breakInfo.isEmpty())
			return;

		System.out.println("in break correction");
		for (ModuleNode bNode : breakInfo.keySet()) {
			BreakInfoObject info = breakInfo.get(bNode);
			// System.out.println("node :" + bNode);
			ModuleNode current = bNode;
			HashSet<ModuleNode> succNodes = graph.getSuccessors(current);
			HashSet<ModuleEdge> edgesToRemove = new HashSet<>();
			ModuleNode startNode = info.getDoStartNode();
			boolean hasMultiplePreds = false;

			while (!succNodes.contains(startNode)) {
				HashSet<ModuleEdge> outedges = graph.getOutEdges(current);

				if (!hasMultiplePreds) {
					HashSet<ModuleEdge> inEdges = graph.getInEdges(current);
					if ((current.equals(bNode) && inEdges.size() > 1) || (!bNode.equals(current) && inEdges.size() > 0))
						hasMultiplePreds = true;
				}

				if (!hasMultiplePreds) {
					graph.removeEdges(outedges);
				}

				HashSet<ModuleNode> temp = new HashSet<>();
				for (ModuleNode node : succNodes) {
					temp.addAll(graph.getSuccessors(node));
				}

				// NOTE this operation is safe here by construction
				for (ModuleNode next : succNodes)
					current = next;
				succNodes = new HashSet<>(temp);

			}

			// adding the edge now to the non -start successor
			succNodes.remove(info.getDoStartNode());
			for (ModuleNode node : succNodes) {
				ModuleEdge e = new ModuleEdge(bNode, node,
						TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>()),
						CounterHandler.getNoChange(messageSet));
				// System.out.println("adding edge " + e);
				graph.addEdge(e);
				leaders.add(node);
			}
		}

	}

	private Node getFirstChild(Node node, String name) {

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == doc.ELEMENT_NODE && n.getNodeName().equals(name))
				return n;
		}

		return null;
	}

	private Node getNode(String name, String modName) {
		Node mnode;
		NodeList list = doc.getElementsByTagName(name);

		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			NodeList children = n.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);
				if (child.getNodeType() == Document.ELEMENT_NODE && child.getNodeName().equals("NAME")) {
					String val = child.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
					// System.out.println(val);
					if (val.equals(modName))
						return n;
				}
			}
		}
		return null;
	}

	/**
	 * using the DOM model of the system extracts the total number of messages
	 * and adds it to messageList
	 */
	private void computeMessages() {

		NodeList mtypes = doc.getElementsByTagName("MTYPE");
		for (int i = 0; i < mtypes.getLength(); i++) {
			Node mtype = mtypes.item(i);

			NodeList names = mtype.getChildNodes();
			for (int j = 0; j < names.getLength(); j++) {
				Node child = names.item(j);
				if (child.getNodeType() == Document.ELEMENT_NODE) {
					String val = child.getAttributes().getNamedItem("VALUE").getTextContent().trim();
					// System.out.println("adding: " + val);
					messageSet.add(val);
				}

			}

		}

	}

	/**
	 * Methods for processing the xml tree top down
	 */

	private ModuleGraph processSeq(Node seq) {

		NodeList children = seq.getChildNodes();

		// dummy head node
		ModuleNode prevNode = new ModuleNode();

		// dummy end node
		ModuleNode lastNode = new ModuleNode();

		// new Graph
		ModuleGraph graph = new ModuleGraph();
		graph.addNode(prevNode);
		graph.addNode(lastNode);
		graph.setStartNode(prevNode);
		graph.setEndNode(lastNode);

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Document.ELEMENT_NODE && child.getNodeName().equals("STEP")) {
				ModuleGraph childG = processStep(child);
				if (childG.getStartNode() != null) {
					graph.addNodes(childG.getNodes());
					graph.addEdges(childG.getEdges());

					// getting function
					TFunction v = TFunctionFactory.createFunction(latticeType, Statement.ID, variables,
							new ArrayList<>());

					// getting delta
					HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);
					graph.addEdge(prevNode, childG.getStartNode(), v, del);
					if (childG.getEndNode() != null)
						prevNode = childG.getEndNode();

				}
			}
		}

		// getting delta
		HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);

		TFunction fun = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		graph.addEdge(prevNode, lastNode, fun, del);

		return graph;

	}

	private ModuleGraph processStep(Node child) {

		ModuleGraph graph = new ModuleGraph();

		Node stmt = getFirstChild(child, "STMT");
		if (stmt == null)
			return graph;

		Node stmtChild = getFirstElementChild(stmt);
		String nodename = stmtChild.getNodeName();
		// split on statment type
		if (nodename.equals("ASSIGN")) {
			graph = processAssign(stmtChild);
		} else if (nodename.equals("SEND")) {
			graph = processSend(stmtChild);
		} else if (nodename.equals("RECEIVE")) {
			graph = processReceive(stmtChild);
		} else if (nodename.equals("IF")) {
			graph = processIf(stmtChild);
		} else if (nodename.equals("DO")) {
			graph = processDo(stmtChild);
		} else if (nodename.equals("BREAK")) {
			graph = processBreak(stmtChild);
		} else if (nodename.equals("EXPR")) {
			graph = processExpr(stmtChild);
		} else if (nodename.equals("ASSERT")) {
			graph = processAssert(stmtChild);
		} else if (nodename.equals("LABELEDST")) {
			graph = processLabel(stmtChild);
		} else if (nodename.equals("GOTO")) {
			graph = processGoto(stmtChild);
		} else if (nodename.equals("ATOMIC")) {
			graph = processAtomic(stmtChild);
		}

		return graph;
	}

	private ModuleGraph processAtomic(Node stmt) {

		Node stmtChild = getFirstChild(stmt, "SEQUENCE");
		ModuleGraph seqGraph = processSeq(stmtChild);

		// compose the graph transfer function and demand
		HashMap<String, Integer> delta = CounterHandler.getNoChange(messageSet);
		TFunction func = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph graph = new ModuleGraph(start, end);

		Vector<ModuleNode> setofNodes = new Vector<>();
		setofNodes.addElement(seqGraph.getStartNode());
		while (!setofNodes.isEmpty()) {
			ModuleNode n = setofNodes.get(0);
			for (ModuleEdge e : n.getOutgoing()) {
				func = func.compose(e.gettFunc());
				delta = composeDelta(delta, e.getDelta());
				setofNodes.add(e.getTo());
			}

			// checking if that node was required for useinfo
			if (varUseInfo.containsKey(n)) {
				String varuse = varUseInfo.get(n);
				varUseInfo.remove(n);
				String currentVarUse = varUseInfo.get(start);
				if (currentVarUse == null)
					varUseInfo.put(start, varuse);
				else
					varUseInfo.put(start, currentVarUse + "," + varuse);
				System.out.println("replaced the use by node " + n + " by node " + start);
				System.out.println("current varuse of " + start + " : " + varUseInfo.get(start));
			}
			if (assertExpressions.containsKey(n)) {
				String varuse = assertExpressions.get(n);
				assertExpressions.remove(n);
				assertExpressions.put(start, varuse);
				System.out.println("transferred the assertion at node " + n + " to node " + start);
			}
			setofNodes.remove(n);
		}

		ModuleEdge edge = new ModuleEdge(start, end, func, delta);
		graph.addEdge(edge);

		setofNodes = null;

		return graph;
	}

	private HashMap<String, Integer> composeDelta(HashMap<String, Integer> delta1, HashMap<String, Integer> delta2) {
		HashMap<String, Integer> delta = new HashMap<>();
		for (String msg : delta1.keySet()) {
			delta.put(msg, delta1.get(msg) + delta2.get(msg));
		}
		return delta;
	}

	/**
	 * This method processes goto statements As the lable may succeed goto in
	 * the code, here it only does bookeeping
	 */
	private ModuleGraph processGoto(Node stmtChild) {

		ModuleNode singleNode = new ModuleNode();
		ModuleGraph graph = new ModuleGraph(singleNode);

		Node nameChild = getFirstChild(stmtChild, "NAME");
		String label = nameChild.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		gotoTargets.put(singleNode, label);
		return graph;
	}

	private ModuleGraph processLabel(Node stmtChild) {
		ModuleNode singleNode = new ModuleNode();
		ModuleGraph graph = new ModuleGraph(singleNode);

		Node nameChild = getFirstChild(stmtChild, "NAME");
		String label = nameChild.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		labeledNodes.put(label, singleNode);
		return graph;
	}

	private ModuleGraph processAssert(Node stmtChild) {

		String varuse = null;
		Node vuseNode = stmtChild.getAttributes().getNamedItem("VARUSE");
		if (vuseNode != null) {
			varuse = vuseNode.getNodeValue().trim();

		}
		String eval = stmtChild.getAttributes().getNamedItem("EVAL").getNodeValue().trim();
		String label = stmtChild.getAttributes().getNamedItem("LABEL").getNodeValue().trim();

		Node exprChild = getFirstChild(stmtChild, "EXPR");

		Node variableRef = getFirstChild(exprChild, "VARREF");
		Node opNode = getFirstChild(exprChild, "OP");
		Node constNode = getFirstChild(exprChild, "CONST");

		String varname = getFirstChild(variableRef, "NAME").getAttributes().getNamedItem("VALUE").getNodeValue();
		String op = opNode.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		String constval = constNode.getAttributes().getNamedItem("VALUE").getNodeValue().trim();

		String expr = varname + op + constval;
		// System.out.println("assert expresssion :" + expr);

		HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);
		TFunction func = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph graph = new ModuleGraph(start, end);
		ModuleEdge edge = new ModuleEdge(start, end, func, del);
		graph.addEdge(edge);

		// adding use info
		if (eval.equals("TRUE") && varuse != null && label != null) {
			String detail = varuse + ";" + label;
			varUseInfo.put(start, detail);
			assertExpressions.put(start, expr);
			System.out.println(
					"adding var use  " + varuse + " for " + varuse + " for node " + label + " with expr : " + expr);
		}

		return graph;
	}

	// cannot handle conditionals, therefore transfer function is ID
	private ModuleGraph processExpr(Node stmtChild) {
		HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);
		TFunction func = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		ModuleNode start = new ModuleNode();

		ModuleGraph graph = new ModuleGraph(start);

		return graph;

	}

	private ModuleGraph processBreak(Node stmtChild) {
		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph g = new ModuleGraph();
		g.addNode(start);
		g.setStartNode(start);
		g.addNode(end);
		g.setEndNode(end);

		TFunction f = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		ModuleEdge e = new ModuleEdge(start, end, f, CounterHandler.getNoChange(messageSet));
		// System.out.println("adding break edge :" + e);
		g.addEdge(e);

		// System.out.println("adding breakInfo");
		breakInfo.put(start, new BreakInfoObject(e, lastDoEntry));
		return g;
	}

	private ModuleGraph processDo(Node stmtChild) {
		
		NodeList children = stmtChild.getChildNodes();
		HashMap<ModuleGraph, ArrayList<String>> cGraphs = new HashMap<>();
		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph doGraph = new ModuleGraph(start, end);
		doExits.add(end);
		ModuleNode prevLastEntry = lastDoEntry;
		lastDoEntry = start;
		TFunction f1 = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		doGraph.addEdge(new ModuleEdge(end, start, f1, CounterHandler.getNoChange(messageSet))); // back
																									// edge

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Document.ELEMENT_NODE) {
				if (child.getNodeName().equals("OPTION")) {
					// System.out.println("got node :" + child.getNodeName());

					// varuse code

					Node vuseNode = child.getAttributes().getNamedItem("VARUSE");

					Node evalNode = child.getAttributes().getNamedItem("EVAL");
					Node labelNode = child.getAttributes().getNamedItem("LABEL");
					String op = "==";
					String varuse = null;
					String eval = null;
					String label = null;
					if (vuseNode != null) {
						varuse = vuseNode.getNodeValue().trim();
					}

					if (evalNode != null) {
						eval = evalNode.getNodeValue().trim();
					}

					if (labelNode != null)
						label = labelNode.getNodeValue().trim();

					// getting expression
					ArrayList<String> args = new ArrayList<>();
					Node exprChild = getFirstChild(child, "EXPR");
					if (exprChild != null) {
						args = getIfExpr(exprChild);
						op = args.remove(1);
					}

					// process expr

					Node seqChild = getFirstChild(child, "SEQUENCE");
					ModuleGraph g = processSeq(seqChild);

					doGraph.addNodes(g.getNodes());
					doGraph.addEdges(g.getEdges());

					TFunction startID = TFunctionFactory.createFunction(latticeType, Statement.ID, variables,
							new ArrayList<>());
					TFunction endID = TFunctionFactory.createFunction(latticeType, Statement.ID, variables,
							new ArrayList<>());

					TFunction comd;

					// when there are no arguments only add start and end nodes
					if (args.isEmpty()) {
						doGraph.addEdge(new ModuleEdge(start, g.getStartNode(), startID,
								CounterHandler.getNoChange(messageSet)));
						doGraph.addEdge(
								new ModuleEdge(g.getEndNode(), end, endID, CounterHandler.getNoChange(messageSet)));
						leaders.add(g.getStartNode());

						// adding use info
						if (eval != null && eval.equals("TRUE") && varuse != null && label != null) {
							String detail = varuse + ";" + label;
							varUseInfo.put(g.getStartNode(), detail);
							System.out.println("adding var use  " + varuse + " for " + varuse + " for node " + label);
						}

					} else {

						// decide transfer function
						if (op.equals("=="))
							comd = TFunctionFactory.createFunction(latticeType, Statement.EQUALSTRUE, variables, args);
						else if (op.equals(">"))
							comd = TFunctionFactory.createFunction(latticeType, Statement.GREATERTHANTRUE, variables,
									args);
						else
							comd = TFunctionFactory.createFunction(latticeType, Statement.EQUALSTRUE, variables, args);

						ModuleNode pseudoStart = new ModuleNode();
						doGraph.addNode(pseudoStart);
						ModuleEdge pseudoStartEdge = new ModuleEdge(pseudoStart, g.getStartNode(), comd,
								CounterHandler.getNoChange(messageSet));
						doGraph.addEdge(pseudoStartEdge);

						doGraph.addEdge(
								new ModuleEdge(start, pseudoStart, startID, CounterHandler.getNoChange(messageSet)));
						doGraph.addEdge(
								new ModuleEdge(g.getEndNode(), end, endID, CounterHandler.getNoChange(messageSet)));
						leaders.add(pseudoStart);

						// adding use info
						if (eval != null && eval.equals("TRUE") && varuse != null && label != null) {
							String detail = varuse + ";" + label;
							varUseInfo.put(pseudoStart, detail);
							System.out.println("adding var use  " + varuse + " for " + varuse + " for node " + label);
						}

					}

				}
			}
		}

		// removing from list
		doExits.remove(end);

		leaders.add(start);
		leaders.add(end);
		controlEnds.add(end);

		// adding the last node for edge removal later for faithful do semantics
		exitEdgesToRemove.addElement(new LoopExitObject(end, start.getId()));

		lastDoEntry = prevLastEntry;
		return doGraph;
	}

	private ModuleGraph processIf(Node stmtChild) {

		// System.out.println("handling if");
		NodeList children = stmtChild.getChildNodes();
		HashMap<ModuleGraph, ArrayList<String>> cGraphs = new HashMap<>();
		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph ifGraph = new ModuleGraph(start, end);
		ArrayList<String> args = new ArrayList<>();
		ArrayList<String> elseargs = new ArrayList<>();

		String op = "==";
		leaders.add(start);
		leaders.add(end);
		controlEnds.add(end);

		// get condition
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Document.ELEMENT_NODE) {
				if (child.getNodeName().equals("OPTION")) {
					// varuse code
					Node vuseNode = child.getAttributes().getNamedItem("VARUSE");

					Node evalNode = child.getAttributes().getNamedItem("EVAL");
					Node labelNode = child.getAttributes().getNamedItem("LABEL");

					String varuse = null;
					String eval = null;
					String label = null;
					if (vuseNode != null) {
						varuse = vuseNode.getNodeValue().trim();
					}

					if (evalNode != null) {
						eval = evalNode.getNodeValue().trim();
					}

					if (labelNode != null)
						label = labelNode.getNodeValue().trim();

					// getting expression
					Node exprChild = getFirstChild(child, "EXPR");
					if (exprChild != null) {
						args = getIfExpr(exprChild);
						op = args.get(1);
						args.remove(1); // TODO generalize later
						elseargs.addAll(args);
					}

					// getting subgraph
					Node seqChild = getFirstChild(child, "SEQUENCE");

					ModuleGraph g = processSeq(seqChild);
					ifGraph.addNodes(g.getNodes());
					ifGraph.addEdges(g.getEdges());

					TFunction startID = TFunctionFactory.createFunction(latticeType, Statement.ID, variables,
							new ArrayList<>());
					TFunction endID = TFunctionFactory.createFunction(latticeType, Statement.ID, variables,
							new ArrayList<>());

					TFunction comd;

					// when there are no arguments only add start and end nodes
					if (args.isEmpty()) {
						ifGraph.addEdge(new ModuleEdge(start, g.getStartNode(), startID,
								CounterHandler.getNoChange(messageSet)));
						ifGraph.addEdge(
								new ModuleEdge(g.getEndNode(), end, endID, CounterHandler.getNoChange(messageSet)));
						leaders.add(g.getStartNode());

						// adding use info
						if (eval != null && eval.equals("TRUE") && varuse != null && label != null) {
							String detail = varuse + ";" + label;
							varUseInfo.put(g.getStartNode(), detail);
							System.out.println("adding var use  " + varuse + " for " + varuse + " for node " + label);
						}

					} else {

						// decide transfer function
						if (op.equals("=="))
							comd = TFunctionFactory.createFunction(latticeType, Statement.EQUALSTRUE, variables, args);
						else if (op.equals(">"))
							comd = TFunctionFactory.createFunction(latticeType, Statement.GREATERTHANTRUE, variables,
									args);
						else
							comd = TFunctionFactory.createFunction(latticeType, Statement.EQUALSTRUE, variables, args);

						ModuleNode pseudoStart = new ModuleNode();
						ifGraph.addNode(pseudoStart);
						ModuleEdge pseudoStartEdge = new ModuleEdge(pseudoStart, g.getStartNode(), comd,
								CounterHandler.getNoChange(messageSet));
						ifGraph.addEdge(pseudoStartEdge);

						ifGraph.addEdge(
								new ModuleEdge(start, pseudoStart, startID, CounterHandler.getNoChange(messageSet)));
						ifGraph.addEdge(
								new ModuleEdge(g.getEndNode(), end, endID, CounterHandler.getNoChange(messageSet)));
						leaders.add(pseudoStart);

						// adding use info
						if (eval != null && eval.equals("TRUE") && varuse != null && label != null) {
							String detail = varuse + ";" + label;
							varUseInfo.put(pseudoStart, detail);
							System.out.println("adding var use  " + varuse + " for " + varuse + " for node " + label);
						}

					}

				}
			}
		}

		// processing else node

		Node child = getFirstChild(stmtChild, "ELSE");

		if (child != null) {
			Node seqChild = getFirstChild(child, "SEQUENCE");

			ModuleGraph g = processSeq(seqChild);
			ifGraph.addNodes(g.getNodes());
			ifGraph.addEdges(g.getEdges());

			TFunction startID;

			if (elseargs.isEmpty())
				startID = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());
			else if (elseargs.size() == 2) {
				if (op.equals(""))
					startID = TFunctionFactory.createFunction(latticeType, Statement.EQUALSFALSE, variables, elseargs);
				else if (op.equals(">"))
					startID = TFunctionFactory.createFunction(latticeType, Statement.GREATERTHANFALSE, variables,
							elseargs);
				else
					startID = TFunctionFactory.createFunction(latticeType, Statement.EQUALSFALSE, variables, elseargs);
			} else
				startID = TFunctionFactory.createFunction(latticeType, Statement.EQUALSSWITCHFALSE, variables,
						elseargs);

			TFunction endID = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

			ifGraph.addEdge(new ModuleEdge(start, g.getStartNode(), startID, CounterHandler.getNoChange(messageSet)));
			ifGraph.addEdge(new ModuleEdge(g.getEndNode(), end, endID, CounterHandler.getNoChange(messageSet)));
			leaders.add(g.getStartNode());

		}

		return ifGraph;
	}

	private ModuleGraph processSend(Node stmtChild) {
		Node child = getFirstChild(stmtChild, "NAME");
		String messageName = child.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);
		del = CounterHandler.getDelta(del, messageName, 1);

		TFunction func = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph graph = new ModuleGraph(start, end);
		graph.addEdge(new ModuleEdge(start, end, func, del));
		return graph;
	}

	private ModuleGraph processReceive(Node stmtChild) {
		Node child = getFirstChild(stmtChild, "NAME");
		String messageName = child.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);
		del = CounterHandler.getDelta(del, messageName, -1);

		TFunction func = TFunctionFactory.createFunction(latticeType, Statement.ID, variables, new ArrayList<>());

		ModuleNode start = new ModuleNode();
		ModuleNode end = new ModuleNode();
		ModuleGraph graph = new ModuleGraph(start, end);
		ModuleEdge edge = new ModuleEdge(start, end, func, del);
		graph.addEdge(edge);
		// System.out.println("handled recieve :" + edge);
		return graph;
	}

	// currently works for equality
	// TODO generalize
	private ArrayList<String> getIfExpr(Node expr) {
		ArrayList<String> args = new ArrayList<>();

		Node lhs = getFirstChild(expr, "VARREF");
		String lhsVarname = getFirstChild(lhs, "NAME").getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		args.add(lhsVarname);

		Node opNode = getFirstChild(expr, "OP");
		String op = opNode.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		// need to remove it before passing to constructor
		args.add(op);

		Node rhsNode = lhs.getNextSibling();
		String rhsTerm = "";
		while (rhsNode.getNodeType() != Document.ELEMENT_NODE || !rhsNode.getNodeName().equals("VARREF")) {
			rhsNode = rhsNode.getNextSibling();
		}
		rhsTerm = getFirstChild(rhsNode, "NAME").getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		args.add(rhsTerm);
		
		//add to the conditional list
		condVars.add(lhsVarname);
		String[] rhsarr = rhsTerm.split(",");
		String rhs = rhsarr[1];
		if(!StringUtils.isNumeric(rhs))
			condVars.add(rhs);
		
		return args;
	}

	private ModuleGraph processAssign(Node stmtChild) {
		// assuming we do not have property access or arrays for now.
		// TODO add the array support later?

		String varuse = null;
		Node vuseNode = stmtChild.getAttributes().getNamedItem("VARUSE");
		if (vuseNode != null) {
			varuse = vuseNode.getNodeValue().trim();
		}
		String eval = stmtChild.getAttributes().getNamedItem("EVAL").getNodeValue().trim();
		String label = stmtChild.getAttributes().getNamedItem("LABEL").getNodeValue().trim();

		Node lhs = getFirstChild(stmtChild, "VARREF");
		Node rhs = getFirstChild(stmtChild, "EXPR");
		String varname = getFirstChild(lhs, "NAME").getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		String operand1 = getFirstChild(getFirstChild(rhs, "VARREF"), "NAME").getAttributes().getNamedItem("VALUE")
				.getNodeValue().trim();
		Node opNode = getFirstChild(rhs, "OP");
		String op = null;

		String operand2 = null;
		if (opNode != null) {
			op = opNode.getAttributes().getNamedItem("VALUE").getNodeValue().trim();
			Node op2Node = opNode.getNextSibling();
			while (op2Node.getNodeType() != Document.ELEMENT_NODE) {
				op2Node = op2Node.getNextSibling();
			}
			operand2 = getFirstChild(op2Node, "NAME").getAttributes().getNamedItem("VALUE").getNodeValue().trim();
		}

		// System.out.println("the expression is " + varname + " = " + operand1
		// + op + operand2);

		ModuleNode start = new ModuleNode();

		ModuleNode end = new ModuleNode();

		ModuleGraph g = new ModuleGraph(start, end);

		TFunction fun = getAssignFunction(varname, op, operand1, operand2);

		HashSet<String> uses = dataDependenceClosure.get(varname);
		String opVar = operand1.split(",")[1];
		if (!StringUtils.isNumeric(opVar)) {
			uses.add(opVar);
			dataDependenceClosure.put(varname, uses);
		}

		// computing dependence info

		// getting delta
		HashMap<String, Integer> del = CounterHandler.getNoChange(messageSet);

		ModuleEdge e = new ModuleEdge(start, end, fun, del);
		g.addEdge(e);

		String def = varname.trim();
		// defMap entry for data dependence
		HashSet<ModuleEdge> oldSet = defMap.get(def);
		oldSet.add(e);
		defMap.put(def, oldSet);

		// adding use info, here only operand 1 can be a variable currently
		// TODO - generalize this for ARA
		String op1 = operand1.split(",")[1];
		// adding use info
		if (eval.equals("TRUE") && varuse != null && label != null) {
			String detail = varuse + ";" + label;
			varUseInfo.put(start, detail);
			System.out.println("adding var use  " + varuse + " for " + varuse + " for node " + label);
		}

		return g;
	}

	private TFunction getAssignFunction(String varname, String op, String operand1, String operand2) {

		ArrayList<String> args = new ArrayList<>();
		args.add(varname);
		args.add(operand1);

		if (op != null) {

			args.add(op);
			args.add(operand2);
			// linear assignment
			return TFunctionFactory.createFunction(latticeType, Statement.ASSIGN, variables, args);
		} else {
			// const or copy
			String[] operand1Comp = operand1.split(",");
			if (StringUtils.isNumeric(operand1Comp[1]))
				return TFunctionFactory.createFunction(latticeType, Statement.CONSTASSIGN, variables, args);
			else
				return TFunctionFactory.createFunction(latticeType, Statement.ASSIGN, variables, args);
		}

	}

	private Node getFirstElementChild(Node stmt) {

		NodeList list = stmt.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Document.ELEMENT_NODE)
				return list.item(i);
		}
		return null;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public HashSet<String> getMessageSet() {
		return messageSet;
	}

	public void setMessageSet(HashSet<String> messageSet) {
		this.messageSet = messageSet;
	}

	public String getLatticeType() {
		return latticeType;
	}

	public void setLatticeType(String latticeType) {
		this.latticeType = latticeType;
	}

	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}

	public ArrayList<String> getVariables() {
		return variables;
	}

	public static void main(String[] args) {
		SpecParser sp = new SpecParser();
		Document parsedFile = sp.getXMLDoc("test/test1.xml");

		GraphExtractor ge = new GraphExtractor();
		ge.setDoc(parsedFile);
		ge.setLatticeType("lcp");
		ModuleGraph g = ge.getModuleGraph("P1");
		/*
		 * GraphExtractor ge2 = new GraphExtractor(); ge2.doc =
		 * sp.getXMLDoc("test/test1.xml"); ge2.getMessages(); ge2.variables =
		 * ge2.getVariables(); ge2.latticeType = "lcp";
		 * 
		 * Graph g1 = ge2.getModuleGraph("P1");
		 * 
		 * /*GraphExtractor ge3 = new GraphExtractor(); ge3.doc =
		 * sp.getXMLDoc("test/test1.xml"); ge3.getMessages(); ge3.variables =
		 * ge3.getVariables(); ge3.latticeType = "lcp";
		 * 
		 * Graph g2 = ge2.getModuleGraph("P1");
		 */
		g.printGraph();
		// g1.printGraph();
		// g.printNodes();

		// g1.printNodes();
		// g2.printNodes();

		/*
		 * ArrayList<Graph> graphs = new ArrayList<>(); graphs.add(g);
		 * graphs.add(g1); //graphs.add(g2); ProductConstructor p = new
		 * ProductConstructor(graphs); Graph mainGraph = p.constructProduct();
		 */

	}

	public HashMap<ModuleNode, String> getVarUseInfo() {
		return varUseInfo;
	}

	public void setVarUseInfo(HashMap<ModuleNode, String> varUseInfo) {
		this.varUseInfo = varUseInfo;
	}

	public HashMap<ModuleNode, String> getAssertExpressions() {
		return assertExpressions;
	}

	public void setAssertExpressions(HashMap<ModuleNode, String> assertExpressions) {
		this.assertExpressions = assertExpressions;
	}

	private ArrayList<String> computeVariables() {
		ArrayList<String> vars = new ArrayList<>();

		NodeList decls = doc.getElementsByTagName("IVAR");
		for (int i = 0; i < decls.getLength(); i++) {
			Node decl = decls.item(i);
			Node name = getFirstChild(decl, "NAME");
			String varname = name.getAttributes().getNamedItem("VALUE").getNodeValue().trim();

			vars.add(varname);
		}

		return vars;
	}

	public Set<ModuleNode> getLeaders() {
		// TODO Auto-generated method stub
		return leaders;
	}

}
