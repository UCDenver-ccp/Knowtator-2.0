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

package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.iaa.IAA;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassAndSpanMatcher implements Matcher {

	/**
	 * @param matchResult
	 *            will be set to NONTRIVIAL_MATCH or NONTRIVIAL_NONMATCH.
	 *            Trivial matches and non-matches are not defined for this
	 *            matcher.
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(Annotation, String, Set,
	 *      IAA, MatchResult)
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 */
	@SuppressWarnings("Duplicates")
	public Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
							MatchResult matchResult) {
		Annotation match = match(annotation, compareSetName, iaa, excludeAnnotations);
		if (match != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return match;
		} else {
			matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
			return null;
		}
	}

	/**
	 * This is a static version of the above match method that can be called by
	 * other matcher implementations.
	 *
	 * @return an Annotation that matches or null.
	 */
	public static Annotation match(Annotation annotation, String compareSetName, IAA iaa,
								   Set<Annotation> excludeAnnotations) {
		Set<Annotation> singleMatchSet = matches(annotation, compareSetName, iaa, excludeAnnotations, true);
		if (singleMatchSet.size() == 1) {
			return singleMatchSet.iterator().next();
		} else
			return null;

	}

	/**
	 *
	 * @param returnFirst
	 *            if true then a set of size 1 will be returned as soon as a
	 *            match is found. If false then all matches will be returned.
	 * @return this method will not return null - but rather an empty set of no
	 *         matches are found.
	 */

	public static Set<Annotation> matches(Annotation annotation, String compareSetName, IAA iaa,
										  Set<Annotation> excludeAnnotations, boolean returnFirst) {
		String type = annotation.getClassName();
		Set<Annotation> candidateAnnotations = new HashSet<>(iaa.getExactlyOverlappingAnnotations(annotation,
				compareSetName));
		candidateAnnotations.removeAll(excludeAnnotations);
		if (candidateAnnotations.size() == 0)
			return Collections.emptySet();

		Set<Annotation> returnValues = new HashSet<>();
		for (Annotation candidateAnnotation : candidateAnnotations) {
			if (!excludeAnnotations.contains(candidateAnnotation)
					&& candidateAnnotation.getClassName().equals(type)) {
				returnValues.add(candidateAnnotation);
				if (returnFirst)
					return returnValues;
			}
		}
		return returnValues;
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
