package edu.ucdenver.ccp.knowtator.listeners;

import org.semanticweb.owlapi.model.OWLEntity;

public interface OWLClassSelectionListener {
	void owlEntityChanged(OWLEntity owlClass);
}
