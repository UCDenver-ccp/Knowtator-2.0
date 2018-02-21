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

package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.xml.XmlTags;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class TextSource implements Savable {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);


    private AnnotationManager annotationManager;
    private String docID;
    private String content;

    TextSource(KnowtatorManager manager, String docID) {
        this.docID = docID;
        this.annotationManager = new AnnotationManager(manager, this);

        File file = new File(manager.getProjectManager().getArticlesLocation(), docID + ".txt");
        while (!file.exists()) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();

            }
        }

        try {
            content = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getDocID() {
        return docID;
    }

    public String getContent() {
        return content;
    }

    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }
    @Override
    public String toString() {
        return String.format("TextSource: docID: %s content: %s", docID, content);
    }
    @Override
    public void writeToXml(Document dom, Element parent) {
        Element textSourceElement = dom.createElement(XmlTags.DOCUMENT);
        parent.appendChild(textSourceElement);
        textSourceElement.setAttribute(XmlTags.ID, docID);
        annotationManager.writeToXml(dom, textSourceElement);
    }

    @Override
    public void readFromXml(Element parent) {
        annotationManager.readFromXml(parent);
    }
}
