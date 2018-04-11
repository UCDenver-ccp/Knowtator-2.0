package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.ProfileChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;

import javax.swing.*;

public class ProfileChooser extends Chooser<Profile> implements ProfileCollectionListener {

	private ProfileCollection collection;

	public ProfileChooser(KnowtatorController controller) {
		super(
				controller,
				controller.getProfileManager().getProfileCollection().getData().toArray(new Profile[0]));
		this.collection = controller.getProfileManager().getProfileCollection();
		controller.getProfileManager().getProfileCollection().addListener(this);
	}

	@Override
	public void activeProfileChange(ProfileChangeEvent e) {
		setSelectedItem(e.getNew());
	}

	@Override
	public void projectLoaded() {
		collection.addListener(this);
		setModel(new DefaultComboBoxModel<>(collection.getData().toArray(new Profile[0])));
		collection.addListener(this);
	}
}
