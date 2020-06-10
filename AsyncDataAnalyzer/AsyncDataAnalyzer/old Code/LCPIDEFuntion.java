package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;
import java.util.HashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;
import iisc.edu.pll.data.lattice.LCPIDE;

public abstract class LCPIDEFunction implements TFunction {

	
	HashMap<VarPair, LCPRep> funcMap;
	

	boolean isTop;
	boolean isBot;

	

	LCPIDEFunction() {
		funcMap = new HashMap<>();
	}

	public synchronized TFunction join(TFunction v) {
		LCPIDEFunction fun = (LCPIDEFunction) v;
		LCPIDEFunction newFun = new LCPEmpty();

		for (VarPair pair : newFun.funcMap.keySet()) {
			VarPair pair1 = getPair(pair.getFrom(), pair.getTo(), this);
			VarPair pair2 = getPair(pair.getFrom(), pair.getTo(), fun);
			LCPRep val = (LCPRep) funcMap.get(pair1).join(fun.funcMap.get(pair2));
			newFun.funcMap.replace(pair, val);
		}

		return newFun;
	}

	private synchronized VarPair getPair(String var1, String var2, LCPIDEFunction lcpide) {

		VarPair v = new VarPair(var1, var2);
		for (VarPair key : lcpide.funcMap.keySet()) {
			if (v.getFrom().equals(key.getFrom()) && v.getTo().equals(key.getTo()))
				return key;
		}

		return v;
	}

	@Override
	public synchronized TFunction compose(TFunction f) {
		
		/*if(this instanceof LCPEmpty)
			return f;
		if(f instanceof LCPEmpty)
			return this;*/
		
		LCPIDEFunction fun = (LCPIDEFunction) f;
		LCPIDEFunction newFun = new LCPEmpty();
		for (String var1 : Globals.DVars) {
			for (String var2 : Globals.DVars) {
				for (String var3 : Globals.DVars) {
					VarPair p1 = getPair(var1, var2, this);
					VarPair p2 = getPair(var2, var3, fun);
					LCPRep r1 = funcMap.get(p1);
					LCPRep r2 = fun.funcMap.get(p2);
					LCPRep r3 = r1.compose(r2);
					VarPair p3 = getPair(var1, var3, newFun);
					LCPRep oldVal = newFun.funcMap.get(p3);
					LCPRep newVal = (LCPRep) oldVal.join(r3);

					newFun.funcMap.replace(p3, newVal);
				}
			}
		}

		return newFun;
	}

	@Override
	public synchronized String toString() {
		StringBuilder content = new StringBuilder();
		for (VarPair v : funcMap.keySet()) {
			content.append(v.getFrom() + ", " + v.getTo() + " : " + funcMap.get(v).toString() + "\n");
		}

		return content.toString();
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized boolean isGreater(TFunction v) {
		LCPIDEFunction v2 = (LCPIDEFunction) v;
		
		for(VarPair pair :  funcMap.keySet())
		{
			LCPRep r1 = getValue(pair, v2.funcMap);
			if(!funcMap.get(pair).isGreater(r1))
			{
				return false;
			}
		}
		return true;
	}

	private  synchronized LCPRep getValue(VarPair pair, HashMap<VarPair, LCPRep> funcMap2) {
		for(VarPair p : funcMap2.keySet())
		{
			if(p.getFrom().equals(pair.getFrom()) && p.getTo().equals(pair.getTo()) )
				return funcMap2.get(p);
		}
		return null;
	}
	@Override
	public synchronized void setIsBot(boolean b) {
		isBot = b;
		
	}

	@Override
	public synchronized boolean isBot() {
		
		return isBot;
	}

	@Override
	public synchronized void setIsTop(boolean b) {
		isTop = b;
		
	}

	@Override
	public synchronized boolean isTop() {
		return isTop;
	}

	@Override
	public synchronized int hashCode() {
		int t1 = isTop ? 23 : 2;
		int t2 = isBot ? 29 : 3;
		int res = 0;
		for(VarPair v : funcMap.keySet())
			res += funcMap.get(v).hashCode();
		
		return (res + t1 + t2);
	}

	@Override
	public synchronized boolean equals(Object obj) {
		
		if(obj== this)
			return true;
		
		LCPIDEFunction f = (LCPIDEFunction) obj;
		
		return (f.funcMap.equals(funcMap));
	}

	@Override
	public synchronized TFunction makeDeepCopy(){
		LCPIDEFunction newFunc = new LCPEmpty(); 
		
		for(VarPair vp : funcMap.keySet())
		{
			newFunc.funcMap.put(vp, funcMap.get(vp));
		}
		
		return newFunc;
	}
	
	

}

