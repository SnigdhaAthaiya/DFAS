package tests.iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.concurrent.dataflow.JoinedFunctionMatrix;
import iisc.edu.pll.analysis.concurrent.dataflow.JoinedFunctionMatrixFactory;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunction;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunctionFactory;

public class JoinedFunctionMatrixTest {

	
	public static void main(String args[])
	{
		int numOfCounters = 4; //2 dimensions
		int initSize = 2;  // size of each dim
		int[] sizes = new int[numOfCounters];
		Globals.numberOfCounters = numOfCounters;
		Arrays.fill(sizes, initSize);
		JoinedFunctionMatrix mat = JoinedFunctionMatrixFactory.getJoinedFunctionMatrix(sizes);
		ArrayList<String> vars = new ArrayList<>();
		vars.add(Globals.lambda);
		vars.add("a");
		vars.add("b");
		Globals.DVars = vars;
		ArrayList<String> arguments = new ArrayList<>();
		arguments.add("a");
		arguments.add("1,4");
		arguments.add("+");
		arguments.add("1,1");
		
		ArrayList<String> arguments2 = new ArrayList<>();
		arguments2.add("b");
		arguments2.add("1,a");
		arguments2.add("+");
		arguments2.add("1,5");
		LCPIDEFunction f1 = LCPIDEFunctionFactory.createFunction(Statement.ASSIGN, arguments2);
		LCPIDEFunction f2 = LCPIDEFunctionFactory.createFunction(Statement.ASSIGN, arguments2);
		int[] dem1 = {0,0,0,0};
		int[] dem2 = {1,1,1,1};
		
		mat.set(f1, dem1);
		mat.set(f2, dem2);
		int[] dem3 = {1,0,0,0};
		TFunction f3 = mat.get(dem2);
		TFunction f4 = mat.get(dem3);
		
		
		
		//System.out.println("printing matrix");
		//System.out.println(mat);
		System.out.println(f3);
	//	System.out.println(f4);
		
		
		int[] dem4 = {0,0,2,0};
		mat.set(f2, dem4);
		
		System.out.println("getting value : " + mat.get(dem4));
		
		System.out.println(mat);
		System.out.println("printing lowers");
		List<TFunction> lowers =  mat.getAllLower(dem2);
		LCPIDEFunction f6 = LCPIDEFunctionFactory.createFunction(Statement.EMPTY, new ArrayList<>());
		for(TFunction f5 : lowers)
		{
			System.out.println(f5);
			f6 = (LCPIDEFunction) f6.join(f5);
		}
		
		System.out.println(f1.isGreater(f6));
		
		
		
		
	}
}
