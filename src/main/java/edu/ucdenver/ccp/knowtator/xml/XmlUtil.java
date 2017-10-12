package edu.ucdenver.ccp.knowtator.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public final class XmlUtil {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);
    KnowtatorView view;

    public XmlUtil(KnowtatorView view) {
        this.view = view;
    }

    public void read(InputStream is) throws IOException, SAXException, ParserConfigurationException {
        XmlReader.read(is, view);
    }

    public void write(FileWriter fw) throws IOException, NoSuchFieldException {
        XmlWriter.write(fw, view.getTextAnnotationManager());
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
}
