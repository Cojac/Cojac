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

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PolyBehaviourLoader {
    //private static String ROOT_ELT = "classes";
    private static String CLASS_ELT = "class";
    private static String LINE_ELT = "line";
    private static String CLASS_NAME_ATTR = "className";
    private static String LINE_NUMBER_ATTR = "lineNumber";
    private static String BEHAVIOUR_ATTR = "behaviour";

    private static PolyBehaviourLoader INSTANCE;

    // classeName, lineNumber, behaviour
    private HashMap<String, HashMap<Integer, String>> classMap;

    private PolyBehaviourLoader() {
    }

    public static PolyBehaviourLoader getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new PolyBehaviourLoader();
        }
        return INSTANCE;
    }

    public void loadBehaviours(String filePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(filePath); // Parse the XML
                                                            // file
            classMap = new HashMap<>();
            NodeList classNodes = document.getDocumentElement().getElementsByTagName(CLASS_ELT);
            for (int i = 0; i < classNodes.getLength(); i++) {
                Node classNode = classNodes.item(i);
                if (classNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                Element classElement = (Element) classNode;
                HashMap<Integer, String> lineMap = new HashMap<>();
                String className = classElement.getAttributeNode(CLASS_NAME_ATTR).getValue();
                NodeList lineNodes = classElement.getElementsByTagName(LINE_ELT);
                for (int j = 0; j < lineNodes.getLength(); j++) {
                    Node lineNode = lineNodes.item(j);
                    if (lineNode.getNodeType() != Node.ELEMENT_NODE)
                        continue;
                    Element lineElement = (Element) lineNode;
                    int lineNumber = Integer.parseInt(lineElement.getAttributeNode(LINE_NUMBER_ATTR).getValue());
                    String behaviour = lineElement.getAttributeNode(BEHAVIOUR_ATTR).getValue();
                    lineMap.put(lineNumber, behaviour);
                }
                classMap.put(className, lineMap);
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean isSpecifiedBehaviour(String className, int lineNumber) {
        return classMap.containsKey(className) &&
                classMap.get(className).containsKey(lineNumber);
    }

    public String getSpecifiedBehaviour(String className, int lineNumber) {
        return classMap.get(className).get(lineNumber);
    }
}
