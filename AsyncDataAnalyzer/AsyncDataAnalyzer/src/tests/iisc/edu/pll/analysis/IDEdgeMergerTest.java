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
package tests.iisc.edu.pll.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.analysis.GraphExtractor;
import iisc.edu.pll.analysis.IDEdgeMerger;
import iisc.edu.pll.analysis.SpecParser;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.ModuleNode;

public class IDEdgeMergerTest {

	public static void main(String[] args) {
		
		String filename = "test/test4.xml";
		String mName = "P2";
		SpecParser sp = new SpecParser();
		Document parsedFile = sp.getXMLDoc(filename);
		
		
		GraphExtractor ge = new GraphExtractor();
		ge.setDoc(parsedFile);
		ge.setLatticeType("lcp");
		
		
		HashSet<String> modules = new HashSet<>();
		modules = getModuleNames(parsedFile);
		ArrayList<String> variables = computeVariables(parsedFile);
		HashSet<String> messageSet = (HashSet<String>) computeMessages(parsedFile);

		Globals.latticeType = "lcp";
		Globals.numberOfCounters = messageSet.size();
		Globals.numberOfVariables = variables.size();
		Globals.DVars = new ArrayList<>();
		Globals.DVars.add(Globals.lambda);
		Globals.DVars.addAll(variables);

		
		ModuleGraph g = ge.getModuleGraph(mName);
		
		g.writeGraphToFile("graph" + mName+".txt");
		
		IDEdgeMerger merge = new IDEdgeMerger();
		g = merge.mergeIDEdges(g, ge.getLeaders(), ge.getVarUseInfo(), ge.getAssertExpressions());
		
		g.writeGraphToFile("collapsedgraph" + mName + ".txt");
		
		
		Map<ModuleNode, String> varuse = ge.getVarUseInfo();
		for(ModuleNode key :  varuse.keySet())
			System.out.println(key +" : " + varuse.get(key));
		
		
		Map<ModuleNode, String> assertExps = ge.getAssertExpressions();
		for(ModuleNode key :  assertExps.keySet())
			System.out.println(key +" : " + assertExps.get(key));
		
		System.out.println("done");

	}
	private static HashSet<String> getModuleNames(Document parsedFile) {
		HashSet<String> names = new HashSet<>();

		NodeList initNode = parsedFile.getElementsByTagName("INIT");
		if (initNode.getLength() >= 1)
			names.add("INIT");

		NodeList otherModules = parsedFile.getElementsByTagName("PROCTYPE");

		for (int i = 0; i < otherModules.getLength(); i++) {
			Node proc = otherModules.item(i);
			NodeList children = proc.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);
				if (child.getNodeType() == Document.ELEMENT_NODE && child.getNodeName().equals("NAME")) {
					names.add(child.getAttributes().getNamedItem("VALUE").getNodeValue().trim());
				}
			}

		}

		return names;
	}

	private static ArrayList<String> computeVariables(Document doc) {
		ArrayList<String> vars = new ArrayList<>();

		NodeList decls = doc.getElementsByTagName("IVAR");
		for (int i = 0; i < decls.getLength(); i++) {
			Node decl = decls.item(i);
			Node name = getFirstChild(doc, decl, "NAME");
			String varname = name.getAttributes().getNamedItem("VALUE").getNodeValue().trim();

			vars.add(varname);
		}

		return vars;
	}

	private static Node getFirstChild(Document doc, Node node, String name) {

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == doc.ELEMENT_NODE && n.getNodeName().equals(name))
				return n;
		}

		return null;
	}

	/**
	 * using the DOM model of the system extracts the total number of messages
	 * and adds it to messageList
	 */
	private static Set<String> computeMessages(Document doc) {

		HashSet<String> messageSet = new HashSet<>();
		NodeList mtypes = doc.getElementsByTagName("MTYPE");
		for (int i = 0; i < mtypes.getLength(); i++) {
			Node mtype = mtypes.item(i);

			NodeList names = mtype.getChildNodes();
			for (int j = 0; j < names.getLength(); j++) {
				Node child = names.item(j);
				if (child.getNodeType() == Document.ELEMENT_NODE) {
					String val = child.getAttributes().getNamedItem("VALUE").getTextContent().trim();
					// System.out.println("adding: " + val);
					messageSet.add(val);
				}

			}

		}
		return messageSet;

	}



}
