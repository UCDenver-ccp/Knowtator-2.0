/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.BaseKnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TextSourceCollection extends KnowtatorCollection<TextSource> implements BratStandoffIO, KnowtatorXMLIO, BaseKnowtatorManager {
    @SuppressWarnings("unused")
    private final Logger log = Logger.getLogger(TextSourceCollection.class);

    private final KnowtatorController controller;
    private File articlesLocation;
    private File annotationsLocation;


    public TextSourceCollection(KnowtatorController controller) {
        super(controller);
        this.controller = controller;

    }

    @Override
    public void add(TextSource textSource) {
        if (get(textSource.getId()) == null) {
            if (textSource.getTextFile().exists()) {
                super.add(textSource);
            }
        }
    }


    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        forEach(textSource -> textSource.writeToKnowtatorXML(dom, parent));
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        for (Node documentNode :
                KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            String textSourceId = documentElement.getAttribute(KnowtatorXMLAttributes.ID);
            String textFileName = documentElement.getAttribute(KnowtatorXMLAttributes.FILE);
            if (textFileName == null || textFileName.equals("")) {
                textFileName = textSourceId;
            }
            TextSource newTextSource = new TextSource(controller, file, textFileName);
            add(newTextSource);
            newTextSource.readFromKnowtatorXML(null, documentElement);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {

        String textSourceId = parent.getAttribute(OldKnowtatorXMLAttributes.TEXT_SOURCE).replace(".txt", "");
        TextSource newTextSource = new TextSource(controller, file, textSourceId);
        add(newTextSource);
        get(newTextSource.getId()).readFromOldKnowtatorXML(null, parent);
    }

    @Override
    public void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationMap, String content) {
        String textSourceId = annotationMap.get(StandoffTags.DOCID).get(0)[0];

        TextSource newTextSource = new TextSource(controller, file, textSourceId);
        add(newTextSource);
        newTextSource.readFromBratStandoff(null, annotationMap, null);
    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) {
    }

    public void addDocument(File file) {

        TextSource newTextSource = new TextSource(controller, null, file.getName());
        add(newTextSource);
    }

    @Override
    public void load() {
        try {
            log.warn("Loading annotations");
            controller.getOWLModel().setRenderRDFSLabel();
            KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
            Files.newDirectoryStream(Paths.get(annotationsLocation.toURI()), path -> path.toString().endsWith(".xml"))
                    .forEach(inputFile -> xmlUtil.read(this, inputFile.toFile()));
            controller.getOWLModel().resetRenderRDFS();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (size() == 0) {
//            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setCurrentDirectory(getArticlesLocation());
//
//            JOptionPane.showMessageDialog(null, "Please select a document to annotate");
//            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//                File file = fileChooser.getSelectedFile();
//                if (!file.getParentFile().equals(controller.getTextSourceCollection().getArticlesLocation())) {
//                    try {
//                        FileUtils.copyFile(file, new File(controller.getTextSourceCollection().getArticlesLocation(), file.getName()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                add(new TextSource(controller, null, file.getName()));
//            }
//        }
    }

    @Override
    public void save() {
        controller.getOWLModel().setRenderRDFSLabel();
        forEach(TextSource::save);
        controller.getOWLModel().resetRenderRDFS();
    }

    public File getAnnotationsLocation() {
        return annotationsLocation;
    }

    public File getArticlesLocation() {
        return articlesLocation;
    }

    @Override
    public void setSaveLocation(File newSaveLocation) throws IOException {
        articlesLocation = new File(newSaveLocation, "Articles");
        Files.createDirectories(articlesLocation.toPath());
        annotationsLocation = new File(newSaveLocation, "Annotations");
        Files.createDirectories(annotationsLocation.toPath());
    }

    @Override
    public void finishLoad() {
        if (size() > 0) {
            setSelection(first());
        }
    }

    @Override
    public File getSaveLocation() {
        return annotationsLocation;

    }

    @Override
    public void remove(TextSource textSource) {
        try {
            if (textSource == getSelection()) {
                selectPrevious();
            }
        } catch (NoSelectionException ignored) {
        }
        super.remove(textSource);
    }

    @Override
    public void reset() {

    }
}
