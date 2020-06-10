package iisc.edu.pll.data.lattice.ara;
import java.util.ArrayList;
import iisc.edu.pll.data.Statement;

public class ARAIDEFunctionFactory {
	public static ARAIDEFunction createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new ARAIDEAssign(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new ARAIDEAssign(args);
		if(stType.equals(Statement.ID))
			return new ARAIDEID(args);
		if(stType.equals(Statement.EMPTY))
			return new ARAIDEEmpty();
		//check here
		
		return new ARAIDEID(args);
	}
}
