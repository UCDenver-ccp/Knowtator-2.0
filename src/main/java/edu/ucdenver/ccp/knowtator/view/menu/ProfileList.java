/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
