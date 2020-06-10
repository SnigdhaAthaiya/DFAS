package iisc.edu.pll.lattice.cp;

import java.util.ArrayList;
import java.util.Set;

import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public abstract class CPFunction implements TFunction {

	// in CP we will only be using the apply feature which will be specific to
	// functions

	@Override
	public TFunction join(TFunction f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction compose(TFunction f) {
		ArrayList<CPFunction> composition = new ArrayList<>();

		if (this instanceof CPComposedFunc) {
			CPComposedFunc comp = (CPComposedFunc) this;
			composition.addAll(comp.getCompFunc());
		} else
			composition.add(this);

		if (f instanceof CPComposedFunc) {
			CPComposedFunc comp = (CPComposedFunc) f;
			composition.addAll(comp.getCompFunc());
		} else
			composition.add((CPFunction)f);
		
		
		return new CPComposedFunc(composition);

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

	
}
