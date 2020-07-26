/*******************************************************************************
 * Copyright (C) 2020 Animesh Kumar
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
package iisc.edu.pll.data.lattice.ara;

import java.util.ArrayList;

import org.ejml.data.DMatrixRMaj;

import iisc.edu.pll.analysis.Globals;

public class ARAIDEID extends ARAIDEFunction{

	public ARAIDEID(ArrayList<String> params)
	{
		super();
		
		//ID will have no edges
	/*	for(String var : Globals.DVars)
		{
			VarPair pair= new VarPair(var, var);
			LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
			funcEdges.addElement(new VarPairValue(pair, value));
		}
		*/
//		if (params.size() < 2)
//			return;
		//Initializing the identity matrix
		 int sizeLimits = Globals.numberOfVariables+1;
	       double[][] inputMatrix= new double[sizeLimits][sizeLimits];
	       for(int i =0;i<sizeLimits;i++){
	           inputMatrix[i][i]=1;
	       }
	       DMatrixRMaj matrixObj = new DMatrixRMaj(inputMatrix);
			this.matrixSet.add(matrixObj);
		
	}

	

}
