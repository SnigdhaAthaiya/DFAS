package iisc.edu.pll.data.lattice.lcp;

public class LCPRepID extends LCPRep{

	public LCPRepID(boolean b) {
		super(b);
		this.c = null;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj==this)
			return true;
		
		if(obj==null || !(obj instanceof LCPRepID))
			return false;
		
		LCPRepID l =(LCPRepID) obj;
		return(l.a == a && l.b==b && l.c ==null && c==null );
	}
	@Override
	public int hashCode() {
		
		return (a + b +13);
	}

}
