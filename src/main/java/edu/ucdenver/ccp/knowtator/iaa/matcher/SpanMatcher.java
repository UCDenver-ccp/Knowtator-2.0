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
import java.util.HashSet;
import java.util.Set;

/** The type Span matcher. */
public class SpanMatcher implements Matcher {
  /**
   * This method will return an concept with the same class and spans. If one does not exist, then
   * it will return an concept with the same spans (but different class). Otherwise, null is
   * returned.
   *
   * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH. Trivial matches and
   *     non-matches are not defined for this matcher.
   * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(ConceptAnnotation, String, Set, Iaa,
   *     MatchResult)
   * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
   * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
   */
  @SuppressWarnings("SuspiciousMethodCalls")
  public ConceptAnnotation match(
      ConceptAnnotation conceptAnnotation,
      String compareSetName,
      Set<ConceptAnnotation> excludeConceptAnnotations,
      Iaa iaa,
      MatchResult matchResult) {
    ConceptAnnotation spanAndClassMatch =
        ClassAndSpanMatcher.match(
            conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
    if (spanAndClassMatch != null) {
      matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
      return spanAndClassMatch;
    }

    Set<ConceptAnnotation> candidateConceptAnnotations =
        new HashSet<>(iaa.getExactlyOverlappingAnnotations(conceptAnnotation, compareSetName));
    candidateConceptAnnotations.remove(excludeConceptAnnotations);

    for (ConceptAnnotation candidateConceptAnnotation : candidateConceptAnnotations) {
      if (!excludeConceptAnnotations.contains(candidateConceptAnnotation)) {
        matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
        return candidateConceptAnnotation;
      }
    }
    matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
    return null;
  }

  public String getName() {
    return "Span matcher";
  }

  public String getDescription() {
    return "Annotations match if they have the same spans.";
  }

  public boolean returnsTrivials() {
    return false;
  }
}
