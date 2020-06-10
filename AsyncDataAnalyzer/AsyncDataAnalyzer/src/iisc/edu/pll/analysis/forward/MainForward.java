package iisc.edu.pll.analysis.forward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import iisc.edu.pll.Main;
import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.analysis.AssignToIDConverter;
import iisc.edu.pll.analysis.ForwardGraphExtractor;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.GraphExtractor;
import iisc.edu.pll.analysis.IDEdgeMerger;
import iisc.edu.pll.analysis.SpecParser;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.lattice.AbstractValue;

public class MainForward {

	private final static Logger mainLogger = Logger.getLogger(Main.class);

	private String inputFilename;
	private String latticeType;
	private int cutoff = 0; // default cutoff
	private boolean debug = false; // default
	private boolean validParams;

	long mainProgramStartTime;

	public static void main(String[] args) {
		MainForward m = new MainForward();

		try {
			m.validParams = true;
			m.parseArguments(args);
			PropertyConfigurator.configure("log4j.properties");
			m.mainProgramStartTime = System.currentTimeMillis();
			if (m.validParams)
				m.runAnalysis();
			else
				System.out.println("invalid params");
		} catch (Exception e) {
			System.out.println("excpetion in main :" + e);
			e.printStackTrace();

		}

		long totalTime = ((System.currentTimeMillis() - m.mainProgramStartTime));

		System.out.println("Total Time Taken (ms):" + totalTime); // time
		mainLogger.info("Total Time Taken (ms):" + totalTime); // time

		System.out.println("Total Time Taken (s):" + totalTime / 1000); // time
		mainLogger.info("Total Time Taken (s):" + totalTime / 1000 + "\n"); // time
		// in
		// sec

	}

