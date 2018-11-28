/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.iaa.html;

import edu.ucdenver.ccp.knowtator.iaa.AnnotationSpanIndex;
import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import java.io.File;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.*;

public class SpanMatcherHTML {

	public static void printIAA(
			IAA iaa,
			Matcher matcher,
			File directory,
			int numberOfDocs,
			Map<ConceptAnnotation, String> annotationTexts,
			Map<ConceptAnnotation, String> annotationTextNames)
			throws Exception {
		NumberFormat percentageFormat = NumberFormat.getPercentInstance();
		percentageFormat.setMinimumFractionDigits(2);

		String fileName = matcher.getName();

		PrintStream html = new PrintStream(new File(directory, fileName + ".html"));

		html.println(IAA2HTML.initHTML(matcher.getName(), matcher.getDescription()));
		html.println("<h2>" + iaa.getSetNames().size() + "-way IAA Results</h2>");

		html.println(
				"<table border=1><tr>"
						+ "<td><b>Type</b></td>"
						+ "<td><b>IAA</b></td>"
						+ "<td><b>matches</b></td>"
						+ "<td><b>non-matches</b></td>"
						+ "<td><b>confused class assignments</tr>");

		Set<String> classes = iaa.getAnnotationClasses();
		Set<String> sets = iaa.getSetNames();

		Map<String, Set<ConceptAnnotation>> allwayMatches = iaa.getAllwayMatches();
		Map<String, Set<ConceptAnnotation>> allwayNonmatches = iaa.getAllwayNonmatches();

		Map<ConceptAnnotation, Set<ConceptAnnotation>> matchSets = iaa.getAllwayMatchSets();

		Set<ConceptAnnotation> allwayMatchesSingleSet = IAA2HTML.getSingleSet(allwayMatches);
		Set<ConceptAnnotation> allwayNonmatchesSingleSet = IAA2HTML.getSingleSet(allwayNonmatches);

		AnnotationSpanIndex spanIndex = new AnnotationSpanIndex(allwayNonmatchesSingleSet);

		int totalAllwayMatches = allwayMatchesSingleSet.size();
		int totalAllwayNonmatches = allwayNonmatchesSingleSet.size();

		double iaaScore =
				(double) totalAllwayMatches
						/ ((double) totalAllwayMatches + (double) totalAllwayNonmatches);

		html.println(
				"<tr><td><b>All classes</b></td>"
						+ "<td>"
						+ percentageFormat.format(iaaScore)
						+ "</td>"
						+ "<td>"
						+ totalAllwayMatches
						+ "</td>"
						+ "<td>"
						+ totalAllwayNonmatches
						+ "</td></tr>");

		Map<String, Set<ConceptAnnotation>> sortedAllwayMatches =
				IAA2HTML.sortByType(classes, allwayMatchesSingleSet);
		Map<String, Set<ConceptAnnotation>> sortedAllwayNonmatches =
				IAA2HTML.sortByType(classes, allwayNonmatchesSingleSet);

		List<String> sortedTypes = new ArrayList<>(classes);
		Collections.sort(sortedTypes);

		for (String type : sortedTypes) {
			int classMatches = sortedAllwayMatches.get(type).size();
			int classNonmatches = sortedAllwayNonmatches.get(type).size();

			iaaScore = (double) classMatches / ((double) classMatches + (double) classNonmatches);

			html.println(
					"<tr><td>"
							+ type
							+ "</td>"
							+ "<td>"
							+ percentageFormat.format(iaaScore)
							+ "</td>"
							+ "<td>"
							+ classMatches
							+ "</td>"
							+ "<td>"
							+ classNonmatches
							+ "</td>");
			Map<String, int[]> confusionCounts = errorMatrix(sortedAllwayMatches.get(type), matchSets);
			html.println("<td>");
			for (String confusedClass : confusionCounts.keySet()) {
				html.println("  " + confusedClass + "=" + confusionCounts.get(confusedClass)[0]);
			}
			html.println("</td>");
		}
		html.println("</table>");

		html.println("<br>IAA calculated on " + numberOfDocs + " documents.");
		html.println("<br>all annotations = matches + non-matches");
		html.println("<br>IAA = matches / all annotations");

		IAA2HTML.printMatchData(
				html,
				sets,
				fileName,
				directory,
				allwayMatches,
				annotationTexts,
				annotationTextNames,
				classes,
				iaa);

		IAA2HTML.printNonmatchData(
				html,
				sets,
				fileName,
				directory,
				allwayNonmatches,
				spanIndex,
				annotationTexts,
				annotationTextNames,
				classes);

		Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseMatches = iaa.getPairwiseMatches();
		Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseNonmatches = iaa.getPairwiseNonmatches();

		IAA2HTML.printPairwiseAgreement(
				html, sets, pairwiseMatches, pairwiseNonmatches, percentageFormat);

		html.flush();
		html.close();
	}

	private static Map<String, int[]> errorMatrix(
			Set<ConceptAnnotation> matches, Map<ConceptAnnotation, Set<ConceptAnnotation>> matchSets) {
		Map<String, int[]> counts = new HashMap<>();

		for (ConceptAnnotation match : matches) {
			Set<ConceptAnnotation> matchedConceptAnnotations = matchSets.get(match);
			for (ConceptAnnotation matchedConceptAnnotation : matchedConceptAnnotations) {
				if (!matchedConceptAnnotation.equals(match)) {
					String annotationClass = matchedConceptAnnotation.getOWLClassLabel();
					if (!counts.containsKey(annotationClass)) {
						counts.put(annotationClass, new int[1]);
					}
					counts.get(annotationClass)[0]++;
				}
			}
		}
		return counts;
	}
}
