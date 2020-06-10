package tests.iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.HashSet;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;
import iisc.edu.pll.data.lattice.lcp.VarPairValue;

public class VarPairValueTest {
	
	public static void main(String args[])
	{
		VarPair p1 = new VarPair("a", "b");
		VarPair p2 = new VarPair("a", "b");
		
		LCPRep v1 = new LCPRep(0, 0, new LCPValues(0, false, true));
		LCPRep v2 = new LCPRep(0, 0, new LCPValues(0, false, true));
		
		VarPairValue pair1 = new VarPairValue(p1, v1);
		VarPairValue pair2 = new VarPairValue(p2, v2);
		
		System.out.println(p1.equals(p2));
		System.out.println(v1.equals(v2));
		System.out.println(pair1.equals(pair2));
		
		HashSet<VarPairValue> set = new HashSet<>();
		set.add(pair1);
		set.add(pair2);
		System.out.println(set.size());
	}

}
