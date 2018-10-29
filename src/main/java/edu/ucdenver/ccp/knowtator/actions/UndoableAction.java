package edu.ucdenver.ccp.knowtator.actions;

abstract class UndoableAction {
    private boolean redoable;

    UndoableAction(boolean redoable) {
        this.redoable = redoable;
    }

    abstract void reverse();

    abstract void execute();

    boolean isRedoable() {
        return redoable;
    }
}
