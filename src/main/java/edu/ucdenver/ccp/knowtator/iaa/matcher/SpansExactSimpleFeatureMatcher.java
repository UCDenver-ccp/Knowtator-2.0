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

import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.iaa.IAA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpansExactSimpleFeatureMatcher implements Matcher {
	// public static final String FEATURE_NAMES =
	// SpansExactSimpleFeatureMatcher.class.getName()+".FEATURE_NAMES";

	/**
	 * This method will return an Annotation that has the exact same spans and
	 * simple features. It is not required that the Annotation class match.
	 * Preference will be given to an Annotation that has the same class as well
	 * as spans and simple features. If one does not exist, then null is
	 * returned.
	 */
	public Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
							MatchResult matchResult) {
		return match(annotation, compareSetName, iaa, excludeAnnotations, matchResult);
	}

	/**
	 * This method will return an Annotation that has the exact same spans and
	 * simple features. It is not required that the Annotation class match.
	 * Preference will be given to an Annotation that has the same class as well
	 * as spans and simple features. If one does not exist, then null is
	 * returned.
	 *
	 * @param matchResult
	 *            will be set to:
	 *            <ul>
	 *            <li>TRIVIAL_NONMATCH if there are no exactly overlapping
	 *            annotations with the passed in Annotation
	 *            <li>NONTRIVIAL_MATCH if there is an Annotation that is exactly
	 *            overlapping and the Annotation.compareSimpleFeatures returns
	 *            NONTRIVIAL_MATCH
	 *            <li>TRIVIAL_MATCH if there is an Annotation that is exactly
	 *            overlapping and the Annotation.compareSimpleFeatures returns
	 *            TRIVIAL_MATCH <br>
	 *            Note: if there is a trivial_match then there cannot possibly
	 *            be a NONTRIVIAL_MATCH because one of the simple features of
	 *            the passed in Annotation must have a null value or there are
	 *            no simple features.
	 *            <li>NONTRIVIAL_NONMATCH if there an Annotation that is exactly
	 *            overlapping and the Annotation.compareSimpleFeatures returns
	 *            NONTRIVIAL_NONMATCH
	 *            <li>TRIVIAL_NONMATCH if there is no match or non-trivial
	 *            non-match found.
	 * @return will return the first nontrivial match that it finds preferring
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(Annotation, String, Set,
	 *      IAA, MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#TRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#TRIVIAL_NONMATCH
	 */

	public static Annotation match(Annotation annotation, String compareSetName, IAA iaa,
								   Set<Annotation> excludeAnnotations, MatchResult matchResult) {
		// prefer class and Span matches over just Span matches
		Set<Annotation> classAndSpanMatches = ClassAndSpanMatcher.matches(annotation, compareSetName, iaa,
				excludeAnnotations, false);
		Set<Annotation> exactlyOverlappingAnnotations = new HashSet<>(iaa.getExactlyOverlappingAnnotations(
				annotation, compareSetName));
		exactlyOverlappingAnnotations.removeAll(classAndSpanMatches);
		exactlyOverlappingAnnotations.removeAll(excludeAnnotations);

		List<Annotation> candidateAnnotations = new ArrayList<>(classAndSpanMatches.size()
				+ exactlyOverlappingAnnotations.size());
		candidateAnnotations.addAll(classAndSpanMatches);
		candidateAnnotations.addAll(exactlyOverlappingAnnotations);

		boolean nontrivialNonmatch = false;

		for (Annotation candidateAnnotation : candidateAnnotations) {
			if (!excludeAnnotations.contains(candidateAnnotation)) {
				int result = Annotation.compareSimpleFeatures(annotation, candidateAnnotation);
				// if there is a trivial_match then there cannot possibly be a
				// NONTRIVIAL_MATCH
				// because one of the simple features of the passed in
				// Annotation must have a null value
				// or there are no simple features.
				if (result == MatchResult.NONTRIVIAL_MATCH || result == MatchResult.TRIVIAL_MATCH) {
					matchResult.setResult(result);
					return candidateAnnotation;
				}
				if (result == MatchResult.NONTRIVIAL_NONMATCH) {
					nontrivialNonmatch = true;
				}
			}
		}

		if (nontrivialNonmatch)
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		else
			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
		return null;
	}

	public String getName() {
		return "Simple slots matcher (with same spans)";
	}

	public String getDescription() {
		return "Annotations match if they have the same spans and the same value for simple slots (e.g. slots that are primitive values such as integer and String).  Only slots that are specified must match.";
	}

	public boolean returnsTrivials() {
		return true;
	}

}
