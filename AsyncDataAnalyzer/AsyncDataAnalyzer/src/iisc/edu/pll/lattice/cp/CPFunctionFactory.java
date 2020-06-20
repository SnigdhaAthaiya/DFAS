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

import iisc.edu.pll.data.Statement;

public class CPFunctionFactory {

	public static CPFunction createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.ASSIGN))
			return new CPAssignFunction(args);
		if(stType.equals(Statement.CONSTASSIGN))
			return new CPConstAssignFunction(args);
		if(stType.equals(Statement.EQUALSTRUE))
			return new CPSimpleEqualTrue(args);
		if(stType.equals(Statement.EQUALSFALSE))
			return new CPSimpleEqualFalse(args);
		if(stType.equals(Statement.ID))
			return new CPIDFunction();
		if(stType.equals(Statement.EQUALSSWITCHFALSE))
			return new CPSwitchDefault(args);
		if(stType.equals(Statement.GREATERTHANTRUE))
			return new CPSimpleGreaterThanTrue(args);
		if(stType.equals(Statement.GREATERTHANFALSE))
			return new CPSimpleGreaterThanFalse(args);
		return new CPAssignFunction(args);
	}

}
