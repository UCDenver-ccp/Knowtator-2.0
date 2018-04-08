package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;

public interface GraphListener {
    void newGraph(GraphSpace graphSpace);

    void graphSpaceRemoved(GraphSpace graphSpace);
}


