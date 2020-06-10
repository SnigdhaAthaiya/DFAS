package tests.iisc.edu.pll.data.lattice;

import java.util.concurrent.ConcurrentHashMap;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPValues;
import iisc.edu.pll.data.lattice.lcp.VarPair;

public class LCPRepVarPairTest {

	public static void main(String[] args) {
		VarPair p1 = new VarPair("a", "b");
		VarPair p2 = new VarPair("a", "b");
		
		LCPRep val = new LCPRep(0, 3, new LCPValues(0, false, true));
		
		ConcurrentHashMap<VarPair, LCPRep> map  = new ConcurrentHashMap<VarPair, LCPRep>();
		
		map.put(p1, val);
		
		System.out.println(map.get(p2));

	}

}
