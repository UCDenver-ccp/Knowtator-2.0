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

package edu.ucdenver.ccp.knowtator.view.iaa;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class IAAOptionsDialogTest {

  private void checkMerge(String projectName1, TestingHelpers.ProjectCounts project1Counts, String projectName2, TestingHelpers.ProjectCounts project2Counts, TestingHelpers.ProjectCounts overlapCounts) throws IOException {
    KnowtatorModel model1  = TestingHelpers.getLoadedModel(projectName1);
    TestingHelpers.countCollections(model1, project1Counts);

    KnowtatorModel model2 = TestingHelpers.getLoadedModel(projectName2);
    TestingHelpers.countCollections(model2, project2Counts);

    model2.load(model1.getProjectLocation());
    TestingHelpers.ProjectCounts mergeCounts = project2Counts.add(project1Counts, overlapCounts);
    TestingHelpers.countCollections(model2, mergeCounts);

  }

  @Test
  public void mergeProjectsTest() throws ActionUnperformable, IOException {
    String projectName1 = "import_test_project1";

    TestingHelpers.ProjectCounts model1Counts = new TestingHelpers.ProjectCounts(1, 1, 1, 0 , 1, 0 , 0, 0, 0);

    String projectName2 = "import_test_project2";
    TestingHelpers.ProjectCounts model2Counts = new TestingHelpers.ProjectCounts(1, 2, 1, 0, 1, 0, 0, 0, 0);

    TestingHelpers.ProjectCounts overlaps = new TestingHelpers.ProjectCounts(1,1,1,0,1,0,0,0,0);
    checkMerge(projectName1, model1Counts, projectName2, model2Counts, overlaps);
  }
}