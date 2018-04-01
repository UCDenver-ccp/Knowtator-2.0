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

package edu.ucdenver.ccp.knowtator.model.io.uima;

import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.io.XMLUtil;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSourceManager;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.List;

public class UIMAXMIUtil extends XMLUtil implements BasicIOUtil {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(UIMAXMIUtil.class);


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

                List<Node> uimaNodes = asList(doc.getElementsByTagName(UIMAXMITags.XMI_XMI));
                if (uimaNodes.size() > 0) {
                    Element uimaElement = (Element) uimaNodes.get(0);
                    savable.readFromKnowtatorXML(uimaElement, null);
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
            try {
                XMLInputSource input = new XMLInputSource("E:/Documents/RoomNumberAnnotator/desc/KnowtatorAnnotatorDescriptor.xml");
                AnalysisEngineDescription description = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input);
                AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(description);

                ((TextSourceManager) savable).getTextSources().values().forEach(textSource -> {
                    CAS cas;
                    try {
                        cas = analysisEngine.newCAS();
                        textSource.convertToUIMA(cas);
                        CasIOUtils.save(cas, new FileOutputStream(new File(file.getAbsolutePath() + File.separator + textSource.getDocID() + ".xmi")), SerialFormat.XMI);
                    } catch (ResourceInitializationException | IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException | InvalidXMLException | ResourceInitializationException e) {
                e.printStackTrace();
            }
        }
    }
}
