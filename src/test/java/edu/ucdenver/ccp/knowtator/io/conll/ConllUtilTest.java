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

package edu.ucdenver.ccp.knowtator.io.conll;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ConllUtilTest {

  @Test
  void readToStructureAnnotations() throws IOException {
    KnowtatorModel model = TestingHelpers.getLoadedModel("structure_test_project");
    TestingHelpers.countCollections(model,
        1,
        0,
        0,
        0,
        1,
        0,
        0,
        0,
        8072,
        361,
        8072,
        7711);
  }

  @Test
  void readConceptsAndAssertions() throws IOException {
    KnowtatorModel model = TestingHelpers.getLoadedModel("concepts+assertions");
    TestingHelpers.countCollections(model,
        1,
        643,
        660,
        49,
        1,
        0,
        157,
        91,
        0,
        0,
        0,
        0);
  }

//  @Test
//  void readBioCreativeProject() throws  IOException {
//    KnowtatorModel model = TestingHelpers.getLoadedModel("biocreative_chemprot");
//    TestingHelpers.countCollections(model,
//        1,
//        0,
//        0,
//        0,
//        1,
//        0,
//        0,
//        0,
//        8072,
//        361,
//        8072,
//        7711);
//  }
}
