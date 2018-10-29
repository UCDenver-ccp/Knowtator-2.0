package edu.ucdenver.ccp.knowtator.actions;

import javax.swing.undo.AbstractUndoableEdit;

public abstract class KnowtatorAction extends AbstractUndoableEdit {

    private final String presentationName;

    KnowtatorAction(String presentationName) {
        this.presentationName = presentationName;
    }

    @Override
    public String getPresentationName() {
        return this.presentationName;
    }

    void execute() {
        redo();
    }

}
