package iisc.edu.pll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import iisc.edu.pll.analysis.AssignToIDConverter;
import iisc.edu.pll.analysis.DataFlow;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.GraphExtractor;
import iisc.edu.pll.analysis.IDEdgeMerger;
import iisc.edu.pll.analysis.ProductConstructor;
import iisc.edu.pll.analysis.SpecParser;
import iisc.edu.pll.analysis.assertions.ConditionCheck;
import iisc.edu.pll.analysis.assertions.ConditionCheckFactory;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;

import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.arashort.ARAIDEAssignShortOb;
import iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat;
import iisc.edu.pll.data.lattice.lcp.LCPIDEAssign;
import iisc.edu.pll.data.lattice.lcp.LCPIDEConst;
import iisc.edu.pll.data.lattice.reachingdef.OtherRHS;
import iisc.edu.pll.data.lattice.reachingdef.RDSingle;
import iisc.edu.pll.exceptions.InvalidArguments;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;

public class Main {

	private final static Logger mainLogger = Logger.getLogger(Main.class);

	public String inputFilename;
	private String latticeType;
	private Mode mode = Mode.DEMAND;
	private boolean debug = false; // default
	private String assertionFile;
	private boolean checkAssertion;
	private byte filterMode;

	long mainProgramStartTime;
	private ARAIDEFunctionShortMat ARAIDEFunctionShortMat;

