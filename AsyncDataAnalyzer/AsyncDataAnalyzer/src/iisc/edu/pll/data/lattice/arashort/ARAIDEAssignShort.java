package iisc.edu.pll.data.lattice.arashort;

import java.util.ArrayList;
/**
 * @author Animesh Kumar
 * Class to convert assignment statement to transfer function
 */

import iisc.edu.pll.analysis.Globals;
public class ARAIDEAssignShort extends ARAIDEFunctionShort {
	
	/**
	 * @author Animesh Kumar
	 * @param args assignment statement(string) in specified format
	 * The order of arguments is as follows for the statement a = b+ c, where c
	 * is a const args[0] = a args[1] = b args[2] = op args[3] = const c
	 * 
	 * for assignment a = c args[0] = a args[1] = c
	 */
	public ARAIDEAssignShort(ArrayList<String> args) {
		super();
		if (args.size() < 2)// invalid format for ID function
			return;
		
		//Initializing the identity matrix
		 	int sizeLimits = Globals.numberOfVariables+1;
	       short[][] inputMatrix= new short[sizeLimits][sizeLimits];

	       for(int i =0;i<sizeLimits;i++){
	           inputMatrix[i][i]=1;
	       }
	      
	       
		String varname = args.get(0);// getting LHS variable name
		int sourceIndex = Globals.ARAVArs.indexOf(varname);// mapping it to index in the matrix
		sourceIndex++;
		 inputMatrix[sourceIndex][sourceIndex]=0;// setting source by source cell to 0

		 // assignment a = c
		if (args.size() == 2) {
			String[] rhs = args.get(1).split(",");
			String coeff = rhs[0];
			String rhsVar = rhs[1];
			try {
				inputMatrix[0][sourceIndex]=Short.parseShort(rhsVar);// for constant term
			} catch (Exception e) {
				// TODO: handle exception
				int targetIndex = Globals.ARAVArs.indexOf(rhsVar)+1;
				inputMatrix[targetIndex][sourceIndex]=Short.parseShort(coeff);// for variable term
			}
			
			}
		//statement a = b+ c
		 else {
			String op = args.get(2); // assuming it to be plus right now
			String[] rhsOperand1 = args.get(1).split(",");
			String source = rhsOperand1[1];
			String coeff = rhsOperand1[0];
			int targetIndex;
			targetIndex = Globals.ARAVArs.indexOf(source)+1;
			inputMatrix[targetIndex][sourceIndex]= Short.parseShort(coeff);
			String[] rhsOperand2 = args.get(3).split(",");
			try {
				inputMatrix[0][sourceIndex] = Short.parseShort(rhsOperand2[1]);// for constant term
			} catch (Exception e) {
				targetIndex = Globals.ARAVArs.indexOf(rhsOperand2[1])+1;
				inputMatrix[targetIndex][sourceIndex]=Short.parseShort(rhsOperand2[0]);// for variable term
				// TODO: handle exception
			}
			
			
			
			}
		 
		this.matrixSet.add(inputMatrix);
	
	}
	}


