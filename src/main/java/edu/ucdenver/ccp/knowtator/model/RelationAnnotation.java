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

package edu.ucdenver.ccp.knowtator.model;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class RelationAnnotation extends mxCell implements KnowtatorXMLIO, TextBoundModelObject<RelationAnnotation> {
	private final String quantifier;
	private final String quantifierValue;
	private final Profile annotator;
	private String bratID;
	private final KnowtatorModel controller;
	private final Boolean isNegated;
	private final TextSource textSource;
	private OWLObjectProperty property;
	private final String propertyID;
	private final GraphSpace graphSpace;
	private final String motivation;

	private AnnotationNode sourceAnnotationNode;
	private AnnotationNode targetAnnotationNode;

	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(RelationAnnotation.class);

	public GraphSpace getGraphSpace() {
		return graphSpace;
	}

	RelationAnnotation(
			String id,
			AnnotationNode source,
			AnnotationNode target,
			OWLObjectProperty property,
			String propertyID,
			Profile annotator,
			String quantifier,
			String quantifierValue,
			Boolean isNegated,
			String motivation,
			KnowtatorModel controller, TextSource textSource, GraphSpace graphSpace) {
		super(null, new mxGeometry(), null);

		this.propertyID = propertyID;
		this.isNegated = isNegated;
		this.motivation = motivation;
		this.textSource = textSource;
		this.controller = controller;
		this.annotator = annotator;
		this.quantifier = quantifier;
		this.quantifierValue = quantifierValue;
		this.graphSpace = graphSpace;

		controller.verifyId(id, this, false);

		getGeometry().setRelative(true);
		setEdge(true);
		setSource(source);
		this.sourceAnnotationNode = source;
		setTarget(target);
		this.targetAnnotationNode = target;
		setProperty(property);

//		if (property.isPresent()) {
//			setProperty(property.getAnnotation());
//		} else {
//			setProperty(controller.getOWLObjectPropertyByID(propertyID).orElse(null));
//		}
	}

    /*
    GETTERS
     */

	@SuppressWarnings("unused")
	String getBratID() {
		return bratID;
	}

	@Override
	public TextSource getTextSource() {
		return textSource;
	}

	@Override
	public void addDataObjectModificationListener(ModelObjectListener listener) {

	}

	@Override
	public void removeDataObjectModificationListener(ModelObjectListener listener) {

	}

	@Override
	public String toString() {
		return String.format("Subject: %s; OWLObjectProperty: %s Target: %s", sourceAnnotationNode.getConceptAnnotation(), getOwlPropertyRendering(), targetAnnotationNode.getConceptAnnotation());
	}

	@Override
	public void modify() {

	}

    /*
    SETTERS
     */

	@SuppressWarnings("unused")
	void setBratID(String bratID) {
		this.bratID = bratID;
	}

	private String getOwlPropertyRendering() {
		return controller.getOWLEntityRendering(property);
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			value = String.format("%s%s\n%s %s",
					isNegated ? "not " : "",
					getOwlPropertyRendering(),
					quantifier,
					quantifierValue);


			super.setValue(value);
			if (graphSpace != null && controller.isNotLoading()) {
				graphSpace.reDrawGraph();
			}
//            textSource.save();
		}
	}

	void setProperty(OWLObjectProperty owlObjectProperty) {
		property = owlObjectProperty;
		setValue(property == null ? propertyID : property);
	}

    /*
    WRITERS
     */

	public void writeToKnowtatorXML(Document dom, Element graphElem) {
		Element tripleElem = dom.createElement(KnowtatorXMLTags.TRIPLE);
		tripleElem.setAttribute(KnowtatorXMLAttributes.ID, id);
		try {
			tripleElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
			tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT, getSource().getId());
			tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT, getTarget().getId());
		} catch (NullPointerException ignore) {

		}

		String propertyID = getOwlPropertyRendering();

		tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY, propertyID);

		tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER, quantifier);
		tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE, quantifierValue);
		tripleElem.setAttribute(KnowtatorXMLAttributes.IS_NEGATED, isNegated ? KnowtatorXMLAttributes.IS_NEGATED_TRUE : KnowtatorXMLAttributes.IS_NEGATED_FALSE);
		tripleElem.setAttribute(KnowtatorXMLAttributes.MOTIVATION, motivation);

		graphElem.appendChild(tripleElem);
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


	@Override
	public void dispose() {
	}

	@Override
	public int compareTo(RelationAnnotation o) {
		return id.compareTo(o.getId());
	}

	public OWLObjectProperty getProperty() {
		return property;
	}
}
