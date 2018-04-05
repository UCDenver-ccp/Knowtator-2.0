package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;

import java.util.Set;

public interface Matcher {
	/**
	 * @param annotation
	 *            the annotation that is to be matched
	 * @param compareSetName
	 *            the set of annotations that we will look for a match in
	 * @param excludeAnnotations
	 *            a set of annotations that cannot be the return value
	 * @param iaa
	 *            an instance of IAA from which to get information about
	 *            annotations
	 * @param matchResult
	 *            instantiate a new MatchType and pass it to the method. The
	 *            method must set the result to one of the four result values
	 *            given in MatchResult.
	 * @return the annotation that was matched. If none exists then return null.
	 */
	Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
					 MatchResult matchResult);

	String getName();

	String getDescription();

	boolean returnsTrivials();
}
