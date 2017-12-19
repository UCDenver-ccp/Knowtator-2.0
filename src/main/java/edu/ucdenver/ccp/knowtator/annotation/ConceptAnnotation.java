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
 * Copyright (C) 2005-2008.  All Rights Reserved.
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

package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

import java.awt.*;
import java.util.List;
import java.util.TreeSet;

public class ConceptAnnotation extends Annotation {

	private String className;
	private String classID;
	private TreeSet<Span> spans;


	public ConceptAnnotation(String classID, String className, String annotationID, TextSource textSource, Profile annotator) {
		super(annotationID, textSource, annotator);
		this.className = className;
		this.classID = classID;

		spans = new TreeSet<>((span1, span2) -> {
			if (span1 == null) {
				return span2 == null ? 0 : -1;
			} else if (span2 == null) {
				return 1;
			}
			int compare = span1.getStart().compareTo(span2.getStart());
			if (compare == 0) {
				compare = span1.getEnd().compareTo(span2.getEnd());
			}
			return compare;
		});

//		log.warn(String.format("%1$-30s %2$30s", "annotation", toString()));
	}

	public String getClassID() {
		return classID;
	}
	public String getClassName() {
		return className;
	}
	public TreeSet<Span> getSpans() {
		return spans;
	}

	public Color getColor() {
		return annotator.getColor(classID, className);
	}

	/**
	 * @return the size of the Span associated with the annotation. If the
	 *         annotation has more than one Span, then the sum of the size of
	 *         the spans is returned.
	 */

	public int getSize() {
		int size = 0;
		TreeSet<Span> spans = getSpans();
		for (Span span : spans) {
			size += span.getSize();
		}
		return size;
	}




	void setSpans(List<Span> spans) {
		this.spans.clear();
		this.spans.addAll(spans);
	}

	void setSpan(Span span) {
		this.spans.clear();
		this.spans.add(span);
	}




	/**
	 * this needs to be moved out of this class
	 * 
	 * @return an html representation of the annotation
	 */
	public String toHTML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<ul><li>").append(annotator.getProfileID()).append("</li>");
		sb.append("<li>class = ").append(className).append("</li>");
		sb.append("<li>spans = ");
		for (Span span : spans)
			sb.append(span.toString()).append(" ");
		sb.append("</li>");

		sb.append("</ul>");
		return sb.toString();
	}

	@Override
	public String toString() {
		return  String.format("%s\n%s - %s\n%s", id, classID, className, getSpannedText(spans));
	}

	private String getSpannedText(TreeSet<Span> spans) {
		StringBuilder sb = new StringBuilder();
		spans.forEach(span -> {
			sb.append(textSource.getContent().substring(span.getStart(), span.getEnd()));
			sb.append("\n");
		});
		return sb.toString();
	}

	void addSpan(Span newSpan) {
		spans.add(newSpan);
	}

	void removeSpan(Span span) {
		spans.remove(span);
	}

	public boolean contains(Integer loc) {
		for (Span span : spans) {
			if (span.contains(loc)) {
				return true;
			}
		}
		return false;
	}



