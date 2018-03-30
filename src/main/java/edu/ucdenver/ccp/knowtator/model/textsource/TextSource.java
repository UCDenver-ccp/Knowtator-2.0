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

package edu.ucdenver.ccp.knowtator.model.textsource;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class TextSource implements Savable {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);


    private AnnotationManager annotationManager;
    private String docID;
    private File file;
//    private String content;

    public TextSource(KnowtatorManager manager, String docID) {
        this.annotationManager = new AnnotationManager(manager, this);

        if (docID != null) {
            this.docID = docID;
            file = new File(manager.getProjectManager().getArticlesLocation(), docID + ".txt");
            while (!file.exists()) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();

                }
            }

//            try {
//                content = FileUtils.readFileToString(file, "UTF-8");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        else {
//            this.docID = "Instructions";
//            content = "***Instructions:***" +
//                    "\n" +
//                    "Create a new project: Project -> New Project" +
//                    "\n" +
//                    "Load an existing project: Project -> Load Project";
//        }

    }

    public String getDocID() {
        return docID;
    }

    public File getFile() {
        return file;
    }

    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }
    @Override
    public String toString() {
        return String.format("TextSource: docID: %s", docID);
    }
    @Override
    public void writeToKnowtatorXml(Document dom, Element parent) {
        Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
        parent.appendChild(textSourceElement);
        textSourceElement.setAttribute(KnowtatorXMLTags.ID, docID);
        annotationManager.writeToKnowtatorXml(dom, textSourceElement);
    }

    @Override
    public void readFromKnowtatorXml(Element parent, String content) {
        try {
            content = FileUtils.readFileToString(file, "UTF-8");
            annotationManager.readFromKnowtatorXml(parent, content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void readFromOldKnowtatorXml(Element parent) {
        annotationManager.readFromOldKnowtatorXml(parent);
    }

    @Override
    public void readFromBratStandoff(Map<Character, List<String[]>> annotationMap, String content) {
        try {
            content = FileUtils.readFileToString(file, "UTF-8");
            annotationManager.readFromBratStandoff(annotationMap, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {
        annotationManager.writeToBratStandoff(writer);
    }

    @Override
    public void readFromGeniaXml(Element parent, String content) {

    }
}
