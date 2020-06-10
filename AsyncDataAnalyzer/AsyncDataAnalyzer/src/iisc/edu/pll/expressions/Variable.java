package iisc.edu.pll.expressions;

public class Variable extends ExpressionComponent {

	int coeff;
	String var;
	public int getCoeff() {
		return coeff;
	}
	public void setCoeff(int coeff) {
		this.coeff = coeff;
	}
	public String getVar() {
		return var;
	}
	public void setVar(String var) {
		this.var = var;
	}
	public Variable(int coeff, String var) {
		super();
		this.coeff = coeff;
		this.var = var;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + coeff;
		result = prime * result + ((var == null) ? 0 : var.hashCode());
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
		Variable other = (Variable) obj;
		if (coeff != other.coeff)
			return false;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Variable [coeff=" + coeff + ", var=" + var + "]";
	}
	
	
}

