package iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.TFunction;

public class JoinedFunctionMap {
	
	ConcurrentHashMap<Vector<Integer>, TFunction> jfmap ;
	
	public JoinedFunctionMap(){
		jfmap = new ConcurrentHashMap<>();
	}

	public ConcurrentHashMap<Vector<Integer>, TFunction> getJfmap() {
		return jfmap;
	}

	public void setJfmap(ConcurrentHashMap<Vector<Integer>, TFunction> jfmap) {
		this.jfmap = jfmap;
	}

	@Override
	public synchronized String toString()
	{
		StringBuffer content = new StringBuffer();
		
		for(Vector<Integer> key : jfmap.keySet()){
			content.append("Demand : " + key +"\n");
			content.append("Value : " + jfmap.get(key) + "\n");
		}
		content.append("\n");
		
		return content.toString();	
		
	}
	
	@Override
	public synchronized boolean equals(Object obj)
	{
		if(this== obj)
			return true;
		
		if(obj==null || !(obj instanceof JoinedFunctionMap))
			return false;
		
		JoinedFunctionMap jmapObj = (JoinedFunctionMap) obj;
		return jfmap.equals(jmapObj.jfmap);
	}

}
