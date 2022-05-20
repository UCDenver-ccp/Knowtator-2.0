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

package edu.ucdenver.ccp.knowtator.view.profile;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProfileDialogTest {
  private static KnowtatorView view;
  private static final int defaultExpectedHighlighters = 1;

  @BeforeAll
  static void setUp() {
    view = new KnowtatorView();
  }

  @Test
  void cancelProfileDialogTest() throws IOException {
    KnowtatorModel model = TestingHelpers.getLoadedModel();
    TestingHelpers.checkDefaultCollectionValues(model);
    view.loadProject(model.getProjectLocation(), null);
    TestingHelpers.checkDefaultCollectionValues(view.getModel().get());

    ProfileDialog dialog = new ProfileDialog(JOptionPane.getFrameForComponent(view), view);
    dialog.pack();
    dialog.onCancel();
    TestingHelpers.checkDefaultCollectionValues(model);
  }

  @Test
  void okProfileDialogTest() throws IOException {
    KnowtatorModel model = TestingHelpers.getLoadedModel();
    TestingHelpers.checkDefaultCollectionValues(model);
    view.loadProject(model.getProjectLocation(), null);
    TestingHelpers.checkDefaultCollectionValues(view.getModel().get());

    ProfileDialog dialog = new ProfileDialog(JOptionPane.getFrameForComponent(view), view);
    dialog.pack();
    dialog.onCancel();
    TestingHelpers.checkDefaultCollectionValues(model);
  }

  public static void testKnowtatorAction(
      ProfileDialog dialog,
      KnowtatorModel controller,
      AbstractKnowtatorAction action,
      int expectedProfiles,
      int expectedHighlighters)
      throws ActionUnperformable {
    checkDefaultCollectionValues(dialog);
    controller.registerAction(action);
    countCollections(
        dialog,
        expectedProfiles,
        expectedHighlighters);
    controller.undo();
    controller.getProfiles().selectOnly(controller.getDefaultProfile());
    checkDefaultCollectionValues(dialog);
    controller.redo();
    countCollections(
        dialog,
        expectedProfiles,
        expectedHighlighters);
    controller.undo();
  }

  private static void countCollections(ProfileDialog dialog, int expectedProfiles, int expectedHighlighters) {
    assert dialog.profileList.getModel().getSize() == expectedProfiles;
    assert dialog.colorList.getModel().getSize() == expectedHighlighters;
  }

  private static void checkDefaultCollectionValues(ProfileDialog dialog) {
    assert dialog.profileList.getModel().getSize() == TestingHelpers.defaultExpectedProfiles;
    assert dialog.colorList.getModel().getSize() == defaultExpectedHighlighters;
  }

  @Test
  void addProfileDialogTest() throws IOException, ActionUnperformable {
    KnowtatorModel model = TestingHelpers.getLoadedModel();
    TestingHelpers.checkDefaultCollectionValues(model);
    view.loadProject(model.getProjectLocation(), null);
    TestingHelpers.checkDefaultCollectionValues(view.getModel().get());

    ProfileDialog dialog = new ProfileDialog(JOptionPane.getFrameForComponent(view), view);
    dialog.pack();

    model = view.getModel().get();

    testKnowtatorAction(dialog, model,
        new ProfileAction(model, ADD, "I'm new here"),
        TestingHelpers.defaultExpectedProfiles + 1,
        0);

    dialog.onCancel();
    TestingHelpers.checkDefaultCollectionValues(model);
  }

}