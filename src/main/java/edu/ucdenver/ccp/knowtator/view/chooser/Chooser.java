package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.CollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;

public class Chooser<objectType> extends JComboBox<objectType> implements SelectionListener, ProjectListener, CollectionListener<objectType> {

	Chooser(KnowtatorController controller) {
		controller.getSelectionManager().addListener(this);
		controller.getProjectManager().addListener(this);
	}

	Chooser(KnowtatorController controller, objectType[] initialData) {
		super(initialData);
		controller.getSelectionManager().addListener(this);
		controller.getProjectManager().addListener(this);
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
	public void added(objectType addedObject) {
		addItem(addedObject);
	}

	@Override
	public void removed(objectType removedObject) {
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
