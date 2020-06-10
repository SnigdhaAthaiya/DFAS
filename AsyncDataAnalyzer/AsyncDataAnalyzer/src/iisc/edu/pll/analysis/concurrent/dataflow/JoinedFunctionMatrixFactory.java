package iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.Vector;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.TFunction;

public class JoinedFunctionMatrixFactory {
	
	
	public static JoinedFunctionMatrix getJoinedFunctionMatrix( int[] sizes){
		 
		Vector mat = constructMatrix(Globals.numberOfCounters, sizes);
		return new JoinedFunctionMatrix(mat, sizes); //return new matrix
		
	}

	private static Vector constructMatrix(int level, int[] sizes) {
		 
		
		if (level == 1){
				Vector head = new Vector<TFunction>(10);
				for (int i = 0; i < sizes[level]; i++)
					 head.add(null);
				return head;
			 }
			 
			 level = level - 1;
			 Vector head = new Vector<>(sizes[level]);
			 for (int i = 0; i < sizes[level]; i++) 
				 head.add(constructMatrix(level, sizes));
			 return head; 



	}

}
