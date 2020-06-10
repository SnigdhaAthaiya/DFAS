package tests.iisc.edu.pll.data.lattice;

import iisc.edu.pll.data.lattice.lcp.LCPValues;

public class LCPValuesTest {

	public static void main(String[] args) {
		LCPValues l1 = new LCPValues(1, true, false);
		LCPValues l2 = new LCPValues(1, true, false);
		
		System.out.println(l1.equals(l2));
		System.out.println(l2.equals(l1));
		System.out.println(l1.equals(l1));

	}

}
