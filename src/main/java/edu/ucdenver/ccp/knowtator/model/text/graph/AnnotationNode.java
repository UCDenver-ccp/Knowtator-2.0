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

package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.text.DataObjectModificationListener;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.List;

public class AnnotationNode extends mxCell implements KnowtatorTextBoundDataObjectInterface, KnowtatorXMLIO {

	private final ConceptAnnotation conceptAnnotation;
	private final TextSource textSource;

	AnnotationNode(KnowtatorController controller, String id, ConceptAnnotation conceptAnnotation, TextSource textSource, double x, double y) {
		super(
				conceptAnnotation.getSpannedText(),
				new mxGeometry(x, y, 150, 150),
				"fontSize=16;fontColor=black;strokeColor=black");
		this.textSource = textSource;
		this.conceptAnnotation = conceptAnnotation;

		controller.verifyId(id, this, false);

		setVertex(true);
		setConnectable(true);
	}

	/*
	WRITERS
	 */

	@Override
	public void writeToKnowtatorXML(Document dom, Element parent) {
		Element vertexElem = dom.createElement(KnowtatorXMLTags.VERTEX);
		vertexElem.setAttribute(KnowtatorXMLAttributes.ID, getId());
		vertexElem.setAttribute(KnowtatorXMLTags.ANNOTATION, conceptAnnotation.getId());
		vertexElem.setAttribute(KnowtatorXMLAttributes.X_LOCATION, String.valueOf(getGeometry().getX()));
		vertexElem.setAttribute(KnowtatorXMLAttributes.Y_LOCATION, String.valueOf(getGeometry().getY()));
		parent.appendChild(vertexElem);
	}

	/*
	READERS
	 */

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}

	/*
	GETTERS
	 */

	public ConceptAnnotation getConceptAnnotation() {
		return conceptAnnotation;
	}

	@Override
	public TextSource getTextSource() {
		return textSource;
	}

	@Override
	public void addDataObjectModificationListener(DataObjectModificationListener listener) {

	}

	@Override
	public void removeDataObjectModificationListener(DataObjectModificationListener listener) {

	}

	@Override
	public void modify(List parameters) {

	}

	@Override
	public void dispose() {
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
