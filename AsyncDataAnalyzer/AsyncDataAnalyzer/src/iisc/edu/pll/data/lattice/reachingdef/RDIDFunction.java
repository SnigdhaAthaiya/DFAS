package iisc.edu.pll.data.lattice.reachingdef;

import java.util.ArrayList;

import iisc.edu.pll.data.lattice.TFunction;

public class RDIDFunction extends RDTFunction {

	
	public RDIDFunction(String args[])
	{
		super();
	}
	
	public RDIDFunction(ArrayList<String> args) {
		super();
	}

	@Override
	public TFunction compose(TFunction f) {
		// TODO Auto-generated method stub
		return f;
	}

	@Override
	public String toString() {
		return "RDIDFunction []";
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
}
