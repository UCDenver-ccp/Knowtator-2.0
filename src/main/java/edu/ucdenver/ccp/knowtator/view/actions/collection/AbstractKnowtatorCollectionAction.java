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

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractKnowtatorCollectionAction<K extends ModelObject> extends AbstractKnowtatorAction {


	protected final CollectionActionType actionType;
	private final KnowtatorCollection<K> collection;
	protected K object;

	protected AbstractKnowtatorCollectionAction(KnowtatorModel model, CollectionActionType actionType, String presentationName, KnowtatorCollection<K> collection) {
		super(model, String.format("%s %s", actionType, presentationName));
		this.collection = collection;
		this.actionType = actionType;
		object = null;
	}


	@Override
	public void execute() throws ActionUnperformableException {
		switch (actionType) {
			case ADD:
				int size = collection.size();
				prepareAdd();
				collection.add(getObject().orElseThrow(() -> new ActionUnperformableException(getMessage())));
				cleanUpAdd();
				if (size + 1 != collection.size()) {
					setMessage("Object not added to collection");
					throw new ActionUnperformableException(getMessage());
				}
				break;
			case REMOVE:
				size = collection.size();
				if (size == 0) {
					setMessage("Collection is empty");
					throw new ActionUnperformableException(getMessage());
				}
				prepareRemove();
				collection.remove(getObject().orElseThrow(() -> new ActionUnperformableException(getMessage())));
				if (size - 1 != collection.size()) {
					setMessage("Object not removed from collection");
					throw new ActionUnperformableException(getMessage());
				}
				cleanUpRemove();
				break;
		}

	}


	@Override
	public void undo() {
		super.undo();
		switch (actionType) {
			case ADD:
				try {
					collection.remove(getObject().orElseThrow(() -> new ActionUnperformableException(getMessage())));
				} catch (ActionUnperformableException ignored) {

				}
				break;
			case REMOVE:
				try {
					collection.add(getObject().orElseThrow(() -> new ActionUnperformableException(getMessage())));
				} catch (ActionUnperformableException ignored) {

				}
				break;
		}
	}

	@Override
	public void redo() {
		super.redo();
		switch (actionType) {
			case ADD:
				try {
					int size = collection.size();
					collection.add(getObject().orElseThrow(() -> new ActionUnperformableException(getMessage())));
					if (size + 1 != collection.size()) {
						setMessage("Object not added to collection");
						throw new ActionUnperformableException(getMessage());
					}
				} catch (ActionUnperformableException ignored) {
				}
				break;
			case REMOVE:
				try {
					int size = collection.size();
					if (size == 0) {
						setMessage("Collection is empty");
						throw new ActionUnperformableException(getMessage());
					}
					collection.remove(getObject().orElseThrow(() -> new ActionUnperformableException(getMessage())));
					if (size - 1 != collection.size()) {
						setMessage("Object not removed from collection");
						throw new ActionUnperformableException(getMessage());
					}
				} catch (ActionUnperformableException ignored) {

				}
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

	protected abstract void prepareAdd();

	protected abstract void cleanUpRemove() throws ActionUnperformableException;

	@SuppressWarnings("EmptyMethod")
	protected abstract void cleanUpAdd();


	public void setObject(K object) {
		this.object = object;
	}

	public static void pickAction(KnowtatorView view, String id, File file, ActionParameters... actionParametersList) {
		view.getModel().ifPresent(model -> {
			List<AbstractKnowtatorCollectionAction> actions = new ArrayList<>();

			Arrays.asList(actionParametersList).forEach(parameters -> {
				KnowtatorCollectionType collectionType = parameters.getCollectionType();
				CollectionActionType actionType = parameters.getActionType();

				switch (collectionType) {
					case ANNOTATION:
						model.getSelectedTextSource()
								.ifPresent(textSource -> actions.add(new ConceptAnnotationAction(
										model, parameters.getActionType(),
										textSource)));
						break;
					case SPAN:
						model.getSelectedTextSource()
								.ifPresent(textSource -> textSource.getSelectedAnnotation()
										.ifPresent(conceptAnnotation -> actions.add(new SpanAction(model, actionType, conceptAnnotation))));
						break;
					case PROFILE:
						actions.add(new ProfileAction(model, actionType, id));
						break;
					case DOCUMENT:
						actions.add(new TextSourceAction(model, actionType, file));
				}


			});

			if (!actions.isEmpty()) {
				int response = 0;
				if (actions.size() != 1) {
					response = JOptionPane.showOptionDialog(view,
							"Choose an option",
							"New Concept Annotation or Span",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.PLAIN_MESSAGE,
							null,
							actions.stream().map(AbstractKnowtatorAction::getPresentationName).toArray(),
							2);
				}

					if (response != JOptionPane.CLOSED_OPTION) {
						try {
							model.registerAction(actions.get(response));
						} catch (ActionUnperformableException e) {
							JOptionPane.showMessageDialog(view, e.getMessage());
						}
				}
			}
		});
	}
}