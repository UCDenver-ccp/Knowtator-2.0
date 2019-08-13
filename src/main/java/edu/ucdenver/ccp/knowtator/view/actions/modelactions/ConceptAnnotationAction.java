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

import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction;
import edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType;
import java.util.Optional;

/** The type Concept annotation action. */
public class ConceptAnnotationAction extends AbstractKnowtatorCollectionAction<ConceptAnnotation> {

  private final TextSource textSource;

  /**
   * Instantiates a new Concept annotation action.
   *
   * @param model the model
   * @param actionType the action type
   * @param textSource the text source
   */
  public ConceptAnnotationAction(
      KnowtatorModel model, CollectionActionType actionType, TextSource textSource) {
    super(model, actionType, "concept annotation", textSource.getConceptAnnotations());
    this.textSource = textSource;
  }

  @Override
  public void cleanUpAdd() {}

  @Override
  public void cleanUpRemove() {
    textSource
        .getGraphSpaces()
        .forEach(graphSpace -> graphSpace.getModel().removeListener(this, mxEvent.UNDO));
  }

  @Override
  public void prepareRemove() throws ActionUnperformable {
    super.prepareRemove();
    textSource
        .getGraphSpaces()
        .forEach(graphSpace -> graphSpace.getModel().addListener(mxEvent.UNDO, this));
  }

  @Override
  protected void prepareAdd() {
    Optional<Profile> profileOptional = model.getSelectedProfile();
    if (profileOptional.isPresent()) {
      Optional<String> owlClassOptional = model.getSelectedOwlClass();
      if (owlClassOptional.isPresent()) {
        ConceptAnnotation newConceptAnnotation =
            new ConceptAnnotation(
                textSource, null, owlClassOptional.get(), profileOptional.get(), "identity", "",
                model.getSelectedLayers());
        newConceptAnnotation.add(
            new Span(
                newConceptAnnotation,
                null,
                model.getSelection().getStart(),
                model.getSelection().getEnd()));
        setObject(newConceptAnnotation);
      } else {
        setMessage("No OWL Class selected. ");
      }
    } else {
      setMessage("No profile selected. ");
    }
  }
}
