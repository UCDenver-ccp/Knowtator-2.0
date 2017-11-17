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

package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class IAA {
	Set<String> annotationClasses;

	Set<String> setNames;

	Set<Annotation> annotations;

	Set<Annotation> emptyAnnotationSet;

	// key is an Annotation set, value is a map whose value is an Annotation
	// class
	// and values is the set of annotations in the set having that class.
	Map<String, Map<String, Set<Annotation>>> class2AnnotationsMap;

	// key is an Annotation set, value is a annotationSpanIndex for the
	// annotations in that set.
	Map<String, AnnotationSpanIndex> spanIndexes;

	// key is an Annotation set, value is a set of annotations that are
	// considered matches.
	Map<String, Set<Annotation>> allwayMatches;

	Map<String, Set<Annotation>> trivialAllwayMatches;

	Map<String, Set<Annotation>> nontrivialAllwayMatches;

	// key is an Annotation set, value is a set of annotations that are
	// considered non-matches.
	Map<String, Set<Annotation>> allwayNonmatches;

	Map<String, Set<Annotation>> trivialAllwayNonmatches;

	Map<String, Set<Annotation>> nontrivialAllwayNonmatches;

	// key is an Annotation, value is the set of n annotations that it was
	// matched with in n-way IAA.
	Map<Annotation, Set<Annotation>> allwayMatchSets;

	// key is an Annotation set that is considered gold standard by which other
	// Annotation sets are compared,
	// value is a map whose key is the Annotation set being compared to gold
	// standard and whose value are annotations (from the
	// gold standard set) that are matches.

	Map<String, Map<String, Set<Annotation>>> pairwiseMatches;

	Map<String, Map<String, Set<Annotation>>> trivialPairwiseMatches;

	Map<String, Map<String, Set<Annotation>>> nontrivialPairwiseMatches;

	Map<String, Map<String, Set<Annotation>>> pairwiseNonmatches;

	Map<String, Map<String, Set<Annotation>>> trivialPairwiseNonmatches;

	Map<String, Map<String, Set<Annotation>>> nontrivialPairwiseNonmatches;

	Map<Annotation, Set<Annotation>> pairwiseMatchPairs;
	private HashMap<String, Collection<Annotation>> annotationSets;

	public IAA(Set<String> setNames) {
		this.setNames = setNames;
		annotationClasses = new HashSet<>();

		annotationSets = new HashMap<>();

		emptyAnnotationSet = Collections.unmodifiableSet(new HashSet<Annotation>());

		Set<Annotation> emptySet = Collections.emptySet();
		setAnnotations(emptySet);
		reset();
	}

	public IAA(Set<String> setNames, Set<Annotation> annotations) {
		this.setNames = setNames;
		annotationClasses = new HashSet<>();
		setAnnotations(annotations);
		reset();
	}

	public void reset() {
		allwayMatches = new HashMap<>();
		trivialAllwayMatches = new HashMap<>();
		nontrivialAllwayMatches = new HashMap<>();
		allwayNonmatches = new HashMap<>();
		trivialAllwayNonmatches = new HashMap<>();
		nontrivialAllwayNonmatches = new HashMap<>();

		allwayMatchSets = new HashMap<>();

		pairwiseMatches = new HashMap<>();
		trivialPairwiseMatches = new HashMap<>();
		nontrivialPairwiseMatches = new HashMap<>();
		pairwiseNonmatches = new HashMap<>();
		trivialPairwiseNonmatches = new HashMap<>();
		nontrivialPairwiseNonmatches = new HashMap<>();

		pairwiseMatchPairs = new HashMap<>();

		for (String setName : setNames) {
			allwayMatches.put(setName, new HashSet<>());
			trivialAllwayMatches.put(setName, new HashSet<>());
			nontrivialAllwayMatches.put(setName, new HashSet<>());
			allwayNonmatches.put(setName, new HashSet<>());
			trivialAllwayNonmatches.put(setName, new HashSet<>());
			nontrivialAllwayNonmatches.put(setName, new HashSet<>());

			pairwiseMatches.put(setName, new HashMap<>());
			trivialPairwiseMatches.put(setName, new HashMap<>());
			nontrivialPairwiseMatches.put(setName, new HashMap<>());
			pairwiseNonmatches.put(setName, new HashMap<>());
			trivialPairwiseNonmatches.put(setName, new HashMap<>());
			nontrivialPairwiseNonmatches.put(setName, new HashMap<>());

			for (String compareSet : annotationSets.keySet()) {
				if (!setName.equals(compareSet)) {
					pairwiseMatches.get(setName).put(compareSet, new HashSet<>());
					trivialPairwiseMatches.get(setName).put(compareSet, new HashSet<>());
					nontrivialPairwiseMatches.get(setName).put(compareSet, new HashSet<>());
					pairwiseNonmatches.get(setName).put(compareSet, new HashSet<>());
					trivialPairwiseNonmatches.get(setName).put(compareSet, new HashSet<>());
					nontrivialPairwiseNonmatches.get(setName).put(compareSet, new HashSet<>());
				}
			}
		}

	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;

		for (String setName : setNames) {
			annotationSets.put(setName, new HashSet<>());
		}

		class2AnnotationsMap = new HashMap<>();
		spanIndexes = new HashMap<>();

		for (Annotation annotation : annotations) {
			String setName = annotation.getProfile().getName();
			String annotationClass = annotation.getClassName();
			if (annotationClass != null)
				annotationClasses.add(annotationClass);
			// throw exception here if there is a setName in the annotations
			// that was not passed in.
			annotationSets.get(setName).add(annotation);
		}

		for (String setName : setNames) {
			Collection<Annotation> setAnnotations = annotationSets.get(setName);

			spanIndexes.put(setName, new AnnotationSpanIndex(setAnnotations));

			Map<String, Set<Annotation>> classAnnotations = new HashMap<>();
			class2AnnotationsMap.put(setName, classAnnotations);

			for (Annotation setAnnotation : setAnnotations) {
				String annotationClass = setAnnotation.getClassName();
				if (!classAnnotations.containsKey(annotationClass)) {
					classAnnotations.put(annotationClass, new HashSet<>());
				}
				classAnnotations.get(annotationClass).add(setAnnotation);
			}
		}
	}

	public void allwayIAA(Matcher matcher) throws IAAException {
		/*
		 * At the moment an Annotation is found to be a match, there are n-1
		 * other annotations that are also found to be a match (an Annotation
		 * for each of the other annotators). We will gather all matches as we
		 * discover them so that a multiple annotations will not match with an
		 * Annotation that has already been matched. This might happen if, for
		 * example, one profile mistakenly created a duplicate Annotation. We
		 * would only want to consider one of them a match. All annotations that
		 * have been found to be a match will be put in
		 * matchedAnnotationsAllway.
		 */
		Set<Annotation> matchedAnnotations = new HashSet<>();
		for (Annotation annotation : annotations) {
			String setName = annotation.getProfile().getName();
			if (!matchedAnnotations.contains(annotation)) {
				MatchResult matchResult = new MatchResult();
				// just because an Annotation matches with another Annotation
				// from each
				// of the other sets, that does not mean the other annotations
				// match with
				// each other. This is particularly true for 'overlapping' Span
				// criteria.
				Set<Annotation> matches = match(annotation, matchedAnnotations, matcher, matchResult);
				if (matches != null) {
					allwayMatches.get(setName).add(annotation);
					Set<Annotation> allMatches = new HashSet<>(matches);
					allMatches.add(annotation);
					allwayMatchSets.put(annotation, allMatches);

					for (Annotation match : matches) {
						String matchedSet = match.getProfile().getName();
						allwayMatches.get(matchedSet).add(match);
						allwayMatchSets.put(match, allMatches);
					}
					if (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH) {
						nontrivialAllwayMatches.get(setName).add(annotation);
						for (Annotation match : matches) {
							String matchedSet = match.getProfile().getName();
							nontrivialAllwayMatches.get(matchedSet).add(match);
						}

					} else if (matchResult.getResult() == MatchResult.TRIVIAL_MATCH) {
						trivialAllwayMatches.get(setName).add(annotation);
						for (Annotation match : matches) {
							String matchedSet = match.getProfile().getName();
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
	@SuppressWarnings("unchecked")
	public void pairwiseIAA(Class matchClass) throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, IAAException {
		Constructor constructor = matchClass.getConstructor();
		Matcher matcher = (Matcher) constructor.newInstance();
		pairwiseIAA(matcher);
	}

	public void pairwiseIAA(Matcher matcher) throws IAAException {
		for (Annotation annotation : annotations) {
			String setName = annotation.getProfile().getName();
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
							pairwiseMatchPairs.put(annotation, new HashSet<>());
						if (!pairwiseMatchPairs.containsKey(match))
							pairwiseMatchPairs.put(match, new HashSet<>());
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
		String setName = annotation.getProfile().getName();
		Set<Annotation> matchedAnnotations = new HashSet<>();

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
		String annotationClass = annotation.getClassName();
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

	public Map<String, Collection<Annotation>> getAnnotationSets() {
		return annotationSets;
	}

	public Set<String> getSetNames() {
		return setNames;
	}

	public Set<String> getAnnotationClasses() {
		return annotationClasses;
	}

	public Map<String, Set<Annotation>> getTrivialAllwayMatches() {
		return trivialAllwayMatches;
	}

	public Map<String, Set<Annotation>> getTrivialAllwayNonmatches() {
		return trivialAllwayNonmatches;
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

}
