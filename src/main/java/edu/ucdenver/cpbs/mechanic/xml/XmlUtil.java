package edu.ucdenver.cpbs.mechanic.xml;

import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotation;
import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public final class XmlUtil {
    private static String TAG_ANNOTATIONS = "annotations";
    private static String TAG_TEXTSOURCE = "textSource";

    private static String TAG_MENTION = "mention";
    private static String TAG_MENTION_ID = "id";
    private static String TAG_ANNOTATOR = "annotator";
    private static String TAG_ANNOTATOR_ID = "id";
    private static String TAG_SPAN = "span";
    private static String TAG_SPAN_START = "start";
    private static String TAG_SPAN_END = "end";
    private static String TAG_SPANNEDTEXT = "spannedText";
    private static String TAG_ANNOTATION = "annotation";

    private static String TAG_CLASS_MENTION = "classMention";
    private static String TAG_CLASS_MENTION_ID = "id";
    private static String TAG_MENTION_CLASS = "mentionClass";
    private static String TAG_MENTION_CLASS_ID = "id";
    private TextAnnotationManager textAnnotationManager;

    public XmlUtil(TextAnnotationManager textAnnotationManager) {
        this.textAnnotationManager = textAnnotationManager;
    }

    private static List<Node> asList(NodeList n) {
        return n.getLength()==0?
                Collections.emptyList(): new NodeListWrapper(n);
    }
    static final class NodeListWrapper extends AbstractList<Node>
            implements RandomAccess {
        private final NodeList list;
        NodeListWrapper(NodeList l) {
            list=l;
        }
        public Node get(int index) {
            return list.item(index);
        }
        public int size() {
            return list.getLength();
        }
    }

    private String[] annotationToXML(TextAnnotation textAnnotation, String mention) {
        String[] toWrite = new String[6];

        toWrite[0] = String.format("  <%s>", TAG_ANNOTATION);
        toWrite[1] = String.format("    <%s %s=\"%s\" />", TAG_MENTION, TAG_MENTION_ID, mention);
        toWrite[2] = String.format("    <%s %s=\"%s\">%s</%s>", TAG_ANNOTATOR, TAG_ANNOTATOR_ID, textAnnotation.getAnnotatorID(), textAnnotation.getAnnotator(), TAG_ANNOTATOR);
        toWrite[3] = String.format("    <%s %s=\"%s\" %s=\"%s\" />", TAG_SPAN, TAG_SPAN_START, textAnnotation.getSpanStart().toString(), TAG_SPAN_END, textAnnotation.getSpanEnd().toString());
        toWrite[4] = String.format("    <%s>%s</%s>", TAG_SPANNEDTEXT, textAnnotation.getSpannedText(), TAG_SPANNEDTEXT);
        toWrite[5] = String.format("  </%s>", TAG_ANNOTATION);

        return toWrite;
    }

    public void writeTextAnnotationsToXML(FileWriter fw, JTabbedPane tabbedPane) throws IOException, NoSuchFieldException {
        BufferedWriter bw = new BufferedWriter(fw);
        File textSource =new File(tabbedPane.getTitleAt((tabbedPane.getSelectedIndex())));

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();
        bw.write(String.format("<%s %s=\"%s\">", TAG_ANNOTATIONS, TAG_TEXTSOURCE, textSource.getName()));
        bw.newLine();

        for (Map.Entry<String, HashMap<Integer, TextAnnotation>> instance1 : textAnnotationManager.getTextAnnotations().entrySet()) {
            String mentionSource = instance1.getKey();
            for (Map.Entry<Integer, TextAnnotation> instance2 : instance1.getValue().entrySet() ){
                Integer mentionID = instance2.getKey();
                TextAnnotation textAnnotation = instance2.getValue();

                String mention = String.format("%s_Instance_%d", mentionSource, mentionID);

                for (String tag : annotationToXML(textAnnotation, mention)) {
                    bw.write(tag);
                    bw.newLine();
                }

                bw.write(String.format("  <%s %s=\"%s\">", TAG_CLASS_MENTION, TAG_CLASS_MENTION_ID, mention));
                bw.newLine();
                bw.write(String.format("    <%s %s=\"%s\">%s</%s>", TAG_MENTION_CLASS, TAG_MENTION_CLASS_ID, textAnnotation.getClassID(), textAnnotation.getClassName(), TAG_MENTION_CLASS));
                bw.newLine();
                bw.write(String.format("  </%s>", TAG_CLASS_MENTION));
                bw.newLine();
            }
        }

        bw.write(String.format("</%s>", TAG_ANNOTATIONS));
        bw.flush();
    }

    public void loadTextAnnotationsFromXML(InputStream is, MechAnICTextViewer textViewer) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();

        for (Node node: asList(doc.getElementsByTagName("annotation"))) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element)node;

                String fullMention = ((Element)eElement.getElementsByTagName(TAG_MENTION).item(0)).getAttribute(TAG_MENTION_ID);
                String annotatorName = eElement.getElementsByTagName(TAG_ANNOTATOR).item(0).getTextContent();
                String annotatorID = ((Element)eElement.getElementsByTagName(TAG_ANNOTATOR).item(0)).getAttribute(TAG_ANNOTATOR_ID);
                Integer spanStart = Integer.parseInt(((Element)eElement.getElementsByTagName(TAG_SPAN).item(0)).getAttribute(TAG_SPAN_START));
                Integer spanEnd = Integer.parseInt(((Element)eElement.getElementsByTagName(TAG_SPAN).item(0)).getAttribute(TAG_SPAN_END));
                String spannedText = eElement.getElementsByTagName(TAG_SPANNEDTEXT).item(0).getTextContent();

                String mentionSource = getMentionSourceFromXML(fullMention);
                int mentionID = getMentionIDFromXML(fullMention);

                //TODO: Figure out how to get the OWL classes from annotation instances
                //OWLClass cls = view.getOWLModelManager().getOWLEntityFinder().getOWLClass()
                TextAnnotation newAnnotation = new TextAnnotation(annotatorID, annotatorName, spanStart, spanEnd, spannedText);

                if(!textAnnotationManager.getTextAnnotations().containsKey(mentionSource)) {
                    textAnnotationManager.getTextAnnotations().put(mentionSource, new HashMap<>());
                }
                textAnnotationManager.getTextAnnotations().get(mentionSource).put(mentionID, newAnnotation);
                //textAnnotationManager.highlightAnnotation(spanStart, spanEnd, textViewer, );
            }
        }
        for (Node node : asList(doc.getElementsByTagName("classMention"))) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element)node;
                String fullMention = eElement.getAttribute(TAG_CLASS_MENTION_ID);
                String classID = ((Element)eElement.getElementsByTagName(TAG_MENTION_CLASS).item(0)).getAttribute(TAG_MENTION_CLASS_ID);
                String className = eElement.getElementsByTagName(TAG_MENTION_CLASS).item(0).getTextContent();

                String mentionSource = getMentionSourceFromXML(fullMention);
                int mentionID = getMentionIDFromXML(fullMention);

                if(!textAnnotationManager.getTextAnnotations().containsKey(mentionSource)) {
                    textAnnotationManager.getTextAnnotations().put(mentionSource, new HashMap<>());
                }
                TextAnnotation textAnnotation = textAnnotationManager.getTextAnnotations().get(mentionSource).get(mentionID);
                textAnnotation.setClassID(classID);
                textAnnotation.setClassName(className);
            }
        }
        //textAnnotationManager.highlightAllAnnotations(textViewer);
    }

    private String getMentionSourceFromXML(String fullMention) {
        String mentionSource;
        if(fullMention.indexOf("_new_Instance") < fullMention.indexOf("Instance") && fullMention.contains("_new_Instance")) {
            mentionSource = fullMention.substring(0, fullMention.indexOf("_new_Instance"));
        } else {
            mentionSource = fullMention.substring(0, fullMention.indexOf("_Instance"));
        }
        return mentionSource;
    }

    private Integer getMentionIDFromXML(String fullMention) {
        return Integer.parseInt(fullMention.substring(fullMention.indexOf("_Instance_")+10));
    }
}
