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
 * Copyright (C) 2005-2008.  All Rights Reserved.
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

package edu.uchsc.ccp.iaa;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uchsc.ccp.iaa.matcher.MatchResult;
import edu.uchsc.ccp.iaa.matcher.Matcher;

public class IAA {
	Set<String> annotationClasses;

	Set<String> setNames;

	Set<Annotation> annotations;

	Set<Annotation> emptyAnnotationSet;

	// key is the name of an annotations set, value is a set of annotations
	Map<String, Set<Annotation>> annotationSets;

	// key is an annotation set, value is a map whose value is an annotation
	// class
	// and values is the set of annotations in the set having that class.
	Map<String, Map<String, Set<Annotation>>> class2AnnotationsMap;

	// key is an annotation set, value is a annotationSpanIndex for the
	// annotations in that set.
	Map<String, AnnotationSpanIndex> spanIndexes;

	// key is an annotation set, value is a set of annotations that are
	// considered matches.
	Map<String, Set<Annotation>> allwayMatches;

	Map<String, Set<Annotation>> trivialAllwayMatches;

	Map<String, Set<Annotation>> nontrivialAllwayMatches;

	// key is an annotation set, value is a set of annotations that are
	// considered non-matches.
	Map<String, Set<Annotation>> allwayNonmatches;

	Map<String, Set<Annotation>> trivialAllwayNonmatches;

	Map<String, Set<Annotation>> nontrivialAllwayNonmatches;

	// key is an annotation, value is the set of n annotations that it was
	// matched with in n-way IAA.
	Map<Annotation, Set<Annotation>> allwayMatchSets;

	// key is an annotation set that is considered gold standard by which other
	// annotation sets are compared,
	// value is a map whose key is the annotation set being compared to gold
	// standard and whose value are annotations (from the
	// gold standard set) that are matches.

	Map<String, Map<String, Set<Annotation>>> pairwiseMatches;

	Map<String, Map<String, Set<Annotation>>> trivialPairwiseMatches;

	Map<String, Map<String, Set<Annotation>>> nontrivialPairwiseMatches;

	Map<String, Map<String, Set<Annotation>>> pairwiseNonmatches;

	Map<String, Map<String, Set<Annotation>>> trivialPairwiseNonmatches;

	Map<String, Map<String, Set<Annotation>>> nontrivialPairwiseNonmatches;

	Map<Annotation, Set<Annotation>> pairwiseMatchPairs;

	Map<String, Object> matcherInfo;

	public IAA(Set<String> setNames) {
		this.setNames = setNames;
		annotationClasses = new HashSet<String>();

		emptyAnnotationSet = Collections.unmodifiableSet(new HashSet<Annotation>());

		Set<Annotation> emptySet = Collections.emptySet();
		setAnnotations(emptySet);
		reset();
	}

	public IAA(Set<String> setNames, Set<Annotation> annotations) {
		this.setNames = setNames;
		annotationClasses = new HashSet<String>();
		setAnnotations(annotations);
		reset();
	}

