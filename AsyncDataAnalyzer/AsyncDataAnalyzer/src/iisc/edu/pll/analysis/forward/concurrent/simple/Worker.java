package iisc.edu.pll.analysis.forward.concurrent.simple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;
import iisc.edu.pll.analysis.forward.concurrent.simple.Supervisor.Command;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.AbstractValueFactory;

public class Worker extends AbstractBehavior<Worker.Command> {

	// messages
	public interface Command {
	}

	// dataflow class
	public static final class DFFact implements Command {
		public static int count = -1;
		public final UUID id;
		public final ActorRef<Command> sender;
		public final Vector<Integer> config;
		public final AbstractValue val;

		public DFFact(Vector<Integer> config, AbstractValue val, ActorRef<Command> sender) {
			
			this.id = UUID.randomUUID();
			this.config = config;
			this.val = val;
			this.sender = sender;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id.hashCode();
			return result;
		}

		// consider equal for equal ids
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DFFact other = (DFFact) obj;
			if (id != other.id)
				return false;
			return true;
		}

	}

	public static final class InitInfo implements Command {
		public final LinkedList<SuccStateInfo> succStates;
		public final ActorRef<Supervisor.Command> supervisor;

		public InitInfo(LinkedList<SuccStateInfo> sstates, ActorRef<Supervisor.Command> sup) {
			succStates = new LinkedList<SuccStateInfo>(sstates);
			supervisor = sup;

		}
	}

	public static final class Finished implements Command {
		public final UUID wpId;

		public Finished(UUID id) {
			// TODO Auto-generated constructor stub
			wpId = id;
		}
	}

	public enum GetVal implements Command {
		INSTANCE
	}

	public enum SetInit implements Command {
		INSTANCE
	}

	
	private final static Logger logger = Logger.getLogger(Worker.class);
	// local state

	LinkedList<SuccStateInfo> successorStates;
	HashMap<Vector<Integer>, AbstractValue> valueMap;
	int cutoff;
	String latticeType;
	ActorRef<Supervisor.Command> supervisor;
	boolean isInit; // does the worker represent init product state

	HashMap<UUID, HashSet<UUID>> workPacketMap;
	HashMap<UUID, ActorRef<Command>> wpToPredMap;

	// creation of this Actor

	public static Behavior<Command> create(int cutoff, String type) {
		return Behaviors.setup(context -> new Worker(context, cutoff, type));
	}

	private Worker(ActorContext<Command> context, int cutoff, String type) {
		super(context);
		successorStates = new LinkedList<>();
		valueMap = new HashMap<>();
		this.cutoff = cutoff; // corresponds to naive
		this.latticeType = type;
		this.isInit = false;
		workPacketMap = new HashMap<>();
		wpToPredMap = new HashMap<>();
		logger.setLevel(Level.INFO);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder().onMessage(InitInfo.class, this::onInitInfo).onMessage(DFFact.class, this::onDFFact)
				.onMessage(GetVal.class, unUsed -> onGetVal()).onMessage(Finished.class, this::onFinished)
				.onMessage(SetInit.class, unUsed -> onSetInit()).build();

	}

	private Behavior<Command> onFinished(Finished f) {
		UUID finishedID = f.wpId;

		UUID key = null;
		for (UUID k : workPacketMap.keySet()) {
			if (workPacketMap.get(k).contains(finishedID))
				key = k;
		}

		if(key == null){
			System.out.println("could not find the wpID " + finishedID);
			logger.debug("could not find wpID " + finishedID+"\n");
			return this;
		}
		HashSet<UUID> set = workPacketMap.get(key);
		set.remove(finishedID);
		logger.debug("removing wpID " + finishedID + "\n");
		if (set.isEmpty()) {
			
			//we are done with this key
			
			workPacketMap.remove(key); // remove from wp maps

			if (isInit) {
				//send to supervisor that all work packets are done
				supervisor.tell(Supervisor.Finished.INSTANCE);
			} else {
				// send message to predecessor
				wpToPredMap.get(key).tell(new Finished(key)); 
			}
			// remove from wp-> actor ref map
			wpToPredMap.remove(key);
		} else {
			workPacketMap.put(key, set); // update with the smaller set
		}
		return this;
	}

	private Behavior<Command> onSetInit() {
		this.isInit = true;
		return this;
	}

	private Behavior<Command> onInitInfo(InitInfo message) {
		successorStates = new LinkedList<>(message.succStates);
		supervisor = message.supervisor;
		return this;
	}

	private Behavior<Command> onDFFact(DFFact message) {
		boolean flag = false;
		Vector<Integer> conf = message.config;
		AbstractValue val = message.val;
		AbstractValue finalVal = val;
		ActorRef<Command> sender = message.sender;
		UUID wpID = message.id;
		if(workPacketMap.containsKey(wpID))
		{
			System.out.println("seeing the key again");
		}
		
		if(wpToPredMap.containsKey(wpID))
		{
			System.out.println("seeing the key again");
		}
	
		// get the current value for the config
		if (!valueMap.containsKey(conf)) {
			flag = true;
			valueMap.put(conf, val);
		} else {
			AbstractValue oldVal = valueMap.get(conf);
			AbstractValue joinedVal = oldVal.join(val);
			if (joinedVal.isGreater(oldVal) && !oldVal.isGreater(joinedVal)) {
				flag = true;
				valueMap.put(conf, joinedVal);
				finalVal = joinedVal;
			}
		}

		if (flag) {
			HashSet<UUID> wpIDs = new HashSet<>();
			for (SuccStateInfo state : successorStates) {
				AbstractValue transferedVal = state.tfunc.apply(finalVal);

				Vector<Integer> newConfig = computeConfig(state.delta, conf);
				if (newConfig != null) {
					DFFact fact = new DFFact(newConfig, transferedVal, getContext().getSelf());
					wpIDs.add(fact.id);
					//System.out.println("adding " + fact.id + " for wp " + wpID);
					logger.debug("adding wpid " + fact.id + " for wpID " + wpID + "\n" );
					state.succ.tell(fact);
					// add to the maps

				}
			}
			if (!wpIDs.isEmpty()) {
				// created successors
				// add to map
				workPacketMap.put(wpID, wpIDs);
				// add predecessor
				wpToPredMap.put(wpID, sender);

			} else {
				// no children hence terminate right here
				sender.tell(new Finished(wpID));
				
			}
			// supervisor.tell(Supervisor.Yeah.INSTANCE);
		} else {
			// no successors created hence finish with this wp
			sender.tell(new Finished(wpID));
		}

		return this;
	}

	private Vector<Integer> computeConfig(HashMap<String, Integer> delta, Vector<Integer> config) {
		Vector<Integer> newConfigVector = new Vector<>(Globals.numberOfCounters);

		int i = 0;
		for (String msg : delta.keySet()) {
			int del = delta.get(msg); // edge delta
			int conVal = config.get(i);

			int newConfig = 0;

			if (conVal == cutoff) {
				newConfig = cutoff;
			} else {
				newConfig = conVal + del;
			}
			// the new config can be negative, return null
			if (newConfig < 0)
				return null;

			newConfigVector.addElement(newConfig);
			i++;
		}

		return newConfigVector;

	}

	public Behavior<Command> onGetVal() {
		AbstractValue v = AbstractValueFactory.createAbstractValue(latticeType, false, false, 0); // bot
		for (Vector<Integer> conf : valueMap.keySet()) {
			v = v.join(valueMap.get(conf));
		}
		supervisor.tell(new Supervisor.ComputedVal(v));

		return this;
	}

}
