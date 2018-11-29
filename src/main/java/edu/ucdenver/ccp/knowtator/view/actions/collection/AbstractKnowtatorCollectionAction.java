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

package edu.ucdenver.ccp.knowtator.view.actions.collection;

import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ConceptAnnotationAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.SpanAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.TextSourceAction;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractKnowtatorCollectionAction<K extends ModelObject> extends AbstractKnowtatorAction {


	protected final CollectionActionType actionType;
	protected final KnowtatorCollectionEdit<K> edit;
	protected final KnowtatorCollection<K> collection;
	protected K object;

	protected AbstractKnowtatorCollectionAction(CollectionActionType actionType, String presentationName, KnowtatorCollection<K> collection) {
		super(String.format("%s %s", actionType, presentationName));
		this.collection = collection;
		this.actionType = actionType;
		object = null;
		this.edit = new KnowtatorCollectionEdit<>(actionType, collection, null, getPresentationName());
	}


	@Override
	public void execute() throws ActionUnperformableException {
		switch (actionType) {
			case ADD:
				prepareAdd();
				collection.add(getObject().orElseThrow(ActionUnperformableException::new));
				cleanUpAdd();
				break;
			case REMOVE:
				prepareRemove();
				collection.remove(getObject().orElseThrow(ActionUnperformableException::new));
				cleanUpRemove();
				break;
		}

	}

	private Optional<K> getObject() {
		return Optional.ofNullable(object);
	}

	public void prepareRemove() throws ActionUnperformableException {
		if (!getObject().isPresent()) {
			collection.getSelection().ifPresent(this::setObject);
		}
	}

	protected abstract void prepareAdd() throws ActionUnperformableException;

	protected abstract void cleanUpRemove() throws ActionUnperformableException;

	@SuppressWarnings("EmptyMethod")
	protected abstract void cleanUpAdd();


	@Override
	public UndoableEdit getEdit() {
		return edit;
	}

	public void setObject(K object) {
		this.object = object;
		edit.setObject(this.object);
	}

	public static void pickAction(KnowtatorView view, String id, File file, ActionParameters... actionParametersList) {
		List<AbstractKnowtatorCollectionAction> actions = new ArrayList<>();

		Arrays.asList(actionParametersList).forEach(parameters -> {
			KnowtatorCollectionType collectionType = parameters.getCollectionType();
			CollectionActionType actionType = parameters.getActionType();

			switch (collectionType) {
				case ANNOTATION:
					KnowtatorView.MODEL.getSelectedTextSource()
							.ifPresent(textSource -> actions.add(new ConceptAnnotationAction(
									parameters.getActionType(),
									textSource)));
					break;
				case SPAN:
					KnowtatorView.MODEL.getSelectedTextSource()
							.ifPresent(textSource -> textSource.getSelectedAnnotation()
									.ifPresent(conceptAnnotation -> actions.add(new SpanAction(actionType, conceptAnnotation))));
					break;
				case PROFILE:
					actions.add(new ProfileAction(actionType, id));
					break;
				case DOCUMENT:
					actions.add(new TextSourceAction(actionType, file));
			}


		});

		if (!actions.isEmpty()) {
			AbstractKnowtatorAction action;
			if (actions.size() == 1) {
				action = actions.get(0);
			} else {
				int response = JOptionPane.showOptionDialog(view,
						"Choose an option",
						"New Concept Annotation or Span",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,
						actions.stream().map(AbstractKnowtatorAction::getPresentationName).toArray(),
						2);
				action = actions.get(response);
			}

			KnowtatorView.MODEL.registerAction(action);
		}
	}
}