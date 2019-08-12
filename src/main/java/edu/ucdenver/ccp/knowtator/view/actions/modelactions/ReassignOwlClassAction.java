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
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;

/** The type Reassign owl class action. */
public class ReassignOwlClassAction extends AbstractKnowtatorAction {

  private final String oldOwlClass;
  private final ConceptAnnotation conceptAnnotation;
  private final String newOwlClass;

  /**
   * Instantiates a new Reassign owl class action.
   *  @param model the model
   * @param conceptAnnotation the concept annotation
   * @param owlClass the owl class
   */
  public ReassignOwlClassAction(
      KnowtatorModel model, ConceptAnnotation conceptAnnotation, String owlClass) {
    super(model, "Reassign OWL class");

    this.conceptAnnotation = conceptAnnotation;

    oldOwlClass = conceptAnnotation.getOwlClass();
    this.newOwlClass = owlClass;
  }

  @Override
  public void execute() {
    conceptAnnotation.setOwlClass(newOwlClass);
  }

  @Override
  public void undo() {
    super.undo();
    conceptAnnotation.setOwlClass(oldOwlClass);
  }

  @Override
  public void redo() {
    super.redo();
    conceptAnnotation.setOwlClass(newOwlClass);
  }
}