//		public Set<String> getSimpleFeatureNames() {
//		return Collections.unmodifiableSet(simpleFeatures.keySet());
//	}
//
//	public Set<Object> getSimpleFeatureValues(String featureName) {
//		if (simpleFeatures.get(featureName) == null)
//			return Collections.emptySet();
//		return Collections.unmodifiableSet(simpleFeatures.get(featureName));
//	}
//
//	@SuppressWarnings("unused")
//	public boolean isSimpleFeature(String featureName) {
//		return simpleFeatures.containsKey(featureName);
//	}
//
//	@SuppressWarnings("unused")
//	public void setSimpleFeature(String featureName, Set<Object> featureValues) {
//		if (featureValues == null)
//			return;
//		complexFeatures.remove(featureName);
//		simpleFeatures.put(featureName, new HashSet<>(featureValues));
//	}
//
//	@SuppressWarnings("unused")
//	public void setSimpleFeature(String featureName, Object featureValue) {
//		if (featureValue == null)
//			return;
//		complexFeatures.remove(featureName);
//		HashSet<Object> featureValues = new HashSet<>();
//		featureValues.add(featureValue);
//		simpleFeatures.put(featureName, featureValues);
//	}
//
//	public Set<String> getComplexFeatureNames() {
//		return Collections.unmodifiableSet(complexFeatures.keySet());
//	}
//
//	public Set<annotation> getComplexFeatureValues(String featureName) {
//		if (complexFeatures.get(featureName) == null)
//			return Collections.emptySet();
//		return Collections.unmodifiableSet(complexFeatures.get(featureName));
//	}
//
//	@SuppressWarnings("unused")
//	public boolean isComplexFeature(String featureName) {
//		return complexFeatures.containsKey(featureName);
//	}
//
//	@SuppressWarnings("unused")
//	public void setComplexFeature(String featureName, Set<annotation> featureValues) {
//		simpleFeatures.remove(featureName);
//		complexFeatures.put(featureName, new HashSet<>(featureValues));
//	}
//
//	@SuppressWarnings("unused")
//	public void setComplexFeature(String featureName, annotation featureValue) {
//		simpleFeatures.remove(featureName);
//		HashSet<annotation> featureValues = new HashSet<>();
//		featureValues.add(featureValue);
//		complexFeatures.put(featureName, featureValues);
//	}
//
//	public Set<String> getFeatureNames() {
//		Set<String> featureNames = new HashSet<>(simpleFeatures.keySet());
//		featureNames.addAll(complexFeatures.keySet());
//		return Collections.unmodifiableSet(featureNames);
//	}
	//	/**
//	 * This method checks to see if two annotations have the same simple
//	 * features but does not compare the values of the features.
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @return true if both annotations have the same number of simple features
//	 *         and they have the same names.
//	 */
//	@SuppressWarnings({"JavaDoc", "unused"})
//	public static boolean compareSimpleFeatureNames(annotation annotation1, annotation annotation2) {
//		return compareNames(annotation1.getSimpleFeatureNames(), annotation2.getSimpleFeatureNames());
//	}

//	/**
//	 * This method checks to see if two annotations have the same complex
//	 * features but does not compare the values of the features.
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @return true if both annotations have the same number of complex features
//	 *         and they have the same names.
//	 */
//	@SuppressWarnings({"JavaDoc", "unused"})
//	public static boolean compareComplexFeatureNames(annotation annotation1, annotation annotation2) {
//		return compareNames(annotation1.getComplexFeatureNames(), annotation2.getComplexFeatureNames());
//	}
//
//	@SuppressWarnings("unused")
//	public static boolean compareFeatureNames(annotation annotation1, annotation annotation2) {
//		return compareNames(annotation1.getFeatureNames(), annotation2.getFeatureNames());
//	}
//		public String getProperty(String propertyTag) {
//		return properties.get(propertyTag);
//	}
	//	/**
