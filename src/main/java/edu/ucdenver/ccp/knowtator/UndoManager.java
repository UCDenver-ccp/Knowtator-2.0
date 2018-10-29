package edu.ucdenver.ccp.knowtator;

import java.util.Stack;

public abstract class UndoManager extends DebugManager {

    private Stack<UndoEvent> undoEvents;
    private Stack<UndoEvent> redoEvents;

    UndoManager() {
        undoEvents = new Stack<>();
        redoEvents = new Stack<>();
    }

    public void addUndoEvent(UndoEvent event) {
        undoEvents.push(event);
        redoEvents.clear();
    }

    private void addRedoEvent(UndoEvent event) {
        redoEvents.push(event);
    }

    public void undo() {
        UndoEvent lastEvent = undoEvents.pop();

        lastEvent.reverse();

        addRedoEvent(lastEvent);
    }

    public void redo() {
        UndoEvent nextEvent = redoEvents.pop();

        nextEvent.execute();

        undoEvents.push(nextEvent);
    }
}
