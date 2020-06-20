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
package iisc.edu.pll.lattice.cp;

import iisc.edu.pll.data.lattice.AbstractValue;


public class ConstantPropagationSingle implements AbstractValue {

	
	//here we use the PAV meanings of bot and top
	//top means not constant
	//bot means unreachable

	private int value;
	private byte isBot; //treat them as boolean 0 for false, 1 for true
	private byte isTop; //treat them as boolean 0 for false, 1 for true
	
	
	
	@Override
	public String toString() {
		
		if(isBot == 1)
			return "bot";
		if(isTop == 1)
			return "top";
		else
			return ""+value;
	}

	public ConstantPropagationSingle(int value, boolean isBot, boolean isTop) {
		super();
		this.value = value;
		this.isBot = (byte)(isBot ? 1 : 0);
		this.isTop = (byte)(isTop ?  1 : 0);
	}
	
	public ConstantPropagationSingle(ConstantPropagationSingle cps) {
		this.value = cps.value;
		this.isBot = cps.isBot;
		this.isTop = cps.isTop;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean isBot() {
		return (isBot == 1);
	}
	
	public void setIsBot(boolean isBot) {
		this.isBot = (byte)(isBot ? 1 : 0);
		
	}
	public boolean isTop() {
		return (isTop == 1);
	}
	public void setIsTop(boolean isTop) {
		this.isTop = (byte)(isTop ? 1 : 0);
	}

	@Override
	public AbstractValue join(AbstractValue v) {
		ConstantPropagationSingle val = (ConstantPropagationSingle) v;
		
		if(isTop() || val.isTop())
			return new ConstantPropagationSingle(0, false, true);
		
		if(isBot())
			return new ConstantPropagationSingle(val) ;
		
		if(val.isBot())
			return new ConstantPropagationSingle(this);
		
		if(value ==  val.value)
			return new ConstantPropagationSingle(this);
		
		//return top
		return new ConstantPropagationSingle(0, false, true);
	}

	
	
	@Override
	public boolean isGreater(AbstractValue v) {
		ConstantPropagationSingle v2 = (ConstantPropagationSingle) v;
		
		if(isTop())
			return true;
		if(v2.isBot())
			return true;
		
		if(!isBot() & !v2.isTop() && (value == v2.value))
			return true;
		
		return false;
	}

	
	public ConstantPropagationSingle multiply(ConstantPropagationSingle v1)
	{
		if(isTop() || v1.isTop())
			return new  ConstantPropagationSingle(0, false, true);
		
		if(isBot() || v1.isBot())
			return new ConstantPropagationSingle(0, true, false);		
		else
			return new ConstantPropagationSingle(value *  v1.value, false, false);
	}
	
	public ConstantPropagationSingle add(ConstantPropagationSingle v1)
	{
		if(isTop() || v1.isTop())
			return new  ConstantPropagationSingle(0, false, true);
		
		if(isBot() || v1.isBot())
			return new ConstantPropagationSingle(0, true, false);		
		else
			return new ConstantPropagationSingle(value + v1.value, false, false);
	}

	@Override
	public int hashCode() {
		int res1= isBot()? 5:3;
		int res2 = isTop() ? 7 : 11; 
		return (value*13 + res1 + res2);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		
		if(obj==null || !(obj instanceof ConstantPropagationSingle))
			return false;
		
		ConstantPropagationSingle l = (ConstantPropagationSingle) obj;
		
		return (l.value == value && l.isBot == isBot && l.isTop == isTop);
	}

	@Override
	public boolean isConstantFor(String s) {
		if(!isTop() && !isBot())
			return true;
		return false;
	}

	@Override
	public boolean isConstant() {
		if(!isTop() && !isBot())
			return true;
		return false;
	}
	
	
	
	

}
