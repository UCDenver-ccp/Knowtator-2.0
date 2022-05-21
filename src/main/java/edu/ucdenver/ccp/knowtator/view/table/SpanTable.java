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

package edu.ucdenver.ccp.knowtator.view.table;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.DefaultTableModel;

/** The type Span list. */
public class SpanTable extends KnowtatorTable<Span> {

  /**
   * Instantiates a new Knowtator list.
   *
   * @param view The Knowtator view
   */
  public SpanTable(KnowtatorView view) {
    super(view);
    setModel(
        new DefaultTableModel(
            new Object[][] {}, new String[] {"Spanned Text", "Start", "End"}) {
          @Override
          public boolean isCellEditable(int row, int col) {
            return false;
          }
        });
  }

  @Override
  public void reactToClick() {
    Optional<Span> spanOptional = getSelectedValue();
    spanOptional.ifPresent(
        span -> {
          view.getModel()
              .ifPresent(model -> model.getTextSources().selectOnly(span.getTextSource()));
          span.getTextSource().getConceptAnnotations().selectOnly(span.getConceptAnnotation());
          span.getConceptAnnotation().selectOnly(span);
        });
  }

  @Override
  public void addElementsFromModel() {
    view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .flatMap(textSource -> textSource.getConceptAnnotations().getOnlySelected())
        .ifPresent(conceptAnnotation -> conceptAnnotation.forEach(this::addValue));
  }

  @Override
  protected Optional<List<Span>> getSelectedFromModel() {
    return view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .flatMap(textSource -> textSource.getConceptAnnotations().getOnlySelected())
        .flatMap(ConceptAnnotation::getOnlySelected)
        .map(span -> {
          ArrayList<Span> l = new ArrayList<>();
          l.add(span);
          return l;
        });
  }

  @Override
  Optional<Span> getSelectedValue() {
    return Optional.empty();
  }

  @Override
  void addValue(Span modelObject) {
    ((DefaultTableModel) getModel())
        .addRow(
            new Object[] {
              modelObject, modelObject.getStart(), modelObject.getEnd()
            });
  }
}
