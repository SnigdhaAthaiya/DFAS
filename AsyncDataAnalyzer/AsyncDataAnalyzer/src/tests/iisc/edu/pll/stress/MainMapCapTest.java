package tests.iisc.edu.pll.stress;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class MainMapCapTest {

	private static final int MegaBytes = 10241024;

	public static void main(String[] args) {

		ConcurrentHashMap<ProductState, ConcurrentHashMap<Vector<Integer>, TFunction>> stateToValMap = new ConcurrentHashMap<ProductState, ConcurrentHashMap<Vector<Integer>, TFunction>>();

		long totalMemory = Runtime.getRuntime().totalMemory() / MegaBytes;
		
		System.out.println("initial JVM Memory : " + totalMemory);
		ArrayList<String> vars = new ArrayList<>();
		vars.add(Globals.lambda);
		vars.add("a");
		vars.add("b");
		vars.add("c");
		vars.add("d");
		vars.add("e");
		/*vars.add("f");
		vars.add("g");
		vars.add("h");
		vars.add("i");
		vars.add("j");
		vars.add("k");
		vars.add("l");*/

		Globals.DVars = vars;

		int mainSize = 225;
		int rowSize = 1;
		int sizeOfVec = 10;
		for (int i = 1; i <= mainSize; i++) {
			for (int j = 1; j <= mainSize; j++) {
				ModuleNode n1 = new ModuleNode();
				ModuleNode n2 = new ModuleNode();
				ModuleNode[] stateVec = { n1, n2 };
				ProductState state = new ProductState(stateVec);
				ConcurrentHashMap<Vector<Integer>, TFunction> sourcemap = getMap(rowSize, sizeOfVec);
				stateToValMap.put(state, sourcemap);
				// System.out.println("done state :" + state);
				// System.out.println("i,j :" + i +", " + j);
			}
		}
		totalMemory = Runtime.getRuntime().totalMemory() / MegaBytes;
		System.out.println("done adding " + stateToValMap.mappingCount() + " entries");
		System.out.println("totalMemory in JVM shows current size of java heap : " + totalMemory);
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static ConcurrentHashMap<Vector<Integer>, TFunction> getMap(int rowSize, int sizeOfVec) {
		ConcurrentHashMap<Vector<Integer>, TFunction> smap = new ConcurrentHashMap<Vector<Integer>, TFunction>();

		for (int i = 0; i < rowSize; i++) {
			Vector<Integer> vec = new Vector<>();
			vec = fillVector(vec, sizeOfVec);
			vec.set(0, i);

			ArrayList<String> arguments = new ArrayList<>();
			arguments.add("a");
			arguments.add("1," + i);

		//	TFunction fun = TFunctionFactory.createFunction("lcp", Statement.CONSTASSIGN, new ArrayList<>(), arguments);
			TFunction fun = TFunctionFactory.createFunction("lcp","test13", new ArrayList<>(), arguments);
			smap.put(vec, fun);
			//System.out.println("putting :" + fun + " for vector "  + vec);
		}

		//System.out.println("number of rows :" + smap.mappingCount());
		return smap;
	}

	private static Vector<Integer> fillVector(Vector<Integer> vec, int val) {
		for (int i = 0; i < val; i++) {
			vec.addElement(0);

		}
		return vec;
	}

}
