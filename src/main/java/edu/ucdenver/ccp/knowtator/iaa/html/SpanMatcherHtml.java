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

package edu.ucdenver.ccp.knowtator.iaa.html;

import edu.ucdenver.ccp.knowtator.iaa.AnnotationSpanIndex;
import edu.ucdenver.ccp.knowtator.iaa.Iaa;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** The type Span matcher html. */
public class SpanMatcherHtml {

  /**
   * Print iaa.
   *
   * @param iaa the iaa
   * @param matcher the matcher
   * @param directory the directory
   * @param numberOfDocs the number of docs
   */
  public static void printIaa(
      Iaa iaa,
      Matcher matcher,
      File directory,
      int numberOfDocs) throws IOException {
    NumberFormat percentageFormat = NumberFormat.getPercentInstance();
    percentageFormat.setMinimumFractionDigits(2);

    String fileName = matcher.getName();

    PrintStream html = new PrintStream(new File(directory, String.format("%s.html", fileName)), "UTF-8");

    html.println(Iaa2Html.initHTML(matcher.getName(), matcher.getDescription()));
    html.printf("<h2>%d-way Iaa Results</h2>%n", iaa.getSetNames().size());

    html.printf(
        "<table border=1><tr>"
            + "<td><b>Type</b></td><td><b>Iaa</b>"
            + "</td><td><b>matches</b></td>"
            + "<td><b>non-matches</b></td>"
            + "<td><b>confused class assignments</tr>%n");

    Set<String> classes = iaa.getAnnotationClasses();
    Set<String> sets = iaa.getSetNames();

    Map<String, Set<ConceptAnnotation>> allwayMatches = iaa.getAllwayMatches();
    Map<String, Set<ConceptAnnotation>> allwayNonmatches = iaa.getAllwayNonmatches();

    Map<ConceptAnnotation, Set<ConceptAnnotation>> matchSets = iaa.getAllwayMatchSets();

    Set<ConceptAnnotation> allwayMatchesSingleSet = Iaa2Html.getSingleSet(allwayMatches);
    Set<ConceptAnnotation> allwayNonmatchesSingleSet = Iaa2Html.getSingleSet(allwayNonmatches);

    AnnotationSpanIndex spanIndex = new AnnotationSpanIndex(allwayNonmatchesSingleSet);

    int totalAllwayMatches = allwayMatchesSingleSet.size();
    int totalAllwayNonmatches = allwayNonmatchesSingleSet.size();

    double iaaScore =
        (double) totalAllwayMatches
            / ((double) totalAllwayMatches + (double) totalAllwayNonmatches);

    html.printf(
        "<tr><td><b>All classes</b></td><td>%s</td><td>%d</td><td>%d</td></tr>%n",
        percentageFormat.format(iaaScore), totalAllwayMatches, totalAllwayNonmatches);

    Map<String, Set<ConceptAnnotation>> sortedAllwayMatches =
        Iaa2Html.sortByType(classes, allwayMatchesSingleSet);
    Map<String, Set<ConceptAnnotation>> sortedAllwayNonmatches =
        Iaa2Html.sortByType(classes, allwayNonmatchesSingleSet);

    List<String> sortedTypes = new ArrayList<>(classes);
    Collections.sort(sortedTypes);

    for (String type : sortedTypes) {
      int classMatches = sortedAllwayMatches.get(type).size();
      int classNonmatches = sortedAllwayNonmatches.get(type).size();

      iaaScore = (double) classMatches / ((double) classMatches + (double) classNonmatches);
      Map<String, int[]> confusionCounts = errorMatrix(sortedAllwayMatches.get(type), matchSets);

      if (type.startsWith("<") && type.endsWith(">")) {
        type = type.substring(1, type.length()- 2);
      }
      html.printf(
          "<tr><td>%s</td><td>%s</td><td>%d</td><td>%d</td>%n",
          type, percentageFormat.format(iaaScore), classMatches, classNonmatches);

      html.println("<td>");
      for (Map.Entry<String, int[]> confusedClass : confusionCounts.entrySet()) {
        String confusedType = confusedClass.getKey();
        if (confusedType.startsWith("<") && confusedType.endsWith(">")) {
          confusedType = confusedType.substring(1, confusedType.length()- 2);
        }
        html.printf("  %s=%d%n", confusedType, confusedClass.getValue()[0]);
      }
      html.println("</td>");
    }
    html.println("</table>");

    html.printf("<br>Iaa calculated on %d documents.%n", numberOfDocs);
    html.println("<br>all annotations = matches + non-matches");
    html.println("<br>Iaa = matches / all annotations");

    Iaa2Html.printMatchData(
        html,
        sets,
        fileName,
        directory,
        allwayMatches,
        classes,
        iaa);

    Iaa2Html.printNonmatchData(
        html,
        sets,
        fileName,
        directory,
        allwayNonmatches,
        spanIndex,
        classes);

    Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseMatches = iaa.getPairwiseMatches();
    Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseNonmatches =
        iaa.getPairwiseNonmatches();

    Iaa2Html.printPairwiseAgreement(
        html, sets, pairwiseMatches, pairwiseNonmatches, percentageFormat);

    html.flush();
    html.close();
  }

  private static Map<String, int[]> errorMatrix(
      Set<ConceptAnnotation> matches, Map<ConceptAnnotation, Set<ConceptAnnotation>> matchSets) {
    Map<String, int[]> counts = new HashMap<>();

    for (ConceptAnnotation match : matches) {
      Set<ConceptAnnotation> matchedConceptAnnotations = matchSets.get(match);
      for (ConceptAnnotation matchedConceptAnnotation : matchedConceptAnnotations) {
        if (!matchedConceptAnnotation.equals(match)) {
          String annotationClass = matchedConceptAnnotation.getOwlClassRendering();
          if (!counts.containsKey(annotationClass)) {
            counts.put(annotationClass, new int[1]);
          }
          counts.get(annotationClass)[0]++;
        }
      }
    }
    return counts;
  }
}
