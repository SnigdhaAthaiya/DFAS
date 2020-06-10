package tests.iisc.edu.pll.data.lattice;

import java.util.ArrayList;

import iisc.edu.pll.data.lattice.LCPIDE;

public class LCPIDETest {

	public static void main(String[] args) {
		
		ArrayList<String> vars = new ArrayList<>();
		vars.add("a");
		vars.add("b");
		
		 LCPIDE l1 = new LCPIDE(vars);
		 l1.handleAssign("a", null, "1,3", null);
		 LCPIDE l2 = new LCPIDE(vars);
		 l2.handleAssign("b", "+", "1,a", "1");
		 LCPIDE l3 = (LCPIDE)l1.compose(l2);
		 System.out.println(l3);
		 

	}

}
