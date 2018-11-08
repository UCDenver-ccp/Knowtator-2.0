package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.junit.Test;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class OWLActionsTest {
    private static final KnowtatorController controller = TestingHelpers.getLoadedController();

    @Test
    public void reassignOWLClassAction() throws ActionUnperformableException, NoSelectionException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);

        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass() == null;

        controller.registerAction(new OWLActions.ReassignOWLClassAction(controller));
        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass().equals(controller.getOWLModel().getSelectedOWLEntity());
        controller.undo();
        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass() == null;
        controller.redo();
        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass().equals(controller.getOWLModel().getSelectedOWLEntity());
        controller.undo();
        //TODO: Add more to this test to see if descendants are reassigned too. Probably will need to edit OWLModel's debug to make some descendants
    }

    @Test
    public void changeColorAction() throws NoSelectionException, ActionUnperformableException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        Profile profile = controller.getProfileCollection().getSelection();
        assert profile.getColor(conceptAnnotation).equals(Color.CYAN);

        Set<Object> owlClassSet = new HashSet<>();
        owlClassSet.add(controller.getOWLModel().getSelectedOWLEntity());

        controller.registerAction(new OWLActions.ReassignOWLClassAction(controller));
        assert profile.getColor(conceptAnnotation).equals(Color.CYAN);

        controller.registerAction(new OWLActions.ColorChangeAction(profile, owlClassSet, Color.GREEN));
        assert profile.getColor(conceptAnnotation).equals(Color.GREEN);
        controller.undo();
        assert profile.getColor(conceptAnnotation).equals(Color.CYAN);
        controller.redo();
        assert profile.getColor(conceptAnnotation).equals(Color.GREEN);
        controller.undo();

    }
}