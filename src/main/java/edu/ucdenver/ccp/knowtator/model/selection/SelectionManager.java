package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.OWLClassSelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectionManager implements CaretListener, ProjectListener, KnowtatorManager {

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

    public SelectionManager(KnowtatorController knowtatorController) {
        controller = knowtatorController;
        controller.addProjectListener(this);
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
    public File getSaveLocation(String extension) {
        return null;
    }

    @Override
    public void setSaveLocation(File newSaveLocation, String extension) {

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
    public void writeToKnowtatorXML(Document dom, Element parent) {

    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {

    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {

    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    @Override
    public void save() {

    }
}
