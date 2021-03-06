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
package iisc.edu.pll.lattice.cp;

import java.util.ArrayList;

import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.AbstractValueFactory;

public class CPComposedFunc extends CPFunction {

	ArrayList<CPFunction> compFunc;
	public CPComposedFunc(ArrayList<CPFunction> compFunc) {
		super();
		this.compFunc = compFunc;
	}

	public ArrayList<CPFunction> getCompFunc() {
		return compFunc;
	}

	public void setCompFunc(ArrayList<CPFunction> compFunc) {
		this.compFunc = compFunc;
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		AbstractValue result = new ConstantPropagation((ConstantPropagation)v);
		for(CPFunction func :  compFunc)
		{
			result = func.apply(result);
		}
		return result;
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		boolean isID = true;
		for(CPFunction func : compFunc)
			isID = isID && func.isID();
		return isID;
	}

	@Override
	public String toString() {
		return "CPComposedFunc [compFunc=" + compFunc + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compFunc == null) ? 0 : compFunc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CPComposedFunc other = (CPComposedFunc) obj;
		if (compFunc == null) {
			if (other.compFunc != null)
				return false;
		} else if (!compFunc.equals(other.compFunc))
			return false;
		return true;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
