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

package edu.ucdenver.ccp.knowtator.view.label;

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import javax.swing.JLabel;

/** The type Knowtator label. */
public abstract class KnowtatorLabel extends JLabel implements KnowtatorComponent, ModelListener {

  /** The View. */
  KnowtatorView view;

  /**
   * Instantiates a new Knowtator label.
   *
   * @param view the view
   */
  KnowtatorLabel(KnowtatorView view) {
    this.view = view;
  }

  /** React. */
  protected abstract void react();

  @Override
  public void reset() {
    view.getModel().ifPresent(model -> model.addModelListener(this));
  }

  @Override
  public void dispose() {

    view.getModel().ifPresent(model -> model.removeModelListener(this));
  }

  @Override
  public void filterChangedEvent() {
    react();
  }

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    react();
  }

  @Override
  public void colorChangedEvent() {
    react();
  }
}
