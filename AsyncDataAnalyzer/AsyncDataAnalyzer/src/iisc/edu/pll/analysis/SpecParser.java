package iisc.edu.pll.analysis;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;

public class SpecParser {

	public  Document getXMLDoc(String filename) {

		try {
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			return doc;

		} catch (ParserConfigurationException e) {
			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			System.out.println("Error :" + e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public static void main(String[] args) {
		Document doc = (new SpecParser()).getXMLDoc("test/test1.xml");
		if (doc != null) {
			NodeList nList = doc.getElementsByTagName("NAME");
			for(int i =0; i< nList.getLength(); i++)
			{
				Node node = nList.item(i);
				System.out.println("value = " + node.getAttributes().getNamedItem("VALUE").getTextContent());
			}

		}
		System.out.println("successful");

	}

}
