package com.github.cojac.deltadebugging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.cojac.deltadebugging.utils.BehaviourEditor;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Colorizor {

	private static final String BLACK = "black";
    private static final String GREEN = "green";
    private static final String ORANGE = "orange";
    private static final String RED = "red";
    private static final String IGNORE = "IGNORE";
    private static final String START_FILE = "<!DOCTYPE html><html><body><pre>";
	private static final String	  END_FILE = "</pre></body></html>";

	private HashMap<String, HashMap<Integer, String>> colorMap;
	//              className   ->  (lineNumber -> Color)

	public Colorizor(String filePath) throws Exception {
		initColor(filePath);
	}

	private void initColor(String filePath) throws Exception {
		colorMap = new HashMap<>();

		Document document;
		if (filePath==null) 
		    document = BehaviourEditor.getInstance().getDocument();
		else
		    document = openXMLDocument(filePath);

		NodeList classNodes = document.getDocumentElement().getElementsByTagName("class");
		for (int i = 0; i < classNodes.getLength(); i++) {
			Node classNode = classNodes.item(i);
			if (classNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element classElement = (Element) classNode;

			HashMap<Integer, String> subColorMap = new HashMap<>();
			String className = classElement.getAttributeNode("className").getValue();

			NodeList lineNodes = classElement.getElementsByTagName("line");
			for (int k = 0; k < lineNodes.getLength(); k++) {
				Node lineNode = lineNodes.item(k);
				if (lineNode.getNodeType() != Node.ELEMENT_NODE) continue;
				Element lineElement = (Element) lineNode;
				int lineNumber = Integer.parseInt(lineElement.getAttributeNode("lineNumber").getValue());
				NodeList instructionNodes = lineElement.getElementsByTagName("instruction");
				for (int l = 0; l < instructionNodes.getLength(); l++) {
					Node instructionNode = instructionNodes.item(l);
					if (instructionNode.getNodeType() != Node.ELEMENT_NODE) continue;
					Element instructionElement = (Element) instructionNode;
					String behaviour = instructionElement.getAttributeNode("behaviour").getValue();
					putBehaviourColor(subColorMap, lineNumber, behaviour);
				}
			}
			colorMap.put(className, subColorMap);
		}
	}
	
	private static Document openXMLDocument(String filePath) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(filePath); // Parse the XML file
        return document;
	}

	private void putBehaviourColor(HashMap<Integer, String> subColorMap, Integer lineNumber, String behaviour) {
		if (!subColorMap.containsKey(lineNumber)) {
			if (behaviour.equals(IGNORE)) subColorMap.put(lineNumber, RED);
			else subColorMap.put(lineNumber, GREEN);
		} else {
		    String crtColor = subColorMap.get(lineNumber);
		    if (crtColor.equals(RED) && !behaviour.equals(IGNORE)
		            || crtColor.equals(GREEN) && behaviour.equals(IGNORE))
		        subColorMap.put(lineNumber, ORANGE);
		}
	}

	public void colorizeClass(String javaFilePath, 
	        String htmlFilePath, String className) throws Exception {
	    BufferedReader br = Files.newBufferedReader(Paths.get(javaFilePath));
	    BufferedWriter bw = Files.newBufferedWriter(Paths.get(htmlFilePath));
	    // BufferedWriter bw =
	    // Files.newBufferedWriter(Paths.get("/Users/remibadoud/Desktop/maClass.html"));

	    bw.write(START_FILE);
	    bw.newLine();
	    int lineNumber = 1;
	    String line;
	    while ((line = br.readLine()) != null) {
	        line = "<span style=\"color:gray\">" + String.format("%03d", lineNumber) + "|</span>"
	                + "<span style=\"color:" + getColorFor(className, lineNumber++) + "\">" + line + "</span>";
	        bw.write(line);
	        bw.newLine();
	    }

	    bw.write(END_FILE);
	    bw.close();
	}

	private String getColorFor(String className, int lineNumber) {
		if (colorMap.containsKey(className) && colorMap.get(className).containsKey(lineNumber))
			return colorMap.get(className).get(lineNumber);
		return BLACK;
	}
	
	//----------------------------------------------------------------------
	
	public static void main(String[] args) throws Exception {
	    String xmlFilePath = "E:\\_temp\\ConFrac.txt";
	    String sourceCodeFilePath = "D:\\workspace_plugins\\UsingCojac\\src\\dd\\ConFrac.java";
	    String htmlOutputFilePath = "E:\\_temp\\ConFrac.java.html";
        String klassName = "dd/ConFrac"; 
	    if(args.length>0) {
	        xmlFilePath = args[0];
	        sourceCodeFilePath = args[1];
	        htmlOutputFilePath = args[2];
	        klassName = args[3];
	    }
	    Colorizor co = new Colorizor(xmlFilePath);
	    co.colorizeClass(sourceCodeFilePath, htmlOutputFilePath, klassName);
	}

}
