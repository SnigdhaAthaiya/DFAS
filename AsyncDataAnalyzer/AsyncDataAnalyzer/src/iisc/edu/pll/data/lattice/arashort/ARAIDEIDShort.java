package iisc.edu.pll.data.lattice.arashort;

import java.util.ArrayList;
import iisc.edu.pll.analysis.Globals;

/**
 * 
 * @author Animesh Kumar
 * Class to initialize an ID Transfer function
 *
 */
public class ARAIDEIDShort extends ARAIDEFunctionShort{
/**
 * Constructor to initialise an ID transfer function
 * @param params String having the assignment statement in specified format
 */
	public ARAIDEIDShort(ArrayList<String> params)
	{
		super();
		
		
		//Initializing the identity matrix
		 int sizeLimits = Globals.numberOfVariables+1;
	       short[][] inputMatrix= new short[sizeLimits][sizeLimits];
	       for(int i =0;i<sizeLimits;i++){
	           inputMatrix[i][i]=1;// diagonal elements set to 1
	       }
			this.matrixSet.add(inputMatrix);
		
	}

	

}
