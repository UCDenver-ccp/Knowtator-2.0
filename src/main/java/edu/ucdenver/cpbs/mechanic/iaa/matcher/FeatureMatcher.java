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

import java.util.*;

@SuppressWarnings("unused")
public class FeatureMatcher implements Matcher {
	private boolean matchClasses = true;

	private int matchSpans = Annotation.SPANS_OVERLAP_COMPARISON;

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

	public Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
			MatchResult matchResult) {
		Set<Annotation> candidateAnnotations = new HashSet<>();
		if (matchClasses) {
			if (matchSpans == Annotation.SPANS_EXACT_COMPARISON) {
				candidateAnnotations.addAll(iaa.getAnnotationsOfSameType(annotation, compareSetName));
				candidateAnnotations.retainAll(iaa.getExactlyOverlappingAnnotations(annotation, compareSetName));
			} else if (matchSpans == Annotation.SPANS_OVERLAP_COMPARISON) {
				// Set<Annotation> someAnnotations =
				// iaa.getExactlyOverlappingAnnotations(annotation,
				// compareSetName);
				//					
				// Set<Annotation> someAnnotations =
				// iaa.getExactlyOverlappingAnnotations(annotation,
				// compareSetName);
				//					
				candidateAnnotations.addAll(iaa.getExactlyOverlappingAnnotations(annotation, compareSetName));
				candidateAnnotations.addAll(iaa.getOverlappingAnnotations(annotation, compareSetName));
				candidateAnnotations.retainAll(iaa.getAnnotationsOfSameType(annotation, compareSetName));
			}
		} else {
			if (matchSpans == Annotation.SPANS_EXACT_COMPARISON) {
				// we want all annotations that have the exactly matching spans,
				// but we want them ordered in the following way:
				// 1) annotations with the same class and same spans
				// 2) annotations with the same spans
				candidateAnnotations.addAll(iaa.getAnnotationsOfSameType(annotation, compareSetName));
				Set<Annotation> exactlyOverlappingAnnotations = iaa.getExactlyOverlappingAnnotations(annotation,
						compareSetName);
				candidateAnnotations.retainAll(exactlyOverlappingAnnotations);
				candidateAnnotations.addAll(exactlyOverlappingAnnotations);
			} else if (matchSpans == Annotation.SPANS_OVERLAP_COMPARISON) {
				// we want all annotations that are overlapping, but we want
				// them ordered in the following way:
				// 1) annotations with the same class and same spans
				// 2) annotations with the same class and overlapping spans
				// 3) annotations with the same spans
				// 4) annotations with overlapping spans
				Set<Annotation> classAnnotations = iaa.getAnnotationsOfSameType(annotation, compareSetName);
				Set<Annotation> exactlyOverlappingAnnotations = iaa.getExactlyOverlappingAnnotations(annotation,
						compareSetName);
				Set<Annotation> overlappingAnnotations = iaa.getOverlappingAnnotations(annotation, compareSetName);

				Set<Annotation> classAndExactSpanAnnotations = new HashSet<>(classAnnotations);
				classAndExactSpanAnnotations.retainAll(exactlyOverlappingAnnotations);

				Set<Annotation> classAndOverlappingSpanAnnotations = new HashSet<>(classAnnotations);
				classAndOverlappingSpanAnnotations.retainAll(overlappingAnnotations);

				candidateAnnotations.addAll(classAndExactSpanAnnotations);
				candidateAnnotations.addAll(classAndOverlappingSpanAnnotations);
				candidateAnnotations.addAll(exactlyOverlappingAnnotations);
				candidateAnnotations.addAll(overlappingAnnotations);
			} else {
				// we want all annotations that are in the other set, but we
				// want them ordered in the following way:
				// 1) annotations with the same class and same spans
				// 2) annotations with the same class and overlapping spans
				// 3) annotations with the same spans
				// 4) annotations with overlapping spans
				// 5) annotations with the same class
				// 6) all other annotations
				Set<Annotation> classAnnotations = iaa.getAnnotationsOfSameType(annotation, compareSetName);
				Set<Annotation> exactlyOverlappingAnnotations = iaa.getExactlyOverlappingAnnotations(annotation,
						compareSetName);
				Set<Annotation> overlappingAnnotations = iaa.getOverlappingAnnotations(annotation, compareSetName);

				Set<Annotation> classAndExactSpanAnnotations = new HashSet<>(classAnnotations);
				classAndExactSpanAnnotations.retainAll(exactlyOverlappingAnnotations);

				Set<Annotation> classAndOverlappingSpanAnnotations = new HashSet<>(classAnnotations);
				classAndOverlappingSpanAnnotations.retainAll(overlappingAnnotations);

				candidateAnnotations.addAll(classAndExactSpanAnnotations);
				candidateAnnotations.addAll(classAndOverlappingSpanAnnotations);
				candidateAnnotations.addAll(exactlyOverlappingAnnotations);
				candidateAnnotations.addAll(overlappingAnnotations);
				candidateAnnotations.addAll(classAnnotations);
				candidateAnnotations.addAll(iaa.getAnnotationSets().get(compareSetName));
			}
		}

		candidateAnnotations.removeAll(excludeAnnotations);

		if (candidateAnnotations.size() == 0) {
			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
			return null;
		}

		// we are going to collect all matches because we want to return the
		// shortest of the matches if there is more than one.
		List<Annotation> nontrivialMatches = new ArrayList<>();
		List<Annotation> trivialMatches = new ArrayList<>();
		boolean nontrivialNonmatch = false;

		for (Annotation candidateAnnotation : candidateAnnotations) {
			int result;
			if (comparedSimpleFeatures.size() > 0)
				result = Annotation.compareSimpleFeatures(annotation, candidateAnnotation, comparedSimpleFeatures);
			else
				result = MatchResult.NONTRIVIAL_MATCH;

			if (result == MatchResult.TRIVIAL_NONMATCH)
				continue;

			for (String complexFeatureName : comparedComplexFeatures.keySet()) {
				ComplexFeatureMatchCriteria matchCriteria = comparedComplexFeatures.get(complexFeatureName);

				int complexResult = Annotation.compareComplexFeature(annotation, candidateAnnotation,
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
                    trivialMatches.add(candidateAnnotation);
                    break;
                case MatchResult.NONTRIVIAL_MATCH:
                    nontrivialMatches.add(candidateAnnotation);
                    break;
            }
		}

		if (nontrivialMatches.size() > 0) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			if (nontrivialMatches.size() == 1)
				return nontrivialMatches.iterator().next();
			else
				return Annotation.getShortestAnnotation(nontrivialMatches);
		}
		if (trivialMatches.size() > 0) {
			matchResult.setResult(MatchResult.TRIVIAL_MATCH);
			if (trivialMatches.size() == 1)
				return trivialMatches.iterator().next();
			else
				return Annotation.getShortestAnnotation(trivialMatches);
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
