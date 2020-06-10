package iisc.edu.pll.data.lattice.lcp;

import iisc.edu.pll.data.lattice.AbstractValue;

public class LCPRep implements AbstractValue {

	protected int a;
	protected int b;
	protected LCPValues c;

	private boolean isTop;

	public LCPRep(boolean b) {
		isTop = b;
		this.a = 0;
		this.b = 0;
		this.c = new LCPValues(0, false, true);
	}

	public LCPRep(int a, int b, LCPValues c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		isTop = false;
	}

	// copy constructor
	public LCPRep(LCPRep val) {

		a = val.a;
		b = val.b;
		c = new LCPValues(val.c.getValue(), val.c.isBot(), val.c.isTop());

	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public LCPValues getC() {
		return c;
	}

	public void setC(LCPValues c) {
		this.c = c;
	}

	@Override
	public String toString() {
		if (isTop)
			return "LCPREP [is TOP]";

		return "LCPRep [a=" + a + ", b=" + b + ", c=" + c + "]";
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	// a1 = this
	// a2 = l
	// this gives a2(a1(x))
	public LCPRep compose(LCPRep l) {

		if (isTop)
			return new LCPRep(true);

		if (l.isTop)
			return new LCPRep(true);

		int newA = a * l.a;
		int newB = a * l.b + b; // handling the const case
		LCPValues temp1 = new LCPValues(a, false, false);
		LCPValues temp2 = temp1.multiply(l.c); // handling the const case
		LCPValues temp3 = temp2.add(new LCPValues(b, false, false));
		LCPValues temp4 = (LCPValues) temp3.join(c);
		return new LCPRep(newA, newB, temp4);
	}

	// returning bot right now on join
	@Override
	public AbstractValue join(AbstractValue v) {
		LCPRep botValue = new LCPRep(0, 0, new LCPValues(0, true, false));
		LCPRep v2 = (LCPRep) v;

		if (isTop)
			return v2;

		if (v2.isTop())
			return this;

		// case 1
		if (a == v2.a && b == v2.b)
			return new LCPRep(a, b, (LCPValues) c.join(v2.c));

		// case 2
		if ((v2.a - a) != 0) {
			int rem = (b - v2.b) % (v2.a - a);
			if (rem == 0) {
				int l0 = (b - v2.b) / (v2.a - a);
				int tempVal = a * l0 + b;
				LCPValues t1 = new LCPValues(tempVal, false, false);
				LCPValues t2 = (LCPValues) t1.join(c);
				LCPValues t3 = (LCPValues) t2.join(v2.c);
				return new LCPRep(a, b, t3);
			}
		}

		// return bot otherwise
		return botValue;
	}

	// is this > v
	@Override
	public boolean isGreater(AbstractValue v) {

		LCPRep v2 = (LCPRep) v;

		if (isBot())
			return true;

		if (v2.isTop())
			return true;

		if ((!isTop && !v2.isBot()) && (a != v2.a || b != v2.b))
			return false;

		if ((!isTop() && !v2.isBot()) && (a == v2.a || b == v2.b)) {
			if (c.isGreater(v2.c))
				return true;
		}

		return false;
	}

	@Override
	public void setIsBot(boolean b) {
		c = new LCPValues(0, true, false);

	}

	@Override
	public boolean isBot() {
		if (c.isBot())
			return true;
		return false;
	}

	@Override
	public void setIsTop(boolean b) {
		isTop = true;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this)
			return true;

		if (obj == null || !(obj instanceof LCPRep))
			return false;

		LCPRep l = (LCPRep) obj;
		return (l.a == a && l.b == b && l.c.equals(c) && l.isTop == isTop && l.isBot() == isBot());
	}

	@Override
	public int hashCode() {

		return (a + b + c.hashCode());
	}

	@Override
	public boolean isConstantFor(String s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
	}

}
