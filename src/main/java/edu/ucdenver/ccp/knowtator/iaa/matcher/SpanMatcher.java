package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import java.util.HashSet;
import java.util.Set;

public class SpanMatcher implements Matcher {
	/**
	 * This method will return an concept with the same class and spans. If one does not exist,
	 * then it will return an concept with the same spans (but different class). Otherwise, null is
	 * returned.
	 *
	 * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH. Trivial matches and
	 *                    non-matches are not defined for this matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(ConceptAnnotation, String, Set, IAA,
	 * MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 */
	@SuppressWarnings("SuspiciousMethodCalls")
	public ConceptAnnotation match(
			ConceptAnnotation conceptAnnotation,
			String compareSetName,
			Set<ConceptAnnotation> excludeConceptAnnotations,
			IAA iaa,
			MatchResult matchResult) {
		ConceptAnnotation spanAndClassMatch =
				ClassAndSpanMatcher.match(conceptAnnotation, compareSetName, iaa, excludeConceptAnnotations);
		if (spanAndClassMatch != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return spanAndClassMatch;
		}

		Set<ConceptAnnotation> candidateConceptAnnotations =
				new HashSet<>(iaa.getExactlyOverlappingAnnotations(conceptAnnotation, compareSetName));
		candidateConceptAnnotations.remove(excludeConceptAnnotations);

		for (ConceptAnnotation candidateConceptAnnotation : candidateConceptAnnotations) {
			if (!excludeConceptAnnotations.contains(candidateConceptAnnotation)) {
				matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
				return candidateConceptAnnotation;
			}
		}
		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		return null;
	}

	public String getName() {
		return "Span matcher";
	}

	public String getDescription() {
		return "Annotations match if they have the same spans.";
	}

	public boolean returnsTrivials() {
		return false;
	}
}
