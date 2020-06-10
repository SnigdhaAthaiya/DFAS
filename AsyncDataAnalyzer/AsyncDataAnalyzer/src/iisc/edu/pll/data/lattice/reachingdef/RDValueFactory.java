package iisc.edu.pll.data.lattice.reachingdef;


//will be called directly
public class RDValueFactory {
	
	public static RDValue createValue(String vType, String[] args ){
		
		if(vType.equals("constant"))
		{
			return new RDValue();
		}
		
		return null;
	}

}
