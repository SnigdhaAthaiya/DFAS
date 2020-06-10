package iisc.edu.pll.analysis.forward;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.analysis.concurrent.dataflow.WorkerCounter;
import iisc.edu.pll.analysis.concurrent.dataflow.WorkerUnit;

import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel.SinglePathExtender;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.AbstractValueFactory;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;
import iisc.edu.pll.exceptions.DFRejectedException;
import iisc.edu.pll.lattice.cp.ConstantPropagation;


public class DataFlowSequential {

	private final static Logger logger = Logger.getLogger(DataFlowParallel.class);

	private HashMap<ProductState, HashMap<Configuration, AbstractValue>> pStateToValueMatrix;
	private LinkedList<PathExtender> worklist;

	public ArrayList<ModuleGraph> graphsInSystem;
	private String latticetype;
	private int cutoff;
	long startTime;
	ProductState startState;
	Configuration initLocationVec;

	private long funcSizeCount = 0;
	private double funcSizeSum = 0.0;
	private long demVectorFuncMapCount = 0;
	private double demVectorFuncmapSum = 0.0;
	private int funcSizeMax = 0;
	private int demVectorFuncMapSizemax = 0;

	private String logFileName = "";

	// this is for debugging
	public ProductState stateofInterest;
	public double newConfigs ;

	boolean debug;

	// old constructor
	public DataFlowSequential(String type, ArrayList<String> v, HashSet<String> messageSet,
			ArrayList<ModuleGraph> graphs, int cutOff, boolean debug) {

		latticetype = type;
		graphsInSystem = new ArrayList<>(graphs);
		pStateToValueMatrix = new HashMap<>();
		worklist = new LinkedList<>();
		this.cutoff = cutOff;

		this.debug = debug;

		
		ModuleNode[] startStateVec = new ModuleNode[Globals.numberOfModules];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startStateVec[i] = graphs.get(i).getStartNode();

		startState = new ProductState(startStateVec);
		initLocationVec = new Configuration(Globals.numberOfCounters);
	//	initLocationVec = fillVector(initLocationVec, 0);
		
		newConfigs = 0;

	}

	private Vector<Integer> fillVector(Vector<Integer> vec, int val) {
		
		
		for (int i = 0; i < Globals.numberOfCounters; i++) {
			vec.addElement(val);

		}
		return vec;
	}

	public void runAnalysis() {

		try {
			PropertyConfigurator.configure("log4j.properties");
			if (debug)
				logger.setLevel(Level.DEBUG);
			else
				logger.setLevel(Level.INFO);

			// initializing the main table
			HashMap<Configuration, AbstractValue> initMap = new HashMap<>();
			
			AbstractValue val = AbstractValueFactory.createAbstractValue(latticetype, false, true, 0);
			initMap.put(initLocationVec, val);
			pStateToValueMatrix.put(startState, initMap);
			
			//initialize bot value
			Globals.botVal = AbstractValueFactory.createAbstractValue(latticetype, false, false, 0);

			startTime = System.currentTimeMillis();
			// initializing the worklist with the start state
			PathExtender initEx = new PathExtender(startState, initLocationVec);
			worklist.add(initEx);

			
			logger.info("\n\nstarting analysis");
			logger.info("\nstate :" + startState);
			logger.info("\ninitial value :" + val);

			// main loop
			while (!worklist.isEmpty()) {

				PathExtender pathEx = worklist.remove();
				pathEx.run();
			}

			logger.info("stopped after " + ((System.currentTimeMillis() - startTime)) + " milliseconds");
			System.out.println("stopped after " + ((System.currentTimeMillis() - startTime)) + "milliseconds");
			System.out.println("result times");

			printStats();
			// printResult();

		} catch (Exception e) {
			System.out.println("Some other Exception" + e.getMessage());
		}

	}

	// internal class that takes a Product state and configuration vector,
	// acesses the
	// central table and extends the value of the state, vector pair to all
	// possible targets

