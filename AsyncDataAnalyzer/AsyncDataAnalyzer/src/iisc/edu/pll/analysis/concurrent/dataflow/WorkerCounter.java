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
