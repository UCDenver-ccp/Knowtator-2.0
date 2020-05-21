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

import static edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction.assignColorToClass;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.Component;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionListener;

/** The type Color list. */
public class ColorList extends JList<String> implements KnowtatorComponent, ModelListener {

  private final ListSelectionListener lsl;
  private KnowtatorView view;

  /** Instantiates a new Color list. */
  public ColorList(KnowtatorView view) {
    this.view = view;
    setModel(new DefaultListModel<>());
    setCellRenderer(new ColorListRenderer<>());
    lsl = e -> assignColorToClass(view, getSelectedValue());
  }

  private void setCollection() {
    removeListSelectionListener(lsl);
    setModel(new DefaultListModel<>());
    view.getModel().flatMap(BaseModel::getSelectedProfile).ifPresent(profile ->
        profile.getColors().keySet().stream()
            .sorted()
            .forEach(
                o -> ((DefaultListModel<String>) getModel()).addElement(o)));

    addListSelectionListener(lsl);
  }

  @Override
  public void reset() {
    view.getModel().ifPresent(model -> model.addModelListener(this));
    setCollection();
  }

  @Override
  public void dispose() {
    view.getModel().ifPresent(model -> model.removeModelListener(this));
    setModel(new DefaultListModel<>());
  }

  @Override
  public void filterChangedEvent() {}

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    event
        .getNew()
        .filter(modelObject -> modelObject instanceof Profile)
        .ifPresent(modelObject -> setCollection());
  }

  @Override
  public void colorChangedEvent(Profile profile) {
    setCollection();
  }

  /**
   * The type Color list renderer.
   *
   * @param <O> the type parameter
   */
  class ColorListRenderer<O> extends JLabel implements ListCellRenderer<O> {

    /** Instantiates a new Color list renderer. */
    ColorListRenderer() {
      setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      view.getModel()
          .filter(model -> value instanceof String)
          .ifPresent(
              model -> {
                model
                    .getSelectedProfile()
                    .ifPresent(profile -> setBackground(profile.getColors().get(value)));
                setText(value.toString());
              });
      return this;
    }
  }
}
