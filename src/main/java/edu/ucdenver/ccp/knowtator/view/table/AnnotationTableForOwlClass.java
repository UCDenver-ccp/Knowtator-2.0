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

import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/** The type Annotation table for owl class. */
public class AnnotationTableForOwlClass extends AnnotationTable {
  private final JCheckBox includeClassDescendantsCheckBox;
  private final JLabel owlClassLabel;
  private final Set<String> activeOwlClassDescendants;

  /**
   * Instantiates a new Annotation table for owl class.
   *
   * @param includeClassDescendantsCheckBox the include class descendants check box
   * @param owlClassLabel the owl class label
   */
  public AnnotationTableForOwlClass(
      JCheckBox includeClassDescendantsCheckBox, JLabel owlClassLabel) {
    this.includeClassDescendantsCheckBox = includeClassDescendantsCheckBox;
    this.owlClassLabel = owlClassLabel;
    this.activeOwlClassDescendants = new HashSet<>();
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
                                        activeOwlClassDescendants.contains(
                                            conceptAnnotation.getOwlClass()))
                                .forEach(this::addValue)));
  }

  @Override
  public void reset() {
    activeOwlClassDescendants.clear();
    view.getModel()
        .ifPresent(
            model ->
                model
                    .getSelectedOwlClass()
                    .ifPresent(
                        owlClass -> {
                          activeOwlClassDescendants.add(owlClass);
                          if (includeClassDescendantsCheckBox.isSelected()) {
                            activeOwlClassDescendants.addAll(
                                model.getOwlClassDescendants(owlClass));
                          }
                          owlClassLabel.setText(model.getOwlEntityRendering(owlClass));
                        }));

    super.reset();
  }
}
