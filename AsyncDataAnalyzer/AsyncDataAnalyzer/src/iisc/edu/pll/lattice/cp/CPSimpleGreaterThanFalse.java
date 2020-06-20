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

import org.apache.commons.lang.StringUtils;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.expressions.Constant;
import iisc.edu.pll.expressions.ExpressionComponent;
import iisc.edu.pll.expressions.Variable;

public class CPSimpleGreaterThanFalse extends CPFunction {

	//handles false branch of lhs > rhs
	String lhs;
	ExpressionComponent rhs;
	
	public CPSimpleGreaterThanFalse(ArrayList<String> args) {
		if (args.size() < 2) {
			lhs = "";
			rhs = new Constant(0);
			return;
		}

		lhs = args.get(0);
		String[] rhsTerm = args.get(1).split(",");

		String term = rhsTerm[1];
		String coeff = rhsTerm[0];
		if (StringUtils.isNumeric(term))
			rhs = new Constant(Integer.parseInt(term) * Integer.parseInt(coeff));
		else
			rhs = new Variable(Integer.parseInt(coeff), term);

	}
	@Override
	public AbstractValue apply(AbstractValue v) {
		ConstantPropagation incoming = (ConstantPropagation)v;
		if(incoming.isBot())
			return (ConstantPropagation) Globals.botVal;
		
		ConstantPropagationSingle lhsVal = incoming.values[Globals.varNum.get(lhs)];
		
		if(lhsVal.isBot())
			return (ConstantPropagation) Globals.botVal;
		else if(lhsVal.isTop())
			return new ConstantPropagation(incoming); //id transfer function
		else{
			//lhs is constant
			//get rhs
			int rhsVal = 0;
			if(rhs instanceof Constant)
			{
				rhsVal = ((Constant)rhs).getValue();
				if(lhsVal.getValue() > rhsVal)
					return (ConstantPropagation) Globals.botVal; //return bot
				else
					return new ConstantPropagation(incoming);
			}
			else
			{
				Variable rhsVar =(Variable) rhs;
				ConstantPropagationSingle rhscp  = incoming.values[Globals.varNum.get(rhsVar.getVar())]; 
				if(rhscp.isBot())
					return (ConstantPropagation) Globals.botVal;
				if(rhscp.isTop())
					return new ConstantPropagation(incoming);
				rhsVal  = rhscp.getValue() * rhsVar.getCoeff();
				if(lhsVal.getValue() > rhsVal)
					return (ConstantPropagation) Globals.botVal; //return bot
				else
					return new ConstantPropagation(incoming);
				
			}
		}
	
	}

	
	@Override
	public String toString() {
		return "CPSimpleGreaterThanFalse [lhs=" + lhs + ", rhs=" + rhs + "]";
	}
	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

}
