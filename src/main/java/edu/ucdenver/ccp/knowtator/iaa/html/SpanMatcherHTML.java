package edu.ucdenver.ccp.knowtator.iaa.html;

import edu.ucdenver.ccp.knowtator.iaa.AnnotationSpanIndex;
import edu.ucdenver.ccp.knowtator.iaa.IAA;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;

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
			Map<Annotation, String> annotationTexts,
			Map<Annotation, String> annotationTextNames)
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

		Map<String, Set<Annotation>> allwayMatches = iaa.getAllwayMatches();
		Map<String, Set<Annotation>> allwayNonmatches = iaa.getAllwayNonmatches();

		Map<Annotation, Set<Annotation>> matchSets = iaa.getAllwayMatchSets();

		Set<Annotation> allwayMatchesSingleSet = IAA2HTML.getSingleSet(allwayMatches);
		Set<Annotation> allwayNonmatchesSingleSet = IAA2HTML.getSingleSet(allwayNonmatches);

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

		Map<String, Set<Annotation>> sortedAllwayMatches =
				IAA2HTML.sortByType(classes, allwayMatchesSingleSet);
		Map<String, Set<Annotation>> sortedAllwayNonmatches =
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

		Map<String, Map<String, Set<Annotation>>> pairwiseMatches = iaa.getPairwiseMatches();
		Map<String, Map<String, Set<Annotation>>> pairwiseNonmatches = iaa.getPairwiseNonmatches();

		IAA2HTML.printPairwiseAgreement(
				html, sets, pairwiseMatches, pairwiseNonmatches, percentageFormat);

		html.flush();
		html.close();
	}

	private static Map<String, int[]> errorMatrix(
			Set<Annotation> matches, Map<Annotation, Set<Annotation>> matchSets) {
		Map<String, int[]> counts = new HashMap<>();

		for (Annotation match : matches) {
			Set<Annotation> matchedAnnotations = matchSets.get(match);
			for (Annotation matchedAnnotation : matchedAnnotations) {
				if (!matchedAnnotation.equals(match)) {
					String annotationClass = matchedAnnotation.getOwlClass().toString();
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
