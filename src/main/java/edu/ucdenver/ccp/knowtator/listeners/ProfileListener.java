package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.profile.Profile;

public interface ProfileListener extends Listener {
    void profileAdded(Profile profile);

    void profileRemoved();

    void profileSelectionChanged(Profile profile);

    void profileFilterSelectionChanged(boolean filterByProfile);

    void colorChanged();
}
