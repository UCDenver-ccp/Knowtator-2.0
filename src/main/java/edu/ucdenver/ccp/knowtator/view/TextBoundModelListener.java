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
public abstract class TextBoundModelListener implements KnowtatorCollectionListener<TextSource> {
	private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
	private final KnowtatorCollectionListener<Span> spanCollectionListener;
	private final KnowtatorCollectionListener<GraphSpace> graphSpaceCollectionListener;


	protected TextBoundModelListener(KnowtatorController controller) {
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
				if (event.getOld() != null) {
					event.getOld().getSpanCollection().removeCollectionListener(spanCollectionListener);
				}
				if (event.getNew() != null) {
					event.getNew().getSpanCollection().addCollectionListener(spanCollectionListener);
				}

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

	public abstract void respondToGraphSpaceCollectionFirstAddedEvent();

	public abstract void respondToGraphSpaceCollectionEmptiedEvent();

	public abstract void respondToGraphSpaceChangedEvent(ChangeEvent<GraphSpace> event);

	public abstract void respondToGraphSpaceRemovedEvent(RemoveEvent<GraphSpace> event);

	public abstract void respondToGraphSpaceAddedEvent(AddEvent<GraphSpace> event);

	public abstract void respondToGraphSpaceSelectionEvent(SelectionChangeEvent<GraphSpace> event);

	public abstract void respondToConceptAnnotationCollectionEmptiedEvent();

	public abstract void respondToConceptAnnotationChangedEvent(ChangeEvent<ConceptAnnotation> event);

	public abstract void respondToConceptAnnotationRemovedEvent(RemoveEvent<ConceptAnnotation> event);

	public abstract void respondToConceptAnnotationAddedEvent(AddEvent<ConceptAnnotation> event);

	public abstract void respondToConceptAnnotationCollectionFirstAddedEvent();

	public abstract void respondToSpanCollectionFirstAddedEvent();

	public abstract void respondToSpanCollectionEmptiedEvent();

	public abstract void respondToSpanChangedEvent(ChangeEvent<Span> event);

	public abstract void respondToSpanRemovedEvent(RemoveEvent<Span> event);

	public abstract void respondToSpanAddedEvent(AddEvent<Span> event);

	public abstract void respondToSpanSelectionEvent(SelectionChangeEvent<Span> event);

	public abstract void respondToConceptAnnotationSelectionEvent(SelectionChangeEvent<ConceptAnnotation> event);

	public abstract void respondToTextSourceSelectionEvent(SelectionChangeEvent<TextSource> event);

	public abstract void respondToTextSourceAddedEvent(AddEvent<TextSource> event);

	public abstract void respondToTextSourceRemovedEvent(RemoveEvent<TextSource> event);

	public abstract void respondToTextSourceChangedEvent(ChangeEvent<TextSource> event);

	public abstract void respondToTextSourceCollectionEmptiedEvent();

	public abstract void respondToTextSourceCollectionFirstAddedEvent();

	@Override
	public void selected(SelectionChangeEvent<TextSource> event) {
		if (event.getOld() != null) {
			event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
			event.getOld().getGraphSpaceCollection().removeCollectionListener(graphSpaceCollectionListener);
		}
		if (event.getNew() != null) {
			event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
			event.getNew().getGraphSpaceCollection().addCollectionListener(graphSpaceCollectionListener);
		}

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
