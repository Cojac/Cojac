package com.github.cojac.deltadebugging.utils;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.cojac.deltadebugging.Opt;

/**
 * Cette class permet d'editer le comportement dans le fichier d'instruction.
 * 
 * @author remibadoud
 *
 */

// TODO (Bapst): refactor as not-a-singleton, and parameterized with
//               Opt.BEHAVIOURSFILE.getValue() Opt.MODE.getValue()...

public class BehaviourEditor implements Editor {

	private static final String		LINE_ELT			= "line";
	private static final String		INSTRUCTION_ELT		= "instruction";
	private static final String		BEHAVIOUR_ATTR		= "behaviour";
	private static BehaviourEditor	instance;
	private int						nbrOfInstructions	= 0;
	private Document				document;
	private String					filePath;
	private String					tagName;

	/**
	 * Class constructor.
	 */
	private BehaviourEditor() {
		initInstructionMap();
	}

	/**
	 * 
	 * @return Singleton instance of the class
	 */
	public static BehaviourEditor getInstance() {
		if (instance == null) {
			instance = new BehaviourEditor();
		}
		return instance;
	}

	/**
	 * Initialize.
	 */
	private void initInstructionMap() {
		this.filePath = Opt.BEHAVIOURSFILE.getValue();
		this.nbrOfInstructions = 0;
		if (Opt.MODE.getValue().equals("wrap")) this.tagName = LINE_ELT;
		else this.tagName = INSTRUCTION_ELT;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			document = docBuilder.parse(filePath); // Parse the XML file
			NodeList intructionNodes = document.getDocumentElement().getElementsByTagName(tagName);
			nbrOfInstructions = intructionNodes.getLength();
			System.out.println("new BehaviourEditor, #instructions="+nbrOfInstructions);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Edit the behaviours in the instructionsFile. Redefine behaviours in
	 * function of the BitSet.
	 * 
	 * @param instrumentationSet BitSet that represent instructions behaviours:
	 *            1-> "IGNORE", 0 -> "BD2F", bitIndex -> index of instruction as
	 *            appeared in file.
	 */
	public void editBehaviours(BitSet instrumentationSet) {
		NodeList intructionNodes = document.getDocumentElement().getElementsByTagName(tagName);
		nbrOfInstructions = intructionNodes.getLength();

		for (int i = 0; i < intructionNodes.getLength(); i++) {
			Node instructionNode = intructionNodes.item(i);
			Node behaviourNode = instructionNode.getAttributes().getNamedItem(BEHAVIOUR_ATTR);
			if (!instrumentationSet.get(i)) behaviourNode.setTextContent("FLOAT");
			else behaviourNode.setTextContent("IGNORE");
		}
		saveBehaviours();
	}

	/**
	 * Write the behaviours the XML file.
	 */
	private void saveBehaviours() {
		try {
			// ----------------------------------------------------------------------
			// prepare to write the XML document
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(filePath));
			// ----------------------------------------------------------------------
			// write the XML document to XML file
			transformer.transform(source, result);
			// ----------------------------------------------------------------------
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return The number of instruction that is presented in the file.
	 */
	public int getNbrOfInstructions() {
		return nbrOfInstructions;
	}

	/**
	 * 
	 * @return The representation of instructions file.
	 */
	public Document getDocument() {
		return document;
	}
}
