package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

class ProfileActions {
    static void addProfile(KnowtatorView view, String profileName) {
            view.getController().getProfileCollection().addProfile(profileName);
    }

    static void removeProfile(KnowtatorView view) {
        view.getController().getProfileCollection().removeSelected();
    }
}
