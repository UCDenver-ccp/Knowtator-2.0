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

package edu.ucdenver.ccp.knowtator.view.actions;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public abstract class AbstractKnowtatorAction extends AbstractUndoableEdit implements mxEventSource.mxIEventListener {

    protected final KnowtatorModel model;
    private final String presentationName;
    private String message;

    private final mxUndoManager mxUndoManager;
    private final UndoManager undoManager;

    protected AbstractKnowtatorAction(KnowtatorModel model, String presentationName) {
        this.model = model;
        this.message = "";
        mxUndoManager = new mxUndoManager();
        undoManager = new UndoManager();
        this.presentationName = presentationName;
    }

    public abstract void execute() throws ActionUnperformableException;


    protected void setMessage(String message) {
        this.message = message;
    }

    protected String getMessage() {
        return message;
    }

    @Override
    public void undo() {
        super.undo();
        while (undoManager.canUndo()) {
            undoManager.undo();
        }
        while(mxUndoManager.canUndo()) {
            mxUndoManager.undo();
        }
    }

    @Override
    public void redo() {
        super.redo();
        while (undoManager.canRedo()) {
            undoManager.redo();
        }
        while(mxUndoManager.canRedo()) {
            mxUndoManager.redo();
        }
    }

    @Override
    public String getPresentationName() {
        return this.presentationName;
    }

    @Override
    public void invoke(Object sender, mxEventObject evt) {
        addmxUndoableEdit(evt);
    }

    private void addmxUndoableEdit(mxEventObject edit) {
        mxUndoManager.undoableEditHappened((mxUndoableEdit) edit.getProperty("edit"));
    }

    protected void addKnowtatorEdit(UndoableEdit edit) {
        undoManager.addEdit(edit);
    }
}
