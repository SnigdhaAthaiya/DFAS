package iisc.edu.pll.expressions;

public class ExpressionPair {

	
	String lhs;
	ExpressionComponent rhs;
	
	
	public ExpressionPair(String lhs, ExpressionComponent rhs) {
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
	public ExpressionComponent getRhs() {
		return rhs;
	}
	public void setRhs(ExpressionComponent rhs) {
		this.rhs = rhs;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ExpressionPair other = (ExpressionPair) obj;
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
	
	
	
}
