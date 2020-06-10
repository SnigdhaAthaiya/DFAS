package iisc.edu.pll.data.lattice.lcp;

import iisc.edu.pll.data.ModuleEdge;

public class VarPair {

	
	private String from;
	private String to;
	public VarPair(String from, String to) {
		super();
		this.from = from;
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	@Override
	public String toString() {
		return "VarPair [from=" + from + ", to=" + to + "]";
	}
	@Override
	public int hashCode() {
		
		return from.hashCode() + to.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		
		if(obj==null || !(obj instanceof VarPair))
			return false;
		
		VarPair v = (VarPair) obj;
		
		
		return (from.equals(v.from) && to.equals(v.to));
	}
	
	
	
	
}
