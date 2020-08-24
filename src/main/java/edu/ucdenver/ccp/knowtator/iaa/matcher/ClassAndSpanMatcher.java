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

package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.Iaa;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** The type Class and span matcher. */
public class ClassAndSpanMatcher implements Matcher {

  /**
   * This is a static version of the above match method that can be called by other matcher
   * implementations.
   *
   * @param conceptAnnotation the concept annotation
   * @param compareSetName the compare set name
   * @param iaa the iaa
   * @param excludeConceptAnnotations the exclude concept annotations
   * @return an concept that matches or null.
   */
  public static ConceptAnnotation match(
      ConceptAnnotation conceptAnnotation,
      String compareSetName,
      Iaa iaa,
      Set<ConceptAnnotation> excludeConceptAnnotations) {
    Set<ConceptAnnotation> singleMatchSet =
        matches(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
    if (singleMatchSet.size() == 1) {
      return singleMatchSet.iterator().next();
    } else {
      return null;
    }
  }

  /**
   * Finds a match.
   *
   * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH. Trivial matches and
   *     non-matches are not defined for this matcher.
   * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(ConceptAnnotation, String, Set, Iaa,
   *     MatchResult)
   * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
   * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
   */
  @Override
  public ConceptAnnotation match(
      ConceptAnnotation conceptAnnotation,
      String compareSetName,
      Set<ConceptAnnotation> excludeConceptAnnotations,
      Iaa iaa,
      MatchResult matchResult) {
    ConceptAnnotation match =
        match(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
    if (match != null) {
      matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
    } else {
      matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
      match = null;
    }
    return match;
  }

  /**
   * Finds matches.
   *
   * @return this method will not return null - but rather an empty set of no matches are found.
   */
  private static Set<ConceptAnnotation> matches(
      ConceptAnnotation conceptAnnotation,
      String compareSetName,
      Iaa iaa,
      Set<ConceptAnnotation> excludeConceptAnnotations) {
    String type = conceptAnnotation.getOwlClassRendering();
    Set<ConceptAnnotation> candidateConceptAnnotations =
        new HashSet<>(iaa.getExactlyOverlappingAnnotations(conceptAnnotation, compareSetName));
    candidateConceptAnnotations.removeAll(excludeConceptAnnotations);
    if (candidateConceptAnnotations.size() == 0) {
      return Collections.emptySet();
    }

    Set<ConceptAnnotation> returnValues = new HashSet<>();
    for (ConceptAnnotation candidateConceptAnnotation : candidateConceptAnnotations) {
      if (!excludeConceptAnnotations.contains(candidateConceptAnnotation)
          && candidateConceptAnnotation.getOwlClassRendering().equals(type)) {
        returnValues.add(candidateConceptAnnotation);
        return returnValues;
      }
    }
    return returnValues;
  }

  public String getName() {
    return "Class and span matcher";
  }

  public String getDescription() {

    return "Annotations match if they have the same class assignment and the same spans.";
  }

  public boolean returnsTrivials() {
    return false;
  }
}
