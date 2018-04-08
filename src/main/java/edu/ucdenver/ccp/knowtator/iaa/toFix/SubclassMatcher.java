///*
// * The contents of this test_project are subject to the Mozilla Public
// * License Version 1.1 (the "License"); you may not use this test_project
// * except in compliance with the License. You may obtain a copy of
// * the License at http://www.mozilla.org/MPL/
// *
// * Software distributed under the License is distributed on an "AS
// * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// * implied. See the License for the specific language governing
// * rights and limitations under the License.
// *
// * The Original Code is Knowtator.
// *
// * The Initial Developer of the Original Code is University of Colorado.
// * Copyright (C) 2005 - 2008.  All Rights Reserved.
// *
// * Knowtator was developed by the Center for Computational Pharmacology
// * (http://compbio.uchcs.edu) at the University of Colorado Health
// *  Sciences Center School of Medicine with support from the National
// *  Library of Medicine.
// *
// * Current information about Knowtator can be obtained at
// * http://knowtator.sourceforge.net/
// *
// * Contributor(s):
// *   Philip V. Ogren <philip@ogren.info> (Original Author)
// */
//package edu.ucdenver.ccp.knowtator.iaa.matcher;
//
//import edu.ucdenver.ccp.knowtator.annotation.text.annotation;
//import edu.ucdenver.ccp.knowtator.iaa.IAA;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * This matcher is very similar to ClassMatcher.
// *
// * @author Compaq_Owner
// *
// */
//
//public class SubclassMatcher implements Matcher {
//
//	String className;
//
//	Set<String> subclassNames;
//
//
//	public SubclassMatcher() {
//	}
//
//	/**
//	 * This method will return a match from ClassMatcher. If one does not exist,
//	 * then match for
//	 *
//	 *
//	 *
//	 * Otherwise, null is returned.
//	 *
//	 * @param matchResult
//	 *            will be set to NONTRIVIAL_MATCH, NONTRIVIAL_NONMATCH, or
//	 *            TRIVIAL_NONMATCH. Trivial non-matches occur when the
//	 *            annotation is not of the class specified by setIAAClass or a
//	 *            subclass of it. Trivial non-matches should be ignored and not
//	 *            counted in any IAA metrics.
//	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher#match(annotation, String, Set,
//	 *      IAA, MatchResult)
//	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_MATCH
//	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#NONTRIVIAL_NONMATCH
//	 * @see edu.ucdenver.ccp.knowtator.iaa.matcher.MatchResult#TRIVIAL_NONMATCH
//	 */
//	public annotation match(annotation annotation, String compareSetName, Set<annotation> excludeAnnotations, IAA iaa,
//							MatchResult matchResult) {
//
//		String annotationClassName = annotation.getOWLClassID();
//		if (!subclassNames.contains(annotationClassName)) {
//			matchResult.setResult(MatchResult.TRIVIAL_NONMATCH);
//			return null;
//		}
//
//		annotation classMatch = ClassMatcher.match(annotation, compareSetName, iaa, excludeAnnotations);
//		if (classMatch != null) {
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return classMatch;
//		}
//
//		Set<annotation> candidateAnnotations = new HashSet<>();
//		for (String subclassName : subclassNames) {
//			candidateAnnotations.addAll(iaa.getAnnotationsOfClass(subclassName, compareSetName));
//		}
//
//		Set<annotation> exactlyOverlappingAnnotations = new HashSet<>(iaa.getExactlyOverlappingAnnotations(
//				annotation, compareSetName));
//		exactlyOverlappingAnnotations.retainAll(candidateAnnotations);
//		exactlyOverlappingAnnotations.removeAll(excludeAnnotations);
//		if (exactlyOverlappingAnnotations.size() > 0) {
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			return exactlyOverlappingAnnotations.iterator().next();
//		}
//
//		Set<annotation> overlappingAnnotations = new HashSet<>(iaa.getOverlappingAnnotations(annotation,
//				compareSetName));
//		overlappingAnnotations.retainAll(candidateAnnotations);
//		overlappingAnnotations.removeAll(excludeAnnotations);
//		if (overlappingAnnotations.size() > 0) {
//			matchResult.setResult(MatchResult.NONTRIVIAL_MATCH);
//			if (overlappingAnnotations.size() == 1)
//				return overlappingAnnotations.iterator().next();
//			return annotation.getShortestAnnotation(overlappingAnnotations);
//		}
//
//		matchResult.setResult(MatchResult.NONTRIVIAL_NONMATCH);
//		return null;
//	}
////
////	/**
////	 * Sets the class of the
////	 *
////	 * @param className
////	 */
////	public void setIAAClass(String className) {
////		this.className = className;
////		subclassNames = hierarchy.getSubclasses(className);
////	}
//
//	public String getIAAClass() {
//		return className;
//	}
//
//	public String getId() {
//		return "Subclass matcher for class '" + className + "'";
//	}
//
//	public String getDescription() {
//		return "Two annotations match if their class assignments are equal to '" + className + "' or a subclass of '"
//				+ className + "' and their spans overlap.";
//	}
//
//	public boolean returnsTrivials() {
//		return true;
//	}
//
////	public Set<String> getSubclasses() {
////		return hierarchy.getSubclasses(className);
////	}
//}
