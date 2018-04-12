package edu.ucdenver.ccp.knowtator.listeners;

import org.semanticweb.owlapi.model.OWLClass;

public interface OWLClassSelectionListener {
	void owlClassChanged(OWLClass owlClass);
}
