/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.io.knowtator;

import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.io.BasicIOUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.util.*;

public final class KnowtatorXMLUtil implements BasicIOUtil {
    private static final Logger log = Logger.getLogger(KnowtatorXMLUtil.class);


    public static List<Node> asList(NodeList n) {
        return n.getLength() == 0 ? Collections.emptyList() : new NodeListWrapper(n);
    }

    @Override
    public void read(Savable savable, File file) {
        try {
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

                List<Node> knowtatorNodes = KnowtatorXMLUtil.asList(doc.getElementsByTagName(KnowtatorXMLTags.KNOWTATOR_PROJECT));
                if (knowtatorNodes.size() > 0) {
                    Element knowtatorElement = (Element) knowtatorNodes.get(0);
                    savable.readFromKnowtatorXml(knowtatorElement, "");
                }

                List<Node> annotationNodes = KnowtatorXMLUtil.asList(doc.getElementsByTagName(OldXmlTags.ANNOTATIONS));
                if (annotationNodes.size() > 0) {
                    savable.readFromOldKnowtatorXml(doc.getDocumentElement());
                }
            } catch (IllegalArgumentException | IOException | SAXException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Savable savable, File file) {
        Document dom;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            log.warn("Writing to " + file.getAbsolutePath());
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element root = dom.createElement(KnowtatorXMLTags.KNOWTATOR_PROJECT);
            dom.appendChild(root);
            savable.writeToKnowtatorXml(dom, root);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "knowtator");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://knowtator.apache.org/xslt}indent-amount", "4");

                // send DOM to test_project
                PrintWriter pw = new PrintWriter(file);
                pw.close();
                OutputStream os = new FileOutputStream(file, false);
                tr.transform(new DOMSource(dom),
                        new StreamResult(os));
                os.close();

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }

    }

    static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
        final NodeList list;

        NodeListWrapper(NodeList l) {
            list = l;
        }

        public Node get(int index) {
            return list.item(index);
        }

        public int size() {
            return list.getLength();
        }
    }

    public static List<Span> getSpanInfo(Element annotationElement) {
        List<Span> spans = new ArrayList<>();

        Element spanElement;
        int spanStart;
        int spanEnd;
        String spannedText;
        for (Node spanNode : KnowtatorXMLUtil.asList(annotationElement.getElementsByTagName(OldXmlTags.SPAN))) {
            if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
                spanElement = (Element) spanNode;
                spanStart = Integer.parseInt(spanElement.getAttribute(OldXmlTags.SPAN_START));
                spanEnd = Integer.parseInt(spanElement.getAttribute(OldXmlTags.SPAN_END));
                spannedText = spanElement.getTextContent();

                spans.add(new Span(spanStart, spanEnd, spannedText));
            }
        }
        return spans;
    }

    public static HashMap<String, Element> getClassIDsFromXml(Element textSourceElement) {
        /*
        Next parse classes and add the annotations
         */
        HashMap<String, Element> mentionTracker = new HashMap<>();

        for (Node classNode : KnowtatorXMLUtil.asList(textSourceElement.getElementsByTagName(OldXmlTags.CLASS_MENTION))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String annotationID = classElement.getAttribute(OldXmlTags.ID);
                mentionTracker.put(annotationID, classElement);
            }
        }

        return mentionTracker;
    }


}
