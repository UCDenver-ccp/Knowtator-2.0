package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.listeners.OWLClassSelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager extends KnowtatorManager implements CaretListener, ProjectListener {

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

    private boolean filterByOWLClass;

    public SelectionManager(KnowtatorController controller) {
        this.controller = controller;
        this.controller.addProjectListener(this);
        filterByProfile = false;
        filterByOWLClass = false;
        owlEntityListeners = new ArrayList<>();

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
        this.activeProfile = newProfile;
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
        if (controller.isProjectLoaded() && activeTextSource != newTextSource) {
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
        if (controller.isProjectLoaded()) {
            setActiveTextSource(
                    controller.getTextSourceManager().getTextSourceCollection().getNext(activeTextSource));
        }
    }

    public void getPreviousTextSource() {
        if (controller.isProjectLoaded()) {
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


    public void addOWLEntityListener(OWLClassSelectionListener listener) {
        owlEntityListeners.add(listener);
    }

    public void dispose() {
        owlEntityListeners.clear();
    }

    @Override
    public void makeDirectory() {

    }

    @Override
    public File getSaveLocation() {
        return null;
    }

    @Override
    public void setSaveLocation(File saveLocation) {

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


    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
