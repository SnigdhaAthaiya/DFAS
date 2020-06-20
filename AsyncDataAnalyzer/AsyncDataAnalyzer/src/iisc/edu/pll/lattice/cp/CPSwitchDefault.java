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

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.apache.commons.lang.StringUtils;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.expressions.Constant;
import iisc.edu.pll.expressions.ExpressionComponent;
import iisc.edu.pll.expressions.ExpressionPair;
import iisc.edu.pll.expressions.Variable;

public class CPSwitchDefault extends CPFunction {

	ArrayList<ExpressionPair> expressions;

	// the input is a sequence of lhs == rhs pairs
	public CPSwitchDefault(ArrayList<String> args) {
		// TODO Auto-generated constructor stub
		if (args.size() < 2) {
			expressions = new ArrayList<>();
			return;
		}

		System.out.println("numberof cases in switch  :" + args.size() / 2);

		for (int i = 0; i < args.size(); i += 2) {
			String lhs = args.get(i);
			String[] rhsTerm = args.get(i+1).split(",");

			String term = rhsTerm[1];
			String coeff = rhsTerm[0];
			ExpressionComponent rhs;
			if (StringUtils.isNumeric(term))
				rhs = new Constant(Integer.parseInt(term) * Integer.parseInt(coeff));
			else
				rhs = new Variable(Integer.parseInt(coeff), term);
			expressions.add(new ExpressionPair(lhs, rhs));
		}
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		ConstantPropagation incoming = (ConstantPropagation)v;
		if(incoming.isBot())
			return Globals.botVal;
		
		for(ExpressionPair expPair : expressions)
		{
			String lhs = expPair.getLhs();
			ExpressionComponent rhs = expPair.getRhs();
			ConstantPropagationSingle lhsVal = incoming.values[Globals.varNum.get(lhs)];
			
			if(lhsVal.isBot())
				return Globals.botVal;
			else{
				//lhs is constant
				//get rhs
				int rhsVal = 0;
				if(rhs instanceof Constant)
				{
					rhsVal = ((Constant)rhs).getValue();
					if(lhsVal.getValue() ==rhsVal)
						return Globals.botVal; //return bot
					
				}
				else
				{
					Variable rhsVar =(Variable) rhs;
					ConstantPropagationSingle rhscp  = incoming.values[Globals.varNum.get(rhsVar.getVar())]; 
					if(rhscp.isBot())
						return Globals.botVal;
					rhsVal  = rhscp.getValue() * rhsVar.getCoeff();
					if(lhsVal.getValue() ==rhsVal)
						return Globals.botVal; //return bot
					
				}
			}
			

		}
		return new ConstantPropagation(incoming);
		
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return expressions.isEmpty();
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

}
