package tests.iisc.edu.pll.data.lattice;

import java.util.HashMap;

import iisc.edu.pll.data.lattice.lcp.LCPRep;
import iisc.edu.pll.data.lattice.lcp.LCPRepID;
import iisc.edu.pll.data.lattice.lcp.LCPValues;

public class LCPRepTest {

	// the composition of non-const to const will never happen as no edge goes
	// to lambda
	public static void main(String args[]) {
		LCPRep l1 = new LCPRep(1, 5, new LCPValues(0, false, true));
		LCPRep l2 = new LCPRep(0, 3, new LCPValues(0, false, true));

		System.out.println(l1.isGreater(l2));
		System.out.println(l2.isGreater(l1));

		LCPRep l3 = new LCPRep(1, 1, new LCPValues(0, false, true));

		System.out.println(l1.compose(l2));
		System.out.println(l2.compose(l1));

		System.out.println(l1.join(l2));
		
		

	}

}
