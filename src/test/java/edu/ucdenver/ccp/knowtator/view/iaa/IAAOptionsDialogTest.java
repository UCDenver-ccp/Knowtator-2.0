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
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class IAAOptionsDialogTest {

  @Test
  void mergeProjectsTest() throws IOException {
    File project1 = new File("/home/harrisonpl/Downloads/concepts+assertions_3_2 batch 1/CRAFT_assertions.knowtator");
    File project2 = new File("/home/harrisonpl/Downloads/CRAFT high-level concept annotation project/CRAFT_assertions.knowtator");

    KnowtatorModel model2 = new KnowtatorModel(project2, null);
    model2.load(model2.getProjectLocation());

    TestingHelpers.ProjectCounts model2Counts = new TestingHelpers.ProjectCounts(97, 740, 740, 412, 2, 0, 381, 96, 0);
    TestingHelpers.countCollections(model2, model2Counts);

    KnowtatorModel model1 = new KnowtatorModel(project1, null);
    model1.load(model1.getProjectLocation());
    TestingHelpers.ProjectCounts model1Counts = new TestingHelpers.ProjectCounts(97, 718, 718, 412, 1, 18, 381, 96, 0);
    TestingHelpers.countCollections(model1, model1Counts);

    KnowtatorModel mergeModel = IAAOptionsDialog.mergeProjects(project1, model2, null, true, null);
    TestingHelpers.ProjectCounts overlapCounts = new TestingHelpers.ProjectCounts(97, 718, 718, 412, 0, 0, 0, 0, 0);
    TestingHelpers.ProjectCounts mergeModelCounts = model1Counts.add(model2Counts, overlapCounts);
    TestingHelpers.countCollections(mergeModel, mergeModelCounts);
//    97,
//        1458,
//        1458,
//        412,
//        3,
//        18,
//        381,
//        96,
//        0
  }

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
  public void mergeProjectsTest2() throws ActionUnperformable, IOException {
    String projectName1 = "import_test_project1";

    KnowtatorModel model1  = TestingHelpers.getLoadedModel(projectName1);

    TestingHelpers.ProjectCounts model1Counts = new TestingHelpers.ProjectCounts(1, 1, 1, 0 , 1, 0 , 0, 0, 0);

    TestingHelpers.countCollections(model1,model1Counts);

    String projectName2 = "import_test_project1";
    KnowtatorModel model2 = TestingHelpers.getLoadedModel(projectName2);
  }
}