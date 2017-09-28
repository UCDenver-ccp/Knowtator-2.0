package edu.ucdenver.ccp.knowtator.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.iaa.Annotation;
import edu.ucdenver.ccp.knowtator.iaa.Span;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class XmlUtil {
    private static final Logger log = Logger.getLogger(KnowtatorView.class);

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
    private KnowtatorView view;

    public XmlUtil(KnowtatorView view) {
        this.view = view;
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

    private ArrayList<String> annotationToXML(Annotation textAnnotation, JTextPane textViewer) {
        ArrayList<String> toWrite = new ArrayList<>();

        toWrite.add(String.format("  <%s>", TAG_ANNOTATION));
//        toWrite.add(String.format("    <%s %s=\"%s\" />", TAG_MENTION, TAG_MENTION_ID, mention));
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", TAG_ANNOTATOR, TAG_ANNOTATOR_ID, textAnnotation.getAnnotatorID(), textAnnotation.getAnnotatorName(), TAG_ANNOTATOR));
        for (Span span : textAnnotation.getSpans()) {
            toWrite.add(String.format("    <%s %s=\"%s\" %s=\"%s\" />", TAG_SPAN, TAG_SPAN_START, span.getStart(), TAG_SPAN_END, span.getEnd()));
//            toWrite.add(String.format("    <%s>%s</%s>", TAG_SPANNEDTEXT, textViewer.getText().substring(span.getStart(), span.getEnd()), TAG_SPANNEDTEXT));
        }
        toWrite.add(String.format("    <%s %s=\"%s\">%s</%s>", TAG_MENTION_CLASS, TAG_MENTION_CLASS_ID, textAnnotation.getClassID(), textAnnotation.getClassName(), TAG_MENTION_CLASS));
        toWrite.add(String.format("  </%s>", TAG_CLASS_MENTION));
        toWrite.add(String.format("  </%s>", TAG_ANNOTATION));

        return toWrite;
    }

    public void writeTextAnnotationsToXML(FileWriter fw) throws IOException, NoSuchFieldException {
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bw.newLine();

        for (int i = 0; i < view.getTextViewer().getTabCount(); i++) {
            KnowtatorTextPane textPane = view.getTextViewer().getTextPaneByIndex(i);
            bw.write(String.format("<%s %s=\"%s\">", TAG_ANNOTATIONS, TAG_TEXTSOURCE, textPane.getName()));
            bw.newLine();
            textPane.getTextAnnotationManager().getTextAnnotations().forEach(textAnnotation ->  {

//                String mention = String.format("%s_Instance_%d", textAnnotation.getMentionSource(), textAnnotation.getMentionID());
                try {
                    for (String tag : annotationToXML(textAnnotation, textPane)) {
                        bw.write(tag);
                        bw.newLine();
                    }

//                    bw.write(String.format("  <%s %s=\"%s\">", TAG_CLASS_MENTION, TAG_CLASS_MENTION_ID, mention));
//                    bw.newLine();

                } catch (IOException e) {
                    log.error("IOException");
                    e.printStackTrace();
                }
            });
            bw.write(String.format("</%s>", TAG_ANNOTATIONS));
        }


        bw.flush();
    }

    public void loadTextAnnotationsFromXML(InputStream is) throws ParserConfigurationException, IOException, SAXException {
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
        for (Node textSourceNode: asList(doc.getElementsByTagName("annotations"))) {
            Element textSourceElement = (Element) textSourceNode;
            String textSource = textSourceElement.getAttribute(TAG_TEXTSOURCE);
            log.warn(String.format("Text source: %s", textSource));

            KnowtatorTextPane textPane = view.getTextViewer().getTextPaneByName(textSource);

            if (textPane != null) {
                HashMap<String, Element> mentionTracker = mapMentionToAnnotationElement(textSourceElement);
                addAnnotationsToKnowtator(textSourceElement, mentionTracker, textSource, textPane);
            }
        }
    }

    private void addAnnotationsToKnowtator(Element textSourceElement, HashMap<String, Element> mentionTracker, String textSource, KnowtatorTextPane textPane) {
        /*
        Next parse classes and add the annotations
         */
        for (Node classNode : asList(textSourceElement.getElementsByTagName("classMention"))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String fullMention = classElement.getAttribute(TAG_CLASS_MENTION_ID);
                String mentionSource = getMentionSourceFromXML(fullMention);
                int mentionID = getMentionIDFromXML(fullMention);

                Element annotationElement = mentionTracker.get(mentionSource + mentionID);

                String classID = ((Element) classElement.getElementsByTagName(TAG_MENTION_CLASS).item(0)).getAttribute(TAG_MENTION_CLASS_ID);
                String className;
                String annotatorName = annotationElement.getElementsByTagName(TAG_ANNOTATOR).item(0).getTextContent();
                String annotatorID = ((Element) annotationElement.getElementsByTagName(TAG_ANNOTATOR).item(0)).getAttribute(TAG_ANNOTATOR_ID);

                OWLClass cls = view.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
                if (cls != null) {
                    OWLAPIDataExtractor dataExtractor = textPane.getTextAnnotationManager().getDataExtractor();
                    try {
                        dataExtractor.extractOWLObjectData(cls);
                        className = dataExtractor.getClassName();
                        classID = dataExtractor.getClassID();
                    } catch (NullPointerException e) {
                        className = classElement.getElementsByTagName(TAG_MENTION_CLASS).item(0).getTextContent();
                    }
                    Annotation newAnnotation = new Annotation(textSource, cls);
                    newAnnotation.setClassName(className);
                    newAnnotation.setClassID(classID);
                    newAnnotation.setAnnotatorName(annotatorName);
                    newAnnotation.setAnnotatorID(annotatorID);
                    newAnnotation.setSpans(getSpanInfo(annotationElement));

                    try {
                        textPane.getTextAnnotationManager().addTextAnnotation(newAnnotation);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private ArrayList<Span> getSpanInfo(Element annotationElement) {
        ArrayList<Span> spans = new ArrayList<>();
        for (Node spanNode : asList(annotationElement.getElementsByTagName(TAG_SPAN))) {
            if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
                Element spanElement = (Element) spanNode;
                Integer spanStart = Integer.parseInt(spanElement.getAttribute(TAG_SPAN_START));
                Integer spanEnd = Integer.parseInt(spanElement.getAttribute(TAG_SPAN_END));

                Span newSpan = new Span(spanStart, spanEnd);
                spans.add(newSpan);
            }
        }
        log.warn(String.format("\t\tSpans: %d", spans.size()));
        return spans;
    }

    private HashMap<String, Element> mapMentionToAnnotationElement(Element textSourceElement) {
        /*
        Parse mentions first and refer to them later after parsing classes
         */
        HashMap<String, Element> mentionTracker = new HashMap<>();
        for (Node annotationNode : asList(textSourceElement.getElementsByTagName("annotation"))) {
            if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element annotationElement = (Element) annotationNode;

                String fullMention = ((Element) annotationElement.getElementsByTagName(TAG_MENTION).item(0)).getAttribute(TAG_MENTION_ID);

                String mentionSource = getMentionSourceFromXML(fullMention);
                int mentionID = getMentionIDFromXML(fullMention);

                mentionTracker.put(mentionSource + mentionID, annotationElement);
            }
        }
        log.warn(String.format("\tMentions: %d", mentionTracker.size()));

        return mentionTracker;
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

    public static void main(String[] args) {
        XmlUtil xmlUtil = new XmlUtil(new KnowtatorView());
        FileWriter fw;
        try {
            fw = new FileWriter("C:/Users/Harrison/Google Drive/HunterLab/test_anno.xml");
            xmlUtil.writeTextAnnotationsToXML(fw);
            fw.close();
        } catch (IOException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
    }
}
