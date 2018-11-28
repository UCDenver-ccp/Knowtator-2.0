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

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.SpanActions;
import org.junit.jupiter.api.Test;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SpanActionsTests {
	private static final KnowtatorModel controller = TestingHelpers.getLoadedController();
    private static final int initialStart = 0;
    private static final int initialEnd = 4;

	private static void checkSpanModificationsTest(AbstractKnowtatorAction action, Span span, int expectedStart, int expectedEnd) {
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
    public void modifySpanActionTest() {
	    TextSource textSource = controller.getSelectedTextSource().get();
	    ConceptAnnotation conceptAnnotation = textSource.firstConceptAnnotation();
	    textSource.setSelection(conceptAnnotation);
	    Span span = conceptAnnotation.first();
	    conceptAnnotation.setSelection(span);

	    checkSpanModificationsTest(new SpanActions.ModifySpanAction(SpanActions.START, SpanActions.SHRINK, span),
                span, initialStart + 1, initialEnd);
	    checkSpanModificationsTest(new SpanActions.ModifySpanAction(SpanActions.START, SpanActions.GROW, span),
                span, max(initialStart - 1, 0), initialEnd);
	    checkSpanModificationsTest(new SpanActions.ModifySpanAction(SpanActions.END, SpanActions.SHRINK, span),
                span, initialStart, initialEnd - 1);
	    checkSpanModificationsTest(new SpanActions.ModifySpanAction(SpanActions.END, SpanActions.GROW, span),
                span, initialStart, min(initialEnd + 1, textSource.getContent().length()));
    }

}