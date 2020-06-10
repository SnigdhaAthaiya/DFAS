package iisc.edu.pll.data.lattice.lcp;

public class VarPairValue {

	VarPair pair;
	LCPRep value;

	public VarPairValue(VarPair pair, LCPRep value) {
		super();
		this.pair = new VarPair(pair.getFrom(), pair.getTo());
		this.value = value;
	}

	public VarPair getPair() {
		return pair;
	}

	public void setPair(VarPair pair) {
		this.pair = pair;
	}

	public LCPRep getValue() {
		return value;
	}

	public void setValue(LCPRep value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o==this)
			return true;
		
		if(o== null || !(o instanceof VarPairValue))
			return false;
		
		VarPairValue v = (VarPairValue)o;
		
		if(v.getPair().equals(pair) && v.getValue().equals(value))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return (value.hashCode()+ pair.hashCode());
	}
	
	@Override
	public String toString(){
		return (pair.toString() + " : " + value.toString());
	}

}
