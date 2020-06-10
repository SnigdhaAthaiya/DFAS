package iisc.edu.pll.analysis.forward.concurrent.simple;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import akka.actor.dungeon.*;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import akka.actor.typed.javadsl.TimerScheduler;
import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.AbstractValueFactory;
import iisc.edu.pll.data.lattice.TFunction;

public class Supervisor extends AbstractBehavior<Supervisor.Command> {

	public interface Command {
	}

	public static enum StartAnalysis implements Command {
		INSTANCE
	}

	public static enum Yeah implements Command {
		INSTANCE
	}

	public enum Timeout implements Command {
		INSTANCE
	}

	public enum GetVal implements Command {
		INSTANCE
	}
	public enum Finished implements Command {
		INSTANCE
	}

	public static final class ComputedVal implements Command {
		public final AbstractValue val;

		public ComputedVal(AbstractValue v) {
			val = v;
		}

	}

	// local state

	boolean stillActive;
	String type;
	ArrayList<ModuleGraph> graphs;
	int cutOff;
	TimerScheduler<Command> timers;
	Duration timeout;
	ArrayList<VarUseInfo> totalvarUse;
	HashMap<ProductState, ActorRef<Worker.Command>> pStateToActorRef;
	HashSet<String> modules;
	long actorsStartTime;
	long resultCollectionTime;
	long timeoutTime;

	int productStateCounter;
	AbstractValue resultVal;
	int numOfConsts;
	VarUseInfo currentVuse;
	

	
	private final static Logger logger = Logger.getLogger(DataFlowParallel.class);
	
	public static Behavior<Command> create(Duration timeout, String type, ArrayList<ModuleGraph> graphs, int cutOff,
			ArrayList<VarUseInfo> varuse, HashSet<String> modules) {
		
		return Behaviors.setup(context -> (Behaviors.withTimers(
				timers -> new Supervisor(context, timers, timeout, type, graphs, cutOff, varuse, modules))));
	/*	return Behaviors.setup(context -> (Behaviors.withTimers(
				timers -> new Supervisor(context, timers, timeout, type, graphs, cutOff, varuse, modules))));*/
	}

	private Supervisor(ActorContext<Command> context, TimerScheduler<Command> timers, Duration timeout, String type,
			ArrayList<ModuleGraph> graphs, int cutOff, ArrayList<VarUseInfo> vuse, HashSet<String> modules) {
		super(context);
		// timers.startTimerWithFixedDelay(Timeout.INSTANCE, timeout);
		this.timers = timers;
		stillActive = true;
		this.type = type;
		this.graphs = new ArrayList<>(graphs);
		this.cutOff = cutOff;
		this.timeout = timeout;
		this.totalvarUse = new ArrayList<>(vuse);
		pStateToActorRef = new HashMap<>();
		this.productStateCounter = 0;
		this.resultVal = AbstractValueFactory.createAbstractValue(type, false, false, 0);
		this.modules = new HashSet<>(modules);
		this.timeoutTime = 0;
		logger.setLevel(Level.INFO);
		this.numOfConsts =0;
	}

	@Override
	public Receive<Command> createReceive() {
		// TODO Auto-generated method stub

		return newReceiveBuilder().onMessage(StartAnalysis.class, unUsed -> onStartAnalysis())
				.onMessage(GetVal.class, unUsed -> onGetVal()).onMessage(ComputedVal.class, this::onComputedVal)
				.onMessage(Finished.class, unUsed -> onFinished())
				.build();
	}


