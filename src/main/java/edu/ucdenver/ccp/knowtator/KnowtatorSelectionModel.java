package edu.ucdenver.ccp.knowtator;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class KnowtatorSelectionModel {

    private OWLClass selectedClass;

    private ArrayList<KnowtatorSelectionListener> listeners;


    KnowtatorSelectionModel() {
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

    public void addSelectionListener(KnowtatorSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionListener(KnowtatorSelectionListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectionChanged() {
        for (KnowtatorSelectionListener lsnr : listeners) {
            lsnr.selectionChanged(this);
        }
    }


}


