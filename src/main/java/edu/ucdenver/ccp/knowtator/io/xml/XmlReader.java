package edu.ucdenver.ccp.knowtator.io.xml;

import edu.ucdenver.ccp.knowtator.io.txt.KnowtatorDocumentHandler;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
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

class XmlReader {
    private static final Logger log = Logger.getLogger(KnowtatorManager.class);

    static void read(String fileName, KnowtatorManager manager, Boolean fromResources) throws ParserConfigurationException, IOException, SAXException {
        /*
        doc parses the XML into a graph
         */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        InputStream is = KnowtatorDocumentHandler.getFileInputStream(fileName, fromResources);

        if (is != null) {
            Document doc;
            try {
                doc = dBuilder.parse(is);
                doc.getDocumentElement().normalize();

                List<Node> knowtatorNodes = XmlUtil.asList(doc.getElementsByTagName(XmlTags.KNOWTATOR_PROJECT));
                if (knowtatorNodes.size() > 0) {
                    Element knowtatorElement = (Element) knowtatorNodes.get(0);


                    List<Node> profileNodes = XmlUtil.asList(knowtatorElement.getElementsByTagName(XmlTags.PROFILE));
                    List<Node> configNodes = XmlUtil.asList(knowtatorElement.getElementsByTagName(XmlTags.CONFIG));
                    List<Node> documentNodes = XmlUtil.asList(knowtatorElement.getElementsByTagName(XmlTags.DOCUMENT));

                    if (configNodes.size() > 0) {
                        log.warn("Reading in configuration");
                        readConfigFile(manager, configNodes);
                    }
                    if (profileNodes.size() > 0) {
                        log.warn("Reading in profiles");
                        readProfiles(manager, profileNodes);
                    }
                    if (documentNodes.size() > 0) {
                        log.warn("Reading in documents");
                        readDocuments(manager, documentNodes);
                    }

                }

                List<Node> annotationNodes = XmlUtil.asList(doc.getElementsByTagName(XmlTags.ANNOTATIONS));
                if (annotationNodes.size() > 0) {
                    log.warn("Reading in old Knowtator project");
                    readAnnotations(manager, annotationNodes);
                }


            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else {
            log.warn(String.format("Could not read %s", fileName));
        }
    }

    private static void readDocuments(KnowtatorManager manager, List<Node> documentNodes) {
        for (Node documentNode : documentNodes) {
            Element documentElement = (Element) documentNode;

            String documentID = documentElement.getAttribute(XmlTags.DOCUMENT_ID);



            StringBuilder text = new StringBuilder();
            for (Node textNode : XmlUtil.asList(documentElement.getElementsByTagName(XmlTags.TEXT))) {
                Element textElement = (Element) textNode;
                text.append(textElement.getTextContent());
            }

            TextSource newTextSource = manager.getTextSourceManager().addTextSource(documentID, text.toString());

            getAnnotationsFromXml_NEW(manager, newTextSource, documentElement);
            getAssertionsFromXml(manager, newTextSource, documentElement);
        }
    }

    private static void getAssertionsFromXml(KnowtatorManager manager, TextSource textSource, Element documentElement) {
        for (Node assertionNode : XmlUtil.asList(documentElement.getElementsByTagName(XmlTags.ASSERTION))) {
            Element assertionElement = (Element) assertionNode;

            String assertionID = assertionElement.getElementsByTagName(XmlTags.ASSERTION_ID).item(0).getTextContent();
            String profileID = assertionElement.getElementsByTagName(XmlTags.ANNOTATOR).item(0).getTextContent();

            Profile profile = manager.getProfileManager().addNewProfile(profileID);
            String source = assertionElement.getElementsByTagName(XmlTags.ASSERTION_SOURCE).item(0).getTextContent();
            String target = assertionElement.getElementsByTagName(XmlTags.ASSERTION_TARGET).item(0).getTextContent();
            String relationship = assertionElement.getElementsByTagName(XmlTags.ASSERTION_RELATIONSHIP).item(0).getTextContent();

            textSource.addAssertion(assertionID, profile, source, target, relationship);
        }
    }


    private static void readConfigFile(KnowtatorManager manager, List<Node> configNodes) {
        for (Node configNode: configNodes) {
            Element configElement = (Element) configNode;

            manager.getConfigProperties().setAutoLoadOntologies(Boolean.valueOf(configElement.getElementsByTagName(XmlTags.AUTO_LOAD_ONTOLOGIES).item(0).getTextContent()));
            manager.getConfigProperties().setFormat(configElement.getElementsByTagName(XmlTags.FORMAT).item(0).getTextContent());
            manager.getConfigProperties().setDefaultSaveLocation(configElement.getElementsByTagName(XmlTags.DEFAULT_SAVE_LOCATION).item(0).getTextContent());
        }
    }

    private static void readAnnotations(KnowtatorManager manager, List<Node> textSourceNodes) throws ParserConfigurationException, IOException, SAXException {
        /*
        Get annotations by document
         */
        for (Node textSourceNode: textSourceNodes) {
            Element textSourceElement = (Element) textSourceNode;
            String docID = textSourceElement.getAttribute(XmlTags.DOCUMENT_ID);

            TextSource newTextSource = manager.getTextSourceManager().addTextSource(docID, null);
            getAnnotationsFromXml_OLD(manager, newTextSource, textSourceElement);
        }

    }

    private static void readProfiles(KnowtatorManager manager, List<Node> profileNodes) throws ParserConfigurationException, IOException, SAXException {
        /*
        Get annotations by document
         */
        for (Node profileNode: profileNodes) {
            Element profileElement = (Element) profileNode;
            String profileID = profileElement.getAttribute(XmlTags.PROFILE_ID);

            Profile newProfile = manager.getProfileManager().addNewProfile(profileID);

            getHighlightersFromXml(newProfile, profileElement);
        }
    }

    private static void getHighlightersFromXml(Profile newProfile, Element profileElement) {

        for (Node highlighterNode : XmlUtil.asList(profileElement.getElementsByTagName(XmlTags.HIGHLIGHTER))) {
            if (highlighterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element highlighterElement = (Element) highlighterNode;

                String classID = highlighterElement.getElementsByTagName(XmlTags.CLASS_ID).item(0).getTextContent();
                String color = highlighterElement.getElementsByTagName(XmlTags.COLOR).item(0).getTextContent();

                newProfile.addColor(classID, color);
            }
        }
    }

    private static HashMap<String, Element> getClassIDsFromXml(Element textSourceElement) {
        /*
        Next parse classes and add the annotations
         */
        HashMap<String, Element> mentionTracker = new HashMap<>();

        for (Node classNode : XmlUtil.asList(textSourceElement.getElementsByTagName(XmlTags.CLASS_MENTION))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String mentionKey = getMentionKey(classElement.getAttribute(XmlTags.CLASS_MENTION_ID));
                mentionTracker.put(mentionKey, classElement);
            }
        }

        return mentionTracker;
    }

    private static String getMentionKey(String fullMention) {
        String mentionSource = XmlUtil.getMentionSourceFromXML(fullMention);
        int mentionID = XmlUtil.getMentionIDFromXML(fullMention);
        return mentionSource + mentionID;
    }

    //TODO: Parse complexSlotMentions
    private static void getAnnotationsFromXml_OLD(KnowtatorManager manager, TextSource textSource, Element textSourceElement) {

        Map<String, Element> classMentionToClassIDMap = getClassIDsFromXml(textSourceElement);

        for (Node annotationNode : XmlUtil.asList(textSourceElement.getElementsByTagName(XmlTags.ANNOTATION))) {
            if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element annotationElement = (Element) annotationNode;
                String profileID = annotationElement.getElementsByTagName(XmlTags.ANNOTATOR).item(0).getTextContent();
                Profile profile = manager.getProfileManager().addNewProfile(profileID);
                List<Span> spans = getSpanInfo(annotationElement);


                String mentionKey = getMentionKey(((Element) annotationElement.getElementsByTagName(XmlTags.MENTION).item(0)).getAttribute(XmlTags.MENTION_ID));
                Element classElement = classMentionToClassIDMap.get(mentionKey);


                String className = classElement.getElementsByTagName(XmlTags.MENTION_CLASS).item(0).getTextContent();
                String classID = ((Element) classElement.getElementsByTagName(XmlTags.MENTION_CLASS).item(0)).getAttribute(XmlTags.MENTION_CLASS_ID);

                textSource.addAnnotation(null, classID, profile, className, spans);
            }
        }
    }

    private static void getAnnotationsFromXml_NEW(KnowtatorManager manager, TextSource textSource, Element documentElement) {
        for (Node annotationNode : XmlUtil.asList(documentElement.getElementsByTagName(XmlTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            String annotationID = annotationElement.getElementsByTagName(XmlTags.ANNOTATION_ID).item(0).getTextContent();
            String profileID = annotationElement.getElementsByTagName(XmlTags.ANNOTATOR).item(0).getTextContent();

            Profile profile = manager.getProfileManager().addNewProfile(profileID);
            String className = annotationElement.getElementsByTagName(XmlTags.CLASS_NAME).item(0).getTextContent();
            String classID = annotationElement.getElementsByTagName(XmlTags.CLASS_ID).item(0).getTextContent();
            List<Span> spans = getSpanInfo(annotationElement);

            textSource.addAnnotation(annotationID, classID, profile, className, spans);
        }
    }

    private static List<Span> getSpanInfo(Element annotationElement) {
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
