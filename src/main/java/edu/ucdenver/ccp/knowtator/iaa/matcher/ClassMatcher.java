/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassMatcher implements Matcher {
	private static ConceptAnnotation match(
			ConceptAnnotation conceptAnnotation, String compareSetName, IAA iaa, Set<ConceptAnnotation> excludeConceptAnnotations) {
		ConceptAnnotation spanAndClassMatch =
				ClassAndSpanMatcher.match(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
		if (spanAndClassMatch != null) {
			return spanAndClassMatch;
		}

		Set<ConceptAnnotation> matches = matches(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
		if (matches.size() > 0) {
			if (matches.size() == 1) return matches.iterator().next();
			else {
				return IAA.getShortestAnnotation(matches);
			}
		} else {
			return null;
		}
	}

	private static Set<ConceptAnnotation> matches(
			ConceptAnnotation conceptAnnotation, String compareSetName, IAA iaa, Set<ConceptAnnotation> excludeConceptAnnotations) {

		Set<ConceptAnnotation> overlappingConceptAnnotations =
				iaa.getOverlappingAnnotations(conceptAnnotation, compareSetName);
		Set<ConceptAnnotation> annotationsOfSameType =
				iaa.getAnnotationsOfSameType(conceptAnnotation, compareSetName);
		Set<ConceptAnnotation> candidateConceptAnnotations = new HashSet<>(overlappingConceptAnnotations);
		candidateConceptAnnotations.retainAll(annotationsOfSameType);
		candidateConceptAnnotations.removeAll(excludeConceptAnnotations);

		if (candidateConceptAnnotations.size() > 0) {
			return Collections.unmodifiableSet(candidateConceptAnnotations);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * This method will return an concept with the same class and spans. If one does not exist,
	 * then it will return an concept with the same class and overlapping spans. If more than one
	 * of these exists, then the shortest concept with the same class and overlapping spans will be
	 * returned. Otherwise, null is returned.
	 *
	 * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH. Trivial matches and
	 *                    non-matches are not defined for this matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(ConceptAnnotation, String, Set, IAA,
	 * MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 */
	@SuppressWarnings("Duplicates")
	public ConceptAnnotation match(
			ConceptAnnotation conceptAnnotation,
			String compareSetName,
			Set<ConceptAnnotation> excludeConceptAnnotations,
			IAA iaa,
			MatchResult matchResult) {
		ConceptAnnotation match = match(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
		if (match != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return match;
		} else {
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			return null;
		}
	}

	public String getName() {
		return "Class matcher";
	}

	public String getDescription() {
		return "Annotations match if they have the same class assignment and their spans overlap.";
	}

	public boolean returnsTrivials() {
		return false;
	}
}
