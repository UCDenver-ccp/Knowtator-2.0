package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;

import javax.swing.*;

public class ProfileChooser extends JComboBox<Profile>
		implements ProfileCollectionListener, SelectionListener {

	private KnowtatorController controller;

	ProfileChooser(KnowtatorController controller) {
		super(controller.getProfileManager().getProfileCollection().getData().toArray(new Profile[0]));
		this.controller = controller;
		controller.getProfileManager().getProfileCollection().addListener(this);
	}

	@Override
	public void added(Profile profile) {
		addItem(profile);
	}

	@Override
	public void removed(Profile profile) {
		removeItem(profile);
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
		setSelectedItem(controller.getSelectionManager().getActiveProfile());
	}
}
