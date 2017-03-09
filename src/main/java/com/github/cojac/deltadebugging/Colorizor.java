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

public class Colorizor {

	private static final String							START_FILE	= "<!DOCTYPE html><html><body><pre>";
	private static final String							END_FILE	= "</pre></body></html>";

	private HashMap<String, HashMap<Integer, String>>	colorMap;

	public Colorizor() {
		initColor();
	}

	private void initColor() {
		colorMap = new HashMap<>();

		Document document = BehaviourEditor.getInstance().getDocument();

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

		//		NodeList classMapNodes = document.getDocumentElement().getElementsByTagName("classMap");
		//		for (int i = 0; i < classMapNodes.getLength(); i++) {
		//			Node classMapNode = classMapNodes.item(i);
		//			if (classMapNode.getNodeType() != Node.ELEMENT_NODE) continue;
		//			Element classMapElement = (Element) classMapNode;
		//			HashMap<Integer, String> subColorMap = new HashMap<>();
		//			String className = classMapElement.getAttributeNode("className").getValue();
		//			
		//			NodeList instructionNodes = classMapElement.getElementsByTagName("instruction");
		//			for (int k = 0; k < instructionNodes.getLength(); k++) {
		//				Node instructionNode = instructionNodes.item(k);
		//				if (instructionNode.getNodeType() != Node.ELEMENT_NODE) continue;
		//				Element instructionElement = (Element) instructionNode;
		//
		//				int lineNumber = Integer.parseInt(instructionElement.getAttributeNode("lineNumber").getValue());
		//				String behaviour = instructionElement.getAttributeNode("behaviour").getValue();
		//				putBehaviourColor(subColorMap, lineNumber, behaviour);
		//			}
		//			colorMap.put(className, subColorMap);
		//		}

	}

	private void putBehaviourColor(HashMap<Integer, String> subColorMap, Integer lineNumber, String behaviour) {
		if (!subColorMap.containsKey(lineNumber)) {
			if (behaviour.equals("IGNORE")) subColorMap.put(lineNumber, "red");
			else subColorMap.put(lineNumber, "green");
		} else {
			String crtColor = subColorMap.get(lineNumber);
			if (crtColor.equals("red") && !behaviour.equals("IGNORE")
					|| crtColor.equals("green") && behaviour.equals("IGNORE"))
				subColorMap.put(lineNumber, "orange");
		}
	}

	public void colorizeClass(String javaFilePath, String htmlFilePath, String className) {
		try {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getColorFor(String className, int lineNumber) {
		if (colorMap.containsKey(className) && colorMap.get(className).containsKey(lineNumber))
			return colorMap.get(className).get(lineNumber);
		return "black";
	}

}
