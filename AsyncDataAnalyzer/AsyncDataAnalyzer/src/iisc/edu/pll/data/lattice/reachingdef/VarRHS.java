package iisc.edu.pll.data.lattice.reachingdef;

public class VarRHS extends RDRHS {
	
	String varname;

	public VarRHS(String varname) {
		super();
		this.varname = varname;
	}

	public String getVarname() {
		return varname;
	}

	public void setVarname(String varname) {
		this.varname = varname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((varname == null) ? 0 : varname.hashCode());
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
		VarRHS other = (VarRHS) obj;
		if (varname == null) {
			if (other.varname != null)
				return false;
		} else if (!varname.equals(other.varname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VarRHS [varname=" + varname + "]";
	}
	
	

}
