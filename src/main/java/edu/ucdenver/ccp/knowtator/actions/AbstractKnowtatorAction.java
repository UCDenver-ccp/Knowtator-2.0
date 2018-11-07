package edu.ucdenver.ccp.knowtator.actions;

import javax.swing.undo.UndoableEdit;

public abstract class AbstractKnowtatorAction {

    private String presentationName;

    AbstractKnowtatorAction(String presentationName) {

        this.presentationName = presentationName;
    }

    String getPresentationName() {
        return presentationName;
    }

    public abstract void execute() throws ActionUnperformableException;

    public abstract UndoableEdit getEdit();

}
