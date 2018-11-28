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

package edu.ucdenver.ccp.knowtator.model.collection.listener;


import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;

/**
 * Provides methods to respond to changes in model selection events
 *
 * @author Harrison
 */
public abstract class TextBoundModelListener implements TextSourceCollectionListener {
	private final ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
	private final SpanCollectionListener spanCollectionListener;
	private final GraphSpaceCollectionListener graphSpaceCollectionListener;
	private final ModelObjectListener graphSpaceModificationListener;
	private final ModelObjectListener conceptAnnotationModificationListener;
	private final ModelObjectListener spanModificationListener;
	private final KnowtatorModel controller;


	protected TextBoundModelListener(KnowtatorModel controller) {
		this.controller = controller;


		graphSpaceModificationListener = this::respondToGraphSpaceModification;

		spanModificationListener = this::respondToSpanModification;

		conceptAnnotationModificationListener = this::respondToConceptAnnotationModification;

		graphSpaceCollectionListener = new GraphSpaceCollectionListener() {
			@Override
			public void selected(SelectionEvent<GraphSpace> event) {
				event.getOld().ifPresent(graphSpace -> graphSpace.removeDataObjectModificationListener(graphSpaceModificationListener));
				event.getNew().ifPresent(graphSpace -> graphSpace.addDataObjectModificationListener(graphSpaceModificationListener));

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

		conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {

			@Override
			public void selected(SelectionEvent<ConceptAnnotation> event) {
				event.getOld().ifPresent(conceptAnnotation -> conceptAnnotation.removeCollectionListener(spanCollectionListener));
				event.getOld().ifPresent(conceptAnnotation -> conceptAnnotation.removeDataObjectModificationListener(conceptAnnotationModificationListener));
				event.getNew().ifPresent(conceptAnnotation -> conceptAnnotation.addCollectionListener(spanCollectionListener));
				event.getNew().ifPresent(conceptAnnotation -> conceptAnnotation.addDataObjectModificationListener(conceptAnnotationModificationListener));

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

		spanCollectionListener = new SpanCollectionListener() {

			@Override
			public void selected(SelectionEvent<Span> event) {
				event.getOld().ifPresent(span -> span.removeDataObjectModificationListener(spanModificationListener));
				event.getNew().ifPresent(span -> span.addDataObjectModificationListener(spanModificationListener));

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

		setupListeners();
	}

	private void setupListeners() {
		controller.addTextSourceCollectionListener(this);
		controller.getSelectedTextSource().ifPresent(textSource -> {
			textSource.addCollectionListener(conceptAnnotationCollectionListener);
			textSource.addCollectionListener(graphSpaceCollectionListener);
			textSource.getSelectedAnnotation().ifPresent(graphSpace -> graphSpace.addDataObjectModificationListener(graphSpaceModificationListener));
			textSource.getSelectedAnnotation().ifPresent(conceptAnnotation -> {
				conceptAnnotation.addCollectionListener(spanCollectionListener);
				conceptAnnotation.addDataObjectModificationListener(conceptAnnotationModificationListener);
				conceptAnnotation.getSelection().ifPresent(span -> span.addDataObjectModificationListener(spanModificationListener));
			});
		});
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
		event.getOld().ifPresent(textSource -> textSource.removeCollectionListener(graphSpaceCollectionListener));
		event.getOld().ifPresent(textSource -> textSource.removeCollectionListener(conceptAnnotationCollectionListener));
		event.getNew().ifPresent(textSource -> textSource.addCollectionListener(conceptAnnotationCollectionListener));
		event.getNew().ifPresent(textSource -> textSource.addCollectionListener(graphSpaceCollectionListener));

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

	public void dispose() {
		controller.removeTextSourceCollectionListener(this);

		removeListeners();
	}

	private void removeListeners() {
		controller.removeTextSourceCollectionListener(this);
		controller.getSelectedTextSource().ifPresent(textSource -> {
			textSource.removeCollectionListener(conceptAnnotationCollectionListener);
			textSource.removeCollectionListener(graphSpaceCollectionListener);
			textSource.getSelectedAnnotation().ifPresent(graphSpace -> graphSpace.removeDataObjectModificationListener(graphSpaceModificationListener));
			textSource.getSelectedAnnotation().ifPresent(conceptAnnotation -> {
				conceptAnnotation.removeCollectionListener(spanCollectionListener);
				conceptAnnotation.removeDataObjectModificationListener(conceptAnnotationModificationListener);
				conceptAnnotation.getSelection().ifPresent(span -> span.removeDataObjectModificationListener(spanModificationListener));
			});
		});
	}
}
