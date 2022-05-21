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

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextBoundModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import org.apache.log4j.Logger;

/** The type Text source collection. */
public class TextSourceCollection extends KnowtatorCollection<TextSource> implements ModelListener {
  @SuppressWarnings("unused")
  private final Logger log = Logger.getLogger(TextSourceCollection.class);

  /**
   * Instantiates a new Text source collection.
   *
   * @param model the model
   */
  public TextSourceCollection(KnowtatorModel model) {
    super(model);
    model.addModelListener(this);
  }

  @Override
  public void add(TextSource textSource) {
    if (!get(textSource.getId()).isPresent()) {
      if (textSource.getTextFile().exists()) {
        super.add(textSource);
      }
    }
  }

  @Override
  public void selectOnly(TextSource textSource) {
    super.selectOnly(textSource);
  }

  @Override
  public void remove(TextSource textSource) {
    getOnlySelected()
        .filter(textSource1 -> textSource1.equals(textSource))
        .ifPresent(textSource1 -> selectPrevious());
    super.remove(textSource);
  }

  @Override
  public void filterChangedEvent() {}

  @Override
  public void colorChangedEvent(Profile profile) {}

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    event
        .getNew()
        .filter(modelObject -> modelObject instanceof TextBoundModelObject)
        .ifPresent(
            modelObject -> selectOnly(((TextBoundModelObject) modelObject).getTextSource()));
  }
}
