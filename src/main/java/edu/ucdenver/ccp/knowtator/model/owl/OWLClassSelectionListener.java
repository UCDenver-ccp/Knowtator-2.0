package edu.ucdenver.ccp.knowtator.model.owl;

import org.semanticweb.owlapi.model.OWLEntity;

public interface OWLClassSelectionListener {
	void owlEntityChanged(OWLEntity owlClass);
}
