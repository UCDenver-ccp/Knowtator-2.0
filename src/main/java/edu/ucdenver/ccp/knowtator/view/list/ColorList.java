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

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

import static edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction.assignColorToClass;

public class ColorList extends JList<OWLClass> implements KnowtatorComponent, ModelListener {

	private final ListSelectionListener lsl;
	private final KnowtatorView view;

	public ColorList(KnowtatorView view) {
		setModel(new DefaultListModel<>());
		this.view = view;
		setCellRenderer(new ColorListRenderer<>());
		lsl = e -> assignColorToClass(view, getSelectedValue());
	}

	private void setCollection() {
		removeListSelectionListener(lsl);
		setModel(new DefaultListModel<>());
		view.getModel().ifPresent(model -> model.getSelectedProfile()
				.ifPresent(profile -> profile.getColors().keySet().stream()
						.sorted(model.getOWLObjectComparator())
						.forEach(o -> ((DefaultListModel<OWLClass>) getModel()).addElement(o))));

		addListSelectionListener(lsl);
	}

	@Override
	public void reset() {
		view.getModel().ifPresent(model -> model.addModelListener(this));
		setCollection();
	}

	@Override
	public void dispose() {
		view.getModel().ifPresent(model -> model.removeModelListener(this));
		setModel(new DefaultListModel<>());
	}

	@Override
	public void filterChangedEvent() {

	}


	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		event.getNew()
				.filter(modelObject -> modelObject instanceof Profile)
				.ifPresent(modelObject -> setCollection());
	}

	@Override
	public void colorChangedEvent() {
		setCollection();
	}

	class ColorListRenderer<o> extends JLabel implements ListCellRenderer<o> {

		ColorListRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			view.getModel()
					.filter(model -> value instanceof OWLClass)
					.ifPresent(model -> {
						model.getSelectedProfile().ifPresent(profile -> setBackground(profile.getColors().get(value)));
						setText(model.getOWLEntityRendering((OWLEntity) value));
					});
			return this;
		}
	}
}
