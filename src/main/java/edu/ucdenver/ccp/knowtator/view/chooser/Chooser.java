package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class Chooser<K extends KnowtatorObject> extends JComboBox<K> implements KnowtatorCollectionListener<K>, KnowtatorViewComponent {

	private ActionListener al;
	private KnowtatorCollection<K> collection;
	KnowtatorView view;

	Chooser(KnowtatorView view) {
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
	public void added(K addedObject) {
		addItem(addedObject);
	}

	@Override
	public void removed(K removedObject) {
		removeItem(removedObject);
	}

	@Override
	public void emptied(K object) {
		setEnabled(false);
	}

	@Override
	public void firstAdded(K object) {
		setEnabled(true);
	}

	@Override
	public void noSelection(K previousSelection) {
		setSelectedIndex(0);
	}

	@Override
	public void selected(K previousSelection, K currentSelection) {
		removeActionListener(al);
		setSelectedItem(currentSelection);
		addActionListener(al);
	}

	@Override
	public void updated(K updatedItem) {
		removeActionListener(al);
		setSelectedItem(updatedItem);
		addActionListener(al);
	}
}
