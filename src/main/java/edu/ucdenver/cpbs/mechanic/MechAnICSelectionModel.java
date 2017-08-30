package edu.ucdenver.cpbs.mechanic;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class MechAnICSelectionModel {

    private OWLClass selectedClass;

    private ArrayList<MechAnICSelectionListener> listeners;


    MechAnICSelectionModel() {
        listeners = new ArrayList<>();
    }


    public OWLClass getSelectedClass() {
        return selectedClass;

    }


    void setSelectedClass(OWLClass selectedClass) {
        boolean changed = false;
        if (selectedClass != null) {
            if (this.selectedClass == null) {
                changed = true;
            }
            else {
                if (!selectedClass.equals(this.selectedClass)) {
                    changed = true;
                }
            }
        }
        else {
            if (this.selectedClass != null) {
                changed = true;
            }
        }
        this.selectedClass = selectedClass;
        if (changed) {
            fireSelectionChanged();
        }
    }

    public void addSelectionListener(MechAnICSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionListener(MechAnICSelectionListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectionChanged() {
        for (MechAnICSelectionListener lsnr : listeners) {
            lsnr.selectionChanged(this);
        }
    }


}


