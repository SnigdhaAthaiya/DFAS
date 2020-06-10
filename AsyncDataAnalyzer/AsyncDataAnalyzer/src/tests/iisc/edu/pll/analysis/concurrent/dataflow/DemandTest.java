package tests.iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.Collections;
import java.util.Vector;

public class DemandTest {
	
	public static void main(String args[])
	{
		int size = 5;
		int[] a1 = new int[size];
		int[] a2 = new int[size];
		
		System.out.println(a1.equals(a2));
		
		Vector<Integer> v1 = new Vector<>(size);
		Collections.fill(v1, new Integer(1));
		Vector<Integer> v2 = new Vector<>(size);
		Collections.fill(v2, new Integer(1));
		System.out.println(v1.equals(v2));
		System.out.println(v1.size());
		System.out.println(v2.size());
		
		System.out.println(v1);
		v1.addElement(1);
		System.out.println(v1);
		
	}

}
