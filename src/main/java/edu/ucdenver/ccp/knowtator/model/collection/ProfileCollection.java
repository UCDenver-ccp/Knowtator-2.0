package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.listeners.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;

import java.util.ArrayList;

public class ProfileCollection
		extends ListenableCollection<Profile, ArrayList<Profile>, ProfileCollectionListener> {

	public ProfileCollection() {
		super(new ArrayList<>());
	}
}
