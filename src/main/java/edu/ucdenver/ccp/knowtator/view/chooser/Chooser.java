package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.CollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;

public class Chooser<K extends KnowtatorObject> extends JComboBox<K> implements SelectionListener, ProjectListener, CollectionListener<K> {

	Chooser(KnowtatorView view) {
		view.getController().getSelectionManager().addListener(this);
		view.getController().getProjectManager().addListener(this);
	}

	Chooser(KnowtatorView view, K[] initialData) {
		super(initialData);
		view.getController().getSelectionManager().addListener(this);
		view.getController().getProjectManager().addListener(this);
	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {

	}

	@Override
	public void selectedSpanChanged(SpanChangeEvent e) {

	}

	@Override
	public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {

	}

	@Override
	public void activeTextSourceChanged(TextSourceChangeEvent e) {

	}

	@Override
	public void activeProfileChange(ProfileChangeEvent e) {

	}

	@Override
	public void owlPropertyChangedEvent(OWLObjectProperty value) {

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
