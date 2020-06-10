package iisc.edu.pll.data.lattice.reachingdef;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;


public class RDValue implements TFunction {

	LinkedList<RDSingle>[] value;

	public RDValue() {
		value = new LinkedList[Globals.numberOfQueryVar];
		for (int i = 0; i < Globals.numberOfQueryVar; i++)
			value[i] = new LinkedList<RDSingle>();

	}

	// do not create internal lists if empty is true
	public RDValue(boolean empty) {
		value = new LinkedList[Globals.numberOfQueryVar];
		if (empty)
			return;
		else {
			for (int i = 0; i < Globals.numberOfQueryVar; i++)
				value[i] = new LinkedList<RDSingle>();
		}
	}

	@Override
	public String toString() {
		StringBuilder content = new StringBuilder();
		content.append("RDValue for Var : \n");
		for(String var: Globals.rdmap.keySet()){
			content.append(var + " : ");
			content.append(value[Globals.rdmap.get(var)] + "\n");
		}
		return content.toString();
	}

	public RDValue(LinkedList<RDSingle>[] value) {
		super();
		this.value = value;
	}

	// will never be called
	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction join(TFunction f) {
		// pointwise join
		RDValue v1 = (RDValue) f;
		RDValue v2 = new RDValue(false); // the lists will be created later

		for (int i = 0; i < Globals.numberOfQueryVar; i++) {
			LinkedList<RDSingle> list = new LinkedList<>();
			list.addAll(value[i]);
			list.removeAll(v1.value[i]); // remove duplicates
			list.addAll(v1.value[i]);
			v2.value[i] = list;
			

		}

		//System.out.println("computing v2 " + v2);
		return v2;
	}

	private boolean hasVarRHS(LinkedList<RDSingle> list) {
		for(RDSingle val : list)
		{
			if(val.getRhs() instanceof VarRHS)
			{
				System.out.println("found it");
				return true;
			}
		}
		return false;
	}

	@Override
	public TFunction compose(TFunction f) {
		// TODO Auto-generated method stub
		return null;
	}

	//returns greater than equal to
	@Override
	public boolean isGreater(TFunction v) {
		RDValue v1 = (RDValue) v;

		boolean flag = true;
		for (int i = 0; i < Globals.numberOfQueryVar; i++) {
			// emptiness checks
			if (value[i].isEmpty())
				return false;

			if (!value[i].containsAll(v1.value[i])) // some elements from v1 are
													// not present
				return false;

			// that means this contains all elements of v1[i]
			// if v1 also contains then isgreater should be false

			
		}

		return true;
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
		int index = Globals.rdmap.get(use); // get index
		int constval = Integer.MIN_VALUE;
		boolean init = false;
		LinkedList<RDSingle> val = value[index];

		for (RDSingle v : val) {
			if (v.getRhs() instanceof VarRHS || v == Globals.other)
				return false;

			if (v.getRhs() instanceof ConstantRHS) {
				ConstantRHS rhs = (ConstantRHS) v.getRhs();

				if (!init) {
					init = true;
					constval = rhs.getValue(); // the first constant
				} else {
					if (constval != rhs.getValue()) // two different constants
						return false;
				}

			}
		}

		return true; // no warning cases
	}

	@Override
	public boolean isNotConstantFor(String use) {
		int index = Globals.rdmap.get(use); // get index
		int constval = Integer.MIN_VALUE;
		boolean init = false;
		LinkedList<RDSingle> val = value[index];

		for (RDSingle v : val) {
			if (v.getRhs() instanceof VarRHS || v == Globals.other)
				return true;

			if (v.getRhs() instanceof ConstantRHS) {
				ConstantRHS rhs = (ConstantRHS) v.getRhs();
				if (!init) {
					init = true;
					constval = rhs.getValue(); // first constant
				} else {
					if (constval != rhs.getValue())
						return true; // two diff constant
				}
			}
		}

		return false; // no multiple constants

	}

	@Override
	public boolean isID() {
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

	public LinkedList[] getValue() {
		return value;
	}

	public LinkedList<RDSingle> getValueAt(int i) {
		return value[i];
	}

	public void setValueAt(int i, LinkedList<RDSingle> v) {
		value[i] = v;
	}

	// returns true if all variables are non-constants
	public boolean isNotConstant() {
		for (String var : Globals.rdmap.keySet()) {
			if (isConstantFor(var))
				return false;
		}
		return true;
	}

}