	private void printStats() {
		int reachableState= 0, maxConfig = -1, minConfig = Integer.MAX_VALUE;
		double totalConfig =0.0, avgConfig =0.0;
		HashMap<Integer, Vector<Byte>> mode = new HashMap<>();
		
		int states = 1;
		for(ModuleGraph graph : graphsInSystem)
		{
			states *= graph.getNodes().size();
		}
		System.out.println("total number of states :" + states);
		System.out.println("total number of variables :" + Globals.numberOfVariables);
		System.out.println("total number of messages :" + Globals.numberOfCounters);
		
		
		
		reachableState = pStateToValueMatrix.size();
		for(ProductState state : pStateToValueMatrix.keySet())
		{
			int currSize = pStateToValueMatrix.get(state).size();
			if(currSize>maxConfig)
				maxConfig = currSize;
			
			if(currSize<minConfig)
				minConfig = currSize; 
			
			Integer curInt = new  Integer(currSize);
			if(!mode.containsKey(curInt))
			{
				Vector<Byte>  tally = new Vector<>();
				tally.add(new Byte((byte)1));
				mode.put(curInt, tally);			
			}
			else{
				mode.get(curInt).add((byte)1);
			}
			
			totalConfig+=currSize;
		}
		
		int mostFrequentConfig = 0;
		int freq = -1;
		for(Integer conf : mode.keySet())
		{
			int currFreq = mode.get(conf).size();
			if(currFreq > freq){
				freq = currFreq;
				mostFrequentConfig = conf;
			}
		}
				
		System.out.println("total Number of reachable states :" + reachableState);
		System.out.println("max number of configs :" + maxConfig);
		System.out.println("min number of configs :" + minConfig);
		
		System.out.println("average number of configs per state :" + (totalConfig/(reachableState*1.0)));
		System.out.println("mode of config size per state with freq :" + mostFrequentConfig +  ", " + freq);
		
		
	}

	private void printResult() {
		for (ProductState state : pStateToValueMatrix.keySet()) {
			// System.out.println("for state :" + state);
			logger.info("for state :" + state + "\n");
			HashMap<Configuration, AbstractValue> v = pStateToValueMatrix.get(state);
			for (Configuration config : v.keySet()) {
				logger.info("  " + config + ": " + v.get(config) + "\n");
				// System.out.println(" " + config + ": " + v.get(config) );
			}
		}

	}

	private class PathExtender {
		private ProductState state;
		private Configuration config;

		public PathExtender(ProductState state, Configuration config) {
			super();
			this.state = state;
			this.config = config;
		}

