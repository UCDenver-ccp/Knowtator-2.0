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

package edu.ucdenver.ccp.knowtator.view;


import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;

/**
 * Provides methods to respond to changes in model selction events
 *
 * @author Harrison
 */
public class TextBoundModelListener implements KnowtatorCollectionListener<TextSource> {
	private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
	private final KnowtatorCollectionListener<Span> spanCollectionListener;
	private final KnowtatorCollectionListener<GraphSpace> graphSpaceCollectionListener;


	TextBoundModelListener(KnowtatorController controller) {
		controller.getTextSourceCollection().addCollectionListener(this);

		graphSpaceCollectionListener = new KnowtatorCollectionListener<GraphSpace>() {
			@Override
			public void selected(SelectionChangeEvent<GraphSpace> event) {
				respondToGraphSpaceSelectionEvent(event);
			}

			@Override
			public void added(AddEvent<GraphSpace> event) {
				respondToGraphSpaceAddedEvent(event);
			}

			@Override
			public void removed(RemoveEvent<GraphSpace> event) {
				respondToGraphSpaceRemovedEvent(event);
			}

			@Override
			public void changed(ChangeEvent<GraphSpace> event) {
				respondToGraphSpaceChangedEvent(event);
			}

			@Override
			public void emptied() {
				respondToGraphSpaceCollectionEmptiedEvent();
			}

			@Override
			public void firstAdded() {
				respondToGraphSpaceCollectionFirstAddedEvent();
			}
		};

		conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {

			@Override
			public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
				respondToConceptAnnotationSelectionEvent(event);
			}

			@Override
			public void added(AddEvent<ConceptAnnotation> event) {
				respondToConceptAnnotationAddedEvent(event);
			}

			@Override
			public void removed(RemoveEvent<ConceptAnnotation> event) {
				respondToConceptAnnotationRemovedEvent(event);
			}

			@Override
			public void changed(ChangeEvent<ConceptAnnotation> event) {
				respondToConceptAnnotationChangedEvent(event);
			}

			@Override
			public void emptied() {
				respondToConceptAnnotationCollectionEmptiedEvent();
			}

			@Override
			public void firstAdded() {
				respondToConceptAnnotationCollectionFirstAddedEvent();
			}
		}

		;

		spanCollectionListener = new KnowtatorCollectionListener<Span>() {

			@Override
			public void selected(SelectionChangeEvent<Span> event) {
				respondToSpanSelectionEvent(event);
			}

			@Override
			public void added(AddEvent<Span> event) {
				respondToSpanAddedEvent(event);
			}

			@Override
			public void removed(RemoveEvent<Span> event) {
				respondToSpanRemovedEvent(event);
			}

			@Override
			public void changed(ChangeEvent<Span> event) {
				respondToSpanChangedEvent(event);
			}

			@Override
			public void emptied() {
				respondToSpanCollectionEmptiedEvent();
			}

			@Override
			public void firstAdded() {
				respondToSpanCollectionFirstAddedEvent();
			}
		};
	}

	public void respondToGraphSpaceCollectionFirstAddedEvent() {

	}

	public void respondToGraphSpaceCollectionEmptiedEvent() {

	}

	public void respondToGraphSpaceChangedEvent(ChangeEvent<GraphSpace> event) {

	}

	public void respondToGraphSpaceRemovedEvent(RemoveEvent<GraphSpace> event) {

	}

	public void respondToGraphSpaceAddedEvent(AddEvent<GraphSpace> event) {

	}

	public void respondToGraphSpaceSelectionEvent(SelectionChangeEvent<GraphSpace> event) {

	}

	public void respondToConceptAnnotationCollectionEmptiedEvent() {

	}

	public void respondToConceptAnnotationChangedEvent(ChangeEvent<ConceptAnnotation> event) {

	}

	public void respondToConceptAnnotationRemovedEvent(RemoveEvent<ConceptAnnotation> event) {

	}

	public void respondToConceptAnnotationAddedEvent(AddEvent<ConceptAnnotation> event) {

	}

	public void respondToConceptAnnotationCollectionFirstAddedEvent() {

	}

	public void respondToSpanCollectionFirstAddedEvent() {

	}

	public void respondToSpanCollectionEmptiedEvent() {
	}

	public void respondToSpanChangedEvent(ChangeEvent<Span> event) {

	}

	public void respondToSpanRemovedEvent(RemoveEvent<Span> event) {
	}

	public void respondToSpanAddedEvent(AddEvent<Span> event) {
	}

	public void respondToSpanSelectionEvent(SelectionChangeEvent<Span> event) {

	}

	public void respondToConceptAnnotationSelectionEvent(SelectionChangeEvent<ConceptAnnotation> event) {
		if (event.getOld() != null) {
			event.getOld().getSpanCollection().removeCollectionListener(spanCollectionListener);
		}
		if (event.getNew() != null) {
			event.getNew().getSpanCollection().addCollectionListener(spanCollectionListener);
		}
	}

	public void respondToTextSourceSelectionEvent(SelectionChangeEvent<TextSource> event) {
		if (event.getOld() != null) {
			event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
			event.getOld().getGraphSpaceCollection().removeCollectionListener(graphSpaceCollectionListener);
		}
		if (event.getNew() != null) {
			event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
			event.getNew().getGraphSpaceCollection().addCollectionListener(graphSpaceCollectionListener);
		}
	}

	public void respondToTextSourceAddedEvent(AddEvent<TextSource> event) {

	}

	public void respondToTextSourceRemovedEvent(RemoveEvent<TextSource> event) {

	}

	public void respondToTextSourceChangedEvent(ChangeEvent<TextSource> event) {

	}

	public void respondToTextSourceCollectionEmptiedEvent() {

	}

	public void respondToTextSourceCollectionFirstAddedEvent() {

	}

	@Override
	public void selected(SelectionChangeEvent<TextSource> event) {
		respondToTextSourceSelectionEvent(event);
	}

	@Override
	public void added(AddEvent<TextSource> event) {
		respondToTextSourceAddedEvent(event);
	}

	@Override
	public void removed(RemoveEvent<TextSource> event) {
		respondToTextSourceRemovedEvent(event);
	}

	@Override
	public void changed(ChangeEvent<TextSource> event) {
		respondToTextSourceChangedEvent(event);
	}

	@Override
	public void emptied() {
		respondToTextSourceCollectionEmptiedEvent();
	}

	@Override
	public void firstAdded() {
		respondToTextSourceCollectionFirstAddedEvent();
	}

}
