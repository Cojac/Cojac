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

    private static BehaviourLoader INSTANCE;
    private HashMap<String, HashMap<String, HashMap<Integer, Instruction>>> instructionMap;

    private BehaviourLoader() {
        instructionMap = new HashMap<>();
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

            // NodeList nodeList =
            // document.getDocumentElement().getElementsByTagName("instruction");
            // for (int i = 0; i < nodeList.getLength(); i++) {
            // NamedNodeMap attributes = nodeList.item(i).getAttributes();
            // String classPath =
            // attributes.getNamedItem("classPath").getNodeValue();
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

            NodeList classMapNodes = document.getDocumentElement().getElementsByTagName("classMap");
            for (int i = 0; i < classMapNodes.getLength(); i++) {
                Node classMapNode = classMapNodes.item(i);
                if (classMapNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element classMapElement = (Element) classMapNode;
                HashMap<String, HashMap<Integer, Instruction>> classMap = new HashMap<>();
                String className = classMapElement.getAttributeNode("className").getValue();
                NodeList methodMapNodes = classMapElement.getElementsByTagName("methodMap");
                for (int j = 0; j < methodMapNodes.getLength(); j++) {

                    Node methodMapNode = methodMapNodes.item(j);
                    if (methodMapNode.getNodeType() != Node.ELEMENT_NODE)
                        continue;
                    Element methodMapElement = (Element) methodMapNode;
                    HashMap<Integer, Instruction> methodMap = new HashMap<>();
                    String methodName = methodMapElement.getAttributeNode("methodName").getValue();

                    NodeList instructionNodes = methodMapElement.getElementsByTagName("insctruction");
                    for (int k = 0; k < instructionNodes.getLength(); k++) {
                        Node instructionNode = instructionNodes.item(k);
                        if (instructionNode.getNodeType() != Node.ELEMENT_NODE)
                            continue;
                        Element instructionElement = (Element) instructionNode;
                        System.out.println(instructionElement.getNodeName());
                        Instruction instruction = new Instruction();
                        int opCode = Integer.parseInt(instructionElement.getAttributeNode("opCode").getValue());
                        instruction.setOpCode(opCode);
                        String opName = instructionElement.getAttributeNode("opName").getValue();
                        instruction.setOpName(opName);
                        int lineNumber = Integer.parseInt(instructionElement.getAttributeNode("lineNumber").getValue());
                        instruction.setLineNumber(lineNumber);
                        int globalInstructionNumber = Integer.parseInt(instructionElement.getAttributeNode("globalInstructionNumber").getValue());
                        instruction.setGlobalInstructionNumber(globalInstructionNumber);
                        int localInstructionNumber = Integer.parseInt(instructionElement.getAttributeNode("localInstructionNumber").getValue());
                        instruction.setLocalInstructionNumber(localInstructionNumber);
                        String invokedMethod = instructionElement.getAttributeNode("invokedMethod").getValue();
                        instruction.setInvokedMethod(invokedMethod);
                        String behaviour = instructionElement.getAttributeNode("behaviour").getValue();
                        instruction.setBehaviour(behaviour);
                        methodMap.put(localInstructionNumber, instruction);
                    }
                    classMap.put(methodName, methodMap);
                }
                instructionMap.put(className, classMap);
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
            }
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

    public boolean containsClassBehaviourMap(String className) {
        return instructionMap.containsKey(className);
    }

    public HashMap<String, HashMap<Integer, Instruction>> getClassBehaviourMap(String className) {
        return instructionMap.get(className);
    }

    public boolean containsMethodBehaviourMap(String className, String methodName) {
        return instructionMap.containsKey(className) &&
                instructionMap.get(className).containsKey(methodName);
    }

    public HashMap<Integer, Instruction> getMethodBehaviourMap(String className, String methodName) {
        return instructionMap.get(className).get(methodName);
    }

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
