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

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.ConceptAnnotationAction;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

/**
 * The type Annotation table.
 */
public class AnnotationTable extends KnowtatorTable<ConceptAnnotation> {

  /**
   * Instantiates a new Annotation table.
   */
  AnnotationTable(KnowtatorView view) {
    super(view);
    setModel(
        new DefaultTableModel(
            new Object[][] {}, new String[] {"Spanned Text", "OWL Entity", "Text Source"}) {
          @Override
          public boolean isCellEditable(int row, int col) {
            return false;
          }
        });
  }

  @Override
  public void reactToRightClick(MouseEvent e) {
    new SelectionPopupMenu(e).showPopUpMenu();
  }

  @Override
  public void reactToSelection() {
    List<ConceptAnnotation> selected = getSelectedValues();
    if (selected.size() > 1) {
      view.getModel()
          .map(BaseModel::getTextSources)
          .ifPresent(textSources -> textSources.stream()
              .map(TextSource::getConceptAnnotations)
              .forEach(ConceptAnnotationCollection::clearSelection));
    }
    selected.stream()
        .collect(Collectors.groupingBy(ConceptAnnotation::getTextSource))
        .forEach((textSource, conceptAnnotations) -> textSource.getConceptAnnotations().setSelection(conceptAnnotations));
  }

  @Override
  public void reactToClick() {
    List<ConceptAnnotation> selected = getSelectedValues();
    if (selected.size() == 1) {
      ConceptAnnotation conceptAnnotation = getSelectedValues().get(0);
      view.getModel()
          .ifPresent(
              model -> {
                if (!model
                    .getTextSources()
                    .getOnlySelected()
                    .map(textSource -> textSource.equals(conceptAnnotation.getTextSource()))
                    .orElse(false)) {
                  model.getTextSources().selectOnly(conceptAnnotation.getTextSource());
                }
              });
      conceptAnnotation.getTextSource().getConceptAnnotations().selectOnly(conceptAnnotation);
    }
  }

  @Override
  public void reactToModelEvent(Optional<ChangeEvent<ModelObject>> event) {
    event
        .filter(event1 -> !(event1 instanceof SelectionEvent))
        .map(ChangeEvent::getNew)
        .flatMap(modelObjects -> modelObjects.stream()
            .filter(modelObject -> modelObject instanceof ConceptAnnotation)
            .findAny()).ifPresent(modelObject -> {
          clearSelection();
          ((DefaultTableModel) getModel()).setRowCount(0);
          addElementsFromModel();
          setSelected();
        });
  }

  @Override
  protected Optional<List<ConceptAnnotation>> getSelectedFromModel() {
    return view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .map(TextSource::getConceptAnnotations)
        .map(ConceptAnnotationCollection::getSelection);
  }

  @Override
  List<ConceptAnnotation> getSelectedValues() {
    int column = getTableHeader().getColumnModel().getColumnIndex("Spanned Text");
    return Arrays.stream(getSelectedRows())
        .mapToObj(row -> (ConceptAnnotation) getValueAt(row, column))
        .collect(Collectors.toList());
  }

  @Override
  void addValue(ConceptAnnotation modelObject) {
    ((DefaultTableModel) getModel())
        .addRow(
            new Object[] {
                modelObject, modelObject.getOwlClassRendering(), modelObject.getTextSource()
            });
  }

  @Override
  public void addElementsFromModel() {
    view.getModel()
        .map(BaseModel::getTextSources)
        .map(TextSourceCollection::stream)
        .ifPresent(
            textSources ->
                textSources
                    .map(TextSource::getConceptAnnotations)
                    .flatMap(ConceptAnnotationCollection::stream)
                    .forEach(this::addValue));
  }

  /**
   * The type Annotation popup menu.
   */
  class SelectionPopupMenu extends JPopupMenu {
    private final MouseEvent mouseEvent;

    /**
     * Instantiates a new Annotation popup menu.
     *
     * @param mouseEvent the mouse event
     */
    SelectionPopupMenu(MouseEvent mouseEvent) {
      this.mouseEvent = mouseEvent;
    }

    private JMenuItem removeAnnotationsCommand() {
      JMenuItem removeAnnotationMenuItem =
          new JMenuItem("Delete selected annotations");
      removeAnnotationMenuItem.addActionListener(actionEvent -> view.getModel()
          .ifPresent(model -> {
            ArrayList<ConceptAnnotationAction> actions = new ArrayList<>();
            for (ConceptAnnotation conceptAnnotation : getSelectedValues()) {
              ConceptAnnotationAction action = new ConceptAnnotationAction(model, REMOVE, conceptAnnotation.getTextSource());
              action.setObject(conceptAnnotation);
              try {
                action.setSignificant(false);
                model.registerAction(action);
                actions.add(action);
              } catch (ActionUnperformable e) {
                throw new RuntimeException(e);
              }
            }
            ConceptAnnotationAction firstAction = actions.get(0);
            firstAction.setSignificant(true);
            reset();
          }));

      return removeAnnotationMenuItem;
    }

    void showPopUpMenu() {
      add(removeAnnotationsCommand());
      show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());

    }
  }
}
