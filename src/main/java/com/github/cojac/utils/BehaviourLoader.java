package com.github.cojac.utils;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BehaviourLoader {

    private static BehaviourLoader INSTANCE;
    private Document document;
    private BehaviourMap behaviourMap = new BehaviourMap();

    private BehaviourLoader() {
    }

    public static BehaviourLoader getinstance() {
        if (INSTANCE == null) {
            INSTANCE = new BehaviourLoader();
        }
        return INSTANCE;
    }

    public void initDocument(String filePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(filePath); // Parse the XML file
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

        NodeList nodeList = document.getDocumentElement().getElementsByTagName("instruction");
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();
            String classPath = attributes.getNamedItem("classPath").getNodeValue();
            int lineNb = Integer.parseInt(attributes.getNamedItem("lineNb").getNodeValue());
            int instructionNb = Integer.parseInt(attributes.getNamedItem("instructionNb").getNodeValue());
            int opCode = Integer.parseInt(attributes.getNamedItem("opCode").getNodeValue());
            String behaviour = attributes.getNamedItem("behaviour").getNodeValue();
            behaviourMap.put(classPath, lineNb, instructionNb, opCode, behaviour);
        }

    }

    public boolean contains(String classPath, int instructionNb) {
        return behaviourMap.contains(classPath, instructionNb);
    }

    public boolean isBehaviourFor(String classPath, int instructionNb) {
        return behaviourMap.isBehaviourFor(classPath, instructionNb);
    }

    private class BehaviourMap {

        private HashMap<String, HashMap<Integer, Behaviour>> map = new HashMap<String, HashMap<Integer, Behaviour>>();

        public void put(String classPath, int lineNb, int instructionNb, int opCode, String behaviour) {
            if (map.containsKey(classPath))
                map.get(classPath).put(instructionNb, new Behaviour(classPath, lineNb, instructionNb, opCode, behaviour));
            else {
                HashMap<Integer, Behaviour> subMap = new HashMap<Integer, Behaviour>();
                subMap.put(instructionNb, new Behaviour(classPath, lineNb, instructionNb, opCode, behaviour));
                map.put(classPath, subMap);
            }
        }

        public boolean contains(String classPath, int instructionNb) {
            return map.containsKey(classPath) &&
                    map.get(classPath).containsKey(instructionNb);
        }

        public boolean isBehaviourFor(String classPath, int instructionNb) {
            return map.containsKey(classPath) &&
                    map.get(classPath).containsKey(instructionNb) &&
                    !map.get(classPath).get(instructionNb).behaviour.equals("IGNORE");
        }

        private class Behaviour {
            private String classPath;
            private int lineNb;
            private int instructionNb;
            private int opCode;
            private String behaviour;

            public Behaviour(String classPath, int lineNb, int instructionNb, int opCode, String behaviour) {
                this.classPath = classPath;
                this.lineNb = lineNb;
                this.instructionNb = instructionNb;
                this.opCode = opCode;
                this.behaviour = behaviour;
            }

        }
    }

}
