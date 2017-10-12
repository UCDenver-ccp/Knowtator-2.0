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

import java.util.Set;


@SuppressWarnings("unused")
public class OverlappingSpanMatcher implements Matcher {

	public TextAnnotation match(TextAnnotation textAnnotation, String compareSetName, Set<TextAnnotation> excludeTextAnnotations, IAA iaa,
								MatchResult matchResult) {

		TextAnnotation spanAndClassMatch = ClassAndSpanMatcher.match(textAnnotation, compareSetName, iaa, excludeTextAnnotations);
		if (spanAndClassMatch != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return spanAndClassMatch;
		}

		Set<TextAnnotation> classMatches = ClassMatcher.matches(textAnnotation, compareSetName, iaa, excludeTextAnnotations);
		if (classMatches.size() > 0) {
			TextAnnotation match = TextAnnotation.getShortestAnnotation(classMatches);
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return match;
		}

		Set<TextAnnotation> overlappingTextAnnotations = iaa.getOverlappingAnnotations(textAnnotation, compareSetName);
		if (overlappingTextAnnotations.size() > 0) {
			TextAnnotation match = TextAnnotation.getShortestAnnotation(overlappingTextAnnotations);
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return match;
		}

		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		return null;
	}

	public String getName() {
		return "Overlapping spans matcher";
	}

	public String getDescription() {
		return "Annotations match if they have their spans overlap.";
	}

	public boolean returnsTrivials() {
		return false;
	}

}
