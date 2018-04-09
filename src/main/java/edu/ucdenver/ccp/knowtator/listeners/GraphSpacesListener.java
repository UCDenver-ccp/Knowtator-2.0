package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;

public interface GraphSpacesListener {
    void GraphSpacesChanged(GraphSpace graphSpace, Boolean wasAdded);
}
