package iisc.edu.pll.data.lattice.lcp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public abstract class LCPIDEFunction implements TFunction {

	HashMap<VarPair, LCPRep> funcEdges; // keep the edges the function
													// requires, and not all
													// edges

	boolean isTop;
	boolean isBot;

	public LCPIDEFunction() {
		funcEdges = new HashMap<VarPair, LCPRep>();
	}

	public TFunction join(TFunction v) {
		LCPIDEFunction fun = (LCPIDEFunction) v;
		LCPIDEFunction newFun = new LCPIDEID(null);

		// creating local functions for safe iteration
		HashMap<VarPair, LCPRep> fun1 = new HashMap<VarPair, LCPRep>(funcEdges);
		HashMap<VarPair, LCPRep> fun2 = new HashMap<VarPair, LCPRep>(fun.funcEdges);

		// both are ID
		if (fun1.isEmpty() && fun2.isEmpty())
			return newFun;

		if (fun1.isEmpty()) {
			for (String var : Globals.DVars) {
				VarPair pair = new VarPair(var, var);
				LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
				fun1.put(pair, value);
			}

		}
		if (fun2.isEmpty()) {
			for (String var : Globals.DVars) {
				VarPair pair = new VarPair(var, var);
				LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
				fun2.put(pair, value);
			}

		}

		for (VarPair pair : fun1.keySet()) {
			newFun.funcEdges.put(pair, fun1.get(pair));
		}

		for (VarPair pair : fun2.keySet()) {
			LCPRep value1 = fun1.get(pair);
			LCPRep value2 = fun2.get(pair);
			if (value1 != null)
				newFun.funcEdges.replace(pair, (LCPRep) value1.join(value2));
			else
				newFun.funcEdges.put(pair, value2);
		}

		return newFun;
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction compose(TFunction f) {
		/*
		 * if(this instanceof LCPEmpty) return f; if(f instanceof LCPEmpty)
		 * return this;
		 */

		LCPIDEFunction fun = (LCPIDEFunction) f;
		LCPIDEFunction newFun = new LCPIDEID(null);

		// ID case
		if (funcEdges.isEmpty())
			return f;

		if (fun.funcEdges.isEmpty())
			return this;

		for (VarPair pair1 : funcEdges.keySet())
			for (VarPair pair2 : fun.funcEdges.keySet()) {
				if (pair1.getTo().equals(pair2.getFrom())) {
					LCPRep val1 = funcEdges.get(pair1);
					LCPRep val2 = fun.funcEdges.get(pair2);
					LCPRep newVal = val2.compose(val1);
					VarPair finalPair = new VarPair(pair1.getFrom(), pair2.getTo());
					LCPRep oldVal = newFun.funcEdges.get(finalPair);
					if (oldVal == null)
						newFun.funcEdges.put(finalPair, newVal);
					else
						newFun.funcEdges.put(finalPair, (LCPRep) oldVal.join(newVal));

				}
			}

		if(newFun ==null){
			System.out.println("found it");
		}
		return newFun;
	}

	@Override
	public boolean isGreater(TFunction v) {

		LCPIDEFunction v1 = (LCPIDEFunction) v;

		HashMap<VarPair, LCPRep> fun1 = new HashMap<VarPair, LCPRep>(funcEdges);
		HashMap<VarPair, LCPRep> fun2 = new HashMap<VarPair, LCPRep>(v1.funcEdges);

		int fun1Size = fun1.size();
		int fun2Size = fun2.size();

		if (fun1Size == 0) {
			for (String var : Globals.DVars) {
				VarPair pair = new VarPair(var, var);
				LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
				fun1.put(pair, value);
			}

		}
		if (fun2Size == 0) {
			for (String var : Globals.DVars) {
				VarPair pair = new VarPair(var, var);
				LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
				fun2.put(pair, value);
			}

		}
		fun1Size = fun1.size();
		fun2Size = fun2.size();

		if (fun2Size > fun1Size){
			return false;
		}

		int matchedCount = 0;

		for (VarPair pair : fun1.keySet()) {
			LCPRep val1 = fun1.get(pair);
			LCPRep val2 = fun2.get(pair);

			if (val2 != null) {
				matchedCount++;
				if (!val1.isGreater(val2)) {
					return false;
				}
			}

		}

		if (matchedCount == fun2Size && fun2Size <= fun1Size)
			return true;

		return false;

	}

	@Override
	public void setIsBot(boolean b) {
		isBot = b;

	}

	@Override
	public boolean isBot() {
		// TODO Auto-generated method stub
		return isBot;
	}

	@Override
	public void setIsTop(boolean b) {
		isTop = b;

	}

	@Override
	public boolean isTop() {
		// TODO Auto-generated method stub
		return isTop;
	}

	// this is not the deepest copy
	// TODO make a REAL deep copy
	@Override
	public TFunction makeDeepCopy() {
		LCPIDEID fun = new LCPIDEID(null);
		ConcurrentHashMap<VarPair, LCPRep> oldversion = new ConcurrentHashMap<VarPair, LCPRep>(funcEdges);

		for (VarPair pair : oldversion.keySet()) {
			LCPRep value = oldversion.get(pair);
			VarPair newPair;
			LCPRep newvalue;

			newPair = new VarPair(pair.getFrom(), pair.getTo());

			LCPValues v = value.c;
			newvalue = new LCPRep(value.a, value.b, new LCPValues(v.getValue(), v.isBot(), v.isTop()));

			fun.funcEdges.put(newPair, newvalue);
		}
		return fun;

	}

	/**
	 * This method is calling the weakly consistent method equals over the
	 * ConcurrentHashMap - not reliable
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == this)
			return true;

		LCPIDEFunction f = (LCPIDEFunction) obj;

		return (f.funcEdges.equals(funcEdges));
	}

	@Override
	public int hashCode() {
		int t1 = isTop ? 23 : 2;
		int t2 = isBot ? 29 : 3;
		int res = 0;
		for (VarPair v : funcEdges.keySet())
			res += v.hashCode() + funcEdges.get(v).hashCode();

		return (res + t1 + t2);
	}

	@Override
	public String toString() {
		StringBuffer content = new StringBuffer();
		if (funcEdges.isEmpty())
			content.append("ID Function");
		else {

			for (VarPair pair : funcEdges.keySet()) {
				content.append(pair.getFrom() + " -->" + pair.getTo() + " : " + funcEdges.get(pair) + "\n");
			}

		}

		return content.toString();

	}

	@Override
	public int getSize() {
		return funcEdges.size();
	}
	
	@Override
	public boolean isConstantFor(String use){
		use = use.trim();
		VarPair pair = new VarPair(Globals.lambda, use);
		if(funcEdges.isEmpty())
			return false;
		
		LCPRep val = funcEdges.get(pair);
		
		boolean othervalue = false;
		for(VarPair v :  funcEdges.keySet())
		{
			if(!v.equals(pair) && v.getTo().equals(use))
			{
				if(funcEdges.get(v) !=null)
					othervalue = true;
			}
		}
		if(val!=null)
		{
			if(!val.isBot())
				return (true && !othervalue);
			else
				return false;
		}
		
		return !othervalue;
	}
	
	public boolean isNotConstantFor(String use){
		VarPair pair = new VarPair(Globals.lambda, use);
		if(funcEdges.isEmpty())
			return false;
		
		int othervalue = 0;
		for(VarPair v :  funcEdges.keySet())
		{
			if( v.getTo().equals(use))
			{
				if(funcEdges.get(v) !=null)
					othervalue++;
			}
		}
		
		
		LCPRep val = funcEdges.get(pair);
		
		
		if(val!=null)
		{
			if(val.isBot())
				return true;
		}
		
		if(othervalue>=2)
			return true;
		
		return false;
	}
	
	@Override
	public boolean isID() {
		if(funcEdges.isEmpty())
			return true;
		
		return false;
	}

}
