package iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.Vector;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.ModuleNode;

public class WorkerUnit {
	
	private ProductState state;
	private Vector<Integer> demand;
	
	public WorkerUnit(ProductState state, Vector<Integer> loc)
	{
		this.state = state;
		this.demand = loc;
	}
	public synchronized ProductState getState() {
		return state;
	}
	public synchronized void setState(ProductState state) {
		this.state = state;
	}

	public synchronized Vector<Integer> getDemand() {
		return demand;
	}

	public synchronized void setDemand(Vector<Integer> demand) {
		this.demand = demand;
	}

	@Override
	public synchronized  int hashCode() {
		int result = state.hashCode()* 5;
		for(int i = 0 ; i< demand.size(); i++)
			result+= demand.get(i);
		return result;
	}

	@Override
	public synchronized  boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(obj==null || !(obj instanceof WorkerUnit))
			return false;
		
		WorkerUnit  w = (WorkerUnit) obj;
		
		if(!w.state.equals(state))
			return false;
		
		
		if(w.demand.equals(demand))
			return true;
		
		return false;
	}

	@Override
	public synchronized String toString() {
		StringBuffer s = new StringBuffer();
		s.append(state.toString());
		s.append("[ ");
		for(int i =0; i < Globals.numberOfCounters - 1; i++)
			s.append(demand.get(i) + ", ");
		
		s.append(demand.get(Globals.numberOfCounters-1) + "]");
		 
		return s.toString();
	}
	
	
	
	
	
}
