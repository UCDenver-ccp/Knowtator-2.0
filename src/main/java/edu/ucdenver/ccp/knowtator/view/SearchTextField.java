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

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchTextField extends JTextField implements KnowtatorComponent, CaretListener {

    private final KnowtatorView view;
    private Pattern pattern;
    private Matcher matcher;
    private int matchEnd;

    SearchTextField(KnowtatorView view) {
        this.view = view;
        view.getKnowtatorTextPane().addCaretListener(this);

        pattern = Pattern.compile("");
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                makePattern();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                makePattern();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        new TextBoundModelListener(view.getController()) {
            @Override
            public void respondToConceptAnnotationModification() {

            }

            @Override
            public void respondToSpanModification() {

            }

            @Override
            public void respondToGraphSpaceModification() {

            }

            @Override
            public void respondToGraphSpaceCollectionFirstAdded() {

            }

            @Override
            public void respondToGraphSpaceCollectionEmptied() {

            }

            @Override
            public void respondToGraphSpaceRemoved() {

            }

            @Override
            public void respondToGraphSpaceAdded() {

            }

            @Override
            public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {

            }

            @Override
            public void respondToConceptAnnotationCollectionEmptied() {

            }

            @Override
            public void respondToConceptAnnotationRemoved() {

            }

            @Override
            public void respondToConceptAnnotationAdded() {

            }

            @Override
            public void respondToConceptAnnotationCollectionFirstAdded() {

            }

            @Override
            public void respondToSpanCollectionFirstAdded() {

            }

            @Override
            public void respondToSpanCollectionEmptied() {

            }

            @Override
            public void respondToSpanRemoved() {

            }

            @Override
            public void respondToSpanAdded() {

            }

            @Override
            public void respondToSpanSelection(SelectionEvent<Span> event) {
                if (event.getNew() != null) {
                    setText(event.getNew().getSpannedText());
                }
            }

            @Override
            public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {

            }

            @Override
            public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
                matcher = pattern.matcher(event.getNew().getContent());
            }

            @Override
            public void respondToTextSourceAdded() {

            }

            @Override
            public void respondToTextSourceRemoved() {

            }

            @Override
            public void respondToTextSourceCollectionEmptied() {

            }

            @Override
            public void respondToTextSourceCollectionFirstAdded() {

            }
        };
    }

    int searchForward(TextSource textSource) {
        matcher.reset();
        int start = -1;
        matchEnd = 0;
        int startBound = view.getKnowtatorTextPane().getSelectionEnd();
        while (matcher.find()) {
            if (matcher.start() > startBound &&
                    (!view.getOnlyInAnnotationsCheckBox().isSelected() || !(textSource.getConceptAnnotationCollection().getSpans(matcher.start()).size() == 0))) {
                start = matcher.start();
                matchEnd = matcher.end();
                break;
            }
        }
        if (matcher.hitEnd()) {
            matcher.reset();
            //noinspection ResultOfMethodCallIgnored
            matcher.find();
            if (!view.getOnlyInAnnotationsCheckBox().isSelected() || !(textSource.getConceptAnnotationCollection().getSpans(matcher.start()).size() == 0)) {
                start = matcher.start();
                matchEnd = matcher.end();
            }
        }
        return start;
    }

    int searchPrevious(TextSource textSource) {
        matcher.reset();
        int start = -1;
        matchEnd = 0;
        int endBound = view.getKnowtatorTextPane().getSelectionStart();
        while (matcher.find()) {
            if (matcher.start() < endBound || matcher.hitEnd() && (!view.getOnlyInAnnotationsCheckBox().isSelected() || !(textSource.getConceptAnnotationCollection().getSpans(matcher.start()).size() == 0))) {
                start = matcher.start();
                matchEnd = matcher.end();
            } else if (start == -1) {
                endBound = view.getKnowtatorTextPane().getText().length();
            } else {
                break;
            }
        }
        return start;
    }

    void makePattern() {
        pattern = Pattern.compile(getText(), (view.getRegexCheckBox().isSelected() ? 0 : Pattern.LITERAL) | (view.getCaseSensitiveCheckBox().isSelected() ? 0 : Pattern.CASE_INSENSITIVE));
        matcher.usePattern(pattern);
    }


    @Override
    public void dispose() {

    }

    @Override
    public void reset() {

    }


    @Override
    public void caretUpdate(CaretEvent e) {
        if (!view.getRegexCheckBox().isSelected()) {
            setText(view.getKnowtatorTextPane().getSelectedText());
        }

    }

    int getMatchEnd() {
        return matchEnd;
    }
}
