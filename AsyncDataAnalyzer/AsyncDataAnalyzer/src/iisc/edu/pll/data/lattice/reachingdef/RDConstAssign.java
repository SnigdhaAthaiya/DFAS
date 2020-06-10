package iisc.edu.pll.data.lattice.reachingdef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.TFunction;

public class RDConstAssign extends RDTFunction {

	int value;
	String lhs;
	RDSingle thisval;

	public RDConstAssign(ArrayList<String> args) {
		if (args.size() < 2) {
			value = 0;
			lhs = "";
			System.out.println("incorrect params");
			thisval = Globals.other;
			return;
		}

		lhs = args.get(0);
		String[] rhs = args.get(1).split(",");
		value = Integer.parseInt(rhs[0]) * Integer.parseInt(rhs[1]);
		thisval = new RDSingle(lhs, new ConstantRHS(value));
	}

	@Override
	public TFunction compose(TFunction f) {
		// TODO Auto-generated method stub

		RDValue v1 = (RDValue) f;

		// check
		// boolean runrqd = false;
		//
		// for (int i = 0; i < Globals.numberOfQueryVar; i++) {
		// LinkedList<RDSingle> current = v1.value[i];
		// if (!current.contains(thisval)) {
		// runrqd = true;
		// break;
		// }
		//
		// }
		// if (!runrqd)
		// return v1;
		//
		RDValue v2 = new RDValue(true); // make sure each row is added

		boolean updated = false;

		for (int i = 0; i < Globals.numberOfQueryVar; i++) {
			LinkedList<RDSingle> current = v1.value[i];
			v2.value[i] = new LinkedList<>(v1.value[i]);
			boolean add = false; // whether to add this def

			for (RDSingle val : current) {
				LinkedList<RDSingle> toremove = new LinkedList<>();
				if (val.rhs instanceof VarRHS) {
					VarRHS v = (VarRHS) val.rhs;
					if (v.varname.equals(lhs)) {
						add = true;
						v2.value[i].remove(val);
						updated = true;
					}
				}
			}

			if (add) {
				v2.value[i].remove(thisval); // remove duplicates - it should be
												// transitive
				v2.value[i].add(thisval);
			}

		}

		if (updated) {
		//	System.out.println("added thisval :" + thisval);
			//System.out.println("v2 :" + v2);

			return v2;
		} else {
			return v1;
		}
	}

	@Override
	public String toString() {
		return "RDConstAssign [value=" + value + ", lhs=" + lhs + "]";
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}

}
