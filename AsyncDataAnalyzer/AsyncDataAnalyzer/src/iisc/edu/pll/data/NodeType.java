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
package iisc.edu.pll.data;

public class NodeType {

	
	public static final byte ENTRY=0;
	public static final byte EXIT=1;
	public static final byte RETSITE=2;
	public static final byte CALLSITE=3;
	public static final byte OTHER = 4;
	
	private byte type;
	private String procName;
	
	
	public NodeType(byte type, String procName) {
		
		this.type = type;
		this.procName = procName;
	}


	public byte getType() {
		return type;
	}


	public void setType(byte type) {
		this.type = type;
	}


	public String getProcName() {
		return procName;
	}


	public void setProcName(String procName) {
		this.procName = procName;
	}


	@Override
	public String toString() {
		return "NodeType [type=" + type + ", procName=" + procName + "]";
	}
	
	
}
