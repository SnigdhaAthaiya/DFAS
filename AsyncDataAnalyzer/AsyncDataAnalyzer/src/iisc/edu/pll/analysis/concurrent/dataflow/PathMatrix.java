package iisc.edu.pll.analysis.concurrent.dataflow;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;

public class PathMatrix {

	//have an explored and unexplored multidim matrix here
	
	Object pathsWithDemand;
	int[] sizes; //size of matrix in each dimension
	int dim ; //number of dimensions
	
	//sizes.length == dim
	public PathMatrix(int dim, int[]sizes)
	{
		this.sizes = sizes;
		this.dim = dim;
		this.pathsWithDemand= Array.newInstance(DPath.class, sizes);
	
	}
	
	public PathMatrix(int dim, int size)
	{
		this.dim = dim;
		sizes = new int[dim];
		for(int i=0;i<dim;i++)
			sizes[i] = size;
		
		this.pathsWithDemand= Array.newInstance(DPath.class, sizes);
		
	}

	public void addPath(DPath path) {
		//get location
		int[] location = path.getDemand();
		set(path, location);
		
	}

	
	/**Returns the item at entry location[0], location[1],...location[n]
	 * the innermost location is given by the greatest index*/
	public synchronized void set(DPath path, int[] location) {
		int level = 0;
		correctSize(location);
		Object arr = Array.get(pathsWithDemand, location[level]);
		level++;
		while(level<= dim - 2)
		{
			arr = Array.get(arr, location[level]);
			level++;
		}
		Array.set(arr, location[level], path);
		
	}
	
	//this method is internally going to increase the size of the array on demand
	//rest of the methods on the same object will not be allowed to start
	private synchronized void correctSize(int[] location) {
		int[] maxSize = new int[dim];
		boolean flag = true;
		for(int i=0; i< dim; i++)
		{
			if(location[i] > sizes[i])
			{
				maxSize[i] = location[i];
				flag = false;
			}
			else
				maxSize[i] = sizes[i];
						
		}
		if(flag)
			return ;
		
		Object arrPrev = Array.newInstance(DPath.class, maxSize);
		//sizes = maxSize;
		makeDeepCopy(arrPrev, 0);
		//correcting
		sizes  = maxSize;
		pathsWithDemand = arrPrev;
		
		
	}

	private synchronized void makeDeepCopy(Object arrPrev, int level) {
		if(level ==dim-1)
		{
			for(int i=0 ; i < sizes[level]; i++)
			{
				Array.set(arrPrev, i, Array.get(pathsWithDemand, i)) ;
			}
		}
		else
		{
			for(int i =0; i< sizes[level]; i++)
			{
				makeDeepCopy(Array.get(arrPrev, i), level - 1);
			}
		}
		
	}

	public  synchronized  DPath get(int[] location)
	{
		int level = 0;
		Object arr = Array.get(pathsWithDemand, location[level]);
		level++;
		while(level <= dim-2)
		{
			arr = Array.get(arr, location[level]);
			level++;
		}
		
		return (DPath)Array.get(arr, location[level]);
	}

	
	
	public synchronized HashSet<DPath> getAllLower(int[] newDemand, int level) {
		HashSet<DPath> set = new HashSet<>();
		set  = getAllLower(pathsWithDemand, newDemand, level);
		return set;
	}
	
	private synchronized HashSet<DPath> getAllLower(Object arr, int[] newDemand, int level)
	{
		
		HashSet<DPath> set = new HashSet<>();
		
		if(level == dim-1) //inner most level
		{
			for(int i = 0; i< newDemand[level]; i++)
				if(Array.get(arr,i) !=null)
				{
					set.add(new DPath((DPath)Array.get(arr, i))); //making a deep copy					
				}
				
		}
		else{
			//recurse
			for(int i = 0; i< newDemand[level]; i++)
			{
				set.addAll(getAllLower(Array.get(arr, i), newDemand, level+1));
			}
		}
		return set;
	}

	@Override
	public String toString() {
		String content = Arrays.deepToString((Object[]) pathsWithDemand);
		return content;
	}
	
	
	
	//TODO override hashcode and equals
}
