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

public class DataDependenceTest {

	public static void main(String[] args) {
		String filename = "models/lynch.xml";
	//	String mName = "P2";
		SpecParser sp = new SpecParser();
		Document parsedFile = sp.getXMLDoc(filename);
		
		
		GraphExtractor ge = new GraphExtractor();
		ge.setDoc(parsedFile);
		ge.setLatticeType("lcp");
		
		
		HashSet<String> modules = new HashSet<>();
		modules = getModuleNames(parsedFile);
		ArrayList<String> variables = computeVariables(parsedFile);
		HashSet<String> messageSet = (HashSet<String>) computeMessages(parsedFile);
		
		ArrayList<String> assignments = ListAssignments(parsedFile);

	/*	Globals.latticeType = "lcp";
		Globals.numberOfCounters = messageSet.size();
		Globals.numberOfVariables = variables.size();
		Globals.DVars = new ArrayList<>();
		Globals.DVars.add(Globals.lambda);
		Globals.DVars.addAll(variables);

		
		for (String mName : modules) {

			System.out.println("constructing graph for  :" + mName);
			ge = new GraphExtractor();
			ge.setDoc(parsedFile);
			ge.setLatticeType("lcp");

			ModuleGraph g = ge.getModuleGraph(mName);
		}*/

	}
	private static ArrayList<String> ListAssignments(Document parsedFile) {
		ArrayList<String> names = new ArrayList<>();

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
