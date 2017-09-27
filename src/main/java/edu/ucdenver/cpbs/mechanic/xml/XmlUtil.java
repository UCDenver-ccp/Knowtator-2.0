//TODO Fix XML read/write

//package edu.ucdenver.cpbs.mechanic.xml;
//
//import edu.ucdenver.cpbs.mechanic.MechAnICView;
//import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotation;
//import edu.ucdenver.cpbs.mechanic.iaa.Annotation;
//import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
//import org.apache.log4j.Logger;
//import org.semanticweb.owlapi.model.OWLClass;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.AbstractList;
//import java.util.Collections;
//import java.util.List;
//import java.util.RandomAccess;
//
//public final class XmlUtil {
//    private static final Logger log = Logger.getLogger(MechAnICView.class);
//
//    private static String TAG_ANNOTATIONS = "annotations";
//    private static String TAG_TEXTSOURCE = "textSource";
//
//    private static String TAG_MENTION = "mention";
//    private static String TAG_MENTION_ID = "id";
//    private static String TAG_ANNOTATOR = "annotator";
//    private static String TAG_ANNOTATOR_ID = "id";
//    private static String TAG_SPAN = "span";
//    private static String TAG_SPAN_START = "start";
//    private static String TAG_SPAN_END = "end";
//    private static String TAG_SPANNEDTEXT = "spannedText";
//    private static String TAG_ANNOTATION = "annotation";
//
//    private static String TAG_CLASS_MENTION = "classMention";
//    private static String TAG_CLASS_MENTION_ID = "id";
//    private static String TAG_MENTION_CLASS = "mentionClass";
//    private static String TAG_MENTION_CLASS_ID = "id";
//    private MechAnICView view;
//
//    public XmlUtil(MechAnICView view) {
//        this.view = view;
//    }
//
//    private static List<Node> asList(NodeList n) {
//        return n.getLength()==0?
//                Collections.emptyList(): new NodeListWrapper(n);
//    }
//    static final class NodeListWrapper extends AbstractList<Node>
//            implements RandomAccess {
//        private final NodeList list;
//        NodeListWrapper(NodeList l) {
//            list=l;
//        }
//        public Node get(int index) {
//            return list.item(index);
//        }
//        public int size() {
//            return list.getLength();
//        }
//    }
//
//    private String[] annotationToXML(Annotation textAnnotation, String mention) {
//        String[] toWrite = new String[6];
//
//        toWrite[0] = String.format("  <%s>", TAG_ANNOTATION);
//        toWrite[1] = String.format("    <%s %s=\"%s\" />", TAG_MENTION, TAG_MENTION_ID, mention);
//        toWrite[2] = String.format("    <%s %s=\"%s\">%s</%s>", TAG_ANNOTATOR, TAG_ANNOTATOR_ID, textAnnotation.getAnnotatorID(), textAnnotation.getAnnotator(), TAG_ANNOTATOR);
//        toWrite[3] = String.format("    <%s %s=\"%s\" %s=\"%s\" />", TAG_SPAN, TAG_SPAN_START, textAnnotation.getSpanStart().toString(), TAG_SPAN_END, textAnnotation.getSpanEnd().toString());
//        toWrite[4] = String.format("    <%s>%s</%s>", TAG_SPANNEDTEXT, textAnnotation.getSpannedText(), TAG_SPANNEDTEXT);
//        toWrite[5] = String.format("  </%s>", TAG_ANNOTATION);
//
//        return toWrite;
//    }
//
//    public void writeTextAnnotationsToXML(FileWriter fw) throws IOException, NoSuchFieldException {
//        BufferedWriter bw = new BufferedWriter(fw);
//
//        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//        bw.newLine();
//
//        for (int i = 0; i < view.getTextViewerTabbedPane().getTabCount(); i++) {
//            MechAnICTextViewer textViewer = (MechAnICTextViewer) view.getTextViewerTabbedPane().getTabComponentAt(i);
//            bw.write(String.format("<%s %s=\"%s\">", TAG_ANNOTATIONS, TAG_TEXTSOURCE, textViewer.getName().replace(".txt", "")));
//            bw.newLine();
//            textViewer.getTextAnnotationManager().getTextAnnotations().forEach((Integer mentionID, TextAnnotation textAnnotation) ->  {
//
//                String mention = String.format("%s_Instance_%d", textAnnotation.getMentionSource(), textAnnotation.getMentionID());
//                try {
//                    for (String tag : annotationToXML(textAnnotation, mention)) {
//                        bw.write(tag);
//                        bw.newLine();
//                    }
//
//                    bw.write(String.format("  <%s %s=\"%s\">", TAG_CLASS_MENTION, TAG_CLASS_MENTION_ID, mention));
//                    bw.newLine();
//                    bw.write(String.format("    <%s %s=\"%s\">%s</%s>", TAG_MENTION_CLASS, TAG_MENTION_CLASS_ID, textAnnotation.getClassID(), textAnnotation.getClassName(), TAG_MENTION_CLASS));
//                    bw.newLine();
//                    bw.write(String.format("  </%s>", TAG_CLASS_MENTION));
//                    bw.newLine();
//                } catch (IOException e) {
//                    log.error("IOException");
//                    e.printStackTrace();
//                }
//            });
//        }
//
//        bw.write(String.format("</%s>", TAG_ANNOTATIONS));
//        bw.flush();
//    }
//
//    public void loadTextAnnotationsFromXML(InputStream is, MechAnICTextViewer textViewer) throws ParserConfigurationException, IOException, SAXException {
//        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//        Document doc = dBuilder.parse(is);
//        doc.getDocumentElement().normalize();
//        for (Node textSourceNode: asList(doc.getElementsByTagName("annotations"))) {
//            Element textSourceElement = (Element) textSourceNode;
//            String textSource = textSourceElement.getAttribute(TAG_TEXTSOURCE);
//
//            for (Node annotationNode : asList(textSourceElement.getElementsByTagName("annotation"))) {
//                if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element annotationElement = (Element) annotationNode;
//
//                    String fullMention = ((Element) annotationElement.getElementsByTagName(TAG_MENTION).item(0)).getAttribute(TAG_MENTION_ID);
//                    String annotatorName = annotationElement.getElementsByTagName(TAG_ANNOTATOR).item(0).getTextContent();
//                    String annotatorID = ((Element) annotationElement.getElementsByTagName(TAG_ANNOTATOR).item(0)).getAttribute(TAG_ANNOTATOR_ID);
//                    Integer spanStart = Integer.parseInt(((Element) annotationElement.getElementsByTagName(TAG_SPAN).item(0)).getAttribute(TAG_SPAN_START));
//                    Integer spanEnd = Integer.parseInt(((Element) annotationElement.getElementsByTagName(TAG_SPAN).item(0)).getAttribute(TAG_SPAN_END));
//                    String spannedText = annotationElement.getElementsByTagName(TAG_SPANNEDTEXT).item(0).getTextContent();
//
//                    String mentionSource = getMentionSourceFromXML(fullMention);
//                    int mentionID = getMentionIDFromXML(fullMention);
//
//                    //TODO: Figure out how to get the OWL classes from annotation instances
//                    //
//                    TextAnnotation newAnnotation = new TextAnnotation(textSource, mentionSource, mentionID, annotatorID, annotatorName, spanStart, spanEnd, spannedText);
//
//                    textViewer.getTextAnnotationManager().getTextAnnotations().put(mentionID, newAnnotation);
//                }
//            }
//            for (Node node : asList(textSourceElement.getElementsByTagName("classMention"))) {
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) node;
//                    String fullMention = eElement.getAttribute(TAG_CLASS_MENTION_ID);
//                    String classID = ((Element) eElement.getElementsByTagName(TAG_MENTION_CLASS).item(0)).getAttribute(TAG_MENTION_CLASS_ID);
//                    String className = eElement.getElementsByTagName(TAG_MENTION_CLASS).item(0).getTextContent();
//
////                    String mentionSource = getMentionSourceFromXML(fullMention);
//                    int mentionID = getMentionIDFromXML(fullMention);
//                    OWLClass cls = view.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
//
//
//                    TextAnnotation textAnnotation = textViewer.getTextAnnotationManager().getTextAnnotations().get(mentionID);
//                    textAnnotation.setClassID(classID);
//                    textAnnotation.setClassName(className);
//                    textAnnotation.setOwlClass(cls);
//                }
//            }
//        }
//    }
//
//    private String getMentionSourceFromXML(String fullMention) {
//        String mentionSource;
//        if(fullMention.indexOf("_new_Instance") < fullMention.indexOf("Instance") && fullMention.contains("_new_Instance")) {
//            mentionSource = fullMention.substring(0, fullMention.indexOf("_new_Instance"));
//        } else {
//            mentionSource = fullMention.substring(0, fullMention.indexOf("_Instance"));
//        }
//        return mentionSource;
//    }
//
//    private Integer getMentionIDFromXML(String fullMention) {
//        return Integer.parseInt(fullMention.substring(fullMention.indexOf("_Instance_")+10));
//    }
//}
