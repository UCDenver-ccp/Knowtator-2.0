package edu.ucdenver.ccp.knowtator.actions;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;

import javax.swing.undo.AbstractUndoableEdit;

public abstract class KnowtatorEdit extends AbstractUndoableEdit implements mxEventSource.mxIEventListener {

    private final String presentationName;
    private mxUndoManager mxUndoManager;

    KnowtatorEdit(String presentationName) {
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

    @Override
    public void invoke(Object sender, mxEventObject evt) {
        addmxUndoableEdit(evt);
    }

    private void addmxUndoableEdit(mxEventObject edit) {
        mxUndoManager.undoableEditHappened((mxUndoableEdit) edit.getProperty("edit"));
    }
}