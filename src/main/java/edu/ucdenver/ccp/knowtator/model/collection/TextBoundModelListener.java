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


import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.text.DataObjectModificationListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;

/**
 * Provides methods to respond to changes in model selection events
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

				respondToGraphSpaceSelection(event);
			}

			@Override
			public void added() {
				respondToGraphSpaceAdded();
			}

			@Override
			public void removed() {
				respondToGraphSpaceRemoved();
			}

			@Override
			public void emptied() {
				respondToGraphSpaceCollectionEmptied();
			}

			@Override
			public void firstAdded() {
				respondToGraphSpaceCollectionFirstAdded();
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

				respondToConceptAnnotationSelection(event);
			}

			@Override
			public void added() {
				respondToConceptAnnotationAdded();
			}

			@Override
			public void removed() {
				respondToConceptAnnotationRemoved();
			}

			@Override
			public void emptied() {
				respondToConceptAnnotationCollectionEmptied();
			}

			@Override
			public void firstAdded() {
				respondToConceptAnnotationCollectionFirstAdded();
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

				respondToSpanSelection(event);
			}

			@Override
			public void added() {
				respondToSpanAdded();
			}

			@Override
			public void removed() {
				respondToSpanRemoved();
			}

			@Override
			public void emptied() {
				respondToSpanCollectionEmptied();
			}

			@Override
			public void firstAdded() {
				respondToSpanCollectionFirstAdded();
			}
		};
	}

	protected abstract void respondToConceptAnnotationModification();

	protected abstract void respondToSpanModification();

	protected abstract void respondToGraphSpaceModification();

	protected abstract void respondToGraphSpaceCollectionFirstAdded();

	protected abstract void respondToGraphSpaceCollectionEmptied();

	protected abstract void respondToGraphSpaceRemoved();

	protected abstract void respondToGraphSpaceAdded();

	protected abstract void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event);

	protected abstract void respondToConceptAnnotationCollectionEmptied();

	protected abstract void respondToConceptAnnotationRemoved();

	protected abstract void respondToConceptAnnotationAdded();

	protected abstract void respondToConceptAnnotationCollectionFirstAdded();

	protected abstract void respondToSpanCollectionFirstAdded();

	protected abstract void respondToSpanCollectionEmptied();

	protected abstract void respondToSpanRemoved();

	protected abstract void respondToSpanAdded();

	protected abstract void respondToSpanSelection(SelectionEvent<Span> event);

	protected abstract void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event);

	protected abstract void respondToTextSourceSelection(SelectionEvent<TextSource> event);

	protected abstract void respondToTextSourceAdded();

	protected abstract void respondToTextSourceRemoved();

	protected abstract void respondToTextSourceCollectionEmptied();

	protected abstract void respondToTextSourceCollectionFirstAdded();

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

		respondToTextSourceSelection(event);
	}

	@Override
	public void added() {
		respondToTextSourceAdded();
	}

	@Override
	public void removed() {
		respondToTextSourceRemoved();
	}

	@Override
	public void emptied() {
		respondToTextSourceCollectionEmptied();
	}

	@Override
	public void firstAdded() {
		respondToTextSourceCollectionFirstAdded();
	}

}
