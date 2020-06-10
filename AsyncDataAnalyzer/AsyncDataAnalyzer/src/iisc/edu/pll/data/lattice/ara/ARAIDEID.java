package iisc.edu.pll.data.lattice.ara;

import java.util.ArrayList;

import org.ejml.data.DMatrixRMaj;

import iisc.edu.pll.analysis.Globals;

public class ARAIDEID extends ARAIDEFunction{

	public ARAIDEID(ArrayList<String> params)
	{
		super();
		
		//ID will have no edges
	/*	for(String var : Globals.DVars)
		{
			VarPair pair= new VarPair(var, var);
			LCPRep value = new LCPRep(1, 0, new LCPValues(0, false, true));
			funcEdges.addElement(new VarPairValue(pair, value));
		}
		*/
//		if (params.size() < 2)
//			return;
		//Initializing the identity matrix
		 int sizeLimits = Globals.numberOfVariables+1;
	       double[][] inputMatrix= new double[sizeLimits][sizeLimits];
	       for(int i =0;i<sizeLimits;i++){
	           inputMatrix[i][i]=1;
	       }
	       DMatrixRMaj matrixObj = new DMatrixRMaj(inputMatrix);
			this.matrixSet.add(matrixObj);
		
	}

	

}
