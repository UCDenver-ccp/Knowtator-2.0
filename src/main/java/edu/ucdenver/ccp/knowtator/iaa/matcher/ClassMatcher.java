/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassMatcher implements Matcher {
	private static Annotation match(Annotation annotation, String compareSetName, IAA iaa,
									Set<Annotation> excludeAnnotations) {
		Annotation spanAndClassMatch = ClassAndSpanMatcher.match(annotation, compareSetName, iaa, excludeAnnotations);
		if (spanAndClassMatch != null) {
			return spanAndClassMatch;
		}

		Set<Annotation> matches = matches(annotation, compareSetName, iaa, excludeAnnotations);
		if (matches.size() > 0) {
			if (matches.size() == 1)
				return matches.iterator().next();
			else {
				return IAA.getShortestAnnotation(matches);
			}
		} else {
			return null;
		}
	}

	private static Set<Annotation> matches(Annotation annotation, String compareSetName, IAA iaa,
										   Set<Annotation> excludeAnnotations) {

		Set<Annotation> overlappingAnnotations = iaa.getOverlappingAnnotations(annotation, compareSetName);
		Set<Annotation> annotationsOfSameType = iaa.getAnnotationsOfSameType(annotation, compareSetName);
		Set<Annotation> candidateAnnotations = new HashSet<>(overlappingAnnotations);
		candidateAnnotations.retainAll(annotationsOfSameType);
		candidateAnnotations.removeAll(excludeAnnotations);

		if (candidateAnnotations.size() > 0) {
			return Collections.unmodifiableSet(candidateAnnotations);
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * This method will return an annotation with the same class and spans. If
	 * one does not exist, then it will return an annotation with the same class
	 * and overlapping spans. If more than one of these exists, then the
	 * shortest annotation with the same class and overlapping spans will be
	 * returned. Otherwise, null is returned.
	 *
	 * @param matchResult
	 *            will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH.
	 *            Trivial matches and non-matches are not defined for this
	 *            matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(Annotation, String, Set,
	 *      IAA, MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 */

	@SuppressWarnings("Duplicates")
	public Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
							MatchResult matchResult) {
		Annotation match = match(annotation, compareSetName, iaa, excludeAnnotations);
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
