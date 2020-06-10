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
