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
package edu.ucdenver.cpbs.mechanic.iaa.matcher;

import edu.ucdenver.cpbs.mechanic.iaa.Annotation;
import edu.ucdenver.cpbs.mechanic.iaa.IAA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"JavadocReference", "unused"})
public class SpansOverlapSimpleFeatureMatcher implements Matcher {

	/**
	 * This method will return an annotation that has overlapping spans and the
	 * same simple features. It is not required that the annotation class match.
	 * Preference will be given to an annotation that has the same class and
	 * spans. Secondary preference will be given to an annotation with the same
	 * span followed by an the shortest annotation with overlapping spans and
	 * the same simple features. If no annotation has overlapping spans and the
	 * same simple features, then null is returned.
	 * 
	 * @param matchResult
	 *            will be set to:
	 *            <ul>
	 *            <li>TRIVIAL_NONMATCH if there are no overlapping annotations
	 *            with the passed in annotation
	 *            <li>NONTRIVIAL_MATCH if there is an annotation that is
	 *            overlapping and the Annotation.compareSimpleFeatures returns
	 *            NONTRIVIAL_MATCH
	 *            <li>TRIVIAL_MATCH if there is an annotation that is
	 *            overlapping and the Annotation.compareSimpleFeatures returns
	 *            TRIVIAL_MATCH <br>
	 *            Note: if there is a trivial_match then there cannot possibly
	 *            be a NONTRIVIAL_MATCH because one of the simple features of
	 *            the passed in annotation must have a null value or there are
	 *            no simple features.
	 *            <li>NONTRIVIAL_NONMATCH if there an annotation that is
	 *            overlapping and the Annotation.compareSimpleFeatures returns
	 *            NONTRIVIAL_NONMATCH
	 *            <li>TRIVIAL_NONMATCH if there is no match or non-trivial
	 *            non-match found.
	 * @return will return the first nontrivial match that it finds preferring
	 * @see edu.uchsc.ccp.iaa.matcher.Matcher#match(Annotation, String, Set,
	 *      IAA, MatchResult)
	 * @see edu.uchsc.ccp.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.uchsc.ccp.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 * @see edu.uchsc.ccp.iaa.matcher.MatchResult#TRIVIAL_MATCH
	 * @see edu.uchsc.ccp.iaa.matcher.MatchResult#TRIVIAL_NONMATCH
	 */

	public Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
							MatchResult matchResult) {

		Annotation spansExactSimpleFeatureMatch = SpansExactSimpleFeatureMatcher.match(annotation, compareSetName, iaa,
				excludeAnnotations, matchResult);

		// if TRIVIAL_MATCH then we do not have to worry about there being an
		// overlapping NONTRIVIAL_MATCH further down
		// because we know that a trivial match is the best we can do.
		if (spansExactSimpleFeatureMatch != null
				&& (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH || matchResult.getResult() == MatchResult.TRIVIAL_MATCH)) {
			return spansExactSimpleFeatureMatch;
		}

		Set<Annotation> candidateAnnotations = new HashSet<>(iaa.getOverlappingAnnotations(annotation,
				compareSetName));
		candidateAnnotations.removeAll(excludeAnnotations);

		// we are going to collect all matches because we want to return the
		// shortest of the matches if there is more than one.
		List<Annotation> nontrivialMatches = new ArrayList<>();
		List<Annotation> trivialMatches = new ArrayList<>();

		boolean nontrivialNonmatch = false;

		for (Annotation candidateAnnotation : candidateAnnotations) {
			if (!excludeAnnotations.contains(candidateAnnotation)) {
				int result = Annotation.compareSimpleFeatures(annotation, candidateAnnotation);
				if (result == MatchResult.NONTRIVIAL_MATCH) {
					nontrivialMatches.add(candidateAnnotation);
				} else if (result == MatchResult.TRIVIAL_MATCH) {
					trivialMatches.add(candidateAnnotation);
				}
				if (result == MatchResult.NONTRIVIAL_NONMATCH) {
					nontrivialNonmatch = true;
				}
			}
		}

		if (nontrivialMatches.size() > 0) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			if (nontrivialMatches.size() == 1) {
				return nontrivialMatches.iterator().next();
			} else {
				return Annotation.getShortestAnnotation(nontrivialMatches);
			}
		}
		if (trivialMatches.size() > 0) {
			matchResult.setResult(MatchResult.TRIVIAL_MATCH);
			if (trivialMatches.size() == 1) {
				return trivialMatches.iterator().next();
			} else {
				return Annotation.getShortestAnnotation(trivialMatches);
			}
		}

		if (nontrivialNonmatch)
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		else
			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
		return null;
	}

	public String getName() {
		return "Simple slots matcher (with overlapping spans)";
	}

	public String getDescription() {
		return "Annotations match if they have overlapping spans and the same value for simple slots (e.g. slots that are primitive values such as integer and String).  Only slots that are specified must match.";
	}

	public boolean returnsTrivials() {
		return true;
	}

}
