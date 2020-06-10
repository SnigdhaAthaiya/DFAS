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
