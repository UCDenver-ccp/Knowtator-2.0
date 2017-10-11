package edu.ucdenver.ccp.knowtator.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextSpan;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static edu.ucdenver.ccp.knowtator.xml.XmlTags.TAG_SPAN;

public final class XmlUtil {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);

    public XmlWriter xmlWriter;
    public XmlReader xmlReader;

    public XmlUtil(KnowtatorView view) {
        xmlReader = new XmlReader(view.getTextAnnotationManager(), view.getDataExtractor());
        xmlWriter = new XmlWriter(view.getTextAnnotationManager());
    }

    public void read(InputStream is) throws IOException, SAXException, ParserConfigurationException {
        xmlReader.read(is);
    }

    public void write(FileWriter fw) throws IOException, NoSuchFieldException {
        xmlWriter.write(fw);
    }

    public static List<Node> asList(NodeList n) {
        return n.getLength()==0 ? Collections.emptyList(): new NodeListWrapper(n);
    }

    static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
        public final NodeList list;

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


    public static String getMentionSourceFromXML(String fullMention) {
        String mentionSource;
        if(fullMention.indexOf("_new_Instance") < fullMention.indexOf("Instance") && fullMention.contains("_new_Instance")) {
            mentionSource = fullMention.substring(0, fullMention.indexOf("_new_Instance"));
        } else {
            mentionSource = fullMention.substring(0, fullMention.indexOf("_Instance"));
        }
        return mentionSource;
    }

    public static Integer getMentionIDFromXML(String fullMention) {
        return Integer.parseInt(fullMention.substring(fullMention.indexOf("_Instance_")+10));
    }

    public static ArrayList<TextSpan> getSpanInfo(Element annotationElement) {
        ArrayList<TextSpan> textSpans = new ArrayList<>();
        for (Node spanNode : XmlUtil.asList(annotationElement.getElementsByTagName(TAG_SPAN))) {
            if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
                Element spanElement = (Element) spanNode;
                Integer spanStart = Integer.parseInt(spanElement.getAttribute(XmlTags.TAG_SPAN_START));
                Integer spanEnd = Integer.parseInt(spanElement.getAttribute(XmlTags.TAG_SPAN_END));

                TextSpan newTextSpan = new TextSpan(spanStart, spanEnd);
                textSpans.add(newTextSpan);
            }
        }
        log.warn(String.format("\t\tSpans: %d", textSpans.size()));
        return textSpans;
    }
}