//	 * This method compares two annotations with respect to their spans,
//	 * annotation classes and simple features.
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @param spanComparison
//	 *            must be one of SPANS_OVERLAP_COMPARISON,
//	 *            SPANS_EXACT_COMPARISON, or IGNORE_SPANS_COMPARISON. If
//	 *            IGNORE_SPANS_COMPARISON is passed in, then the spans will be
//	 *            considered as matching.
//	 * @param compareClass
//	 *            if true, then the classes will be compared and will be
//	 *            considered matched if they are the same. If false, then the
//	 *            classes will not be compared and will be considered as
//	 *            matching.
//	 * @param simpleFeatureNames
//	 *            the simple features that will be compared.
//	 * @return MatchResult.TRIVIAL_NONMATCH if the spans do not match.
//	 *         MatchResult.TRIVIAL_NONMATCH if the classes do not match.
//	 *         MatchResult.TRIVIAL_MATCH if spans and classes match and
//	 *         simpleFeatureNames is empty or null. If spans and classes match,
//	 *         then the result of compareSimpleFeatures(annotation, annotation,
//	 *         Set<String>) is returned.
//	 */
//
//	@SuppressWarnings("JavaDoc")
//	public static int compareAnnotations(annotation annotation1, annotation annotation2, int spanComparison,
//										 boolean compareClass, Set<String> simpleFeatureNames) {
//		boolean spansMatch = false;
//		boolean classesMatch = false;
//
//		if (spanComparison == SPANS_OVERLAP_COMPARISON && spansOverlap(annotation1, annotation2))
//			spansMatch = true;
//		else if (spanComparison == SPANS_EXACT_COMPARISON && spansMatch(annotation1, annotation2))
//			spansMatch = true;
//		else if (spanComparison == IGNORE_SPANS_COMPARISON)
//			spansMatch = true;
//
//		if (spanComparison != SPANS_OVERLAP_COMPARISON && spanComparison != SPANS_EXACT_COMPARISON
//				&& spanComparison != IGNORE_SPANS_COMPARISON)
//			throw new IllegalArgumentException(
//					"The value for the parameter compareSpans is illegal.  Please use one of SPANS_OVERLAP_COMPARISON, SPANS_EXACT_COMPARISON, or IGNORE_SPANS_COMPARISON.");
//
//		if (!spansMatch)
//			return MatchResult.TRIVIAL_NONMATCH;
//
//		if (compareClass && classesMatch(annotation1, annotation2))
//			classesMatch = true;
//		else if (!compareClass)
//			classesMatch = true;
//
//		if (!classesMatch)
//			return MatchResult.TRIVIAL_NONMATCH;
//
//		return compareSimpleFeatures(annotation1, annotation2, simpleFeatureNames);
//
//	}
	//	/**
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @param featureName
//	 *            the docID of the feature that will be compared between the two
//	 *            annotations
//	 * @return MatchResult.TRIVIAL_NONMATCH if the featureName does not
//	 *         correspond to a simple feature in either or both of the
//	 *         annotations <br>
//	 *         the result of trivialCompare for the feature values unless that
//	 *         method returns MatchResult.MATCH_RESULT_UNASSIGNED. Otherwise, <br>
//	 *         MatchResult.NONTRIVIAL_MATCH if the values of the features are
//	 *         equal as defined by the equals method. <br>
//	 *         MatchResult.NONTRIVIAL_NONMATCH if the values are not equal as
//	 *         defined by the equals method.
//	 * @see #trivialCompare(Set, Set)
//	 */
//
//	@SuppressWarnings("JavaDoc")
//	public static int compareSimpleFeature(annotation annotation1, annotation annotation2, String featureName) {
//		// if(!annotation1.isSimpleFeature(featureName) ||
//		// !annotation2.isSimpleFeature(featureName)) return
//		// MatchResult.TRIVIAL_NONMATCH;
//
//		int trivialResult = trivialCompare(annotation1.getSimpleFeatureValues(featureName), annotation2
//				.getSimpleFeatureValues(featureName));
//		if (trivialResult != MatchResult.MATCH_RESULT_UNASSIGNED)
//			return trivialResult;
//
//		Set<Object> featureValues1 = annotation1.getSimpleFeatureValues(featureName);
//		Set<Object> featureValues2 = new HashSet<>(annotation2.getSimpleFeatureValues(featureName));
//
//		for (Object featureValue : featureValues1) {
//			if (!featureValues2.contains(featureValue)) {
//				return MatchResult.NONTRIVIAL_NONMATCH;
//			}
//			featureValues2.remove(featureValue);
//		}
//
//		return MatchResult.NONTRIVIAL_MATCH;
//
//	}

