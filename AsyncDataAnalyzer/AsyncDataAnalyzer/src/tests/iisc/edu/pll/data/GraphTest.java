package tests.iisc.edu.pll.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class GraphTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		ModuleNode n1 = new ModuleNode();
		ModuleNode n2 = new ModuleNode();
		ModuleGraph g = new ModuleGraph(n1, n2);
		
		HashSet<ModuleEdge> set = new HashSet<>();
		
		ArrayList<String> vars = new ArrayList<>();
		vars.add("a");
		ModuleEdge e1 = new ModuleEdge(n1, n2, TFunctionFactory.createFunction("lcp", Statement.ID, vars, new ArrayList<>()),
				new HashMap<>());
		
		ModuleEdge e2 = new ModuleEdge(n1, n2, TFunctionFactory.createFunction("lcp", Statement.ID, vars, new ArrayList<>()),
				new HashMap<>());
		g.addEdge(e1);

		set.add(e1);
		System.out.println(set.contains(e2));

		g.addEdge(e2);
		
		System.out.println(e1.equals(e2));
		g.printGraph();
	}

}
