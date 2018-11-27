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
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;

public class KnowtatorCollectionActionsTest {


    private KnowtatorModel controller;

    @BeforeEach
    void setup() {
        controller = TestingHelpers.getLoadedController();
        KnowtatorView.MODEL = controller;
    }

    @Test
    public void addTextSourceAction() {
        TestingHelpers.testKnowtatorAction(controller,
                new TextSourceAction(ADD, TestingHelpers.getArticleFile(TestingHelpers.projectFileName, "document4")),
                TestingHelpers.defaultExpectedTextSources + 1,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }

    @Test
    public void removeTextSourceAction() {
        TestingHelpers.testKnowtatorAction(controller,
                new TextSourceAction(REMOVE, null),
                TestingHelpers.defaultExpectedTextSources - 1,
                TestingHelpers.defaultExpectedConceptAnnotations - 2,
                TestingHelpers.defaultExpectedSpans - 3,
                TestingHelpers.defaultExpectedGraphSpaces - 1,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes - 2,
                TestingHelpers.defaultExpectedTriples - 1);
    }

    @Test
    public void addConceptAnnotationAction() {
        TestingHelpers.testKnowtatorAction(controller,
                new ConceptAnnotationAction(ADD, controller.getTextSource().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations + 1,
                TestingHelpers.defaultExpectedSpans + 1,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }

    @Test
    public void removeConceptAnnotationAction() {
        TextSource textSource = controller.getTextSource().get();
        textSource.getConceptAnnotationCollection().setSelection(textSource.getConceptAnnotationCollection().first());
        TestingHelpers.testKnowtatorAction(controller,
                new ConceptAnnotationAction(REMOVE, controller.getTextSource().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations - 1,
                TestingHelpers.defaultExpectedSpans - 1,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes - 1,
                TestingHelpers.defaultExpectedTriples - 1);
    }

    @Test
    public void addSpanAction() {
        TextSource textSource = controller.getTextSource().get();
        textSource.getConceptAnnotationCollection().setSelection(textSource.getConceptAnnotationCollection().first());
        TestingHelpers.testKnowtatorAction(controller,
                new SpanAction(ADD, controller.getTextSource().get().getConceptAnnotationCollection().getSelection().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans + 1,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }

    @Test
    public void removeSpanAction() {
        TextSource textSource = controller.getTextSource().get();
        // First test remove span if there is only one in the collection. This should be equivalent to just removing the annotation
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        conceptAnnotation.getSpanCollection().setSelection(conceptAnnotation.getSpanCollection().first());
        TestingHelpers.testKnowtatorAction(controller,
                new SpanAction(REMOVE, controller.getTextSource().get().getConceptAnnotationCollection().getSelection().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations - 1,
                TestingHelpers.defaultExpectedSpans - 1,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes - 1,
                TestingHelpers.defaultExpectedTriples - 1);

        // Next test remove span if there are multiple spans. This should only remove the span.
        textSource.getConceptAnnotationCollection().selectNext();
        conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection().get();
        conceptAnnotation.getSpanCollection().setSelection(conceptAnnotation.getSpanCollection().first());
        TestingHelpers.testKnowtatorAction(controller,
                new SpanAction(REMOVE, controller.getTextSource().get().getConceptAnnotationCollection().getSelection().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations ,
                TestingHelpers.defaultExpectedSpans - 1,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }

    @Test
    public void addProfileAction() {
        TestingHelpers.testKnowtatorAction(controller,
                new ProfileAction(ADD, "I'm new here"),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles + 1,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }

    @Test
    public void removeProfileAction() {
        TestingHelpers.testKnowtatorAction(controller,
                new ProfileAction(REMOVE, "profile1"),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations - 2,
                TestingHelpers.defaultExpectedSpans - 3,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles - 1,
                TestingHelpers.defaultExpectedHighlighters - 2,
                TestingHelpers.defaultExpectedAnnotationNodes - 2,
                TestingHelpers.defaultExpectedTriples - 2);

//        assertThrows(ActionUnperformableException.class, this::removeDefaultProfileAction);
    }

    @SuppressWarnings("unused")
    void removeDefaultProfileAction() throws ActionUnperformableException {
        // It should not be possible to remove the default profile
        new ProfileAction(REMOVE, "Default").execute();

    }

    @Test
    public void addGraphSpaceAction() {
        TestingHelpers.testKnowtatorAction(controller,
                new GraphSpaceAction(ADD, "new_graph_space", controller.getTextSource().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans,
                TestingHelpers.defaultExpectedGraphSpaces + 1,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }

    @Test
    public void removeGraphSpaceAction() {
        TextSource textSource = controller.getTextSource().get();
        textSource.getGraphSpaceCollection().selectNext();
        TestingHelpers.testKnowtatorAction(controller,
                new GraphSpaceAction(REMOVE, null, controller.getTextSource().get()),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans,
                TestingHelpers.defaultExpectedGraphSpaces - 1,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes - 2,
                TestingHelpers.defaultExpectedTriples - 1);
    }

    public void setController(KnowtatorModel controller) {
        this.controller = controller;
    }
}