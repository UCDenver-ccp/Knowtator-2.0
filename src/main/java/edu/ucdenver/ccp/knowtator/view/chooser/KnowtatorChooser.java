package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class KnowtatorChooser<K extends KnowtatorObjectInterface> extends JComboBox<K> implements KnowtatorCollectionListener<K>, KnowtatorViewComponent {

	private ActionListener al;
	private KnowtatorCollection<K> collection;
	KnowtatorView view;

	KnowtatorChooser(KnowtatorView view) {
		this.view = view;

		al = e -> {
			JComboBox comboBox = (JComboBox) e.getSource();
			if (comboBox.getSelectedItem() != null) {
				this.collection.setSelection(getItemAt(getSelectedIndex()));
			}
		};


	}

	public void setCollection(KnowtatorCollection<K> collection) {
		if (this.collection != null) {
			this.collection.removeCollectionListener(this);
			this.collection.forEach(this::removeItem);
		}

		this.collection = collection;
		this.collection.addCollectionListener(this);

		collection.forEach(this::addItem);

		for (ActionListener a : getActionListeners()) {
			removeActionListener(a);
		}

		addActionListener(al);
	}

	public void dispose() {
		removeAllItems();
	}

	@Override
	public void added(AddEvent<K> event) {
		addItem(event.getAdded());
	}

	@Override
	public void removed(RemoveEvent<K> event) {
		removeItem(event.getRemoved());
	}

	@Override
	public void emptied(RemoveEvent<K> event) {
		setEnabled(false);
	}

	@Override
	public void firstAdded(AddEvent<K> event) {
		setEnabled(true);
	}

	@Override
	public void changed(ChangeEvent<K> event) {
	}

	@Override
	public void selected(SelectionChangeEvent<K> event) {
		removeActionListener(al);
		setSelectedItem(event.getNew());
		addActionListener(al);
	}

	@Override
	public void updated(K updatedItem) {
		removeActionListener(al);
		setSelectedItem(updatedItem);
		addActionListener(al);
	}
}
