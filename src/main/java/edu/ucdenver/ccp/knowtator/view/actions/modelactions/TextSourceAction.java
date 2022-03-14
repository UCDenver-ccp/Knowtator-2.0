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

import edu.ucdenver.ccp.knowtator.io.XmlUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXmlUtil;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction;
import edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/** The type Text source action. */
public class TextSourceAction extends AbstractKnowtatorCollectionAction<TextSource> {
  private final File file;
  private final File annotationFile;

  /**
   * Instantiates a new Text source action.
   *
   * @param model the model
   * @param actionType the action type
   * @param file the file
   * @param annotationFile the annotation file
   */
  public TextSourceAction(
      KnowtatorModel model, CollectionActionType actionType, File file, File annotationFile) {
    super(model, actionType, "text source", model.getTextSources());
    this.file = file;
    this.annotationFile = annotationFile;
  }

  @Override
  protected void prepareAdd() {
    if (!file.getParentFile().equals(BaseModel.getArticlesLocation(model.getProjectLocation()))) {
      try {
        FileUtils.copyFile(file, new File(BaseModel.getArticlesLocation(model.getProjectLocation()), file.getName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    setObject(new TextSource(model, file, null));
  }

  @Override
  protected void cleanUpRemove() {}

  @Override
  protected void cleanUpAdd() {
    if (annotationFile != null) {
      XmlUtil xmlUtil = new KnowtatorXmlUtil();
      xmlUtil.readToTextSource(object, annotationFile);
    }
  }
}
