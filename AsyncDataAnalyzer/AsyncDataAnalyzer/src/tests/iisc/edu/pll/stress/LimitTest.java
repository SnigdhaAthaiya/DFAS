package tests.iisc.edu.pll.stress;

public class LimitTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		long lprod1 = 114*78*18;
		long lprod2 = 18*110*110;
		int prod = 114*78*18*18*110*110;
		long lprod = 114*78*18*18*110*110;
		double dprod= 114*78*18*18*110*110;
		
		long flprod = lprod1*lprod2;
		System.out.println(prod);
		System.out.println(lprod);
		System.out.println(dprod);
		System.out.println(flprod);
		System.out.println((int) flprod);
		
	}

}
