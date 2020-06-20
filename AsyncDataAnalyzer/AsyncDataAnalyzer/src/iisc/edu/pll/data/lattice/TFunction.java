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

import java.util.HashSet;
import java.util.Set;

public interface TFunction {
	
	//apply the function
	public AbstractValue apply(AbstractValue v);
	
	public  TFunction join(TFunction f);
	
	public TFunction compose(TFunction f);
		
	public boolean isGreater(TFunction v);
	
	
	public void setIsBot(boolean b) ;
	public boolean isBot() ;

	
	public void setIsTop(boolean b);
	public boolean isTop() ;

	public TFunction makeDeepCopy();
	
	public int getSize();

	public boolean isConstantFor(String use);
	public boolean isNotConstantFor(String use);

	public boolean isID();
	
	public Set<String> getUse();
	
	public String getDef();
	
	
	
	
	
	
	
	
	
	
	
}
