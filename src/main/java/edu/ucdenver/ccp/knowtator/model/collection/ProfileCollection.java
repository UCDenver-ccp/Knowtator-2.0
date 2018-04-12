package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;

import java.util.TreeSet;

public class ProfileCollection extends CyclableCollection<Profile, ProfileCollectionListener> {

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
