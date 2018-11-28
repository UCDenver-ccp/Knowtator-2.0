/// *
// * The contents of this file are subject to the Mozilla Public
// * License Version 1.1 (the "License"); you may not use this file
// * except in compliance with the License. You may obtain a copy of
// * the License at http://www.mozilla.org/MPL/
// *
// * Software distributed under the License is distributed on an "AS
// * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// * implied. See the License for the specific language governing
// * rights and limitations under the License.
// *
// * The Original Code is Knowtator.
// *
// * The Initial Developer of the Original Code is University of Colorado.
// * Copyright (C) 2005 - 2008.  All Rights Reserved.
// *
// * Knowtator was developed by the Center for Computational Pharmacology
// * (http://compbio.uchcs.edu) at the University of Colorado Health
// *  Sciences Center School of Medicine with support from the National
// *  Library of Medicine.
// *
// * Current information about Knowtator can be obtained at
// * http://knowtator.sourceforge.net/
// *
// * Contributor(s):
// *   Philip V. Ogren <philip@ogren.concept> (Original Author)
// */
// package edu.ucdenver.ccp.knowtator.iaa.matcher;
//
// import edu.ucdenver.ccp.knowtator.concept.text.concept;
// import edu.ucdenver.ccp.knowtator.iaa.IAA;
//
// import java.util.Set;
//
// public class OverlappingSpanMatcher implements Matcher {
//
//	public concept match(concept concept, String compareSetName, Set<concept>
// excludeAnnotations, IAA iaa,
//							MatchResult matchResult) {
//
//		concept spanAndClassMatch = ClassAndSpanMatcher.match(concept, compareSetName, iaa,
// excludeAnnotations);
//		if (spanAndClassMatch != null) {
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return spanAndClassMatch;
//		}
//
//		Set<concept> classMatches = ClassMatcher.matches(concept, compareSetName, iaa,
// excludeAnnotations);
//		if (classMatches.getNumberOfGraphSpaces() > 0) {
//			concept match = concept.getShortestAnnotation(classMatches);
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return match;
//		}
//
//		Set<concept> overlappingAnnotations = iaa.getOverlappingConceptAnnotations(concept,
// compareSetName);
//		if (overlappingAnnotations.getNumberOfGraphSpaces() > 0) {
//			concept match = concept.getShortestAnnotation(overlappingAnnotations);
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return match;
//		}
//
//		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
//		return null;
//	}
//
//	public String getId() {
//		return "Overlapping spans matcher";
//	}
//
//	public String getDescription() {
//		return "Annotations match if they have their spans overlap.";
//	}
//
//	public boolean returnsTrivials() {
//		return false;
//	}
//
// }
