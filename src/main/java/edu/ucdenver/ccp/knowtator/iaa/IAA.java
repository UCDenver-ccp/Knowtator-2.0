package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;

import java.util.*;

public class IAA {
	private final Set<String> annotationClasses;

	private final Set<String> setNames;

	private Set<ConceptAnnotation> conceptAnnotations;

	private Set<ConceptAnnotation> emptyConceptAnnotationSet;

	// key is an concept set, value is a map whose value is an concept
	// class
	// and values is the set of conceptAnnotations in the set having that class.
	private Map<String, Map<String, Set<ConceptAnnotation>>> class2AnnotationsMap;

	// key is an concept set, value is a annotationSpanIndex for the
	// conceptAnnotations in that set.
	private Map<String, AnnotationSpanIndex> spanIndexes;

	// key is an concept set, value is a set of conceptAnnotations that are
	// considered matches.
	private Map<String, Set<ConceptAnnotation>> allwayMatches;

	private Map<String, Set<ConceptAnnotation>> trivialAllwayMatches;

	private Map<String, Set<ConceptAnnotation>> nontrivialAllwayMatches;

	// key is an concept set, value is a set of conceptAnnotations that are
	// considered non-matches.
	private Map<String, Set<ConceptAnnotation>> allwayNonmatches;

	private Map<String, Set<ConceptAnnotation>> trivialAllwayNonmatches;

	private Map<String, Set<ConceptAnnotation>> nontrivialAllwayNonmatches;

	// key is an concept, value is the set of n conceptAnnotations that it was
	// matched with in n-way IAA.
	private Map<ConceptAnnotation, Set<ConceptAnnotation>> allwayMatchSets;

	// key is an concept set that is considered gold standard by which other
	// concept sets are compared,
	// value is a map whose key is the concept set being compared to gold
	// standard and whose value are conceptAnnotations (from the
	// gold standard set) that are matches.

	private Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseMatches;

	private Map<String, Map<String, Set<ConceptAnnotation>>> trivialPairwiseMatches;

	private Map<String, Map<String, Set<ConceptAnnotation>>> nontrivialPairwiseMatches;

	private Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseNonmatches;

	private Map<String, Map<String, Set<ConceptAnnotation>>> trivialPairwiseNonmatches;

	private Map<String, Map<String, Set<ConceptAnnotation>>> nontrivialPairwiseNonmatches;

	private Map<ConceptAnnotation, Set<ConceptAnnotation>> pairwiseMatchPairs;
	private HashMap<String, Collection<ConceptAnnotation>> annotationSets;

	public IAA(Set<String> setNames) {
		this.setNames = setNames;
		annotationClasses = new HashSet<>();

		annotationSets = new HashMap<>();

		emptyConceptAnnotationSet = Collections.unmodifiableSet(new HashSet<>());

		Set<ConceptAnnotation> emptySet = Collections.emptySet();
		setConceptAnnotations(emptySet);
		reset();
	}

	public IAA(Set<String> setNames, Set<ConceptAnnotation> conceptAnnotations) {
		this.setNames = setNames;
		annotationClasses = new HashSet<>();
		setConceptAnnotations(conceptAnnotations);
		reset();
	}

	/**
	 * @param conceptAnnotation1
	 * @param conceptAnnotation2
	 * @return true if the conceptAnnotations have the same spans
	 */
	@SuppressWarnings("JavaDoc")
	static boolean spansMatch(ConceptAnnotation conceptAnnotation1, ConceptAnnotation conceptAnnotation2) {
		return Span.spansMatch(
				conceptAnnotation1.getSpanCollection().getCollection(), conceptAnnotation2.getSpanCollection().getCollection());
	}

	@SuppressWarnings("unused")
	public static boolean spansMatch(List<ConceptAnnotation> conceptAnnotations) {
		for (int i = 1; i < conceptAnnotations.size(); i++) {
			if (!spansMatch(conceptAnnotations.get(0), conceptAnnotations.get(i))) return false;
		}
		return true;
	}

