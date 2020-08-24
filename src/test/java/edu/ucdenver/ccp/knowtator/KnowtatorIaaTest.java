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

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.iaa.IaaException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIaa;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KnowtatorIaaTest {

  private static KnowtatorIaa knowtatorIAA;
  private static File outputDir;
  private static File goldStandardDir;
  private static KnowtatorModel model;
  private static final TestingHelpers.ProjectCounts counts = new TestingHelpers.ProjectCounts(4, 456, 456, 4, 3, 0, 0, 0, 0);

  @BeforeAll
  static void makeProjectTest() throws IaaException, IOException {
    String projectFileName = "iaa_test_project";
    File projectDirectory = TestingHelpers.getProjectFile(projectFileName).getParentFile();
    File tempProjectDir = Files.createTempDir();
    FileUtils.copyDirectory(projectDirectory, tempProjectDir);

    model = new KnowtatorModel(tempProjectDir, null);
    model.load(model.getProjectLocation());

    goldStandardDir = new File(model.getProjectLocation(), "iaa");

    outputDir = new File(model.getProjectLocation(), "iaa_results");

    boolean created = outputDir.mkdir();
    if (created) {
      ProfileCollection profiles = new ProfileCollection(model);
      profiles.remove(profiles.getDefaultProfile());
      HashSet<String> myProfiles = new HashSet<>();
      myProfiles.add("Kristin Garcia");
      myProfiles.add("Mike Bada");

      model.getProfile("Kristin Garcia").ifPresent(profiles::add);
      model.getProfile("Mike Bada").ifPresent(profiles::add);
      knowtatorIAA = new KnowtatorIaa(outputDir,
          model,
          model.getTextSources().stream().map(TextSource::getId).collect(Collectors.toSet()),
          model.getProfiles().stream()
              .map(Profile::getId)
              .filter(myProfiles::contains)
              .collect(Collectors.toSet()),
          new ArrayList<>(new HashSet<>(model.getTextSources().stream()
              .flatMap(textSource -> textSource.getConceptAnnotations().stream()
                  .map(ConceptAnnotation::getOwlClass))
              .collect(Collectors.toSet()))));
    }
  }

  @Test
  void runClassIaaTest() throws IOException, IaaException {
    TestingHelpers.countCollections(model, counts);
    knowtatorIAA.runClassIaa();
    // TODO: Rerun test data because concept annotations no longer store owl class label

    try {
      assert FileUtils.contentEqualsIgnoreEOL(
          new File(outputDir, "Class matcher.dat"),
          new File(goldStandardDir, "Class matcher.dat"),
          "utf-8");
    } catch (AssertionError e) {
      System.out.println(String.format("Gold: %s\nThis: %s\n",
          new File(goldStandardDir, "index.html").getAbsolutePath(),
          new File(outputDir, "index.html").getAbsolutePath()));
      throw e;
    }
  }

  @Test
  void runSpanIaaTest() throws IOException, IaaException {
    TestingHelpers.countCollections(model, counts);
    knowtatorIAA.runSpanIaa();

    try {
      assert FileUtils.contentEqualsIgnoreEOL(
          new File(outputDir, "Span matcher.html"),
          new File(goldStandardDir, "Span matcher.html"),
          "utf-8");
    } catch (AssertionError e) {
      System.out.println(String.format("Gold: %s\nThis: %s\n",
          new File(goldStandardDir, "index.html").getAbsolutePath(),
          new File(outputDir, "index.html").getAbsolutePath()));
      throw e;
    }
  }

  @Test
  void runClassAndSpanIaaTest() throws IaaException, IOException {
    TestingHelpers.countCollections(model, counts);
    knowtatorIAA.runClassAndSpanIaa();

    try {
      assert FileUtils.contentEqualsIgnoreEOL(
          new File(outputDir, "Class and span matcher.dat"),
          new File(goldStandardDir, "Class and span matcher.dat"),
          "utf-8");
    } catch (AssertionError e) {
      System.out.println(String.format("Gold: %s\nThis: %s\n",
          new File(goldStandardDir, "index.html").getAbsolutePath(),
          new File(outputDir, "index.html").getAbsolutePath()));
      throw e;
    }
  }

  @AfterAll
  static void cleanUp() {
    knowtatorIAA.closeHtml();
  }
}
