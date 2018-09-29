package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;

class SpanActions {
    final static String GROW = "grow";
    final static String SHRINK = "shrink";
    final static String START = "start";
    final static String END = "end";

    static void selectNextSpan(KnowtatorView view) {
        view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getNextSpan();
    }

    static void selectPreviousSpan(KnowtatorView view) {
        view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getPreviousSpan();
    }

    static void modifySelectedSpan(KnowtatorView view, String startOrEnd, String growOrShrink) {
        ConceptAnnotationCollection conceptAnnotationCollection = view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection();
        conceptAnnotationCollection.modifySelection(
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
