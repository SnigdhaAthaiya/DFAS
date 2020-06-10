package iisc.edu.pll.data.lattice.arashort;
import java.util.ArrayList;
import iisc.edu.pll.data.Statement;
/**
 * Class to initialsie Transfer function
 * @author Animesh Kumar
 * 
 *
 */
public class ARAIDEFunctionFactoryShort {
	/**
	 * 
	 * @param stType Type of assignment statement
	 * @param args arraylist containing the assignment statement in specified format
	 * @return Nothing
	 */
	public static ARAIDEFunctionShort createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new ARAIDEAssignShort(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new ARAIDEAssignShort(args);
		if(stType.equals(Statement.ID))
			return new ARAIDEIDShort(args);
		if(stType.equals(Statement.EMPTY))
			return new ARAIDEEmptyShort();
		return new ARAIDEIDShort(args);
	}
}
