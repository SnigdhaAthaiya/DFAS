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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.print.attribute.standard.RequestingUserName;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;

//this class will have a per variable cp value
public class ConstantPropagation implements AbstractValue {

	// should be accessed by cp functions only - TODO correct this later
	public ConstantPropagationSingle[] values;

	// initialization to bot
	public ConstantPropagation() {
		values = new ConstantPropagationSingle[Globals.numberOfVariables];
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			values[i] = new ConstantPropagationSingle(0, true, false);
		}
	}

	public ConstantPropagation(ConstantPropagation val) {
		values = new ConstantPropagationSingle[Globals.numberOfVariables];
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			values[i] = new ConstantPropagationSingle(val.values[i]);
		}
	}

	public ConstantPropagation(boolean isTop, boolean isConst, int val) {
		values = new ConstantPropagationSingle[Globals.numberOfVariables];

		if (isTop) {
			for (int i = 0; i < Globals.numberOfVariables; i++) {
				values[i] = new ConstantPropagationSingle(0, false, true);
			}
		} else if (isConst) {
			// make it constant
			for (int i = 0; i < Globals.numberOfVariables; i++) {
				values[i] = new ConstantPropagationSingle(val, false, false);
			}
		} else {
			// make it bot
			for (int i = 0; i < Globals.numberOfVariables; i++) {
				values[i] = new ConstantPropagationSingle(0, true, false);
			}
		}
	}

	public ConstantPropagation(boolean isTop) {
		values = new ConstantPropagationSingle[Globals.numberOfVariables];

		if (isTop) {
			for (int i = 0; i < Globals.numberOfVariables; i++) {
				values[i] = new ConstantPropagationSingle(0, false, true);
			}
		} else {
			// make it bot
			for (int i = 0; i < Globals.numberOfVariables; i++) {
				values[i] = new ConstantPropagationSingle(0, true, false);
			}
		}

	}

	@Override
	public AbstractValue join(AbstractValue v) {
		// new value with bots for everything
		ConstantPropagation newval = new ConstantPropagation();
		ConstantPropagation oldval = (ConstantPropagation) v;

		// pointwise join
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			ConstantPropagationSingle singleValOld = oldval.values[i];
			ConstantPropagationSingle singleValCurrent = values[i];
			newval.values[i] = (ConstantPropagationSingle) singleValOld.join(singleValCurrent);
		}
		return newval;
	}

	// returns true if the current value is greater than or equal to the
	// parameter
	@Override
	public boolean isGreater(AbstractValue v) {
		boolean isGreater = true;
		ConstantPropagation v1 = (ConstantPropagation) v;
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			if (!values[i].isGreater(v1.values[i])) {
				isGreater = false;
				break;
			}
		}
		return isGreater;
	}

	@Override
	public void setIsBot(boolean b) {
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			values[i].setIsBot(b);
		}

	}

	
	//bot even if one entry is bot
	@Override
	public boolean isBot() {
		
		if(this == Globals.botVal)
		{
			return true;
		}
			
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			if (values[i].isBot())
				return true;
		}

		return false;
	}

	@Override
	public void setIsTop(boolean b) {
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			values[i].setIsTop(b);
		}

	}

	@Override
	public boolean isTop() {
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			if (!values[i].isTop())
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int res1 = isBot() ? 5 : 3;
		int res2 = isTop() ? 7 : 11;
		int num = (int) (Math.random() * (Globals.numberOfVariables - 1));
		return (values[num].getValue() * 13 + res1 + res2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || !(obj instanceof ConstantPropagation))
			return false;

		ConstantPropagation l = (ConstantPropagation) obj;
		for (int i = 0; i < Globals.numberOfVariables; i++) {
			ConstantPropagationSingle l1 = values[i];
			ConstantPropagationSingle l2 = l.values[i];
			if (l1.getValue() != l2.getValue() || (l1.isBot() != l2.isBot()) || (l1.isTop() != l2.isTop()))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder content = new StringBuilder();
		content.append("ConstantPropagation [");
		for (String var : Globals.varNum.keySet()) {
			content.append(" " + var + " = " + values[Globals.varNum.get(var)] + " ");
		}
		content.append("]");
		return content.toString();

	}

	@Override
	public boolean isConstantFor(String s) {
		s = s.trim();
		if(values[Globals.varNum.get(s)].isConstant())
			return true;
		return false;
	}

	@Override
	public boolean isConstant() {
		
		for(int i=0; i< Globals.numberOfVariables; i++)
			if((this!=Globals.botVal) && !values[i].isConstant())
				return false;
		return false;
	}

}
