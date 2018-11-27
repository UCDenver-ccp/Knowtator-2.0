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

package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;

import javax.swing.undo.UndoableEdit;
import java.util.Optional;

public abstract class AbstractKnowtatorCollectionAction<K extends KnowtatorDataObjectInterface> extends AbstractKnowtatorAction {


	final CollectionActionType actionType;
	final KnowtatorCollectionEdit<K> edit;
	final KnowtatorCollection<K> collection;
	Optional<K> object;

	AbstractKnowtatorCollectionAction(CollectionActionType actionType, String presentationName, KnowtatorCollection<K> collection) {
		super(String.format("%s %s", actionType, presentationName));
		this.collection = collection;
		this.actionType = actionType;
		this.edit = new KnowtatorCollectionEdit<>(actionType, collection, object, getPresentationName());
		object = Optional.empty();
	}


	@Override
	public void execute() throws ActionUnperformableException {
		switch (actionType) {
			case ADD:
				prepareAdd();
				collection.add(object.orElseThrow(ActionUnperformableException::new));
				cleanUpAdd();
				break;
			case REMOVE:
				prepareRemove();
				collection.remove(object.orElseThrow(ActionUnperformableException::new));
				cleanUpRemove();
				break;
		}

	}

	void prepareRemove() throws ActionUnperformableException {
		if (!object.isPresent()) {
			collection.getSelection().ifPresent(this::setObject);
		}
	}

	abstract void prepareAdd() throws ActionUnperformableException;

	abstract void cleanUpRemove() throws ActionUnperformableException;

	@SuppressWarnings("EmptyMethod")
	abstract void cleanUpAdd();


	@Override
	public UndoableEdit getEdit() {
		return edit;
	}

	void setObject(K object) {
		this.object = Optional.ofNullable(object);
		edit.setObject(this.object);
	}
}