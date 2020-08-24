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

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** The type Annotation notes. */
public class AnnotationNotes extends JTextArea implements KnowtatorComponent, ModelListener {
  private ConceptAnnotation conceptAnnotation;
  private KnowtatorView view;

  /**
   * Instantiates a new Annotation notes.
   *
   */
  AnnotationNotes(KnowtatorView view) {
    this.view = view;
    addKeyListener(
        new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
            if (getText().length() > 100) {
              e.consume();
            }
          }

          @Override
          public void keyPressed(KeyEvent e) {}

          @Override
          public void keyReleased(KeyEvent e) {}
        });

    getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {}

              @Override
              public void removeUpdate(DocumentEvent e) {}

              @Override
              public void changedUpdate(DocumentEvent e) {
                conceptAnnotation.setMotivation(getText());
              }
            });
  }

  @Override
  public void reset() {
    view.getModel().ifPresent(model -> model.addModelListener(this));
  }

  @Override
  public void dispose() {
    view.getModel().ifPresent(model -> model.removeModelListener(this));
  }

  @Override
  public void filterChangedEvent() {}

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    react();
  }

  private void react() {
    view.getModel()
        .flatMap(BaseModel::getSelectedTextSource)
        .ifPresent(
            textSource -> {
              if (textSource.getSelectedAnnotation().isPresent()) {
                conceptAnnotation = textSource.getSelectedAnnotation().get();
                setEnabled(true);
                setText(conceptAnnotation.getMotivation());
              } else {
                setEnabled(false);
                setText("");
              }
            });
  }

  @Override
  public void colorChangedEvent(Profile profile) {}
}
