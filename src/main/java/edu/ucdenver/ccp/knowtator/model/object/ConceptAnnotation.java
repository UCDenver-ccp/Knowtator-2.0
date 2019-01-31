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

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class ConceptAnnotation extends SpanCollection implements TextBoundModelObject<ConceptAnnotation> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ConceptAnnotation.class);
	private final TextSource textSource;
	private OWLClass owlClass;
	private final String annotationType;

	private final Set<ConceptAnnotation> overlappingConceptAnnotations;
	private final Profile annotator;
	private String bratID;
	private String motivation;
	private String id;

	public ConceptAnnotation(
			@Nonnull TextSource textSource,
			String annotationID,
			@Nonnull OWLClass owlClass,
			@Nonnull Profile annotator,
			String annotationType,
			String motivation) {
		super(textSource.getKnowtatorModel());

		this.annotator = annotator;
		this.annotationType = annotationType;
		this.motivation = motivation;
		this.owlClass = owlClass;
		this.textSource = textSource;

		model.verifyId(annotationID, this, false);

		overlappingConceptAnnotations = new HashSet<>();

		getColor();
	}

	public TextSource getTextSource() {
		return textSource;
	}

  /*
  GETTERS
   */

	public Profile getAnnotator() {
		return annotator;
	}

	public OWLClass getOwlClass() {
		return owlClass;
	}

	/**
	 * @return the getNumberOfGraphSpaces of the Span associated with the concept. If the concept has more than
	 * one Span, then the sum of the getNumberOfGraphSpaces of the spanCollection is returned.
	 */
	public int getSize() {
		int size = 0;
		for (Span span : this) {
			size += span.getSize();
		}
		return size;
	}

	public String getSpannedText() {
		return stream().map(Span::getSpannedText).collect(Collectors.joining(" "));
	}

	@SuppressWarnings("unused")
	public Set<ConceptAnnotation> getOverlappingConceptAnnotations() {
		return overlappingConceptAnnotations;
	}

	public String getBratID() {
		return bratID;
	}

	public Color getColor() {
		return annotator.getColor(owlClass);
	}


  /*
  SETTERS
   */


	public void setOwlClass(OWLClass owlClass) {
		this.owlClass = owlClass;
		model.fireModelEvent(new ChangeEvent<>(model, null, this));

	}

	public void setBratID(String bratID) {
		this.bratID = bratID;
	}


	public String getOWLClassLabel() {
		return owlClass.getAnnotationPropertiesInSignature().stream()
				.filter(OWLAnnotationProperty::isLabel)
				.findFirst()
				.map(OWLObject::toString).orElse(model.getOWLEntityRendering(owlClass));
	}


	/**
	 * this needs to be moved out of this class
	 *
	 * @return an html representation of the concept
	 */
	public String toHTML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<ul><li>").append(annotator.getId()).append("</li>");

		sb.append("<li>class = ").append(getOwlClass()).append("</li>");
		sb.append("<li>spanCollection = ");
		for (Span span : this) sb.append(span.toString()).append(" ");
		sb.append("</li>");

		sb.append("</ul>");
		return sb.toString();
	}

	@Override
	public String toString() {
		return String.format(
				"%s", stream().map(Span::toString).collect(Collectors.joining(" ")));
	}


	@SuppressWarnings("unused")
	void addOverlappingAnnotation(ConceptAnnotation conceptAnnotation) {
		overlappingConceptAnnotations.add(conceptAnnotation);
	}

	@Override
	public int compareTo(ConceptAnnotation conceptAnnotation2) {
		int result = TextBoundModelObject.super.compareTo(conceptAnnotation2);
		Iterator<Span> spanIterator1 = iterator();
		Iterator<Span> spanIterator2 = conceptAnnotation2.iterator();
		while (result == 0 && spanIterator1.hasNext() && spanIterator2.hasNext()) {
			Span span1 = spanIterator1.next();
			Span span2 = spanIterator2.next();
			if (span2 == null) {
				result = 1;
			} else if (span1 == null) {
				result = -1;
			} else {
				result = span1.getStart().compareTo(span2.getStart());
				if (result == 0) {
					result = span1.getEnd().compareTo(span2.getEnd());
				}
			}
		}
		if (result == 0) {
			result = this.getId().compareTo(conceptAnnotation2.getId());
		}
		return result;
	}

	public boolean contains(Integer loc) {
		for (Span span : this) {
			if (span.contains(loc)) {
				return true;
			}
		}
		return false;
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
	public KnowtatorModel getKnowtatorModel() {
		return model;
	}

	public String getMotivation() {
		return motivation;
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
		//TODO: Potentially could make typing very slow
		model.fireModelEvent(new ChangeEvent<>(model, null, this));
	}

	String toMultilineString() {
		return String.format(
				"%s\n(%s)", stream().map(Span::toString).collect(Collectors.joining("\n")),
				model.getOWLEntityRendering(owlClass));
	}

	public String getAnnotationType() {
		return annotationType;
	}

	public String getOwlClassRendering() {
		return model.getOWLEntityRendering(owlClass);
	}
}
