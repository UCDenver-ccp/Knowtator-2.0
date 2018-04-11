package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.Annotation;

import java.util.HashSet;
import java.util.Set;

public class SpanMatcher implements Matcher {
	/**
	 * This method will return an annotation with the same class and spans. If one does not exist,
	 * then it will return an annotation with the same spans (but different class). Otherwise, null is
	 * returned.
	 *
	 * @param matchResult will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH. Trivial matches and
	 *                    non-matches are not defined for this matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(Annotation, String, Set, IAA,
	 * MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 */
	@SuppressWarnings("SuspiciousMethodCalls")
	public Annotation match(
			Annotation annotation,
			String compareSetName,
			Set<Annotation> excludeAnnotations,
			IAA iaa,
			MatchResult matchResult) {
		Annotation spanAndClassMatch =
				ClassAndSpanMatcher.match(annotation, compareSetName, iaa, excludeAnnotations);
		if (spanAndClassMatch != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return spanAndClassMatch;
		}

		Set<Annotation> candidateAnnotations =
				new HashSet<>(iaa.getExactlyOverlappingAnnotations(annotation, compareSetName));
		candidateAnnotations.remove(excludeAnnotations);

		for (Annotation candidateAnnotation : candidateAnnotations) {
			if (!excludeAnnotations.contains(candidateAnnotation)) {
				matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
				return candidateAnnotation;
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