	private Behavior<Command> onStartAnalysis() {
		// create product states and actors
		System.out.println("Supervisor started analysis");
		actorsStartTime = System.currentTimeMillis();
		
		long actorCreationTime =System.currentTimeMillis();
		createAllStatesAndActors(modules, graphs);
		System.out.println("Created Actors in :" + (System.currentTimeMillis()- actorCreationTime)/1000 + "sec" );
		logger.info("Created Actors in :" + (System.currentTimeMillis()- actorCreationTime)/1000 + "sec\n" );
		logger.info("Created Actors in :" + (System.currentTimeMillis()- actorCreationTime) + "msec\n" );

		
		long succActorListTime = System.currentTimeMillis();
		// create succ lists
		createSuccListsandInit();
		System.out.println("Created Successor Actors in :" + (System.currentTimeMillis()- succActorListTime)/1000 + "sec" );
		logger.info("Created Successor in :" + (System.currentTimeMillis()- succActorListTime)/1000 + "sec\n" );


		// start worker
		AbstractValue initVal =AbstractValueFactory.createAbstractValue(type, false, true, 0);
		ModuleNode[] startStateVec = new ModuleNode[graphs.size()];

		for (int i = 0; i < Globals.numberOfModules; i++)
			startStateVec[i] = graphs.get(i).getStartNode();

		ProductState startState = new ProductState(startStateVec);
		Vector<Integer> initLocationVec = new Vector<>(Globals.numberOfCounters);
		initLocationVec = fillVector(initLocationVec, 0);
		
		ActorRef<Worker.Command> startRef = pStateToActorRef.get(startState);
		
		startRef.tell(Worker.SetInit.INSTANCE);
		
		startRef.tell(new Worker.DFFact(initLocationVec, initVal, null)); //no sender ref
	
		// finally start timer
	//	timers.startTimerWithFixedDelay(Timeout.INSTANCE, timeout);
		return this;
	}
	
	private Vector<Integer> fillVector(Vector<Integer> vec, int val) {
		for (int i = 0; i < Globals.numberOfCounters; i++) {
			vec.addElement(val);

		}
		return vec;
	}

	private void createSuccListsandInit() {
		for(ProductState p : pStateToActorRef.keySet())
		{
			ModuleNode[] stateVec = p.getState();
			int numberOfGraphs = graphs.size();
			LinkedList<SuccStateInfo> succList = new LinkedList<>();
			for(int i = 0; i< numberOfGraphs; i++){
				Set<ModuleEdge> outEdges = graphs.get(i).getOutEdges(stateVec[i]);
				for(ModuleEdge edge : outEdges)
				{
					ModuleNode[] succ = Arrays.copyOf(stateVec, numberOfGraphs);
					succ[i] = edge.getTo();
					ProductState succState = new ProductState(succ);
					ActorRef<Worker.Command> succActor = pStateToActorRef.get(succState);
					SuccStateInfo sInfo = new SuccStateInfo(edge.gettFunc(), 
							edge.getDelta(), succActor) ;
					succList.add(sInfo);					
				}
			}
			//send message to the current state
			ActorRef<Worker.Command> sourceActor = pStateToActorRef.get(p);
			sourceActor.tell(new Worker.InitInfo(succList, getContext().getSelf()));
 		}
		
	}
	
	

	private Behavior<Command> onYeah() {
		stillActive = true;
		//System.out.println("received a yeah");
		return this;
	}

	private Behavior<Command> onComputedVal(ComputedVal message) {
		
		resultVal = resultVal.join(message.val);
		productStateCounter--;
		//System.out.println("decremented counter :" + productStateCounter);
		if (productStateCounter == 0) {
			System.out.println("the JOP value : " + resultVal);
			logger.info("the JOP value : " + resultVal +"\n");
			
			String[] vuses = currentVuse.getVarUse().split(";");
			for(int i =0 ; i< vuses.length; i++)
			{
				String[] nodevuse = vuses[i].split(",");
				for(int j=0; j<nodevuse.length; j++)
				{
					if(resultVal.isConstantFor(nodevuse[j]))
						numOfConsts++;
				}
			}
			getContext().getSelf().tell(GetVal.INSTANCE);
		}
		return this;
	}

