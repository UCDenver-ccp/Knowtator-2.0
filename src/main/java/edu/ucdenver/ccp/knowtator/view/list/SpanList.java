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

package edu.ucdenver.ccp.knowtator.view.list;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.util.Optional;

/** The type Span list. */
public class SpanList extends KnowtatorList<Span> {


  @Override
  public void reactToClick() {
    Optional<Span> spanOptional = Optional.ofNullable(getSelectedValue());
    spanOptional.ifPresent(
        span -> {
          view.getModel()
              .ifPresent(model -> model.getTextSources().setSelection(span.getTextSource()));
          span.getTextSource().setSelectedConceptAnnotation(span.getConceptAnnotation());
          span.getConceptAnnotation().setSelection(span);
        });
  }

  @Override
  protected void addElementsFromModel() {
    view.getModel()
        .flatMap(BaseModel::getSelectedTextSource)
        .flatMap(TextSource::getSelectedAnnotation)
        .ifPresent(
            conceptAnnotation ->
                conceptAnnotation.forEach(span -> getDefaultListModel().addElement(span)));
  }

  @Override
  protected Optional<Span> getSelectedFromModel() {
    return view.getModel()
        .flatMap(BaseModel::getSelectedTextSource)
        .flatMap(TextSource::getSelectedAnnotation)
        .flatMap(ConceptAnnotation::getSelection);
  }
}
