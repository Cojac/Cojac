package com.github.cojac.utils;

import java.io.File;

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

public class InstructionWriter {

    private static InstructionWriter INSTANCE;
    private Document document;

    private InstructionWriter() {
        initDocument();
    }

    public static InstructionWriter getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new InstructionWriter();
        }
        return INSTANCE;
    }

    private void initDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            this.document = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document.appendChild(document.createElement("instructions"));
    }

    public void appendInstruction(String classPath, int lineNb, int instructionNb, int opCode) {

        Element element = document.createElement("instruction");
        element.setAttribute("classPath", "" + classPath);
        element.setAttribute("lineNb", "" + lineNb);
        element.setAttribute("instructionNb", "" + instructionNb);
        element.setAttribute("opCode", "" + opCode);

        document.getDocumentElement().appendChild(element);
    }

    public void writeInstructionDocumentToFile(File file) {
        try {
            // Make a transformer factory to create the Transformer
            TransformerFactory tFactory = TransformerFactory.newInstance();

            // Make the Transformer
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // Mark the document as a DOM (XML) source
            DOMSource source = new DOMSource(document);

            // Say where we want the XML to go
            StreamResult result = new StreamResult(file);

            // Write the XML to file

            transformer.transform(source, result);
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
