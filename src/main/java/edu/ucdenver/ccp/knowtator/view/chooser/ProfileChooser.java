package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class ProfileChooser extends Chooser<Profile>
        implements ProfileCollectionListener, ViewListener {

    private ProfileCollection collection;

    public ProfileChooser(KnowtatorView view) {
        super(view, new Profile[0]);
        this.collection = view.getController().getProfileManager().getProfileCollection();
        collection.addListener(this);
        view.getController().getProfileManager().getProfileCollection().addListener(this);
        view.getController().addViewListener(this);
    }

    @Override
    public void projectLoaded() {

        setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new Profile[0])));
    }

    @Override
    public void viewChanged() {
        setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new Profile[0])));
        setSelectedItem(getView().getController()
                .getProfileManager().getSelection());
    }
}
