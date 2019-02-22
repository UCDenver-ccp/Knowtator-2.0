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

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/** The type Annotation table for spanned text. */
public class AnnotationTableForSpannedText extends AnnotationTable {

  private final JCheckBox exactMatchCheckBox;
  private final JTextField annotationsContainingTextTextField;

  /**
   * Instantiates a new Annotation table for spanned text.
   *
   * @param view the view
   * @param exactMatchCheckBox the exact match check box
   * @param annotationsContainingTextTextField the annotations containing text text field
   */
  public AnnotationTableForSpannedText(
      KnowtatorView view,
      JCheckBox exactMatchCheckBox,
      JTextField annotationsContainingTextTextField) {
    super(view);
    this.exactMatchCheckBox = exactMatchCheckBox;
    this.annotationsContainingTextTextField = annotationsContainingTextTextField;
  }

  @Override
  public void addElementsFromModel() {
    view.getModel()
        .ifPresent(
            model ->
                model
                    .getTextSources()
                    .forEach(
                        textSource ->
                            textSource.getConceptAnnotations().stream()
                                .filter(
                                    conceptAnnotation ->
                                        (exactMatchCheckBox.isSelected()
                                                && conceptAnnotation
                                                    .getSpannedText()
                                                    .equals(
                                                        annotationsContainingTextTextField
                                                            .getText()))
                                            || (!exactMatchCheckBox.isSelected()
                                                && conceptAnnotation
                                                    .getSpannedText()
                                                    .contains(
                                                        annotationsContainingTextTextField
                                                            .getText())))
                                .forEach(this::addValue)));
  }
}
