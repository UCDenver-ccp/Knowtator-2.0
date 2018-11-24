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

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public abstract class KnowtatorList<K extends KnowtatorDataObjectInterface> extends JList<K> implements KnowtatorComponent {

	final KnowtatorView view;
	protected KnowtatorCollection<K> collection;
	private ListSelectionListener al;

	KnowtatorList(KnowtatorView view) {
		this.view = view;
		setModel(new DefaultListModel<>());

		al = e -> {
			JList jList = (JList) e.getSource();
			if (jList.getSelectedValue() != null) {
				collection.setSelection(this.getSelectedValue());
			}
		};

		addListSelectionListener(al);

	}

	@Override
	public void setupListeners() {
		//noinspection Duplicates
		new TextBoundModelListener(view.getController()) {
			@Override
			public void respondToConceptAnnotationModification() {
				react();
			}

			@Override
			public void respondToSpanModification() {
				react();
			}

			@Override
			public void respondToGraphSpaceModification() {
				react();
			}

			@Override
			public void respondToGraphSpaceCollectionFirstAdded() {
				react();
			}

			@Override
			public void respondToGraphSpaceCollectionEmptied() {
				react();
			}

			@Override
			public void respondToGraphSpaceRemoved() {
				react();
			}

			@Override
			public void respondToGraphSpaceAdded() {
				react();
			}

			@Override
			public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {
				react();
			}

			@Override
			public void respondToConceptAnnotationCollectionEmptied() {
				react();
			}

			@Override
			public void respondToConceptAnnotationRemoved() {
				react();
			}

			@Override
			public void respondToConceptAnnotationAdded() {
				react();
			}

			@Override
			public void respondToConceptAnnotationCollectionFirstAdded() {
				react();
			}

			@Override
			public void respondToSpanCollectionFirstAdded() {
				react();
			}

			@Override
			public void respondToSpanCollectionEmptied() {
				react();
			}

			@Override
			public void respondToSpanRemoved() {
				react();
			}

			@Override
			public void respondToSpanAdded() {
				react();
			}

			@Override
			public void respondToSpanSelection(SelectionEvent<Span> event) {
				react();
			}

			@Override
			public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
				react();
			}

			@Override
			public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
				react();
			}

			@Override
			public void respondToTextSourceAdded() {
				react();
			}

			@Override
			public void respondToTextSourceRemoved() {
				react();
			}

			@Override
			public void respondToTextSourceCollectionEmptied() {
				react();
			}

			@Override
			public void respondToTextSourceCollectionFirstAdded() {
				react();
			}
		};

	}

	protected abstract void react();

	public void setCollection(KnowtatorCollection<K> collection) {
		//clear collection
		dispose();
		this.collection = collection;
		if (collection.size() == 0) {
			setEnabled(false);
		} else {
			setEnabled(true);
			collection.forEach(k -> ((DefaultListModel<K>) getModel()).addElement(k));
		}
	}


	void setSelected() throws NoSelectionException {
		if (view.getController().isNotLoading()) {
			for (int i = 0; i < getModel().getSize(); i++) {
				K element = getModel().getElementAt(i);
				if (element == collection.getSelection()) {
					removeListSelectionListener(al);
					setSelectedIndex(i);
					ensureIndexIsVisible(i);
					addListSelectionListener(al);
					return;
				}
			}
		}

	}

	@Override
	public void reset() {
		setupListeners();
	}

	@Override
	public void dispose() {
		((DefaultListModel) getModel()).clear();
	}
}