	public void reset() {
		allwayMatches = new HashMap<String, Set<Annotation>>();
		trivialAllwayMatches = new HashMap<String, Set<Annotation>>();
		nontrivialAllwayMatches = new HashMap<String, Set<Annotation>>();
		allwayNonmatches = new HashMap<String, Set<Annotation>>();
		trivialAllwayNonmatches = new HashMap<String, Set<Annotation>>();
		nontrivialAllwayNonmatches = new HashMap<String, Set<Annotation>>();

		allwayMatchSets = new HashMap<Annotation, Set<Annotation>>();

		pairwiseMatches = new HashMap<String, Map<String, Set<Annotation>>>();
		trivialPairwiseMatches = new HashMap<String, Map<String, Set<Annotation>>>();
		nontrivialPairwiseMatches = new HashMap<String, Map<String, Set<Annotation>>>();
		pairwiseNonmatches = new HashMap<String, Map<String, Set<Annotation>>>();
		trivialPairwiseNonmatches = new HashMap<String, Map<String, Set<Annotation>>>();
		nontrivialPairwiseNonmatches = new HashMap<String, Map<String, Set<Annotation>>>();

		pairwiseMatchPairs = new HashMap<Annotation, Set<Annotation>>();

		for (String setName : setNames) {
			allwayMatches.put(setName, new HashSet<Annotation>());
			trivialAllwayMatches.put(setName, new HashSet<Annotation>());
			nontrivialAllwayMatches.put(setName, new HashSet<Annotation>());
			allwayNonmatches.put(setName, new HashSet<Annotation>());
			trivialAllwayNonmatches.put(setName, new HashSet<Annotation>());
			nontrivialAllwayNonmatches.put(setName, new HashSet<Annotation>());

			pairwiseMatches.put(setName, new HashMap<String, Set<Annotation>>());
			trivialPairwiseMatches.put(setName, new HashMap<String, Set<Annotation>>());
			nontrivialPairwiseMatches.put(setName, new HashMap<String, Set<Annotation>>());
			pairwiseNonmatches.put(setName, new HashMap<String, Set<Annotation>>());
			trivialPairwiseNonmatches.put(setName, new HashMap<String, Set<Annotation>>());
			nontrivialPairwiseNonmatches.put(setName, new HashMap<String, Set<Annotation>>());

			for (String compareSet : annotationSets.keySet()) {
				if (!setName.equals(compareSet)) {
					pairwiseMatches.get(setName).put(compareSet, new HashSet<Annotation>());
					trivialPairwiseMatches.get(setName).put(compareSet, new HashSet<Annotation>());
					nontrivialPairwiseMatches.get(setName).put(compareSet, new HashSet<Annotation>());
					pairwiseNonmatches.get(setName).put(compareSet, new HashSet<Annotation>());
					trivialPairwiseNonmatches.get(setName).put(compareSet, new HashSet<Annotation>());
					nontrivialPairwiseNonmatches.get(setName).put(compareSet, new HashSet<Annotation>());
				}
			}
		}

	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
		annotationSets = new HashMap<String, Set<Annotation>>();
		for (String setName : setNames) {
			annotationSets.put(setName, new HashSet<Annotation>());
		}

		class2AnnotationsMap = new HashMap<String, Map<String, Set<Annotation>>>();
		spanIndexes = new HashMap<String, AnnotationSpanIndex>();

		for (Annotation annotation : annotations) {
			String setName = annotation.getSetName();
			String annotationClass = annotation.getAnnotationClass();
			if (annotationClass != null)
				annotationClasses.add(annotationClass);
			// throw exception here if there is a setName in the annotations
			// that was not passed in.
			annotationSets.get(setName).add(annotation);
		}

		for (String setName : setNames) {
			Set<Annotation> setAnnotations = annotationSets.get(setName);

			spanIndexes.put(setName, new AnnotationSpanIndex(setAnnotations));

			Map<String, Set<Annotation>> classAnnotations = new HashMap<String, Set<Annotation>>();
			class2AnnotationsMap.put(setName, classAnnotations);

			for (Annotation setAnnotation : setAnnotations) {
				String annotationClass = setAnnotation.getAnnotationClass();
				if (!classAnnotations.containsKey(annotationClass)) {
					classAnnotations.put(annotationClass, new HashSet<Annotation>());
				}
				classAnnotations.get(annotationClass).add(setAnnotation);
			}
		}
	}

	public void allwayIAA(Class matcherClass) throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, IAAException {
		Constructor constructor = matcherClass.getConstructor();
		Matcher matcher = (Matcher) constructor.newInstance();
		allwayIAA(matcher);
	}

	public void allwayIAA(Matcher matcher) throws IAAException {
		/*
		 * At the moment an annotation is found to be a match, there are n-1
		 * other annotations that are also found to be a match (an annotation
		 * for each of the other annotators). We will gather all matches as we
		 * discover them so that a multiple annotations will not match with an
		 * annotation that has already been matched. This might happen if, for
		 * example, one annotator mistakenly created a duplicate annotation. We
		 * would only want to consider one of them a match. All annotations that
		 * have been found to be a match will be put in
		 * matchedAnnotationsAllway.
		 */
		Set<Annotation> matchedAnnotations = new HashSet<Annotation>();
		for (Annotation annotation : annotations) {
			String setName = annotation.getSetName();
			if (!matchedAnnotations.contains(annotation)) {
				MatchResult matchResult = new MatchResult();
				// just because an annotation matches with another annotation
				// from each
				// of the other sets, that does not mean the other annotations
				// match with
				// each other. This is particularly true for 'overlapping' span
				// criteria.
				Set<Annotation> matches = match(annotation, matchedAnnotations, matcher, matchResult);
				if (matches != null) {
					allwayMatches.get(setName).add(annotation);
					Set<Annotation> allMatches = new HashSet<Annotation>(matches);
					allMatches.add(annotation);
					allwayMatchSets.put(annotation, allMatches);

					for (Annotation match : matches) {
						String matchedSet = match.getSetName();
						allwayMatches.get(matchedSet).add(match);
						allwayMatchSets.put(match, allMatches);
					}
					if (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH) {
						nontrivialAllwayMatches.get(setName).add(annotation);
						for (Annotation match : matches) {
							String matchedSet = match.getSetName();
							nontrivialAllwayMatches.get(matchedSet).add(match);
						}

					} else if (matchResult.getResult() == MatchResult.TRIVIAL_MATCH) {
						trivialAllwayMatches.get(setName).add(annotation);
						for (Annotation match : matches) {
							String matchedSet = match.getSetName();
							trivialAllwayMatches.get(matchedSet).add(match);
						}
					} else {
						// needs to either be an error - or we need a lot more
						// descriptive information that a user can report back
						// to me.
						throw new IAAException(
								"Match algorithm resulted in a NONTRIVIAL_MATCH or TRIVIAL_MATCH, but it also returned null.");
					}

					matchedAnnotations.add(annotation);
					matchedAnnotations.addAll(matches);
				} else {
					allwayNonmatches.get(setName).add(annotation);
					if (matchResult.getResult() == MatchResult.NONTRIVIAL_NONMATCH)
						nontrivialAllwayNonmatches.get(setName).add(annotation);
					else if (matchResult.getResult() == MatchResult.TRIVIAL_NONMATCH)
						trivialAllwayNonmatches.get(setName).add(annotation);
					else {
						throw new IAAException(
								"Match algorithm resulted in a NONTRIVIAL_NONMATCH or TRIVIAL_NONMATCH, but the match algorithm did not return null.");
					}
				}
			}
		}
	}

