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

import java.util.Set;

import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public abstract class RDTFunction implements TFunction {

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction join(TFunction f) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public boolean isGreater(TFunction v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsBot(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isBot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsTop(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TFunction makeDeepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNotConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

}
