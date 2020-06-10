package iisc.edu.pll.data.lattice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;

public class LCPIDE implements TFunction {

	private ArrayList<String> DVars;
	private HashMap<VarPair, LCPRep> funcMap;
	private final String lambda = "_lambda";
	

	@Override
	public String toString() {
		StringBuilder content = new StringBuilder();
		for (VarPair v : funcMap.keySet()) {
			content.append(v.getFrom() + ", " + v.getTo() + " : " + funcMap.get(v).toString() + "\n");
		}

		return content.toString();
	}

	// needs to be initialized for the analysis
	public LCPIDE(ArrayList<String> vars) {
		vars.remove(lambda);
		DVars = new ArrayList<>(vars);
		DVars.add(lambda);
		funcMap = new HashMap<>();
		for (String v1 : DVars)
			for (String v2 : DVars) {
				if(!v1.equals(lambda) || !v2.equals(lambda)){
					VarPair p = new VarPair(v1, v2);
					// initializing all values to top - i.e. no functions at ll
					LCPRep l = new LCPRep(true);
					funcMap.put(p, l);
				}
				else
				{
					VarPair p = new VarPair(v1, v2);
					// initializing to ID for lambda --> lambda edge
					LCPRep l = new LCPRep(1,0,new LCPValues(0, false, true));					
					
					funcMap.put(p, l);
				}
				
			}

	}

	// set D to { \lambda}
	public LCPIDE() {
		DVars = new ArrayList<>();
		DVars.add(lambda);
	}

	// takes as input an assignment, and sets the transfer function
	// appropriately
	
	public void handleAssign(String varname, String op, String operand1, String operand2) {
		LCPRep func;
		
		handleID();
		
		//get components of operand 1
		String[] comps = operand1.split(",");
		String source = comps[1];
		String coeff =comps[0];
				
		// case 1 - copy constant, op null
		if (op == null) {
			if (StringUtils.isNumeric(source)) {
				// const case
				VarPair key = findKey(lambda, varname);
				func = new LCPRep(0, Integer.parseInt(source), new LCPValues(0, false, true));
				funcMap.replace(key, func);

			} else {
				// var case
				VarPair key = findKey(source, varname);
				
				func = new LCPRep(Integer.parseInt(coeff), 0, new LCPValues(0, false, true));
				funcMap.replace(key, func);
			}
		}
		//linear term
		else{
			//op can be +  right now
			//assuming operand1 is the variable and operand 2 is the ocnstant
			VarPair key = findKey(source, varname);
			func = new LCPRep(Integer.parseInt(coeff), Integer.parseInt(operand2), new LCPValues(0, false, true));
			funcMap.replace(key, func);
			
		}

	}
	
	
	public void handleID() {
		funcMap = new HashMap<>();
		for(String v1 : DVars)
			for(String v2 : DVars)
			{
				VarPair p = new VarPair(v1, v2);
				if(v1.equals(v2))
				{
					//id for the pairs with same variables
					LCPRep l = new LCPRep(1, 0, new LCPValues(0, false, true));
					funcMap.put(p, l);
				}
				else
				{
					//rest are top 
					LCPRep l = new LCPRep(true);
					funcMap.put(p, l);				
				}
				//initializing all values to bot 
				
			}
			
		
	}

	private VarPair findKey(String source, String dest) {
		for (VarPair v : funcMap.keySet()) {
			if (v.getFrom().equals(source) && v.getTo().equals(dest))
				return v;
		}
		return null;
	}

	@Override
	public TFunction join(TFunction v) {
		LCPIDE fun = (LCPIDE)v;
		LCPIDE newFun = new LCPIDE(DVars);
		ArrayList<String> funVars = new ArrayList<>(DVars);
		funVars.add(lambda);
		
		for(VarPair pair : newFun.funcMap.keySet())
		{
			VarPair pair1  = getPair(pair.getFrom(), pair.getTo(), this);
			VarPair pair2  = getPair(pair.getFrom(), pair.getTo(), fun);
			LCPRep val = (LCPRep) funcMap.get(pair1).join(fun.funcMap.get(pair2));
			newFun.funcMap.replace(pair, val);
		}
		
		
		return newFun;
		
	}

	@Override
	public boolean isGreater(TFunction v) {
		LCPIDE v2 = (LCPIDE) v;
		
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

	private LCPRep getValue(VarPair pair, HashMap<VarPair, LCPRep> funcMap2) {
		for(VarPair p : funcMap2.keySet())
		{
			if(p.getFrom().equals(pair.getFrom()) && p.getTo().equals(pair.getTo()) )
				return funcMap2.get(p);
		}
		return null;
	}

	@Override
	public TFunction compose(TFunction f) {
		LCPIDE fun = (LCPIDE)f;
		LCPIDE newFun = new LCPIDE(DVars);
		ArrayList<String> funVars = new ArrayList<>(DVars);
		funVars.add(lambda);
		for(String var1 : funVars)
		{
			for (String var2: funVars){
				for(String var3: funVars)
				{
					VarPair p1 = getPair(var1, var2, this);
					VarPair p2 = getPair(var2, var3, fun);
					LCPRep r1 = funcMap.get(p1);
					LCPRep r2 = fun.funcMap.get(p2);
					LCPRep r3 = r1.compose(r2);
					VarPair p3 = getPair(var1, var3, newFun);
					LCPRep oldVal = newFun.funcMap.get(p3);
					LCPRep newVal = (LCPRep)oldVal.join(r3);
							
					newFun.funcMap.replace(p3, newVal);					
				}
			}
		}
		
		
		return newFun;
	}

	private VarPair getPair(String var1, String var2, LCPIDE lcpide) {
		
		VarPair v = new VarPair(var1, var2);
		for(VarPair key : lcpide.funcMap.keySet())
		{
			if(v.getFrom().equals(key.getFrom()) && v.getTo().equals(key.getTo()))
				return key;
		}
		
		
		
		return v;
	}

	@Override
	public void setIsBot(boolean b) {
		

	}

	@Override
	public boolean isBot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsTop(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction makeDeepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		
		return funcMap.size();
	}

	@Override
	public boolean isConstantFor(String use){
		return false;
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNotConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
