package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.DebugListener;

import javax.swing.undo.UndoManager;
import java.util.ArrayList;
import java.util.List;

public abstract class DebugManager extends UndoManager {

    public boolean isDebug() {
        return debug;
    }

    private boolean debug;
    private final List<DebugListener> debugListeners;

    DebugManager() {
        debug = false;
        debugListeners = new ArrayList<>();
    }

    public void addDebugListener(DebugListener listener) {
        debugListeners.add(listener);
    }

    void setDebug(boolean debug) {
        this.debug = debug;
        debugListeners.forEach(listener -> listener.setDebug(debug));
    }


}
