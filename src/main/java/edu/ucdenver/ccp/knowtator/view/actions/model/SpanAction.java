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

package edu.ucdenver.ccp.knowtator.view.actions.model;

import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction;
import edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;

public class SpanAction extends AbstractKnowtatorCollectionAction<Span> {

	private final ConceptAnnotation conceptAnnotation;

	public SpanAction(CollectionActionType actionName, ConceptAnnotation conceptAnnotation) {
		super(actionName, "span", conceptAnnotation.getSpanCollection());
		this.conceptAnnotation = conceptAnnotation;

	}

	@Override
	protected void prepareAdd() {
		int start = KnowtatorView.CONTROLLER.getSelectionModel().getStart();
		int end = KnowtatorView.CONTROLLER.getSelectionModel().getEnd();

		Span newSpan = new Span(KnowtatorView.CONTROLLER, conceptAnnotation.getTextSource(), conceptAnnotation, null, start, end);
		setObject(newSpan);
	}

	@Override
	public void prepareRemove() throws ActionUnperformableException {
		// If the concept annotation only has one, remove the annotation instead
		if (conceptAnnotation.getSpanCollection().size() == 1) {
			setObject(null);
			ConceptAnnotationAction action = new ConceptAnnotationAction(REMOVE, conceptAnnotation.getTextSource());
			action.setObject(conceptAnnotation);
			KnowtatorView.CONTROLLER.registerAction(action);
			edit.addKnowtatorEdit(action.getEdit());
		} else {
			super.prepareRemove();
		}
	}

	@Override
	protected void cleanUpRemove() {

	}

	@Override
	public void execute() throws ActionUnperformableException {
		if (actionType == REMOVE && conceptAnnotation.getSpanCollection().size() == 1) {
			try {
				super.execute();
			} catch (ActionUnperformableException ignored) {

			}
		} else {
			super.execute();
		}
	}

	@Override
	public void cleanUpAdd() {

	}

}
