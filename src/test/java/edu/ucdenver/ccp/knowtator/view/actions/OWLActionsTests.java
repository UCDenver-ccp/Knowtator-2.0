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
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;
import edu.ucdenver.ccp.knowtator.view.actions.model.ColorChangeAction;
import edu.ucdenver.ccp.knowtator.view.actions.model.ReassignOWLClassAction;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLClass;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class OWLActionsTests {
    private static final KnowtatorModel controller = TestingHelpers.getLoadedController();

    @Test
    public void reassignOWLClassActionTest() {
        TextSource textSource = controller.getTextSource().get();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);

        OWLClass owlClass = controller.getOWLClassByID("Pizza").get();
        assert controller.getTextSource().get().getConceptAnnotationCollection().getSelection().get().getOwlClass() == owlClass;

        controller.registerAction(new ReassignOWLClassAction(conceptAnnotation, controller.getSelectedOWLClass().get()));
        assert conceptAnnotation.getOwlClass().equals(controller.getSelectedOWLClass().get());
        controller.undo();
        assert conceptAnnotation.getOwlClass() == owlClass;
        controller.redo();
        assert conceptAnnotation.getOwlClass().equals(controller.getSelectedOWLClass().get());
        controller.undo();
        //TODO: Add more to this test to see if descendants are reassigned too. Probably will need to edit OWLModel's debug to make some descendants
    }

    @Test
    public void changeColorActionTest() {
        TextSource textSource = controller.getTextSource().get();
        ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
        textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
        Profile profile = controller.getProfileCollection().getSelection().get();
        assert profile.getColor(conceptAnnotation.getOwlClass()).equals(Color.RED);

        Set<OWLClass> owlClassSet = new HashSet<>();
        owlClassSet.add(controller.getSelectedOWLClass().get());

        controller.registerAction(new ReassignOWLClassAction(conceptAnnotation, controller.getSelectedOWLClass().get()));
        assert conceptAnnotation.getColor().equals(KnowtatorDefaultSettings.COLORS.get(0));

        controller.registerAction(new ColorChangeAction(profile, owlClassSet, Color.GREEN));
        assert conceptAnnotation.getColor().equals(Color.GREEN);
        controller.undo();
        assert conceptAnnotation.getColor().equals(KnowtatorDefaultSettings.COLORS.get(0));
        controller.redo();
        assert profile.getColor(conceptAnnotation.getOwlClass()).equals(Color.GREEN);
        controller.undo();

    }
}