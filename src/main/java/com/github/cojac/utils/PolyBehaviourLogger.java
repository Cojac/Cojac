/*
 *    Copyright 2017 Frédéric Bapst et al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

public class PolyBehaviourLogger {

    private static String ROOT_ELT = "classes";
    private static String CLASS_ELT = "class";
    private static String LINE_ELT = "line";
    private static String CLASS_NAME_ATTR = "className";
    private static String LINE_NUMBER_ATTR = "lineNumber";
    private static String BEHAVIOUR_ATTR = "behaviour";

    private static PolyBehaviourLogger INSTANCE;

    // classeName, lineNumber, behaviours
    private HashMap<String, HashMap<Integer, String>> classMap;

    private PolyBehaviourLogger() {
        classMap = new HashMap<>();
    }

    public static PolyBehaviourLogger getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new PolyBehaviourLogger();
        }
        return INSTANCE;
    }

    public void writeLogs(String filePath) {
        try {
            // ----------------------------------------------------------------------
            // prepare the XML document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            // ----------------------------------------------------------------------
            // fill the XML document with instructions
            Element classesElement = document.createElement(ROOT_ELT);
            for (String className : classMap.keySet()) {
                Element classElement = document.createElement(CLASS_ELT);
                classElement.setAttribute(CLASS_NAME_ATTR, className);
                for (Integer lineNumber : classMap.get(className).keySet()) {
                    Element lineElement = document.createElement(LINE_ELT);
                    lineElement.setAttribute(LINE_NUMBER_ATTR, lineNumber + "");
                    lineElement.setAttribute(BEHAVIOUR_ATTR, classMap.get(className).get(lineNumber));
                    classElement.appendChild(lineElement);
                }
                classesElement.appendChild(classElement);
            }
            document.appendChild(classesElement);

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
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }

    public synchronized void log(String className, int lineNumer) {
        if (!classMap.containsKey(className))
            classMap.put(className, new HashMap<>());
        classMap.get(className).put(lineNumer, "IGNORE");
    }

}
