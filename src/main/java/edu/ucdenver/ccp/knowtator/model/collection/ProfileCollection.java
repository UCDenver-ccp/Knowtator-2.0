package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionModel;

import java.util.TreeSet;

public class ProfileCollection extends SelectionModel<Profile, ProfileCollectionListener> {

  private Profile defaultProfile;

  public ProfileCollection(KnowtatorController controller) {
    super(controller, new TreeSet<>(Profile::compare));
    defaultProfile = new Profile(controller, "Default");
    add(defaultProfile);
  }

  public Profile getDefaultProfile() {
    return defaultProfile;
  }

  @Override
  public void projectLoaded() {
    super.projectLoaded();
    add(defaultProfile);
  }
}