		public void run() {

			try {

				ModuleNode[] stateVec = state.getState();

				HashMap<Configuration, AbstractValue> cvmap = pStateToValueMatrix.get(state);
				AbstractValue currentValue = cvmap.get(config);

				if (currentValue == null)
					System.out.println("found it");

				// extending to one step successors
				for (int i = 0; i < Globals.numberOfModules; i++) {
					Set<ModuleEdge> outEdges = graphsInSystem.get(i).getOutEdges(stateVec[i]);
					for (ModuleEdge e : outEdges) {
						TFunction edgeFunction = e.gettFunc();

						AbstractValue newValue = edgeFunction.apply(currentValue);
						if(newValue == Globals.botVal)
						{
							//not propagating bot value
							continue;
						}

						Configuration succConfig = computeConfig(e.getDelta());
						if (succConfig == null) {
							// invalid configuration
							continue;
						}

						ModuleNode[] succ = Arrays.copyOf(stateVec, Globals.numberOfModules);
						succ[i] = e.getTo();
						ProductState succState = new ProductState(succ);

						HashMap<Configuration, AbstractValue> oldMap = pStateToValueMatrix.get(succState);
						if (oldMap == null) {
							HashMap<Configuration, AbstractValue> newMap = new HashMap<>();
							newMap.put(succConfig, newValue);
							pStateToValueMatrix.put(succState, newMap);
							PathExtender newPathEx = new PathExtender(succState, succConfig);
							// System.out.println("adding new map to " +
							// succState +", " + succConfig +": " + newValue);
							// logger.info("adding new map to " + succState +",
							// " + succConfig +": " + newValue +"\n");
							worklist.add(newPathEx);
							
							//first config in the map
							newConfigs++;

						} else {
							AbstractValue oldValue = oldMap.get(succConfig);
							
							if (oldValue == null) {
								oldMap.put(succConfig, newValue);
								pStateToValueMatrix.put(succState, oldMap);
								PathExtender newPathEx = new PathExtender(succState, succConfig);
								// System.out.println("adding new config to " +
								// succState+", " +succConfig+ ": " + newValue);
								// logger.info("adding new config to " +
								// succState+", " +succConfig+ ": " + newValue +
								// "\n");
								worklist.add(newPathEx);
								
								//new config in a map
								newConfigs++;

							} else {
								AbstractValue joinedValue = oldValue.join(newValue);
								if (joinedValue.isGreater(oldValue) && !oldValue.isGreater(joinedValue)) {
									// this state needs to be enqueued
									oldMap.put(succConfig, joinedValue);
									pStateToValueMatrix.put(succState, oldMap);
									PathExtender newPathEx = new PathExtender(succState, succConfig);
									// System.out.println("incrementing existing
									// value of "+ succState+", " +succConfig+
									// ": " + newValue);
									// logger.info("incrementing existing value
									// of "+ succState+", " +succConfig+ ": " +
									// newValue +"\n");
									worklist.add(newPathEx);
								} else {

									// logger.info("not adding to " +
									// succState+", " +succConfig+ ": " +
									// newValue + "\n");
									// System.out.println("not adding to " +
									// succState+", " +succConfig+ ": " +
									// newValue);
								}

							}

						}
						
						if((newConfigs % 1000)==0)
						{
							logger.info("current config value at time elapsed" + ((System.currentTimeMillis()- startTime)/1000)+ "sec :" + newConfigs+ "\n");
						}

					}
				}

			} catch (Exception e) {
				System.out.println(" failed with " + state + " and " + config + " at "
						+ (System.currentTimeMillis() - startTime) + " ms elapsed");
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}

		private Configuration computeConfig(HashMap<String, Integer> delta) {
			Vector<Integer> newConfigVector = new Vector<>(Globals.numberOfCounters);
			byte[] newConfigArr = new byte[Globals.numberOfCounters];
			
			
			if (cutoff == 0) {
				
				return new Configuration(newConfigArr);
			}
			
			if(isZeroVector(delta)){
				return config;
			}

			int i = 0;
			for (String msg : delta.keySet()) {
				int del = delta.get(msg); // edge delta
				int conVal = config.getConfAt(i);

				byte newConfig = 0;

				if (conVal == cutoff) {
					newConfig = (byte)cutoff;

				} else {
					newConfig = (byte)(conVal + del);
				}
				// the new config can be negative, return null
				if (newConfig < 0)
					return null;
				newConfigArr[i] = newConfig;
				i++;
			}

			return  new Configuration(newConfigArr);

		}

		private boolean isZeroVector(HashMap<String, Integer> delta) {
			boolean flag = true;
			
			for(String key : delta.keySet())
			{
				if(delta.get(key).byteValue()!=0){
					flag =false;
					break;
				}
				
			}
			return flag;
		}

	}

	public AbstractValue getResult(int graph, ModuleNode node) {
		// System.out.println(pStateToPathFuncMatrix.get(init));
		// TODO get a bot abstract value here which is non-null
		AbstractValue result = AbstractValueFactory.createAbstractValue(latticetype, false, false, 0);
		for (ProductState state : pStateToValueMatrix.keySet()) {
			ModuleNode[] stateVec = state.getState();
			if (stateVec[graph].equals(node)) {
				// found a state for which the module node is a component
				HashMap<Configuration, AbstractValue> map = pStateToValueMatrix.get(state);
				for (Configuration vec : map.keySet()) {
					result = result.join(map.get(vec));
				}
			}
		}
		return result;

	}

}