//	/**
//	 * Compares all of the simple features of two annotations
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @return <ul>
//	 *
//	 *         <li>TRIVIAL_NONMATCH if any of the simple features are trivial
//	 *         non-matches.
//	 *         <li>NONTRIVIAL_NONMATCH there is a non-matching simple feature
//	 *         and all non-matching simple features are non-trivial.
//	 *         <li>TRIVIAL_MATCH all simple features match and there is one
//	 *         simple feature that is a trivial match.
//	 *         <li>TRIVIAL_MATCH if there are no simple features.
//	 *         <li>NONTRIVIAL_MATH all simple features match and are non-trivial
//	 *         </ul>
//	 */
//	@SuppressWarnings("JavaDoc")
//	public static int compareSimpleFeatures(annotation annotation1, annotation annotation2) {
//		Set<String> featureNames = new HashSet<>(annotation1.getSimpleFeatureNames());
//		featureNames.addAll(annotation2.getSimpleFeatureNames());
//
//		if (featureNames.size() == 0)
//			return MatchResult.TRIVIAL_MATCH;
//
//		return compareSimpleFeatures(annotation1, annotation2, featureNames);
//	}

//	/**
//	 * Compares the simple features of two annotations named in featureNames
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @param featureNames
//	 *            the simple features to compare.
//	 * @return <ul>
//	 *         <li>TRIVIAL_NONMATCH if any of the features are trivial
//	 *         non-matches
//	 *         <li>NONTRIVIAL_NONMATCH if each of the simple features that are
//	 *         non-matching are also non-trivial. For example, if there are five
//	 *         simple features being compared and 2 are trivial matches, 1 is a
//	 *         non-trivial match, and the other 2 are non-trivial non-matches,
//	 *         then NONTRIVIAL_NONMATCH will be returned.
//	 *         <li>TRIVIAL_MATCH if all of the features match and at least one
//	 *         of them is a trivial match
//	 *         <li>TRIVIAL_MATCH if featureNames is an empty set or null.
//	 *         <li>NONTRIVIAL_MATH all simple features match and are non-trivial
//	 *         </ul>
//	 */
//	@SuppressWarnings("JavaDoc")
//	public static int compareSimpleFeatures(annotation annotation1, annotation annotation2, Set<String> featureNames) {
//		if (featureNames == null || featureNames.size() == 0)
//			return MatchResult.TRIVIAL_MATCH;
//		boolean trivialMatch = false;
//		boolean nonmatch = false;
//
//		for (String featureName : featureNames) {
//			int result = compareSimpleFeature(annotation1, annotation2, featureName);
//			if (result == MatchResult.TRIVIAL_NONMATCH) {
//				return result;
//			} else if (result == MatchResult.TRIVIAL_MATCH) {
//				trivialMatch = true;
//			} else if (result == MatchResult.NONTRIVIAL_NONMATCH) {
//				nonmatch = true;
//			}
//		}
//		if (nonmatch)
//			return MatchResult.NONTRIVIAL_NONMATCH;
//		if (trivialMatch)
//			return MatchResult.TRIVIAL_MATCH;
//		return MatchResult.NONTRIVIAL_MATCH;
//	}

