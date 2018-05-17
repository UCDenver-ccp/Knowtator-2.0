package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.TextSource;

public interface GraphSpaceListener {
	void graphTextChanged(TextSource textSource, int start, int end);
	void annotationNodeAdded(GraphSpace graphSpace, AnnotationNode node);
	void annotationNodeRemoved(GraphSpace graphSpace, AnnotationNode node);
}