	private void runAnalysis() {
		SpecParser sp = new SpecParser();
		Document parsedFile = sp.getXMLDoc(inputFilename);

		ForwardGraphExtractor ge = new ForwardGraphExtractor();
		ge.setDoc(parsedFile);
		ge.setLatticeType(latticeType);

		HashSet<String> modules = new HashSet<>();
		modules = getModuleNames(parsedFile);
		ArrayList<String> variables = computeVariables(parsedFile);
		HashSet<String> messageSet = (HashSet<String>) computeMessages(parsedFile);

		// for output
		ArrayList<VarUseInfo> totalVarUseInfo = new ArrayList<>();
		HashMap<ModuleNode, String> assertionExpr = new HashMap<>();

		// for data dependence pruning of nodes

		HashMap<String, HashSet<String>> mainDataDependence = new HashMap<>();
		HashMap<String, HashSet<String>> dataIndependence = new HashMap<>();
		HashMap<String, Set<ModuleNode>> leaderMap = new HashMap<>();
		HashMap<String, HashSet<ModuleEdge>> defMap = new HashMap<>();
		HashSet<String> condSet = new HashSet<>();

		for (String var : variables) {
			mainDataDependence.put(var, new HashSet<>());
			dataIndependence.put(var, new HashSet<>());
			defMap.put(var, new HashSet<>());
		}

		Globals.latticeType = latticeType;
		Globals.numberOfCounters = messageSet.size();
		Globals.numberOfVariables = variables.size();
		Globals.DVars = new ArrayList<>();
		Globals.DVars.add(Globals.lambda);
		Globals.DVars.addAll(variables);

		Globals.varNum = new HashMap<>();
		int count = 0;
		for (String var : variables) {
			Globals.varNum.put(var, count);
			count++;
		}

		ArrayList<ModuleGraph> setOfModules = new ArrayList<>();
		count = 0;
		int totalUses = 0;

		for (String mName : modules) {

			System.out.println("constructing graph for  :" + mName);
			ge = new ForwardGraphExtractor();
			ge.setDoc(parsedFile);
			ge.setLatticeType(latticeType);

			ModuleGraph g = ge.getModuleGraph(mName);
			setOfModules.add(g);

			HashMap<ModuleNode, String> tempUInfo = ge.getVarUseInfo();
			HashMap<ModuleNode, String> tempAExpr = ge.getAssertExpressions();

			for (ModuleNode key : tempUInfo.keySet()) {

				String[] varsues = tempUInfo.get(key).split(":");
				String labels = "";
				String uses = "";
				for (String vuse : varsues) {
					String[] details = vuse.split(";");
					labels = labels + ";" + details[1];
					uses = uses + ";" + details[0];
					totalUses += details[0].split(",").length;
				}
				labels = labels.substring(1);
				uses = uses.substring(1);
				VarUseInfo v = new VarUseInfo(key, count, uses, labels);
				totalVarUseInfo.add(v);
			}

			for (ModuleNode key : tempAExpr.keySet()) {
				assertionExpr.put(key, tempAExpr.get(key));
			}

			// data dependence closure merging
			/// commenting out code for now
			HashMap<String, HashSet<String>> tempDataClosure = ge.getDataDependenceClosure();
			mainDataDependence = mergeDataDependence(mainDataDependence, tempDataClosure);

			// leader record

			leaderMap.put(count + "", ge.getLeaders());

			// defMap merging
			HashMap<String, HashSet<ModuleEdge>> tempdef = ge.getDefMap();
			defMap = mergeDefMap(defMap, tempdef);

			g.writeGraphToFile("graph" + mName + ".txt");

			count++;

		}

		// adding the variable itself in the dependent list
		// this is not true in general, but it hold for the way we will be
		// using this dependence information later

		for (String var : variables) {
			HashSet<String> depSet = mainDataDependence.get(var);
			depSet.add(var);
			mainDataDependence.put(var, depSet);
		}
		mainDataDependence = computeDataClosure(mainDataDependence, variables);
		dataIndependence = computeIndependentVariables(mainDataDependence, variables, dataIndependence);

		System.out.println("all graphs done");
		System.out.println("total number of uses :" + totalUses);
		System.out.println("total number of assertions :" + assertionExpr.size());
		Globals.numberOfModules = setOfModules.size();

		ModuleNode[] startState = new ModuleNode[Globals.numberOfModules];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startState[i] = setOfModules.get(i).getStartNode();

		ProductState init = new ProductState(startState);
		// get result of init state with 0 demand
		Vector<Integer> initLocationVec = new Vector<>(Globals.numberOfCounters);
		initLocationVec = fillVector(initLocationVec, 0);

		/*
		 * for (VarUseInfo varuse : totalVarUseInfo) {
		 * 
		 * ModuleNode singleModTarget = varuse.getNode(); int targetModindex =
		 * varuse.getModIndex(); String label = varuse.getLabel();
		 * 
		 * ArrayList<ModuleGraph> newSetOfModules = new ArrayList<>();
		 * 
		 * for (int i = 0; i < count; i++) { AssignToIDConverter cov = new
		 * AssignToIDConverter(); ModuleGraph newGraph =
		 * cov.convertAssign(setOfModules.get(i), dataIndependence, defMap,
		 * singleModTarget, varuse, latticeType, variables); IDEdgeMerger idm =
		 * new IDEdgeMerger(); newGraph = idm.mergeIDEdgesWithoutInfo(newGraph,
		 * leaderMap.get(i + ""));
		 * 
		 * // newGraph.writeGraphToFile("graph" + i + ".txt");
		 * newSetOfModules.add(newGraph);
		 * 
		 * } }
		 */

		mainLogger.info("start state :" + init + "\n");
		mainLogger.info("cutoff :" + cutoff + "\n");
		

		int numOfConstants = 0;
		try {
			DataFlowSequential dfs = new DataFlowSequential(latticeType, variables, messageSet, setOfModules, cutoff, debug);
			dfs.runAnalysis();
			for (VarUseInfo varuse : totalVarUseInfo) {

				ModuleNode singleModTarget = varuse.getNode();
				int targetModindex = varuse.getModIndex();
				String label = varuse.getLabel();

				AbstractValue resultVal = dfs.getResult(targetModindex, singleModTarget);

				String[] vuses = varuse.getVarUse().split(";");
				for (int i = 0; i < vuses.length; i++) {
					String[] nodevuse = vuses[i].split(",");
					for (int j = 0; j < nodevuse.length; j++) {
						if (resultVal.isConstantFor(nodevuse[j])) {
							System.out.println(nodevuse[j] + " is constant at use " + varuse);
							mainLogger.info(nodevuse[j] + " is constant at use " + varuse + "\n");
							numOfConstants++;
						}
					}
				}
				System.out.println("result for Node " + singleModTarget + " in graph " + targetModindex);
				System.out.println("Var Use Info : " + varuse);
				System.out.println(resultVal);
				System.out.println("-------------------");

				mainLogger.info("result for Node " + singleModTarget + " in graph " + targetModindex + "\n");
				mainLogger.info("Var Use Info : " + varuse + "\n");
				mainLogger.info(resultVal + "\n");
				mainLogger.info("-------------------\n");
			}

		} catch (Exception e) {
			System.out.println("exception in main" + e);
		}

		System.out.println("Total Number of Constants  :" + numOfConstants);
		mainLogger.info("Total Number of Constants  :" + numOfConstants + "\n");

	}

