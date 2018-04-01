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
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class UIMAXMIUtil extends XMLUtil implements BasicIOUtil {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(UIMAXMIUtil.class);
    private static final URL ANNOTATOR_DESCRIPTOR = UIMAXMIUtil.class.getResource("/KnowtatorToUIMAAnnotatorDescriptor.xml");
//private static final File ANNOTATOR_DESCRIPTOR = new File("E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml");

    @Override
    public void read(Savable savable, File file) {

    }

    @Override
    public void write(Savable savable, File file) {

        try {
            XMLInputSource input = new XMLInputSource(ANNOTATOR_DESCRIPTOR);
            AnalysisEngineDescription description = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input);
            AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(description);

            if (savable instanceof TextSourceManager) {
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
            } else if (savable instanceof TextSource){
                CAS cas;
                try {
                    cas = analysisEngine.newCAS();
                    savable.convertToUIMA(cas);
                    CasIOUtils.save(cas, new FileOutputStream(file), SerialFormat.XMI);
                } catch (ResourceInitializationException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | InvalidXMLException | ResourceInitializationException e) {
            e.printStackTrace();
        }
    }
}
