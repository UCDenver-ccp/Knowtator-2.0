package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import java.util.Set;

public interface Matcher {
	/**
	 * @param conceptAnnotation         the concept that is to be matched
	 * @param compareSetName     the set of annotations that we will look for a match in
	 * @param excludeConceptAnnotations a set of annotations that cannot be the return value
	 * @param iaa                an instance of IAA from which to get information about annotations
	 * @param matchResult        instantiate a new MatchType and pass it to the method. The method must set
	 *                           the result to one of the four result values given in MatchResult.
	 * @return the concept that was matched. If none exists then return null.
	 */
	ConceptAnnotation match(
			ConceptAnnotation conceptAnnotation,
			String compareSetName,
			Set<ConceptAnnotation> excludeConceptAnnotations,
			IAA iaa,
			MatchResult matchResult);

	String getName();

	String getDescription();

	boolean returnsTrivials();
}
