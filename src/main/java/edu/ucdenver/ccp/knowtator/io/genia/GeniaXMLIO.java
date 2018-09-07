package edu.ucdenver.ccp.knowtator.io.genia;

import edu.ucdenver.ccp.knowtator.io.BasicIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

public interface GeniaXMLIO extends Serializable, BasicIO {
    void readFromGeniaXML(Element parent, String content);

    void writeToGeniaXML(Document dom, Element parent);
}
