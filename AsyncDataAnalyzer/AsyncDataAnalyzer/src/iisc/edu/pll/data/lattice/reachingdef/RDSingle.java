package iisc.edu.pll.data.lattice.reachingdef;

public class RDSingle {

	//the variable being defined
	String lhs;
	
	//the definition- can be const, variable or unhandled/other
	RDRHS rhs;
	

	public RDSingle(String lhs, RDRHS rhs) {
		super();
		this.lhs = lhs;
		this.rhs = rhs;
		
	}

	public String getLhs() {
		return lhs;
	}

	public void setLhs(String lhs) {
		this.lhs = lhs;
	}

	public RDRHS getRhs() {
		return rhs;
	}

	public void setRhs(RDRHS rhs) {
		this.rhs = rhs;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result ;
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RDSingle other = (RDSingle) obj;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		if (rhs == null) {
			if (other.rhs != null)
				return false;
		} else if (!rhs.equals(other.rhs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RDSingle [lhs=" + lhs + ", rhs=" + rhs +  "]";
	}

		
}

