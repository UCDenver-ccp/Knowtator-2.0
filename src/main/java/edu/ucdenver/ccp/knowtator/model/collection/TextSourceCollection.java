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

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextBoundModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class TextSourceCollection extends KnowtatorCollection<TextSource> implements BratStandoffIO, KnowtatorXMLIO, ModelListener {
	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(TextSourceCollection.class);

	private final KnowtatorModel model;


	public TextSourceCollection(KnowtatorModel model) {
		super(model);
		this.model = model;
		model.addModelListener(this);

	}

	@Override
	public void add(TextSource textSource) {
		if (!get(textSource.getId()).isPresent()) {
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
			TextSource newTextSource = new TextSource(model, file, textFileName);
			add(newTextSource);
			newTextSource.readFromKnowtatorXML(null, documentElement);
		}
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {

		String textSourceId = parent.getAttribute(OldKnowtatorXMLAttributes.TEXT_SOURCE).replace(".txt", "");
		TextSource newTextSource = new TextSource(model, file, textSourceId);
		add(newTextSource);
		get(newTextSource.getId())
				.ifPresent(textSource -> textSource.readFromOldKnowtatorXML(null, parent));
	}

	@Override
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationMap, String content) {
		String textSourceId = annotationMap.get(StandoffTags.DOCID).get(0)[0];

		TextSource newTextSource = new TextSource(model, file, textSourceId);
		add(newTextSource);
		newTextSource.readFromBratStandoff(null, annotationMap, null);
	}

	@Override
	public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) {
	}

	public void load() throws IOException {
		log.info("Loading annotations");
		KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
		Files.list(model.getAnnotationsLocation().toPath())
				.filter(path -> path.toString().endsWith(".xml"))
				.forEach(inputFile -> xmlUtil.read(this, inputFile.toFile()));

	}

	@Override
	public void setSelection(TextSource textSource) {
		super.setSelection(textSource);
	}

	@Override
	public void remove(TextSource textSource) {
		getSelection()
				.filter(textSource1 -> textSource1.equals(textSource))
				.ifPresent(textSource1 -> selectPrevious());
		super.remove(textSource);
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
		.filter(modelObject -> modelObject instanceof TextBoundModelObject)
		.ifPresent(modelObject -> setSelection(((TextBoundModelObject) modelObject).getTextSource()));
	}
}
