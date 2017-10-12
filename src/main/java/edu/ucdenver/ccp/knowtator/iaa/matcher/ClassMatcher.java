/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Knowtator.
 *
 * The Initial Developer of the Original Code is University of Colorado.  
 * Copyright (C) 2005 - 2008.  All Rights Reserved.
 *
 * Knowtator was developed by the Center for Computational Pharmacology
 * (http://compbio.uchcs.edu) at the University of Colorado Health 
 *  Sciences Center School of Medicine with support from the National 
 *  Library of Medicine.  
 *
 * Current information about Knowtator can be obtained at 
 * http://knowtator.sourceforge.net/
 *
 * Contributor(s):
 *   Philip V. Ogren <philip@ogren.info> (Original Author)
 */
package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.iaa.IAA;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("JavadocReference")
public class ClassMatcher implements Matcher {
	/**
	 * This method will return an annotation with the same class and spans. If
	 * one does not exist, then it will return an annotation with the same class
	 * and overlapping spans. If more than one of these exists, then the
	 * shortest annotation with the same class and overlapping spans will be
	 * returned. Otherwise, null is returned.
	 * 
	 * @param textAnnotation
	 * @param compareSetName
	 * @param excludeTextAnnotations
	 * @param iaa
	 * @param matchResult
	 *            will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH.
	 *            Trivial matches and non-matches are not defined for this
	 *            matcher.
	 * @see edu.uchsc.ccp.iaa.matcher.Matcher#match(TextAnnotation, String, Set,
	 *      IAA, MatchResult)
	 * @see edu.uchsc.ccp.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.uchsc.ccp.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 * @see edu.uchsc.ccp.iaa.Annotation#getShortestAnnotation(Collection)
	 */

	@SuppressWarnings("JavaDoc")
	public TextAnnotation match(TextAnnotation textAnnotation, String compareSetName, Set<TextAnnotation> excludeTextAnnotations, IAA iaa,
								MatchResult matchResult) {
		TextAnnotation match = match(textAnnotation, compareSetName, iaa, excludeTextAnnotations);
		if (match != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return match;
		} else {
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			return null;
		}
	}

	public static TextAnnotation match(TextAnnotation textAnnotation, String compareSetName, IAA iaa,
                                       Set<TextAnnotation> excludeTextAnnotations) {
		TextAnnotation spanAndClassMatch = ClassAndSpanMatcher.match(textAnnotation, compareSetName, iaa, excludeTextAnnotations);
		if (spanAndClassMatch != null) {
			return spanAndClassMatch;
		}

		Set<TextAnnotation> matches = matches(textAnnotation, compareSetName, iaa, excludeTextAnnotations);
		if (matches.size() > 0) {
			if (matches.size() == 1)
				return matches.iterator().next();
			else {
				return TextAnnotation.getShortestAnnotation(matches);
			}
		} else {
			return null;
		}
	}

	public static Set<TextAnnotation> matches(TextAnnotation textAnnotation, String compareSetName, IAA iaa,
											  Set<TextAnnotation> excludeTextAnnotations) {

		Set<TextAnnotation> overlappingTextAnnotations = iaa.getOverlappingAnnotations(textAnnotation, compareSetName);
		Set<TextAnnotation> annotationsOfSameType = iaa.getAnnotationsOfSameType(textAnnotation, compareSetName);
		Set<TextAnnotation> candidateTextAnnotations = new HashSet<>(overlappingTextAnnotations);
		candidateTextAnnotations.retainAll(annotationsOfSameType);
		candidateTextAnnotations.removeAll(excludeTextAnnotations);

		if (candidateTextAnnotations.size() > 0) {
			return Collections.unmodifiableSet(candidateTextAnnotations);
		} else {
			return Collections.emptySet();
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
