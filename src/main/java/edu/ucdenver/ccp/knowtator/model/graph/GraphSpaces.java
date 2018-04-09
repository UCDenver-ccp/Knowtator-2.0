package edu.ucdenver.ccp.knowtator.model.graph;

import edu.ucdenver.ccp.knowtator.listeners.GraphSpacesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class GraphSpaces {
    private TreeSet<GraphSpace> graphSpaces;

    private List<GraphSpacesListener> listeners;

    public GraphSpaces() {
        this.graphSpaces = new TreeSet<>(GraphSpace::compare);
        listeners = new ArrayList<>();
    }

    public TreeSet<GraphSpace> getGraphSpaces() {
        return graphSpaces;
    }

    public void addListener(GraphSpacesListener listener) {
        listeners.add(listener);
    }

    public void add(GraphSpace graphSpace) {
        graphSpaces.add(graphSpace);
        listeners.forEach(graphSpacesListener -> graphSpacesListener.GraphSpacesChanged(graphSpace, true));
    }

    public void remove(GraphSpace graphSpace) {
        graphSpaces.remove(graphSpace);
        listeners.forEach(graphSpacesListener -> graphSpacesListener.GraphSpacesChanged(graphSpace, false));

    }

    public void removeAllListeners() {
        listeners.clear();
    }
}
