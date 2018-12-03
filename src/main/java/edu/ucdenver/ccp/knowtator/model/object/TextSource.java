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

package edu.ucdenver.ccp.knowtator.model.object;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;
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

public class TextSource implements ModelObject<TextSource>, BratStandoffIO, Savable, KnowtatorXMLIO, ModelListener {
	@SuppressWarnings("unused")
	private static Logger log = LogManager.getLogger(TextSource.class);

	private final BaseModel model;
	private final File saveFile;
	private final ConceptAnnotationCollection conceptAnnotationCollection;
	private File textFile;
	private String content;
	private final GraphSpaceCollection graphSpaceCollection;
	private String id;

	public TextSource(BaseModel model, File saveFile, String textFileName) {
		this.model = model;
		this.saveFile = saveFile == null ? new File(model.getAnnotationsLocation().getAbsolutePath(), String.format("%s.xml", textFileName.replace(".txt", ""))) : saveFile;
		this.conceptAnnotationCollection = new ConceptAnnotationCollection(model, this);
		this.graphSpaceCollection = new GraphSpaceCollection(model, this);
		model.addModelListener(this);

		model.verifyId(FilenameUtils.getBaseName(textFileName), this, true);

		textFile =
				new File(
						model.getArticlesLocation(),
						textFileName.endsWith(".txt") ? textFileName : String.format("%s.txt", textFileName));

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
											model
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

	public File getTextFile() {
		return textFile;
	}

	public ConceptAnnotationCollection getConceptAnnotations() {
		return conceptAnnotationCollection;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
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
		model.removeModelListener(this);
		conceptAnnotationCollection.readFromKnowtatorXML(null, parent);
		graphSpaceCollection.readFromKnowtatorXML(null, parent);
		model.addModelListener(this);
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
					textFile = new File(model.getArticlesLocation(), String.format("%s.txt", id));
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

	public GraphSpaceCollection getGraphSpaces() {
		return graphSpaceCollection;
	}

	@Override
	public void save() {
		model.saveToFormat(KnowtatorXMLUtil.class, this, getSaveLocation());
	}

	@Override
	public void load() {

	}

	public File getSaveLocation() {
		return new File(model.getAnnotationsLocation().getAbsolutePath(), saveFile.getName());
	}

	@Override
	public void filterChangedEvent() {

	}

	@Override
	public void colorChangedEvent() {

	}

	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		event.getNew()
				.filter(modelObject -> !(event instanceof SelectionEvent))
				.filter(modelObject -> modelObject instanceof TextBoundModelObject)
				.map(modelObject -> (TextBoundModelObject) modelObject)
				.filter(textBoundModelObject -> textBoundModelObject.getTextSource().equals(this))
				.ifPresent(modelObject -> save());
		event.getOld()
				.filter(modelObject -> !(event instanceof SelectionEvent))
				.filter(modelObject -> modelObject instanceof TextBoundModelObject)
				.map(modelObject -> (TextBoundModelObject) modelObject)
				.filter(textBoundModelObject -> textBoundModelObject.getTextSource().equals(this))
				.ifPresent(modelObject -> save());
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

	Optional<ConceptAnnotation> getAnnotation(String annotationID) {
		return conceptAnnotationCollection.get(annotationID);
	}

	public void setSelectedConceptAnnotation(ConceptAnnotation conceptAnnotation) {
		conceptAnnotationCollection.setSelection(conceptAnnotation);
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

	public Optional<ConceptAnnotation> firstConceptAnnotation() {
		return conceptAnnotationCollection.first();
	}

	public int getNumberOfGraphSpaces() {
		return graphSpaceCollection.size();
	}

	public int getNumberOfConceptAnnotations() {
		return conceptAnnotationCollection.size();
	}

	public void setSelectedGraphSpace(GraphSpace graphSpace) {
		graphSpaceCollection.setSelection(graphSpace);
	}

	public void selectNextConceptAnnotation() {
		conceptAnnotationCollection.selectNext();
	}
}
