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

import iisc.edu.pll.data.ModuleNode;


/** This is the product state class for the dataflow analysis
 * The main algorithm is supposed to remember the order of the graphs*/
public class ProductState {
	
	private ModuleNode[] state;
	public ProductState(ModuleNode[] state) {
		
		this.state = state;
		
	}
	
	public ProductState(int size)
	{
		
		this.state  = new ModuleNode[size];
				
	}

	public ModuleNode[] getState() {
		return state;
	}

	public void setState(ModuleNode[] state) {
		this.state = state;
	}

	
	@Override
	public int hashCode() {
		int res = 0;
		for(int i =0; i<state.length; i++)
		{
			res+= state[i].hashCode();
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this)
			return true;
		
		if(obj==null || !(obj instanceof ProductState))
			return false;
		
		ProductState p = (ProductState) obj;
		if(p.state.length != state.length)
			return false;
		
		for(int i =0 ; i<state.length; i++)
			if(!state[i].equals(p.state[i]))
				return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[");
		for(int  i = 0; i<(state.length-1); i++)
			b.append(state[i].getId() + ", ");
		b.append(state[state.length-1].getId() + "]");
		
		return b.toString();
	}
	
	

	

}
