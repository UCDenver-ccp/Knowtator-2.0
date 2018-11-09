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
import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.text.DataObjectModificationListener;
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
	private final DataObjectModificationListener<GraphSpace> graphSpaceModificationListener;
	private final DataObjectModificationListener<ConceptAnnotation> conceptAnnotationModificationListener;
	private final DataObjectModificationListener<Span> spanModificationListener;


	protected TextBoundModelListener(KnowtatorController controller) {
		controller.getTextSourceCollection().addCollectionListener(this);

		graphSpaceModificationListener = this::respondToGraphSpaceModification;

		spanModificationListener = this::respondToSpanModification;

		conceptAnnotationModificationListener = this::respondToConceptAnnotationModification;

		graphSpaceCollectionListener = new KnowtatorCollectionListener<GraphSpace>() {
			@Override
			public void selected(SelectionEvent<GraphSpace> event) {
				if (event.getOld() != null) {
					event.getOld().removeDataObjectModificationListener(graphSpaceModificationListener);
				}
				if (event.getNew() != null) {
					event.getNew().addDataObjectModificationListener(graphSpaceModificationListener);
				}

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
			public void selected(SelectionEvent<ConceptAnnotation> event) {
				if (event.getOld() != null) {
					event.getOld().getSpanCollection().removeCollectionListener(spanCollectionListener);
					event.getOld().removeDataObjectModificationListener(conceptAnnotationModificationListener);
				}
				if (event.getNew() != null) {
					event.getNew().getSpanCollection().addCollectionListener(spanCollectionListener);
					event.getNew().addDataObjectModificationListener(conceptAnnotationModificationListener);
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
			public void selected(SelectionEvent<Span> event) {
				if (event.getOld() != null) {
					event.getOld().removeDataObjectModificationListener(spanModificationListener);
				}
				if (event.getNew() != null) {
					event.getNew().addDataObjectModificationListener(spanModificationListener);
				}

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
			public void emptied() {
				respondToSpanCollectionEmptiedEvent();
			}

			@Override
			public void firstAdded() {
				respondToSpanCollectionFirstAddedEvent();
			}
		};
	}

	public abstract void respondToConceptAnnotationModification();

	public abstract void respondToSpanModification();

	public abstract void respondToGraphSpaceModification();

	public abstract void respondToGraphSpaceCollectionFirstAddedEvent();

	public abstract void respondToGraphSpaceCollectionEmptiedEvent();

	public abstract void respondToGraphSpaceRemovedEvent(RemoveEvent<GraphSpace> event);

	public abstract void respondToGraphSpaceAddedEvent(AddEvent<GraphSpace> event);

	public abstract void respondToGraphSpaceSelectionEvent(SelectionEvent<GraphSpace> event);

	public abstract void respondToConceptAnnotationCollectionEmptiedEvent();

	public abstract void respondToConceptAnnotationRemovedEvent(RemoveEvent<ConceptAnnotation> event);

	public abstract void respondToConceptAnnotationAddedEvent(AddEvent<ConceptAnnotation> event);

	public abstract void respondToConceptAnnotationCollectionFirstAddedEvent();

	public abstract void respondToSpanCollectionFirstAddedEvent();

	public abstract void respondToSpanCollectionEmptiedEvent();

	public abstract void respondToSpanRemovedEvent(RemoveEvent<Span> event);

	public abstract void respondToSpanAddedEvent(AddEvent<Span> event);

	public abstract void respondToSpanSelectionEvent(SelectionEvent<Span> event);

	public abstract void respondToConceptAnnotationSelectionEvent(SelectionEvent<ConceptAnnotation> event);

	public abstract void respondToTextSourceSelectionEvent(SelectionEvent<TextSource> event);

	public abstract void respondToTextSourceAddedEvent(AddEvent<TextSource> event);

	public abstract void respondToTextSourceRemovedEvent(RemoveEvent<TextSource> event);

	public abstract void respondToTextSourceCollectionEmptiedEvent();

	public abstract void respondToTextSourceCollectionFirstAddedEvent();

	@Override
	public void selected(SelectionEvent<TextSource> event) {
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
	public void emptied() {
		respondToTextSourceCollectionEmptiedEvent();
	}

	@Override
	public void firstAdded() {
		respondToTextSourceCollectionFirstAddedEvent();
	}

}
