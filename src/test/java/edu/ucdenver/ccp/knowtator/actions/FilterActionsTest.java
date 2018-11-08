package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.FilterModel;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.junit.Test;

public class FilterActionsTest {
    private static final KnowtatorController controller = TestingHelpers.getLoadedController();

    @Test
    public void filterAction() throws ActionUnperformableException, NoSelectionException {
        TestingHelpers.checkDefaultCollectionValues(controller);
        TestingHelpers.testKnowtatorAction(controller, new FilterActions.FilterAction(controller, FilterModel.PROFILE, true),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations ,
                TestingHelpers.defaultExpectedSpans - 2,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
        TestingHelpers.testKnowtatorAction(controller, new FilterActions.FilterAction(controller, FilterModel.PROFILE, false),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations,
                TestingHelpers.defaultExpectedSpans,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);

        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        controller.registerAction(new OWLActions.ReassignOWLClassAction(controller));

        TestingHelpers.testKnowtatorAction(controller, new FilterActions.FilterAction(controller, FilterModel.OWLCLASS, true),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations ,
                TestingHelpers.defaultExpectedSpans - 3,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);
        TestingHelpers.testKnowtatorAction(controller, new FilterActions.FilterAction(controller, FilterModel.OWLCLASS, false),
                TestingHelpers.defaultExpectedTextSources,
                TestingHelpers.defaultExpectedConceptAnnotations ,
                TestingHelpers.defaultExpectedSpans ,
                TestingHelpers.defaultExpectedGraphSpaces,
                TestingHelpers.defaultExpectedProfiles,
                TestingHelpers.defaultExpectedHighlighters,
                TestingHelpers.defaultExpectedAnnotationNodes,
                TestingHelpers.defaultExpectedTriples);


    }

}