package edu.ucdenver.ccp.knowtator.events;

import edu.ucdenver.ccp.knowtator.model.GraphSpace;

public class GraphSpaceChangeEvent extends ChangeEvent<GraphSpace> {

	public GraphSpaceChangeEvent(GraphSpace oldGraphSpace, GraphSpace newGraphSpace) {
		super(oldGraphSpace, newGraphSpace);
	}
}
