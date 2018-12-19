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

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class AnnotationNode extends mxCell implements ConceptAnnotationBoundModelObject<AnnotationNode>, GraphBoundModelObject<AnnotationNode>, KnowtatorXMLIO {

	private final ConceptAnnotation conceptAnnotation;
	private final TextSource textSource;
	private final GraphSpace graphSpace;
	private final KnowtatorModel model;

	AnnotationNode(String id, ConceptAnnotation conceptAnnotation, double x, double y, GraphSpace graphSpace) {
		super(conceptAnnotation.toMultilineString(), new mxGeometry(x, y, 150, 150), "defaultVertex");
		this.textSource = conceptAnnotation.getTextSource();
		this.conceptAnnotation = conceptAnnotation;
		this.graphSpace = graphSpace;
		this.model = conceptAnnotation.getKnowtatorModel();
		model.verifyId(id, this, false);

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

	@Override
	public ConceptAnnotation getConceptAnnotation() {
		return conceptAnnotation;
	}

	@Override
	public TextSource getTextSource() {
		return textSource;
	}

	@Override
	public void dispose() {
	}

	@Override
	public KnowtatorModel getKnowtatorModel() {
		return model;
	}

	@Override
	public int compareTo(AnnotationNode o) {
		return 0;
	}

	@Override
	public GraphSpace getGraphSpace() {
		return graphSpace;
	}
}
