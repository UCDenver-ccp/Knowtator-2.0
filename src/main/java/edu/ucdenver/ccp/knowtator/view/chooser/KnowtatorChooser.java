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

package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;

/**
 * The type Knowtator chooser.
 *
 * @param <K> the type parameter
 */
public abstract class KnowtatorChooser<K extends ModelObject> extends JComboBox<K>
    implements KnowtatorComponent, ModelListener {

  private final ActionListener al;
  private KnowtatorCollection<K> collection;
  /** The View. */
  final KnowtatorView view;

  /** Instantiates a new Knowtator chooser. */
  KnowtatorChooser(KnowtatorView view) {
    this.view = view;
    al =
        e -> {
          JComboBox comboBox = (JComboBox) e.getSource();
          if (comboBox.getSelectedItem() != null) {
            this.collection.setSelection(getItemAt(getSelectedIndex()));
          }
        };
  }

  @Override
  public void reset() {
    view.getModel().ifPresent(model -> model.addModelListener(this));
  }

  /** React. */
  protected abstract void react();

  /**
   * Sets collection.
   *
   * @param collection the collection
   */
  void setCollection(KnowtatorCollection<K> collection) {
    removeAllItems();

    this.collection = collection;
    if (collection.size() == 0) {
      setEnabled(false);
    } else {
      setEnabled(true);
      removeActionListener(al);
      collection.forEach(this::addItem);
      addActionListener(al);
    }
  }

  /** Sets selected. */
  void setSelected() {
    view.getModel()
        .filter(BaseModel::isNotLoading)
        .ifPresent(
            model -> {
              removeActionListener(al);
              collection.getSelection().ifPresent(this::setSelectedItem);
              addActionListener(al);
            });
  }

  @Override
  public void dispose() {
    removeAllItems();
    setSelectedItem(null);
    view.getModel().ifPresent(model -> model.removeModelListener(this));
  }

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    react();
  }

  @Override
  public void filterChangedEvent() {
    react();
  }

  @Override
  public void colorChangedEvent(Profile profile) {
    react();
  }
}
