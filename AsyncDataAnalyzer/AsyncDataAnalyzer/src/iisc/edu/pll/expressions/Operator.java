package iisc.edu.pll.expressions;

import iisc.edu.pll.analysis.Globals.OpType;

public class Operator extends ExpressionComponent {

	
	OpType  op;
	public Operator(String op) {
		super();
		if(op.equals("+"))
			this.op = OpType.Plus;
		else if(op.equals("-"))
			this.op = OpType.Minus;
		else if(op.equals("*"))
			this.op = OpType.Times;
		else if(op.equals("/"))
			this.op = OpType.Divide;
		else
			this.op = OpType.Plus;
		
	}
	public OpType getOp() {
		return op;
	}
	public void setOp(OpType op) {
		this.op = op;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		Operator other = (Operator) obj;
		if (op != other.op)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Operator [op=" + op + "]";
	}
	
	
}
