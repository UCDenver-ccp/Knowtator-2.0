package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;

import java.util.TreeSet;

public class GraphSpaceCollection
		extends CyclableCollection<GraphSpace, GraphSpaceCollectionListener> {

	public GraphSpaceCollection() {
		super(new TreeSet<>(GraphSpace::compare));
	}
}
