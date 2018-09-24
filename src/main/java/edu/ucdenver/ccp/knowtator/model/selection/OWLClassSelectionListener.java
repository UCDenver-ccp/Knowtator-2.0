package edu.ucdenver.ccp.knowtator.model.selection;

import org.semanticweb.owlapi.model.OWLEntity;

public interface OWLClassSelectionListener {
	void owlEntityChanged(OWLEntity owlClass);
}
