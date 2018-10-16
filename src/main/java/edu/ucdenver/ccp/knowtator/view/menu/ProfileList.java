package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorList;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class ProfileList extends KnowtatorList<Profile> {
    ProfileList(KnowtatorView view) {
        super(view);

        view.getController().getProfileCollection().addCollectionListener(this);
        setCollection(view.getController().getProfileCollection());
    }

    @Override
    protected void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {

    }

    @Override
    public void selected(SelectionChangeEvent<Profile> event) {
        for (int i = 0; i < getModel().getSize(); i++) {
            Profile profile = getModel().getElementAt(i);
            if (profile == event.getNew()) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    // I am overriding here because the base method adds this as a collection listener to its collection,
    // but that generates a concurrent modification exception during "added" events
    @Override
    protected void setCollection(KnowtatorCollection<Profile> collection) {
        dispose();
        this.collection = collection;
        collection.forEach(k -> ((DefaultListModel<Profile>) getModel()).addElement(k));
    }

    @Override
    public void added(AddEvent<Profile> event) {
        setCollection(view.getController().getProfileCollection());
    }

    @Override
    public void removed(RemoveEvent<Profile> event) {
        setCollection(view.getController().getProfileCollection());
    }

    @Override
    public void changed(ChangeEvent<Profile> event) {
        setCollection(view.getController().getProfileCollection());
    }

    @Override
    public void emptied() {
        setCollection(view.getController().getProfileCollection());
    }

    @Override
    public void firstAdded() {
        setCollection(view.getController().getProfileCollection());
    }
}
