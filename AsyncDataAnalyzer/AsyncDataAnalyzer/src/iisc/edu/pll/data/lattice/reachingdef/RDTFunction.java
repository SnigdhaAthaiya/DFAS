package iisc.edu.pll.data.lattice.reachingdef;

import java.util.Set;

import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public abstract class RDTFunction implements TFunction {

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction join(TFunction f) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public boolean isGreater(TFunction v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsBot(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isBot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsTop(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TFunction makeDeepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNotConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

}
