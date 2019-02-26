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

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction;
import edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType;

/** The type Span action. */
public class SpanAction extends AbstractKnowtatorCollectionAction<Span> {

  private final ConceptAnnotation conceptAnnotation;

  /**
   * Instantiates a new Span action.
   *
   * @param model the model
   * @param actionName the action name
   * @param conceptAnnotation the concept annotation
   */
  public SpanAction(
      KnowtatorModel model, CollectionActionType actionName, ConceptAnnotation conceptAnnotation) {
    super(model, actionName, "span", conceptAnnotation);
    this.conceptAnnotation = conceptAnnotation;
  }

  @Override
  protected void prepareAdd() {
    Span newSpan =
        new Span(
            conceptAnnotation,
            null,
            model.getSelection().getStart(),
            model.getSelection().getEnd());
    setObject(newSpan);
  }

  @Override
  public void prepareRemove() throws ActionUnperformable {
    // If the concept annotation only has one, remove the annotation instead
    if (conceptAnnotation.size() == 1) {
      setObject(null);
      ConceptAnnotationAction action =
          new ConceptAnnotationAction(model, REMOVE, conceptAnnotation.getTextSource());
      action.setObject(conceptAnnotation);
      model.registerAction(action);
      addKnowtatorEdit(action);
    } else {
      super.prepareRemove();
    }
  }

  @Override
  protected void cleanUpRemove() {}

  @Override
  public void execute() throws ActionUnperformable {
    if (actionType == REMOVE && conceptAnnotation.size() == 1) {
      try {
        super.execute();
      } catch (ActionUnperformable ignored) {
        // Ok if action can't be performed
      }
    } else {
      super.execute();
    }
  }

  @Override
  public void cleanUpAdd() {}
}
