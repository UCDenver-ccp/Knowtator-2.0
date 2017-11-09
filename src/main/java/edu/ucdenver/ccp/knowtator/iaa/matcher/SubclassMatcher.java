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

import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.iaa.IAA;

import java.util.HashSet;
import java.util.Set;

/**
 * This matcher is very similar to ClassMatcher.
 * 
 * @author Compaq_Owner
 * 
 */

public class SubclassMatcher implements Matcher {

	String className;

	Set<String> subclassNames;


	public SubclassMatcher() {
	}

	/**
	 * This method will return a match from ClassMatcher. If one does not exist,
	 * then match for
	 * 
	 * 
	 * 
	 * Otherwise, null is returned.
	 * 
	 * @param annotation
	 * @param compareSetName
	 * @param excludeAnnotations
	 * @param iaa
	 * @param matchResult
	 *            will be set to NONTRIVIAL_MATCH, NONTRIVIAL_NONMATCH, or
	 *            TRIVIAL_NONMATCH. Trivial non-matches occur when the
	 *            annotation is not of the class specified by setIAAClass or a
	 *            subclass of it. Trivial non-matches should be ignored and not
	 *            counted in any IAA metrics.
	 * @seeedu.ucdenver.ccp.knowtator.iaa_original.matcher.Matcher#match(TextAnnotation, String, Set,
	 *      IAA, MatchResult)
	 * @seeedu.ucdenver.ccp.knowtator.iaa_original.matcher.MatchResult#NONTRIVIAL_MATCH
	 * @seeedu.ucdenver.ccp.knowtator.iaa_original.matcher.MatchResult#NONTRIVIAL_NONMATCH
	 * @seeedu.ucdenver.ccp.knowtator.iaa_original.matcher.MatchResult#TRIVIAL_NONMATCH
	 * @seeedu.ucdenver.ccp.knowtator.iaa_original.TextAnnotation#getShortestAnnotation(Collection)
	 */
	public TextAnnotation match(TextAnnotation annotation, String compareSetName, Set<TextAnnotation> excludeAnnotations, IAA iaa,
			MatchResult matchResult) {

		String annotationClassName = annotation.getOwlClassName();
		if (!subclassNames.contains(annotationClassName)) {
			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
			return null;
		}

		TextAnnotation classMatch = ClassMatcher.match(annotation, compareSetName, iaa, excludeAnnotations);
		if (classMatch != null) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return classMatch;
		}

		Set<TextAnnotation> candidateAnnotations = new HashSet<>();
		for (String subclassName : subclassNames) {
			candidateAnnotations.addAll(iaa.getAnnotationsOfClass(subclassName, compareSetName));
		}

		Set<TextAnnotation> exactlyOverlappingAnnotations = new HashSet<>(iaa.getExactlyOverlappingAnnotations(
				annotation, compareSetName));
		exactlyOverlappingAnnotations.retainAll(candidateAnnotations);
		exactlyOverlappingAnnotations.removeAll(excludeAnnotations);
		if (exactlyOverlappingAnnotations.size() > 0) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			return exactlyOverlappingAnnotations.iterator().next();
		}

		Set<TextAnnotation> overlappingAnnotations = new HashSet<>(iaa.getOverlappingAnnotations(annotation,
				compareSetName));
		overlappingAnnotations.retainAll(candidateAnnotations);
		overlappingAnnotations.removeAll(excludeAnnotations);
		if (overlappingAnnotations.size() > 0) {
			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
			if (overlappingAnnotations.size() == 1)
				return overlappingAnnotations.iterator().next();
			return TextAnnotation.getShortestAnnotation(overlappingAnnotations);
		}

		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
		return null;
	}
//
//	/**
//	 * Sets the class of the
//	 *
//	 * @param className
//	 */
//	public void setIAAClass(String className) {
//		this.className = className;
//		subclassNames = hierarchy.getSubclasses(className);
//	}

	public String getIAAClass() {
		return className;
	}

	public String getName() {
		return "Subclass matcher for class '" + className + "'";
	}

	public String getDescription() {
		return "Two annotations match if their class assignments are equal to '" + className + "' or a subclass of '"
				+ className + "' and their spans overlap.";
	}

	public boolean returnsTrivials() {
		return true;
	}

//	public Set<String> getSubclasses() {
//		return hierarchy.getSubclasses(className);
//	}
}
