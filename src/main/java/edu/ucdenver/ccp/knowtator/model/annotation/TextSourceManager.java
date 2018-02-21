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
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class TextSourceManager implements Savable {
    private Map<String, TextSource> textSources;

    private KnowtatorManager manager;
    private Logger log = Logger.getLogger(TextSourceManager.class);

    public TextSourceManager(KnowtatorManager manager) {
        this.manager = manager;
        textSources = new HashMap<>();
    }

    public TextSource addTextSource(String fileLocation) {
        textSources.values().forEach(t -> log.warn(t));
        String docID = FilenameUtils.getBaseName(fileLocation);
        TextSource newTextSource = textSources.get(docID);
        if (newTextSource == null) {
            newTextSource = new TextSource(manager, docID);
            textSources.put(docID, newTextSource);
            manager.textSourceAddedEvent(newTextSource);
        } else {
            log.warn(docID + " is not null");
        }
        return newTextSource;
    }

    public Map<String, TextSource> getTextSources() {
        return textSources;
    }

    public void remove(TextSource textSource) {
        textSources.remove(textSource.getDocID());
    }

    @Override
    public void writeToXml(Document dom, Element parent) {
        textSources.values().forEach(textSource -> textSource.writeToXml(dom, parent));

    }

    @Override
    public void readFromXml(Element parent) {
        for (Node documentNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            String documentID = documentElement.getAttribute(XmlTags.ID);
            TextSource newTextSource = addTextSource(documentID);
            newTextSource.readFromXml(documentElement);
        }
    }

    public KnowtatorManager getManager() {
        return manager;
    }
}
