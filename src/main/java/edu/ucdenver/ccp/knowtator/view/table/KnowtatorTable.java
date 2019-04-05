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

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;

/**
 * The type Knowtator table.
 *
 * @param <M> the type parameter
 */
public abstract class KnowtatorTable<M extends ModelObject> extends JTable
    implements KnowtatorComponent, ModelListener {

  KnowtatorView view;

  /** Instantiates a new Knowtator table. */
  KnowtatorTable() {
    getTableHeader().setFont(new Font(getFont().getName(), Font.BOLD, 16));
    setAutoCreateRowSorter(true);

    KnowtatorTable<M> table = this;

    addMouseListener(
        new MouseInputAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
              if (table.getSelectedRow() != -1) {
                reactToClick();
              }
            }
          }
        });
  }

  /** React to click. */
  public abstract void reactToClick();

  /**
   * Gets selected from model.
   *
   * @return the selected from model
   */
  protected abstract Optional<Object> getSelectedFromModel();

  /**
   * Gets selected value.
   *
   * @return the selected value
   */
  abstract Optional<M> getSelectedValue();

  /**
   * Add value.
   *
   * @param modelObject the model object
   */
  abstract void addValue(M modelObject);

  private void setSelected() {
    getSelectedFromModel()
        .ifPresent(
            cell -> {
              for (int i = 0; i < getModel().getRowCount(); i++) {
                Object element = getModel().getValueAt(i, 0);

                if (element == cell) {
                  setRowSelectionInterval(i, i);
                  this.scrollRectToVisible(getCellRect(i, 0, true));
                  return;
                }
              }
            });
  }

  /** Add elements from model. */
  public abstract void addElementsFromModel();

  @Override
  public void reset() {
    dispose();
    view.getModel().ifPresent(model -> model.addModelListener(this));
    addElementsFromModel();
    setSelected();
  }

  @Override
  public void dispose() {
    clearSelection();
    ((DefaultTableModel) getModel()).setRowCount(0);
    view.getModel().ifPresent(model -> model.removeModelListener(this));
  }

  /** React to model event. */
  public void reactToModelEvent() {
    ((DefaultListModel) getModel()).clear();
    addElementsFromModel();
    setSelected();
  }

  @Override
  public void filterChangedEvent() {
    reactToModelEvent();
  }

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    reactToModelEvent();
  }

  @Override
  public void colorChangedEvent() {
    reactToModelEvent();
  }

  @Override
  public void setView(KnowtatorView view) {
    this.view = view;
  }
}
