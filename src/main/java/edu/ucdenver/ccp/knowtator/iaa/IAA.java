package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.model.Annotation;
import edu.ucdenver.ccp.knowtator.model.Span;

import java.util.*;

public class IAA {
	private Set<String> annotationClasses;

	private Set<String> setNames;

	private Set<Annotation> annotations;

	private Set<Annotation> emptyAnnotationSet;

	// key is an annotation set, value is a map whose value is an annotation
	// class
	// and values is the set of annotations in the set having that class.
	private Map<String, Map<String, Set<Annotation>>> class2AnnotationsMap;

	// key is an annotation set, value is a annotationSpanIndex for the
	// annotations in that set.
	private Map<String, AnnotationSpanIndex> spanIndexes;

	// key is an annotation set, value is a set of annotations that are
	// considered matches.
	private Map<String, Set<Annotation>> allwayMatches;

	private Map<String, Set<Annotation>> trivialAllwayMatches;

	private Map<String, Set<Annotation>> nontrivialAllwayMatches;

	// key is an annotation set, value is a set of annotations that are
	// considered non-matches.
	private Map<String, Set<Annotation>> allwayNonmatches;

	private Map<String, Set<Annotation>> trivialAllwayNonmatches;

	private Map<String, Set<Annotation>> nontrivialAllwayNonmatches;

	// key is an annotation, value is the set of n annotations that it was
	// matched with in n-way IAA.
	private Map<Annotation, Set<Annotation>> allwayMatchSets;

	// key is an annotation set that is considered gold standard by which other
	// annotation sets are compared,
	// value is a map whose key is the annotation set being compared to gold
	// standard and whose value are annotations (from the
	// gold standard set) that are matches.

	private Map<String, Map<String, Set<Annotation>>> pairwiseMatches;

	private Map<String, Map<String, Set<Annotation>>> trivialPairwiseMatches;

	private Map<String, Map<String, Set<Annotation>>> nontrivialPairwiseMatches;

	private Map<String, Map<String, Set<Annotation>>> pairwiseNonmatches;

	private Map<String, Map<String, Set<Annotation>>> trivialPairwiseNonmatches;

	private Map<String, Map<String, Set<Annotation>>> nontrivialPairwiseNonmatches;

	private Map<Annotation, Set<Annotation>> pairwiseMatchPairs;
	private HashMap<String, Collection<Annotation>> annotationSets;

	public IAA(Set<String> setNames) {
		this.setNames = setNames;
		annotationClasses = new HashSet<>();

		annotationSets = new HashMap<>();

		emptyAnnotationSet = Collections.unmodifiableSet(new HashSet<>());

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

	/**
	 * @param annotation1
	 * @param annotation2
	 * @return true if the annotations have the same spans
	 */
	@SuppressWarnings("JavaDoc")
	static boolean spansMatch(Annotation annotation1, Annotation annotation2) {
		return Span.spansMatch(
				annotation1.getSpanCollection().getData(), annotation2.getSpanCollection().getData());
	}

	@SuppressWarnings("unused")
	public static boolean spansMatch(List<Annotation> annotations) {
		for (int i = 1; i < annotations.size(); i++) {
			if (!spansMatch(annotations.get(0), annotations.get(i))) return false;
		}
		return true;
	}

	static boolean spansOverlap(Annotation annotation1, Annotation annotation2) {
		return Span.intersects(
				annotation1.getSpanCollection().getData(), annotation2.getSpanCollection().getData());
	}

	//	/**
	//	 * This method performs pairwise IAA for each combination of annotators.
	//	 *
	//	 */
	//	public void pairwiseIAA(Class matchClass) throws NoSuchMethodException, InstantiationException,
	//			IllegalAccessException, InvocationTargetException, IAAException {
	//		Constructor constructor = matchClass.getConstructor();
	//		Matcher matcher = (Matcher) constructor.newInstance();
	//		pairwiseIAA(matcher);
	//	}

	/**
	 * Returns the text covered by an annotation.
	 *
	 * @param annotation     an annotation that has spans corresponding to extents of annotationText
	 * @param annotationText the text from which an annotation corresponds to.
	 * @param spanSeparator  if more than one Span exists, then this String will be inserted between
	 *                       each segment of text.
	 * @return the text covered by an annotation.
	 */
	public static String getCoveredText(
			Annotation annotation, String annotationText, String spanSeparator) {
		TreeSet<Span> spans = annotation.getSpanCollection().getData();
		if (spans == null || spans.size() == 0) return "";
		else if (spans.size() == 1) {
			return Span.substring(annotationText, spans.first());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(Span.substring(annotationText, spans.first()));
			for (int i = 1; i < spans.size(); i++) {
				sb.append(spanSeparator);
				sb.append(Span.substring(annotationText, spans.first()));
			}
			return sb.toString();
		}
	}

	/**
	 * This method returns the shortest annotation - that is the annotation whose Span is the
	 * shortest. If an annotation has more than one Span, then its size is the sum of the size of each
	 * of its spans.
	 *
	 * @param annotations
	 * @return will only return one annotation. In the case of a tie, will return the first annotation
	 * with the smallest size encountered during iteration. Returns null if annotations is null or
	 * empty.
	 */
	@SuppressWarnings("JavaDoc")
	public static Annotation getShortestAnnotation(Collection<Annotation> annotations) {
		if (annotations == null || annotations.size() == 0) return null;

		Annotation shortestAnnotation = null;
		int shortestAnnotationLength = -1;

		for (Annotation annotation : annotations) {
			int annotationSize = annotation.getSize();
			if (shortestAnnotationLength == -1 || annotationSize < shortestAnnotationLength) {
				shortestAnnotation = annotation;
				shortestAnnotationLength = annotationSize;
			}
		}
		return shortestAnnotation;
	}

	private void reset() {
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

	//	public Set<annotation> getAnnotationsOfClass(String className, String compareSetName) {
	//		if (class2AnnotationsMap.containsKey(compareSetName)
	//				&& class2AnnotationsMap.get(compareSetName).containsKey(className)) {
	//			return class2AnnotationsMap.get(compareSetName).get(className);
	//		} else
	//			return Collections.emptySet();
	//	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;

		for (String setName : setNames) {
			annotationSets.put(setName, new HashSet<>());
		}

		class2AnnotationsMap = new HashMap<>();
		spanIndexes = new HashMap<>();

		for (Annotation annotation : annotations) {
			String setName = annotation.getAnnotator().getId();
			String annotationClass = annotation.getOwlClass().toString();
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
				String annotationClass = setAnnotation.getOwlClass().toString();
				if (!classAnnotations.containsKey(annotationClass)) {
					classAnnotations.put(annotationClass, new HashSet<>());
				}
				classAnnotations.get(annotationClass).add(setAnnotation);
			}
		}
	}

