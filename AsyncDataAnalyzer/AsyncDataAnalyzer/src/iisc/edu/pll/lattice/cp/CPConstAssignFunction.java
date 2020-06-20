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

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;
import iisc.edu.pll.expressions.Constant;

public class CPConstAssignFunction extends CPFunction{

	String lhs;
	int value;
	
	public CPConstAssignFunction(ArrayList<String> args){
		
		if (args.size() < 2)
			return;

		// TODO add sanity checks later
		String varname = args.get(0);
		lhs=varname;
		String[] constTerm = args.get(1).split(",");
		
		String constant = constTerm[1];
		String coeff = constTerm[0];
		value = Integer.parseInt(constant) * Integer.parseInt(coeff);
		
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		ConstantPropagation val = (ConstantPropagation) v;
		
		if(val.isBot())
			return Globals.botVal;
		
		ConstantPropagation newVal = new ConstantPropagation(val);
		//assign the value to the lhs variable
		newVal.values[Globals.varNum.get(lhs)] = new ConstantPropagationSingle(value, false, false);
		
		return newVal;
	}

	public String getLhs() {
		return lhs;
	}

	public void setLhs(String lhs) {
		this.lhs = lhs;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CPConstAssignFunction [lhs=" + lhs + ", value=" + value + "]";
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return lhs;
	}
	
	
}
