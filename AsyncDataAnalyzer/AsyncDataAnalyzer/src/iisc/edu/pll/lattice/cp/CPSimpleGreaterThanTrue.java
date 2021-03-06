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

public class CPSimpleGreaterThanTrue extends CPFunction {

	// assumed operator is >
	String lhs;
	ExpressionComponent rhs;

	public CPSimpleGreaterThanTrue(ArrayList<String> args) {
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
		ConstantPropagation val = (ConstantPropagation) v;

		if (val.isBot())
			return Globals.botVal;

		ConstantPropagation newVal = new ConstantPropagation(val);

		ConstantPropagationSingle varVal = val.values[Globals.varNum.get(lhs)];
		if (varVal.isBot()) {
			newVal = (ConstantPropagation) Globals.botVal;
		} else if (varVal.isTop()) {
			// bot if rhs is bot, ID otherwise
			if (rhs instanceof Variable) {
				Variable rhsVar = (Variable) rhs;
				ConstantPropagationSingle rhsVal = val.values[Globals.varNum.get(rhsVar.getVar())];
				if (rhsVal.isBot())
					newVal = (ConstantPropagation) Globals.botVal;
			} else
				return newVal;

		} else

		{ // lhs is a constant
			if (rhs instanceof Constant) {
				int finalVal = ((Constant) rhs).getValue();
				int lhsVal = varVal.getValue();
				if (lhsVal <= finalVal)
					newVal = (ConstantPropagation) Globals.botVal;
			}
			if (rhs instanceof Variable) {
				Variable rhsVar = (Variable) rhs;
				ConstantPropagationSingle rhsVal = val.values[Globals.varNum.get(rhsVar.getVar())];
				if (rhsVal.isBot())
					newVal = (ConstantPropagation) Globals.botVal;
				else if (rhsVal.isTop()) { // set rhs equal to lhs
											// newVal.values[Globals.varNum.get(rhsVar.getVar())]
											// = new
											// ConstantPropagationSingle(varVal.getValue(),
											// false, false);
					return newVal;
				} else {
					int finalVal = rhsVar.getCoeff() * rhsVal.getValue();
					int lhsVal = varVal.getValue();
					if (lhsVal <= finalVal)
						newVal = (ConstantPropagation) Globals.botVal; // return bot

				}

			}

		}

		return newVal;

	}
	
	

	@Override
	public String toString() {
		return "CPSimpleGreaterThanTrue [lhs=" + lhs + ", rhs=" + rhs + "]";
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
