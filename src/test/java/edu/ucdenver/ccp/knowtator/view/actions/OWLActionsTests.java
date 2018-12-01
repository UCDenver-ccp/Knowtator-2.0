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
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ColorChangeAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ReassignOWLClassAction;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLClass;

import java.awt.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class OWLActionsTests {
    private static KnowtatorModel controller;

    static {
        try {
            controller = TestingHelpers.getLoadedController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void reassignOWLClassActionTest() {
        TextSource textSource = controller.getSelectedTextSource().get();
        ConceptAnnotation conceptAnnotation = textSource.firstConceptAnnotation();
        textSource.setSelection(conceptAnnotation);

        OWLClass owlClass = controller.getOWLClassByID("Pizza").get();
        assert controller.getSelectedTextSource().get().getSelectedAnnotation().get().getOwlClass() == owlClass;

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
    void changeColorActionTest() {
        TextSource textSource = controller.getSelectedTextSource().get();
        ConceptAnnotation conceptAnnotation = textSource.firstConceptAnnotation();
        textSource.setSelection(conceptAnnotation);
        Profile profile = controller.getSelectedProfile().get();
        assert profile.getColor(conceptAnnotation.getOwlClass()).equals(Color.RED);

        controller.getSelectedTextSource().get().selectNextSpan();
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