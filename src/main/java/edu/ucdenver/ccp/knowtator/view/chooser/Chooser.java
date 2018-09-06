package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.collection.CollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class Chooser<K extends KnowtatorObject> extends JComboBox<K> implements ProjectListener, CollectionListener<K> {

	public KnowtatorView getView() {
		return view;
	}

	private KnowtatorView view;

	Chooser(KnowtatorView view) {
		view.getProjectManager().addListener(this);
		this.view = view;
	}

	Chooser(KnowtatorView view, K[] initialData) {
		super(initialData);
		this.view = view;
		view.getProjectManager().addListener(this);
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
	public void projectClosed() {
		removeAllItems();
	}

	@Override
	public void projectLoaded() {

	}
}
