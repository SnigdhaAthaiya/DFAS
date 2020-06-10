package iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import iisc.edu.pll.data.CounterHandler;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class DPath {
	
	private TFunction func;
	private int[] demand;
	private String latticetype;
	private ArrayList<String> vars;
	
	
	public DPath(String type, ArrayList<String> v, HashSet<String> messageSet)
	{
		latticetype = type;
		vars = new ArrayList<>(v);
		func = TFunctionFactory.createFunction(latticetype, Statement.ID,vars, new ArrayList<>());
		demand = new int[messageSet.size()];
		
	}

	public DPath(DPath path) {
		latticetype = path.latticetype;
		vars = new ArrayList<>(path.vars);
		func = path.func;
		demand = new int[path.demand.length];
		for(int i =0; i< path.demand.length; i++)
			demand[i] = path.demand[i];
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

	public int[] getDemand() {
		return demand;
	}

	public void setDemand(int[] demand) {
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

	@Override
	public int hashCode() {
		int res = func.hashCode();
		for(int i = 0; i< demand.length; i++)
			res+= demand[i];
		
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj ==this)
			return true;
		
		if(obj==null || !(obj instanceof DPath) )
			return false;
		
		
		DPath p = (DPath) obj;
		if(p.demand.length != demand.length)
			return false;
		
		for(int i = 0; i< demand.length; i++)
			if(demand[i] != p.demand[i])
				return false;
			
		return (p.func.equals(func));
	}

	


}
