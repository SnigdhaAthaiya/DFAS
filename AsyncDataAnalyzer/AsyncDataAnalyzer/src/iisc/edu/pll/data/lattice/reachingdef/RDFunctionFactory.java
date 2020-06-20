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
package iisc.edu.pll.data.lattice.reachingdef;

import java.util.ArrayList;

import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.lcp.LCPEmpty;
import iisc.edu.pll.data.lattice.lcp.LCPIDEAssign;
import iisc.edu.pll.data.lattice.lcp.LCPIDEConst;
import iisc.edu.pll.data.lattice.lcp.LCPIDEID;

public class RDFunctionFactory {
	
	public static TFunction createFunction(String stType,ArrayList<String> args)
	{
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new RDConstAssign(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new RDAssign(args);
		if(stType.equals(Statement.ID))
			return new RDIDFunction(args);
		if(stType.equals(Statement.EMPTY)) //this is called only in the context of values
			return new RDValue(); //new RD Value with empty sets
		
		return new RDIDFunction(args);

	}

}
