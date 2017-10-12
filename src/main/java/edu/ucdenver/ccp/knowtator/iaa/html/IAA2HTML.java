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

package edu.ucdenver.ccp.knowtator.iaa.html;

import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextSpan;
import edu.ucdenver.ccp.knowtator.iaa.matcher.Matcher;
import edu.ucdenver.ccp.knowtator.iaa.AnnotationSpanIndex;
import edu.ucdenver.ccp.knowtator.iaa.IAA;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class IAA2HTML {

	public static void printIAA(IAA iaa, Matcher matcher, File directory, int numberOfDocs,
								Map<TextAnnotation, String> annotationTexts, Map<TextAnnotation, String> annotationTextNames) throws Exception {
		NumberFormat percentageFormat = NumberFormat.getPercentInstance();
		percentageFormat.setMinimumFractionDigits(2);

		String fileName = matcher.getName();

		PrintStream tabular = new PrintStream(new File(directory, fileName + ".dat"));
		PrintStream html = new PrintStream(new File(directory, fileName + ".html"));

		initHTML(html, matcher.getName(), null, null, matcher.getDescription());

		printIntro(html, iaa, numberOfDocs, fileName, matcher);
		html.println("<p>");
		printTitleRowForAllwayIAA(html, matcher);

		tabular
				.println("This file is provided to facilitate cut-n-paste into a spreadsheet.\n"
						+ "If you cannot directly copy the data below into a spreadsheet without it all going into a single cell,\n"
						+ "then try copying to a text editor first and then copy it from there.  There is typically a 'paste special'\n"
						+ "option under the Edit menu that will allow you to paste the copied data as text.  This will also work.\n\n\n");

		if (matcher.returnsTrivials())
			tabular
					.println("type\tmatches\ttrivial matches\tnon-trivial matches\tnon-matches\ttrivial non-matches\tnon-trivial non-matches");
		else
			tabular.println("type\tmatches\tnon-matches");

		Set<String> classes = iaa.getAnnotationClasses();
		Set<String> sets = iaa.getSetNames();

		Map<String, Set<TextAnnotation>> allwayMatches = iaa.getAllwayMatches();
		Map<String, Set<TextAnnotation>> nontrivialAllwayMatches = iaa.getNontrivialAllwayMatches();
		Map<String, Set<TextAnnotation>> trivialAllwayMatches = iaa.getTrivialAllwayMatches();
		Map<String, Set<TextAnnotation>> allwayNonmatches = iaa.getAllwayNonmatches();
		Map<String, Set<TextAnnotation>> nontrivialAllwayNonmatches = iaa.getNontrivialAllwayNonmatches();
		Map<String, Set<TextAnnotation>> trivialAllwayNonmatches = iaa.getTrivialAllwayNonmatches();

		Set<TextAnnotation> allwayMatchesSingleSet = getSingleSet(allwayMatches);
		Set<TextAnnotation> trivialAllwayMatchesSingleSet = getSingleSet(trivialAllwayMatches);
		Set<TextAnnotation> nontrivialAllwayMatchesSingleSet = getSingleSet(nontrivialAllwayMatches);
		Set<TextAnnotation> allwayNonmatchesSingleSet = getSingleSet(allwayNonmatches);
		Set<TextAnnotation> trivialAllwayNonmatchesSingleSet = getSingleSet(trivialAllwayNonmatches);
		Set<TextAnnotation> nontrivialAllwayNonmatchesSingleSet = getSingleSet(nontrivialAllwayNonmatches);

		AnnotationSpanIndex spanIndex = new AnnotationSpanIndex(allwayNonmatchesSingleSet);

		int totalAllwayMatches = allwayMatchesSingleSet.size();
		int totalTrivialAllwayMatches = trivialAllwayMatchesSingleSet.size();
		int totalNontrivialAllwayMatches = nontrivialAllwayMatchesSingleSet.size();
		int totalAllwayNonmatches = allwayNonmatchesSingleSet.size();
		int totalTrivialAllwayNonmatches = trivialAllwayNonmatchesSingleSet.size();
		int totalNontrivialAllwayNonmatches = nontrivialAllwayNonmatchesSingleSet.size();

		double iaaScore = (double) totalAllwayMatches / ((double) totalAllwayMatches + (double) totalAllwayNonmatches);
		double stingyIAAScore = (double) totalNontrivialAllwayMatches
				/ ((double) totalAllwayMatches + (double) totalAllwayNonmatches);
		double respectableIAAScore = (double) totalNontrivialAllwayMatches
				/ ((double) totalNontrivialAllwayMatches + (double) totalAllwayNonmatches);
		double nontrivialIAAScore = (double) totalNontrivialAllwayMatches
				/ ((double) totalNontrivialAllwayMatches + (double) totalNontrivialAllwayNonmatches);

		if (matcher.returnsTrivials()) {
			html.println("<tr><td><b>All classes</b></td>" + "<td>" + percentageFormat.format(iaaScore) + "</td>"
					+ "<td>" + percentageFormat.format(stingyIAAScore) + "</td>" + "<td>"
					+ percentageFormat.format(respectableIAAScore) + "</td>" + "<td>"
					+ percentageFormat.format(nontrivialIAAScore) + "</td>" + "<td>" + totalAllwayMatches + "</td>"
					+ "<td>" + totalTrivialAllwayMatches + "</td>" + "<td>" + totalNontrivialAllwayMatches + "</td>"
					+ "<td>" + totalAllwayNonmatches + "</td>" + "<td>" + totalTrivialAllwayNonmatches + "</td>"
					+ "<td>" + totalNontrivialAllwayNonmatches + "</td></tr>");
			tabular.println("All classes\t" + totalAllwayMatches + "\t" + totalTrivialAllwayMatches + "\t"
					+ totalNontrivialAllwayMatches + "\t" + totalAllwayNonmatches + "\t" + totalTrivialAllwayNonmatches
					+ "\t" + totalNontrivialAllwayNonmatches);
		} else {
			html.println("<tr><td><b>All classes</b></td>" + "<td>" + percentageFormat.format(iaaScore) + "</td>"
					+ "<td>" + totalAllwayMatches + "</td>" + "<td>" + totalAllwayNonmatches + "</td></tr>");
			tabular.println("All classes\t" + totalAllwayMatches + "\t" + totalAllwayNonmatches);
		}

		Map<String, Set<TextAnnotation>> sortedAllwayMatches = sortByType(classes, allwayMatchesSingleSet);
		Map<String, Set<TextAnnotation>> sortedAllwayTrivialMatches = sortByType(classes, trivialAllwayMatchesSingleSet);
		Map<String, Set<TextAnnotation>> sortedAllwayNontrivialMatches = sortByType(classes,
				nontrivialAllwayMatchesSingleSet);
		Map<String, Set<TextAnnotation>> sortedAllwayNonmatches = sortByType(classes, allwayNonmatchesSingleSet);
		Map<String, Set<TextAnnotation>> sortedAllwayTrivialNonmatches = sortByType(classes,
				trivialAllwayNonmatchesSingleSet);
		Map<String, Set<TextAnnotation>> sortedAllwayNontrivialNonmatches = sortByType(classes,
				nontrivialAllwayNonmatchesSingleSet);

		List<String> sortedTypes = new ArrayList<>(classes);
		Collections.sort(sortedTypes);

		for (String type : sortedTypes) {
			int classMatches = sortedAllwayMatches.get(type).size();
			int classTrivialMatches = sortedAllwayTrivialMatches.get(type).size();
			int classNontrivialMatches = sortedAllwayNontrivialMatches.get(type).size();
			int classNonmatches = sortedAllwayNonmatches.get(type).size();
			int classTrivialNonmatches = sortedAllwayTrivialNonmatches.get(type).size();
			int classNontrivialNonmatches = sortedAllwayNontrivialNonmatches.get(type).size();

			iaaScore = (double) classMatches / ((double) classMatches + (double) classNonmatches);
			stingyIAAScore = (double) classNontrivialMatches / ((double) classMatches + (double) classNonmatches);
			respectableIAAScore = (double) classNontrivialMatches
					/ ((double) classNontrivialMatches + (double) classNonmatches);
			nontrivialIAAScore = (double) classNontrivialMatches
					/ ((double) classNontrivialMatches + (double) classNontrivialNonmatches);

			if (matcher.returnsTrivials()) {
				html.println("<tr><td>" + type + "</td>" + "<td>" + percentageFormat.format(iaaScore) + "</td>"
						+ "<td>" + percentageFormat.format(stingyIAAScore) + "</td>" + "<td>"
						+ percentageFormat.format(respectableIAAScore) + "</td>" + "<td>"
						+ percentageFormat.format(nontrivialIAAScore) + "</td>" + "<td>" + classMatches + "</td>"
						+ "<td>" + classTrivialMatches + "</td>" + "<td>" + classNontrivialMatches + "</td>" + "<td>"
						+ classNonmatches + "</td>" + "<td>" + classTrivialNonmatches + "</td>" + "<td>"
						+ classNontrivialNonmatches + "</td></tr>");
				tabular.println(type + "\t" + classMatches + "\t" + classTrivialMatches + "\t" + classNontrivialMatches
						+ "\t" + classNonmatches + "\t" + classTrivialNonmatches + "\t" + classNontrivialNonmatches);
			} else {
				html.println("<tr><td>" + type + "</td>" + "<td>" + percentageFormat.format(iaaScore) + "</td>"
						+ "<td>" + classMatches + "</td>" + "<td>" + classNonmatches + "</td></tr>");
				tabular.println(type + "\t" + classMatches + "\t" + classNonmatches);
			}
		}
		html.println("</table>");

		printMatchData(html, sets, fileName, directory, allwayMatches, classes, spanIndex, sortedTypes,
				annotationTexts, annotationTextNames, matcher, trivialAllwayMatches, nontrivialAllwayMatches, iaa);

		printNonmatchData(html, sets, fileName, directory, allwayNonmatches, classes, spanIndex, sortedTypes,
				annotationTexts, annotationTextNames, matcher, trivialAllwayNonmatches, nontrivialAllwayNonmatches);

		Map<String, Map<String, Set<TextAnnotation>>> pairwiseMatches = iaa.getPairwiseMatches();
		Map<String, Map<String, Set<TextAnnotation>>> pairwiseNonmatches = iaa.getPairwiseNonmatches();

		printPairwiseAgreement(html, sets, pairwiseMatches, pairwiseNonmatches, percentageFormat);

		html.flush();
		html.close();
		tabular.flush();
		tabular.close();
	}

	private static void printMatchData(PrintStream html, Set<String> sets, String fileName, File directory,
                                       Map<String, Set<TextAnnotation>> allwayMatches, Set<String> classes, AnnotationSpanIndex spanIndex,
                                       List<String> sortedTypes, Map<TextAnnotation, String> annotationTexts,
                                       Map<TextAnnotation, String> annotationTextNames, Matcher matcher,
                                       Map<String, Set<TextAnnotation>> trivialAllwayMatches, Map<String, Set<TextAnnotation>> nontrivialAllwayMatches,
                                       IAA iaa) throws Exception {
		html.println("<h2>match data</h2>");
		html.println("<ul>");

		Map<TextAnnotation, Set<TextAnnotation>> matchSets = iaa.getAllwayMatchSets();

		for (String set : sets) {
			String matchesFileName = fileName + ".matches." + set + ".html";
			html.println("<li><a href=\"" + matchesFileName + "\">matches for " + set + "</a></li>");
			PrintStream matchesStream = new PrintStream(new File(directory, matchesFileName));
			Set<TextAnnotation> matches = allwayMatches.get(set);
			Map<String, Set<TextAnnotation>> sortedMatches = sortByType(classes, matches);

			initHTML(
					matchesStream,
					"Matches for " + set,
					fileName + ".html",
					fileName,
					"Each annotation that was considered a match is shown in the text that it was found in.  The matching annotations from the other annotation sets are also shown.");
			printInstances(matchesStream, sortedMatches, sortedTypes, annotationTexts, annotationTextNames, matchSets);
			matchesStream.flush();
			matchesStream.close();

			if (matcher.returnsTrivials()) {
				String trivialMatchesFileName = fileName + ".trivial.matches." + set + ".html";
				html.println("<li><a href=\"" + trivialMatchesFileName + "\">trivial matches for " + set + "</a></li>");
				PrintStream trivialMatchesStream = new PrintStream(new File(directory, trivialMatchesFileName));
				Set<TextAnnotation> trivialMatches = trivialAllwayMatches.get(set);
				Map<String, Set<TextAnnotation>> sortedTrivialMatches = sortByType(classes, trivialMatches);
				initHTML(
						trivialMatchesStream,
						"Trivial matches for " + set,
						fileName + ".html",
						fileName,
						"Each annotation that was considered a trival match is shown in the text that it was found in.  The matching annotations from the other annotation sets are also shown.");
				printInstances(trivialMatchesStream, sortedTrivialMatches, sortedTypes, annotationTexts,
						annotationTextNames, matchSets);
				trivialMatchesStream.flush();
				trivialMatchesStream.close();

				String nontrivialMatchesFileName = fileName + ".nontrivial.matches." + set + ".html";
				html.println("<li><a href=\"" + nontrivialMatchesFileName + "\">non-trivial matches for " + set
						+ "</a></li>");
				PrintStream nontrivialMatchesStream = new PrintStream(new File(directory, nontrivialMatchesFileName));
				Set<TextAnnotation> nontrivialMatches = nontrivialAllwayMatches.get(set);
				Map<String, Set<TextAnnotation>> sortedNontrivialMatches = sortByType(classes, nontrivialMatches);
				initHTML(
						nontrivialMatchesStream,
						"non-trivial non-matches for " + set,
						fileName + ".html",
						fileName,
						"Each annotation that was considered a non-trival match is shown in the text that it was found in.  The matching from the other annotation sets are also shown.");
				printInstances(nontrivialMatchesStream, sortedNontrivialMatches, sortedTypes, annotationTexts,
						annotationTextNames, matchSets);
				nontrivialMatchesStream.flush();
				nontrivialMatchesStream.close();
			}
		}
		html.println("</ul><hr>");

	}

	private static void printNonmatchData(PrintStream html, Set<String> sets, String fileName, File directory,
                                          Map<String, Set<TextAnnotation>> allwayNonmatches, Set<String> classes, AnnotationSpanIndex spanIndex,
                                          List<String> sortedTypes, Map<TextAnnotation, String> annotationTexts,
                                          Map<TextAnnotation, String> annotationTextNames, Matcher matcher,
                                          Map<String, Set<TextAnnotation>> trivialAllwayNonmatches,
                                          Map<String, Set<TextAnnotation>> nontrivialAllwayNonmatches) throws Exception {
		html.println("<h2>non-match data</h2>");
		html.println("<ul>");

		for (String set : sets) {
			String errorsFileName = fileName + ".nonmatches." + set + ".html";
			html.println("<li><a href=\"" + errorsFileName + "\">non-matches for " + set + "</a></li>");
			PrintStream errors = new PrintStream(new File(directory, errorsFileName));
			Set<TextAnnotation> nonmatches = allwayNonmatches.get(set);
			Map<String, Set<TextAnnotation>> sortedNonmatches = sortByType(classes, nonmatches);

			Map<TextAnnotation, Set<TextAnnotation>> comparisonAnnotations = new HashMap<>();
			for (TextAnnotation nonmatch : nonmatches) {
				comparisonAnnotations.put(nonmatch, getCandidateAnnotations(nonmatch, spanIndex));
			}

			initHTML(
					errors,
					"Non-matches for " + set,
					fileName + ".html",
					fileName,
					"Each annotation that was considered a non-match is shown in the text that it was found in.  Overlapping annotations from the other annotation sets are also shown.");
			printInstances(errors, sortedNonmatches, sortedTypes, annotationTexts, annotationTextNames,
					comparisonAnnotations);
			errors.flush();
			errors.close();

			if (matcher.returnsTrivials()) {
				String trivialNonMatchesFileName = fileName + ".trivial.nonmatches." + set + ".html";
				html.println("<li><a href=\"" + trivialNonMatchesFileName + "\">trivial non-matches for " + set
						+ "</a></li>");
				PrintStream trivialErrors = new PrintStream(new File(directory, trivialNonMatchesFileName));
				Set<TextAnnotation> trivialNonmatches = trivialAllwayNonmatches.get(set);
				Map<String, Set<TextAnnotation>> sortedTrivialNonmatches = sortByType(classes, trivialNonmatches);
				initHTML(
						trivialErrors,
						"Trivial non-matches for " + set,
						fileName + ".html",
						fileName,
						"Each annotation that was considered a trival non-match is shown in the text that it was found in.  Overlapping annotations from the other annotation sets are also shown.");
				printInstances(trivialErrors, sortedTrivialNonmatches, sortedTypes, annotationTexts,
						annotationTextNames, comparisonAnnotations);
				trivialErrors.flush();
				trivialErrors.close();

				String nontrivialNonMatchesFileName = fileName + ".nontrivial.nonmatches." + set + ".html";
				html.println("<li><a href=\"" + nontrivialNonMatchesFileName + "\">non-trivial non-matches for " + set
						+ "</a></li>");
				PrintStream nontrivialErrors = new PrintStream(new File(directory, nontrivialNonMatchesFileName));
				Set<TextAnnotation> nontrivialNonmatches = nontrivialAllwayNonmatches.get(set);
				Map<String, Set<TextAnnotation>> sortedNontrivialNonmatches = sortByType(classes, nontrivialNonmatches);
				initHTML(
						nontrivialErrors,
						"non-trivial non-matches for " + set,
						fileName + ".html",
						fileName,
						"Each annotation that was considered a non-trival non-match is shown in the text that it was found in.  Overlapping annotations from the other annotation sets are also shown.");
				printInstances(nontrivialErrors, sortedNontrivialNonmatches, sortedTypes, annotationTexts,
						annotationTextNames, comparisonAnnotations);
				nontrivialErrors.flush();
				nontrivialErrors.close();
			}
		}
		html.println("</ul><hr>");
	}

	private static Set<TextAnnotation> getCandidateAnnotations(TextAnnotation textAnnotation, AnnotationSpanIndex spanIndex) {
		Set<TextAnnotation> candidateTextAnnotations = new HashSet<>();
		String set = textAnnotation.getSetName();
		String docID = textAnnotation.getDocID();

		Set<TextAnnotation> overlappingTextAnnotations = spanIndex.getOverlappingAnnotations(textAnnotation);
		for (TextAnnotation overlappingTextAnnotation : overlappingTextAnnotations) {
			String candidateAnnotationSet = overlappingTextAnnotation.getSetName();
			if (!candidateAnnotationSet.equals(set)) {
				String candidateDocID = overlappingTextAnnotation.getDocID();
				if (candidateDocID.equals(docID)) {
					candidateTextAnnotations.add(overlappingTextAnnotation);
				}
			}
		}
		return candidateTextAnnotations;
	}

	private static void printInstances(PrintStream out, Map<String, Set<TextAnnotation>> sortedAnnotations,
                                       List<String> sortedTypes, Map<TextAnnotation, String> annotationTexts,
                                       Map<TextAnnotation, String> annotationTextNames, Map<TextAnnotation, Set<TextAnnotation>> comparisonAnnotations) {
		for (String type : sortedTypes) {
			out.println("<h2>" + type + "</h2>");
			Set<TextAnnotation> typeTextAnnotations = sortedAnnotations.get(type);
			for (TextAnnotation textAnnotation : typeTextAnnotations) {
				writeAnnotationTextSourceHTML(out, textAnnotation, annotationTexts.get(textAnnotation), annotationTextNames
						.get(textAnnotation));
				out.println("<ul><li>");
				printAnnotationHTML(out, textAnnotation, annotationTexts.get(textAnnotation));

				Set<TextAnnotation> comparisons = comparisonAnnotations.get(textAnnotation);
				if (comparisons != null) {
					for (TextAnnotation comparisonTextAnnotation : comparisons) {
						if (!comparisonTextAnnotation.equals(textAnnotation)) {
							out.println("<li>");
							printAnnotationHTML(out, comparisonTextAnnotation, annotationTexts.get(comparisonTextAnnotation));
						}
					}
				}
				out.println("</ul>");
			}
		}
	}

	public static Set<TextAnnotation> getSingleSet(Map<String, Set<TextAnnotation>> annotations) {
		Set<TextAnnotation> returnValues = new HashSet<>();
		for (String setName : annotations.keySet()) {
			returnValues.addAll(annotations.get(setName));
		}
		return returnValues;
	}

	private static void initHTML(PrintStream html, String title, String link, String linkLabel, String description) {
		html.println("<html>");
		html.println("<head><title>" + title + "</title></head>");
		html.println("<body>");
		if (link != null)
			html.println("<a href=\"" + link + "\">" + linkLabel + "</a>");
		html.println("<h1>" + title + "</h1>");
		html.println(description);
		html.println("<hr>");
	}

	static Map<String, Set<TextAnnotation>> sortByType(Set<String> types, Collection<TextAnnotation> textAnnotations) {
		Map<String, Set<TextAnnotation>> sortedAnnotations = new HashMap<>();

		for (String type : types) {
			sortedAnnotations.put(type, new HashSet<>());
		}
		for (TextAnnotation textAnnotation : textAnnotations) {
			String type = textAnnotation.getClassName();
			if (type != null)
				sortedAnnotations.get(type).add(textAnnotation);
		}
		return sortedAnnotations;
	}

	private static void writeAnnotationTextSourceHTML(PrintStream out, TextAnnotation textAnnotation, String annotationText,
                                                      String annotationTextName) {
		StringBuilder html = new StringBuilder("<hr><p>");
		if (annotationTextName != null)
			html.append("Text source name = ").append(annotationTextName).append("<p>");

		if (annotationText != null) {
			List<TextSpan> textSpans = textAnnotation.getTextSpans();
			List<TextSpan> modifiedTextSpans = new ArrayList<>(textSpans);

			annotationText = shortenText(annotationText, modifiedTextSpans);

			int mark = 0;

			for (TextSpan textSpan : modifiedTextSpans) {
				try {
					html.append(annotationText.substring(mark, textSpan.getStart())).append("<b>");
					html.append(TextSpan.substring(annotationText, textSpan)).append("</b>");
					mark = textSpan.getEnd();
				} catch (StringIndexOutOfBoundsException sioobe) {
					sioobe.printStackTrace();

				}

			}
			if (mark < annotationText.length())
				html.append(annotationText.substring(mark));
		}
		out.println(html.toString());
	}

	private static String shortenText(String text, List<TextSpan> textSpans) {
		int frontBuffer = 150;
		int endBuffer = 150;
		if (textSpans.size() > 0) {
			TextSpan textSpan = textSpans.get(0);
			int start = Math.max(0, textSpan.getStart() - frontBuffer);
			int end = Math.min(text.length(), textSpan.getEnd() + endBuffer);
			String substring = text.substring(start, end);

			for (int i = 0; i < textSpans.size(); i++) {
				textSpan = textSpans.get(i);
				TextSpan offsetTextSpan = new TextSpan(textSpan.getStart() - start, textSpan.getEnd() - start);
				textSpans.set(i, offsetTextSpan);
			}
			return substring;
		}
		return text;
	}

	private static void printAnnotationHTML(PrintStream out, TextAnnotation textAnnotation, String annotationText) {
		StringBuilder html = new StringBuilder();

		if (annotationText != null) {
			String coveredText = TextAnnotation.getCoveredText(textAnnotation, annotationText, " ... ");
			html.append(coveredText);
		}
		html.append("  ").append(textAnnotation.toHTML());
		out.print(html.toString());
	}

	private static void printIntro(PrintStream html, IAA iaa, int numberOfDocs, String fileName, Matcher matcher) {
		html
				.println("<p>For more detailed documentation on IAA please see the <a href=\"http://knowtator.sourceforge.net//iaa.shtml\">"
						+ "IAA documentation</a>.");

		html.println("<p>");
		html.println("<h2>" + iaa.getSetNames().size() + "-way IAA Results</h2>");
		html.println("IAA calculated on " + numberOfDocs + " documents.");
		html.println("<p><a href=\"" + fileName + ".dat\">tabular data</a>");
		html.println("<p>all annotations = matches + non-matches");
		html.println("<br> IAA = matches / all annotations");
		if (matcher.returnsTrivials()) {
			html.println("<br>stingy IAA = non-trivial matches / (matches + non-matches)");
			html.println("<br>respectable IAA = non-trivial matches / (non-trivial matches + non-matches)");
			html.println("<br>non-trivial IAA = non-trivial matches / (non-trivial matches + non-trivial non-matches)");
		}

	}

	private static void printTitleRowForAllwayIAA(PrintStream html, Matcher matcher) {
		html.println("<table border=1><tr><td><b>Type</b></td>" + "<td><b>IAA</b></td>");
		if (matcher.returnsTrivials()) {
			html.println("<td><b>stingy IAA</b></td>" + "<td><b>respectable IAA</b></td>"
					+ "<td><b>non-trivial IAA</b></td>");
		}
		html.println("<td><b>matches</b></td>");
		if (matcher.returnsTrivials()) {
			html.println("<td><b>trivial matches</b></td>" + "<td><b>non-trivial matches</b></td>");
		}
		html.println("<td><b>non-matches</b></td>");
		if (matcher.returnsTrivials()) {
			html.println("<td><b>trivial non-matches</b></td>" + "<td><b>non-trivial non-matches</b></td>");
		}
		html.println("</tr>");
	}

	public static String initHTML(String title, String description) {
		return "<html>\n" +
				"<head><title>" + title + "</title></head>\n" +
				"<body>\n" +
				"<h1>" + title + "</h1>\n" +
				description +
				" For more detailed documentation on IAA please see the <a href=\"http://knowtator.sourceforge.net//iaa.shtml\">IAA documentation</a>.\n";
	}

	static void printMatchData(PrintStream html, Set<String> sets, String fileName, File directory,
                               Map<String, Set<TextAnnotation>> allwayMatches, Map<TextAnnotation, String> annotationTexts,
                               Map<TextAnnotation, String> annotationTextNames, Set<String> classes, IAA iaa) throws IOException

	{
		html.println("<h2>match data</h2>");
		html.println("<ul>");

		Map<TextAnnotation, Set<TextAnnotation>> matchSets = iaa.getAllwayMatchSets();
		List<String> sortedTypes = new ArrayList<>(classes);
		Collections.sort(sortedTypes);

		for (String set : sets) {
			String matchesFileName = fileName + ".matches." + set + ".html";
			html.println("<li><a href=\"" + matchesFileName + "\">matches for " + set + "</a></li>");
			PrintStream matchesStream = new PrintStream(new File(directory, matchesFileName));
			Set<TextAnnotation> matches = allwayMatches.get(set);
			Map<String, Set<TextAnnotation>> sortedMatches = IAA2HTML.sortByType(classes, matches);

			matchesStream
					.println(initHTML(
							"Matches for " + set,
							"Each annotation that was considered a match is shown in the text that it was found in.  The matching annotations from the other annotation sets are also shown."));
			IAA2HTML.printInstances(matchesStream, sortedMatches, sortedTypes, annotationTexts, annotationTextNames,
					matchSets);
			matchesStream.flush();
			matchesStream.close();
		}
		html.println("</ul><hr>");
	}

	static void printNonmatchData(PrintStream html, Set<String> sets, String fileName, File directory,
                                  Map<String, Set<TextAnnotation>> allwayNonmatches, AnnotationSpanIndex spanIndex,
                                  Map<TextAnnotation, String> annotationTexts, Map<TextAnnotation, String> annotationTextNames, Set<String> classes,
                                  IAA iaa) throws IOException {
		html.println("<h2>non-match data</h2>");
		html.println("<ul>");

		List<String> sortedTypes = new ArrayList<>(classes);
		Collections.sort(sortedTypes);

		for (String set : sets) {
			String errorsFileName = fileName + ".nonmatches." + set + ".html";
			html.println("<li><a href=\"" + errorsFileName + "\">non-matches for " + set + "</a></li>");
			PrintStream errors = new PrintStream(new File(directory, errorsFileName));
			Set<TextAnnotation> nonmatches = allwayNonmatches.get(set);
			Map<String, Set<TextAnnotation>> sortedNonmatches = IAA2HTML.sortByType(classes, nonmatches);

			Map<TextAnnotation, Set<TextAnnotation>> comparisonAnnotations = new HashMap<>();
			for (TextAnnotation nonmatch : nonmatches) {
				comparisonAnnotations.put(nonmatch, IAA2HTML.getCandidateAnnotations(nonmatch, spanIndex));
			}

			errors
					.println(initHTML(
							"Non-matches for " + set,
							"Each annotation that was considered a non-match is shown in the text that it was found in.  Overlapping annotations from the other annotation sets are also shown."));
			IAA2HTML.printInstances(errors, sortedNonmatches, sortedTypes, annotationTexts, annotationTextNames,
					comparisonAnnotations);
			errors.flush();
			errors.close();

		}
		html.println("</ul><hr>");
	}

	static void printPairwiseAgreement(PrintStream html, Set<String> sets,
                                       Map<String, Map<String, Set<TextAnnotation>>> pairwiseMatches,
                                       Map<String, Map<String, Set<TextAnnotation>>> pairwiseNonmatches, NumberFormat percentageFormat) {
		html.println("<h2>Pair-wise agreement</h2>");
		html.println("<table border=1><tr><td><b>Gold standard set</b></td>" + "<td><b>compared set</b></td>"
				+ "<td><b>true positives</b></td>" + "<td><b>false positives</b></td>"
				+ "<td><b>false negatives</b></td>" + "<td><b>precision</b></td>" + "<td><b>recall</b></td>"
				+ "<td><b>F-score</b></td></tr>");

		for (String setName : sets) {
			for (String setName2 : sets) {

				if (!setName.equals(setName2)) {
					Set<TextAnnotation> truePositives = pairwiseMatches.get(setName).get(setName2);
					Set<TextAnnotation> falseNegatives = pairwiseNonmatches.get(setName).get(setName2);
					Set<TextAnnotation> falsePositives = pairwiseNonmatches.get(setName2).get(setName);
					double precision = (double) truePositives.size()
							/ ((double) truePositives.size() + (double) falsePositives.size());
					double recall = (double) truePositives.size()
							/ ((double) truePositives.size() + (double) falseNegatives.size());
					double f_score = ((double) 2 * precision * recall) / (recall + precision);

					html.println("<tr><td>" + setName + "</td>" + "<td>" + setName2 + "</td>" + "<td>"
							+ truePositives.size() + "</td>" + "<td>" + falsePositives.size() + "</td>" + "<td>"
							+ falseNegatives.size() + "</td>" + "<td>" + percentageFormat.format(precision) + "</td>"
							+ "<td>" + percentageFormat.format(recall) + "</td>" + "<td>"
							+ percentageFormat.format(f_score) + "</td></tr>");
				}
			}
		}
		html.println("</table>");
		html.println("Precision and recall are given equal weight for the F-score.");
	}

}

