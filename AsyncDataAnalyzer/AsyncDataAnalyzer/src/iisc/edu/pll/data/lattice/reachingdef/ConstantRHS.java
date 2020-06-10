package iisc.edu.pll.data.lattice.reachingdef;

public class ConstantRHS extends RDRHS {
	
	int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public ConstantRHS(int value) {
		super();
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
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
		ConstantRHS other = (ConstantRHS) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConstantRHS [value=" + value + "]";
	}
	

}
