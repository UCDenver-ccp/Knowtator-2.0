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

public class ClassAndSpanMatcher implements Matcher {

	/**
	 * This is a static version of the above match method that can be called by
	 * other matcher implementations.
	 *
	 * @return an annotation that matches or null.
	 */
	public static Annotation match(Annotation annotation, String compareSetName, IAA iaa,
								   Set<Annotation> excludeAnnotations) {
		Set<Annotation> singleMatchSet = matches(annotation, compareSetName, iaa, excludeAnnotations);
		if (singleMatchSet.size() == 1) {
			return singleMatchSet.iterator().next();
		} else
			return null;

	}

	/**
	 *
	 * @return this method will not return null - but rather an empty set of no
	 *         matches are found.
	 */

	private static Set<Annotation> matches(Annotation annotation, String compareSetName, IAA iaa,
										   Set<Annotation> excludeAnnotations) {
		String type = annotation.getOwlClass().toString();
		Set<Annotation> candidateAnnotations = new HashSet<>(iaa.getExactlyOverlappingAnnotations(annotation,
				compareSetName));
		candidateAnnotations.removeAll(excludeAnnotations);
		if (candidateAnnotations.size() == 0)
			return Collections.emptySet();

		Set<Annotation> returnValues = new HashSet<>();
		for (Annotation candidateAnnotation : candidateAnnotations) {
			if (!excludeAnnotations.contains(candidateAnnotation)
					&& candidateAnnotation.getOwlClass().toString().equals(type)) {
				returnValues.add(candidateAnnotation);
				return returnValues;
			}
		}
		return returnValues;
	}

	/**
	 * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH.
	 *                    Trivial matches and non-matches are not defined for this
	 *                    matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(Annotation, String, Set,
	 * IAA, MatchResult)
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
		return "Class and Span matcher";
	}

	public String getDescription() {

		return "Annotations match if they have the same class assignment and the same spans.";
	}

	public boolean returnsTrivials() {
		return false;
	}

}
