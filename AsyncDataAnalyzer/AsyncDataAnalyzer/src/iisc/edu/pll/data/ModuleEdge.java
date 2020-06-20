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
