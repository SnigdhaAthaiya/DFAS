package iisc.edu.pll.analysis.forward.concurrent.simple;

import java.io.IOException;
import java.time.Duration;
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

import akka.actor.typed.ActorSystem;
import iisc.edu.pll.Main;
import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.analysis.ForwardGraphExtractor;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.SpecParser;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.analysis.forward.DataFlowSequential;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;

public class ActorSimpleMain {

	private final static Logger mainLogger = Logger.getLogger(Main.class);

	private String inputFilename;
	private String latticeType;
	private int cutoff = 0; // default cutoff
	private boolean debug = false; // default
	private String assertionFile;
	private boolean checkAssertion;

	long mainProgramStartTime;

	// starts the system
	public static void main(String[] args) {

		ActorSimpleMain m = new ActorSimpleMain();

		try {
			m.parseArguments(args);

			PropertyConfigurator.configure("log4j.properties");

			m.mainProgramStartTime = System.currentTimeMillis();

			m.runAnalysis();
		} catch (Throwable e) {
			System.out.println("excpetion in main :" + e.getMessage());
			e.printStackTrace();

		}

		long totalTime = ((System.currentTimeMillis() - m.mainProgramStartTime) / 1000);
		System.out.println("Total Time Taken :" + totalTime); // time
		mainLogger.info("Total Time Taken :" + totalTime); // time
															// in
															// sec

	}

	private void parseArguments(String[] args) {

		Options options = new Options();
		options.addOption("inputFileName", true, "Input XML file for analysis");
		options.addOption("latticeType", true, "Name of underlying lattice for analysis (lcp/ara)");
		options.addOption("debug", false, "provide this if debugging required, off by default");
		options.addOption("cutoff", true, "this is the abstract cutoff for the forward algorithm");
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("inputFileName")) {
				this.inputFilename = cmd.getOptionValue("inputFileName");
			} else {
				System.out.println("kindly enter input XML filename");

				return;
			}
			if (cmd.hasOption("latticeType")) {
				this.latticeType = cmd.getOptionValue("latticeType");
			} else {
				System.out.println("kindly enter lattice type ");

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

			if (cmd.hasOption("checkAssertion")) {
				this.checkAssertion = true;
				this.assertionFile = getFileName();
			} else {
				this.checkAssertion = false;
				this.assertionFile = "none";
			}

		} catch (Exception e) {
			System.out.println("exception!! " + e.getMessage());
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

	private void runAnalysis() {
		
		long graphConstructTime = System.currentTimeMillis();
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
		int totalAsserts =0;

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
					totalUses+= details[0].split(",").length;
					
				}
				labels = labels.substring(1);
				uses = uses.substring(1);
				VarUseInfo v = new VarUseInfo(key, count, uses, labels);
				totalVarUseInfo.add(v);
			}

			
			//TODO correct assertion nnumber reporting
			for (ModuleNode key : tempAExpr.keySet()) {
				assertionExpr.put(key, tempAExpr.get(key));
			}

			g.writeGraphToFile("graph" + mName + ".txt");

			count++;

		}

		System.out.println("all graphs done");
		System.out.println("total number fo uses :" + totalUses);
		System.out.println("total number of assertions :" + assertionExpr.size());
		
		mainLogger.info("total number fo uses :" + totalUses +"\n");
		mainLogger.info("total number of assertions :" + assertionExpr.size() + "\n");
	
		Globals.numberOfModules = setOfModules.size();

		ModuleNode[] startState = new ModuleNode[Globals.numberOfModules];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startState[i] = setOfModules.get(i).getStartNode();

		ProductState init = new ProductState(startState);
		// get result of init state with 0 demand
		Vector<Integer> initLocationVec = new Vector<>(Globals.numberOfCounters);
		initLocationVec = fillVector(initLocationVec, 0);

		mainLogger.info("start state :" + init + "\n");
		mainLogger.info("cutoff :" + cutoff + "\n");

		int numOfConstants = 0;
		try {
			// #actor-system
			final ActorSystem<Supervisor.Command> supervisor = ActorSystem.create(
					Supervisor.create(Duration.ofSeconds(50), latticeType, setOfModules, 
							cutoff, totalVarUseInfo,modules),
					"supervisor");
			// #actor-system
			try {
				supervisor.tell(Supervisor.StartAnalysis.INSTANCE);
				System.out.println("Graph construction Time  :" + 
				(System.currentTimeMillis() - graphConstructTime)/1000 + " sec");
				System.out.println("Graph construction Time  :" + 
						(System.currentTimeMillis() - graphConstructTime) + " msec");
						
				System.out.println(">>> Press ENTER to exit <<<");
				System.in.read();
			} catch (IOException ignored) {
			} catch (Exception e) {
			} finally {
				supervisor.terminate();
			}

			// DataFlowSequential dfs = new DataFlowSequential(latticeType,
			// variables, messageSet, setOfModules, cutoff,
			// debug);

			/*
			 * dfs.runAnalysis(); for (VarUseInfo varuse : totalVarUseInfo) {
			 * 
			 * ModuleNode singleModTarget = varuse.getNode(); int targetModindex
			 * = varuse.getModIndex(); String label = varuse.getLabel();
			 * System.out.println("result for Node " + singleModTarget +
			 * " in graph " + targetModindex);
			 * System.out.println("Var Use Info : " + varuse);
			 * System.out.println(dfs.getResult(targetModindex,
			 * singleModTarget)); System.out.println("-------------------");
			 * 
			 * mainLogger.info("result for Node " + singleModTarget +
			 * " in graph " + targetModindex + "\n");
			 * mainLogger.info("Var Use Info : " + varuse + "\n");
			 * mainLogger.info(dfs.getResult(targetModindex, singleModTarget) +
			 * "\n"); mainLogger.info("-------------------\n");
			 */
			// }

		} catch (Exception e) {
			System.out.println("exception in main" + e);
		}

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

}
