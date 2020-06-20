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

import java.util.HashSet;

public class ModuleNode {

	private static long GlobalId = 0;
	private long id;
	HashSet<ModuleEdge> incoming;
	HashSet<ModuleEdge> outgoing;
	
	
	

	public ModuleNode() {
		id = GlobalId++;	
		incoming = new HashSet<>();
		outgoing = new HashSet<>();
	}
	
	public ModuleNode(ModuleNode node){
		id = node.id;
		incoming = new HashSet<>();
		outgoing = new HashSet<>();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	@Override
	public String toString() {
		
		return ("id : " + id); 
	}
	
	
	

	public HashSet<ModuleEdge> getIncoming() {
		return incoming;
	}


	public void setIncoming(HashSet<ModuleEdge> incoming) {
		this.incoming = incoming;
	}


	public HashSet<ModuleEdge> getOutgoing() {
		return outgoing;
	}


	public void setOutgoing(HashSet<ModuleEdge> outgoing) {
		this.outgoing = outgoing;
	}


	@Override
	public int hashCode() {
		int seed = 17;
		int result = 3 * (seed + (int)id);
		return  result;
	}


	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		
		if(obj==null || !(obj instanceof ModuleNode))
			return false;
		
		
		ModuleNode n = (ModuleNode) obj;
		return (id == n.id);
	}
	
	

}
