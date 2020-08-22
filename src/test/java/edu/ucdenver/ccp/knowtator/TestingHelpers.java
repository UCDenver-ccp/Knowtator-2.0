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
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/** The type Testing helpers. */
public class TestingHelpers {

  /** The constant projectFileName. */
  private static final String projectFileName = "test_project_using_uris";

  public static final ProjectCounts defaultCounts = new ProjectCounts(5, 6, 7, 3, 2, 3, 7, 4, 0);

  /** The constant defaultExpectedTextSources. */
  public static final int defaultExpectedTextSources = 5;

  /** The constant defaultExpectedConceptAnnotations. */
  public static final int defaultExpectedConceptAnnotations = 6;

  public static final int defaultExpectedStructureAnnotations = 0;

  /** The constant defaultExpectedSpans. */
  public static final int defaultExpectedSpans = 7;

  /** The constant defaultExpectedGraphSpaces. */
  public static final int defaultExpectedGraphSpaces = 3;

  /** The constant defaultExpectedProfiles. */
  public static final int defaultExpectedProfiles = 2;

  /** The constant defaultExpectedHighlighters. */
  public static final int defaultExpectedHighlighters = 3;

  /** The constant defaultExpectedAnnotationNodes. */
  public static final int defaultExpectedAnnotationNodes = 7;

  /** The constant defaultExpectedTriples. */
  public static final int defaultExpectedTriples = 4;

  public static int defaultAnnotationLayers = 1;

  /**
   * Gets project file.
   *
   * @param projectName the project name
   * @return the project file
   */
  static File getProjectFile(String projectName) {
    return new File(
        TestingHelpers.class
            .getResource(String.format("/%s/%s.knowtator", projectName, projectName))
            .getFile());
  }

  /**
   * Gets article file.
   *
   * @param projectName the project name
   * @param articleName the article name
   * @return the article file
   */
  public static File getArticleFile(String projectName, String articleName) {
    return new File(
        TestingHelpers.class
            .getResource(String.format("/%s/Articles/%s.txt", projectName, articleName))
            .getFile());
  }

  /**
   * Gets loaded model.
   *
   * @return the loaded model
   * @throws IOException the io exception
   */
  public static KnowtatorModel getLoadedModel() throws IOException {
    File projectDirectory = getProjectFile(projectFileName).getParentFile();
    File tempProjectDir = Files.createTempDir();
    FileUtils.copyDirectory(projectDirectory, tempProjectDir);
    KnowtatorModel model = new KnowtatorModel(tempProjectDir, null);
    model.load(model.getProjectLocation());

    return model;
  }

  /**
   * Gets loaded model.
   *
   * @return the loaded model
   * @throws IOException the io exception
   */
  public static KnowtatorModel getLoadedModel(String projectFileName) throws IOException {
    File projectDirectory = getProjectFile(projectFileName).getParentFile();
    File tempProjectDir = Files.createTempDir();
    FileUtils.copyDirectory(projectDirectory, tempProjectDir);
    KnowtatorModel model = new KnowtatorModel(tempProjectDir, null);
    model.load(model.getProjectLocation());

    return model;
  }

  /**
   * Check default collection values.
   *
   * @param model the model
   */
  public static void checkDefaultCollectionValues(KnowtatorModel model) {
    TestingHelpers.countCollections(model, defaultCounts);
  }

