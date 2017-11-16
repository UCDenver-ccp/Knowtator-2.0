package edu.ucdenver.ccp.knowtator.io.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorDocumentHandler;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.annotator.Annotator;
import edu.ucdenver.ccp.knowtator.annotation.text.Span;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.ucdenver.ccp.knowtator.io.xml.XmlTags.*;

public class XmlReader {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public static void read(String fileName, KnowtatorManager manager, Boolean fromResources) throws ParserConfigurationException, IOException, SAXException {
        /*
        doc parses the XML into a graph
         */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        InputStream is = KnowtatorDocumentHandler.getFileInputStream(fileName, fromResources);

        Document doc;
        try {
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            List<Node> textSourceNodes = XmlUtil.asList(doc.getElementsByTagName(XmlTags.ANNOTATIONS));
            List<Node> profileNodes = XmlUtil.asList(doc.getElementsByTagName(XmlTags.PROFILE));
            List<Node> configNodes = XmlUtil.asList(doc.getElementsByTagName(XmlTags.CONFIG));

            if (textSourceNodes.size() > 0) {
                log.warn("Reading in annotations");
                readAnnotations(textSourceNodes, manager);
            }
            if (profileNodes.size() > 0) {
                log.warn("Reading in profiles");
                readProfiles(profileNodes, manager);
            }
            if (configNodes.size() > 0) {
                log.warn("Reading in configuration");
                readConfigFile(configNodes, manager);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private static void readConfigFile(List<Node> configNodes, KnowtatorManager manager) {
        for (Node configNode: configNodes) {
            Element configElement = (Element) configNode;

            manager.getConfigProperties().setAutoLoadOntologies(Boolean.valueOf(configElement.getElementsByTagName(XmlTags.AUTO_LOAD_ONTOLOGIES).item(0).getTextContent()));
            manager.getConfigProperties().setFormat(configElement.getElementsByTagName(XmlTags.FORMAT).item(0).getTextContent());
            manager.getConfigProperties().setDefaultSaveLocation(configElement.getElementsByTagName(XmlTags.DEFAULT_SAVE_LOCATION).item(0).getTextContent());
        }
    }

    public static void readAnnotations(List<Node> textSourceNodes, KnowtatorManager manager) throws ParserConfigurationException, IOException, SAXException {
        /*
        Get annotations by document
         */
        for (Node textSourceNode: textSourceNodes) {
            Element textSourceElement = (Element) textSourceNode;
            String textSource = textSourceElement.getAttribute(XmlTags.TEXTSOURCE);

            getAnnotationsFromXml(textSource, textSourceElement, manager);
        }

    }

    public static void readProfiles(List<Node> profileNodes, KnowtatorManager manager) throws ParserConfigurationException, IOException, SAXException {
        /*
        Get annotations by document
         */
        for (Node profileNode: profileNodes) {
            Element profileElement = (Element) profileNode;
            String profileName = profileElement.getAttribute(XmlTags.PROFILE_NAME);
            String profileID = profileElement.getAttribute(XmlTags.PROFILE_ID);

            Annotator newAnnotator = manager.getAnnotatorManager().addNewAnnotator(profileName, profileID);

            getHighlightersFromXml(newAnnotator, profileElement);
        }
    }

    public static void getHighlightersFromXml(Annotator newAnnotator, Element profileElement) {

        for (Node highlighterNode : XmlUtil.asList(profileElement.getElementsByTagName(XmlTags.HIGHLIGHTER))) {
            if (highlighterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element highlighterElement = (Element) highlighterNode;

                String className = highlighterElement.getElementsByTagName(XmlTags.CLASS_NAME).item(0).getTextContent();
                String color = highlighterElement.getElementsByTagName(XmlTags.COLOR).item(0).getTextContent();

                newAnnotator.addColor(className, color);
            }
        }
    }

    public static HashMap<String, String> getClassIDsFromXml(Element textSourceElement) {
        /*
        Next parse classes and add the annotations
         */
        HashMap<String, String> mentionTracker = new HashMap<>();

        for (Node classNode : XmlUtil.asList(textSourceElement.getElementsByTagName(CLASS_MENTION))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String mentionKey = getMentionKey(classElement.getAttribute(CLASS_MENTION_ID));

                String className = classElement.getElementsByTagName(MENTION_CLASS).item(0).getTextContent();

                mentionTracker.put(mentionKey, className);
            }
        }

        return mentionTracker;
    }

    public static String getMentionKey(String fullMention) {
        String mentionSource = XmlUtil.getMentionSourceFromXML(fullMention);
        int mentionID = XmlUtil.getMentionIDFromXML(fullMention);
        return mentionSource + mentionID;
    }

    public static void getAnnotationsFromXml(String textSource, Element textSourceElement, KnowtatorManager manager) {

        Map<String, String> classMentionToClassIDMap = getClassIDsFromXml(textSourceElement);

        for (Node annotationNode : XmlUtil.asList(textSourceElement.getElementsByTagName(ANNOTATION))) {
            if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element annotationElement = (Element) annotationNode;

                String mentionKey = getMentionKey(((Element) annotationElement.getElementsByTagName(MENTION).item(0)).getAttribute(MENTION_ID));

                String annotatorName = annotationElement.getElementsByTagName(ANNOTATOR).item(0).getTextContent();
                String annotatorID = ((Element) annotationElement.getElementsByTagName(ANNOTATOR).item(0)).getAttribute(ANNOTATOR_ID);

                Annotator annotator = manager.getAnnotatorManager().addNewAnnotator(annotatorName, annotatorID);
                String className = classMentionToClassIDMap.get(mentionKey);
                List<Span> spans = getSpanInfo(annotationElement);

                manager.getAnnotationManager().addAnnotation(textSource, annotator, className, spans);
            }
        }
    }

    public static List<Span> getSpanInfo(Element annotationElement) {
        List<Span> spans = new ArrayList<>();

        for (Node spanNode : XmlUtil.asList(annotationElement.getElementsByTagName(XmlTags.SPAN))) {
            if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
                Element spanElement = (Element) spanNode;
                String spanStart = spanElement.getAttribute(XmlTags.SPAN_START);
                String spanEnd = spanElement.getAttribute(XmlTags.SPAN_END);

                spans.add(new Span(Integer.parseInt(spanStart), Integer.parseInt(spanEnd)));
            }
        }
        return spans;
    }



}
