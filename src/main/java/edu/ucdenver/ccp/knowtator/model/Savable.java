package edu.ucdenver.ccp.knowtator.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface Savable {
    void writeToKnowtatorXML(Document dom, Element parent);

    void readFromKnowtatorXML(File file, Element parent, String content);

    void readFromOldKnowtatorXML(File file, Element parent, String content);

    void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content);

    void writeToBratStandoff(Writer writer) throws IOException;

    void readFromGeniaXML(Element parent, String content);

    void writeToGeniaXML(Document dom, Element parent);
}
