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

package edu.ucdenver.ccp.knowtator.view.actions.modelactions;

import edu.ucdenver.ccp.knowtator.model.FilterType;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;

/** The type Filter action. */
public class FilterAction extends AbstractKnowtatorAction {

  private final boolean isFilter;
  private final boolean previousIsFilter;
  private final FilterType filterType;

  /**
   * Instantiates a new Filter action.
   *
   * @param model the model
   * @param filterType the filter type
   * @param isFilter the is filter
   */
  public FilterAction(KnowtatorModel model, FilterType filterType, boolean isFilter) {
    super(model, "Change filterType");
    this.filterType = filterType;
    this.isFilter = isFilter;
    this.previousIsFilter = model.isFilter(filterType);
  }

  @Override
  public void execute() {
    model.setFilter(filterType, isFilter);
  }

  @Override
  public void undo() {
    model.setFilter(filterType, previousIsFilter);
  }

  @Override
  public void redo() {
    model.setFilter(filterType, isFilter);
  }
}
