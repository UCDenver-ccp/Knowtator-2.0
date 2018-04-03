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
import edu.ucdenver.ccp.knowtator.model.io.XMLUtil;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSourceManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class KnowtatorXMLUtil extends XMLUtil implements BasicIOUtil {
    private static final Logger log = Logger.getLogger(KnowtatorXMLUtil.class);




    @Override
    public void read(Savable savable, File file) throws IOException {
        if (file.isDirectory()) {
            Files.newDirectoryStream(Paths.get(file.toURI()),
                    path -> path.toString().endsWith(".xml"))
                    .forEach(inputFile -> readFromInputFile(savable, inputFile.toFile()));
        } else {
            readFromInputFile(savable, file);
        }
    }

    private void readFromInputFile(Savable savable, File file) {
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

                List<Node> knowtatorNodes = asList(doc.getElementsByTagName(KnowtatorXMLTags.KNOWTATOR_PROJECT));
                if (knowtatorNodes.size() > 0) {
                    Element knowtatorElement = (Element) knowtatorNodes.get(0);
                    savable.readFromKnowtatorXML(knowtatorElement, null);
                }

                List<Node> annotationNodes = asList(doc.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATIONS));
                if (annotationNodes.size() > 0) {
                    savable.readFromOldKnowtatorXML(doc.getDocumentElement());
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
        if (savable instanceof TextSourceManager) {
            ((TextSourceManager) savable).getTextSources().values().forEach(textSource -> {
                File outputFile = new File(file.getAbsolutePath() + File.separator + textSource.getDocID() + ".xml");
                writeToOutputFile(savable, outputFile);
            });
        } else if(savable instanceof ProfileManager){
            ((ProfileManager) savable).getProfiles().values().forEach(profile -> {
                File outputFile = new File(file.getAbsolutePath() + File.separator + profile.getId() + ".xml");
                writeToOutputFile(savable, outputFile);
            });
        } else {
            writeToOutputFile(savable, file);
        }

    }

    private void writeToOutputFile(Savable savable, File outputFile) {
        Document dom;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            log.warn("Writing to " + outputFile.getAbsolutePath());
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element root = dom.createElement(KnowtatorXMLTags.KNOWTATOR_PROJECT);
            dom.appendChild(root);
            savable.writeToKnowtatorXML(dom, root);

            finishWritingXML(dom, outputFile);
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }
    }


    public static List<Span> getSpanInfo(Element annotationElement) {
        List<Span> spans = new ArrayList<>();

        Element spanElement;
        int spanStart;
        int spanEnd;
        String spannedText;
        for (Node spanNode : KnowtatorXMLUtil.asList(annotationElement.getElementsByTagName(OldKnowtatorXMLTags.SPAN))) {
            if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
                spanElement = (Element) spanNode;
                spanStart = Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_START));
                spanEnd = Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_END));
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

        for (Node classNode : KnowtatorXMLUtil.asList(textSourceElement.getElementsByTagName(OldKnowtatorXMLTags.CLASS_MENTION))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String annotationID = classElement.getAttribute(OldKnowtatorXMLAttributes.ID);
                mentionTracker.put(annotationID, classElement);
            }
        }

        return mentionTracker;
    }


}
