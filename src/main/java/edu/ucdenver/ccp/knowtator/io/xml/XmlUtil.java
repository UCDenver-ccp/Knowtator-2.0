package edu.ucdenver.ccp.knowtator.io.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public final class XmlUtil {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    KnowtatorManager manager;

    public XmlUtil(KnowtatorManager manager) {
        this.manager = manager;
    }

    //TODO add command to read in profile from xml
    public void read(String fileName, Boolean fromResources) {
        try {
            XmlReader.read(fileName, manager, fromResources);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    //TODO Save profiles to xml
    public void write(String fileName) {
        try {
            XmlWriter.write(fileName, manager.getAnnotationManager());
        } catch (IOException | NoSuchFieldException e) {
            e.printStackTrace();
        }
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
