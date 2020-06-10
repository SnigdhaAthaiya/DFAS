package iisc.edu.pll.data.lattice.arashort;
import java.util.ArrayList;
import iisc.edu.pll.data.Statement;
/**
 * Class to initialsie Transfer function
 * @author Animesh Kumar
 * 
 *
 */
public class ARAIDEFunctionFactoryShortOb {
	/**
	 * 
	 * @param stType Type of assignment statement
	 * @param args arraylist containing the assignment statement in specified format
	 * @return Nothing
	 */
	public static ARAIDEFunctionShortMat createFunction(String stType,ArrayList<String> args){
		if(stType.equals(Statement.CONSTASSIGN))
		{
			return new ARAIDEAssignShortOb(args);
		}
		if(stType.equals(Statement.ASSIGN))
			return new ARAIDEAssignShortOb(args);
		if(stType.equals(Statement.ID))
			return new ARAIDEIDShortOb(args);
		if(stType.equals(Statement.EMPTY))
			return new ARAIDEEmptyShortOb();
		return new ARAIDEIDShortOb(args);
	}
}
