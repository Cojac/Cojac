package com.github.cojac.utils;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.cojac.Arg;
import com.github.cojac.Args;

public class BehaviourLoader {

    // elements tag
    private static final String ROOT_ELT = "classes";
    private static final String CLASS_ELT = "class";
    private static final String METHOD_ELT = "method";
    private static final String LINE_ELT = "line";
    private static final String INSTRUCTION_ELT = "instruction";

    // attributes tag
    private static final String CLASS_NAME_ATTR = "className";
    private static final String METHOD_NAME_ATTR = "methodName";
    private static final String LINE_NUMBER_ATTR = "lineNumber";
    private static final String INSTRUCTION_NUMBER_ATTR = "instructionNumber";
    private static final String OP_CODE_ATTR = "opCode";
    private static final String OP_NAME_ATTR = "opName";
    private static final String INVOKED_METHOD = "invokedMethod";
    private static final String BEHAVIOUR_ATTR = "behaviour";
    private static final String DEFAULT_BEHAVIOUR_VALUE = "IGNORE";

    // singleton instance
    private static BehaviourLoader INSTANCE;

    // Structure that contains all instructions
    // classeName - methodName - lineNumber - instructionNumber -
    // instructionMeta
    private HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, InstructionMeta>>>> classMap;

    // constructor (private cause singleton)
    private BehaviourLoader() {
    }

    // getter (cause singleton)
    public static BehaviourLoader getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new BehaviourLoader();
        }
        return INSTANCE;
    }

    // initialize the structure that contains all instructions with the
    // informations founded in the file
    public void loadInstructionMap(String filePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(filePath);
            // initialize the structure (
            classMap = classMapFromXML(document);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, InstructionMeta>>>> classMapFromXML(Document document) {
        HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, InstructionMeta>>>> classMap = new HashMap<>();
        NodeList classNodes = document.getDocumentElement().getElementsByTagName(CLASS_ELT);
        for (int i = 0; i < classNodes.getLength(); i++) {
            Node classNode = classNodes.item(i);
            if (classNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element classElement = (Element) classNode;
            String className = classElement.getAttributeNode(CLASS_NAME_ATTR).getValue();
            classMap.put(className, methodMapFromXML(classElement));
        }
        return classMap;
    }

    private HashMap<String, HashMap<Integer, HashMap<Integer, InstructionMeta>>> methodMapFromXML(Element classElement) {
        HashMap<String, HashMap<Integer, HashMap<Integer, InstructionMeta>>> methodMap = new HashMap<>();
        NodeList methodNodes = classElement.getElementsByTagName(METHOD_ELT);
        for (int j = 0; j < methodNodes.getLength(); j++) {
            Node methodNode = methodNodes.item(j);
            if (methodNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element methodElement = (Element) methodNode;
            String methodName = methodElement.getAttributeNode(METHOD_NAME_ATTR).getValue();
            methodMap.put(methodName, lineMapFromXML(methodElement));
        }
        return methodMap;
    }

    private HashMap<Integer, HashMap<Integer, InstructionMeta>> lineMapFromXML(Element methodElement) {
        HashMap<Integer, HashMap<Integer, InstructionMeta>> lineMap = new HashMap<>();
        NodeList lineNodes = methodElement.getElementsByTagName(LINE_ELT);
        for (int k = 0; k < lineNodes.getLength(); k++) {
            Node lineNode = lineNodes.item(k);
            if (lineNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element lineElement = (Element) lineNode;
            Integer lineNumber = Integer.parseInt(lineElement.getAttributeNode(LINE_NUMBER_ATTR).getValue());
            lineMap.put(lineNumber, instructionMapFromXML(lineElement));
        }
        return lineMap;
    }

    private HashMap<Integer, InstructionMeta> instructionMapFromXML(Element lineElement) {
        HashMap<Integer, InstructionMeta> instructionMap = new HashMap<>();
        NodeList instructionNodes = lineElement.getElementsByTagName(INSTRUCTION_ELT);
        for (int l = 0; l < instructionNodes.getLength(); l++) {
            Node instructionNode = instructionNodes.item(l);
            if (instructionNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element instructionElement = (Element) instructionNode;
            Integer instructionNumber = Integer.parseInt(instructionElement.getAttributeNode(INSTRUCTION_NUMBER_ATTR).getValue());
            instructionMap.put(instructionNumber, instructionMetaFromXML(instructionElement));
        }
        return instructionMap;
    }

    private InstructionMeta instructionMetaFromXML(Element instructionElement) {
        Integer opCode = Integer.parseInt(instructionElement.getAttributeNode(OP_CODE_ATTR).getValue());
        String opName = instructionElement.getAttributeNode(OP_NAME_ATTR).getValue();
        String invokedMethod = instructionElement.getAttributeNode(INVOKED_METHOD).getValue();
        String behaviour = instructionElement.getAttributeNode(BEHAVIOUR_ATTR).getValue();
        return new InstructionMeta(opCode, opName, invokedMethod, behaviour);
    }

    public boolean isSpecifiedBehaviour(String className, String methodName, int lineNumber, int instructionNumber) {
        return classMap.containsKey(className) &&
                classMap.get(className).containsKey(methodName) &&
                classMap.get(className).get(methodName).containsKey(lineNumber) &&
                classMap.get(className).get(methodName).get(lineNumber).containsKey(instructionNumber);
    }

    public String getSpecifiedBehaviour(String className, String methodName, int lineNumber, int instructionNumber) {
        return classMap.get(className).get(methodName).get(lineNumber).get(instructionNumber).getBehaviour();
    }
}
