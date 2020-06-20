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
package iisc.edu.pll.data.lattice;

import java.util.ArrayList;

import iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionFactoryShortOb;
import iisc.edu.pll.data.lattice.lcp.CCPIDEFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunctionFactory;
import iisc.edu.pll.data.lattice.reachingdef.RDFunctionFactory;
import iisc.edu.pll.lattice.cp.CPFunctionFactory;

public class TFunctionFactory {
	
	
	public static TFunction createFunction(String type, String stType, ArrayList<String> vars, ArrayList<String> args){
		 if(type.equalsIgnoreCase("lcp"))
		 {
			 return LCPIDEFunctionFactory.createFunction(stType, args);
			 
		 }
			 
		 if(type.equalsIgnoreCase("ara"))
		 {
			 return ARAIDEFunctionFactoryShortOb.createFunction(stType, args);
			 
		 }
		 
		 if(type.equalsIgnoreCase("cp"))
		 {
			 return CPFunctionFactory.createFunction(stType, args);
		 }
		 
		 if(type.equalsIgnoreCase("rd"))
		 {
			 return RDFunctionFactory.createFunction(stType, args);
		 }
		 
		 if(type.equalsIgnoreCase("ccp"))
		 {
			 return CCPIDEFunctionFactory.createFunction(stType, args);
		 }
		 return null;
		
	}

}
