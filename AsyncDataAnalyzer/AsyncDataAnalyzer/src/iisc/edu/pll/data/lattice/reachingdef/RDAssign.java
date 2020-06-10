package iisc.edu.pll.data.lattice.reachingdef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import javax.print.attribute.HashPrintServiceAttributeSet;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.TFunction;

public class RDAssign extends RDTFunction {

	String lhs;
	String rhs; // used only for var in copy propagation
	boolean isOther;
	RDSingle thisval;

	public RDAssign(ArrayList<String> args) {
		super();
		if (args.size() < 2) {
			lhs = "";
			rhs = "";
			isOther = true;
			thisval = Globals.other;
			return;
		}

		if (args.size() > 2) {
			lhs = args.get(0);
			rhs = "";
			isOther = true;
			thisval = Globals.other;
			return;
		}

		isOther = false;
		lhs = args.get(0);
		String[] rhsexpr = args.get(1).split(",");
		rhs = rhsexpr[1];
		thisval = new RDSingle(lhs, new VarRHS(rhs));

	}

	@Override
	public TFunction compose(TFunction f) {

		// TODO Auto-generated method stub

		RDValue v1 = (RDValue) f;

		RDValue v2 = new RDValue(true); // make sure to assign later

		boolean updated = false;
		for (int i = 0; i < Globals.numberOfQueryVar; i++) {
			LinkedList<RDSingle> current = v1.value[i];
			v2.value[i] = new LinkedList<>(v1.value[i]);
			boolean add = false;

			for (RDSingle val : current) {
				if (val.rhs instanceof VarRHS) {
					VarRHS v = (VarRHS) val.rhs;
					if (v.varname.equals(lhs)) {
						// replace this def with the function def based on
						// isOther
						add = true;
						v2.value[i].remove(val);
						updated = true;
					}
				}
			}

			if (add) {
				v2.value[i].remove(thisval); // remove duplicates transitively
				v2.value[i].add(thisval);

			}

		}

		if (updated) {
			//System.out.println("added thisval :" + thisval);
			//System.out.println("v2 :" + v2);

			return v2;
		} else {
			return v1;
		}

	}

	@Override
	public String toString() {
		return "RDAssign [lhs=" + lhs + ", rhs=" + rhs + ", isOther=" + isOther + "]";
	}

	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}

}
