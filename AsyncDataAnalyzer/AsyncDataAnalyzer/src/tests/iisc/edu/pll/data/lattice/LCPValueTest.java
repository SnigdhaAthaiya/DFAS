package tests.iisc.edu.pll.data.lattice;

import iisc.edu.pll.data.lattice.lcp.LCPValues;

public class LCPValueTest {
	
	public static void main(String args[])
	{
		LCPValues l1 = new LCPValues(1, false, false);
		LCPValues l2 = new LCPValues(2, false, true);
		
		System.out.println(l1.add(l2));
		System.out.println(l1.multiply(l2));
	}

}
