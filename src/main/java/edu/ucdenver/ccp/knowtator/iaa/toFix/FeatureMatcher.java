/// *
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
// package edu.ucdenver.ccp.knowtator.iaa.matcher;
//
// import edu.ucdenver.ccp.knowtator.concept.text.concept;
// import edu.ucdenver.ccp.knowtator.iaa.IAA;
//
// import java.util.*;
//
// public class FeatureMatcher implements Matcher {
//	boolean matchClasses = true;
//
//	int matchSpans = concept.SPANS_OVERLAP_COMPARISON;
//
//	Set<String> comparedSimpleFeatures = new HashSet<>();
//
//	Map<String, ComplexFeatureMatchCriteria> comparedComplexFeatures = new HashMap<>();
//
//	String docID;
//
//	public FeatureMatcher(String docID) {
//		this.docID = docID;
//	}
//
//	public void setMatchClasses(boolean matchClasses) {
//		this.matchClasses = matchClasses;
//	}
//
//	public void setMatchSpans(int matchSpans) {
//		this.matchSpans = matchSpans;
//	}
//
//	public void addComparedSimpleFeatures(String simpleFeatureName) {
//		comparedSimpleFeatures.add(simpleFeatureName);
//	}
//
//	public void addComparedComplexFeature(String complexFeatureName, ComplexFeatureMatchCriteria
// matchCriteria) {
//		comparedComplexFeatures.put(complexFeatureName, matchCriteria);
//	}
//
//	public concept match(concept concept, String compareSetName, Set<concept>
// excludeAnnotations, IAA iaa,
//							MatchResult matchResult) {
//		Set<concept> candidateAnnotations = new HashSet<>();
//		if (matchClasses) {
//			if (matchSpans == concept.SPANS_EXACT_COMPARISON) {
//				candidateAnnotations.addAll(iaa.getAnnotationsOfSameType(concept, compareSetName));
//				candidateAnnotations.retainAll(iaa.getExactlyOverlappingAnnotations(concept,
// compareSetName));
//			} else if (matchSpans == concept.SPANS_OVERLAP_COMPARISON) {
//				// Set<concept> someAnnotations =
//				// iaa.getExactlyOverlappingAnnotations(concept,
//				// compareSetName);
//				//
//				// Set<concept> someAnnotations =
//				// iaa.getExactlyOverlappingAnnotations(concept,
//				// compareSetName);
//				//
//				candidateAnnotations.addAll(iaa.getExactlyOverlappingAnnotations(concept, compareSetName));
//				candidateAnnotations.addAll(iaa.getOverlappingConceptAnnotations(concept, compareSetName));
//				candidateAnnotations.retainAll(iaa.getAnnotationsOfSameType(concept, compareSetName));
//			}
//		} else {
//			if (matchSpans == concept.SPANS_EXACT_COMPARISON) {
//				// we want all annotations that have the exactly matching spans,
//				// but we want them ordered in the following way:
//				// 1) annotations with the same class and same spans
//				// 2) annotations with the same spans
//				candidateAnnotations.addAll(iaa.getAnnotationsOfSameType(concept, compareSetName));
//				Set<concept> exactlyOverlappingAnnotations =
// iaa.getExactlyOverlappingAnnotations(concept,
//						compareSetName);
//				candidateAnnotations.retainAll(exactlyOverlappingAnnotations);
//				candidateAnnotations.addAll(exactlyOverlappingAnnotations);
//			} else if (matchSpans == concept.SPANS_OVERLAP_COMPARISON) {
//				// we want all annotations that are overlapping, but we want
//				// them ordered in the following way:
//				// 1) annotations with the same class and same spans
//				// 2) annotations with the same class and overlapping spans
//				// 3) annotations with the same spans
//				// 4) annotations with overlapping spans
//				Set<concept> classAnnotations = iaa.getAnnotationsOfSameType(concept, compareSetName);
//				Set<concept> exactlyOverlappingAnnotations =
// iaa.getExactlyOverlappingAnnotations(concept,
//						compareSetName);
//				Set<concept> overlappingAnnotations = iaa.getOverlappingConceptAnnotations(concept,
// compareSetName);
//
//				Set<concept> classAndExactSpanAnnotations = new HashSet<>(classAnnotations);
//				classAndExactSpanAnnotations.retainAll(exactlyOverlappingAnnotations);
//
//				Set<concept> classAndOverlappingSpanAnnotations = new HashSet<>(classAnnotations);
//				classAndOverlappingSpanAnnotations.retainAll(overlappingAnnotations);
//
//				candidateAnnotations.addAll(classAndExactSpanAnnotations);
//				candidateAnnotations.addAll(classAndOverlappingSpanAnnotations);
//				candidateAnnotations.addAll(exactlyOverlappingAnnotations);
//				candidateAnnotations.addAll(overlappingAnnotations);
//			} else {
//				// we want all annotations that are in the other set, but we
//				// want them ordered in the following way:
//				// 1) annotations with the same class and same spans
//				// 2) annotations with the same class and overlapping spans
//				// 3) annotations with the same spans
//				// 4) annotations with overlapping spans
//				// 5) annotations with the same class
//				// 6) all other annotations
//				Set<concept> classAnnotations = iaa.getAnnotationsOfSameType(concept, compareSetName);
//				Set<concept> exactlyOverlappingAnnotations =
// iaa.getExactlyOverlappingAnnotations(concept,
//						compareSetName);
//				Set<concept> overlappingAnnotations = iaa.getOverlappingConceptAnnotations(concept,
// compareSetName);
//
//				Set<concept> classAndExactSpanAnnotations = new HashSet<>(classAnnotations);
//				classAndExactSpanAnnotations.retainAll(exactlyOverlappingAnnotations);
//
//				Set<concept> classAndOverlappingSpanAnnotations = new HashSet<>(classAnnotations);
//				classAndOverlappingSpanAnnotations.retainAll(overlappingAnnotations);
//
//				candidateAnnotations.addAll(classAndExactSpanAnnotations);
//				candidateAnnotations.addAll(classAndOverlappingSpanAnnotations);
//				candidateAnnotations.addAll(exactlyOverlappingAnnotations);
//				candidateAnnotations.addAll(overlappingAnnotations);
//				candidateAnnotations.addAll(classAnnotations);
//				candidateAnnotations.addAll(iaa.getAnnotationSets().get(compareSetName));
//			}
//		}
//
//		candidateAnnotations.clear(excludeAnnotations);
//
//		if (candidateAnnotations.size() == 0) {
//			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
//			return null;
//		}
//
//		// we are going to collect all matches because we want to return the
//		// shortest of the matches if there is more than one.
//		List<concept> nontrivialMatches = new ArrayList<>();
//		List<concept> trivialMatches = new ArrayList<>();
//		boolean nontrivialNonmatch = false;
//
//		for (concept candidateAnnotation : candidateAnnotations) {
//			int result;
//			if (comparedSimpleFeatures.size() > 0)
//				result = concept.compareSimpleFeatures(concept, candidateAnnotation,
// comparedSimpleFeatures);
//			else
//				result = MatchResult.NONTRIVIAL_MATCH;
//
//			if (result == MatchResult.TRIVIAL_NONMATCH)
//				continue;
//
//			for (String complexFeatureName : comparedComplexFeatures.keySet()) {
//				ComplexFeatureMatchCriteria matchCriteria = comparedComplexFeatures.get(complexFeatureName);
//
//				int complexResult = concept.compareComplexFeature(concept, candidateAnnotation,
//						complexFeatureName, matchCriteria.matchSpans, matchCriteria.matchClasses,
//						matchCriteria.comparedSimpleFeatures,
//						matchCriteria.trivialSimpleFeatureMatchesCauseTrivialMatch);
//
//				if (complexResult == MatchResult.TRIVIAL_NONMATCH) {
//					result = MatchResult.TRIVIAL_NONMATCH;
//					break;
//				} else if (complexResult == MatchResult.NONTRIVIAL_NONMATCH
//						&& (result == MatchResult.NONTRIVIAL_MATCH || result == MatchResult.TRIVIAL_MATCH)) {
//					result = MatchResult.NONTRIVIAL_NONMATCH;
//				} else if (complexResult == MatchResult.TRIVIAL_MATCH && result ==
// MatchResult.NONTRIVIAL_MATCH) {
//					result = MatchResult.TRIVIAL_MATCH;
//				}
//			}
//
//			if (result == MatchResult.NONTRIVIAL_NONMATCH) {
//				nontrivialNonmatch = true;
//			} else if (result == MatchResult.TRIVIAL_MATCH) {
//				trivialMatches.add(candidateAnnotation);
//			} else if (result == MatchResult.NONTRIVIAL_MATCH) {
//				nontrivialMatches.add(candidateAnnotation);
//			}
//		}
//
//		if (nontrivialMatches.size() > 0) {
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			if (nontrivialMatches.size() == 1)
//				return nontrivialMatches.iterator().next();
//			else
//				return concept.getShortestAnnotation(nontrivialMatches);
//		}
//		if (trivialMatches.size() > 0) {
//			matchResult.setResult(MatchResult.TRIVIAL_MATCH);
//			if (trivialMatches.size() == 1)
//				return trivialMatches.iterator().next();
//			else
//				return concept.getShortestAnnotation(trivialMatches);
//		}
//
//		if (nontrivialNonmatch)
//			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
//		else
//			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
//		return null;
//	}
//
//	public String getId() {
//		return docID;
//	}
//
//	public String getDescription() {
//		return "";
//	}
//
//	public boolean returnsTrivials() {
//		return true;
//	}
//
// }
