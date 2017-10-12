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
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"JavadocReference", "unused", "JavaDoc"})
public class SpanMatcher implements Matcher {
	/**
	 * This method will return an annotation with the same class and spans. If
	 * one does not exist, then it will return an annotation with the same spans
	 * (but different class). Otherwise, null is returned.
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

	public TextAnnotation match(TextAnnotation textAnnotation, String compareSetName, Set<TextAnnotation> excludeTextAnnotations, IAA iaa,
								MatchResult matchResult) {
		TextAnnotation spanAndClassMatch = ClassAndSpanMatcher.match(textAnnotation, compareSetName, iaa, excludeTextAnnotations);
		if (spanAndClassMatch != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return spanAndClassMatch;
		}

		Set<TextAnnotation> candidateTextAnnotations = new HashSet<>(iaa.getExactlyOverlappingAnnotations(textAnnotation,
				compareSetName));
		candidateTextAnnotations.remove(excludeTextAnnotations);

		for (TextAnnotation candidateTextAnnotation : candidateTextAnnotations) {
			if (!excludeTextAnnotations.contains(candidateTextAnnotation)) {
				matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
				return candidateTextAnnotation;
			}
		}
		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		return null;
	}

	public String getName() {
		return "TextSpan matcher";
	}

	public String getDescription() {
		return "Annotations match if they have the same spans.";
	}

	public boolean returnsTrivials() {
		return false;
	}

}