	public static void main(String[] args) {

		Main m = new Main();

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
		options.addOption("mode", true, "naive/demand mode");
		options.addOption("debug", false, "provide this if debugging required, off by default");
		options.addOption("checkAssertion", false, "provide this if assertion checking for ARA is required");
		options.addOption("filterMode", true, "naive/demand mode");
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("inputFileName")) {
				this.inputFilename = cmd.getOptionValue("inputFileName");
				Globals.filename = inputFilename;
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
			if (cmd.hasOption("mode")) {
				// adding java jar
				String modeStr = cmd.getOptionValue("mode");

				if (modeStr.equalsIgnoreCase("naive"))
					this.mode = Mode.NAIVE;

			} else {
				System.out.println("mode set to demand by default");
				this.mode = Mode.DEMAND;
			}

			if (cmd.hasOption("filterMode")) {

				String modeStr = cmd.getOptionValue("filterMode");

				System.out.println("filter mode : " + modeStr);
				if (modeStr.equalsIgnoreCase("assign"))
					this.filterMode = 1;
				else
					this.filterMode = 0;

			} else {
				System.out.println("filter mode set to nofilter by default");
				this.filterMode = 0;
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

	private void runAnalysis() {
		// parse
		SpecParser sp = new SpecParser();
		Document parsedFile = sp.getXMLDoc(inputFilename);

		GraphExtractor ge = new GraphExtractor();
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

		Globals.ARAVArs = new ArrayList<>();
		Globals.ARAVArs.addAll(variables);
		Globals.labelVectorsHashMap = new HashMap<String, short[][]>();

		// RD analysis, assign number to each variable
		Globals.rdmap = new HashMap<>();
		Globals.other = new RDSingle("dummy", new OtherRHS());

		// Initializing assertion check here in case of ARA lattice type
		// TODO change this later for general
		if (this.latticeType.compareToIgnoreCase("ara") == 0) {

			assertionCheckIntialise(this.assertionFile);

		}

		ArrayList<ModuleGraph> setOfModules = new ArrayList<>();
		int count = 0;
		int totalUses = 0;

		// printing ARA variables in order
		System.out.println("Variable list\n");
		mainLogger.info("Variable list");
		for (int i = 0; i < Globals.ARAVArs.size(); i++) {
			System.out.println(Globals.ARAVArs.get(i));
			mainLogger.info(Globals.ARAVArs.get(i));

		}

		for (String mName : modules) {

			System.out.println("constructing graph for  :" + mName);
			ge = new GraphExtractor();
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
		System.out.println("total number of assertions :" + assertionExpr.size());
		Globals.numberOfModules = setOfModules.size();

		ModuleNode[] startState = new ModuleNode[Globals.numberOfModules];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startState[i] = setOfModules.get(i).getStartNode();

		ProductState init = new ProductState(startState);
		// get result of init state with 0 demand
		Vector<Integer> initLocationVec = new Vector<>(Globals.numberOfCounters);
		initLocationVec = fillVector(initLocationVec, 0);

		mainLogger.info("start state :" + init + "\n");

		int numOfConstants = 0;
		boolean datadep = true;

		// run the main loop for all uses to be analyzed

		if (ge.getLatticeType().equals("lcp") || ge.getLatticeType().equals("ccp")) {
			System.out.println("lattice type  :" + ge.getLatticeType());

			for (VarUseInfo varuse : totalVarUseInfo) {

				try {

					ModuleNode singleModTarget = varuse.getNode();
					int targetModindex = varuse.getModIndex();
					String label = varuse.getLabel();

					ArrayList<ModuleGraph> newSetOfModules = new ArrayList<>();

					if (datadep) {

						// correct the data dependence to include dependence due
						// to multiple varuse

						HashMap<String, HashSet<String>> tempDataIndep = copyMap(dataIndependence);

						String[] uses = varuse.getVarUse().split(";");
						ArrayList<String> finalUses = new ArrayList<>();
						for (int i = 0; i < uses.length; i++) {
							String[] fuses = uses[i].split(",");
							for (String fuse : fuses)
								finalUses.add(fuse);
						}

						// remove uses from data independence lists
						for (String var : dataIndependence.keySet()) {
							tempDataIndep.get(var).removeAll(finalUses);
						}

						for (int i = 0; i < count; i++) {
							AssignToIDConverter cov = new AssignToIDConverter();
							ModuleGraph newGraph = cov.convertAssign(setOfModules.get(i), tempDataIndep, defMap,
									singleModTarget, finalUses, latticeType, variables);
							IDEdgeMerger idm = new IDEdgeMerger();
							newGraph = idm.mergeIDEdgesWithoutInfo(newGraph, leaderMap.get(i + ""));

							// newGraph.writeGraphToFile("graph" + i + ".txt");
							newSetOfModules.add(newGraph);

						}

					} else
						newSetOfModules = setOfModules;

					mainLogger.info("Starting analysis for :" + varuse + "\n");
					System.out.println("Starting analysis for :" + varuse);
					// byte filterMode = 1;
					Vector<ProductState> targetNodes = getFinalTargetStates(modules, newSetOfModules, singleModTarget,
							targetModindex);

					System.out.println("got target nodes");

					// creating early termination checks
					ConditionCheck checkOb = null;

					if (!varuse.getVarUse().contains(";")) // assertion
															// condition
						checkOb = ConditionCheckFactory.createCheck(varuse, "lcp", true);
					else
						checkOb = ConditionCheckFactory.createCheck(varuse, "lcp", false);

					/*
					 * DataFlowParallel dfp = new
					 * DataFlowParallel(ge.getLatticeType(), variables,
					 * messageSet, setOfModules, targetNodes, mode, debug);
					 */
					DataFlowParallel dfp = new DataFlowParallel(ge.getLatticeType(), variables, messageSet,
							setOfModules, targetNodes, mode, debug, checkOb);

					dfp.stateofInterest = init;
					dfp.runAnalysis();
					// dfp.writeResultToFile(outfname);

					TFunction result = dfp.getResult(init, initLocationVec);

					if (result == null) {
						mainLogger.info("node " + label + " is unreachable with zero demand\n");
						continue;
					}
					mainLogger.info("result for node with labels " + label + " : \n" + result + "\n");
					String[] uses = varuse.getVarUse().split(";");
					String[] labels = varuse.getLabel().split(";");

					for (int i = 0; i < uses.length; i++) {
						String vuse = uses[i];
						String lbl = labels[i];
						String[] vuseArr = vuse.split(",");
						for (String var : vuseArr) {
							totalUses++;
							if (result.isConstantFor(var)) {
								mainLogger.info("for node " + lbl + " variable " + var + " is constant \n");
								numOfConstants++;
							}

						}

					}
					String expr = assertionExpr.get(singleModTarget);
					if (expr != null)
						mainLogger.info("the assertion expression at this node is :" + expr + "\n");

					mainLogger.info(
							"************************************************************************************\n");
					// clear dataStructures
					dfp.clean();
					dfp = null;
					targetNodes.clear();
					System.gc();
				} catch (Exception e) {
					System.out.println("Error in the run :" + e.getMessage());
					mainLogger.error("error in the run :" + e.getMessage());
					e.printStackTrace();
				}

			}

			System.out.println("total number of uses :" + totalUses);
			mainLogger.info("total number of uses :" + totalUses + "\n");
			System.out.println("total number of constants :" + numOfConstants);
			mainLogger.info("number of constants : " + numOfConstants + "\n");

		} else if (ge.getLatticeType().equals("ara")) {
			// hashMap to store the results of demand and Naive modes
			HashMap<String, ArrayList> resultMap = new HashMap<String, ArrayList>();
			System.out.println("////////////////////////////////////////\nStarting the naive run");
			mainLogger.info("////////////////////////////////////////\nStarting the naive run");
			long startNaive = System.currentTimeMillis();

			// checking if only naive run is required or both runs are required

			for (VarUseInfo varuse : totalVarUseInfo) {
				totalUses++;

				try {

					ModuleNode singleModTarget = varuse.getNode();
					int targetModindex = varuse.getModIndex();
					String label = varuse.getLabel();
					mainLogger.info("Naive mode");
					mainLogger.info("Starting analysis for :" + varuse + "\n");
					System.out.println("Starting analysis for :" + varuse);

					// data dependence pruning
					ArrayList<ModuleGraph> newSetOfModules = new ArrayList<>();

					if (datadep) {
						// correct the data dependence to include dependence due
						// to multiple varuse

						HashMap<String, HashSet<String>> tempDataIndep = copyMap(dataIndependence);

						String[] uses = varuse.getVarUse().split(";");
						ArrayList<String> finalUses = new ArrayList<>();
						for (int i = 0; i < uses.length; i++) {
							String[] fuses = uses[i].split(",");
							for (String fuse : fuses)
								finalUses.add(fuse);
						}

						// remove uses from data independence lists
						for (String var : dataIndependence.keySet()) {
							tempDataIndep.get(var).removeAll(finalUses);
						}

						for (int i = 0; i < count; i++) {
							AssignToIDConverter cov = new AssignToIDConverter();
							ModuleGraph newGraph = cov.convertAssign(setOfModules.get(i), dataIndependence, defMap,
									singleModTarget, finalUses, latticeType, variables);
							IDEdgeMerger idm = new IDEdgeMerger();
							newGraph = idm.mergeIDEdgesWithoutInfo(newGraph, leaderMap.get(i + ""));

							// newGraph.writeGraphToFile("graph" + i + ".txt");
							newSetOfModules.add(newGraph);

						}

					} else
						newSetOfModules = setOfModules;

					Vector<ProductState> targetNodes = getFinalTargetStates(modules, setOfModules, singleModTarget,
							targetModindex);
					System.out.println("got target nodes");
					// running DataFlowParallel for Naive mode

					// creating assertion checks
					ConditionCheck checkOb = ConditionCheckFactory.createCheck(varuse, latticeType, true);

					DataFlowParallel dfp = new DataFlowParallel(ge.getLatticeType(), variables, messageSet,
							setOfModules, targetNodes, Mode.NAIVE, debug, checkOb);
					dfp.stateofInterest = init;
					dfp.runAnalysis();
					// dfp.writeResultToFile(outfname);

					TFunction result = dfp.getResult(init, initLocationVec);

					if (result == null) {
						mainLogger.info("node " + label + " is unreachable with zero demand\n");
						continue;
					}
					mainLogger.info("result for node with labels " + label + " : \n" + result + "\n");
					String[] uses = varuse.getVarUse().split(";");
					String[] labels = varuse.getLabel().split(";");

					// Storing the results in a hashmap here to compare with
					// demand later if needed
					List<TFunction> aFunctions = new ArrayList<TFunction>();
					aFunctions.add(result);
					resultMap.put(label, (ArrayList) aFunctions);

					mainLogger.info(
							"************************************************************************************\n");

					// clear dataStructures
					dfp.clean();
					dfp = null;
					result = null;
					targetNodes.clear();
				} catch (Exception e) {
					System.out.println("Error in the run :" + e.getMessage());
					mainLogger.error("error in the run :" + e.getMessage());
					e.printStackTrace();
				}
			}

			long endNaive = System.currentTimeMillis();
			double naiveRuntime = endNaive - startNaive;
			// counter to check the runtime of Naive
			naiveRuntime = naiveRuntime / 1000.0;

			try {
				Thread.currentThread().sleep(2000);
			} catch (Exception e) {
				System.out.println("exception in the thread while sleeping");
				e.printStackTrace();
			}

			System.out.println("////////////////////////////////////////\nDemand mode");
			mainLogger.info("//////////////////////////////////////////\nDemand mode");

			long startDemand = System.currentTimeMillis();

			for (VarUseInfo varuse : totalVarUseInfo) {
				try {
					ModuleNode singleModTarget = varuse.getNode();
					int targetModindex = varuse.getModIndex();
					String label = varuse.getLabel();
					mainLogger.info("Starting analysis for :" + varuse + "\n");
					System.out.println("Starting analysis for :" + varuse);

					ArrayList<ModuleGraph> newSetOfModules = new ArrayList<>();

					if (datadep) {
						// correct the data dependence to include dependence due
						// to multiple varuse

						HashMap<String, HashSet<String>> tempDataIndep = copyMap(dataIndependence);

						String[] uses = varuse.getVarUse().split(";");
						ArrayList<String> finalUses = new ArrayList<>();
						for (int i = 0; i < uses.length; i++) {
							String[] fuses = uses[i].split(",");
							for (String fuse : fuses)
								finalUses.add(fuse);
						}

						// remove uses from data independence lists
						for (String var : dataIndependence.keySet()) {
							tempDataIndep.get(var).removeAll(finalUses);
						}

						for (int i = 0; i < count; i++) {
							AssignToIDConverter cov = new AssignToIDConverter();
							ModuleGraph newGraph = cov.convertAssign(setOfModules.get(i), dataIndependence, defMap,
									singleModTarget, finalUses, latticeType, variables);
							IDEdgeMerger idm = new IDEdgeMerger();
							newGraph = idm.mergeIDEdgesWithoutInfo(newGraph, leaderMap.get(i + ""));

							// newGraph.writeGraphToFile("graph" + i + ".txt");
							newSetOfModules.add(newGraph);

						}

					} else
						newSetOfModules = setOfModules;

					Vector<ProductState> targetNodes = getFinalTargetStates(modules, setOfModules, singleModTarget,
							targetModindex);
					System.out.println("got target nodes");

					ConditionCheck checkOb = ConditionCheckFactory.createCheck(varuse, latticeType, true);
					// running DataFlowParallel for Demand mode
					DataFlowParallel dfpD = new DataFlowParallel(ge.getLatticeType(), variables, messageSet,
							setOfModules, targetNodes, Mode.DEMAND, debug, checkOb);
					dfpD.stateofInterest = init;
					dfpD.runAnalysis();
					// dfp.writeResultToFile(outfname);

					TFunction result = dfpD.getResult(init, initLocationVec);

					if (result == null) {
						mainLogger.info("node " + label + " is unreachable with zero demand\n");
						continue;
					}
					mainLogger.info("result for node with labels " + label + " : \n" + result + "\n");
					String[] uses = varuse.getVarUse().split(";");
					String[] labels = varuse.getLabel().split(";");

					ArrayList<TFunction> arrayList;

					arrayList = resultMap.get(label);

					arrayList.add(result);
					resultMap.put(label, arrayList);

					// clear dataStructures
					dfpD.clean();
					dfpD = null;
					targetNodes.clear();
					mainLogger.info(
							"************************************************************************************\n");

				} catch (Exception e) {
					System.out.println("Error in the run :" + e.getMessage());
					mainLogger.error("error in the run :" + e.getMessage());
					e.printStackTrace();
				}

			}

			long endDemand = System.currentTimeMillis();
			double demandRuntime = endDemand - startDemand;
			// Counter to store the runtime for demand mode
			demandRuntime = demandRuntime / 1000.0;
			// Printing runtime analysis
			System.out.println("//--------------------//");
			mainLogger.info("//-----------------------//");
			System.out.println("Runtime for Naive: " + naiveRuntime);
			mainLogger.info("Runtime for Naive: " + naiveRuntime);
			System.out.println("Runtime for Demand: " + demandRuntime);
			mainLogger.info("Runtime for Demand: " + demandRuntime);
			System.out.println("//--------------------//");
			mainLogger.info("//-----------------------//");

			// counter to check how many results have naive strictly greater
			// than demand
			int strictGreatCountNaive = 0;
			ArrayList<TFunction> aList = new ArrayList<TFunction>();
			ARAIDEFunctionShortMat A = new ARAIDEFunctionShortMat();
			ARAIDEFunctionShortMat B = new ARAIDEFunctionShortMat();

			// Iterating through the result map
			for (Map.Entry<String, ArrayList> mEntry : resultMap.entrySet()) {
				String keyString = mEntry.getKey();
				System.out.println("------------------");
				System.out.println("For Label:" + keyString);
				mainLogger.info("------------------\n");
				mainLogger.info("For Label:" + keyString);

				// the list contains the naive result at 0 and demand result at
				// 1
				aList = mEntry.getValue();

				A = (ARAIDEFunctionShortMat) aList.get(0);

				B = (ARAIDEFunctionShortMat) aList.get(1);

				String[] keyList = keyString.split(";");
				for (int i = 0; i < keyList.length; i++) {

					short[][] vec = Globals.labelVectorsHashMap.get(keyList[i]);
					// checking if the label is and assertion label
					if (vec != null) {
						boolean flagDemand;
						boolean flagNaive;

						// if(keyList[i].equals("L1")){
						// System.out.println("Naive");
						// mainLogger.info("Naive"+"\n");
						// flagNaive = resultCheck(A, vec);
						// System.out.println("Demand");
						// mainLogger.info("Demand"+"\n");
						// flagDemand = resultCheck(B, vec);
						// }
						// else {

						// this is a special OR check for assertions just for
						// dijkstra

						if (assertionFile.equals("dijkstra") && keyList[i].equals("L1")) {
							boolean flagN1, flagN2, flagD1, flagD2;
							short[][] vec1 = new short[Globals.numberOfVariables + 1][1];
							vec1[0][0] = -1;
							vec1[1][0] = 1;

							short[][] vec2 = new short[Globals.numberOfVariables + 1][1];
							vec2[0][0] = 0;
							vec2[1][0] = 1;

							flagN1 = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.resultCheck(A, vec1);
							flagN2 = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.resultCheck(A, vec2);
							flagD1 = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.resultCheck(B, vec1);
							flagD2 = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.resultCheck(B, vec2);

							flagNaive = flagN1 || flagN2;
							flagDemand = flagD1 || flagD2;

						} else {
							flagNaive = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.resultCheck(A, vec);// performing
							// assertion
							// check
							// on
							// Naive
							// Result
							flagDemand = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.resultCheck(B, vec);/// performing
							/// assertion
							/// check
							/// on
							/// Demand
							/// Result
							// }

						}

						System.out.println("Assertion Check For Label: " + keyList[i]);
						mainLogger.info("Assertion Check For Label: " + keyList[i] + "\n");
						if (flagNaive) {
							System.out.println("Naive Assertion Passed");
							mainLogger.info("Naive Assertion Passed");

						} else {
							System.out.println("Naive Assertion Failed");
							mainLogger.info("Naive Assertion Failed");
						}

						if (flagDemand) {
							System.out.println("Demand Assertion Passed");
							mainLogger.info("Demand Assertion Passed");

						} else {
							System.out.println("Demand Assertion Failed");
							mainLogger.info("Demand Assertion Failed");
						}

					}
				}

				// Verifying if the Result obtained for naive and demand is
				// correct
				boolean verifyBool = iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat.verify(A, B);
				if (verifyBool) {
					System.out.println("Verified");
					mainLogger.info("Verified" + "\n");
				} else {
					System.out.println("Not Verified");
					mainLogger.info("Not Verified" + "\n");
				}

				if (A.isGreater(B)) {
					if (A.isStrictlyGreater(B)) {
						// increasing strictly greater count here
						strictGreatCountNaive++;
						System.out.println("Naive is strictly Greater");
						mainLogger.info("Naive is strictly Greater");
					} else {
						System.out.println("Naive is Greater");
						mainLogger.info("Naive is Greater");
					}
				} else if (B.isGreater(A)) {
					if (B.isStrictlyGreater(A)) {
						System.out.println("Demand is strictly Greater");
						mainLogger.info("Demand is strictly Greater");
					} else {
						System.out.println("Demand is Greater");
						mainLogger.info("Demand is Greater");
					}
				} else {
					System.out.println("Both are incomparable");
					mainLogger.info("Both are incomparable");
				}

			}
			// Printing total number of uses and Strictly greater cases
			System.out.println("Number of cases for Strictly Greater:" + strictGreatCountNaive);
			mainLogger.info("Number of cases for Strictly Greater:" + strictGreatCountNaive);
			System.out.println("------------------");
			System.out.println("total number of locs :" + totalUses);
			mainLogger.info("total number of locs :" + totalUses + "\n");
		}
		else if (ge.getLatticeType().equals("rd")) {
			System.out.println("lattice type  :" + ge.getLatticeType());

			for (VarUseInfo varuse : totalVarUseInfo) {

				try {

					ModuleNode singleModTarget = varuse.getNode();
					int targetModindex = varuse.getModIndex();
					String label = varuse.getLabel();

					ArrayList<ModuleGraph> newSetOfModules = new ArrayList<>();

					if (datadep) {

						// correct the data dependence to include dependence due
						// to multiple varuse

						HashMap<String, HashSet<String>> tempDataIndep = copyMap(dataIndependence);

						String[] uses = varuse.getVarUse().split(";");
						ArrayList<String> finalUses = new ArrayList<>();
						for (int i = 0; i < uses.length; i++) {
							String[] fuses = uses[i].split(",");
							for (String fuse : fuses)
								finalUses.add(fuse);
						}

						// remove uses from data independence lists
						for (String var : dataIndependence.keySet()) {
							tempDataIndep.get(var).removeAll(finalUses);
						}

						for (int i = 0; i < count; i++) {
							AssignToIDConverter cov = new AssignToIDConverter();
							ModuleGraph newGraph = cov.convertAssign(setOfModules.get(i), tempDataIndep, defMap,
									singleModTarget, finalUses, latticeType, variables);
							IDEdgeMerger idm = new IDEdgeMerger();
							newGraph = idm.mergeIDEdgesWithoutInfo(newGraph, leaderMap.get(i + ""));

							// newGraph.writeGraphToFile("graph" + i + ".txt");
							newSetOfModules.add(newGraph);

						}

					} else
						newSetOfModules = setOfModules;

					mainLogger.info("Starting analysis for :" + varuse + "\n");
					System.out.println("Starting analysis for :" + varuse);
					
					
					//initialize globals - this is a redundant loop - TODO do better job 
					HashSet<String> useVars = new HashSet<>();
					String[] uses1 = varuse.getVarUse().split(";");
					String[] labels1 = varuse.getLabel().split(";");

					for (int i = 0; i < uses1.length; i++) {
						String vuse = uses1[i];
						String lbl = labels1[i];
						String[] vuseArr = vuse.split(",");
						for (String var : vuseArr) {
							useVars.add(var);
						}

					}
					//init query var number
					Globals.numberOfQueryVar = useVars.size();
					
					Globals.rdmap = new HashMap<>();
					int j=0;
					for(String var: useVars)
					{
						Globals.rdmap.put(var, j);
						j++;
					}

					
					// byte filterMode = 1;
					Vector<ProductState> targetNodes = getFinalTargetStates(modules, newSetOfModules, singleModTarget,
							targetModindex);

					System.out.println("got target nodes");

					// creating early termination checks
					ConditionCheck checkOb = ConditionCheckFactory.createCheck(varuse, "rd", true);

					
					/*
					 * DataFlowParallel dfp = new
					 * DataFlowParallel(ge.getLatticeType(), variables,
					 * messageSet, setOfModules, targetNodes, mode, debug);
					 */
					DataFlowParallel dfp = new DataFlowParallel(ge.getLatticeType(), variables, messageSet,
							setOfModules, targetNodes, mode, debug, checkOb);

					dfp.stateofInterest = init;
					dfp.runAnalysis();
					// dfp.writeResultToFile(outfname);

					TFunction result = dfp.getResult(init, initLocationVec);

					if (result == null) {
						mainLogger.info("node " + label + " is unreachable with zero demand\n");
						continue;
					}
					mainLogger.info("result for node with labels " + label + " : \n" + result + "\n");
					String[] uses = varuse.getVarUse().split(";");
					String[] labels = varuse.getLabel().split(";");

					for (int i = 0; i < uses.length; i++) {
						String vuse = uses[i];
						String lbl = labels[i];
						String[] vuseArr = vuse.split(",");
						for (String var : vuseArr) {
							totalUses++;
							if (result.isConstantFor(var)) {
								mainLogger.info("for node " + lbl + " variable " + var + " is constant \n");
								numOfConstants++;
							}

						}

					}
					String expr = assertionExpr.get(singleModTarget);
					if (expr != null)
						mainLogger.info("the assertion expression at this node is :" + expr + "\n");

					mainLogger.info(
							"************************************************************************************\n");
					// clear dataStructures
					dfp.clean();
					dfp = null;
					targetNodes.clear();
				//	System.gc();
				} catch (Exception e) {
					System.out.println("Error in the run :" + e.getMessage());
					mainLogger.error("error in the run :" + e.getMessage());
					e.printStackTrace();
				}

			}

			System.out.println("total number of uses :" + totalUses);
			mainLogger.info("total number of uses :" + totalUses + "\n");
			System.out.println("total number of constants :" + numOfConstants);
			mainLogger.info("number of constants : " + numOfConstants + "\n");

		}

	}

	// computes the data dependence closure of the graph

	private HashMap<String, HashSet<String>> copyMap(HashMap<String, HashSet<String>> mainMap) {
		HashMap<String, HashSet<String>> temp = new HashMap<>();
		for (String key : mainMap.keySet()) {
			temp.put(key, new HashSet<>(mainMap.get(key)));
		}
		return temp;
	}

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

	private HashMap<String, HashSet<String>> computeIndependentVariables(
			HashMap<String, HashSet<String>> mainDataDependence, ArrayList<String> variables,
			HashMap<String, HashSet<String>> dataIndependence) {

		for (String var : variables) {
			HashSet<String> varSet = new HashSet<>(variables);
			varSet.removeAll(mainDataDependence.get(var));
			dataIndependence.put(var, varSet);
		}

		/*
		 * System.out.println("independent variables"); for(String var:
		 * variables) { System.out.println( var + " : " +
		 * dataIndependence.get(var)); }
		 */

		return dataIndependence;
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
					System.out.println("dv = " + dv);
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

	private Vector<ProductState> getFinalTargetStates(HashSet<String> modules, ArrayList<ModuleGraph> setOfModules,
			ModuleNode singleModTarget, int targetModindex) {

		if (filterMode == 0)
			return gettargetStates(modules, setOfModules, singleModTarget, targetModindex);

		else if (filterMode == 1)
			return getAssignmentTargetStates(modules, setOfModules, singleModTarget, targetModindex);

		// do not use this mode as of now - buggy code
		/*
		 * else if (filterMode == 2) return
		 * getDepAssignmentTargetStates(modules, setOfModules, singleModTarget,
		 * targetModindex, mainDataDependence, varuse);
		 */
		return gettargetStates(modules, setOfModules, singleModTarget, targetModindex);

	}

	private Vector<ProductState> getDepAssignmentTargetStates(HashSet<String> modules,
			ArrayList<ModuleGraph> setOfModules, ModuleNode singleModTarget, int targetModindex,
			HashMap<String, HashSet<String>> mainDataDependence, String varuse) {

		HashSet<String> uses = new HashSet<>();
		String[] uses1 = varuse.split(";");
		for (String use : uses1) {
			if (use.contains(",")) {
				String[] uses2 = use.split(",");
				for (String use2 : uses2) {
					uses.add(use2.trim());
				}
			} else
				uses.add(use.trim());
		}

		HashMap<Integer, Vector<ModuleNode>> assignNodeMap = new HashMap<>();

		// compute total number of product nodes
		int noOfGraphs = setOfModules.size();
		int totalsize = 1;
		for (int i = 0; i < noOfGraphs; i++) {

			Vector<ModuleNode> nodes = new Vector<ModuleNode>();
			if (i == targetModindex) {

				nodes.addElement(singleModTarget);
				assignNodeMap.put(targetModindex, nodes);
			} else {

				nodes = getDepAssignmentNodes(setOfModules.get(i).getNodes(), mainDataDependence, uses);

				if (nodes.size() == 0 | nodes.isEmpty())
					nodes.add(setOfModules.get(i).getStartNode());
				assignNodeMap.put(i, nodes);
			}

			totalsize *= nodes.size();
		}

		System.out.println("number of targets :" + totalsize);
		mainLogger.info("number of targets :" + totalsize);

		Vector<ModuleNode[]> targets = new Vector<ModuleNode[]>(totalsize);

		// add empty vectors to the vector of vectors target
		for (int i = 0; i < totalsize; i++) {
			ModuleNode[] vec = new ModuleNode[noOfGraphs];
			targets.addElement(vec);
		}
		int currTotSize = totalsize;

		for (int i = 0; i < noOfGraphs; i++) {

			// get current graph
			ModuleGraph g = setOfModules.get(i);

			// get all nodes of the graph
			Vector<ModuleNode> nodes = assignNodeMap.get(i);

			// compute repetitions
			int size = nodes.size();
			int rep = currTotSize / size;
			int iters = totalsize / rep;

			/*
			 * System.out.println("level :" + i);
			 * System.out.println("number of nodes :" + size);
			 * System.out.println("rep size : " + rep);
			 * System.out.println("current total size :" + currTotSize);
			 * System.out.println("iterations required :" + iters);
			 */

			int last = 0;
			for (int j = 0; j < iters; j++) {
				int pos = j % size;
				ModuleNode val = nodes.get(pos);
				for (int k = last; k < last + rep; k++) {
					targets.get(k)[i] = val;
				}
				last += rep;

			}

			currTotSize = rep;

		}

		// System.out.println("printing the targets");
		Vector<ProductState> fTargets = new Vector<ProductState>(targets.size());
		for (int i = 0; i < targets.size(); i++) {

			ProductState p = new ProductState(targets.get(i));
			fTargets.add(p);
			// System.out.println(p);

		}

		targets.clear();

		assignNodeMap.clear();
		return fTargets;

	}

	private Vector<ModuleNode> getDepAssignmentNodes(Vector<ModuleNode> nodes,
			HashMap<String, HashSet<String>> mainDataDependence, HashSet<String> uses) {

		HashSet<ModuleNode> filteredNodes = new HashSet<>();

		for (ModuleNode node : nodes) {
			HashSet<ModuleEdge> edges = node.getIncoming();
			for (ModuleEdge edge : edges) {

				TFunction fun = edge.gettFunc();
				String def = fun.getDef(); // only assignment nodes will have
											// variable names
				for (String var : uses) {
					if (mainDataDependence.get(var).contains(def))
						filteredNodes.add(node);
				}

				HashMap delta = edge.getDelta();
				if (delta.containsValue(1) || delta.containsValue(-1))
					filteredNodes.add(node);
			}
		}

		Vector<ModuleNode> filteredVector = new Vector<>(filteredNodes.size());
		filteredVector.addAll(filteredNodes);

		System.out.println("final set of nodes :" + filteredNodes.size());
		System.out.println("original set of nodes :" + nodes.size());
		return filteredVector;
	}

	private Vector<ProductState> getAssignmentTargetStates(HashSet<String> modules, ArrayList<ModuleGraph> setOfModules,
			ModuleNode singleModTarget, int targetModindex) {

		HashMap<Integer, Vector<ModuleNode>> assignNodeMap = new HashMap<>();

		// compute total number of product nodes
		int noOfGraphs = setOfModules.size();
		int totalsize = 1;
		for (int i = 0; i < noOfGraphs; i++) {

			Vector<ModuleNode> nodes = new Vector<ModuleNode>();
			if (i == targetModindex) {

				nodes.addElement(singleModTarget);
				assignNodeMap.put(targetModindex, nodes);
			} else {

				nodes = getAssignmentNodes(setOfModules.get(i).getNodes());
				nodes.add(setOfModules.get(i).getStartNode());

				// if (nodes.size() == 0 | nodes.isEmpty())

				assignNodeMap.put(i, nodes);
			}

			totalsize *= nodes.size();
		}

		System.out.println("number of targets :" + totalsize);
		mainLogger.info("number of targets :" + totalsize);

		Vector<ModuleNode[]> targets = new Vector<ModuleNode[]>(totalsize);

		// add empty vectors to the vector of vectors target
		for (int i = 0; i < totalsize; i++) {
			ModuleNode[] vec = new ModuleNode[noOfGraphs];
			targets.addElement(vec);
		}
		int currTotSize = totalsize;

		for (int i = 0; i < noOfGraphs; i++) {

			// get current graph
			ModuleGraph g = setOfModules.get(i);

			// get all nodes of the graph
			Vector<ModuleNode> nodes = assignNodeMap.get(i);

			// compute repetitions
			int size = nodes.size();
			int rep = currTotSize / size;
			int iters = totalsize / rep;

			/*
			 * System.out.println("level :" + i);
			 * System.out.println("number of nodes :" + size);
			 * System.out.println("rep size : " + rep);
			 * System.out.println("current total size :" + currTotSize);
			 * System.out.println("iterations required :" + iters);
			 */

			int last = 0;
			for (int j = 0; j < iters; j++) {
				int pos = j % size;
				ModuleNode val = nodes.get(pos);
				for (int k = last; k < last + rep; k++) {
					targets.get(k)[i] = val;
				}
				last += rep;

			}

			currTotSize = rep;

		}

		// System.out.println("printing the targets");
		Vector<ProductState> fTargets = new Vector<ProductState>(targets.size());
		for (int i = 0; i < targets.size(); i++) {

			ProductState p = new ProductState(targets.get(i));
			fTargets.add(p);
			// System.out.println(p);

		}

		targets.clear();

		assignNodeMap.clear();
		return fTargets;

	}

	private Vector<ModuleNode> getAssignmentNodes(Vector<ModuleNode> nodes) {

		HashSet<ModuleNode> filteredNodes = new HashSet<>();

		for (ModuleNode node : nodes) {
			HashSet<ModuleEdge> edges = node.getIncoming();
			for (ModuleEdge edge : edges) {

				if ((edge.gettFunc() instanceof LCPIDEAssign) || (edge.gettFunc() instanceof ARAIDEAssignShortOb)
						|| edge.gettFunc() instanceof LCPIDEConst) {
					filteredNodes.add(node);
				}
				HashMap delta = edge.getDelta();
				if (delta.containsValue(1) || delta.containsValue(-1))
					filteredNodes.add(node);
			}
		}

		Vector<ModuleNode> filteredVector = new Vector<>(filteredNodes.size());
		filteredVector.addAll(filteredNodes);

		System.out.println("final set of nodes :" + filteredNodes.size());
		System.out.println("original set of nodes :" + nodes.size());
		return filteredVector;
	}

	/**
	 * This method creates the set of target nodes combining the actual target
	 * node in the graph with all other nodes in the graphs of other modules
	 */
	private Vector<ProductState> gettargetStates(HashSet<String> modules, ArrayList<ModuleGraph> setOfModules,
			ModuleNode singleModTarget, int targetModindex) {

		// compute total number of product nodes
		int noOfGraphs = setOfModules.size();
		int totalsize = 1;
		for (int i = 0; i < noOfGraphs; i++) {
			if (i == targetModindex)
				continue;
			totalsize *= setOfModules.get(i).getNodes().size();
		}

		System.out.println("number of targets :" + totalsize);
		mainLogger.info("number of targets :" + totalsize);

		Vector<ModuleNode[]> targets = new Vector<ModuleNode[]>(totalsize);

		// add empty vectors to the vector of vectors target
		for (int i = 0; i < totalsize; i++) {
			ModuleNode[] vec = new ModuleNode[noOfGraphs];
			targets.addElement(vec);
		}
		int currTotSize = totalsize;

		for (int i = 0; i < noOfGraphs; i++) {

			// get current graph
			ModuleGraph g = setOfModules.get(i);

			// get all nodes of the graph
			Vector<ModuleNode> nodes = g.getNodes();

			// if it is the graph with the actual target node, only have one
			// node in the nodes list
			if (i == targetModindex) {
				nodes = new Vector<ModuleNode>();
				nodes.addElement(singleModTarget);
			}

			// compute repetitions
			int size = nodes.size();
			int rep = currTotSize / size;
			int iters = totalsize / rep;

			/*
			 * System.out.println("level :" + i);
			 * System.out.println("number of nodes :" + size);
			 * System.out.println("rep size : " + rep);
			 * System.out.println("current total size :" + currTotSize);
			 * System.out.println("iterations required :" + iters);
			 */

			int last = 0;
			for (int j = 0; j < iters; j++) {
				int pos = j % size;
				ModuleNode val = nodes.get(pos);
				for (int k = last; k < last + rep; k++) {
					targets.get(k)[i] = val;
				}
				last += rep;

			}

			currTotSize = rep;

		}

		// System.out.println("printing the targets");
		Vector<ProductState> fTargets = new Vector<ProductState>(totalsize);
		for (int i = 0; i < totalsize; i++) {

			ProductState p = new ProductState(targets.get(i));
			fTargets.add(p);
			// System.out.println(p);

		}

		targets.clear();

		return fTargets;
	}

	private Vector<Integer> fillVector(Vector<Integer> vec, int val) {
		for (int i = 0; i < Globals.numberOfCounters; i++) {
			vec.addElement(val);

		}
		return vec;
	}

	private HashSet<String> getTStateUInfo(Vector<ProductState> targetNodes, HashMap<Long, String> useInfo) {
		HashSet<String> uinfo = new HashSet<>();

		for (ProductState stateObj : targetNodes) {
			ModuleNode[] state = stateObj.getState();
			for (ModuleNode node : state) {
				String uses = useInfo.get(node.getId());
				if (uses != null) {
					// System.out.println("found use for target " + stateObj + "
					// : " + uses);
					String[] uarr = uses.split(",");
					uinfo.addAll(Arrays.asList(uarr));
				}
			}
		}

		return uinfo;
	}

	private Vector<ProductState> gettargetStatesOld(HashSet<String> modules, ArrayList<ModuleGraph> setOfModules,
			ModuleNode singleModTarget, int index) {

		int targetSetSize = 1;
		int count = 0;
		for (ModuleGraph g : setOfModules) {
			if (count != index) {
				targetSetSize *= g.getNodes().size();
			}
		}

		count = 0;

		ArrayList<ModuleNode[]> targetsArr = new ArrayList<ModuleNode[]>(targetSetSize);

		HashSet<ArrayList<ModuleNode>> setOfNodes = new HashSet<>();
		setOfNodes.add(new ArrayList<>());

		HashSet<ArrayList<ModuleNode>> toRemove = new HashSet<>();
		HashSet<ArrayList<ModuleNode>> toAdd = new HashSet<>();
		// construct nodes
		// assumes unique IDS for all graphs

		for (ModuleGraph g : setOfModules) {

			if (count != index) {
				for (ArrayList<ModuleNode> nodes : setOfNodes) {
					for (ModuleNode node : g.getNodes()) {
						ArrayList<ModuleNode> temp = new ArrayList(nodes);
						temp.add(node);
						toAdd.add(temp);

					}
					toRemove.add(nodes);
				}
				setOfNodes.addAll(toAdd);
				setOfNodes.removeAll(toRemove);
				toRemove = new HashSet<>();
				toAdd = new HashSet<>();
			} else {
				for (ArrayList<ModuleNode> nodes : setOfNodes) {
					ArrayList<ModuleNode> temp = new ArrayList<>(nodes);
					temp.add(singleModTarget);
					toAdd.add(temp);
					toRemove.add(nodes);
				}
				setOfNodes.addAll(toAdd);
				setOfNodes.removeAll(toRemove);
				toRemove = new HashSet<>();
				toAdd = new HashSet<>();
			}
			count++;
		}

		System.out.println("size of set : " + setOfNodes.size());

		Vector<ProductState> targets = new Vector<ProductState>(targetSetSize);
		for (ArrayList<ModuleNode> nodeVector : setOfNodes) {
			ModuleNode[] nArray = new ModuleNode[Globals.numberOfModules];
			for (int i = 0; i < Globals.numberOfModules; i++) {
				nArray[i] = nodeVector.get(i);
			}

			ProductState pState = new ProductState(nArray);
			targets.add(pState);
			// System.out.println("adding product state :" + pState);
		}

		return targets;
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

	/**
	 * This method is used to initialize the vector for assertion check.
	 * 
	 * @author Animesh Kumar
	 * @param file
	 *            This is the name of the file whose assertion check is to be
	 *            performed
	 * @return Nothing.
	 */

	public void assertionCheckIntialise(String file) {

		// Initializing assertion vector for Mutual Exclusion(Critical Section)
		// benchmark
		if (file.equalsIgnoreCase("mutualexclusion")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 1;
			Globals.labelVectorsHashMap.put("L5", vector);
			Globals.labelVectorsHashMap.put("L2", vector);

		}
		// Initializing assertion vector for Server(CSAtomicViolation) benchmark
		if (file.equalsIgnoreCase("server")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = 0;
			vector[1][0] = 1;
			vector[2][0] = -1;
			vector[3][0] = 0;
			Globals.labelVectorsHashMap.put("L3", vector);
		}

		// Initializing assertion vector for RecPExample benchmark
		if (file.equalsIgnoreCase("recpexample")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 1;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 0;
			vector[6][0] = 0;
			Globals.labelVectorsHashMap.put("L1", vector);
			vector[0][0] = -2;
			vector[1][0] = 1;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 0;
			vector[6][0] = 0;
			Globals.labelVectorsHashMap.put("L2", vector);
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 1;
			vector[6][0] = 0;
			Globals.labelVectorsHashMap.put("L3", vector);
			vector[0][0] = -1;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 1;
			vector[5][0] = 0;
			vector[6][0] = 0;
			Globals.labelVectorsHashMap.put("L6", vector);
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 1;
			vector[6][0] = 0;
			Globals.labelVectorsHashMap.put("L7", vector);
		}

		// Initializing assertion vector for receive1 benchmark
		if (file.equalsIgnoreCase("receive1")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 1;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 0;

			Globals.labelVectorsHashMap.put("L1", vector);
			vector[0][0] = -2;
			vector[1][0] = 1;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 0;

			Globals.labelVectorsHashMap.put("L2", vector);
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 1;
			vector[5][0] = 0;
			Globals.labelVectorsHashMap.put("L3", vector);
			vector[0][0] = -1;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 1;
			vector[4][0] = 0;
			vector[5][0] = 0;

			Globals.labelVectorsHashMap.put("L6", vector);
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 1;
			vector[5][0] = 0;
			Globals.labelVectorsHashMap.put("L7", vector);

		}

		// Initializing assertion vector for Peterson benchmark
		if (file.equalsIgnoreCase("peterson")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 0;
			vector[2][0] = 1;
			vector[3][0] = 0;
			vector[4][0] = 0;
			Globals.labelVectorsHashMap.put("L6", vector);
			Globals.labelVectorsHashMap.put("L13", vector);
		}

		// Initializing assertion vector for barlett benchmark
		if (file.equalsIgnoreCase("bartlett")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 0;
			vector[2][0] = 1;
			vector[3][0] = 1;
			Globals.labelVectorsHashMap.put("L7", vector);
		}

		// Initializing assertion vector for dijkstra benchmark
		if (file.equalsIgnoreCase("dijkstra")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = 1;
			vector[1][0] = 1;
			Globals.labelVectorsHashMap.put("L1", vector);
			vector[0][0] = -1;
			vector[1][0] = 1;
			Globals.labelVectorsHashMap.put("L3", vector);
			vector[0][0] = -1;
			vector[1][0] = 1;
			Globals.labelVectorsHashMap.put("L6", vector);
		}

		if (file.equalsIgnoreCase("lynch")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = 1;
			vector[1][0] = -1;
			vector[2][0] = 0;
			vector[3][0] = 1;
			vector[4][0] = 0;
			vector[5][0] = 0;
			Globals.labelVectorsHashMap.put("L2", vector);
			short[][] vector2 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = 1;
			vector2[1][0] = 0;
			vector2[2][0] = 0;
			vector2[3][0] = 1;
			vector2[4][0] = -1;
			vector2[5][0] = 0;
			Globals.labelVectorsHashMap.put("L4", vector2);
		}
		if (file.equalsIgnoreCase("chameneos")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 1;
			vector[5][0] = -1;
			vector[6][0] = 0;
			vector[7][0] = 0;
			vector[8][0] = 0;
			vector[9][0] = 0;
			Globals.labelVectorsHashMap.put("L13USE", vector);
			short[][] vector2 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = 0;
			vector2[1][0] = 1;
			vector2[2][0] = -1;
			vector2[3][0] = 0;
			vector2[4][0] = 0;
			vector2[5][0] = 0;
			vector2[6][0] = 0;
			vector2[7][0] = 0;
			vector2[8][0] = 0;
			vector2[9][0] = 0;
			Globals.labelVectorsHashMap.put("L4", vector2);

		}
		if (file.equalsIgnoreCase("leader")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 1;
			vector[2][0] = 0;
			vector[3][0] = 0;
			vector[4][0] = 0;
			vector[5][0] = 0;
			vector[6][0] = 0;
			vector[7][0] = 0;
			vector[8][0] = 0;
			vector[9][0] = 0;
			vector[10][0] = 0;
			vector[11][0] = 0;
			Globals.labelVectorsHashMap.put("L20", vector);
			Globals.labelVectorsHashMap.put("L24", vector);
			Globals.labelVectorsHashMap.put("L45", vector);
			Globals.labelVectorsHashMap.put("L49", vector);

		}
		if (file.equalsIgnoreCase("boundedAsync")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 1;
			vector[3][0] = -1;
			vector[4][0] = 0;
			vector[5][0] = 0;
			Globals.labelVectorsHashMap.put("L7", vector);
			Globals.labelVectorsHashMap.put("L9", vector);
			short[][] vector2 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = -1;
			vector2[1][0] = 0;
			vector2[2][0] = 1;
			vector2[3][0] = 1;
			vector2[4][0] = 0;
			vector2[5][0] = 0;
			Globals.labelVectorsHashMap.put("L8", vector2);
			Globals.labelVectorsHashMap.put("L10", vector2);

			short[][] vector3 = new short[Globals.numberOfVariables + 1][1];
			vector3[0][0] = 0;
			vector3[1][0] = 0;
			vector3[2][0] = 0;
			vector3[3][0] = 0;
			vector3[4][0] = 1;
			vector3[5][0] = -1;
			Globals.labelVectorsHashMap.put("L15", vector3);
			Globals.labelVectorsHashMap.put("L17", vector3);
			short[][] vector4 = new short[Globals.numberOfVariables + 1][1];
			vector4[0][0] = -1;
			vector4[1][0] = 0;
			vector4[2][0] = 0;
			vector4[3][0] = 0;
			vector4[4][0] = 1;
			vector4[5][0] = 1;
			Globals.labelVectorsHashMap.put("L16", vector4);
			Globals.labelVectorsHashMap.put("L18", vector4);

		}
		if (file.equalsIgnoreCase("mutualexclusionunbounded")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = -1;
			vector[1][0] = 1;
			Globals.labelVectorsHashMap.put("L5", vector);
			Globals.labelVectorsHashMap.put("L2", vector);

		}
		if (file.equalsIgnoreCase("replicatingStorageselfasync")) {
			short[][] vector = new short[Globals.numberOfVariables + 1][1];
			vector[0][0] = 0;
			vector[1][0] = 0;
			vector[2][0] = 0;
			vector[3][0] = 1;
			Globals.labelVectorsHashMap.put("L1", vector);
		}
		if (file.equalsIgnoreCase("event_bus_test")) {
			short[][] vector1 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -2;
			vector1[1][0] = 1;
			vector1[2][0] = 0;
			vector1[3][0] = 0;

			short[][] vector2 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = -1;
			vector2[1][0] = 0;
			vector2[2][0] = 1;
			vector2[3][0] = 0;

			short[][] vector3 = new short[Globals.numberOfVariables + 1][1];
			vector3[0][0] = -2;
			vector3[1][0] = 0;
			vector3[2][0] = 0;
			vector3[3][0] = 1;

			Globals.labelVectorsHashMap.put("L1", vector1);
			Globals.labelVectorsHashMap.put("L2", vector2);
			Globals.labelVectorsHashMap.put("L3", vector3);

		}

		if (file.equalsIgnoreCase("nursery_test")) {
			short[][] vector1 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -1;
			vector1[1][0] = 1;
			vector1[2][0] = 0;

			short[][] vector2 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = -1;
			vector2[1][0] = 0;
			vector2[2][0] = 1;

			Globals.labelVectorsHashMap.put("L1", vector1);
			Globals.labelVectorsHashMap.put("L2", vector2);

		}
		if (file.equalsIgnoreCase("jobqueue_test")) {
			short[][] vector1 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -4;
			vector1[1][0] = 1;

			Globals.labelVectorsHashMap.put("L1", vector1);

		}
		if (file.equalsIgnoreCase("bookstoresimple")) {
			short[][] vector1 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -1;
			vector1[1][0] = 1;
			vector1[2][0] = 0;

			short[][] vector2 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = -2;
			vector2[1][0] = 0;
			vector2[2][0] = 1;

			short[][] vector3 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -1;
			vector1[1][0] = 1;
			vector1[2][0] = 0;

			short[][] vector4 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = 0;
			vector2[1][0] = 0;
			vector2[2][0] = 1;

			short[][] vector5 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -2;
			vector1[1][0] = 1;
			vector1[2][0] = 0;

			short[][] vector6 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = -4;
			vector2[1][0] = 0;
			vector2[2][0] = 1;

			short[][] vector7 = new short[Globals.numberOfVariables + 1][1];
			vector1[0][0] = -1;
			vector1[1][0] = 1;
			vector1[2][0] = 0;

			short[][] vector8 = new short[Globals.numberOfVariables + 1][1];
			vector2[0][0] = 0;
			vector2[1][0] = 1;
			vector2[2][0] = 0;

			Globals.labelVectorsHashMap.put("L3", vector1);
			Globals.labelVectorsHashMap.put("L4", vector2);
			Globals.labelVectorsHashMap.put("L5", vector3);
			Globals.labelVectorsHashMap.put("L6", vector4);
			Globals.labelVectorsHashMap.put("L7", vector5);
			Globals.labelVectorsHashMap.put("L8", vector6);
			Globals.labelVectorsHashMap.put("L9", vector7);
			Globals.labelVectorsHashMap.put("L10", vector8);

		}
	}

}
