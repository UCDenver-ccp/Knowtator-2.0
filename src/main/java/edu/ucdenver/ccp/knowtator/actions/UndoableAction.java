package edu.ucdenver.ccp.knowtator.actions;

abstract class UndoableAction {
    private boolean redoable;
    private String actionText;

    UndoableAction(boolean redoable, String actionText) {
        this.redoable = redoable;
        this.actionText = actionText;
    }

    abstract void reverse();

    abstract void execute();

    boolean isRedoable() {
        return redoable;
    }

    String getActionText() {
        return actionText;
    }

    static class CancelAction extends UndoableAction {

        CancelAction() {
            super(true, "Cancel");
        }

        @Override
        void reverse() {

        }

        @Override
        void execute() {

        }
    }
}
