package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.DebugListener;

import java.util.ArrayList;
import java.util.List;

public abstract class DebugManager {

    public boolean isDebug() {
        return debug;
    }

    private boolean debug;
    private List<DebugListener> debugListeners;

    protected DebugManager() {
        debug = false;
        debugListeners = new ArrayList<>();
    }

    public void addDebugListener(DebugListener listener) {
        debugListeners.add(listener);
    }

    void removeDebugListener(DebugListener listener) {
        debugListeners.remove(listener);
    }

    void setDebug(boolean debug) {
        this.debug = debug;
        debugListeners.forEach(listener -> listener.setDebug(debug));
    }


}
