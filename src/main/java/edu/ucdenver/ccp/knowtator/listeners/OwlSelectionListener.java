package edu.ucdenver.ccp.knowtator.listeners;

import org.semanticweb.owlapi.model.OWLEntity;

public interface OwlSelectionListener {
    void owlEntitySelectionChanged(OWLEntity ent);
}