	private HashMap<String, HashSet<String>> computeDataClosure(HashMap<String, HashSet<String>> dataDependenceClosure,
			ArrayList<String> variables) {
		boolean changed = true;

		/*
		 * System.out.println("printing the original data dependence");
		 * 
		 * for(String var: variables) { System.out.print(var + " depends on  : "
		 * ); for(String dv: dataDependenceClosure.get(var)) System.out.print(
		 * "\t" + dv);
		 * 
		 * System.out.println();
		 * 
		 * }
		 */
		while (changed) {
			changed = false;
			for (String var : variables) {
				HashSet<String> depVars = new HashSet<>(dataDependenceClosure.get(var));
				for (String dv : depVars) {
					// System.out.println("dv = " + dv);
					HashSet<String> depSet = new HashSet<>(dataDependenceClosure.get(dv));
					if (dataDependenceClosure.get(var).addAll(depSet))
						changed = true;
				}
			}
		}

		/*
		 * System.out.println("printing the closed data dependence");
		 * 
		 * for(String var: variables) { System.out.print(var + " depends on  : "
		 * ); for(String dv: dataDependenceClosure.get(var)) System.out.print(
		 * "\t" + dv);
		 * 
		 * System.out.println();
		 * 
		 * }
		 */

		return dataDependenceClosure;

	}

	private HashMap<String, HashSet<String>> computeIndependentVariables(
			HashMap<String, HashSet<String>> mainDataDependence, ArrayList<String> variables,
			HashMap<String, HashSet<String>> dataIndependence) {

		for (String var : variables) {
			HashSet<String> varSet = new HashSet<>(variables);
			varSet.removeAll(mainDataDependence.get(var));
			dataIndependence.put(var, varSet);
		}

		System.out.println("independent variables");
		for (String var : variables) {
			System.out.println(var + " : " + dataIndependence.get(var));
		}

		return dataIndependence;
	}

	// computes the data dependence closure of the graph

	private HashMap<String, HashSet<ModuleEdge>> mergeDefMap(HashMap<String, HashSet<ModuleEdge>> defMap,
			HashMap<String, HashSet<ModuleEdge>> tempdef) {

		HashMap<String, HashSet<ModuleEdge>> map = new HashMap<>();

		// merge the two maps
		for (String var : defMap.keySet()) {
			HashSet<ModuleEdge> vars = new HashSet<>();
			vars.addAll(defMap.get(var));
			vars.addAll(tempdef.get(var));
			map.put(var, vars);
		}

		return map;

	}