// for(String type : sortedTypes)
// {
// errors.println("<h2>"+type+"</h2>");
// Set<TextAnnotation> typeNonmatches = sortedNonmatches.get(type);
// for(TextAnnotation annotation : typeNonmatches)
// {
// String docID = annotation.getDocID();
// writeAnnotationTextSourceHTML(errors, annotation,
// annotationTexts.get(annotation), annotationTextNames.get(annotation));
// errors.println("<ul><li>");
// printAnnotationHTML(errors, annotation, annotationTexts.get(annotation));
//        
// Set<TextAnnotation> candidateAnnotations =
// spanIndex.getOverlappingAnnotations(annotation);
// for(TextAnnotation candidateAnnotation : candidateAnnotations)
// {
// String candidateAnnotationSet = candidateAnnotation.getSetName();
// if(!candidateAnnotationSet.equals(set))
// {
// String candidateDocID = candidateAnnotation.getDocID();
// if(candidateDocID.equals(docID))
// {
// errors.println("<li>");
// printAnnotationHTML(errors, candidateAnnotation,
// annotationTexts.get(candidateAnnotation));
// }
// }
// }
// errors.println("</ul>");
// }
// }

// for(String set : sets)
// {
// String matchesFileName = fileName+".matches."+set+".html";
//html.println("<li><a href=\""+matchesFileName+"\">matches for "+set+"</a></li>"
// );
// PrintStream matchesStream = new PrintStream(new File(directory,
// matchesFileName));
// Set<TextAnnotation> matches = allwayMatches.get(set);
// Map<String, Set<TextAnnotation>> sortedMatches = sortByType(classes, matches);
// Map<TextAnnotation, Set<TextAnnotation>> matchSets = new HashMap<TextAnnotation,
// Set<TextAnnotation>>();
// for(TextAnnotation nonmatch : nonmatches)
// {
// comparisonAnnotations.put(nonmatch, getCandidateAnnotations(nonmatch,
// spanIndex));
// }
//
// initHTML(errors, "Non-matches for "+set, fileName+".html", fileName,
// "Each annotation that was considered a non-match is shown in the text that it was found in.  Overlapping annotations from the other annotation sets are also shown."
// );
// printInstances(errors, sortedNonmatches, sortedTypes, annotationTexts,
// annotationTextNames, comparisonAnnotations);
// errors.flush(); errors.close();
// }