	void allwayIAA(Matcher matcher) throws IAAException {
		/*
		 * At the moment an annotation is found to be a match, there are n-1
		 * other annotations that are also found to be a match (an annotation
		 * for each of the other annotators). We will gather all matches as we
		 * discover them so that a multiple annotations will not match with an
		 * annotation that has already been matched. This might happen if, for
		 * example, one profile mistakenly created a duplicate annotation. We
		 * would only want to consider one of them a match. All annotations that
		 * have been found to be a match will be put in
		 * matchedAnnotationsAllway.
		 */
		Set<Annotation> matchedAnnotations = new HashSet<>();
		for (Annotation annotation : annotations) {
			String setName = annotation.getAnnotator().getId();
			if (!matchedAnnotations.contains(annotation)) {
				MatchResult matchResult = new MatchResult();
				// just because an annotation matches with another annotation
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
						String matchedSet = match.getAnnotator().getId();
						allwayMatches.get(matchedSet).add(match);
						allwayMatchSets.put(match, allMatches);
					}
					if (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH) {
						nontrivialAllwayMatches.get(setName).add(annotation);
						for (Annotation match : matches) {
							String matchedSet = match.getAnnotator().getId();
							nontrivialAllwayMatches.get(matchedSet).add(match);
						}

					} else if (matchResult.getResult() == MatchResult.TRIVIAL_MATCH) {
						trivialAllwayMatches.get(setName).add(annotation);
						for (Annotation match : matches) {
							String matchedSet = match.getAnnotator().getId();
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

	void pairwiseIAA(Matcher matcher) throws IAAException {
		for (Annotation annotation : annotations) {
			String setName = annotation.getAnnotator().getId();
			for (String compareSetName : annotationSets.keySet()) {
				if (!setName.equals(compareSetName)) {
					Set<Annotation> matchedAnnotations = pairwiseMatches.get(setName).get(compareSetName);
					if (matchedAnnotations.contains(annotation)) continue;

					Set<Annotation> excludeAnnotations = pairwiseMatches.get(compareSetName).get(setName);
					MatchResult matchResult = new MatchResult();
					Annotation match =
							matcher.match(annotation, compareSetName, excludeAnnotations, this, matchResult);
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

	private Set<Annotation> match(
			Annotation annotation,
			Set<Annotation> excludeAnnotations,
			Matcher matcher,
			MatchResult matchResult) {
		String setName = annotation.getAnnotator().getId();
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
				Annotation match =
						matcher.match(annotation, compareSetName, excludeAnnotations, this, result);
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
			if (trivialMatch) matchResult.setResult(MatchResult.TRIVIAL_MATCH);
			else matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return matchedAnnotations;
		} else {
			if (nontrivialNonmatch) matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			else matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
			return null;
		}
	}

	public Set<Annotation> getAnnotationsOfSameType(Annotation annotation, String compareSetName) {
		String annotationClass = annotation.getOwlClass().toString();
		return safeReturn(class2AnnotationsMap.get(compareSetName).get(annotationClass));
	}

	public Set<Annotation> getOverlappingAnnotations(Annotation annotation, String compareSetName) {
		AnnotationSpanIndex spanIndex = spanIndexes.get(compareSetName);
		return safeReturn(spanIndex.getOverlappingAnnotations(annotation));
	}

	public Set<Annotation> getExactlyOverlappingAnnotations(
			Annotation annotation, String compareSetName) {
		AnnotationSpanIndex spanIndex = spanIndexes.get(compareSetName);
		return safeReturn(spanIndex.getExactlyOverlappingAnnotations(annotation));
	}

	//	public Map<String, Collection<annotation>> getAnnotationSets() {
	//		return annotationSets;
	//	}

	public Set<String> getSetNames() {
		return setNames;
	}

	public Set<String> getAnnotationClasses() {
		return annotationClasses;
	}

	private Set<Annotation> safeReturn(Set<Annotation> returnValues) {
		if (returnValues == null) return emptyAnnotationSet;
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

	//	public Map<String, Map<String, Set<annotation>>> getNontrivialPairwiseMatches() {
	//		return nontrivialPairwiseMatches;
	//	}
	//
	//	public Map<String, Map<String, Set<annotation>>> getNontrivialPairwiseNonmatches() {
	//		return nontrivialPairwiseNonmatches;
	//	}

	public Map<String, Map<String, Set<Annotation>>> getPairwiseNonmatches() {
		return pairwiseNonmatches;
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

	public Map<Annotation, Set<Annotation>> getAllwayMatchSets() {
		return allwayMatchSets;
	}
}
