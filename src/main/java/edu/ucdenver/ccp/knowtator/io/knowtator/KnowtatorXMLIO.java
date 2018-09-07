package edu.ucdenver.ccp.knowtator.io.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.Serializable;

 public interface KnowtatorXMLIO extends BasicIO, Serializable {

     void writeToKnowtatorXML(Document dom, Element parent);

     void readFromKnowtatorXML(File file, Element parent);

     void readFromOldKnowtatorXML(File file, Element parent);
}
