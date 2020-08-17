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

package edu.ucdenver.ccp.knowtator;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("EmptyMethod")
class ProjectManagerTest {

  private static KnowtatorModel model;

  @BeforeEach
  void setup() throws IOException {
    model = TestingHelpers.getLoadedModel();
  }

  @Test
  void loadProjectTest() {
    TestingHelpers.checkDefaultCollectionValues(model);
    model.load(model.getProjectLocation());
  }

  private static List<String> fileToLines(File file) {
    List<String> lines = new LinkedList<>();
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      while ((line = in.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lines;
  }

  @Test
  void saveProjectTest() {
    TestingHelpers.checkDefaultCollectionValues(model);
    model.save();
    TestingHelpers.checkDefaultCollectionValues(model);
    File file1 = new File(BaseModel.getAnnotationsLocation(model.getProjectLocation()), "document1.xml");
    File referenceFile =
        new File(
            TestingHelpers.class
                .getResource("/test_project_using_uris/Annotations/document1.xml")
                .getFile());

    List<String> original = fileToLines(referenceFile);
    List<String> revised = fileToLines(file1);

    Patch patch = DiffUtils.diff(original, revised);

    try {
      assert patch.getDeltas().size() == 0;
    } catch (AssertionError e) {
      for (Delta delta : patch.getDeltas()) {
        System.out.println(delta);
      }
      throw e;
    }
  }

  @Test
  void newProjectTest() {}

  @Test
  void importToManagerTest() {}

  @Test
  void importProjectTest() throws IOException {
    TestingHelpers.checkDefaultCollectionValues(model);
    String projectName = "iaa_test_project";
    File projectFile = new File(
        TestingHelpers.class
            .getResource(String.format("/%s/%s.knowtator", projectName, projectName))
            .getFile());
    projectFile = BaseModel.validateProjectLocation(projectFile);
    model.load(projectFile);
    TestingHelpers.countCollections(model,
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations,
        TestingHelpers.defaultExpectedSpans,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes,
        TestingHelpers.defaultExpectedTriples,
        TestingHelpers.defaultExpectedStructureAnnotations);

  }

  @SuppressWarnings("EmptyMethod")
  @Test
  void makeProjectStructureTest() {}

  @Test
  void loadWithAppropriateFormatTest() {}

  @Test
  void saveToFormatTest() {}
}
