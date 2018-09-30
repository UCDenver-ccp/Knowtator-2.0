package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.*;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class KnowtatorChooser<K extends KnowtatorObjectInterface> extends JComboBox<K> implements KnowtatorCollectionListener<K>, KnowtatorComponent {

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
