package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.ProfileChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class ProfileChooser extends Chooser<Profile> implements ProfileCollectionListener, ProfileSelectionListener {

	private ProfileCollection collection;

	public ProfileChooser(KnowtatorView view) {
		super(view, new Profile[0]);
	}
	
	public void setController(KnowtatorController controller) {
		this.collection = controller.getProfileManager().getProfileCollection();
		controller.getSelectionManager().addProfileListener(this);
		controller.getProfileManager().getProfileCollection().addListener(this);
	}

	@Override
	public void activeProfileChange(ProfileChangeEvent e) {
		setSelectedItem(e.getNew());
	}

	@Override
	public void projectLoaded() {
		collection.addListener(this);
		setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new Profile[0])));
		collection.addListener(this);
	}
}
