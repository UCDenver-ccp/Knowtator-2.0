package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager implements CaretListener, KnowtatorManager {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SelectionManager.class);

    private KnowtatorController controller;

    private boolean filterByProfile;
    private OWLEntity selectedOWLEntity;
    private List<OWLClassSelectionListener> owlEntityListeners;

    private int start;
    private int end;

    private boolean filterByOWLClass;

    public SelectionManager(KnowtatorController controller) {
        this.controller = controller;

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


    public void setSelectedOWLEntity(OWLEntity owlClass) {
        selectedOWLEntity = owlClass;
        owlEntityListeners.forEach(listener -> listener.owlEntityChanged(owlClass));
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        setStart(Math.min(e.getDot(), e.getMark()));
        setEnd(Math.max(e.getDot(), e.getMark()));
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
