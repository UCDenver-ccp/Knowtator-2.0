package edu.ucdenver.ccp.knowtator.actions;

import javax.swing.undo.UndoableEdit;

public abstract class KnowtatorAction {

    private String presentationName;

    KnowtatorAction(String presentationName) {

        this.presentationName = presentationName;
    }

    String getPresentationName() {
        return presentationName;
    }

    public abstract void execute();

    public abstract UndoableEdit getEdit();
}