	private Behavior<Command> onGetVal() {
		resultVal = AbstractValueFactory.createAbstractValue(type, false, false, 0);
		productStateCounter = 0;
		if (totalvarUse.isEmpty()) {
			System.out.println("all varuse done");
			
			long timetaken = (System.currentTimeMillis() - actorsStartTime);
			System.out.println("actors time taken :" + timetaken + " msec");
			logger.info("actors time taken :" + timetaken + " msec\n");
			
			
			System.out.println("actors time taken :" + timetaken/1000 + " sec");
			logger.info("actors time taken :" + timetaken/1000 + " sec\n");
			
			
			
			
			/*System.out.println("total timeouts :" + timeoutTime);
			System.out.println("actors time taken without timeout:" + (System.currentTimeMillis() - actorsStartTime - (timeoutTime*1000)) + " millisec");
			logger.info("actors time taken without timeout :" + (System.currentTimeMillis() - actorsStartTime - (timeoutTime*1000)) + " millisec\n");
			*/
			
			
			System.out.println("result Collection time  :" + (System.currentTimeMillis() - resultCollectionTime)/1000 + "sec");
			logger.info("result Collection time  :" + (System.currentTimeMillis() - resultCollectionTime)/1000 + "sec\n");
			
			System.out.println("Total Number of Constants  :" + numOfConsts);
			logger.info("Total Number of Constants  :" + numOfConsts + "\n");
			
			return this;
		}
		currentVuse = totalvarUse.remove(totalvarUse.size() - 1);
		ModuleNode singgleModtarget = currentVuse.getNode();
		int targetModIndex = currentVuse.getModIndex();
		String label = currentVuse.getLabel();
		System.out.println("result for node " + singgleModtarget + " in graph " + targetModIndex);
		System.out.println("Var Use Info: " + currentVuse);
		
		logger.info("result for node " + singgleModtarget + " in graph " + targetModIndex +"\n");
		logger.info("Var Use Info: " + currentVuse +"\n");
	
		AbstractValue val = AbstractValueFactory.createAbstractValue(type, false, false, 0);
		HashSet<ActorRef<Worker.Command>> targetRefs = new HashSet<>();
		for (ProductState state : pStateToActorRef.keySet()) {
			if (state.getState()[targetModIndex].equals(singgleModtarget)) {
				ActorRef<Worker.Command> targetActor = pStateToActorRef.get(state);
				targetRefs.add(targetActor);
			}
		}
		
		productStateCounter = targetRefs.size();
		
		for(ActorRef<Worker.Command> actorref : targetRefs)
		{
			actorref.tell(Worker.GetVal.INSTANCE);
		}
		//System.out.println("productStateCounter : " + productStateCounter);
		return this;
	}

	
	private Behavior<Command> onFinished() {
		System.out.println("received finish message");
		System.out.println("Final Computation Time " + 
				(System.currentTimeMillis()-actorsStartTime)/1000 + " seconds");
		
		logger.info("Final Computation Time " + 
				(System.currentTimeMillis()-actorsStartTime)/1000 + " seconds \n");
		
		System.out.println("Starting result Collection");
		resultCollectionTime = System.currentTimeMillis();
		getContext().getSelf().tell(GetVal.INSTANCE);
		return this;
	}

	private Behavior<Command> onReceiveTimeout() {

		if (!stillActive) {
			System.out.println("reached stable state");
			System.out.println("Final Computation Time " + 
					(System.currentTimeMillis()-actorsStartTime)/1000 + " seconds");
			
			logger.info("Final Computation Time " + 
					(System.currentTimeMillis()-actorsStartTime)/1000 + " seconds \n");
			timeoutTime+= timeout.getSeconds();
			// stop timer
			timers.cancelAll();
			System.out.println("Starting result Collection");
			resultCollectionTime = System.currentTimeMillis();
			getContext().getSelf().tell(GetVal.INSTANCE);
			System.out.println("printing the results");
		} else {
			timeoutTime+= timeout.getSeconds();
			stillActive = false;
		}
		return this;
	}

	private void createAllStatesAndActors(HashSet<String> modules, ArrayList<ModuleGraph> setOfModules) {

		// compute total number of product nodes
		int noOfGraphs = setOfModules.size();
		int totalsize = 1;
		for (int i = 0; i < noOfGraphs; i++) {
			totalsize *= setOfModules.get(i).getNodes().size();
		}

		System.out.println("number of product states :" + totalsize);

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
			ActorRef<Worker.Command> actor = getContext().spawn(Worker.create(cutOff, type), "state"+i);
			pStateToActorRef.put(p, actor);

		}

		targets.clear();

	}

}
