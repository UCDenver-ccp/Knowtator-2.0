/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.actions;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ColorChangeAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ReassignOwlClassAction;
import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OwlActionsTest {
  private static KnowtatorModel model;

  static {
    try {
      model = TestingHelpers.getLoadedModel();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void reassignOwlClassActionTest() throws ActionUnperformable {
    model.getTextSources().selectOnly(model.getTextSources().get("document1").get());
    TextSource textSource = model.getTextSources().getOnlySelected().get();
    ConceptAnnotation conceptAnnotation =textSource.getConceptAnnotations().first().get();
    textSource.getConceptAnnotations().selectOnly(conceptAnnotation);

    String owlClass = model.getOwlClassById("Pizza").get().toStringID();
    assert model.getTextSources().getOnlySelected().get().getConceptAnnotations().getOnlySelected().get().getOwlClass().equals(owlClass);

    model.registerAction(
        new ReassignOwlClassAction(model, conceptAnnotation, model.getSelectedOwlClass().get()));
    assert conceptAnnotation.getOwlClass().equals(model.getSelectedOwlClass().get());
    model.undo();
    assert conceptAnnotation.getOwlClass().equals(owlClass);
    model.redo();
    assert conceptAnnotation.getOwlClass().equals(model.getSelectedOwlClass().get());
    model.undo();
    // TODO: Add more to this test to see if descendants are reassigned too. Probably will need to
    // edit OwlModel's debug to make some descendants
  }

  @Test
  void changeColorActionTest() throws ActionUnperformable {
    model.getTextSources().selectOnly(model.getTextSources().get("document1").get());
    TextSource textSource = model.getTextSources().getOnlySelected().get();
    ConceptAnnotation conceptAnnotation =textSource.getConceptAnnotations().first().get();
    textSource.getConceptAnnotations().selectOnly(conceptAnnotation);
    Profile profile = model.getSelectedProfile().get();
    assert profile.getColor(conceptAnnotation.getOwlClass()).equals(Color.RED);

    model.getTextSources().getOnlySelected().get().getConceptAnnotations().selectNext();
    Set<String> owlClassSet = new HashSet<>();
    owlClassSet.add(model.getSelectedOwlClass().get());

    model.registerAction(
        new ReassignOwlClassAction(model, conceptAnnotation, model.getSelectedOwlClass().get()));
    assert conceptAnnotation.getColor().equals(KnowtatorDefaultSettings.COLORS.get(0));

    model.registerAction(new ColorChangeAction(model, profile, owlClassSet, Color.GREEN));
    assert conceptAnnotation.getColor().equals(Color.GREEN);
    model.undo();
    assert conceptAnnotation.getColor().equals(KnowtatorDefaultSettings.COLORS.get(0));
    model.redo();
    assert profile.getColor(conceptAnnotation.getOwlClass()).equals(Color.GREEN);
    model.undo();
  }
}
