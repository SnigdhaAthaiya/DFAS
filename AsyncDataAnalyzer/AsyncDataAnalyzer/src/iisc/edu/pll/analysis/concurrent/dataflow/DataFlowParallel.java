package iisc.edu.pll.analysis.concurrent.dataflow;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.assertions.ConditionCheck;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.NodePair;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.VarPair;
import iisc.edu.pll.data.lattice.reachingdef.RDSingle;
import iisc.edu.pll.data.lattice.reachingdef.RDValue;
import iisc.edu.pll.data.lattice.reachingdef.VarRHS;
import iisc.edu.pll.exceptions.DFRejectedException;

public class DataFlowParallel {

	private final static Logger logger = Logger.getLogger(DataFlowParallel.class);

	private ConcurrentHashMap<ProductState, ConcurrentHashMap<Vector<Integer>, TFunction>> pStateToPathFuncMatrix;

	public ArrayList<ModuleGraph> graphsInSystem;
	private String latticetype;
	private ArrayList<String> vars;
	private HashSet<String> messages;
	long startTime;

	// thread pool management
	private int corePoolSize = 32;
	private int maxPoolSize = 64;
	long generalKeepAliveTime = 10000; // keep alive for threads

	ThreadPoolExecutor pool;

	private int timeout = 1;
	private final int POOLSHUTDOWNTIMEOUT = 5;

	private WorkerCounter wcounter;
	private long sleepTime = 1000; // in ms

	private WorkerCounter stepCounter;

	private final int INITSIZE = 3; // initial size of each array

	private long funcSizeCount = 0;
	private double funcSizeSum = 0.0;
	private long demVectorFuncMapCount = 0;
	private double demVectorFuncmapSum = 0.0;
	private int funcSizeMax = 0;
	private int demVectorFuncMapSizemax = 0;

	private String logFileName = "";

	// this is for debugging
	public ProductState stateofInterest;

	Vector<ProductState> targets;

	Globals.Mode runMode;
	boolean debug;

	// Data Structures required for early termination - no state space pruning
	ProductState init;
	Vector<Integer> initLocationVec;
	ConditionCheck checkOb;

	// old constructor
	public DataFlowParallel(String type, ArrayList<String> v, HashSet<String> messageSet, ArrayList<ModuleGraph> graphs,
			Vector<ProductState> targets, Mode mode, boolean debug) {
		latticetype = type;
		vars = new ArrayList<>(v);
		messages = new HashSet<>(messageSet);
		graphsInSystem = new ArrayList<>(graphs);
		pStateToPathFuncMatrix = new ConcurrentHashMap<>();

		this.targets = new Vector<>(targets);
		pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, generalKeepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
		pool.allowCoreThreadTimeOut(true);
		pool.setRejectedExecutionHandler(new DFRejectedException());

		this.wcounter = new WorkerCounter();
		this.debug = debug;

		this.stepCounter = new WorkerCounter();

		System.out.println("mode :" + mode);
		runMode = mode;

		ModuleNode[] startState = new ModuleNode[Globals.numberOfModules];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startState[i] = graphs.get(i).getStartNode();

		init = new ProductState(startState);
		// get result of init state with 0 demand
		initLocationVec = new Vector<>(Globals.numberOfCounters);
		initLocationVec = fillVector(initLocationVec, 0);

	}

