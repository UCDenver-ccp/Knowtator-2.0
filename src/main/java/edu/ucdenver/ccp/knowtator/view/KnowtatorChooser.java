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

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

public abstract class KnowtatorChooser<K extends KnowtatorDataObjectInterface> extends JComboBox<K> implements KnowtatorCollectionListener<K>, KnowtatorComponent {

	private final ActionListener al;
	private KnowtatorCollection<K> collection;
	protected final KnowtatorView view;

	protected KnowtatorChooser(KnowtatorView view) {
		this.view = view;

		al = e -> {
			JComboBox comboBox = (JComboBox) e.getSource();
			if (comboBox.getSelectedItem() != null) {
				this.collection.setSelection(getItemAt(getSelectedIndex()));
			}
		};


	}

	protected void setCollection(KnowtatorCollection<K> collection) {
		if (this.collection != null) {
			this.collection.removeCollectionListener(this);
		}

        removeAllItems();

		this.collection = collection;
		if (collection != null) {
			this.collection.addCollectionListener(this);

			collection.forEach(this::addItem);

			if (!Arrays.asList(getActionListeners()).contains(al)) {
				addActionListener(al);
			}
		}
	}

	@Override
	public void dispose() {
		removeAllItems();
		collection = null;
		setSelectedItem(null);
	}

	@Override
	public void added(AddEvent<K> event) {
		removeAllItems();
		collection.forEach(this::addItem);
	}

	@Override
	public void removed(RemoveEvent<K> event) {
        removeAllItems();
		collection.forEach(this::addItem);
	}

	@Override
	public void emptied() {
		setEnabled(false);
	}

	@Override
	public void firstAdded() {
		setEnabled(true);
	}

	@Override
	public void changed(ChangeEvent<K> event) {
		removeActionListener(al);
		setSelectedItem(event.getNew());
		addActionListener(al);
	}

	@Override
	public void selected(SelectionChangeEvent<K> event) {
		removeActionListener(al);
		setSelectedItem(event.getNew());
		addActionListener(al);
	}
}
