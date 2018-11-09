/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.DebugListener;

import javax.swing.undo.UndoManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles setting debugging
 */
public abstract class DebugManager extends UndoManager {

    /**
     * @return True if debugging mode is on
     */
    public boolean isDebug() {
        return debug;
    }

    private boolean debug;
    private final List<DebugListener> debugListeners;

    /**
     * Constructor for the debug manager. Defaults to debugging is off
     */
    DebugManager() {
        debug = false;
        debugListeners = new ArrayList<>();
    }

    /**
     * Adds a debug listener
     * @param listener A debug listener
     */
    public void addDebugListener(DebugListener listener) {
        debugListeners.add(listener);
    }

    /**
     * Sets the debugging mode. Notifies debug listeners
     * @param debug The debug mode
     */
    void setDebug(boolean debug) {
        this.debug = debug;
        debugListeners.forEach(listener -> listener.setDebug(debug));
    }


}
