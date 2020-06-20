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
