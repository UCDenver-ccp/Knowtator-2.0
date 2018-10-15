package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;

class SpanActions {
    final static String GROW = "grow";
    final static String SHRINK = "shrink";
    final static String START = "start";
    final static String END = "end";

    static void selectNextSpan(TextSource textSource) throws NoSelectionException {
        textSource.getConceptAnnotationCollection().getNextSpan();
    }

    static void selectPreviousSpan(TextSource textSource) throws NoSelectionException {
        textSource.getConceptAnnotationCollection().getPreviousSpan();
    }

    static void modifySpan(Span span, String startOrEnd, String growOrShrink) {
        span.getTextSource().getConceptAnnotationCollection().modifySpan(
                span,
                startOrEnd.equals(START) ?
                        growOrShrink.equals(GROW) ?
                                -1 : growOrShrink.equals(SHRINK) ?
                                +1 : 0 : 0,
                startOrEnd.equals(END) ?
                        growOrShrink.equals(GROW) ?
                                +1 : growOrShrink.equals(SHRINK) ?
                                -1 : 0 : 0);
    }

    static void modifySelection(KnowtatorView view, String startOrEnd, String growOrShrink) {
        KnowtatorTextPane textPane = view.getKnowtatorTextPane();
        textPane.modifySelection(
                startOrEnd.equals(START) ?
                        growOrShrink.equals(GROW) ?
                                -1 : growOrShrink.equals(SHRINK) ?
                                +1 : 0 : 0,
                startOrEnd.equals(END) ?
                        growOrShrink.equals(GROW) ?
                                +1 : growOrShrink.equals(SHRINK) ?
                                -1 : 0 : 0);

    }
}
