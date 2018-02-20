///*
// * The contents of this test_project are subject to the Mozilla Public
// * License Version 1.1 (the "License"); you may not use this test_project
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
// *   Philip V. Ogren <philip@ogren.info> (Original Author)
// */
//package edu.ucdenver.ccp.knowtator.iaa.matcher;
//
//import edu.ucdenver.ccp.knowtator.annotation.text.annotation;
//import edu.ucdenver.ccp.knowtator.iaa.IAA;
//
//import java.util.Set;
//
//public class OverlappingSpanMatcher implements Matcher {
//
//	public annotation match(annotation annotation, String compareSetName, Set<annotation> excludeAnnotations, IAA iaa,
//							MatchResult matchResult) {
//
//		annotation spanAndClassMatch = ClassAndSpanMatcher.match(annotation, compareSetName, iaa, excludeAnnotations);
//		if (spanAndClassMatch != null) {
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return spanAndClassMatch;
//		}
//
//		Set<annotation> classMatches = ClassMatcher.matches(annotation, compareSetName, iaa, excludeAnnotations);
//		if (classMatches.size() > 0) {
//			annotation match = annotation.getShortestAnnotation(classMatches);
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return match;
//		}
//
//		Set<annotation> overlappingAnnotations = iaa.getOverlappingAnnotations(annotation, compareSetName);
//		if (overlappingAnnotations.size() > 0) {
//			annotation match = annotation.getShortestAnnotation(overlappingAnnotations);
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return match;
//		}
//
//		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
//		return null;
//	}
//
//	public String getDocID() {
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
//}
