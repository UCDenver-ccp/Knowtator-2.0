package edu.ucdenver.ccp.knowtator.actions;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;

import javax.swing.undo.AbstractUndoableEdit;

public abstract class KnowtatorAction extends AbstractUndoableEdit {

    private final String presentationName;
    private mxUndoManager mxUndoManager;

    KnowtatorAction(String presentationName) {
        this.presentationName = presentationName;
        mxUndoManager = new mxUndoManager();
    }

    @Override
    public void undo(){
        while(mxUndoManager.canUndo()) {
            mxUndoManager.undo();
        }
    }

    @Override
    public void redo(){
        while(mxUndoManager.canRedo()) {
            mxUndoManager.redo();
        }
    }

    @Override
    public String getPresentationName() {
        return this.presentationName;
    }

    void execute() {
        redo();
    }

    public void addmxUndoableEdit(mxEventObject edit) {
        mxUndoManager.undoableEditHappened((mxUndoableEdit) edit.getProperty("edit"));
    }
}
