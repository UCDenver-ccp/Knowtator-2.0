package edu.ucdenver.ccp.knowtator.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class XmlReader {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);

    public static void read(InputStream is, KnowtatorView view) throws ParserConfigurationException, IOException, SAXException {
        log.warn("Reading annotations from XML");

        /*
        doc parses the XML into a graph
         */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();

        /*
        Get annotations by document
         */
        for (Node textSourceNode: XmlUtil.asList(doc.getElementsByTagName("annotations"))) {
            Element textSourceElement = (Element) textSourceNode;
            String textSource = textSourceElement.getAttribute(XmlTags.TAG_TEXTSOURCE);
            log.warn(String.format("Text source: %s", textSource));

            HashMap<String, Element> mentionTracker = mapMentionToAnnotationElement(textSourceElement);
            addAnnotationsToKnowtator(textSourceElement, mentionTracker, textSource, view);
        }
    }

    public static void addAnnotationsToKnowtator(Element textSourceElement, HashMap<String, Element> mentionTracker, String textSource, KnowtatorView view) {
        log.warn("Adding annotations to Knowtator");

        /*
        Next parse classes and add the annotations
         */
        for (Node classNode : XmlUtil.asList(textSourceElement.getElementsByTagName("classMention"))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String fullMention = classElement.getAttribute(XmlTags.TAG_CLASS_MENTION_ID);
                String mentionSource = XmlUtil.getMentionSourceFromXML(fullMention);
                int mentionID = XmlUtil.getMentionIDFromXML(fullMention);

                Element annotationElement = mentionTracker.get(mentionSource + mentionID);

                String classID = ((Element) classElement.getElementsByTagName(XmlTags.TAG_MENTION_CLASS).item(0)).getAttribute(XmlTags.TAG_MENTION_CLASS_ID);
                String className;
                String annotatorName = annotationElement.getElementsByTagName(XmlTags.TAG_ANNOTATOR).item(0).getTextContent();
                String annotatorID = ((Element) annotationElement.getElementsByTagName(XmlTags.TAG_ANNOTATOR).item(0)).getAttribute(XmlTags.TAG_ANNOTATOR_ID);

                OWLClass cls = OWLAPIDataExtractor.getOWLClassByID(view, classID);
                try {

                    className = OWLAPIDataExtractor.getClassName(view, cls);
                    classID = OWLAPIDataExtractor.getClassID(view, cls);
                } catch (NullPointerException e) {
                    className = classElement.getElementsByTagName(XmlTags.TAG_MENTION_CLASS).item(0).getTextContent();
                }
                TextAnnotation newTextAnnotation = new TextAnnotation(textSource, cls);
                newTextAnnotation.setClassName(className);
                newTextAnnotation.setClassID(classID);
                newTextAnnotation.setAnnotatorName(annotatorName);
                newTextAnnotation.setAnnotatorID(annotatorID);
                newTextAnnotation.setTextSpans(XmlUtil.getSpanInfo(annotationElement));

                try {
                    view.getTextAnnotationManager().addTextAnnotation(textSource, newTextAnnotation);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static HashMap<String, Element> mapMentionToAnnotationElement(Element textSourceElement) {
        /*
        Parse mentions first and refer to them later after parsing classes
         */
        HashMap<String, Element> mentionTracker = new HashMap<>();
        for (Node annotationNode : XmlUtil.asList(textSourceElement.getElementsByTagName("annotation"))) {
            if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element annotationElement = (Element) annotationNode;

                String fullMention = ((Element) annotationElement.getElementsByTagName(XmlTags.TAG_MENTION).item(0)).getAttribute(XmlTags.TAG_MENTION_ID);

                String mentionSource = XmlUtil.getMentionSourceFromXML(fullMention);
                int mentionID = XmlUtil.getMentionIDFromXML(fullMention);

                mentionTracker.put(mentionSource + mentionID, annotationElement);
            }
        }
        log.warn(String.format("\tMentions: %d", mentionTracker.size()));

        return mentionTracker;
    }



}
