package tests.iisc.edu.pll.analysis.concurrent.dataflow;

import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.data.ModuleNode;

public class ProductStateTest {

	public static void main(String[] args) {
		ModuleNode n1 = new ModuleNode();
		ModuleNode n2 = new ModuleNode();
		ModuleNode n3 = new ModuleNode();
		ModuleNode[] arr = {n1,n2};
		ModuleNode[] arr1 = {n1,n2};
		ProductState s1 = new ProductState(arr);
		ProductState s2 = new ProductState(arr1);
		System.out.println(s1.equals(s2));
		System.out.println(s2.equals(s1));
		System.out.println(s1.equals(s1));
		System.out.println(arr.hashCode());
		System.out.println(arr1.hashCode());

	}

}
