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

package edu.ucdenver.ccp.knowtator.view.actions.modelactions;

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** The type Color change action. */
public class ColorChangeAction extends AbstractKnowtatorAction {

  private final Map<String, Color> oldColorAssignments;
  private final Profile profile;
  private final Set<String> owlClasses;
  private final Color color;

  /**
   * Instantiates a new Color change action.
   *
   * @param model the model
   * @param profile the profile
   * @param owlClasses the owl classes
   * @param color the color
   */
  public ColorChangeAction(
      KnowtatorModel model, Profile profile, Set<String> owlClasses, Color color) {
    super(model, "Change color");
    this.profile = profile;
    this.owlClasses = owlClasses;
    this.color = color;

    oldColorAssignments = new HashMap<>();
    owlClasses.forEach(
        owlClass -> {
          Color oldColor = profile.getColor(owlClass);
          if (oldColor != null) {
            oldColorAssignments.put(owlClass, oldColor);
          }
        });
  }

  @Override
  public void execute() {
    owlClasses.forEach(owlClass -> profile.addColor(owlClass, color));
  }

  @Override
  public void undo() {
    super.undo();
    oldColorAssignments.forEach(profile::addColor);
  }

  @Override
  public void redo() {
    super.redo();
    oldColorAssignments.keySet().forEach(owlClass -> profile.addColor(owlClass, color));
  }
}
