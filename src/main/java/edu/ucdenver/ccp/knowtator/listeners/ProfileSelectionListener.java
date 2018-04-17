package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.events.ProfileChangeEvent;

public interface ProfileSelectionListener {
	void activeProfileChange(ProfileChangeEvent e);
}
