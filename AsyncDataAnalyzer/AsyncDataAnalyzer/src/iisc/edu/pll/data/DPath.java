package iisc.edu.pll.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class DPath {

	private TFunction func;
	private HashMap<String, Integer> demand;
	private String latticetype;
	private ArrayList<String> vars;
	private HashSet<String> messages;
	
	public DPath(String type, ArrayList<String> v, HashSet<String> messageSet)
	{
		latticetype = type;
		vars = new ArrayList<>(v);
		func = TFunctionFactory.createFunction(latticetype, Statement.ID,vars, new ArrayList<>());
		demand = CounterHandler.getNoChange(messageSet);
		messages = new HashSet<>(messageSet);
	}

	@Override
	public String toString() {
		return "DPath [func=" + func + ", demand=" + demand + "]";
	}

	public TFunction getFunc() {
		return func;
	}

	public void setFunc(TFunction func) {
		this.func = func;
	}

	public HashMap<String, Integer> getDemand() {
		return demand;
	}

	public void setDemand(HashMap<String, Integer> demand) {
		this.demand = demand;
	}

	public String getLatticetype() {
		return latticetype;
	}

	public void setLatticetype(String latticetype) {
		this.latticetype = latticetype;
	}

	public ArrayList<String> getVars() {
		return vars;
	}

	public void setVars(ArrayList<String> vars) {
		this.vars = vars;
	}

	public HashSet<String> getMessages() {
		return messages;
	}

	public void setMessages(HashSet<String> messages) {
		this.messages = messages;
	}
	
	
	
}
