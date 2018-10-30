package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class SpanActions {
    public final static String GROW = "grow";
    public final static String SHRINK = "shrink";
    public final static String START = "start";
    public final static String END = "end";

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

    public static void selectNextSpan(TextSource textSource) throws NoSelectionException {
        textSource.getConceptAnnotationCollection().getNextSpan();
    }

    public static void selectPreviousSpan(TextSource textSource) throws NoSelectionException {
        textSource.getConceptAnnotationCollection().getPreviousSpan();
    }

    public static void modifySpan(Span span, String startOrEnd, String growOrShrink) {
        int startModification = getStartModification(startOrEnd, growOrShrink);
        int endModification = getEndModification(startOrEnd, growOrShrink);
        span.getTextSource().getConceptAnnotationCollection().modifySpan(span, startModification, endModification);
    }

    public static void modifySelection(KnowtatorView view, String startOrEnd, String growOrShrink) {
        int startModification = getStartModification(startOrEnd, growOrShrink);
        int endModification = getEndModification(startOrEnd, growOrShrink);
        KnowtatorTextPane textPane = view.getKnowtatorTextPane();
        textPane.modifySelection(startModification, endModification);
    }
}