// String matchesFileName = fileName+".matches.html";
// String trivialMatchesFileName = fileName+".trivial.matches.html";
// String nontrivialMatchesFileName = fileName+".nontrivial.matches.html";
// PrintStream matchesStream = new PrintStream(new File(directory,
// matchesFileName));
// PrintStream trivialMatchesStream = new PrintStream(new File(directory,
// trivialMatchesFileName));
// PrintStream nontrivialMatchesStream = new PrintStream(new File(directory,
// nontrivialMatchesFileName));
//
// html.println("<h2>match data</h2>");
// html.println("<ul><li><a href=\""+matchesFileName+"\">Matches</a></li>");
// if(matcher.returnsTrivials())
// { html.println("<li><a href=\""+trivialMatchesFileName+
// "\">Trivial matches</a></li>");
// html.println("<li><a href=\""+nontrivialMatchesFileName+
// "\">Non-trivial matches</a></li>");
// }
// html.println("</ul>");
//
// Map<TextAnnotation, Set<TextAnnotation>> matchSets = iaa.getAllwayMatchSets();
// initHTML(matchesStream, "Matches", fileName+".html", fileName,
// "Each annotation that was considered a match is shown in the text that it was found in.  Annotations from each of annotation sets are shown because there may be differences in the individual annotations if the match criteria ignored those differences.  Only one of the annotation's spans are bolded in the text."
// );
// if(matcher.returnsTrivials())
// {
// initHTML(trivialMatchesStream, "Trivial matches", fileName+".html", fileName,
// "Each annotation that was considered a match is shown in the text that it was found in.  Annotations from each of annotation sets are shown because there may be differences in the individual annotations if the match criteria ignored those differences.  Only one of the annotation's spans are bolded in the text."
// );
// initHTML(nontrivialMatchesStream, "Non-trivial matches", fileName+".html",
// fileName,
// "Each annotation that was considered a match is shown in the text that it was found in.  Annotations from each of annotation sets are shown because there may be differences in the individual annotations if the match criteria ignored those differences.  Only one of the annotation's span is bolded in the text."
// );
// }
//
// Set<TextAnnotation> printedAnnotations = new HashSet<TextAnnotation>();
// for(String type : sortedTypes)
// {
// matchesStream.println("<h2>"+type+"</h2>");
// trivialMatchesStream.println("<h2>"+type+"</h2>");
// nontrivialMatchesStream.println("<h2>"+type+"</h2>");
//  
// Set<TextAnnotation> typeTrivialMatches = sortedAllwayTrivialMatches.get(type);
// Set<TextAnnotation> typeNontrivialMatches =
// sortedAllwayNontrivialMatches.get(type);
// Set<TextAnnotation> typeMatches = sortedAllwayMatches.get(type);
//  
// for(TextAnnotation annotation : typeMatches)
// {
// if(printedAnnotations.contains(annotation)) continue;
// Set<TextAnnotation> matchSet = matchSets.get(annotation);
//  	
// writeAnnotationTextSourceHTML(matchesStream, annotation,
// annotationTexts.get(annotation), annotationTextNames.get(annotation));
// matchesStream.println("<p>");
// printAnnotationHTML(matchesStream, annotation,
// annotationTexts.get(annotation));
// if(typeTrivialMatches.contains(annotation) && matcher.returnsTrivials())
// {
// writeAnnotationTextSourceHTML(trivialMatchesStream, annotation,
// annotationTexts.get(annotation), annotationTextNames.get(annotation));
// trivialMatchesStream.println("<p>");
// printAnnotationHTML(trivialMatchesStream, annotation,
// annotationTexts.get(annotation));
// }
// else if(typeNontrivialMatches.contains(annotation) &&
// matcher.returnsTrivials())
// {
// writeAnnotationTextSourceHTML(nontrivialMatchesStream, annotation,
// annotationTexts.get(annotation), annotationTextNames.get(annotation));
// nontrivialMatchesStream.println("<p>");
// printAnnotationHTML(nontrivialMatchesStream, annotation,
// annotationTexts.get(annotation));
// }
//  	
// printedAnnotations.add(annotation);
// for(TextAnnotation matchedAnnotation : matchSet)
// {
// if(!matchedAnnotation.equals(annotation))
// {
// printAnnotationHTML(matchesStream, matchedAnnotation,
// annotationTexts.get(matchedAnnotation));
// if(typeTrivialMatches.contains(matchedAnnotation) &&
// matcher.returnsTrivials())
// {
// printAnnotationHTML(trivialMatchesStream, matchedAnnotation,
// annotationTexts.get(matchedAnnotation));
// }
// else if(typeNontrivialMatches.contains(matchedAnnotation) &&
// matcher.returnsTrivials())
// {
// printAnnotationHTML(nontrivialMatchesStream, matchedAnnotation,
// annotationTexts.get(matchedAnnotation));
// }
// printedAnnotations.add(matchedAnnotation);
// }
// }
// }
// }
// matchesStream.flush(); matchesStream.close();
// trivialMatchesStream.flush(); trivialMatchesStream.close();
// nontrivialMatchesStream.flush(); nontrivialMatchesStream.close();
//
