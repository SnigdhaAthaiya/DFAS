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

public class VarPairValue {

	VarPair pair;
	LCPRep value;

	public VarPairValue(VarPair pair, LCPRep value) {
		super();
		this.pair = new VarPair(pair.getFrom(), pair.getTo());
		this.value = value;
	}

	public VarPair getPair() {
		return pair;
	}

	public void setPair(VarPair pair) {
		this.pair = pair;
	}

	public LCPRep getValue() {
		return value;
	}

	public void setValue(LCPRep value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o==this)
			return true;
		
		if(o== null || !(o instanceof VarPairValue))
			return false;
		
		VarPairValue v = (VarPairValue)o;
		
		if(v.getPair().equals(pair) && v.getValue().equals(value))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return (value.hashCode()+ pair.hashCode());
	}
	
	@Override
	public String toString(){
		return (pair.toString() + " : " + value.toString());
	}

}
