package edu.ucdenver.ccp.knowtator.io.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class KnowtatorXMLUtil extends OldKnowtatorUtil implements BasicIOUtil<KnowtatorXMLIO> {
  private static final Logger log = Logger.getLogger(KnowtatorXMLUtil.class);

  @Override
  public void read(KnowtatorXMLIO reader, File file) {
    try {
      log.warn("Reading from " + file);

      /*
      doc parses the XML into a graph
       */
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();

      InputStream is = new FileInputStream(file);

      Document doc;
      try {
        doc = db.parse(is);
        doc.getDocumentElement().normalize();

        List<Node> knowtatorNodes =
                asList(doc.getElementsByTagName(KnowtatorXMLTags.KNOWTATOR_PROJECT));
        if (knowtatorNodes.size() > 0) {
          Element knowtatorElement = (Element) knowtatorNodes.get(0);
          reader.readFromKnowtatorXML(file, knowtatorElement);
        }

        List<Node> annotationNodes =
                asList(doc.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATIONS));
        if (annotationNodes.size() > 0) {
          reader.readFromOldKnowtatorXML(file, doc.getDocumentElement());
        }
      } catch (IllegalArgumentException | IOException | SAXException e) {
        e.printStackTrace();
      }
    } catch (ParserConfigurationException | FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void write(KnowtatorXMLIO writer, File file) {
    Document dom;

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    log.warn("Writing to " + file.getAbsolutePath());
    try {

      DocumentBuilder db = dbf.newDocumentBuilder();
      dom = db.newDocument();

      try {

        Element root = dom.createElement(KnowtatorXMLTags.KNOWTATOR_PROJECT);
        dom.appendChild(root);
        writer.writeToKnowtatorXML(dom, root);

        finishWritingXML(dom, file);
      } catch (NullPointerException npe) {
        finishWritingXML(dom, file);
      }
    } catch (ParserConfigurationException e1) {
      e1.printStackTrace();
    }
  }

  private static void finishWritingXML(Document dom, File file) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", 2);
      Transformer tr = transformerFactory.newTransformer();
      tr.setOutputProperty(OutputKeys.INDENT, "yes");
      tr.setOutputProperty(OutputKeys.METHOD, "xml");
      tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tr.setOutputProperty("{http://knowtator.apache.org/xslt}indent-amount", "4");

      // send DOM to test_project
      PrintWriter pw = new PrintWriter(file);
      pw.close();
      OutputStream os = new FileOutputStream(file, false);
      tr.transform(new DOMSource(dom), new StreamResult(new OutputStreamWriter(os, StandardCharsets.UTF_8)));
      os.close();

    } catch (TransformerException | IOException te) {
      System.out.println(te.getMessage());
    }
  }


}
