package iisc.edu.pll.analysis.forward.concurrent.simple;

import java.util.HashMap;

import akka.actor.typed.ActorRef;
import iisc.edu.pll.analysis.forward.concurrent.simple.Worker.Command;
import iisc.edu.pll.data.lattice.TFunction;

public class SuccStateInfo {

	
	public TFunction tfunc;
	public HashMap<String, Integer> delta;
	public ActorRef<Worker.Command> succ;
	public SuccStateInfo(TFunction tfunc, HashMap<String, Integer> delta, ActorRef<Command> succ) {
		super();
		this.tfunc = tfunc;
		this.delta = delta;
		this.succ = succ;
	}
	
	
}
