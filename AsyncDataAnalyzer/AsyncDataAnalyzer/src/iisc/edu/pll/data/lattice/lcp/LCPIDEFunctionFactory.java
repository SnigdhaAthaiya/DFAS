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
package iisc.edu.pll.data.lattice.lcp;

import java.util.ArrayList;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;

public class LCPIDEFunctionFactory {
	
	public static LCPIDEFunction createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new LCPIDEConst(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new LCPIDEAssign(args);
		if(stType.equals(Statement.ID))
			return new LCPIDEID(args);
		if(stType.equals(Statement.EMPTY))
			return new LCPEmpty();
		if(stType.equals("test13"))
			return get13();
		
		return new LCPIDEID(args);
	}

	private static LCPIDEFunction get13() {
		LCPIDEID fun = new LCPIDEID(new ArrayList<>());
		for(String var : Globals.DVars)
		{
			fun.funcEdges.put(new VarPair(var, var), new LCPRep(1,0, new LCPValues(0, false, true)));
			fun.funcEdges.put(new VarPair(Globals.lambda, var), new LCPRep(1,0, new LCPValues(0, false, true)));
		}
		
		System.out.println("added edges: "+ fun.funcEdges.size() );
		return fun;
	}

}
