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

public class LCPIDEAssign extends LCPIDEFunction {

	
	private String def="";
	
	private HashSet<String> uses ;
	/**
	 * The order of arguments is as follows for the statement a = b+ c, where c
	 * is a const args[0] = a args[1] = b args[2] = op args[3] = const c
	 * 
	 * for assignment a = c args[0] = a args[1] = c
	 */
	public LCPIDEAssign(ArrayList<String> args) {
		super();

		if (args.size() < 2)
			return;

		def="";
		
		 uses = new HashSet<>();
		String varname = args.get(0);
		
		def = varname;
		
		if (args.size() == 2) {
			String[] rhs = args.get(1).split(",");
			String coeff = rhs[0];
			String rhsVar = rhs[1];

			VarPair pair = new VarPair(rhsVar, varname);
			uses.add(rhsVar);
			LCPRep func = new LCPRep(Integer.parseInt(coeff), 0, new LCPValues(0, false, true));
			funcEdges.put(pair, func);

			ArrayList<String> vars = new ArrayList<>(Globals.DVars);

		//	if(Globals.filename.contains("receive1")){
				vars.remove(varname);
		//	}
		//	else{
		//		if (varname.equals(rhsVar))
		//			vars.remove(varname);
		//	}
			

			for (String var : vars) {
				VarPair idpair = new VarPair(var, var);
				LCPRep func1 = new LCPRep(1, 0, new LCPValues(0, false, true)); // id
				funcEdges.put(idpair, func1);
			}
		} else {
			String op = args.get(2); // assuming it to be plus right now
			String[] rhsOperand1 = args.get(1).split(",");
			String source = rhsOperand1[1];
			String coeff = rhsOperand1[0];
			String[] rhsOperand2 = args.get(3).split(","); // this is a constant
			VarPair pair = new VarPair(source, varname);

			uses.add(source);
			LCPRep func = new LCPRep(Integer.parseInt(coeff), Integer.parseInt(rhsOperand2[1]),
					new LCPValues(0, false, true));
			funcEdges.put(pair, func);

			ArrayList<String> vars = new ArrayList<>(Globals.DVars);

			vars.remove(varname);

			for (String var : vars) {
				VarPair idpair = new VarPair(var, var);
				LCPRep func1 = new LCPRep(1, 0, new LCPValues(0, false, true)); // id
				funcEdges.put(idpair, func1);
			}
		}
		// TODO add sanity checks later

	}
	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return uses;
	}
	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return def;
	}

}