  public static void countCollections(KnowtatorModel model, ProjectCounts counts) {

    int actualTextSources = model.getNumberOfTextSources();
    int actualConceptAnnotations =
        model.getTextSources().stream()
            .mapToInt(
                textSource ->
                    Math.toIntExact(
                        textSource.getConceptAnnotations().stream()
                            .filter(
                                conceptAnnotation ->
                                    !conceptAnnotation.getLayers().contains("Structures"))
                            .count()))
            .sum();
    int actualStructureAnnotations =
        model.getTextSources().stream()
            .mapToInt(
                textSource ->
                    Math.toIntExact(
                        textSource.getConceptAnnotations().stream()
                            .filter(
                                conceptAnnotation ->
                                    conceptAnnotation.getLayers().contains("Structures"))
                            .count()))
            .sum();
    int actualSpans =
        model.getTextSources().stream()
            .mapToInt(
                textSource ->
                    Math.toIntExact(textSource.getSpans(null).stream()
                        .filter(
                            span -> !span.getConceptAnnotation().getLayers().contains("Structures"))
                        .count()))
            .sum();
    int actualProfiles = model.getNumberOfProfiles();
    int actualHighlighters =
        model.getProfiles().stream().mapToInt(profile -> profile.getColors().size()).sum();
    int actualGraphSpaces =
        model.getTextSources().stream().mapToInt(TextSource::getNumberOfGraphSpaces).sum();
    int actualAnnotationNodes =
        model.getTextSources().stream()
            .mapToInt(
                textSource ->
                    textSource.getGraphSpaces().stream()
                        .mapToInt(
                            graphSpace1 ->
                                graphSpace1.getChildVertices(graphSpace1.getDefaultParent()).length)
                        .sum())
            .sum();

    int actualTriples =
        model.getTextSources().stream()
            .mapToInt(
                textSource ->
                    textSource.getGraphSpaces().stream()
                        .mapToInt(
                            graphSpace1 ->
                                graphSpace1.getChildEdges(graphSpace1.getDefaultParent()).length)
                        .sum())
            .sum();

    String error = "";

    try {
        assert actualTextSources == counts.ets;
    } catch (AssertionError e) {
      error = error.concat(String.format("\nThere were %d text sources instead of %d", actualTextSources, counts.ets));
    }

    try {
        assert actualConceptAnnotations == counts.eca;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d concept annotations instead of %d", actualConceptAnnotations, counts.eca));
    }

    try {
        assert actualStructureAnnotations == counts.esa;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d structure annotations instead of %d", actualStructureAnnotations, counts.esa));
    }

    try {
      assert actualSpans == counts.es;
    } catch (AssertionError e) {
      error = error.concat(String.format("\nThere were %d spans instead of %d", actualSpans, counts.es));
    }

    try {
        assert actualProfiles == counts.ep;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d profiles instead of %d", actualProfiles, counts.ep));
    }

    try {
        assert actualHighlighters == counts.eh;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d highlighters instead of %d", actualHighlighters, counts.eh));
    }

    try {
        assert actualGraphSpaces == counts.egs;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d graph spaces instead of %d", actualGraphSpaces, counts.egs));
    }

    try {
        assert actualAnnotationNodes == counts.ean;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d annotation nodes instead of %d", actualAnnotationNodes, counts.ean));
    }

    try {
        assert actualTriples == counts.et;
    } catch (AssertionError e) {
        error = error.concat(String.format("\nThere were %d triples instead of %d", actualTriples, counts.et));
    }

    if (!error.equals("")) {
        throw new AssertionError(error);
    }
  }


  /**
   * Test owl action.
   *
   * @param controller the controller
   * @param changes the changes
   */
  public static void testOwlAction(
      KnowtatorModel controller,
      List<? extends OWLOntologyChange> changes, ProjectCounts counts) {
    TestingHelpers.checkDefaultCollectionValues(controller);
    controller.getOwlOntologyManager().applyChanges(changes);
    TestingHelpers.countCollections(controller, counts);
  }

  /**
   * Test knowtator action.
   *
   * @param controller the controller
   * @param action the action
   * @throws ActionUnperformable the action unperformable exception
   */
  public static void testKnowtatorAction(
      KnowtatorModel controller,
      AbstractKnowtatorAction action,
      ProjectCounts counts)
      throws ActionUnperformable {
    TestingHelpers.checkDefaultCollectionValues(controller);
    controller.registerAction(action);
    TestingHelpers.countCollections(controller, counts);
    controller.undo();
    TestingHelpers.checkDefaultCollectionValues(controller);
    controller.redo();
    TestingHelpers.countCollections(controller, counts);
    controller.undo();
  }

  public static class ProjectCounts {
    private final int ets;
    private final int eca;
    private final int es;
    private final int egs;
    private final int ep;
    private final int eh;
    private final int ean;
    private final int et;
    private final int esa;

    public ProjectCounts(int ets, int eca, int es, int egs, int ep, int eh, int ean, int et, int esa) {
      this.ets = ets;
      this.eca = eca;
      this.es = es;
      this.egs = egs;
      this.ep = ep;
      this.eh = eh;
      this.ean = ean;
      this.et = et;
      this.esa = esa;
    }

    public ProjectCounts copy(int etsC, int ecaC, int esC, int egsC, int epC, int ehC, int eanC, int etC, int esaC) {
      return new ProjectCounts(
          this.ets + etsC,
          this.eca + ecaC,
          this.es + esC,
          this.egs + egsC,
          this.ep + epC,
          this.eh + ehC,
          this.ean + eanC,
          this.et + etC,
          this.esa + esaC);
    }

    public ProjectCounts copy() {
      return new ProjectCounts(
          this.ets,
          this.eca,
          this.es,
          this.egs,
          this.ep,
          this.eh,
          this.ean,
          this.et,
          this.esa);
    }

    public ProjectCounts add(ProjectCounts counts2, ProjectCounts overlaps) {
      return new ProjectCounts(
      this.ets + counts2.ets - overlaps.ets,
          this.eca + counts2.eca - overlaps.eca,
          this.es + counts2.es - overlaps.es,
          this.egs + counts2.egs - overlaps.egs,
          this.ep + counts2.ep - overlaps.ep,
          this.eh + counts2.eh - overlaps.eh,
          this.ean + counts2.ean - overlaps.ean,
          this.et + counts2.et - overlaps.et,
          this.esa + counts2.esa - overlaps.esa);
    }
  }
}
