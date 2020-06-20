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
package iisc.edu.pll.analysis.assertions;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.VarUseInfo;
import iisc.edu.pll.data.lattice.TFunction;

public class LCPConstantCheck extends ConditionCheck{
	
	
	
	
	public LCPConstantCheck(VarUseInfo vuse) {
		varuse = vuse;
	
	}
	
	@Override
	public boolean check( TFunction val){
		if (val == null) {
			return false;
		}
		
		String[] uses = varuse.getVarUse().split(";");
		int totalUses =0;
		int numOfNonConst = 0;
		
		for (int i = 0; i < uses.length; i++) {
			String vuse = uses[i];
			String[] vuseArr = vuse.split(",");
			for (String var : vuseArr) {
				totalUses++;
				if (val.isNotConstantFor(var)) {					
					numOfNonConst++;
				}
				
			}

		}
		
		if(numOfNonConst == totalUses )
			{
			//System.out.println("check failed");
			return true;}

		
	//	System.out.println("check passed");
		return false;
	}
	

}