	/**
	 * This method performs pairwise IAA for each combination of annotators.
	 * 
	 */
	public void pairwiseIAA(Class matchClass) throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, IAAException {
		Constructor constructor = matchClass.getConstructor();
		Matcher matcher = (Matcher) constructor.newInstance();
		pairwiseIAA(matcher);
	}

	public void pairwiseIAA(Matcher matcher) throws IAAException {
		for (Annotation annotation : annotations) {
			String setName = annotation.getSetName();
			for (String compareSetName : annotationSets.keySet()) {
				if (!setName.equals(compareSetName)) {
					Set<Annotation> matchedAnnotations = pairwiseMatches.get(setName).get(compareSetName);
					if (matchedAnnotations.contains(annotation))
						continue;

					Set<Annotation> excludeAnnotations = pairwiseMatches.get(compareSetName).get(setName);
					MatchResult matchResult = new MatchResult();
					Annotation match = matcher.match(annotation, compareSetName, excludeAnnotations, this, matchResult);
					if (match != null) {
						pairwiseMatches.get(setName).get(compareSetName).add(annotation);
						pairwiseMatches.get(compareSetName).get(setName).add(match);

						if (!pairwiseMatchPairs.containsKey(annotation))
							pairwiseMatchPairs.put(annotation, new HashSet<Annotation>());
						if (!pairwiseMatchPairs.containsKey(match))
							pairwiseMatchPairs.put(match, new HashSet<Annotation>());
						pairwiseMatchPairs.get(annotation).add(match);
						pairwiseMatchPairs.get(match).add(annotation);

						if (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH) {
							nontrivialPairwiseMatches.get(setName).get(compareSetName).add(annotation);
							nontrivialPairwiseMatches.get(compareSetName).get(setName).add(match);
						} else if (matchResult.getResult() == MatchResult.TRIVIAL_MATCH) {
							trivialPairwiseMatches.get(setName).get(compareSetName).add(annotation);
							trivialPairwiseMatches.get(compareSetName).get(setName).add(match);
						} else {
							throw new IAAException(
									"match algorithm did not return null but the match result was not NONTRIVIAL_MATCH or TRIVIAL_MATCH");
						}
					} else {
						pairwiseNonmatches.get(setName).get(compareSetName).add(annotation);
						if (matchResult.getResult() == MatchResult.NONTRIVIAL_NONMATCH)
							nontrivialPairwiseNonmatches.get(setName).get(compareSetName).add(annotation);
						else if (matchResult.getResult() == MatchResult.TRIVIAL_NONMATCH)
							trivialPairwiseNonmatches.get(setName).get(compareSetName).add(annotation);
						else {
							throw new IAAException(
									"match algorithm returned null be the match result was not NONTRIVIAL_NONMATCH or TRIVIAL_NONMATCH");
						}
					}
				}
			}
		}
	}

