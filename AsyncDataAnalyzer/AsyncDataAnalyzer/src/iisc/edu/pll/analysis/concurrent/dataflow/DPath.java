/*******************************************************************************
 * Copyright (C) 2020 Snigdha Athaiya
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
