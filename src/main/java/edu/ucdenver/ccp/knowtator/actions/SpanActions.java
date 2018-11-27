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

package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.textpane.KnowtatorTextPane;

import javax.swing.undo.UndoableEdit;
import java.util.Arrays;

public class SpanActions {
    public final static String GROW = "grow";
    public final static String SHRINK = "shrink";
    public final static String START = "start";
    public final static String END = "end";

    public static class ModifySpanAction extends AbstractKnowtatorAction {

        private final Span span;
        private final int startModification;
        private final int endModification;
        private final String startOrEnd;
        private final String growOrShrink;
        private boolean spanStartChanged;
        private boolean spanEndChanged;

        public ModifySpanAction(String startOrEnd, String growOrShrink, Span span) {
            super("Modify span");
            this.startOrEnd = startOrEnd;
            this.growOrShrink = growOrShrink;
            this.span = span;
            startModification = getStartModification(startOrEnd, growOrShrink);
            endModification = getEndModification(startOrEnd, growOrShrink);
            spanStartChanged = false;
            spanEndChanged = false;
        }

        @Override
        public void execute() {
            int spanStart = span.getStart();
            int spanEnd = span.getEnd();
            span.modify(Arrays.asList(startModification, endModification));
            spanStartChanged = spanStart != span.getStart();
            spanEndChanged = spanEnd != span.getEnd();
        }

        @Override
        public UndoableEdit getEdit() {
            return new KnowtatorEdit(getPresentationName()) {
                private int startModification;
                private int endModification;

                @Override
                public void undo() {
                    super.undo();
                    switch (growOrShrink) {
                        case GROW:
                            startModification = spanStartChanged ? getStartModification(startOrEnd, SHRINK) : 0;
                            endModification = spanEndChanged ? getEndModification(startOrEnd, SHRINK) : 0;
                            span.modify(Arrays.asList(startModification, endModification));
                            break;
                        case SHRINK:
                            startModification = spanStartChanged ? getStartModification(startOrEnd, GROW) : 0;
                            endModification = spanEndChanged ? getEndModification(startOrEnd, GROW) : 0;
                            span.modify(Arrays.asList(startModification, endModification));
                            break;
                    }
                }

                @Override
                public void redo() {
                    super.redo();
                    execute();
                }
            };
        }
    }

    private static int getEndModification(String startOrEnd, String growOrShrink) {
        return startOrEnd.equals(END) ?
                growOrShrink.equals(GROW) ?
                        +1 : growOrShrink.equals(SHRINK) ?
                        -1 : 0 : 0;
    }

    private static int getStartModification(String startOrEnd, String growOrShrink) {
        return startOrEnd.equals(START) ?
                growOrShrink.equals(GROW) ?
                        -1 : growOrShrink.equals(SHRINK) ?
                        +1 : 0 : 0;
    }


    public static void modifySelection(KnowtatorView view, String startOrEnd, String growOrShrink) {
        int startModification = getStartModification(startOrEnd, growOrShrink);
        int endModification = getEndModification(startOrEnd, growOrShrink);
        KnowtatorTextPane textPane = view.getKnowtatorTextPane();
        textPane.modifySelection(startModification, endModification);
    }
}
