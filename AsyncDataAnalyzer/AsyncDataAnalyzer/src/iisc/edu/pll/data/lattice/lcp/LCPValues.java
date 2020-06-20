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

import iisc.edu.pll.data.lattice.AbstractValue;

public class LCPValues implements AbstractValue{

	
		private int value;
		private boolean isBot;
		private boolean isTop;
		
		
		
		@Override
		public String toString() {
			
			if(isBot)
				return "bot";
			if(isTop)
				return "top";
			else
				return ""+value;
		}

		public LCPValues(int value, boolean isBot, boolean isTop) {
			super();
			this.value = value;
			this.isBot = isBot;
			this.isTop = isTop;
		}
		
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public boolean isBot() {
			return isBot;
		}
		
		public void setIsBot(boolean isBot) {
			this.isBot = isBot;
		}
		public boolean isTop() {
			return isTop;
		}
		public void setIsTop(boolean isTop) {
			this.isTop = isTop;
		}

		@Override
		public AbstractValue join(AbstractValue v) {
			LCPValues val = (LCPValues) v;
			
			
			
			if(isBot || val.isBot())
				return new LCPValues(0, true, false);
			if(isTop)
				return val;
			if(val.isTop)
				return this;
			if(value == val.value )
				return this;
			
			//return bot
			return new LCPValues(0, true, false);
				
			
			
			
		}

		
		
		@Override
		public boolean isGreater(AbstractValue v) {
			LCPValues v2 = (LCPValues) v;
			
			if(isBot())
				return true;
			if(v2.isTop())
				return true;
			
			if(!isTop() & !v2.isBot() && (value == v2.value))
				return true;
			
			
			
			return false;
		}

		
		public LCPValues multiply(LCPValues v1)
		{
			if(isBot || v1.isBot)
				return new LCPValues(0, true, false);
			if(isTop || v1.isTop)
				return new  LCPValues(0, false, true);
			else
				return new LCPValues(value *  v1.value, false, false);
		}
		
		public LCPValues add(LCPValues v1)
		{
			if(isBot || v1.isBot)
				return new LCPValues(0, true, false);
			if(isTop || v1.isTop)
				return new  LCPValues(0, false, true);
			else
				return new LCPValues(value + v1.value, false, false);
		}

		@Override
		public int hashCode() {
			int res1= isBot? 5:3;
			int res2 = isTop ? 7 : 11; 
			return (value*13 + res1 + res2);
		}

		@Override
		public boolean equals(Object obj) {
			if(obj==this)
				return true;
			
			if(obj==null || !(obj instanceof LCPValues))
				return false;
			
			LCPValues l = (LCPValues) obj;
			
			return (l.value == value && l.isBot == isBot && l.isTop == isTop);
		}

		@Override
		public boolean isConstantFor(String s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isConstant() {
			// TODO Auto-generated method stub
			return false;
		}
		
		
		
		
}
