package iisc.edu.pll.data;

import java.util.HashMap;

import iisc.edu.pll.data.lattice.TFunction;

public class ModuleEdge {
	
	
	private ModuleNode from;
	private ModuleNode to;
	private TFunction tFunc;
	private HashMap<String, Integer> delta;
	
	public HashMap<String, Integer> getDelta() {
		return delta;
	}

	public void setDelta(HashMap<String, Integer> delta) {
		this.delta = delta;
	}

	public ModuleEdge(ModuleNode from, ModuleNode to, TFunction tFunc, HashMap<String, Integer> delta) {
		super();
		this.from = from;
		this.to = to;
		this.tFunc = tFunc;
		this.delta = delta;
	}
	
	public ModuleEdge(ModuleEdge edge) {
		super();
		this.from = edge.from;
		this.to = edge.to;
		this.tFunc = edge.tFunc;
		this.delta = edge.delta;
	}

	public ModuleNode getFrom() {
		return from;
	}

	public void setFrom(ModuleNode from) {
		this.from = from;
	}

	public ModuleNode getTo() {
		return to;
	}

	public void setTo(ModuleNode to) {
		this.to = to;
	}

	public TFunction gettFunc() {
		return tFunc;
	}

	public void settFunc(TFunction tFunc) {
		this.tFunc = tFunc;
	}

	@Override
	public String toString() {
		return "GEdge [from=" + from + ", to=" + to + ", tFunc=" + tFunc + "Delta =" + delta +"]";
	}

	@Override
	public int hashCode() {
		int seed = 19;
		
		int result = 19 + from.hashCode() + to.hashCode();
		for(Integer i : delta.values())
			result +=  i.intValue();
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		
		if(obj==null || !(obj instanceof ModuleEdge))
			return false;
		
		ModuleEdge e = (ModuleEdge)obj;
			

		return (e.from.equals(from) && e.to.equals(to) && e.delta.equals(delta) && e.tFunc.equals(tFunc));
	}
	
	
	

}
