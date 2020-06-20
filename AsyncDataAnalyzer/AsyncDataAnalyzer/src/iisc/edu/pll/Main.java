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
package iisc.edu.pll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import iisc.edu.pll.analysis.AssignToIDConverter;
import iisc.edu.pll.analysis.DataFlow;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.Globals.Mode;
import iisc.edu.pll.analysis.GraphExtractor;
import iisc.edu.pll.analysis.IDEdgeMerger;
import iisc.edu.pll.analysis.ProductConstructor;
import iisc.edu.pll.analysis.SpecParser;
import iisc.edu.pll.analysis.assertions.ConditionCheck;
import iisc.edu.pll.analysis.assertions.ConditionCheckFactory;
import iisc.edu.pll.analysis.concurrent.dataflow.DataFlowParallel;
import iisc.edu.pll.analysis.concurrent.dataflow.MainBackward;
import iisc.edu.pll.analysis.concurrent.dataflow.ProductState;
import iisc.edu.pll.analysis.forward.MainForward;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.arashort.ARAIDEAssignShortOb;
import iisc.edu.pll.data.lattice.arashort.ARAIDEFunctionShortMat;
import iisc.edu.pll.data.lattice.lcp.LCPIDEAssign;
import iisc.edu.pll.data.lattice.lcp.LCPIDEConst;
import iisc.edu.pll.data.lattice.reachingdef.OtherRHS;
import iisc.edu.pll.data.lattice.reachingdef.RDSingle;
import iisc.edu.pll.exceptions.InvalidArguments;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleGraph;

public class Main {

	private String direction;

	public static void main(String[] args) {

		Main m = new Main();

		try {
			m.direction = args[0];
			PropertyConfigurator.configure("log4j.properties");

			if (m.direction.equals("-forward")) {
				args[0] ="";
				System.out.println("Running Forward DFAS");
				MainForward.main(args);
			}
			else if(m.direction.equals("-backward"))
			{
				args[0] ="";
				System.out.println("Running Backward DFAS");
				MainBackward.main(args);
			}
			else
				System.out.println("Specify the correct direction(forward/backward) for the analysis ");

		} catch (Throwable e) {
			System.out.println("excpetion in main :" + e.getMessage());
			e.printStackTrace();

		}

	}

	private void parseArguments(String[] args) {

		Options options = new Options();
		options.addOption("direction", "Choose between backward and forward DFAS");
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("direction")) {
				this.direction = cmd.getOptionValue("direction");

			} else {
				System.out.println("Enter the direction - forward/backward");
				return;

			}

		} catch (Exception e) {
			System.out.println("exception!! " + e.getMessage());
		}

	}

	
}
