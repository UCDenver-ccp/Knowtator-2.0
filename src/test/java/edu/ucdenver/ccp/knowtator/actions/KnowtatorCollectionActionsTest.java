package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import org.junit.Test;

public class KnowtatorCollectionActionsTest {
    private static final KnowtatorController controller = TestingHelpers.getLoadedController();

    @Test
    public void addConceptAnnotation() {
        try {
            TestingHelpers.checkDefaultCollectionValues(controller);
            controller.registerAction(new KnowtatorCollectionActions.ConceptAnnotationAction(KnowtatorCollectionActions.ADD, controller));
            TestingHelpers.countCollections(controller,
                    TestingHelpers.defaultExpectedTextSources,
                    TestingHelpers.defaultExpectedConceptAnnotations + 1,
                    TestingHelpers.defaultExpectedSpans + 1,
                    TestingHelpers.defaultExpectedGraphSpaces,
                    TestingHelpers.defaultExpectedProfiles,
                    TestingHelpers.defaultExpectedHighlighters,
                    TestingHelpers.defaultExpectedAnnotationNodes,
                    TestingHelpers.defaultExpectedTriples);
            controller.undo();
            TestingHelpers.checkDefaultCollectionValues(controller);
            controller.redo();
            TestingHelpers.countCollections(controller,
                    TestingHelpers.defaultExpectedTextSources,
                    TestingHelpers.defaultExpectedConceptAnnotations + 1,
                    TestingHelpers.defaultExpectedSpans + 1,
                    TestingHelpers.defaultExpectedGraphSpaces,
                    TestingHelpers.defaultExpectedProfiles,
                    TestingHelpers.defaultExpectedHighlighters,
                    TestingHelpers.defaultExpectedAnnotationNodes,
                    TestingHelpers.defaultExpectedTriples);
            controller.undo();
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeConceptAnnotation() {
        try {
            TestingHelpers.checkDefaultCollectionValues(controller);
            controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().setSelection(controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().first());
            controller.registerAction(new KnowtatorCollectionActions.ConceptAnnotationAction(KnowtatorCollectionActions.REMOVE, controller));
            TestingHelpers.countCollections(controller,
                    TestingHelpers.defaultExpectedTextSources,
                    TestingHelpers.defaultExpectedConceptAnnotations - 1,
                    TestingHelpers.defaultExpectedSpans - 1,
                    TestingHelpers.defaultExpectedGraphSpaces,
                    TestingHelpers.defaultExpectedProfiles,
                    TestingHelpers.defaultExpectedHighlighters,
                    TestingHelpers.defaultExpectedAnnotationNodes - 1,
                    TestingHelpers.defaultExpectedTriples - 1);
            controller.undo();
            TestingHelpers.checkDefaultCollectionValues(controller);
            controller.redo();
            TestingHelpers.countCollections(controller,
                    TestingHelpers.defaultExpectedTextSources,
                    TestingHelpers.defaultExpectedConceptAnnotations - 1,
                    TestingHelpers.defaultExpectedSpans - 1,
                    TestingHelpers.defaultExpectedGraphSpaces,
                    TestingHelpers.defaultExpectedProfiles,
                    TestingHelpers.defaultExpectedHighlighters,
                    TestingHelpers.defaultExpectedAnnotationNodes - 1,
                    TestingHelpers.defaultExpectedTriples - 1);
            controller.undo();
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }
}