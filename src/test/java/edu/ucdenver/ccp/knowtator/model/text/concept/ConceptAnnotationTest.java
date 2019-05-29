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

package edu.ucdenver.ccp.knowtator.model.text.concept;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ConceptAnnotationTest {
  private static KnowtatorModel model;

  static {
    try {
      model = TestingHelpers.getLoadedModel();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void getSizeTest() {
    TextSource textSource = model.getTextSources().get("document1").get();
    ConceptAnnotation conceptAnnotation = textSource.firstConceptAnnotation().get();
    assert conceptAnnotation.getSize() == 4;
  }

  @Test
  void getSpannedTextTest() {
    TextSource textSource = model.getTextSources().get("document1").get();
    ConceptAnnotation conceptAnnotation = textSource.firstConceptAnnotation().get();
    assert conceptAnnotation.getSpannedText().equals("This");
  }

  @Test
  void containsTest() {
    TextSource textSource = model.getTextSources().get("document1").get();
    ConceptAnnotation conceptAnnotation = textSource.firstConceptAnnotation().get();
    assert conceptAnnotation.contains(0);
    assert conceptAnnotation.contains(2);
    assert conceptAnnotation.contains(3);
    assert !conceptAnnotation.contains(4);
    assert !conceptAnnotation.contains(5);
    assert !conceptAnnotation.contains(100);
    assert !conceptAnnotation.contains(-1);
  }
}
