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
package iisc.edu.pll.exceptions;

import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel.SinglePathExtender;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.analysis.concurrent.dataflow.WorkerUnit;

public class DFRejectedException implements RejectedExecutionHandler {
	
	private static int count = 0;
	@Override
	public void rejectedExecution(Runnable r,
            ThreadPoolExecutor executor)
	{
		count++;
		DataFlowParallel.SinglePathExtender worker = (SinglePathExtender) r;
		WorkerUnit wu = worker.getToExtend();
		ProductState p = wu.getState();
		//System.out.println(count+": rejected the task with product state : " + p + " and demand :");
		Vector<Integer> dem = wu.getDemand();
		
	//	System.out.println(dem);
	//	System.out.println();
	}

}