	static boolean spansOverlap(ConceptAnnotation conceptAnnotation1, ConceptAnnotation conceptAnnotation2) {
		return Span.intersects(
				conceptAnnotation1.getSpanCollection().getCollection(), conceptAnnotation2.getSpanCollection().getCollection());
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
	 * Returns the text covered by an concept.
	 *
	 * @param conceptAnnotation     an concept that has spans corresponding to extents of annotationText
	 * @param annotationText the text from which an concept corresponds to.
	 * @param spanSeparator  if more than one Span exists, then this String will be inserted between
	 *                       each segment of text.
	 * @return the text covered by an concept.
	 */
	public static String getCoveredText(
			ConceptAnnotation conceptAnnotation, String annotationText, String spanSeparator) {
		TreeSet<Span> spans = conceptAnnotation.getSpanCollection().getCollection();
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
	 * This method returns the shortest concept - that is the concept whose Span is the
	 * shortest. If an concept has more than one Span, then its size is the sum of the size of each
	 * of its spans.
	 *
	 * @param conceptAnnotations
	 * @return will only return one concept. In the case of a tie, will return the first concept
	 * with the smallest size encountered during iteration. Returns null if conceptAnnotations is null or
	 * empty.
	 */
	@SuppressWarnings("JavaDoc")
	public static ConceptAnnotation getShortestAnnotation(Collection<ConceptAnnotation> conceptAnnotations) {
		if (conceptAnnotations == null || conceptAnnotations.size() == 0) return null;

		ConceptAnnotation shortestConceptAnnotation = null;
		int shortestAnnotationLength = -1;

		for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
			int annotationSize = conceptAnnotation.getSize();
			if (shortestAnnotationLength == -1 || annotationSize < shortestAnnotationLength) {
				shortestConceptAnnotation = conceptAnnotation;
				shortestAnnotationLength = annotationSize;
			}
		}
		return shortestConceptAnnotation;
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

	//	public Set<concept> getAnnotationsOfClass(String className, String compareSetName) {
	//		if (class2AnnotationsMap.containsKey(compareSetName)
	//				&& class2AnnotationsMap.get(compareSetName).containsKey(className)) {
	//			return class2AnnotationsMap.get(compareSetName).get(className);
	//		} else
	//			return Collections.emptySet();
	//	}

	void setConceptAnnotations(Set<ConceptAnnotation> conceptAnnotations) {
		this.conceptAnnotations = conceptAnnotations;

		for (String setName : setNames) {
			annotationSets.put(setName, new HashSet<>());
		}

		class2AnnotationsMap = new HashMap<>();
		spanIndexes = new HashMap<>();

		for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
			String setName = conceptAnnotation.getAnnotator().getId();
			String annotationClass = conceptAnnotation.getOwlClass().toString();
			annotationClasses.add(annotationClass);
			// throw exception here if there is a setName in the conceptAnnotations
			// that was not passed in.
			annotationSets.get(setName).add(conceptAnnotation);
		}

		for (String setName : setNames) {
			Collection<ConceptAnnotation> setConceptAnnotations = annotationSets.get(setName);

			spanIndexes.put(setName, new AnnotationSpanIndex(setConceptAnnotations));

			Map<String, Set<ConceptAnnotation>> classAnnotations = new HashMap<>();
			class2AnnotationsMap.put(setName, classAnnotations);

			for (ConceptAnnotation setConceptAnnotation : setConceptAnnotations) {
				String annotationClass = setConceptAnnotation.getOwlClass().toString();
				if (!classAnnotations.containsKey(annotationClass)) {
					classAnnotations.put(annotationClass, new HashSet<>());
				}
				classAnnotations.get(annotationClass).add(setConceptAnnotation);
			}
		}
	}

	void allwayIAA(Matcher matcher) throws IAAException {
		/*
		 * At the moment an concept is found to be a match, there are n-1
		 * other conceptAnnotations that are also found to be a match (an concept
		 * for each of the other annotators). We will gather all matches as we
		 * discover them so that a multiple conceptAnnotations will not match with an
		 * concept that has already been matched. This might happen if, for
		 * example, one profile mistakenly created a duplicate concept. We
		 * would only want to consider one of them a match. All conceptAnnotations that
		 * have been found to be a match will be put in
		 * matchedAnnotationsAllway.
		 */
		Set<ConceptAnnotation> matchedConceptAnnotations = new HashSet<>();
		for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
			String setName = conceptAnnotation.getAnnotator().getId();
			if (!matchedConceptAnnotations.contains(conceptAnnotation)) {
				MatchResult matchResult = new MatchResult();
				// just because an concept matches with another concept
				// from each
				// of the other sets, that does not mean the other conceptAnnotations
				// match with
				// each other. This is particularly true for 'overlapping' Span
				// criteria.
				Set<ConceptAnnotation> matches = match(conceptAnnotation, matchedConceptAnnotations, matcher, matchResult);
				if (matches != null) {
					allwayMatches.get(setName).add(conceptAnnotation);
					Set<ConceptAnnotation> allMatches = new HashSet<>(matches);
					allMatches.add(conceptAnnotation);
					allwayMatchSets.put(conceptAnnotation, allMatches);

					for (ConceptAnnotation match : matches) {
						String matchedSet = match.getAnnotator().getId();
						allwayMatches.get(matchedSet).add(match);
						allwayMatchSets.put(match, allMatches);
					}
					if (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH) {
						nontrivialAllwayMatches.get(setName).add(conceptAnnotation);
						for (ConceptAnnotation match : matches) {
							String matchedSet = match.getAnnotator().getId();
							nontrivialAllwayMatches.get(matchedSet).add(match);
						}

					} else if (matchResult.getResult() == MatchResult.TRIVIAL_MATCH) {
						trivialAllwayMatches.get(setName).add(conceptAnnotation);
						for (ConceptAnnotation match : matches) {
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

					matchedConceptAnnotations.add(conceptAnnotation);
					matchedConceptAnnotations.addAll(matches);
				} else {
					allwayNonmatches.get(setName).add(conceptAnnotation);
					if (matchResult.getResult() == MatchResult.NONTRIVIAL_NONMATCH)
						nontrivialAllwayNonmatches.get(setName).add(conceptAnnotation);
					else if (matchResult.getResult() == MatchResult.TRIVIAL_NONMATCH)
						trivialAllwayNonmatches.get(setName).add(conceptAnnotation);
					else {
						throw new IAAException(
								"Match algorithm resulted in a NONTRIVIAL_NONMATCH or TRIVIAL_NONMATCH, but the match algorithm did not return null.");
					}
				}
			}
		}
	}

	void pairwiseIAA(Matcher matcher) throws IAAException {
		for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
			String setName = conceptAnnotation.getAnnotator().getId();
			for (String compareSetName : annotationSets.keySet()) {
				if (!setName.equals(compareSetName)) {
					Set<ConceptAnnotation> matchedConceptAnnotations = pairwiseMatches.get(setName).get(compareSetName);
					if (matchedConceptAnnotations.contains(conceptAnnotation)) continue;

					Set<ConceptAnnotation> excludeConceptAnnotations = pairwiseMatches.get(compareSetName).get(setName);
					MatchResult matchResult = new MatchResult();
					ConceptAnnotation match =
							matcher.match(conceptAnnotation, compareSetName, excludeConceptAnnotations, this, matchResult);
					if (match != null) {
						pairwiseMatches.get(setName).get(compareSetName).add(conceptAnnotation);
						pairwiseMatches.get(compareSetName).get(setName).add(match);

						if (!pairwiseMatchPairs.containsKey(conceptAnnotation))
							pairwiseMatchPairs.put(conceptAnnotation, new HashSet<>());
						if (!pairwiseMatchPairs.containsKey(match))
							pairwiseMatchPairs.put(match, new HashSet<>());
						pairwiseMatchPairs.get(conceptAnnotation).add(match);
						pairwiseMatchPairs.get(match).add(conceptAnnotation);

						if (matchResult.getResult() == MatchResult.NONTRIVIAL_MATCH) {
							nontrivialPairwiseMatches.get(setName).get(compareSetName).add(conceptAnnotation);
							nontrivialPairwiseMatches.get(compareSetName).get(setName).add(match);
						} else if (matchResult.getResult() == MatchResult.TRIVIAL_MATCH) {
							trivialPairwiseMatches.get(setName).get(compareSetName).add(conceptAnnotation);
							trivialPairwiseMatches.get(compareSetName).get(setName).add(match);
						} else {
							throw new IAAException(
									"match algorithm did not return null but the match result was not NONTRIVIAL_MATCH or TRIVIAL_MATCH");
						}
					} else {
						pairwiseNonmatches.get(setName).get(compareSetName).add(conceptAnnotation);
						if (matchResult.getResult() == MatchResult.NONTRIVIAL_NONMATCH)
							nontrivialPairwiseNonmatches.get(setName).get(compareSetName).add(conceptAnnotation);
						else if (matchResult.getResult() == MatchResult.TRIVIAL_NONMATCH)
							trivialPairwiseNonmatches.get(setName).get(compareSetName).add(conceptAnnotation);
						else {
							throw new IAAException(
									"match algorithm returned null be the match result was not NONTRIVIAL_NONMATCH or TRIVIAL_NONMATCH");
						}
					}
				}
			}
		}
	}

	private Set<ConceptAnnotation> match(
			ConceptAnnotation conceptAnnotation,
			Set<ConceptAnnotation> excludeConceptAnnotations,
			Matcher matcher,
			MatchResult matchResult) {
		String setName = conceptAnnotation.getAnnotator().getId();
		Set<ConceptAnnotation> matchedConceptAnnotations = new HashSet<>();

		// trivial matches trump non-trivial matches. If there is a single
		// trivial match, then trivial_match is the match result.
		boolean trivialMatch = false;
		// nontrivial nonmatches trump trivial nonmatches. If there is a single
		// nontrivial match, then nontrivial_nonmatch is the match result.
		boolean nontrivialNonmatch = false;

		for (String compareSetName : annotationSets.keySet()) {
			if (!setName.equals(compareSetName)) {
				MatchResult result = new MatchResult();
				ConceptAnnotation match =
						matcher.match(conceptAnnotation, compareSetName, excludeConceptAnnotations, this, result);
				if (match != null) {
					matchedConceptAnnotations.add(match);
					if (result.getResult() == MatchResult.TRIVIAL_MATCH) {
						trivialMatch = true;
					}
				} else if (result.getResult() == MatchResult.NONTRIVIAL_NONMATCH) {
					nontrivialNonmatch = true;
				}
			}
		}
		if (matchedConceptAnnotations.size() == annotationSets.keySet().size() - 1) {
			if (trivialMatch) matchResult.setResult(MatchResult.TRIVIAL_MATCH);
			else matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return matchedConceptAnnotations;
		} else {
			if (nontrivialNonmatch) matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			else matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
			return null;
		}
	}

	public Set<ConceptAnnotation> getAnnotationsOfSameType(ConceptAnnotation conceptAnnotation, String compareSetName) {
		String annotationClass = conceptAnnotation.getOwlClass().toString();
		return safeReturn(class2AnnotationsMap.get(compareSetName).get(annotationClass));
	}

	public Set<ConceptAnnotation> getOverlappingAnnotations(ConceptAnnotation conceptAnnotation, String compareSetName) {
		AnnotationSpanIndex spanIndex = spanIndexes.get(compareSetName);
		return safeReturn(spanIndex.getOverlappingAnnotations(conceptAnnotation));
	}

	public Set<ConceptAnnotation> getExactlyOverlappingAnnotations(
			ConceptAnnotation conceptAnnotation, String compareSetName) {
		AnnotationSpanIndex spanIndex = spanIndexes.get(compareSetName);
		return safeReturn(spanIndex.getExactlyOverlappingAnnotations(conceptAnnotation));
	}

	//	public Map<String, Collection<concept>> getAnnotationSets() {
	//		return annotationSets;
	//	}

	public Set<String> getSetNames() {
		return setNames;
	}

	public Set<String> getAnnotationClasses() {
		return annotationClasses;
	}

	private Set<ConceptAnnotation> safeReturn(Set<ConceptAnnotation> returnValues) {
		if (returnValues == null) return emptyConceptAnnotationSet;
		return returnValues;
		// return Collections.unmodifiableSet(returnValues);
	}

	public Map<String, Set<ConceptAnnotation>> getAllwayMatches() {
		return allwayMatches;
	}

	public Map<String, Set<ConceptAnnotation>> getAllwayNonmatches() {
		return allwayNonmatches;
	}

	public Map<String, Map<String, Set<ConceptAnnotation>>> getPairwiseMatches() {
		return pairwiseMatches;
	}

	//	public Map<String, Map<String, Set<concept>>> getNontrivialPairwiseMatches() {
	//		return nontrivialPairwiseMatches;
	//	}
	//
	//	public Map<String, Map<String, Set<concept>>> getNontrivialPairwiseNonmatches() {
	//		return nontrivialPairwiseNonmatches;
	//	}

	public Map<String, Map<String, Set<ConceptAnnotation>>> getPairwiseNonmatches() {
		return pairwiseNonmatches;
	}

	public Map<String, Set<ConceptAnnotation>> getTrivialAllwayMatches() {
		return trivialAllwayMatches;
	}

	public Map<String, Set<ConceptAnnotation>> getTrivialAllwayNonmatches() {
		return trivialAllwayNonmatches;
	}

	public Map<String, Set<ConceptAnnotation>> getNontrivialAllwayMatches() {
		return nontrivialAllwayMatches;
	}

	public Map<String, Set<ConceptAnnotation>> getNontrivialAllwayNonmatches() {
		return nontrivialAllwayNonmatches;
	}

	public Map<ConceptAnnotation, Set<ConceptAnnotation>> getAllwayMatchSets() {
		return allwayMatchSets;
	}
}
