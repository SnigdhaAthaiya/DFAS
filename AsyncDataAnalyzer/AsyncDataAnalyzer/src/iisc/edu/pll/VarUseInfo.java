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
package iisc.edu.pll;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import iisc.edu.pll.data.ModuleNode;

public class VarUseInfo {
	
	ModuleNode node;
	int modIndex;
	String varUse;
	String label;
	
	
	public VarUseInfo(ModuleNode node, int modIndex, String varUse, String label) {
		super();
		this.node = node;
		this.modIndex = modIndex;
		this.varUse = varUse;
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public ModuleNode getNode() {
		return node;
	}
	public void setNode(ModuleNode node) {
		this.node = node;
	}
	public int getModIndex() {
		return modIndex;
	}
	public void setModIndex(int modIndex) {
		this.modIndex = modIndex;
	}
	public String getVarUse() {
		return varUse;
	}
	public void setVarUse(String varUse) {
		this.varUse = varUse;
	}
	
	@Override
	public String toString(){
		return ("[VarUseInfo :"+ label+ ", "  + modIndex+ " , " + varUse +" ]");
	}
	
	@Override
	public int hashCode(){
		return (node.hashCode()+ modIndex + varUse.length() + label.length()+ 23);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj==this)
			return true;
		
		if(obj==null || !(obj instanceof VarUseInfo))
			return false;
		
		VarUseInfo v = (VarUseInfo)obj;
		
		if(node.equals(v.node) && modIndex == v.modIndex && varUse.equals(v.varUse) && label.equals(v.label))
			return true;
		
		return false;
					
					
	}
	

}
