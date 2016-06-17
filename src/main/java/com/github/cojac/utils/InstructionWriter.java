package com.github.cojac.utils;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.cojac.Arg;
import com.github.cojac.Args;

public class InstructionWriter {

    private HashMap<String, HashMap<String, HashMap<Integer, Instruction>>> instructionMap;
    private static InstructionWriter INSTANCE;

    private InstructionWriter() {
        instructionMap = new HashMap<>();
    }

    public static InstructionWriter getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new InstructionWriter();
        }
        return INSTANCE;
    }

    public void writeInstructionDocumentToFile(Args args) {
        try {
            // get the path to XML file
            String filePath = args.getValue(Arg.LISTING_INSTRUCTIONS);
            // ----------------------------------------------------------------------
            // prepare the XML document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            // ----------------------------------------------------------------------
            // fill the XML document with instructions
            Element instructionMapElement = document.createElement("instructionMap");
            for (String className : instructionMap.keySet()) {
                Element classMapElement = document.createElement("classMap");
                classMapElement.setAttribute("className", className);
                for (String methodName : instructionMap.get(className).keySet()) {
                    Element methodMapElement = document.createElement("methodMap");
                    methodMapElement.setAttribute("methodName", methodName);
                    for (Integer localInstructionNumber : instructionMap.get(className).get(methodName).keySet()) {
                        Element instructionElement = document.createElement("insctruction");
                        Instruction instruction = instructionMap.get(className).get(methodName).get(localInstructionNumber);
                        instructionElement.setAttribute("opCode", "" +
                                instruction.getOpCode());
                        instructionElement.setAttribute("opName", "" +
                                instruction.getOpName());
                        instructionElement.setAttribute("lineNumber", "" +
                                instruction.getLineNumber());
                        instructionElement.setAttribute("globalInstructionNumber", "" +
                                instruction.getGlobalInstructionNumber());
                        instructionElement.setAttribute("localInstructionNumber", "" +
                                instruction.getLocalInstructionNumber());
                        instructionElement.setAttribute("invokedMethod", "" +
                                instruction.getInvokedMethod());
                        instructionElement.setAttribute("behaviour", "" +
                                instruction.getBehaviour());
                        methodMapElement.appendChild(instructionElement);
                    }
                    classMapElement.appendChild(methodMapElement);
                }
                instructionMapElement.appendChild(classMapElement);
            }
            document.appendChild(instructionMapElement);
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
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public synchronized void putClassMap(String className, HashMap<String, HashMap<Integer, Instruction>> classMap) {
        instructionMap.put(className, classMap);
    }

}
