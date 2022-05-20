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

import static edu.ucdenver.ccp.knowtator.view.iaa.IAAOptionsDialog.mergeProjects;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.iaa.Iaa;
import edu.ucdenver.ccp.knowtator.iaa.IaaException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIaa;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KnowtatorIaaTest {

  private static KnowtatorIaa knowtatorIAA;
  private static File outputDir;
  private static File goldStandardDir;
  private static KnowtatorModel model;
  private static final TestingHelpers.ProjectCounts counts =
      new TestingHelpers.ProjectCounts(4, 446, 446, 4, 3, 0, 0, 0, 0);

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
      knowtatorIAA =
          new KnowtatorIaa(
              outputDir,
              model,
              model.getTextSources().stream().map(TextSource::getId).collect(Collectors.toSet()),
              model.getProfiles().stream()
                  .map(Profile::getId)
                  .filter(myProfiles::contains)
                  .collect(Collectors.toSet()),
              new ArrayList<>(
                  new HashSet<>(
                      model.getTextSources().stream()
                          .flatMap(
                              textSource ->
                                  textSource.getConceptAnnotations().stream()
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
      System.out.printf(
          "Gold: file://%s\nThis: file://%s\n%n",
          new File(goldStandardDir, "index.html").getAbsolutePath(),
          new File(outputDir, "index.html").getAbsolutePath());
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
      System.out.printf(
          "Gold: file://%s\nThis: file://%s\n%n",
          new File(goldStandardDir, "index.html").getAbsolutePath(),
          new File(outputDir, "index.html").getAbsolutePath());
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
      System.out.printf(
          "Gold: file://%s\nThis: file://%s\n%n",
          new File(goldStandardDir, "index.html").getAbsolutePath(),
          new File(outputDir, "index.html").getAbsolutePath());
      throw e;
    }
  }

  @Test
  void overlappingIDsTest() throws IaaException, IOException {
    // Loading first project from Default
    File initProjectLocation1 =
        new File(
            Objects.requireNonNull(
                    KnowtatorIaaTest.class.getResource(
                        "/iaa_test_overlapping_annotations/project1"))
                .getFile());
    File projectLocation1 = Files.createTempDir();
    FileUtils.copyDirectory(initProjectLocation1, projectLocation1);

    KnowtatorModel model1 = new KnowtatorModel(projectLocation1, null);

    model1.load(model1.getProjectLocation());

    // Loading second project fom NV
    File initProjectLocation2 =
        new File(
            Objects.requireNonNull(
                    KnowtatorIaaTest.class.getResource(
                        "/iaa_test_overlapping_annotations/project2"))
                .getFile());
    File projectLocation2 = Files.createTempDir();
    FileUtils.copyDirectory(initProjectLocation2, projectLocation2);
    KnowtatorModel model2 = new KnowtatorModel(projectLocation2, null);
    model2.load(projectLocation2);

    TestingHelpers.ProjectCounts counts1 =
        new TestingHelpers.ProjectCounts(1, 2, 3, 1, 2, 3, 2, 1, 0);
    TestingHelpers.ProjectCounts counts2 =
        new TestingHelpers.ProjectCounts(1, 2, 3, 1, 2, 3, 2, 1, 0);
    TestingHelpers.countCollections(model1, counts1);
    TestingHelpers.countCollections(model2, counts2);

    // Merge
    KnowtatorModel mergeModel = mergeProjects(projectLocation2, model1, null, false, false);
    TestingHelpers.ProjectCounts mergeCounts =
        counts2.add(counts1, new TestingHelpers.ProjectCounts(1, 2, 3, 1, 0, 0, 0, 0, 0));
    TestingHelpers.countCollections(mergeModel, mergeCounts);
    mergeModel = mergeProjects(projectLocation2, model1, null, false, true);
    mergeCounts = counts2.add(counts1, new TestingHelpers.ProjectCounts(1, 0, 0, 1, 0, 0, 0, 0, 0));

    TestingHelpers.countCollections(mergeModel, mergeCounts);

    // IAA
    File outputDir = new File(mergeModel.getProjectLocation(), "test_results");
    boolean created = outputDir.mkdir();
    if (created) {
      ProfileCollection profiles = new ProfileCollection(mergeModel);
      profiles.remove(profiles.getDefaultProfile());

      KnowtatorIaa knowtatorIAA =
          new KnowtatorIaa(
              outputDir,
              mergeModel,
              mergeModel.getTextSources().stream()
                  .map(TextSource::getId)
                  .collect(Collectors.toSet()),
              mergeModel.getProfiles().stream().map(Profile::getId).collect(Collectors.toSet()),
              new ArrayList<>(
                  new HashSet<>(
                      mergeModel.getTextSources().stream()
                          .flatMap(
                              textSource ->
                                  textSource.getConceptAnnotations().stream()
                                      .map(ConceptAnnotation::getOwlClass))
                          .collect(Collectors.toSet()))));
      Iaa iaa = knowtatorIAA.runClassAndSpanIaa();
      try {

        int ams = iaa.getAllwayMatches().size();
        int anms = iaa.getAllwayNonmatches().size();
        int nams = iaa.getNontrivialAllwayMatches().size();
        int nanms = iaa.getNontrivialAllwayNonmatches().size();
        int tams = iaa.getTrivialAllwayMatches().size();
        int tanms = iaa.getTrivialAllwayNonmatches().size();
        int pms = iaa.getPairwiseMatches().size();
        int pnms = iaa.getPairwiseNonmatches().size();
        int pmss = iaa.getPairwiseMatches().values().stream().mapToInt(Map::size).sum();
        int pnmss = iaa.getPairwiseNonmatches().values().stream().mapToInt(Map::size).sum();
        int pmsss =
            iaa.getPairwiseMatches().values().stream()
                .mapToInt(stringSetMap -> stringSetMap.values().stream().mapToInt(Set::size).sum())
                .sum();
        int pnmsss =
            iaa.getPairwiseNonmatches().values().stream()
                .mapToInt(stringSetMap -> stringSetMap.values().stream().mapToInt(Set::size).sum())
                .sum();

        Assertions.assertAll(
            () -> Assertions.assertEquals(4, ams),
            () -> Assertions.assertEquals(4, anms),
            () -> Assertions.assertEquals(4, nams),
            () -> Assertions.assertEquals(4, tams),
            () -> Assertions.assertEquals(4, nanms),
            () -> Assertions.assertEquals(4, tanms),
            () -> Assertions.assertEquals(4, pms),
            () -> Assertions.assertEquals(4, pnms),
            () -> Assertions.assertEquals(12, pmss),
            () -> Assertions.assertEquals(12, pnmss),
            () -> Assertions.assertEquals(4, pmsss),
            () -> Assertions.assertEquals(8, pnmsss));

      } catch (AssertionError e) {
        System.out.printf(
            "Gold: file://%s\nThis: file://%s\n%n",
            new File(goldStandardDir, "index.html").getAbsolutePath(),
            new File(outputDir, "index.html").getAbsolutePath());
        throw e;
      }
    } else {
      throw new FileAlreadyExistsException(outputDir.getAbsolutePath());
    }

    TextSource textSource = model1.getTextSources().getOnly().get();
    ConceptAnnotation conceptAnnotation =
        new ConceptAnnotation(
            textSource,
            "new_ann",
            "owl_class",
            model1.getDefaultProfile(),
            "",
            "",
            new HashSet<>());
    conceptAnnotation.add(new Span(conceptAnnotation, null, 0, 5));
    textSource.getConceptAnnotations().add(conceptAnnotation);

    mergeModel = mergeProjects(projectLocation2, model1, null, false, true);
    TestingHelpers.countCollections(
        mergeModel,
        mergeCounts.add(
            new TestingHelpers.ProjectCounts(0, 1, 1, 0, 0, 0, 0, 0, 0),
            new TestingHelpers.ProjectCounts(0, 0, 0, 0, 0, 0, 0, 0, 0)));

    outputDir = new File(mergeModel.getProjectLocation(), "test_results_diff");
    created = outputDir.mkdir();
    if (created) {
      ProfileCollection profiles = new ProfileCollection(mergeModel);
      profiles.remove(profiles.getDefaultProfile());

      KnowtatorIaa knowtatorIAA =
          new KnowtatorIaa(
              outputDir,
              mergeModel,
              mergeModel.getTextSources().stream()
                  .map(TextSource::getId)
                  .collect(Collectors.toSet()),
              mergeModel.getProfiles().stream().map(Profile::getId).collect(Collectors.toSet()),
              new ArrayList<>(
                  new HashSet<>(
                      mergeModel.getTextSources().stream()
                          .flatMap(
                              textSource1 ->
                                  textSource1.getConceptAnnotations().stream()
                                      .map(ConceptAnnotation::getOwlClass))
                          .collect(Collectors.toSet()))));
      Iaa iaa = knowtatorIAA.runClassAndSpanIaa();
      try {

        int ams = iaa.getAllwayMatches().size();
        int anms = iaa.getAllwayNonmatches().size();
        int nams = iaa.getNontrivialAllwayMatches().size();
        int nanms = iaa.getNontrivialAllwayNonmatches().size();
        int tams = iaa.getTrivialAllwayMatches().size();
        int tanms = iaa.getTrivialAllwayNonmatches().size();
        int pms = iaa.getPairwiseMatches().size();
        int pnms = iaa.getPairwiseNonmatches().size();
        int pmss = iaa.getPairwiseMatches().values().stream().mapToInt(Map::size).sum();
        int pnmss = iaa.getPairwiseNonmatches().values().stream().mapToInt(Map::size).sum();
        int pmsss =
            iaa.getPairwiseMatches().values().stream()
                .mapToInt(stringSetMap -> stringSetMap.values().stream().mapToInt(Set::size).sum())
                .sum();
        int pnmsss =
            iaa.getPairwiseNonmatches().values().stream()
                .mapToInt(stringSetMap -> stringSetMap.values().stream().mapToInt(Set::size).sum())
                .sum();

        Assertions.assertAll(
            () -> Assertions.assertEquals(4, ams),
            () -> Assertions.assertEquals(4, anms),
            () -> Assertions.assertEquals(4, nams),
            () -> Assertions.assertEquals(4, tams),
            () -> Assertions.assertEquals(4, nanms),
            () -> Assertions.assertEquals(4, tanms),
            () -> Assertions.assertEquals(4, pms),
            () -> Assertions.assertEquals(4, pnms),
            () -> Assertions.assertEquals(12, pmss),
            () -> Assertions.assertEquals(12, pnmss),
            () -> Assertions.assertEquals(4, pmsss),
            () -> Assertions.assertEquals(11, pnmsss));

      } catch (AssertionError e) {
        System.out.printf(
            "Gold: file://%s\nThis: file://%s\n%n",
            new File(goldStandardDir, "index.html").getAbsolutePath(),
            new File(outputDir, "index.html").getAbsolutePath());
        throw e;
      }
    } else {
      throw new FileAlreadyExistsException(outputDir.getAbsolutePath());
    }
  }

  @AfterAll
  static void cleanUp() {
    knowtatorIAA.closeHtml();
  }
}
