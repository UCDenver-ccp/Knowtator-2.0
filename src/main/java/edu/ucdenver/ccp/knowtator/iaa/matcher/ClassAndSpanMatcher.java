package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassAndSpanMatcher implements Matcher {

	/**
	 * This is a static version of the above match method that can be called by other matcher
	 * implementations.
	 *
	 * @return an concept that matches or null.
	 */
	public static ConceptAnnotation match(
			ConceptAnnotation conceptAnnotation, String compareSetName, IAA iaa, Set<ConceptAnnotation> excludeConceptAnnotations) {
		Set<ConceptAnnotation> singleMatchSet = matches(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
		if (singleMatchSet.size() == 1) {
			return singleMatchSet.iterator().next();
		} else return null;
	}

	/**
	 * @return this method will not return null - but rather an empty set of no matches are found.
	 */
	private static Set<ConceptAnnotation> matches(
			ConceptAnnotation conceptAnnotation, String compareSetName, IAA iaa, Set<ConceptAnnotation> excludeConceptAnnotations) {
		String type = conceptAnnotation.getOwlClass().toString();
		Set<ConceptAnnotation> candidateConceptAnnotations =
				new HashSet<>(iaa.getExactlyOverlappingAnnotations(conceptAnnotation, compareSetName));
		candidateConceptAnnotations.removeAll(excludeConceptAnnotations);
		if (candidateConceptAnnotations.size() == 0) return Collections.emptySet();

		Set<ConceptAnnotation> returnValues = new HashSet<>();
		for (ConceptAnnotation candidateConceptAnnotation : candidateConceptAnnotations) {
			if (!excludeConceptAnnotations.contains(candidateConceptAnnotation)
					&& candidateConceptAnnotation.getOwlClass().toString().equals(type)) {
				returnValues.add(candidateConceptAnnotation);
				return returnValues;
			}
		}
		return returnValues;
	}

	/**
	 * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH. Trivial matches and
	 *                    non-matches are not defined for this matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(ConceptAnnotation, String, Set, IAA,
	 * MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 */
	@SuppressWarnings("Duplicates")
	public ConceptAnnotation match(
			ConceptAnnotation conceptAnnotation,
			String compareSetName,
			Set<ConceptAnnotation> excludeConceptAnnotations,
			IAA iaa,
			MatchResult matchResult) {
		ConceptAnnotation match = match(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
		if (match != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return match;
		} else {
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			return null;
		}
	}

	public String getName() {
		return "Class and Span matcher";
	}

	public String getDescription() {

		return "Annotations match if they have the same class assignment and the same spans.";
	}

	public boolean returnsTrivials() {
		return false;
	}
}
