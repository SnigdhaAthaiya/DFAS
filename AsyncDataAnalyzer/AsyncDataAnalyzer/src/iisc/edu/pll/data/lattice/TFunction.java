package iisc.edu.pll.data.lattice;

import java.util.HashSet;
import java.util.Set;

public interface TFunction {
	
	//apply the function
	public AbstractValue apply(AbstractValue v);
	
	public  TFunction join(TFunction f);
	
	public TFunction compose(TFunction f);
		
	public boolean isGreater(TFunction v);
	
	
	public void setIsBot(boolean b) ;
	public boolean isBot() ;

	
	public void setIsTop(boolean b);
	public boolean isTop() ;

	public TFunction makeDeepCopy();
	
	public int getSize();

	public boolean isConstantFor(String use);
	public boolean isNotConstantFor(String use);

	public boolean isID();
	
	public Set<String> getUse();
	
	public String getDef();
	
	
	
	
	
	
	
	
	
	
	
}
