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
