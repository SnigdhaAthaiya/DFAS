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
