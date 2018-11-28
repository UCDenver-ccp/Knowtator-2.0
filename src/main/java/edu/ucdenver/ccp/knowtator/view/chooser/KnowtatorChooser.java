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

package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.listener.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class KnowtatorChooser<K extends ModelObject> extends JComboBox<K> implements KnowtatorComponent {

	private final ActionListener al;
	private KnowtatorCollection<K> collection;

	KnowtatorChooser() {
		al = e -> {
			JComboBox comboBox = (JComboBox) e.getSource();
			if (comboBox.getSelectedItem() != null) {
				this.collection.setSelection(getItemAt(getSelectedIndex()));
			}
		};


	}

	@Override
	public void reset() {
		setupListeners();
	}

	@Override
	public void setupListeners() {
		//noinspection Duplicates
		new TextBoundModelListener(KnowtatorView.MODEL) {

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

	void setCollection(KnowtatorCollection<K> collection) {
		dispose();

		this.collection = collection;
		if (collection.size() == 0) {
			setEnabled(false);
		} else {
			setEnabled(true);
			removeActionListener(al);
			collection.forEach(this::addItem);
			addActionListener(al);
		}
	}

	void setSelected() {
		if (KnowtatorView.MODEL.isNotLoading()) {
			removeActionListener(al);
			collection.getSelection().ifPresent(this::setSelectedItem);
			addActionListener(al);
		}
	}

	@Override
	public void dispose() {
		removeAllItems();

		setSelectedItem(null);
	}

}
