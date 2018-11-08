package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.junit.Test;

public class KnowtatorCollectionActionsTest {
    private static final KnowtatorController controller = TestingHelpers.getLoadedController();

    private static void testCollectionAction(AbstractKnowtatorCollectionAction action,
                                             int expectedTextSources,
                                             int expectedConceptAnnotations,
                                             int expectedSpans,
                                             int expectedGraphSpaces,
                                             int expectedProfiles,
                                             int expectedHighlighters,
                                             int expectedAnnotationNodes,
                                             int expectedTriples) {
        TestingHelpers.checkDefaultCollectionValues(controller);
        controller.registerAction(action);
        TestingHelpers.countCollections(controller,
                expectedTextSources,
                expectedConceptAnnotations,
                expectedSpans,
                expectedGraphSpaces,
                expectedProfiles,
                expectedHighlighters,
                expectedAnnotationNodes,
                expectedTriples);
        controller.undo();
        TestingHelpers.checkDefaultCollectionValues(controller);
        controller.redo();
        TestingHelpers.countCollections(controller,
                expectedTextSources,
                expectedConceptAnnotations,
                expectedSpans,
                expectedGraphSpaces,
                expectedProfiles,
                expectedHighlighters,
                expectedAnnotationNodes,
                expectedTriples);
        controller.undo();
    }

    @Test
    public void addTextSourceAction() {
        testCollectionAction(new KnowtatorCollectionActions.TextSourceAction(KnowtatorCollectionActions.ADD, controller, TestingHelpers.getArticleFile(TestingHelpers.projectFileName, "document2")),
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
        testCollectionAction(new KnowtatorCollectionActions.TextSourceAction(KnowtatorCollectionActions.REMOVE, controller, null),
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
    public void addConceptAnnotationAction() throws NoSelectionException {
        testCollectionAction(new KnowtatorCollectionActions.ConceptAnnotationAction(KnowtatorCollectionActions.ADD, controller),
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
    public void removeConceptAnnotationAction() throws NoSelectionException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        textSource.getConceptAnnotationCollection().setSelection(textSource.getConceptAnnotationCollection().first());
        testCollectionAction(new KnowtatorCollectionActions.ConceptAnnotationAction(KnowtatorCollectionActions.REMOVE, controller),
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
    public void addSpanAction() throws NoSelectionException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        textSource.getConceptAnnotationCollection().setSelection(textSource.getConceptAnnotationCollection().first());
        testCollectionAction(new KnowtatorCollectionActions.SpanAction(KnowtatorCollectionActions.ADD, controller),
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
    public void removeSpanAction() throws NoSelectionException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        conceptAnnotation.getSpanCollection().setSelection(conceptAnnotation.getSpanCollection().first());
        testCollectionAction(new KnowtatorCollectionActions.SpanAction(KnowtatorCollectionActions.REMOVE, controller),
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
        testCollectionAction(new KnowtatorCollectionActions.ProfileAction(KnowtatorCollectionActions.ADD, controller, "I'm new here"),
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
        testCollectionAction(new KnowtatorCollectionActions.ProfileAction(KnowtatorCollectionActions.REMOVE, controller, "profile1"),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles - 1,
                TestingHelpers.defaultExpectedHighlighters - 2,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
    }
}