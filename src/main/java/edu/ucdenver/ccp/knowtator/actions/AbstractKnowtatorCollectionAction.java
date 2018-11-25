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
import edu.ucdenver.ccp.knowtator.model.collection.CantRemoveException;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;

import javax.swing.undo.UndoableEdit;

public abstract class AbstractKnowtatorCollectionAction<K extends KnowtatorDataObjectInterface> extends AbstractKnowtatorAction {
	public final static String ADD = "add";
	public static final String REMOVE = "remove";

	final String actionName;
	final KnowtatorCollectionEdit<K> edit;
	final KnowtatorCollection<K> collection;
	K object;

	AbstractKnowtatorCollectionAction(String actionName, String presentationName, KnowtatorCollection<K> collection) {
		super(String.format("%s %s", actionName, presentationName));
		this.collection = collection;
		this.actionName = actionName;
		this.edit = new KnowtatorCollectionEdit<>(actionName, collection, object, getPresentationName());
	}


	@Override
	public void execute() throws ActionUnperformableException {
		switch (actionName) {
			case ADD:
				prepareAdd();
				if (object != null) {
					collection.add(object);
				} else {
					throw new ActionUnperformableException();
				}
				cleanUpAdd();
				break;
			case REMOVE:
				prepareRemove();
				if (object != null) {
					try {
						collection.remove(object);
					} catch (CantRemoveException e) {
						throw new ActionUnperformableException();
					}
				} else {
					throw new ActionUnperformableException();
				}
				cleanUpRemove();
				break;
		}

	}

	void prepareRemove() throws ActionUnperformableException {
		try {
			setObject(collection.getSelection());
		} catch (NoSelectionException e) {
			throw new ActionUnperformableException();
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
		this.object = object;
		edit.setObject(object);
	}
}