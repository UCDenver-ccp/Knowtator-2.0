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

import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/** The type Relation table. */
public class RelationTable extends KnowtatorTable<RelationAnnotation> {
  private final Set<String> activeOwlPropertyDescendants;
  private final JCheckBox includePropertyDescendantsCheckBox;
  private final JLabel owlPropertyLabel;

  /**
   * Instantiates a new Relation table.
   *
   * @param includePropertyDescendantsCheckBox the include property descendants check box
   * @param owlPropertyLabel the owl property label
   */
  public RelationTable(
      KnowtatorView view, JCheckBox includePropertyDescendantsCheckBox, JLabel owlPropertyLabel) {
    super(view);
    setModel(
        new DefaultTableModel(
            new Object[][] {},
            new String[] {
              "Subject Text",
              "Subject OWL Class",
              "Property",
              "Object Text",
              "Object OWL Class",
              "Text Source"
            }) {
          @Override
          public boolean isCellEditable(int row, int col) {
            return false;
          }
        });

    this.activeOwlPropertyDescendants = new HashSet<>();
    this.includePropertyDescendantsCheckBox = includePropertyDescendantsCheckBox;
    this.owlPropertyLabel = owlPropertyLabel;
  }

  @Override
  protected Optional<List<RelationAnnotation>> getSelectedFromModel() {
    Optional<Object[]> cells = view.getModel()
        .filter(BaseModel::isNotLoading)
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .flatMap(textSource -> textSource.getGraphSpaces().getOnlySelected())
        .map(mxGraph::getSelectionCells);
    if (cells.isPresent() && Arrays.stream(cells.get())
          .allMatch(cell -> cell instanceof  RelationAnnotation)) {
      return cells.map(cells2 -> (List<RelationAnnotation>) Arrays.stream(cells2).map(cell -> (RelationAnnotation) cell));
    } else {
      return Optional.empty();
    }
  }

  @Override
  Optional<RelationAnnotation> getSelectedValue() {
    return Optional.ofNullable((RelationAnnotation) getValueAt(getSelectedRow(), 2));
  }

  @Override
  void addValue(RelationAnnotation modelObject) {
    ((DefaultTableModel) getModel())
        .addRow(
            new Object[] {
              ((AnnotationNode) modelObject.getSource()).getConceptAnnotation(),
              ((AnnotationNode) modelObject.getSource())
                  .getConceptAnnotation()
                  .getOwlClassRendering(),
              modelObject,
              ((AnnotationNode) modelObject.getTarget()).getConceptAnnotation(),
              ((AnnotationNode) modelObject.getTarget())
                  .getConceptAnnotation()
                  .getOwlClassRendering(),
              modelObject.getTextSource()
            });
  }

  @Override
  public void addElementsFromModel() {
    view.getModel()
        .ifPresent(
            model ->
                model
                    .getTextSources()
                    .forEach(
                        textSource ->
                            textSource
                                .getGraphSpaces()
                                .forEach(
                                    graphSpace ->
                                        graphSpace.getRelationAnnotations().stream()
                                            .filter(
                                                relationAnnotation ->
                                                    activeOwlPropertyDescendants.contains(
                                                        relationAnnotation.getProperty()))
                                            .forEach(this::addValue))));
  }

  @Override
  public void reactToClick() {
    Optional<RelationAnnotation> relationAnnotationOptional = getSelectedValue();

    relationAnnotationOptional.ifPresent(
        relationAnnotation -> {
          view.getModel()
              .ifPresent(
                  model -> model.getTextSources().selectOnly(relationAnnotation.getTextSource()));
          relationAnnotation
              .getTextSource()
              .getGraphSpaces()
              .selectOnly(relationAnnotation.getGraphSpace());
          relationAnnotation.getGraphSpace().setSelectionCell(relationAnnotation);
        });
  }

  @Override
  public void reactToModelEvent() {}

  @Override
  public void reset() {
    activeOwlPropertyDescendants.clear();

    view.getModel()
        .ifPresent(
            model ->
                model
                    .getSelectedOwlObjectProperty()
                    .ifPresent(
                        owlObjectProperty -> {
                          activeOwlPropertyDescendants.add(owlObjectProperty);
                          if (includePropertyDescendantsCheckBox.isSelected()) {
                            activeOwlPropertyDescendants.addAll(
                                model.getOwlObjectPropertyDescendants(owlObjectProperty));
                          }
                          owlPropertyLabel.setText(model.getOwlEntityRendering(owlObjectProperty));
                        }));
    super.reset();
  }
}
