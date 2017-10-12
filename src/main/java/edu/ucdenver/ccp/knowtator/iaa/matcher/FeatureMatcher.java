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

import java.util.*;

@SuppressWarnings("unused")
public class FeatureMatcher implements Matcher {
	private boolean matchClasses = true;

	private int matchSpans = TextAnnotation.SPANS_OVERLAP_COMPARISON;

	private Set<String> comparedSimpleFeatures = new HashSet<>();

	private Map<String, ComplexFeatureMatchCriteria> comparedComplexFeatures = new HashMap<>();

	private String name;

	public FeatureMatcher() {
		this("Feature Matcher");
	}

	public FeatureMatcher(String name) {
		this.name = name;
	}

	public void setMatchClasses(boolean matchClasses) {
		this.matchClasses = matchClasses;
	}

	public void setMatchSpans(int matchSpans) {
		this.matchSpans = matchSpans;
	}

	public void addComparedSimpleFeatures(String simpleFeatureName) {
		comparedSimpleFeatures.add(simpleFeatureName);
	}

	public void addComparedComplexFeature(String complexFeatureName, ComplexFeatureMatchCriteria matchCriteria) {
		comparedComplexFeatures.put(complexFeatureName, matchCriteria);
	}

	public TextAnnotation match(TextAnnotation textAnnotation, String compareSetName, Set<TextAnnotation> excludeTextAnnotations, IAA iaa,
                                MatchResult matchResult) {
		Set<TextAnnotation> candidateTextAnnotations = new HashSet<>();
		if (matchClasses) {
			if (matchSpans == TextAnnotation.SPANS_EXACT_COMPARISON) {
				candidateTextAnnotations.addAll(iaa.getAnnotationsOfSameType(textAnnotation, compareSetName));
				candidateTextAnnotations.retainAll(iaa.getExactlyOverlappingAnnotations(textAnnotation, compareSetName));
			} else if (matchSpans == TextAnnotation.SPANS_OVERLAP_COMPARISON) {
				// Set<TextAnnotation> someAnnotations =
				// iaa.getExactlyOverlappingAnnotations(textAnnotation,
				// compareSetName);
				//					
				// Set<TextAnnotation> someAnnotations =
				// iaa.getExactlyOverlappingAnnotations(textAnnotation,
				// compareSetName);
				//					
				candidateTextAnnotations.addAll(iaa.getExactlyOverlappingAnnotations(textAnnotation, compareSetName));
				candidateTextAnnotations.addAll(iaa.getOverlappingAnnotations(textAnnotation, compareSetName));
				candidateTextAnnotations.retainAll(iaa.getAnnotationsOfSameType(textAnnotation, compareSetName));
			}
		} else {
			if (matchSpans == TextAnnotation.SPANS_EXACT_COMPARISON) {
				// we want all annotations that have the exactly matching spans,
				// but we want them ordered in the following way:
				// 1) annotations with the same class and same spans
				// 2) annotations with the same spans
				candidateTextAnnotations.addAll(iaa.getAnnotationsOfSameType(textAnnotation, compareSetName));
				Set<TextAnnotation> exactlyOverlappingTextAnnotations = iaa.getExactlyOverlappingAnnotations(textAnnotation,
						compareSetName);
				candidateTextAnnotations.retainAll(exactlyOverlappingTextAnnotations);
				candidateTextAnnotations.addAll(exactlyOverlappingTextAnnotations);
			} else if (matchSpans == TextAnnotation.SPANS_OVERLAP_COMPARISON) {
				// we want all annotations that are overlapping, but we want
				// them ordered in the following way:
				// 1) annotations with the same class and same spans
				// 2) annotations with the same class and overlapping spans
				// 3) annotations with the same spans
				// 4) annotations with overlapping spans
				Set<TextAnnotation> classTextAnnotations = iaa.getAnnotationsOfSameType(textAnnotation, compareSetName);
				Set<TextAnnotation> exactlyOverlappingTextAnnotations = iaa.getExactlyOverlappingAnnotations(textAnnotation,
						compareSetName);
				Set<TextAnnotation> overlappingTextAnnotations = iaa.getOverlappingAnnotations(textAnnotation, compareSetName);

				Set<TextAnnotation> classAndExactSpanTextAnnotations = new HashSet<>(classTextAnnotations);
				classAndExactSpanTextAnnotations.retainAll(exactlyOverlappingTextAnnotations);

				Set<TextAnnotation> classAndOverlappingSpanTextAnnotations = new HashSet<>(classTextAnnotations);
				classAndOverlappingSpanTextAnnotations.retainAll(overlappingTextAnnotations);

				candidateTextAnnotations.addAll(classAndExactSpanTextAnnotations);
				candidateTextAnnotations.addAll(classAndOverlappingSpanTextAnnotations);
				candidateTextAnnotations.addAll(exactlyOverlappingTextAnnotations);
				candidateTextAnnotations.addAll(overlappingTextAnnotations);
			} else {
				// we want all annotations that are in the other set, but we
				// want them ordered in the following way:
				// 1) annotations with the same class and same spans
				// 2) annotations with the same class and overlapping spans
				// 3) annotations with the same spans
				// 4) annotations with overlapping spans
				// 5) annotations with the same class
				// 6) all other annotations
				Set<TextAnnotation> classTextAnnotations = iaa.getAnnotationsOfSameType(textAnnotation, compareSetName);
				Set<TextAnnotation> exactlyOverlappingTextAnnotations = iaa.getExactlyOverlappingAnnotations(textAnnotation,
						compareSetName);
				Set<TextAnnotation> overlappingTextAnnotations = iaa.getOverlappingAnnotations(textAnnotation, compareSetName);

				Set<TextAnnotation> classAndExactSpanTextAnnotations = new HashSet<>(classTextAnnotations);
				classAndExactSpanTextAnnotations.retainAll(exactlyOverlappingTextAnnotations);

				Set<TextAnnotation> classAndOverlappingSpanTextAnnotations = new HashSet<>(classTextAnnotations);
				classAndOverlappingSpanTextAnnotations.retainAll(overlappingTextAnnotations);

				candidateTextAnnotations.addAll(classAndExactSpanTextAnnotations);
				candidateTextAnnotations.addAll(classAndOverlappingSpanTextAnnotations);
				candidateTextAnnotations.addAll(exactlyOverlappingTextAnnotations);
				candidateTextAnnotations.addAll(overlappingTextAnnotations);
				candidateTextAnnotations.addAll(classTextAnnotations);
				candidateTextAnnotations.addAll(iaa.getAnnotationSets().get(compareSetName));
			}
		}

		candidateTextAnnotations.removeAll(excludeTextAnnotations);

		if (candidateTextAnnotations.size() == 0) {
			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
			return null;
		}

		// we are going to collect all matches because we want to return the
		// shortest of the matches if there is more than one.
		List<TextAnnotation> nontrivialMatches = new ArrayList<>();
		List<TextAnnotation> trivialMatches = new ArrayList<>();
		boolean nontrivialNonmatch = false;

		for (TextAnnotation candidateTextAnnotation : candidateTextAnnotations) {
			int result;
			if (comparedSimpleFeatures.size() > 0)
				result = TextAnnotation.compareSimpleFeatures(textAnnotation, candidateTextAnnotation, comparedSimpleFeatures);
			else
				result = MatchResult.NONTRIVIAL_MATCH;

			if (result == MatchResult.TRIVIAL_NONMATCH)
				continue;

			for (String complexFeatureName : comparedComplexFeatures.keySet()) {
				ComplexFeatureMatchCriteria matchCriteria = comparedComplexFeatures.get(complexFeatureName);

				int complexResult = TextAnnotation.compareComplexFeature(textAnnotation, candidateTextAnnotation,
						complexFeatureName, matchCriteria.matchSpans, matchCriteria.matchClasses,
						matchCriteria.comparedSimpleFeatures,
						matchCriteria.trivialSimpleFeatureMatchesCauseTrivialMatch);

				if (complexResult == MatchResult.TRIVIAL_NONMATCH) {
					result = MatchResult.TRIVIAL_NONMATCH;
					break;
				} else if (complexResult == MatchResult.NONTRIVIAL_NONMATCH
						&& (result == MatchResult.NONTRIVIAL_MATCH || result == MatchResult.TRIVIAL_MATCH)) {
					result = MatchResult.NONTRIVIAL_NONMATCH;
				} else if (complexResult == MatchResult.TRIVIAL_MATCH && result == MatchResult.NONTRIVIAL_MATCH) {
					result = MatchResult.TRIVIAL_MATCH;
				}
			}

            switch (result) {
                case MatchResult.TRIVIAL_NONMATCH:
                    break;
                case MatchResult.NONTRIVIAL_NONMATCH:
                    nontrivialNonmatch = true;
                    break;
                case MatchResult.TRIVIAL_MATCH:
                    trivialMatches.add(candidateTextAnnotation);
                    break;
                case MatchResult.NONTRIVIAL_MATCH:
                    nontrivialMatches.add(candidateTextAnnotation);
                    break;
            }
		}

		if (nontrivialMatches.size() > 0) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			if (nontrivialMatches.size() == 1)
				return nontrivialMatches.iterator().next();
			else
				return TextAnnotation.getShortestAnnotation(nontrivialMatches);
		}
		if (trivialMatches.size() > 0) {
			matchResult.setResult(MatchResult.TRIVIAL_MATCH);
			if (trivialMatches.size() == 1)
				return trivialMatches.iterator().next();
			else
				return TextAnnotation.getShortestAnnotation(trivialMatches);
		}

		if (nontrivialNonmatch)
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		else
			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
		return null;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return "";
	}

	public boolean returnsTrivials() {
		return true;
	}

	public int getMatchSpans() {
		return matchSpans;
	}

}
