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