//	/**
//	 * This method compares the complex features of two annotations. A complex
//	 * feature has a docID and a value. The value is a set of Annotations
//	 * (typically one - but can be more). The parameters to this method
//	 * determine how the feature values should be compared.
//	 *
//	 * @param annotation1
//	 * @param annotation2
//	 * @param complexFeatureName
//	 *            the docID of the feature that will be compared between the two
//	 *            annotations
//	 * @param complexFeatureSpanComparison
//	 *            specifies how the spans of the feature values should be
//	 *            compared. The value of this parameter must be one of
//	 *            SPANS_OVERLAP_COMPARISON, SPANS_EXACT_COMPARISON, or
//	 *            IGNORE_SPANS_COMPARISON. If IGNORE_SPANS_COMPARISON is passed
//	 *            in, then the spans will be considered as matching.
//	 * @param complexFeatureClassComparison
//	 *            specifies how the classes of the feature values should be
//	 *            compared. If true, then the classes will be compared and will
//	 *            be considered matched if they are the same. If false, then the
//	 *            classes will not be compared and will be considered as
//	 *            matching.
//	 * @param simpleFeatureNamesOfComplexFeature
//	 *            specifies which simple features of the feature values should
//	 *            be compared. If null or an empty set is passed in, then the
//	 *            next parameter should probably be set to 'false'.
//	 * @param trivialSimpleFeatureMatchesCauseTrivialMatch
//	 *            this parameter determines how a TRIVIAL_MATCH between simple
//	 *            features of the feature values should affect the return value
//	 *            of this method. If true, then a trivial match between any of
//	 *            the simple features of the feature values will cause
//	 *            TRIVIAL_MATCH (if it not a non-match) to be returned. If
//	 *            false, then a trivial match between any of the simple features
//	 *            will not have an effect on whether the return value of this
//	 *            method is TRIVIAL or NONTRIVIAL.
//	 *
//	 * @return MatchResult.TRIVIAL_NONMATCH if the featureName does not
//	 *         correspond to a complex feature in either or both of the
//	 *         annotations <br>
//	 *         the result of trivialCompare(Set, Set) for the feature values
//	 *         unless that method returns MatchResult.MATCH_RESULT_UNASSIGNED.
//	 *         Note that this is the only other criteria under which
//	 *         TRIVIAL_NONMATCH is returned. <br>
//	 *         MatchResult.NONTRIVIAL_MATCH if the values of the complex feature
//	 *         match as defined by the match parameters. <br>
//	 *         MatchResult.TRIVIAL_MATCH if the values of the complex feature
//	 *         match trivially and the parameter
//	 *         trivialSimpleFeatureMatchesCauseTrivialMatch is true. <br>
//	 *         MatchResult.NONTRIVIAL_NONMATCH if the values are not equal as
//	 *         defined by the match parameters.
//	 *
//	 */
//	@SuppressWarnings("JavaDoc")
//	public static int compareComplexFeature(annotation annotation1, annotation annotation2, String complexFeatureName,
//											int complexFeatureSpanComparison, boolean complexFeatureClassComparison,
//											Set<String> simpleFeatureNamesOfComplexFeature, boolean trivialSimpleFeatureMatchesCauseTrivialMatch) {
//		// if(!annotation1.isComplexFeature(complexFeatureName) ||
//		// !annotation2.isComplexFeature(complexFeatureName)) return
//		// MatchResult.TRIVIAL_NONMATCH;
//
//		Set<annotation> featureValues1 = annotation1.getComplexFeatureValues(complexFeatureName);
//		Set<annotation> featureValues2 = new HashSet<>(annotation2
//				.getComplexFeatureValues(complexFeatureName));
//
//		int trivialResult = trivialCompare(featureValues1, featureValues2);
//
//		if (trivialResult != MatchResult.MATCH_RESULT_UNASSIGNED)
//			return trivialResult;
//
//		boolean trivialSimpleFeatureMatch = false;
//		for (annotation featureValue1 : featureValues1) {
//			annotation matchedFeature = null;
//			int matchedFeatureResult = MatchResult.MATCH_RESULT_UNASSIGNED;
//
//			for (annotation featureValue2 : featureValues2) {
//				int result = compareAnnotations(featureValue1, featureValue2, complexFeatureSpanComparison,
//						complexFeatureClassComparison, simpleFeatureNamesOfComplexFeature);
//				if (result == MatchResult.NONTRIVIAL_MATCH) {
//					matchedFeature = featureValue2;
//					matchedFeatureResult = result;
//					break;
//				} else if (result == MatchResult.TRIVIAL_MATCH) {
//					matchedFeature = featureValue2;
//					matchedFeatureResult = result;
//					// do not break because we want to prefer NONTRIVIAL_MATCHes
//				}
//			}
//			if (matchedFeature != null) {
//				featureValues2.remove(matchedFeature);
//				if (matchedFeatureResult == MatchResult.TRIVIAL_MATCH)
//					trivialSimpleFeatureMatch = true;
//			} else {
//				return MatchResult.NONTRIVIAL_NONMATCH;
//			}
//		}
//
//		if (trivialSimpleFeatureMatch && trivialSimpleFeatureMatchesCauseTrivialMatch) {
//			return MatchResult.TRIVIAL_MATCH;
//		}
//
//		return MatchResult.NONTRIVIAL_MATCH;
//
//	}
// 	/**
//	 * returns true only if both annotations have the same non-null
//	 * annotationClass.
//	 */
//	public static boolean classesMatch(annotation annotation1, annotation annotation2) {
//		String cls1 = annotation1.getClassID();
//		String cls2 = annotation2.getClassID();
//
//		return cls1 != null && cls2 != null && cls1.equals(cls2);
//
//	}
//public static boolean compareNames(Set<String> names1, Set<String> names2) {
//	if (names1.size() != names2.size())
//		return false;
//	for (String docID : names1) {
//		if (!names2.contains(docID))
//			return false;
//	}
//	return true;
//}
//	/**
//	 * @return MatchResult.TRIVIAL_MATCH if both values are null, one is null
//	 *         and the other empty, or if both are empty <br>
//	 *         MatchResult.TRIVIAL_NONMATCH if one of the values is empty and
//	 *         they other is not, or if one values is null and the other is not
//	 *         null and not empty <br>
//	 *         MatchResult.NONTRIVIAL_NONMATCH if the sizes of the values are
//	 *         different. <br>
//	 *         MatchResult.MATCH_RESULT_UNASSIGNED is none of the above.
//	 * @param values1
//	 *            the value of a feature (simple or complex)
//	 * @param values2
//	 *            the value of another feature (simple or complex)
//	 * @return MatchResult.TRIVIAL_MATCH, MatchResult.TRIVIAL_NONMATCH,
//	 *         MatchResult.NONTRIVIAL_NONMATCH, or MATCH_RESULT_UNASSIGNED
//	 *
//	 */
//
//	@SuppressWarnings("JavaDoc")
//	public static int trivialCompare(Set<?> values1, Set<?> values2) {
//		if (values1 == null && values2 == null)
//			return MatchResult.TRIVIAL_MATCH; // if both are null than it is a
//		// trivial match
//		if (values1 == null && values2.size() == 0)
//			return MatchResult.TRIVIAL_MATCH; // if one is null and the other
//		// empty, then trivial match
//		if (values2 == null && values1.size() == 0)
//			return MatchResult.TRIVIAL_MATCH;
//		if (values1 == null || values2 == null)
//			return MatchResult.TRIVIAL_NONMATCH; // if one is null and the other
//		// is not empty, then trivial
//		// nonmatch
//		if (values1.size() == 0 && values2.size() == 0)
//			return MatchResult.TRIVIAL_MATCH; // if both are empty, then trivial
//		// nonmatch
//		if (values1.size() == 0 || values2.size() == 0)
//			return MatchResult.TRIVIAL_NONMATCH; // if one is empty and the
//		// other is not, then trivial
//		// nonmatch
//		if (values1.size() != values2.size())
//			return MatchResult.NONTRIVIAL_NONMATCH; // if neither are empty and
//		// the sizes are different,
//		// then non-trivial nonmatch
//		return MatchResult.MATCH_RESULT_UNASSIGNED;
//	}
//	public static final int SPANS_OVERLAP_COMPARISON = 1;
//
//	public static final int SPANS_EXACT_COMPARISON = 2;
//
//	public static final int IGNORE_SPANS_COMPARISON = 3;
//public Span getSpanContainingLocation(int loc) {
//		for (Span span : spans) {
//			if (loc >= span.getStart() && loc <= span.getEnd()) {
//				return span;
//			}
//		}
//		return null;
//	}
}
