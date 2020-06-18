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

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.OwlModel;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.BasicStroke;
import java.awt.Component;
import java.util.Enumeration;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;

/**
 * The type Color list.
 */
public class ColorList extends JList<String[]> implements KnowtatorComponent, ModelListener, OWLSelectionModelListener {

  private final ListSelectionListener lsl;
  private final KnowtatorView view;
  private static final Logger log = Logger.getLogger(ColorList.class);

  /**
   * Instantiates a new Color list.
   */
  public ColorList(KnowtatorView view) {
    this.view = view;
    setModel(new DefaultListModel<>());
    setCellRenderer(new ColorListRenderer<>());
    lsl = e -> assignColorToClass(view, getSelectedValue()[0]);
  }

  private void setCollection() {
    removeListSelectionListener(lsl);
    setModel(new DefaultListModel<>());
    view.getModel().ifPresent(model -> model.getSelectedProfile().ifPresent(profile ->
        profile.getColors().keySet().stream()
            .sorted()
            .forEach(o ->
                ((DefaultListModel<String[]>) getModel()).addElement(new String[] {o, model.getOwlEntityRendering(o)}))));
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
  public void filterChangedEvent() {
  }

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

  @Override
  public void selectionChanged() {
    Optional<String> owlClassOptional = view.getModel().flatMap(OwlModel::getSelectedOwlClass);

    owlClassOptional.ifPresent(owlClass -> {
      setCollection();
      int i = ((DefaultListModel) getModel()).indexOf(owlClass);
      i = 0;
      Enumeration<String[]> e = ((DefaultListModel<String[]>) getModel()).elements();
      while(e.hasMoreElements()) {
        if(owlClass.equals(e.nextElement()[0])) {
          break;
        }
        i++;
      }

      log.warn(owlClass);
      log.warn(i);
      if (-1 < i) {
        log.warn("here");
        scrollRectToVisible(getCellBounds(i, i));
      }
    });
  }

  /**
   * The type Color list renderer.
   *
   * @param <O> the type parameter
   */
  class ColorListRenderer<O> extends JLabel implements ListCellRenderer<O> {

    /**
     * Instantiates a new Color list renderer.
     */
    ColorListRenderer() {
      setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      view.getModel()
          .filter(model -> value instanceof String[])
          .ifPresent(
              model -> {
                String[] val = (String[]) value;
                Optional<String> owlClass = model.getSelectedOwlClass();
                if (owlClass.isPresent() && val[0].equals(owlClass.get())) {
                  setBorder(BorderFactory.createStrokeBorder(new BasicStroke(2)));
                } else {
                  setBorder(null);
                }
                model
                    .getSelectedProfile()
                    .ifPresent(profile -> setBackground(profile.getColors().get(val[0])));
                setText(val[1]);
              });
      return this;
    }
  }
}
