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

    private static String ROOT_ELT = "classes";
    private static String CLASS_ELT = "class";
    private static String METHOD_ELT = "method";
    private static String LINE_ELT = "line";
    private static String INSTRUCTION_ELT = "instruction";
    private static String CLASS_NAME_ATTR = "className";
    private static String METHOD_NAME_ATTR = "methodName";
    private static String LINE_NUMBER_ATTR = "lineNumber";
    private static String INSTRUCTION_NUMBER_ATTR = "instructionNumber";
    private static String OP_CODE_ATTR = "opCode";
    private static String OP_NAME_ATTR = "opName";
    private static String INVOKED_METHOD = "invokedMethod";
    private static String BEHAVIOUR_ATTR = "behaviour";
    private static String DEFAULT_BEHAVIOUR_VALUE = "IGNORE";

    private static BehaviourLoader INSTANCE;
    /// private HashMap<String, HashMap<String, HashMap<Integer, Instruction>>>
    /// instructionMap;
    private HashMap<String, HashMap<String, HashMap<Integer, HashMap<Integer, InstructionMeta>>>> classMap;

    private BehaviourLoader() {
    }

    public static BehaviourLoader getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new BehaviourLoader();
        }
        return INSTANCE;
    }

    public void loadInstructionMap(Args args) {
        try {
            String filePath = args.getValue(Arg.LOAD_BEHAVIOUR_MAP);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(filePath); // Parse the XML
                                                            // file
            classMap = classMapFromXML(document);
            // classMap = new HashMap<>();
            // NodeList classNodes =
            // document.getDocumentElement().getElementsByTagName(CLASS_ELT);
            // for (int i = 0; i < classNodes.getLength(); i++) {
            // Node classNode = classNodes.item(i);
            // if (classNode.getNodeType() != Node.ELEMENT_NODE)
            // continue;
            // Element classElement = (Element) classNode;
            //
            // HashMap<String, HashMap<Integer, HashMap<Integer,
            // InstructionMeta>>> methodMap = new HashMap<>();
            // String className =
            // classElement.getAttributeNode(CLASS_NAME_ATTR).getValue();
            // NodeList methodNodes =
            // classElement.getElementsByTagName(METHOD_ELT);
            // for (int j = 0; j < methodNodes.getLength(); j++) {
            //
            // Node methodNode = methodNodes.item(j);
            // if (methodNode.getNodeType() != Node.ELEMENT_NODE)
            // continue;
            // Element methodElement = (Element) methodNode;
            // HashMap<Integer, HashMap<Integer, InstructionMeta>> lineMap = new
            // HashMap<>();
            // String methodName =
            // methodElement.getAttributeNode(METHOD_NAME_ATTR).getValue();
            //
            // NodeList lineNodes = classElement.getElementsByTagName(LINE_ELT);
            // for (int k = 0; k < lineNodes.getLength(); k++) {
            //
            // Node lineNode = lineNodes.item(k);
            // if (lineNode.getNodeType() != Node.ELEMENT_NODE)
            // continue;
            // Element lineElement = (Element) lineNode;
            // HashMap<Integer, InstructionMeta> instructionMap = new
            // HashMap<>();
            // Integer lineNumber =
            // Integer.parseInt(lineElement.getAttributeNode(LINE_NUMBER_ATTR).getValue());
            // /// lalalal
            //
            // NodeList instructionNodes =
            // lineElement.getElementsByTagName(INSTRUCTION_ELT);
            // for (int l = 0; l < instructionNodes.getLength(); l++) {
            // Node instructionNode = instructionNodes.item(l);
            // if (instructionNode.getNodeType() != Node.ELEMENT_NODE)
            // continue;
            // Element instructionElement = (Element) instructionNode;
            //
            // InstructionMeta instructionMeta = new InstructionMeta();
            // Integer instructionNumber =
            // Integer.parseInt(instructionElement.getAttributeNode(INSTRUCTION_NUMBER_ATTR).getValue());
            //
            // int opCode =
            // Integer.parseInt(instructionElement.getAttributeNode(OP_CODE_ATTR).getValue());
            // instructionMeta.setOpCode(opCode);
            // String opName =
            // instructionElement.getAttributeNode(OP_NAME_ATTR).getValue();
            // instructionMeta.setOpName(opName);
            //
            // String invokedMethod =
            // instructionElement.getAttributeNode(INVOKED_METHOD).getValue();
            // instructionMeta.setInvokedMethod(invokedMethod);
            //
            // String behaviour =
            // instructionElement.getAttributeNode(BEHAVIOUR_ATTR).getValue();
            // instructionMeta.setBehaviour(behaviour);
            //
            // // Instruction instruction = new Instruction();
            // // int opCode =
            // //
            // Integer.parseInt(instructionElement.getAttributeNode("opCode").getValue());
            // // instruction.setOpCode(opCode);
            // // String opName =
            // // instructionElement.getAttributeNode("opName").getValue();
            // // instruction.setOpName(opName);
            // // int lineNumber =
            // //
            // Integer.parseInt(instructionElement.getAttributeNode("lineNumber").getValue());
            // // instruction.setLineNumber(lineNumber);
            // // int globalInstructionNumber =
            // //
            // Integer.parseInt(instructionElement.getAttributeNode("globalInstructionNumber").getValue());
            // //
            // instruction.setGlobalInstructionNumber(globalInstructionNumber);
            // // int localInstructionNumber =
            // //
            // Integer.parseInt(instructionElement.getAttributeNode("localInstructionNumber").getValue());
            // // instruction.setLocalInstructionNumber(localInstructionNumber);
            // // String invokedMethod =
            // //
            // instructionElement.getAttributeNode("invokedMethod").getValue();
            // // instruction.setInvokedMethod(invokedMethod);
            // // String behaviour =
            // // instructionElement.getAttributeNode("behaviour").getValue();
            // // instruction.setBehaviour(behaviour);
            // // methodMap.put(localInstructionNumber,
            // // instruction);
            // instructionMap.put(instructionNumber, instructionMeta);
            // }
            // lineMap.put(lineNumber, instructionMap);
            // }
            // methodMap.put(methodName, lineMap);
            // }
            // classMap.put(className, methodMap);
            // int lineNb =
            // Integer.parseInt(attributes.getNamedItem("lineNb").getNodeValue());
            // int instructionNb =
            // Integer.parseInt(attributes.getNamedItem("instructionNb").getNodeValue());
            // int opCode =
            // Integer.parseInt(attributes.getNamedItem("opCode").getNodeValue());
            // String behaviour =
            // attributes.getNamedItem("behaviour").getNodeValue();
            // behaviourMap.put(classPath, lineNb, instructionNb, opCode,
            // behaviour);
            // }
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

        InstructionMeta instructionMeta = new InstructionMeta();

        Integer opCode = Integer.parseInt(instructionElement.getAttributeNode(OP_CODE_ATTR).getValue());
        instructionMeta.setOpCode(opCode);

        String opName = instructionElement.getAttributeNode(OP_NAME_ATTR).getValue();
        instructionMeta.setOpName(opName);

        String invokedMethod = instructionElement.getAttributeNode(INVOKED_METHOD).getValue();
        instructionMeta.setInvokedMethod(invokedMethod);

        String behaviour = instructionElement.getAttributeNode(BEHAVIOUR_ATTR).getValue();
        instructionMeta.setBehaviour(behaviour);

        return instructionMeta;
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
    // public boolean containsClassBehaviourMap(String className) {
    // return instructionMap.containsKey(className);
    // }
    //
    // public HashMap<String, HashMap<Integer, Instruction>>
    // getClassBehaviourMap(String className) {
    // return instructionMap.get(className);
    // }
    //
    // public boolean containsMethodBehaviourMap(String className, String
    // methodName) {
    // return instructionMap.containsKey(className) &&
    // instructionMap.get(className).containsKey(methodName);
    // }
    //
    // public HashMap<Integer, Instruction> getMethodBehaviourMap(String
    // className, String methodName) {
    // return instructionMap.get(className).get(methodName);
    // }

    // public boolean contains(String classPath, int instructionNb) {
    // return behaviourMap.contains(classPath, instructionNb);
    // }
    //
    // public boolean isBehaviourFor(String classPath, int instructionNb) {
    // return behaviourMap.isBehaviourFor(classPath, instructionNb);
    // }
    //
    //// private class BehaviourMap {
    //
    // private HashMap<String, HashMap<Integer, Behaviour>> map = new
    // HashMap<String, HashMap<Integer, Behaviour>>();
    //
    // public void put(String classPath, int lineNb, int instructionNb, int
    // opCode, String behaviour) {
    // if (map.containsKey(classPath))
    // map.get(classPath).put(instructionNb, new Behaviour(classPath, lineNb,
    // instructionNb, opCode, behaviour));
    // else {
    // HashMap<Integer, Behaviour> subMap = new HashMap<Integer, Behaviour>();
    // subMap.put(instructionNb, new Behaviour(classPath, lineNb, instructionNb,
    // opCode, behaviour));
    // map.put(classPath, subMap);
    // }
    // }
    //
    // public boolean contains(String classPath, int instructionNb) {
    // return map.containsKey(classPath) &&
    // map.get(classPath).containsKey(instructionNb);
    // }
    //
    // public boolean isBehaviourFor(String classPath, int instructionNb) {
    // return map.containsKey(classPath) &&
    // map.get(classPath).containsKey(instructionNb) &&
    // !map.get(classPath).get(instructionNb).behaviour.equals("IGNORE");
    // }
    //
    // private class Behaviour {
    // private String classPath;
    // private int lineNb;
    // private int instructionNb;
    // private int opCode;
    // private String behaviour;
    //
    // public Behaviour(String classPath, int lineNb, int instructionNb, int
    // opCode, String behaviour) {
    // this.classPath = classPath;
    // this.lineNb = lineNb;
    // this.instructionNb = instructionNb;
    // this.opCode = opCode;
    // this.behaviour = behaviour;
    // }
    //
    // }
    // }

}
