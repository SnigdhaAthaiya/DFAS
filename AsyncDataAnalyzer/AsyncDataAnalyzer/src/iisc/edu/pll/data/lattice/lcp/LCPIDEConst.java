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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public class LCPIDEConst extends LCPIDEFunction {

	private String def;
	
	public LCPIDEConst(ArrayList<String> args) {
		super();
		def = "";
		if (args.size() < 2)
			return;

		// TODO add sanity checks later
		String varname = args.get(0);
		def=varname;
		String[] constTerm = args.get(1).split(",");
		int value = Integer.parseInt(constTerm[1].trim());
		VarPair pair = new VarPair(Globals.lambda, varname);

		LCPRep func = new LCPRep(0, value, new LCPValues(0, false, true));
		funcEdges.put(pair, func);
		ArrayList<String> vars = new ArrayList<>(Globals.DVars);

		vars.remove(varname); //remove the ID edge for the variable being written into

		for (String var : vars) {
			VarPair idpair = new VarPair(var, var);
			LCPRep func1 = new LCPRep(1, 0, new LCPValues(0, false, true)); // id
			funcEdges.put(idpair, func1);
		}

	}
	
	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return def;
	}


}
