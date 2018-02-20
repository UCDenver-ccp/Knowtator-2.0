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

package edu.ucdenver.ccp.knowtator.model.io;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.io.forOld.OldXmlReader;
import edu.ucdenver.ccp.knowtator.model.io.forOld.OldXmlTags;
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
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public final class XmlUtil {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    private KnowtatorManager manager;

    public XmlUtil(KnowtatorManager manager) {
        this.manager = manager;
    }

    public static List<Node> asList(NodeList n) {
        return n.getLength() == 0 ? Collections.emptyList() : new NodeListWrapper(n);
    }

    public void read(File file) {
        try {
            /*
            doc parses the XML into a graph
             */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputStream is = new FileInputStream(file);

            Document doc;
            try {
                log.warn("****************Reading from " + file.getAbsolutePath());
                doc = db.parse(is);
                doc.getDocumentElement().normalize();

                List<Node> knowtatorNodes = XmlUtil.asList(doc.getElementsByTagName(XmlTags.KNOWTATOR_PROJECT));
                if (knowtatorNodes.size() > 0) {
                    Element knowtatorElement = (Element) knowtatorNodes.get(0);
                    manager.readFromXml(knowtatorElement);
                }

                List<Node> annotationNodes = XmlUtil.asList(doc.getElementsByTagName(OldXmlTags.ANNOTATIONS));
                if (annotationNodes.size() > 0) {
                    log.warn("Reading in old Knowtator project");
                    OldXmlReader.readAnnotations(manager, annotationNodes);
                }

                log.warn("***************************");
            } catch (IllegalArgumentException | IOException | SAXException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void write(File file) {
        Document dom;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            log.warn("********************Writing to " + file.getAbsolutePath());
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element root = dom.createElement(XmlTags.KNOWTATOR_PROJECT);
            dom.appendChild(root);
            manager.writeToXml(dom, root);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to test_project
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(file)));

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }

        log.warn("*************************");

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


}
