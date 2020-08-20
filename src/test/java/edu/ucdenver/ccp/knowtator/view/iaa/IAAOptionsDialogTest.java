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

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

class IAAOptionsDialogTest {

  @Test
  void mergeProjectsTest() throws IOException {
    File project1 = new File("/home/harrisonpl/Downloads/concepts+assertions_3_2 batch 1/CRAFT_assertions.knowtator");
    File project2 = new File("/home/harrisonpl/Downloads/CRAFT high-level concept annotation project/CRAFT_assertions.knowtator");

    KnowtatorModel model2 = new KnowtatorModel(project2, null);
    model2.load(model2.getProjectLocation());
    TestingHelpers.countCollections(model2,
        97,
        740,
        740,
        412,
        2,
        0,
        381,
        96,
        0);

    KnowtatorModel model1 = new KnowtatorModel(project1, null);
    model1.load(model1.getProjectLocation());
    TestingHelpers.countCollections(model1,
        97,
        718,
        718,
        412,
        1,
        18,
        381,
        96,
        0);

    KnowtatorModel mergeModel = IAAOptionsDialog.mergeProjects(project1, model2, null, true, null);
    TestingHelpers.countCollections(mergeModel,
        97,
        1458,
        1458,
        412,
        3,
        18,
        381,
        96,
        0);
  }

  @Test
  public void mergeProjectsTest2() throws ActionUnperformable, IOException {
    String projectName = "import_test_project1";

    File projectDirectory = new File(
        TestingHelpers.class
            .getResource(String.format("/%s/%s.knowtator", projectName, projectName))
            .getFile()).getParentFile();
    File tempProjectDir = Files.createTempDir();
    FileUtils.copyDirectory(projectDirectory, tempProjectDir);
    KnowtatorModel model = new KnowtatorModel(tempProjectDir, null);
    model.load(model.getProjectLocation());

    TestingHelpers.countCollections(
        model,
        1,
        1,
        1,
        0,
        1,
        0,
        0,
        0,
        0);
  }
}