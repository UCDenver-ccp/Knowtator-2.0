package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.DebugManager;

import java.util.Stack;

public abstract class UndoManager extends DebugManager {

    private Stack<UndoableAction> undoableActions;
    private Stack<UndoableAction> redoableActions;

    protected UndoManager() {
        super();
        undoableActions = new Stack<>();
        redoableActions = new Stack<>();
    }

    public void registerUndoEvent(UndoableAction action) {
        if (action != null) {
            action.execute();
            undoableActions.push(action);
            redoableActions.clear();
        }
    }

    private void addRedoAction(UndoableAction action) {
        redoableActions.push(action);
    }

    public void undo() {
        UndoableAction lastAction = undoableActions.pop();

        lastAction.reverse();

        addRedoAction(lastAction);
    }

    public void redo() {
        UndoableAction nextAction = redoableActions.pop();
        if (nextAction.isRedoable()) {
            nextAction.execute();
        }

        undoableActions.push(nextAction);
    }
}
