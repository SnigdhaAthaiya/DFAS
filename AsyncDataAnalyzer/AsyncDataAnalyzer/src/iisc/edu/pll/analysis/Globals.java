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
package iisc.edu.pll.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.reachingdef.RDSingle;
import iisc.edu.pll.data.lattice.reachingdef.RDValue;

public class Globals {

	public static int numberOfVariables;
	public static int numberOfCounters;
	public static String latticeType; // to be changed to enum later
	public static int numberOfModules;
	public static String filename;

	public static final String lambda = "_lambda";

	public static ArrayList<String> DVars;

	public static ArrayList<String> ARAVArs;
	
	public enum Mode {
		NAIVE, DEMAND, NAIVEDEMAND
	};

	public static HashMap<String, short[][]> labelVectorsHashMap;
	
	
	//forward analysis
	public static HashMap<String, Integer> varNum;
	public enum OpType  {Plus, Minus, Times, Divide};
	
	//global bot value
	public static AbstractValue botVal;
	
	//for RD analysis
	public static int numberOfQueryVar;
	public static HashMap<String, Integer> rdmap;
	public static RDSingle other;
}
