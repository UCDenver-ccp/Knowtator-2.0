package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.ProfileChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.OWLClassSelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileSelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager implements CaretListener, ProjectListener {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SelectionManager.class);

    private KnowtatorController controller;

    private TextSource activeTextSource;
    private Profile activeProfile;
    private boolean filterByProfile;
    private OWLEntity selectedOWLEntity;
    private List<OWLClassSelectionListener> owlEntityListeners;

    private int start;
    private int end;

    private List<ProfileSelectionListener> profileListeners;
    private boolean filterByOWLClass;

    public SelectionManager(KnowtatorController knowtatorController) {
        controller = knowtatorController;
        controller.getProjectManager().addListener(this);
        filterByProfile = false;
        filterByOWLClass = false;
        owlEntityListeners = new ArrayList<>();
        profileListeners = new ArrayList<>();

        start = 0;
        end = 0;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public void setSelectedProfile(Profile newProfile) {
        ProfileChangeEvent e = new ProfileChangeEvent(this.activeProfile, newProfile);
        this.activeProfile = newProfile;
        profileListeners.forEach(selectionListener -> selectionListener.activeProfileChange(e));
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public OWLEntity getSelectedOWLEntity() {
        return selectedOWLEntity;
    }

    public boolean isFilterByProfile() {
        return filterByProfile;
    }

    public TextSource getActiveTextSource() throws ActiveTextSourceNotSetException {
        if (activeTextSource == null) {
            throw new ActiveTextSourceNotSetException();
        }
        return activeTextSource;
    }

    public void setActiveTextSource(TextSource newTextSource) {
        if (controller.getProjectManager().isProjectLoaded() && activeTextSource != newTextSource) {
            this.activeTextSource = newTextSource;
            controller.refreshView();
        }
    }

    public void setSelectedOWLEntity(OWLEntity owlClass) {
        selectedOWLEntity = owlClass;
        owlEntityListeners.forEach(listener -> listener.owlEntityChanged(owlClass));
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        setStart(Math.min(e.getDot(), e.getMark()));
        setEnd(Math.max(e.getDot(), e.getMark()));
    }


    public void getNextTextSource() {
        if (controller.getProjectManager().isProjectLoaded()) {
            setActiveTextSource(
                    controller.getTextSourceManager().getTextSourceCollection().getNext(activeTextSource));
        }
    }

    public void getPreviousTextSource() {
        if (controller.getProjectManager().isProjectLoaded()) {
            setActiveTextSource(
                    controller
                            .getTextSourceManager()
                            .getTextSourceCollection()
                            .getPrevious(activeTextSource));
        }
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void projectLoaded() {
        setActiveTextSource(
                controller.getTextSourceManager().getTextSourceCollection().getCollection().first());
    }


    public void addProfileListener(ProfileSelectionListener listener) {
        profileListeners.add(listener);
    }


    public void addOWLEntityListener(OWLClassSelectionListener listener) {
        owlEntityListeners.add(listener);
    }

    public void dispose() {
        owlEntityListeners.clear();
        profileListeners.clear();
    }

    public void setFilterByProfile(boolean filterByProfile) {
        this.filterByProfile = filterByProfile;
        controller.refreshView();
    }

    public void setFilterByOWLClass(boolean filterByOWLClass) {
        this.filterByOWLClass = filterByOWLClass;
        controller.refreshView();
    }

    public boolean isFilterByOWLClass() {
        return filterByOWLClass;
    }
}
