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

package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.NoSelectedOWLClassException;
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
    public void reassignOWLClassAction() throws ActionUnperformableException, NoSelectionException, NoSelectedOWLClassException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);

        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass() == null;

        controller.registerAction(new OWLActions.ReassignOWLClassAction(controller));
        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass().equals(controller.getOWLModel().getSelectedOWLClass());
        controller.undo();
        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass() == null;
        controller.redo();
        assert controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass().equals(controller.getOWLModel().getSelectedOWLClass());
        controller.undo();
        //TODO: Add more to this test to see if descendants are reassigned too. Probably will need to edit OWLModel's debug to make some descendants
    }

    @Test
    public void changeColorAction() throws NoSelectionException, ActionUnperformableException, NoSelectedOWLClassException {
        TextSource textSource = controller.getTextSourceCollection().getSelection();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        Profile profile = controller.getProfileCollection().getSelection();
        assert profile.getColor(conceptAnnotation).equals(Color.CYAN);

        Set<Object> owlClassSet = new HashSet<>();
        owlClassSet.add(controller.getOWLModel().getSelectedOWLClass());

        controller.registerAction(new OWLActions.ReassignOWLClassAction(controller));
        assert profile.getColor(conceptAnnotation).equals(Color.CYAN);

	    controller.registerAction(new OWLActions.ColorChangeAction(controller, profile, owlClassSet, Color.GREEN));
        assert profile.getColor(conceptAnnotation).equals(Color.GREEN);
        controller.undo();
        assert profile.getColor(conceptAnnotation).equals(Color.CYAN);
        controller.redo();
        assert profile.getColor(conceptAnnotation).equals(Color.GREEN);
        controller.undo();

    }
}