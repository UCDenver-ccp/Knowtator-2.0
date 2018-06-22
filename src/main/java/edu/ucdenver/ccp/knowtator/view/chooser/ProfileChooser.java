package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.Profile;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class ProfileChooser extends Chooser<Profile>
    implements ProfileCollectionListener, ViewListener {

  private ProfileCollection collection;

  public ProfileChooser(KnowtatorView view) {
    super(view, new Profile[0]);
  }

  public void setController(KnowtatorController controller) {
    this.collection = controller.getProfileManager().getProfileCollection();
    collection.addListener(this);
    controller.getProfileManager().getProfileCollection().addListener(this);
    controller.addViewListener(this);
  }

  @Override
  public void projectLoaded() {

    setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new Profile[0])));
  }

  @Override
  public void viewChanged() {
    try {
      setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new Profile[0])));
      setSelectedItem(getView().getController().getSelectionManager().getActiveProfile());
    } catch (ControllerNotSetException ignored) {

    }
  }
}