	public Set<Annotation> match(Annotation annotation, Set<Annotation> excludeAnnotations, Matcher matcher,
			MatchResult matchResult) {
		String setName = annotation.getSetName();
		Set<Annotation> matchedAnnotations = new HashSet<Annotation>();

		// trivial matches trump non-trivial matches. If there is a single
		// trivial match, then trivial_match is the match result.
		boolean trivialMatch = false;
		// nontrivial nonmatches trump trivial nonmatches. If there is a single
		// nontrivial match, then nontrivial_nonmatch is the match result.
		boolean nontrivialNonmatch = false;

		for (String compareSetName : annotationSets.keySet()) {
			if (!setName.equals(compareSetName)) {
				MatchResult result = new MatchResult();
				Annotation match = matcher.match(annotation, compareSetName, excludeAnnotations, this, result);
				if (match != null) {
					matchedAnnotations.add(match);
					if (result.getResult() == MatchResult.TRIVIAL_MATCH) {
						trivialMatch = true;
					}
				} else if (result.getResult() == MatchResult.NONTRIVIAL_NONMATCH) {
					nontrivialNonmatch = true;
				}
			}
		}
		if (matchedAnnotations.size() == annotationSets.keySet().size() - 1) {
			if (trivialMatch)
				matchResult.setResult(MatchResult.TRIVIAL_MATCH);
			else
				matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return matchedAnnotations;
		} else {
			if (nontrivialNonmatch)
				matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			else
				matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
			return null;
		}
	}

	public Set<Annotation> getAnnotationsOfSameType(Annotation annotation, String compareSetName) {
		String annotationClass = annotation.getAnnotationClass();
		return safeReturn(class2AnnotationsMap.get(compareSetName).get(annotationClass));
	}

	public Set<Annotation> getAnnotationsOfClass(String className, String compareSetName) {
		if (class2AnnotationsMap.containsKey(compareSetName)
				&& class2AnnotationsMap.get(compareSetName).containsKey(className)) {
			return class2AnnotationsMap.get(compareSetName).get(className);
		} else
			return Collections.emptySet();
	}

	public Set<Annotation> getOverlappingAnnotations(Annotation annotation, String compareSetName) {
		AnnotationSpanIndex spanIndex = spanIndexes.get(compareSetName);
		return safeReturn(spanIndex.getOverlappingAnnotations(annotation));
	}

	public Set<Annotation> getExactlyOverlappingAnnotations(Annotation annotation, String compareSetName) {
		AnnotationSpanIndex spanIndex = spanIndexes.get(compareSetName);
		return safeReturn(spanIndex.getExactlyOverlappingAnnotations(annotation));
	}

	private Set<Annotation> safeReturn(Set<Annotation> returnValues) {
		if (returnValues == null)
			return emptyAnnotationSet;
		return returnValues;
		// return Collections.unmodifiableSet(returnValues);
	}

	public Map<String, Set<Annotation>> getAllwayMatches() {
		return allwayMatches;
	}

	public Map<String, Set<Annotation>> getAllwayNonmatches() {
		return allwayNonmatches;
	}

	public Map<String, Map<String, Set<Annotation>>> getPairwiseMatches() {
		return pairwiseMatches;
	}

	public Map<String, Map<String, Set<Annotation>>> getPairwiseNonmatches() {
		return pairwiseNonmatches;
	}

	public Map<String, Set<Annotation>> getAnnotationSets() {
		return annotationSets;
	}

	public Set<String> getSetNames() {
		return setNames;
	}

	public Set<String> getAnnotationClasses() {
		return annotationClasses;
	}

	public void setMatcherInfo(String infoLabel, Object infoObject) {
		matcherInfo.put(infoLabel, infoObject);
	}

	public Object getMatcherInfo(String infoLabel) {
		return matcherInfo.get(infoLabel);
	}

	public Map<String, Set<Annotation>> getTrivialAllwayMatches() {
		return trivialAllwayMatches;
	}

	public Map<String, Set<Annotation>> getTrivialAllwayNonmatches() {
		return trivialAllwayNonmatches;
	}

	public Map<String, Map<String, Set<Annotation>>> getTrivialPairwiseMatches() {
		return trivialPairwiseMatches;
	}

	public Map<String, Map<String, Set<Annotation>>> getTrivialPairwiseNonmatches() {
		return trivialPairwiseNonmatches;
	}

	public Map<String, Set<Annotation>> getNontrivialAllwayMatches() {
		return nontrivialAllwayMatches;
	}

	public Map<String, Set<Annotation>> getNontrivialAllwayNonmatches() {
		return nontrivialAllwayNonmatches;
	}

	public Map<String, Map<String, Set<Annotation>>> getNontrivialPairwiseMatches() {
		return nontrivialPairwiseMatches;
	}

	public Map<String, Map<String, Set<Annotation>>> getNontrivialPairwiseNonmatches() {
		return nontrivialPairwiseNonmatches;
	}

	public Map<Annotation, Set<Annotation>> getAllwayMatchSets() {
		return allwayMatchSets;
	}

	public Map<Annotation, Set<Annotation>> getPairwiseMatchPairs() {
		return pairwiseMatchPairs;
	}

}
