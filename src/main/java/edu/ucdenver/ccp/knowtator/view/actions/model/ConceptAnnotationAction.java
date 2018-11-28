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

import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction;
import edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType;

public class ConceptAnnotationAction extends AbstractKnowtatorCollectionAction<ConceptAnnotation> {

	private TextSource textSource;

	public ConceptAnnotationAction(CollectionActionType actionType, TextSource textSource) {
		super(actionType, "concept annotation", textSource.getConceptAnnotationCollection());
		this.textSource = textSource;
	}

	@Override
	public void cleanUpAdd() {
	}

	@Override
	public void cleanUpRemove() {
		textSource.getGraphSpaceCollection().forEach(graphSpace -> graphSpace.getModel().removeListener(edit, mxEvent.UNDO));
	}


	@Override
	public void prepareRemove() throws ActionUnperformableException {
		super.prepareRemove();
		edit.setObject(object);
//			edit = new KnowtatorCollectionEdit<>(REMOVE, collection, object, getPresentationName(), edit.isSignificant());
		textSource.getGraphSpaceCollection().forEach(graphSpace -> graphSpace.getModel().addListener(mxEvent.UNDO, edit));
	}

	@Override
	protected void prepareAdd() {

		KnowtatorView.MODEL.getProfileCollection().getSelection()
				.ifPresent(annotator -> KnowtatorView.MODEL.getSelectedOWLClass()
						.ifPresent(owlClass -> {
							String owlClassID = KnowtatorView.MODEL.getOWLEntityRendering(owlClass);
							ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(KnowtatorView.MODEL, textSource, null, owlClass, annotator, "identity", "");
							newConceptAnnotation.getSpanCollection().add(new Span(KnowtatorView.MODEL, textSource, newConceptAnnotation, null, KnowtatorView.MODEL.getStart(), KnowtatorView.MODEL.getEnd()));
							setObject(newConceptAnnotation);
						}));

	}

}
