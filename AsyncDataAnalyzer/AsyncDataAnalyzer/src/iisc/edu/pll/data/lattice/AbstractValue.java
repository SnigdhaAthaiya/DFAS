package iisc.edu.pll.data.lattice;

public interface AbstractValue {

	

	AbstractValue join(AbstractValue v);

	// returns true if this >= v
	boolean isGreater(AbstractValue v);
	
	

	void setIsBot(boolean b);

	boolean isBot();

	void setIsTop(boolean b);

	boolean isTop();
	
	boolean isConstantFor(String s);
	boolean isConstant();

	

}
