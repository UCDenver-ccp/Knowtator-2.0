package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionModel;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;

import java.util.TreeSet;

public class GraphSpaceCollection
		extends SelectionModel<GraphSpace, GraphSpaceCollectionListener> {

	public GraphSpaceCollection(KnowtatorController controller) {
		super(controller, new TreeSet<>(GraphSpace::compare));
	}
}
