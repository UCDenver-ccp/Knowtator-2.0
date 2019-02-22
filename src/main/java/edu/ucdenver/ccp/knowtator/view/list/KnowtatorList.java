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

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * The type Knowtator list.
 *
 * @param <K> the type parameter
 */
public abstract class KnowtatorList<K extends ModelObject> extends JList<K>
    implements KnowtatorComponent, ModelListener {

  /** The View. */
  final KnowtatorView view;

  /**
   * Instantiates a new Knowtator list.
   *
   * @param view the view
   */
  KnowtatorList(KnowtatorView view) {
    this.view = view;
    setModel(new DefaultListModel<>());

    KnowtatorList<K> list = this;

    addMouseListener(
        new MouseInputAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
              if (list.getSelectedIndex() != -1) {
                reactToClick();
              }
            }
          }
        });
  }

  /**
   * Gets default list model.
   *
   * @return the default list model
   */
  DefaultListModel<K> getDefaultListModel() {
    return (DefaultListModel<K>) getModel();
  }

  /** React to click. */
  public abstract void reactToClick();

  /**
   * Gets selected from model.
   *
   * @return the selected from model
   */
  protected abstract Optional<K> getSelectedFromModel();

  private void setSelected() {
    getSelectedFromModel()
        .ifPresent(
            cell -> {
              for (int i = 0; i < getModel().getSize(); i++) {
                K element = getModel().getElementAt(i);
                if (element == cell) {
                  setSelectedIndex(i);
                  ensureIndexIsVisible(i);
                  return;
                }
              }
            });
  }

  /** Add elements from model. */
  abstract void addElementsFromModel();

  @Override
  public void reset() {
    dispose();
    view.getModel().ifPresent(model -> model.addModelListener(this));
    addElementsFromModel();
    setSelected();
  }

  @Override
  public void dispose() {
    ((DefaultListModel) getModel()).clear();
    view.getModel().ifPresent(model -> model.removeModelListener(this));
  }

  /** React to model event. */
  void reactToModelEvent() {
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
}
