package tests.iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunction;
import iisc.edu.pll.data.lattice.lcp.LCPIDEFunctionFactory;

public class JoinedFunctionMapTest {
	
	public static void main(String args[])
	{
		int size = 2;
		Vector<Integer> v1 = new Vector<>();
		v1.add(0);
		v1.add(0);
		
		Vector<Integer> v2 = new Vector<>();
		v2.add(0);
		v2.add(1);
		
		
		Vector<Integer> v3 = new Vector<>();
		v3.add(1);
		v3.add(1);
		
		ArrayList<String> vars = new ArrayList<>();
		vars.add("a");
		vars.add("b");
		Globals.DVars = new ArrayList<>();
		Globals.DVars.add(Globals.lambda);
		Globals.DVars.addAll(vars);
		
		Globals.numberOfCounters = 2;
		
		ArrayList<String> arguments = new ArrayList<>();
		arguments.add("a");
		arguments.add("1,4");
		
		
		ArrayList<String> arguments2 = new ArrayList<>();
		arguments2.add("a");
		arguments2.add("1,5");
		
		ArrayList<String> arguments3 = new ArrayList<>();
		arguments3.add("a");
		arguments3.add("1,3");
		
		LCPIDEFunction f1 = LCPIDEFunctionFactory.createFunction(Statement.CONSTASSIGN, arguments);
		LCPIDEFunction f2 = LCPIDEFunctionFactory.createFunction(Statement.CONSTASSIGN, arguments2);
		LCPIDEFunction f3 = LCPIDEFunctionFactory.createFunction(Statement.CONSTASSIGN, arguments3);
		
		HashMap<Vector<Integer>, TFunction> smap = new HashMap<>();
		smap.put(v1, f1);
		smap.put(v2, f2);
		smap.put(v3, f3);
		TFunction joinedFunc = LCPIDEFunctionFactory.createFunction(Statement.ID, new ArrayList<>());
		List<TFunction> ldfunc = getAllLower(smap, v3);
		for(int i =0; i< ldfunc.size();i++)
			joinedFunc = joinedFunc.join(ldfunc.get(i));
		
		System.out.println(joinedFunc);
			
		
				
	}
	
	private static List<TFunction> getAllLower(HashMap<Vector<Integer>, TFunction> sourceMap, Vector<Integer> dVector) {
		List<TFunction> ldFuncs = new ArrayList<>();
		synchronized (sourceMap) {
			for (Vector<Integer> vec : sourceMap.keySet()) {
				synchronized (vec) {
					if (isLower(vec, dVector))
						ldFuncs.add(sourceMap.get(vec));
				}

			}
		}

		return ldFuncs;

	}

	// no need to lock dvector, as the vector content does not change
	private static boolean isLower(Vector<Integer> vec, Vector<Integer> dVector) {

		for (int i = 0; i < Globals.numberOfCounters; i++) {
			if (vec.get(i) > dVector.get(i))
				return false;
		}
		return true;
	}

}
