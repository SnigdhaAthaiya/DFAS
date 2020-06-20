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
import java.util.HashSet;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;
import iisc.edu.pll.expressions.Constant;
import iisc.edu.pll.expressions.ExpressionComponent;
import iisc.edu.pll.expressions.Operator;
import iisc.edu.pll.expressions.Variable;

public class CPAssignFunction extends CPFunction {

	// the assignment is of the following form
	// var1 = var2
	// var1 = const
	// var1 = const + var2
	// we will handle the rest of the functions later
	String lhs;
	ArrayList<ExpressionComponent> rhs;

	public CPAssignFunction(ArrayList<String> args) {
		super();

		if (args.size() < 2)
			return;

		lhs = args.get(0);
		rhs = new ArrayList<>();
		if (args.size() == 2) {

			// copy assignment
			String[] rhsarr = args.get(1).split(",");
			String coeff = rhsarr[0];
			String rhsVar = rhsarr[1];

			Variable rhsExpr = new Variable(Integer.parseInt(coeff), rhsVar);
			rhs.add(rhsExpr);
		} else {

			String[] rhsOperand1 = args.get(1).split(",");
			String source = rhsOperand1[1];
			String coeff = rhsOperand1[0];
			Variable rhsVar = new Variable(Integer.parseInt(coeff), source);
			rhs.add(rhsVar);

			// linear assignment - TODO generalize this better later
			String op = args.get(2); // assuming it to be plus right now
			rhs.add(new Operator(op));

			String[] rhsOperand2 = args.get(3).split(","); // this is a constant
			String constant = rhsOperand2[1];
			String coeff2 = rhsOperand2[0];
			int value = Integer.parseInt(constant) * Integer.parseInt(coeff2);
			Constant c = new Constant(value);
			rhs.add(c);

		}
		// TODO add sanity checks later

	}

	@Override
	public String toString() {
		StringBuilder content = new StringBuilder();
		content.append("CPAssignFunction lhs=" + lhs + ", rhs=");
		for (ExpressionComponent comp : rhs) {
			content.append(comp.toString());
			content.append("\n");
		}

		return content.toString();

	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		ConstantPropagation val = (ConstantPropagation) v;

		if (val.isBot())
			return Globals.botVal;
		
		//evaluate the expression using the incoming value
		//assuming addition as the operator right now and non-1 coeffiecients
		
		ConstantPropagation newVal = new ConstantPropagation(val);
		//creating a new top value
		ConstantPropagationSingle rhsVal = new ConstantPropagationSingle(0, false, true);
		
		//case 1 copy assign
		if(rhs.size() == 1)
		{
			Variable ex = (Variable)rhs.get(0);
			ConstantPropagationSingle rhsEx = val.values[Globals.varNum.get(ex.getVar())];
			
			if(rhsEx.isTop())
			{
				rhsVal = new ConstantPropagationSingle(0, false, true);
			}
			else if (rhsEx.isBot())
			{
				//rhsVal = new ConstantPropagationSingle(0, true, false);
				return Globals.botVal;
			}
			else{
			 int finalValue = rhsEx.getValue()* ex.getCoeff();
			 rhsVal = new ConstantPropagationSingle(finalValue, false, false);
			}	
		}
		else if(rhs.size()==3){ //linear assign TODO generalize this later
			Variable var1 = (Variable)rhs.get(0);
			Constant cons = (Constant) rhs.get(2); //assuming operator is +
			ConstantPropagationSingle rhsEx = val.values[Globals.varNum.get(var1.getVar())];
			
			if(rhsEx.isTop())
			{
				rhsVal = new ConstantPropagationSingle(0,false, true);
			}
			else if(rhsEx.isBot())
			{
				//rhsVal = new ConstantPropagationSingle(0, true, false);
				return Globals.botVal;
			}
			else {
				int finalValue =rhsEx.getValue()* var1.getCoeff() + cons.getValue();
				rhsVal = new ConstantPropagationSingle(finalValue, false, false);
			}
			
		}
		// assign the value to the lhs variable
		newVal.values[Globals.varNum.get(lhs)] = rhsVal;

		return newVal;

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
