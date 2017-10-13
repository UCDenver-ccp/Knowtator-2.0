package edu.ucdenver.ccp.knowtator.listeners;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

public interface OwlSelectionListener {
    void owlClassSelectionChanged(OWLClass cls);
    void owlEntitySelectionChanged(OWLEntity ent);
}
