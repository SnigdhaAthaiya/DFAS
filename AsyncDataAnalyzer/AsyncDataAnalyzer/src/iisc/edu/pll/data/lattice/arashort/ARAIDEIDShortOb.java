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
package iisc.edu.pll.data.lattice.arashort;

import java.util.ArrayList;
import iisc.edu.pll.analysis.Globals;
/**
 * 
 * @author Animesh Kumar
 * Class to initialize an ID Transfer function
 *
 */
public class ARAIDEIDShortOb extends ARAIDEFunctionShortMat{
	/**
	 * Constructor to initialise an ID transfer function
	 * @param params String having the assignment statement in specified format
	 */
	public ARAIDEIDShortOb(ArrayList<String> params)
	{
		super();
		
		
		//Initializing the identity matrix
		 int sizeLimits = Globals.numberOfVariables+1;
	       short[][] inputMatrix= new short[sizeLimits][sizeLimits];
	       for(int i =0;i<sizeLimits;i++){
	           inputMatrix[i][i]=1;// diagonal elements set to 1
	       }
			this.matrixSet.add(new ShortMatrix(inputMatrix));
		
	}

	

}
