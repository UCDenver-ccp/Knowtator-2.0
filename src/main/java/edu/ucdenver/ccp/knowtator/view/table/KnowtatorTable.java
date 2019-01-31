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

package edu.ucdenver.ccp.knowtator.view.table;

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.util.Optional;

public abstract class KnowtatorTable<M extends ModelObject> extends JTable implements KnowtatorComponent, ModelListener {
	protected final KnowtatorView view;

	KnowtatorTable(KnowtatorView view) {
		this.view = view;
		setModel(new DefaultTableModel(0, 1));

		KnowtatorTable<M> table = this;

		addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					if (table.getSelectedRow() != -1) {
						reactToClick();
					}
				}
			}
		});

	}

	public abstract void reactToClick();
	protected abstract Optional<Object> getSelectedFromModel();
	Optional<M> getSelectedValue() {
		return Optional.ofNullable((M) getValueAt(getSelectedRow(), 0));
	}

	void addValue(M modelObject) {
		((DefaultTableModel) getModel()).addRow(new Object[]{modelObject});
	}

	private void setSelected() {
		getSelectedFromModel()
				.ifPresent(cell -> {
					for (int i = 0; i < getModel().getRowCount(); i++) {
						Object element = getModel().getValueAt(i, 0);

						if (element == cell) {
							setRowSelectionInterval(i, i);
							this.scrollRectToVisible(getCellRect(i, 0, true));
							return;
						}
					}
				});
	}

	public abstract void addElementsFromModel();

	@Override
	public void reset() {
		dispose();
		view.getModel().ifPresent(model -> model.addModelListener(this));
		addElementsFromModel();
		setSelected();
	}

	@Override
	public void dispose() {
		for (int i = 0; i < getRowCount(); i++) {
			((DefaultTableModel) getModel()).removeRow(i);
		}
		view.getModel().ifPresent(model -> model.removeModelListener(this));
	}

	public void reactToModelEvent() {
		((DefaultListModel) getModel()).clear();
		addElementsFromModel();
		setSelected();
	}

	@Override
	public void filterChangedEvent() {
		reactToModelEvent();
	}


	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		reactToModelEvent();
	}

	@Override
	public void colorChangedEvent() {
		reactToModelEvent();
	}
}
