package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.undo.UndoableEdit;

public class SpanActions {
    public final static String GROW = "grow";
    public final static String SHRINK = "shrink";
    public final static String START = "start";
    public final static String END = "end";

    public static class ModifySpanAction extends AbstractKnowtatorAction {

        private final Span span;
        private final int startModification;
        private final int endModification;
        private String startOrEnd;
        private String growOrShrink;

        public ModifySpanAction(KnowtatorController controller, String startOrEnd, String growOrShrink) throws NoSelectionException {
            super("Modify span");
            this.startOrEnd = startOrEnd;
            this.growOrShrink = growOrShrink;
            startModification = getStartModification(startOrEnd, growOrShrink);
            endModification = getEndModification(startOrEnd, growOrShrink);
            span = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getSpanCollection().getSelection();
        }

        @Override
        public void execute() {
            span.getTextSource().getConceptAnnotationCollection().modifySpan(span, startModification, endModification);
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
                            startModification = getStartModification(startOrEnd, SHRINK);
                            endModification = getEndModification(startOrEnd, SHRINK);
                            span.getTextSource().getConceptAnnotationCollection().modifySpan(span, startModification, endModification);
                            break;
                        case SHRINK:
                            startModification = getStartModification(startOrEnd, GROW);
                            endModification = getEndModification(startOrEnd, GROW);
                            span.getTextSource().getConceptAnnotationCollection().modifySpan(span, startModification, endModification);
                            break;
                    }
                }

                @Override
                public void redo() {
                    super.redo();
                    span.getTextSource().getConceptAnnotationCollection().modifySpan(span, startModification, endModification);
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
        return  startOrEnd.equals(START) ?
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
