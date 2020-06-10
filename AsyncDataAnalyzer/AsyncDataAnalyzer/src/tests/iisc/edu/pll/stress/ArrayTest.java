package tests.iisc.edu.pll.stress;

import java.util.HashMap;

public class ArrayTest {

	public static void main(String[] args) {
		byte[] a1 = new byte[1];
		byte[] a2 = new byte[1];
		
		System.out.println(a1.equals(a2));
		
		a1[0]= 1;
		a2[0] =1;
		
		System.out.println(a1.equals(a2));
		
		System.out.println(java.util.Arrays.equals(a1, a2));
		
		HashMap<byte[], String> map = new HashMap<>();
		
		map.put(a1, "hello");
		map.put(a2, "world");
		
		System.out.println(map.get(a1));
		System.out.println(map.get(a2));

	}

}
