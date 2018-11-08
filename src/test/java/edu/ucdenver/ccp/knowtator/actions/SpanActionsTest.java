package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import org.junit.Test;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SpanActionsTest {
    private static final KnowtatorController controller = TestingHelpers.getLoadedController();
    private static final int initialStart = 0;
    private static final int initialEnd = 4;

    private static void checkSpanModifications(AbstractKnowtatorAction action, Span span, int expectedStart, int expectedEnd) {
        assert span.getStart() == initialStart;
        assert span.getEnd() == initialEnd;
        controller.registerAction(action);
        assert span.getStart() == expectedStart;
        assert span.getEnd() == expectedEnd;
        controller.undo();
        assert span.getStart() == initialStart;
        assert span.getEnd() == initialEnd;
        controller.redo();
        assert span.getStart() == expectedStart;
        assert span.getEnd() == expectedEnd;
        controller.undo();
    }

    @Test
    public void modifySpanAction() throws NoSelectionException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        Span span = conceptAnnotation.getSpanCollection().first();
        conceptAnnotation.getSpanCollection().setSelection(span);

        checkSpanModifications(new SpanActions.ModifySpanAction(controller, SpanActions.START, SpanActions.SHRINK),
                span, initialStart + 1, initialEnd);
        checkSpanModifications(new SpanActions.ModifySpanAction(controller, SpanActions.START, SpanActions.GROW),
                span, max(initialStart - 1, 0), initialEnd);
        checkSpanModifications(new SpanActions.ModifySpanAction(controller, SpanActions.END, SpanActions.SHRINK),
                span, initialStart, initialEnd - 1);
        checkSpanModifications(new SpanActions.ModifySpanAction(controller, SpanActions.END, SpanActions.GROW),
                span, initialStart, min(initialEnd + 1, textSource.getContent().length()));
    }

}