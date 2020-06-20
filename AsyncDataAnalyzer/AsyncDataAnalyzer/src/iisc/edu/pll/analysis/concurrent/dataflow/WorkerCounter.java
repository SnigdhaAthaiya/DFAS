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
package iisc.edu.pll.analysis.concurrent.dataflow;

public class WorkerCounter {
	
	long count;
	boolean allowedChange = true;
	
	public WorkerCounter(){
		count =0;
	}

	public synchronized long getCount() {
		return count;
	}

	public synchronized void setCount(long count) {
		this.count = count;
	}
	
	public synchronized void setCountToZeroAndStop(){
		this.count = 0;
		this.allowedChange = false;
	}
	public synchronized boolean isZero(){
		return (count == 0);
	}
	
	//will this incur additional cost?
	public synchronized void incCount(){
		count = allowedChange ? count+1 : count;
	}
	
	public synchronized void decCount(){
		count = allowedChange ? count-1 : count;
	}
	

}
