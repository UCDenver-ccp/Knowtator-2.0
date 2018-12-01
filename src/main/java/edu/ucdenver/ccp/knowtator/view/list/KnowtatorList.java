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

package edu.ucdenver.ccp.knowtator.view.list;

import edu.ucdenver.ccp.knowtator.model.FilterType;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public abstract class KnowtatorList<K extends ModelObject> extends JList<K> implements KnowtatorComponent, ModelListener {

	protected KnowtatorCollection<K> collection;
	ListSelectionListener al;

	KnowtatorList() {
		setModel(new DefaultListModel<>());

		al = e -> {
			JList jList = (JList) e.getSource();
			if (jList.getSelectedValue() != null) {
				collection.setSelection(this.getSelectedValue());
			}
		};

		addListSelectionListener(al);

	}

	protected abstract void react();

	protected void setCollection(KnowtatorCollection<K> collection) {
		//clear collection
		((DefaultListModel) getModel()).clear();
		this.collection = collection;
		if (collection.size() == 0) {
			setEnabled(false);
		} else {
			setEnabled(true);
			collection.forEach(k -> ((DefaultListModel<K>) getModel()).addElement(k));
		}
	}


	void setSelected() {
		if (KnowtatorView.MODEL.isNotLoading()) {
			if (collection.getSelection().isPresent()) {
				K k = collection.getSelection().get();
				for (int i = 0; i < getModel().getSize(); i++) {
					K element = getModel().getElementAt(i);
					if (element == k) {
						removeListSelectionListener(al);
						setSelectedIndex(i);
						ensureIndexIsVisible(i);
						addListSelectionListener(al);
						return;
					}
				}
			}
		}
	}

	@Override
	public void reset() {
		dispose();
		KnowtatorView.MODEL.addModelListener(this);
	}

	@Override
	public void dispose() {
		((DefaultListModel) getModel()).clear();
		KnowtatorView.MODEL.removeModelListener(this);
	}

	@Override
	public void filterChangedEvent(FilterType filterType, boolean filterValue) {
		react();
	}


	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		react();
	}

	@Override
	public void colorChangedEvent() {
		react();
	}
}
