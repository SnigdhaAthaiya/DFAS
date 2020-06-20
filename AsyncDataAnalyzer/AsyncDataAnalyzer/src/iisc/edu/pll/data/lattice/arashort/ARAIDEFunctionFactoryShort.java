/*******************************************************************************
 * Copyright (C) 2020 Snigdha Athaiya
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