	public DataFlowParallel(String type, ArrayList<String> v, HashSet<String> messageSet, ArrayList<ModuleGraph> graphs,
			Vector<ProductState> targets, Mode mode, boolean debug, ConditionCheck checkOb) {
		latticetype = type;
		vars = new ArrayList<>(v);
		messages = new HashSet<>(messageSet);
		graphsInSystem = new ArrayList<>(graphs);
		pStateToPathFuncMatrix = new ConcurrentHashMap<>();

		this.targets = new Vector<>(targets);
		pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, generalKeepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
		pool.allowCoreThreadTimeOut(true);
		pool.setRejectedExecutionHandler(new DFRejectedException());

		this.wcounter = new WorkerCounter();
		this.debug = debug;

		this.stepCounter = new WorkerCounter();

		System.out.println("mode :" + mode);
		runMode = mode;

		ModuleNode[] startState = new ModuleNode[Globals.numberOfModules];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startState[i] = graphs.get(i).getStartNode();

		init = new ProductState(startState);
		// get result of init state with 0 demand
		initLocationVec = new Vector<>(Globals.numberOfCounters);
		initLocationVec = fillVector(initLocationVec, 0);
		this.checkOb = checkOb;

	}

	public void runAnalysis() {

		try {
			PropertyConfigurator.configure("log4j.properties");
			if (debug)
				logger.setLevel(Level.DEBUG);
			else
				logger.setLevel(Level.INFO);

			startTime = System.currentTimeMillis();
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			bean.setThreadContentionMonitoringEnabled(true);

			RDValue initVal = new RDValue();
			if (latticetype.equals("rd")) {
				for (String var : Globals.rdmap.keySet()) {
					LinkedList<RDSingle> v = new LinkedList();
					v.add(new RDSingle(var, new VarRHS(var)));
					initVal.setValueAt(Globals.rdmap.get(var), v);
				}
			}

			// initializing with all target states
			for (ProductState target : targets) {
				Vector<Integer> initLocationVec = new Vector<>(Globals.numberOfCounters);
				initLocationVec = fillVector(initLocationVec, 0);

				TFunction emptyFunc = null;
				if (latticetype.equals("rd")) {
					emptyFunc = initVal;
				} else {
					emptyFunc = TFunctionFactory.createFunction(latticetype, Statement.ID, vars, new ArrayList<>());
				}
				ConcurrentHashMap<Vector<Integer>, TFunction> init = new ConcurrentHashMap<Vector<Integer>, TFunction>();
				init.put(initLocationVec, emptyFunc);

				pStateToPathFuncMatrix.put(target, init);
				wcounter.incCount();

			}
			logger.info("\n\nstarting analysis for use " + checkOb.getVaruse());

			// starting work
			for (ProductState target : targets) {
				Vector<Integer> initLocationVec = new Vector<>(Globals.numberOfCounters);
				initLocationVec = fillVector(initLocationVec, 0);
				WorkerUnit w = new WorkerUnit(target, initLocationVec);
				SinglePathExtender worker = new SinglePathExtender(w);
				pool.execute(worker);

			}
			System.out.println("map intialized");
			// emptying vector
			targets = null;

			// waiting for termination
			while (!wcounter.isZero()) {
				Thread.sleep(sleepTime);
				/*
				 * System.out.println("size of table :" +
				 * pStateToPathFuncMatrix.size());
				 * System.out.println("max number of edges added to function :"
				 * + funcSizeMax);
				 * System.out.println("max number of vec-> func edges :" +
				 * demVectorFuncMapSizemax);
				 */
			}

			// pool.awaitTermination(timeout, TimeUnit.SECONDS); // interanlly
			logger.info("stopped after " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
			// System.out.println("checking for deadlocks");
			// checkForDeadlock(bean);
			System.out.println("Shutting down pool");
			pool.shutdown(); // ensure pool is closed
			pool.awaitTermination(POOLSHUTDOWNTIMEOUT, TimeUnit.MINUTES);// shut

			System.out.println("result times");
			logger.info("number of reachable states : " + pStateToPathFuncMatrix.keySet().size());
			printStatistics();
			// writeResultToFile("tableValues.txt");

		} catch (SecurityException se) {
			System.out.println("related to threadpool : " + se.getMessage());
		} catch (InterruptedException ie) {
			System.out.println("main thread interrupted :" + ie.getMessage());
		} catch (Exception e) {
			System.out.println("Some other Exception" + e.getMessage());
		}

	}

	private void printStatistics() {
		double funcSizeAvg = funcSizeSum / funcSizeCount;
		double demVectorFuncmapAvg = demVectorFuncmapSum / demVectorFuncMapCount;

		/*
		 * System.out.println("number of func added to map : " + funcSizeCount);
		 * System.out.println("total number of fun edges : " + funcSizeSum);
		 * System.out.println("total number of vecfunc added to map: " +
		 * demVectorFuncMapCount);
		 * System.out.println("total number of vecfunc edges added to map :" +
		 * demVectorFuncmapSum);
		 * System.out.println("Average number of edges in a function :" +
		 * funcSizeAvg); System.out.
		 * println("Average number of vector --> function rows per map :" +
		 * demVectorFuncmapAvg); System.out.println("max number of func edges :"
		 * + funcSizeMax);
		 * System.out.println("max number of vec --> func rows in a map :" +
		 * demVectorFuncMapSizemax);
		 */

		logger.info("number of func added to map : " + funcSizeCount);
		logger.info("total number of fun edges : " + funcSizeSum);
		logger.info("total number of vecfunc added to map: " + demVectorFuncMapCount);
		logger.info("total number of vecfunc edges added to map :" + demVectorFuncmapSum);
		logger.info("Average number of edges in a function :" + funcSizeAvg);
		logger.info("Average number of vector --> function rows per map :" + demVectorFuncmapAvg);
		logger.info("max number of func edges :" + funcSizeMax);
		logger.info("max number of vec --> func rows in a map :" + demVectorFuncMapSizemax);

	}

	private Vector<Integer> fillVector(Vector<Integer> vec, int val) {
		for (int i = 0; i < Globals.numberOfCounters; i++) {
			vec.addElement(val);

		}
		return vec;
	}

	private void checkForDeadlock(ThreadMXBean bean) {
		try {
			Thread.sleep(2000);

			long ids[] = bean.findDeadlockedThreads();

			if (ids != null) {
				ThreadInfo threadInfo[] = bean.getThreadInfo(ids);

				for (ThreadInfo threadInfo1 : threadInfo) {
					System.out.println(threadInfo1.getThreadId());

					System.out.println(threadInfo1.getThreadName());

					System.out.println(threadInfo1.getLockName());

					System.out.println(threadInfo1.getLockOwnerId());

					System.out.println(threadInfo1.getLockOwnerName());

					StackTraceElement[] stack = threadInfo1.getStackTrace();
					System.out.println("Stack");
					for (int i = 0; i < stack.length; i++)
						System.out.println(stack[i]);

					System.out.println("number of threads deadlocked :" + ids.length);
				}
			} else {
				System.out.println("No Deadlocked Threads");
			}
		} catch (Exception e) {
			System.out.println("error while checking for deadlock :" + e.getMessage());
		}

	}

	/**
	 * This method takes as input the number of messages and an initial count of
	 * how high the demand could go This will create all sets of product states
	 * and initialize the map with empty JoinedFunctionMatrix
	 */
	private void initializePStateToPathFuncMatrix(int numberOfCounters, int maxSize) {

		HashSet<ArrayList<ModuleNode>> setOfNodes = new HashSet<>();
		setOfNodes.add(new ArrayList<>());

		HashSet<ArrayList<ModuleNode>> toRemove = new HashSet<>();
		HashSet<ArrayList<ModuleNode>> toAdd = new HashSet<>();
		// construct nodes
		// assumes unique IDS for all graphs
		for (ModuleGraph g : graphsInSystem) {
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
		}

		System.out.println("size of set : " + setOfNodes.size());

		// for each node vector, we create a product state and put it in the map
		// with a matrix
		for (ArrayList<ModuleNode> nodeVector : setOfNodes) {
			ModuleNode[] nArray = new ModuleNode[Globals.numberOfModules];
			for (int i = 0; i < Globals.numberOfModules; i++) {
				nArray[i] = nodeVector.get(i);
			}

			ProductState pState = new ProductState(nArray);
			int[] sizes = new int[Globals.numberOfCounters];
			Arrays.fill(sizes, INITSIZE);
			// pStateToPathFuncMatrix.put(pState,
			// JoinedFunctionMatrixFactory.getJoinedFunctionMatrix(sizes));

			// pStateToPathFuncMatrix.put(pState, null);
		}

	}

	public void writeResultToFile(String filename) {
		FileWriter writer = null;
		BufferedWriter bwriter = null;
		try {
			writer = new FileWriter(filename);
			bwriter = new BufferedWriter(writer);
			StringBuilder content = new StringBuilder();
			bwriter.write("The final set of paths per pair of program points \n");
			bwriter.write("total number of bins :" + pStateToPathFuncMatrix.keySet().size() + "\n");
			bwriter.write("\n\n");
			for (ProductState state : pStateToPathFuncMatrix.keySet()) {
				bwriter.write(state.toString() + " :\n");
				bwriter.write("-----------------------------------------------\n");
				bwriter.write(pStateToPathFuncMatrix.get(state).toString() + "\n");
				bwriter.write("-----------------------------------------------\n\n");

			}

			bwriter.write("\n\n------------------------------------------------");

			bwriter.close();
			writer.close();
		} catch (IOException ioe) {
			System.out.println("error while writing to file " + filename);
		} finally {
			try {
				if (bwriter != null && writer != null) {
					bwriter.close();
					writer.close();
				}
			} catch (IOException ioe) {
				System.out.println("error while closing");
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * this internal class is responsible for extending path in a bin. it has
	 * access to both CoverPaths, graphsInSystem and worklist and can modify
	 * those concurrently Additionally this can create more runnables for
	 * extending paths
	 */
	public class SinglePathExtender implements Runnable {

		private WorkerUnit toExtend;

		public WorkerUnit getToExtend() {
			return toExtend;
		}

		public void setToExtend(WorkerUnit toExtend) {
			this.toExtend = toExtend;
		}

		public SinglePathExtender(WorkerUnit toExtend) {

			this.toExtend = toExtend;
		}

		boolean flag = false;

		@Override
		public void run() {
			// System.out.println("starting work with " + toExtend);
			try {

				/*
				 * logger.debug("Thread " + Thread.currentThread().getId() +
				 * " starting work with " + toExtend.toString() + " at " +
				 * (System.currentTimeMillis() - startTime) + " ms elapsed");
				 */

				ProductState targetState = toExtend.getState();
				ModuleNode[] target = targetState.getState();

				if (targetState.equals(init)) {

					// logger.info("Start state value after time " +
					// (System.currentTimeMillis() - startTime)+ " ms : "+
					// pStateToPathFuncMatrix.get(init).get(initLocationVec)
					// +"\n" );

					// logger.info("check message :" +
					// checkOb.check(pStateToPathFuncMatrix.get(init).get(initLocationVec)));

					// stop if check returns true
					if (pStateToPathFuncMatrix != null && pStateToPathFuncMatrix.get(init) != null) {
						TFunction f = pStateToPathFuncMatrix.get(init).get(initLocationVec);
						if (f != null) {
							if (checkOb.check(f))
								wcounter.setCountToZeroAndStop();
						}
					}

				}

				Vector<Integer> tDemandVector = toExtend.getDemand();

				
				ConcurrentHashMap<Vector<Integer>, TFunction> jfmap = pStateToPathFuncMatrix.get(targetState);

				TFunction funcToExtend = null;
				if (jfmap == null)
					return;
				synchronized (jfmap) {
					funcToExtend = jfmap.get(tDemandVector);

				}

				/*
				 * logger.debug( "Thread " + Thread.currentThread().getId() +
				 * " with " + targetState + " extending function " +
				 * funcToExtend + " at " + (System.currentTimeMillis() -
				 * startTime) + " ms elapsed");
				 */
				for (int i = 0; i < Globals.numberOfModules; i++) {
					Set<ModuleEdge> inEdges = graphsInSystem.get(i).getInEdges(target[i]);
					for (ModuleEdge e : inEdges) {

						TFunction sourceNewFunc = e.gettFunc().compose(funcToExtend);

						/*
						 * logger.debug("Thread " +
						 * Thread.currentThread().getId() + " with " +
						 * toExtend.toString() + ", newly extended function " +
						 * sourceNewFunc + " at " + (System.currentTimeMillis()
						 * - startTime) + " ms elapsed");
						 */

						Vector<Integer> sDemandVector = composeDemand(e.getDelta(), tDemandVector);
						// Vector<Integer> sDemandVector =
						// abstractComposeDemand(e.getDelta(), tDemandVector);
						ModuleNode[] source = Arrays.copyOf(target, Globals.numberOfModules);
						source[i] = e.getFrom();
						ProductState sourceState = new ProductState(source);

						Vector<Integer> zero = new Vector<>(Globals.numberOfCounters);
						fillVector(zero, 0);

						//logger.debug("source vector : " + sDemandVector);

					//	if (source[2].getId() == 55 && target[2].getId() == 57) {
					//		System.out.println("found assignment");
					//	}
						/*
						 * if(sourceState.equals(init)) {
						 * System.out.println("extending to source"); }
						 */
						/*
						 * if ((source[0].getId() == 0 && source[1].getId() ==
						 * 291)) { logger.debug("Thread " +
						 * Thread.currentThread().getId() + " with " +
						 * toExtend.toString() + ", newly extended function " +
						 * sourceNewFunc + " at " + (System.currentTimeMillis()
						 * - startTime) + " ms elapsed");
						 * logger.debug("Demand :" + sDemandVector); flag =
						 * true;
						 * 
						 * }
						 */
						// [20, 24, 54][ 0, 0, 0, 1, 0, 0]

						/*
						 * if (source[0].getId() == 77 && source[1].getId() ==
						 * 443 ) System.out.println("found it");
						 */

						if (!isCovered(sourceNewFunc, sDemandVector, sourceState)) {
							// logger.info(sourceState+ ", " + sDemandVector+ ":
							// " +sourceNewFunc );
							/*
							 * logger.debug("Thread " +
							 * Thread.currentThread().getId() + " with " +
							 * toExtend.toString() +
							 * ", newly extended function " + sourceNewFunc +
							 * " at " + (System.currentTimeMillis() - startTime)
							 * + " ms elapsed is not covered");
							 * logger.debug("Demand :" + sDemandVector);
							 * logger.debug(sourceState);
							 */

							/*
							 * if ((source[0].getId() == 0 && source[1].getId()
							 * == 291)) { logger.debug("Thread " +
							 * Thread.currentThread().getId() +
							 * "extended function not covered \n");
							 * 
							 * }
							 */
							/*
							 * logger.debug("Thread " +
							 * Thread.currentThread().getId() +
							 * " extended function not covered");
							 */

							/*
							 * if ((source[0].getId() == 0 && source[1].getId()
							 * == 210)) { logger.debug("Thread " +
							 * Thread.currentThread().getId() +
							 * " will add function " + sourceNewFunc +
							 * "\n at demand :" + sDemandVector);
							 * 
							 * }
							 */
							ConcurrentHashMap<Vector<Integer>, TFunction> sourceMap = pStateToPathFuncMatrix
									.get(sourceState);
							ConcurrentHashMap<Vector<Integer>, TFunction> oldSourceMap = null;

							if (sourceMap == null) {
								sourceMap = new ConcurrentHashMap<Vector<Integer>, TFunction>();
								oldSourceMap = sourceMap;
								sourceMap = pStateToPathFuncMatrix.putIfAbsent(sourceState, sourceMap);
								if (sourceMap == null)
									sourceMap = oldSourceMap; // the new matrix
																// was put, else
																// the older one
																// in place
							}

							boolean flag = false;
							TFunction newFunc = null;
							synchronized (sourceMap) {
								TFunction oldValFunction = sourceMap.putIfAbsent(sDemandVector, sourceNewFunc);
								if (oldValFunction != null) {
									newFunc = oldValFunction.join(sourceNewFunc);
									flag = true;
									/*
									 * logger.debug("Thread " +
									 * Thread.currentThread().getId() +
									 * " setting the new function " + newFunc +
									 * (System.currentTimeMillis() - startTime)
									 * + " ms elapsed");
									 */
									sourceMap.put(sDemandVector, newFunc);
								}
							}

							// bookkeeping
							if (flag) {
								// added new function
								funcSizeCount++;
								demVectorFuncMapCount++;

								int funcSize = newFunc.getSize();
								int mapSize = sourceMap.size();

								demVectorFuncmapSum += mapSize;
								funcSizeSum += funcSize;
								if (funcSize > funcSizeMax)
									funcSizeMax = funcSize;

								if (mapSize > demVectorFuncMapSizemax)
									demVectorFuncMapSizemax = mapSize;
							} else {

								funcSizeCount++;
								demVectorFuncMapCount++;

								int funcSize = sourceNewFunc.getSize();
								int mapSize = sourceMap.size();

								demVectorFuncmapSum += mapSize;
								funcSizeSum += funcSize;
								if (funcSize > funcSizeMax)
									funcSizeMax = funcSize;

								if (mapSize > demVectorFuncMapSizemax)
									demVectorFuncMapSizemax = mapSize;

							}

							/*
							 * if(funcSizeCount%1000==0) {
							 * logger.info("time elapsed " +
							 * ((System.currentTimeMillis()- startTime)/1000)+
							 * "sec \n"); }
							 */
							wcounter.incCount();
							WorkerUnit wu = new WorkerUnit(sourceState, sDemandVector);

							/*
							 * logger.debug("Thread " +
							 * Thread.currentThread().getId() +
							 * " added function, spawning worker " +
							 * wu.toString() + " at " +
							 * (System.currentTimeMillis() - startTime) +
							 * " ms elapsed");
							 */
							SinglePathExtender worker = new SinglePathExtender(wu);
							pool.execute(worker);

						} else {

							/*
							 * logger.debug("Thread " +
							 * Thread.currentThread().getId() + " function " +
							 * sourceNewFunc + " is already covered for " +
							 * sourceState + " at " +
							 * (System.currentTimeMillis() - startTime) +
							 * " ms elapsed");
							 */

						}

					}
				}
				/*
				 * logger.debug("Thread " + Thread.currentThread().getId() +
				 * " terminated work with " + toExtend.toString() + " at " +
				 * (System.currentTimeMillis() - startTime) + " ms elapsed");
				 */

			} catch (Throwable e) {
				logger.debug("Thread " + Thread.currentThread().getId() + " failed with " + toExtend.toString() + " at "
						+ (System.currentTimeMillis() - startTime) + " ms elapsed");
				logger.debug(e.getMessage());
				logger.debug(e.getStackTrace());
				System.out.println("Thread " + Thread.currentThread().getId() + " failed with " + toExtend.toString()
						+ " at " + (System.currentTimeMillis() - startTime) + " ms elapsed");
				e.printStackTrace();
				System.out.println(e.getMessage());
			}

			finally {
				wcounter.decCount();
				toExtend = null;// freeing the data structures

			}
		}

		private Vector<Integer> abstractComposeDemand(HashMap<String, Integer> delta, Vector<Integer> demand) {

			Vector<Integer> newDemand = new Vector<>(Globals.numberOfCounters);

			if (runMode == Mode.NAIVE) {
				for (int i = 0; i < Globals.numberOfCounters; i++) {
					newDemand.addElement(0);
				}
				return newDemand;
			}

			int i = 0;
			for (String msg : delta.keySet()) {
				int del = delta.get(msg); // edge delta
				int dem = demand.get(i);
				int newDem = dem - del;
				newDem = newDem >= 0 ? newDem : 0;
				if (newDem > 1) {
					// System.out.println("found higher than 1 demand :" +
					// newDem);
					newDem = 1; // optimization added
				}

				newDemand.addElement(newDem);
				i++;
			}

			return newDemand;

		}

		private boolean isCovered(TFunction newFunc, Vector<Integer> dVector, ProductState source) {

			TFunction joinedFunc = TFunctionFactory.createFunction(latticetype, Statement.EMPTY, vars,
					new ArrayList<>());

			ConcurrentHashMap<Vector<Integer>, TFunction> sourceMap = pStateToPathFuncMatrix.get(source);

			if (sourceMap == null)
				return false; // entry not present in map

			long starttime = System.currentTimeMillis();
			ConcurrentHashMap<Vector<Integer>, TFunction> localMap = new ConcurrentHashMap<Vector<Integer>, TFunction>(
					sourceMap);

			List<TFunction> lowerDemandFuncs = getAllLower(localMap, dVector);

			if (lowerDemandFuncs.isEmpty())
				return false;

			/*
			 * logger.debug("Thread " + Thread.currentThread().getId() +
			 * " getallLower took " + (System.currentTimeMillis() - starttime) +
			 * "ms");
			 */

			for (TFunction ldFunc : lowerDemandFuncs) {

				joinedFunc = joinedFunc.join(ldFunc);

			}

			/*
			 * logger.debug("Thread " + Thread.currentThread().getId() +
			 * " joined function value " + joinedFunc + ", at " +
			 * (System.currentTimeMillis() - startTime) + " ms elapsed");
			 */
			// flag = false;

			if (joinedFunc.isGreater(newFunc))
				return true;

			return false;
		}

		private List<TFunction> getAllLower(ConcurrentHashMap<Vector<Integer>, TFunction> sourceMap,
				Vector<Integer> dVector) {
			List<TFunction> ldFuncs = new ArrayList<>();
			for (Vector<Integer> vec : sourceMap.keySet()) {
				if (isLower(vec, dVector))
					ldFuncs.add(sourceMap.get(vec));
			}

			return ldFuncs;

		}

		// no need to lock dvector, as the vector content does not change
		private boolean isLower(Vector<Integer> vec, Vector<Integer> dVector) {

			for (int i = 0; i < Globals.numberOfCounters; i++) {
				if (vec.get(i) > dVector.get(i))
					return false;
			}
			return true;
		}

		private Vector<Integer> composeDemand(HashMap<String, Integer> delta, Vector<Integer> demand) {
			Vector<Integer> newDemand = new Vector<>(Globals.numberOfCounters);

			if (runMode == Mode.NAIVE) {
				for (int i = 0; i < Globals.numberOfCounters; i++) {
					newDemand.addElement(0);
				}
				return newDemand;
			}

			int i = 0;
			for (String msg : delta.keySet()) {
				int del = delta.get(msg); // edge delta
				int dem = demand.get(i);
				int newDem = dem - del;
				newDem = newDem >= 0 ? newDem : 0;
				newDemand.addElement(newDem);
				i++;
			}

			return newDemand;
		}

	}

	public TFunction getResult(ProductState init, Vector<Integer> initLocationVec) {
		// System.out.println(pStateToPathFuncMatrix.get(init));
		return pStateToPathFuncMatrix.get(init).get(initLocationVec);

	}

	public void clean() {
		pStateToPathFuncMatrix.clear();
		vars.clear();
		messages.clear();
		// targets.clear();
		pool = null;

	}

}
