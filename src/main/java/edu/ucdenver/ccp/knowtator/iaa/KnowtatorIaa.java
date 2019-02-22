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

package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.iaa.html.Iaa2Html;
import edu.ucdenver.ccp.knowtator.iaa.html.SpanMatcherHtml;
import edu.ucdenver.ccp.knowtator.iaa.matcher.ClassAndSpanMatcher;
import edu.ucdenver.ccp.knowtator.iaa.matcher.ClassMatcher;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.iaa.matcher.SpanMatcher;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** The type Knowtator iaa. */
public class KnowtatorIaa {
  private final File outputDirectory;

  // KnowtatorFilter filter;

  private final TextSourceCollection textSources;

  // Project project;

  private final KnowtatorModel controller;

  // KnowtatorProjectUtil kpu;

  // MentionUtil mentionUtil;

  // FilterUtil filterUtil;

  private final Map<ConceptAnnotation, String> annotationTexts;

  private final Map<ConceptAnnotation, String> annotationTextNames;

  private Map<TextSource, ConceptAnnotationCollection> textSourceAnnotationsMap;

  private PrintStream html;

  private Set<String> setNames;

  /**
   * Instantiates a new Knowtator iaa.
   *
   * @param outputDirectory the output directory
   * @param controller the controller
   * @throws IaaException the iaa exception
   */
  @SuppressWarnings("unused")
  public KnowtatorIaa(File outputDirectory, KnowtatorModel controller) throws IaaException {

    this.outputDirectory = outputDirectory;
    // this.filter = filter;
    this.textSources = controller.getTextSources();

    this.controller = controller;
    annotationTexts = new HashMap<>();
    annotationTextNames = new HashMap<>();

    initSetNames();
    initTextSourceAnnotations();
    initHtml();
  }

  private void initSetNames() {
    setNames = controller.getProfiles().stream().map(Profile::getId).collect(Collectors.toSet());
    setNames.remove("Default");
  }

  private void initHtml() throws IaaException {
    try {
      html = new PrintStream(new File(outputDirectory, "index.html"));
      html.println("<html><head><title>Inter-Profile Agreement</title></head>");
      html.println("<body><ul>");
    } catch (IOException ioe) {
      throw new IaaException(ioe);
    }
  }

  /** Close html. */
  public void closeHtml() {
    html.println("</ul>");
    html.println("</body></html>");
    html.flush();
    html.close();
  }

  private void initTextSourceAnnotations() {
    textSourceAnnotationsMap = new HashMap<>();
    for (TextSource textSource : textSources) {
      textSourceAnnotationsMap.put(textSource, textSource.getConceptAnnotations());
    }
  }

  /**
   * Run class iaa.
   *
   * @throws IaaException the iaa exception
   */
  public void runClassIaa() throws IaaException {
    try {
      ClassMatcher classMatcher = new ClassMatcher();
      Iaa classIaa = new Iaa(setNames);

      runIaaWithMatcher(classMatcher, classIaa);

      Iaa2Html.printIaa(
          classIaa,
          classMatcher,
          outputDirectory,
          textSources.size(),
          annotationTexts,
          annotationTextNames);
      html.printf(
          "<li><a href=\"%s.html\">%s</a></li>%n", classMatcher.getName(), classMatcher.getName());
      closeHtml();
    } catch (Exception e) {
      throw new IaaException(e);
    }
  }

  private void runIaaWithMatcher(Matcher matcher, Iaa iaa) throws IaaException {
    for (ConceptAnnotationCollection conceptAnnotationCollection :
        textSourceAnnotationsMap.values()) {
      Set<ConceptAnnotation> conceptAnnotations = conceptAnnotationCollection.getCollection();
      iaa.setConceptAnnotations(conceptAnnotations);
      iaa.allwayIaa(matcher);
      iaa.pairwiseIaa(matcher);
    }
  }

  /**
   * Run span iaa.
   *
   * @throws IaaException the iaa exception
   */
  public void runSpanIaa() throws IaaException {
    try {
      SpanMatcher spanMatcher = new SpanMatcher();
      Iaa spanIaa = new Iaa(setNames);

      runIaaWithMatcher(spanMatcher, spanIaa);
      SpanMatcherHtml.printIaa(
          spanIaa,
          spanMatcher,
          outputDirectory,
          textSources.size(),
          annotationTexts,
          annotationTextNames);
      html.printf(
          "<li><a href=\"%s.html\">%s</a></li>%n", spanMatcher.getName(), spanMatcher.getName());
      closeHtml();
    } catch (Exception e) {
      throw new IaaException(e);
    }
  }

  /**
   * Run class and span iaa.
   *
   * @throws IaaException the iaa exception
   */
  @SuppressWarnings("Duplicates")
  public void runClassAndSpanIaa() throws IaaException {
    try {
      ClassAndSpanMatcher classAndSpanMatcher = new ClassAndSpanMatcher();
      Iaa classAndSpanIaa = new Iaa(setNames);

      runIaaWithMatcher(classAndSpanMatcher, classAndSpanIaa);
      Iaa2Html.printIaa(
          classAndSpanIaa,
          classAndSpanMatcher,
          outputDirectory,
          textSources.size(),
          annotationTexts,
          annotationTextNames);
      html.printf(
          "<li><a href=\"%s.html\">%s</a></li>%n",
          classAndSpanMatcher.getName(), classAndSpanMatcher.getName());
      closeHtml();
    } catch (Exception e) {
      throw new IaaException(e);
    }
  }
}
