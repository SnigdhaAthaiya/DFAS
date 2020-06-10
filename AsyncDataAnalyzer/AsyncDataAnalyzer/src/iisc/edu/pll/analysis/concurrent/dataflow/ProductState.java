package iisc.edu.pll.analysis.concurrent.dataflow;

import iisc.edu.pll.data.ModuleNode;


/** This is the product state class for the dataflow analysis
 * The main algorithm is supposed to remember the order of the graphs*/
public class ProductState {
	
	private ModuleNode[] state;
	public ProductState(ModuleNode[] state) {
		
		this.state = state;
		
	}
	
	public ProductState(int size)
	{
		
		this.state  = new ModuleNode[size];
				
	}

	public ModuleNode[] getState() {
		return state;
	}

	public void setState(ModuleNode[] state) {
		this.state = state;
	}

	
	@Override
	public int hashCode() {
		int res = 0;
		for(int i =0; i<state.length; i++)
		{
			res+= state[i].hashCode();
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this)
			return true;
		
		if(obj==null || !(obj instanceof ProductState))
			return false;
		
		ProductState p = (ProductState) obj;
		if(p.state.length != state.length)
			return false;
		
		for(int i =0 ; i<state.length; i++)
			if(!state[i].equals(p.state[i]))
				return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[");
		for(int  i = 0; i<(state.length-1); i++)
			b.append(state[i].getId() + ", ");
		b.append(state[state.length-1].getId() + "]");
		
		return b.toString();
	}
	
	

	

}
