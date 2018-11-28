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

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.AbstractKnowtatorDataObject;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollectionListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TextSource extends AbstractKnowtatorDataObject<TextSource> implements KnowtatorDataObjectInterface<TextSource>, BratStandoffIO, Savable, KnowtatorXMLIO, KnowtatorCollectionListener, DataObjectModificationListener {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);

    private final KnowtatorModel controller;
    private final File saveFile;
    private final ConceptAnnotationCollection conceptAnnotationCollection;
    private File textFile;
    private String content;
    private final GraphSpaceCollection graphSpaceCollection;
    private boolean notSaving;

    public TextSource(KnowtatorModel controller, File saveFile, String textFileName) {
        super(null);
        this.controller = controller;
        this.saveFile = saveFile == null ? new File(controller.getAnnotationsLocation().getAbsolutePath(), textFileName.replace(".txt", "") + ".xml") : saveFile;
        this.conceptAnnotationCollection = new ConceptAnnotationCollection(controller, this);
        this.graphSpaceCollection = new GraphSpaceCollection(controller, this);
        notSaving = true;

        //noinspection unchecked
        conceptAnnotationCollection.addCollectionListener(this);
        //noinspection unchecked
        graphSpaceCollection.addCollectionListener(this);

        controller.verifyId(FilenameUtils.getBaseName(textFileName), this, true);

        textFile =
                new File(
                        controller.getArticlesLocation(),
                        textFileName.endsWith(".txt") ? textFileName : textFileName + ".txt");

        if (!textFile.exists()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(String.format("Could not find file for %s. Choose file location", id));

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    textFile =
                            Files.copy(
                                    Paths.get(file.toURI()),
                                    Paths.get(
                                            controller
                                                    .getArticlesLocation()
                                                    .toURI()
                                                    .resolve(file.getName())))
                                    .toFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public int compareTo(TextSource textSource2) {
        if (this == textSource2) {
            return 0;
        }
        if (textSource2 == null) {
            return 1;
        }

        int result = KnowtatorDataObjectInterface.extractInt(this.getId()) - KnowtatorDataObjectInterface.extractInt(textSource2.getId());
        if (result == 0) {
            return id.toLowerCase().compareTo(textSource2.getId().toLowerCase());
        } else {
            return result;
        }
    }

    File getTextFile() {
        return textFile;
    }

    public ConceptAnnotationCollection getConceptAnnotationCollection() {
        return conceptAnnotationCollection;
    }

    @Override
    public void dispose() {
        conceptAnnotationCollection.dispose();
        graphSpaceCollection.dispose();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        conceptAnnotationCollection.readFromKnowtatorXML(null, parent);
        graphSpaceCollection.readFromKnowtatorXML(null, parent);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
        parent.appendChild(textSourceElement);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.ID, id);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.FILE, textFile.getName());
        conceptAnnotationCollection.writeToKnowtatorXML(dom, textSourceElement);
        graphSpaceCollection.writeToKnowtatorXML(dom, textSourceElement);
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
        conceptAnnotationCollection.readFromOldKnowtatorXML(null, parent);
        graphSpaceCollection.readFromOldKnowtatorXML(null, parent);
    }

    @Override
    public void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationMap, String content) {
        conceptAnnotationCollection.readFromBratStandoff(null, annotationMap, getContent());
        graphSpaceCollection.readFromBratStandoff(null, annotationMap, getContent());
    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
        conceptAnnotationCollection.writeToBratStandoff(writer, annotationsConfig, visualConfig);
        graphSpaceCollection.writeToBratStandoff(writer, annotationsConfig, visualConfig);
    }

    public String getContent() {
        if (content == null) {
            while (true) {
                try {
                    content = FileUtils.readFileToString(textFile, "UTF-8");
                    return content;
                } catch (IOException e) {
                    textFile = new File(controller.getArticlesLocation(), id + ".txt");
                    while (!textFile.exists()) {
                        JFileChooser fileChooser = new JFileChooser();
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            textFile = fileChooser.getSelectedFile();
                        }
                    }
                }
            }
        } else {
            return content;
        }
    }

    public GraphSpaceCollection getGraphSpaceCollection() {
        return graphSpaceCollection;
    }

    @Override
    public void save() {
        if (controller.isNotLoading() && !controller.renderChangeInProgress() && notSaving) {
            notSaving = false;
            controller.setRenderRDFSLabel();
            controller.saveToFormat(KnowtatorXMLUtil.class, this, saveFile);
            controller.resetRenderRDFS();
            notSaving = true;
        }
    }

    @Override
    public void load() {

    }

    @Override
    public File getSaveLocation() {
        return new File(controller.getAnnotationsLocation().getAbsolutePath(), saveFile.getName());
    }

    @Override
    public void setSaveLocation(File saveLocation) {

    }

    @Override
    public void added() {
        save();
    }

    @Override
    public void removed() {
        save();
    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }

    @Override
    public void selected(SelectionEvent event) {

    }

    @Override
    public void modification() {
        save();
    }

    public Optional<ConceptAnnotation> getSelectedAnnotation() {
        return conceptAnnotationCollection.getSelection();
    }

    public SpanCollection getSpans(Integer loc) {
        return conceptAnnotationCollection.getSpans(loc);
    }

    public void selectNextSpan() {
        conceptAnnotationCollection.selectNextSpan();
    }

    public void selectPreviousSpan() {
        conceptAnnotationCollection.selectPreviousSpan();
    }

    public void setSelectedAnnotation(Span span) {
        conceptAnnotationCollection.setSelectedAnnotation(span);
    }

    public void addCollectionListener(ConceptAnnotationCollectionListener listener) {
        conceptAnnotationCollection.addCollectionListener(listener);
    }

    public void removeCollectionListener(ConceptAnnotationCollectionListener listener) {
        conceptAnnotationCollection.removeCollectionListener(listener);
    }

    public Optional<ConceptAnnotation> getAnnotation(String annotationID) {
        return conceptAnnotationCollection.get(annotationID);
    }

    public void setSelection(ConceptAnnotation conceptAnnotation) {
        conceptAnnotationCollection.setSelection(conceptAnnotation);
    }

    public void addCollectionListener(GraphSpaceCollectionListener listener) {
        graphSpaceCollection.addCollectionListener(listener);
    }

    public void add(GraphSpace graphSpace) {
        graphSpaceCollection.add(graphSpace);
    }

    public void selectPreviousGraphSpace() {
        graphSpaceCollection.selectPrevious();
    }

    public void selectNextGraphSpace() {
        graphSpaceCollection.selectNext();
    }

    public Optional<GraphSpace> getSelectedGraphSpace() {
        return graphSpaceCollection.getSelection();
    }

    public boolean containsID(String id) {
        return graphSpaceCollection.containsID(id);
    }

    public void removeCollectionListener(GraphSpaceCollectionListener listener) {
        graphSpaceCollection.removeCollectionListener(listener);
    }

    public ConceptAnnotation firstConceptAnnotation() {
        return conceptAnnotationCollection.first();
    }

    public int getNumberOfGraphSpaces() {
        return graphSpaceCollection.size();
    }
}