	private HashMap<String, HashSet<String>> mergeDataDependence(HashMap<String, HashSet<String>> mainDataDependence,
			HashMap<String, HashSet<String>> tempDataClosure) {

		HashMap<String, HashSet<String>> map = new HashMap<>();

		// merge the two maps
		for (String var : mainDataDependence.keySet()) {
			HashSet<String> vars = new HashSet<>();
			vars.addAll(mainDataDependence.get(var));
			vars.addAll(tempDataClosure.get(var));
			map.put(var, vars);
		}

		return map;
	}

	/**
	 * using the DOM model of the system extracts the total number of messages
	 * and adds it to messageList
	 */
	private Set<String> computeMessages(Document doc) {

		HashSet<String> messageSet = new HashSet<>();
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
		return messageSet;

	}

	private HashSet<String> getModuleNames(Document parsedFile) {
		HashSet<String> names = new HashSet<>();

		NodeList initNode = parsedFile.getElementsByTagName("INIT");
		if (initNode.getLength() >= 1)
			names.add("INIT");

		NodeList otherModules = parsedFile.getElementsByTagName("PROCTYPE");

		for (int i = 0; i < otherModules.getLength(); i++) {
			Node proc = otherModules.item(i);
			NodeList children = proc.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);
				if (child.getNodeType() == Document.ELEMENT_NODE && child.getNodeName().equals("NAME")) {
					names.add(child.getAttributes().getNamedItem("VALUE").getNodeValue().trim());
				}
			}

		}

		return names;
	}

	private ArrayList<String> computeVariables(Document doc) {
		ArrayList<String> vars = new ArrayList<>();

		NodeList decls = doc.getElementsByTagName("IVAR");
		for (int i = 0; i < decls.getLength(); i++) {
			Node decl = decls.item(i);
			Node name = getFirstChild(doc, decl, "NAME");
			String varname = name.getAttributes().getNamedItem("VALUE").getNodeValue().trim();

			vars.add(varname);
		}

		return vars;
	}

	private Node getFirstChild(Document doc, Node node, String name) {

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == doc.ELEMENT_NODE && n.getNodeName().equals(name))
				return n;
		}

		return null;
	}

	private void parseArguments(String[] args) {

		Options options = new Options();
		options.addOption("inputFileName", true, "Input XML file for analysis");
		options.addOption("latticeType", true, "Name of underlying lattice for analysis (lcp/ara)");
		options.addOption("cutoff", true, "this is the abstract cutoff for the forward algorithm");
		options.addOption("debug", false, "provide this if debugging required, off by default");
		
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("inputFileName")) {
				this.inputFilename = cmd.getOptionValue("inputFileName");
			} else {
				System.out.println("kindly enter input XML filename");
				validParams = false;
				return;
			}
			if (cmd.hasOption("latticeType")) {
				this.latticeType = cmd.getOptionValue("latticeType");
			} else {
				System.out.println("kindly enter lattice type ");
				validParams = false;
				return;

			}
			
			if (cmd.hasOption("cutoff")) {
				// adding java jar
				String cutoffStr = cmd.getOptionValue("cutoff");
				cutoff = Integer.parseInt(cutoffStr);

			} else {
				System.out.println("cut off set to 0 by default by default");
				this.cutoff = 0;
			}

			if (cmd.hasOption("debug")) {
				// adding java jar
				this.debug = true;
			} else {
				System.out.println("debug set to false");
				this.debug = false;
			}

		} catch (Exception e) {
			System.out.println("exception!! " + e.getMessage());
			validParams = false;
		}

	}

	// extracts the file name of the model
	private String getFileName() {
		int sepIndex = 0;
		if (inputFilename.contains("/"))
			sepIndex = inputFilename.lastIndexOf("/");
		if (inputFilename.contains("\\"))
			sepIndex = inputFilename.lastIndexOf("\\");

		int dotindex = inputFilename.lastIndexOf(".");

		String name = inputFilename.substring(sepIndex + 1, dotindex);
		System.out.println("filename :" + name);
		return name;
	}

	private Vector<Integer> fillVector(Vector<Integer> vec, int val) {
		for (int i = 0; i < Globals.numberOfCounters; i++) {
			vec.addElement(val);

		}
		return vec;
	}

}
