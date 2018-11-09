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
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.*;

public class IAA2HTML {

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

		PrintStream tabular = new PrintStream(new File(directory, fileName + ".dat"));
		PrintStream html = new PrintStream(new File(directory, fileName + ".html"));

		initHTML(html, matcher.getName(), null, null, matcher.getDescription());

		printIntro(html, iaa, numberOfDocs, fileName, matcher);
		html.println("<p>");
		printTitleRowForAllwayIAA(html, matcher);

		tabular.println(
				"This file is provided to facilitate cut-n-paste into a spreadsheet.\n"
						+ "If you cannot directly copy the data below into a spreadsheet without it all going into a single cell,\n"
						+ "then try copying to a text editor first and then copy it from there.  There is typically a 'paste special'\n"
						+ "option under the Edit menu that will allow you to paste the copied data as text.  This will also work.\n\n\n");

		if (matcher.returnsTrivials())
			tabular.println(
					"type\tmatches\ttrivial matches\tnon-trivial matches\tnon-matches\ttrivial non-matches\tnon-trivial non-matches");
		else tabular.println("type\tmatches\tnon-matches");

		Set<String> classes = iaa.getAnnotationClasses();
		Set<String> sets = iaa.getSetNames();

		Map<String, Set<ConceptAnnotation>> allwayMatches = iaa.getAllwayMatches();
		Map<String, Set<ConceptAnnotation>> nontrivialAllwayMatches = iaa.getNontrivialAllwayMatches();
		Map<String, Set<ConceptAnnotation>> trivialAllwayMatches = iaa.getTrivialAllwayMatches();
		Map<String, Set<ConceptAnnotation>> allwayNonmatches = iaa.getAllwayNonmatches();
		Map<String, Set<ConceptAnnotation>> nontrivialAllwayNonmatches = iaa.getNontrivialAllwayNonmatches();
		Map<String, Set<ConceptAnnotation>> trivialAllwayNonmatches = iaa.getTrivialAllwayNonmatches();

		Set<ConceptAnnotation> allwayMatchesSingleSet = getSingleSet(allwayMatches);
		Set<ConceptAnnotation> trivialAllwayMatchesSingleSet = getSingleSet(trivialAllwayMatches);
		Set<ConceptAnnotation> nontrivialAllwayMatchesSingleSet = getSingleSet(nontrivialAllwayMatches);
		Set<ConceptAnnotation> allwayNonmatchesSingleSet = getSingleSet(allwayNonmatches);
		Set<ConceptAnnotation> trivialAllwayNonmatchesSingleSet = getSingleSet(trivialAllwayNonmatches);
		Set<ConceptAnnotation> nontrivialAllwayNonmatchesSingleSet = getSingleSet(nontrivialAllwayNonmatches);

		AnnotationSpanIndex spanIndex = new AnnotationSpanIndex(allwayNonmatchesSingleSet);

		int totalAllwayMatches = allwayMatchesSingleSet.size();
		int totalTrivialAllwayMatches = trivialAllwayMatchesSingleSet.size();
		int totalNontrivialAllwayMatches = nontrivialAllwayMatchesSingleSet.size();
		int totalAllwayNonmatches = allwayNonmatchesSingleSet.size();
		int totalTrivialAllwayNonmatches = trivialAllwayNonmatchesSingleSet.size();
		int totalNontrivialAllwayNonmatches = nontrivialAllwayNonmatchesSingleSet.size();

		double iaaScore =
				(double) totalAllwayMatches
						/ ((double) totalAllwayMatches + (double) totalAllwayNonmatches);
		double stingyIAAScore =
				(double) totalNontrivialAllwayMatches
						/ ((double) totalAllwayMatches + (double) totalAllwayNonmatches);
		double respectableIAAScore =
				(double) totalNontrivialAllwayMatches
						/ ((double) totalNontrivialAllwayMatches + (double) totalAllwayNonmatches);
		double nontrivialIAAScore =
				(double) totalNontrivialAllwayMatches
						/ ((double) totalNontrivialAllwayMatches + (double) totalNontrivialAllwayNonmatches);

		if (matcher.returnsTrivials()) {
			html.println(
					"<tr><td><b>All classes</b></td>"
							+ "<td>"
							+ percentageFormat.format(iaaScore)
							+ "</td>"
							+ "<td>"
							+ percentageFormat.format(stingyIAAScore)
							+ "</td>"
							+ "<td>"
							+ percentageFormat.format(respectableIAAScore)
							+ "</td>"
							+ "<td>"
							+ percentageFormat.format(nontrivialIAAScore)
							+ "</td>"
							+ "<td>"
							+ totalAllwayMatches
							+ "</td>"
							+ "<td>"
							+ totalTrivialAllwayMatches
							+ "</td>"
							+ "<td>"
							+ totalNontrivialAllwayMatches
							+ "</td>"
							+ "<td>"
							+ totalAllwayNonmatches
							+ "</td>"
							+ "<td>"
							+ totalTrivialAllwayNonmatches
							+ "</td>"
							+ "<td>"
							+ totalNontrivialAllwayNonmatches
							+ "</td></tr>");
			tabular.println(
					"All classes\t"
							+ totalAllwayMatches
							+ "\t"
							+ totalTrivialAllwayMatches
							+ "\t"
							+ totalNontrivialAllwayMatches
							+ "\t"
							+ totalAllwayNonmatches
							+ "\t"
							+ totalTrivialAllwayNonmatches
							+ "\t"
							+ totalNontrivialAllwayNonmatches);
		} else {
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
			tabular.println("All classes\t" + totalAllwayMatches + "\t" + totalAllwayNonmatches);
		}

		Map<String, Set<ConceptAnnotation>> sortedAllwayMatches = sortByType(classes, allwayMatchesSingleSet);
		Map<String, Set<ConceptAnnotation>> sortedAllwayTrivialMatches =
				sortByType(classes, trivialAllwayMatchesSingleSet);
		Map<String, Set<ConceptAnnotation>> sortedAllwayNontrivialMatches =
				sortByType(classes, nontrivialAllwayMatchesSingleSet);
		Map<String, Set<ConceptAnnotation>> sortedAllwayNonmatches =
				sortByType(classes, allwayNonmatchesSingleSet);
		Map<String, Set<ConceptAnnotation>> sortedAllwayTrivialNonmatches =
				sortByType(classes, trivialAllwayNonmatchesSingleSet);
		Map<String, Set<ConceptAnnotation>> sortedAllwayNontrivialNonmatches =
				sortByType(classes, nontrivialAllwayNonmatchesSingleSet);

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
			stingyIAAScore =
					(double) classNontrivialMatches / ((double) classMatches + (double) classNonmatches);
			respectableIAAScore =
					(double) classNontrivialMatches
							/ ((double) classNontrivialMatches + (double) classNonmatches);
			nontrivialIAAScore =
					(double) classNontrivialMatches
							/ ((double) classNontrivialMatches + (double) classNontrivialNonmatches);

			if (matcher.returnsTrivials()) {
				html.println(
						"<tr><td>"
								+ type
								+ "</td>"
								+ "<td>"
								+ percentageFormat.format(iaaScore)
								+ "</td>"
								+ "<td>"
								+ percentageFormat.format(stingyIAAScore)
								+ "</td>"
								+ "<td>"
								+ percentageFormat.format(respectableIAAScore)
								+ "</td>"
								+ "<td>"
								+ percentageFormat.format(nontrivialIAAScore)
								+ "</td>"
								+ "<td>"
								+ classMatches
								+ "</td>"
								+ "<td>"
								+ classTrivialMatches
								+ "</td>"
								+ "<td>"
								+ classNontrivialMatches
								+ "</td>"
								+ "<td>"
								+ classNonmatches
								+ "</td>"
								+ "<td>"
								+ classTrivialNonmatches
								+ "</td>"
								+ "<td>"
								+ classNontrivialNonmatches
								+ "</td></tr>");
				tabular.println(
						type
								+ "\t"
								+ classMatches
								+ "\t"
								+ classTrivialMatches
								+ "\t"
								+ classNontrivialMatches
								+ "\t"
								+ classNonmatches
								+ "\t"
								+ classTrivialNonmatches
								+ "\t"
								+ classNontrivialNonmatches);
			} else {
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
								+ "</td></tr>");
				tabular.println(type + "\t" + classMatches + "\t" + classNonmatches);
			}
		}
		html.println("</table>");

		printMatchData(
				html,
				sets,
				fileName,
				directory,
				allwayMatches,
				classes,
				sortedTypes,
				annotationTexts,
				annotationTextNames,
				matcher,
				trivialAllwayMatches,
				nontrivialAllwayMatches,
				iaa);

		printNonmatchData(
				html,
				sets,
				fileName,
				directory,
				allwayNonmatches,
				classes,
				spanIndex,
				sortedTypes,
				annotationTexts,
				annotationTextNames,
				matcher,
				trivialAllwayNonmatches,
				nontrivialAllwayNonmatches);

		Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseMatches = iaa.getPairwiseMatches();
		Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseNonmatches = iaa.getPairwiseNonmatches();

		printPairwiseAgreement(html, sets, pairwiseMatches, pairwiseNonmatches, percentageFormat);

		html.flush();
		html.close();
		tabular.flush();
		tabular.close();
	}

	private static void printMatchData(
			PrintStream html,
			Set<String> sets,
			String fileName,
			File directory,
			Map<String, Set<ConceptAnnotation>> allwayMatches,
			Set<String> classes,
			List<String> sortedTypes,
			Map<ConceptAnnotation, String> annotationTexts,
			Map<ConceptAnnotation, String> annotationTextNames,
			Matcher matcher,
			Map<String, Set<ConceptAnnotation>> trivialAllwayMatches,
			Map<String, Set<ConceptAnnotation>> nontrivialAllwayMatches,
			IAA iaa)
			throws Exception {
		html.println("<h2>match data</h2>");
		html.println("<ul>");

		Map<ConceptAnnotation, Set<ConceptAnnotation>> matchSets = iaa.getAllwayMatchSets();

		for (String set : sets) {
			String matchesFileName = fileName + ".matches." + set + ".html";
			html.println("<li><a href=\"" + matchesFileName + "\">matches for " + set + "</a></li>");
			PrintStream matchesStream = new PrintStream(new File(directory, matchesFileName));
			Set<ConceptAnnotation> matches = allwayMatches.get(set);
			Map<String, Set<ConceptAnnotation>> sortedMatches = sortByType(classes, matches);

			initHTML(
					matchesStream,
					"Matches for " + set,
					fileName + ".html",
					fileName,
					"Each concept that was considered a match is shown in the text that it was found in.  The matching annotations from the other concept sets are also shown.");
			printInstances(
					matchesStream,
					sortedMatches,
					sortedTypes,
					annotationTexts,
					annotationTextNames,
					matchSets);
			matchesStream.flush();
			matchesStream.close();

			if (matcher.returnsTrivials()) {
				String trivialMatchesFileName = fileName + ".trivial.matches." + set + ".html";
				html.println(
						"<li><a href=\""
								+ trivialMatchesFileName
								+ "\">trivial matches for "
								+ set
								+ "</a></li>");
				PrintStream trivialMatchesStream =
						new PrintStream(new File(directory, trivialMatchesFileName));
				Set<ConceptAnnotation> trivialMatches = trivialAllwayMatches.get(set);
				Map<String, Set<ConceptAnnotation>> sortedTrivialMatches = sortByType(classes, trivialMatches);
				initHTML(
						trivialMatchesStream,
						"Trivial matches for " + set,
						fileName + ".html",
						fileName,
						"Each concept that was considered a trival match is shown in the text that it was found in.  The matching annotations from the other concept sets are also shown.");
				printInstances(
						trivialMatchesStream,
						sortedTrivialMatches,
						sortedTypes,
						annotationTexts,
						annotationTextNames,
						matchSets);
				trivialMatchesStream.flush();
				trivialMatchesStream.close();

				String nontrivialMatchesFileName = fileName + ".nontrivial.matches." + set + ".html";
				html.println(
						"<li><a href=\""
								+ nontrivialMatchesFileName
								+ "\">non-trivial matches for "
								+ set
								+ "</a></li>");
				PrintStream nontrivialMatchesStream =
						new PrintStream(new File(directory, nontrivialMatchesFileName));
				Set<ConceptAnnotation> nontrivialMatches = nontrivialAllwayMatches.get(set);
				Map<String, Set<ConceptAnnotation>> sortedNontrivialMatches =
						sortByType(classes, nontrivialMatches);
				initHTML(
						nontrivialMatchesStream,
						"non-trivial non-matches for " + set,
						fileName + ".html",
						fileName,
						"Each concept that was considered a non-trival match is shown in the text that it was found in.  The matching from the other concept sets are also shown.");
				printInstances(
						nontrivialMatchesStream,
						sortedNontrivialMatches,
						sortedTypes,
						annotationTexts,
						annotationTextNames,
						matchSets);
				nontrivialMatchesStream.flush();
				nontrivialMatchesStream.close();
			}
		}
		html.println("</ul><hr>");
	}

	private static void printNonmatchData(
			PrintStream html,
			Set<String> sets,
			String fileName,
			File directory,
			Map<String, Set<ConceptAnnotation>> allwayNonmatches,
			Set<String> classes,
			AnnotationSpanIndex spanIndex,
			List<String> sortedTypes,
			Map<ConceptAnnotation, String> annotationTexts,
			Map<ConceptAnnotation, String> annotationTextNames,
			Matcher matcher,
			Map<String, Set<ConceptAnnotation>> trivialAllwayNonmatches,
			Map<String, Set<ConceptAnnotation>> nontrivialAllwayNonmatches)
			throws Exception {
		html.println("<h2>non-match data</h2>");
		html.println("<ul>");

		for (String set : sets) {
			String errorsFileName = fileName + ".nonmatches." + set + ".html";
			html.println("<li><a href=\"" + errorsFileName + "\">non-matches for " + set + "</a></li>");
			PrintStream errors = new PrintStream(new File(directory, errorsFileName));
			Set<ConceptAnnotation> nonmatches = allwayNonmatches.get(set);
			Map<String, Set<ConceptAnnotation>> sortedNonmatches = sortByType(classes, nonmatches);

			Map<ConceptAnnotation, Set<ConceptAnnotation>> comparisonAnnotations = new HashMap<>();
			for (ConceptAnnotation nonmatch : nonmatches) {
				comparisonAnnotations.put(nonmatch, getCandidateAnnotations(nonmatch, spanIndex));
			}

			initHTML(
					errors,
					"Non-matches for " + set,
					fileName + ".html",
					fileName,
					"Each concept that was considered a non-match is shown in the text that it was found in.  Overlapping annotations from the other concept sets are also shown.");
			printInstances(
					errors,
					sortedNonmatches,
					sortedTypes,
					annotationTexts,
					annotationTextNames,
					comparisonAnnotations);
			errors.flush();
			errors.close();

			if (matcher.returnsTrivials()) {
				String trivialNonMatchesFileName = fileName + ".trivial.nonmatches." + set + ".html";
				html.println(
						"<li><a href=\""
								+ trivialNonMatchesFileName
								+ "\">trivial non-matches for "
								+ set
								+ "</a></li>");
				PrintStream trivialErrors = new PrintStream(new File(directory, trivialNonMatchesFileName));
				Set<ConceptAnnotation> trivialNonmatches = trivialAllwayNonmatches.get(set);
				Map<String, Set<ConceptAnnotation>> sortedTrivialNonmatches =
						sortByType(classes, trivialNonmatches);
				initHTML(
						trivialErrors,
						"Trivial non-matches for " + set,
						fileName + ".html",
						fileName,
						"Each concept that was considered a trival non-match is shown in the text that it was found in.  Overlapping annotations from the other concept sets are also shown.");
				printInstances(
						trivialErrors,
						sortedTrivialNonmatches,
						sortedTypes,
						annotationTexts,
						annotationTextNames,
						comparisonAnnotations);
				trivialErrors.flush();
				trivialErrors.close();

				String nontrivialNonMatchesFileName = fileName + ".nontrivial.nonmatches." + set + ".html";
				html.println(
						"<li><a href=\""
								+ nontrivialNonMatchesFileName
								+ "\">non-trivial non-matches for "
								+ set
								+ "</a></li>");
				PrintStream nontrivialErrors =
						new PrintStream(new File(directory, nontrivialNonMatchesFileName));
				Set<ConceptAnnotation> nontrivialNonmatches = nontrivialAllwayNonmatches.get(set);
				Map<String, Set<ConceptAnnotation>> sortedNontrivialNonmatches =
						sortByType(classes, nontrivialNonmatches);
				initHTML(
						nontrivialErrors,
						"non-trivial non-matches for " + set,
						fileName + ".html",
						fileName,
						"Each concept that was considered a non-trival non-match is shown in the text that it was found in.  Overlapping annotations from the other concept sets are also shown.");
				printInstances(
						nontrivialErrors,
						sortedNontrivialNonmatches,
						sortedTypes,
						annotationTexts,
						annotationTextNames,
						comparisonAnnotations);
				nontrivialErrors.flush();
				nontrivialErrors.close();
			}
		}
		html.println("</ul><hr>");
	}

	private static Set<ConceptAnnotation> getCandidateAnnotations(
            ConceptAnnotation conceptAnnotation, AnnotationSpanIndex spanIndex) {
		Set<ConceptAnnotation> candidateConceptAnnotations = new HashSet<>();
		String set = conceptAnnotation.getAnnotator().getId();
		String docID = conceptAnnotation.getTextSource().getId();

		Set<ConceptAnnotation> overlappingConceptAnnotations = spanIndex.getOverlappingAnnotations(conceptAnnotation);
		for (ConceptAnnotation overlappingConceptAnnotation : overlappingConceptAnnotations) {
			String candidateAnnotationSet = overlappingConceptAnnotation.getAnnotator().getId();
			if (!candidateAnnotationSet.equals(set)) {
				String candidateDocID = overlappingConceptAnnotation.getTextSource().getId();
				if (candidateDocID.equals(docID)) {
					candidateConceptAnnotations.add(overlappingConceptAnnotation);
				}
			}
		}
		return candidateConceptAnnotations;
	}

	private static void printInstances(
			PrintStream out,
			Map<String, Set<ConceptAnnotation>> sortedAnnotations,
			List<String> sortedTypes,
			Map<ConceptAnnotation, String> annotationTexts,
			Map<ConceptAnnotation, String> annotationTextNames,
			Map<ConceptAnnotation, Set<ConceptAnnotation>> comparisonAnnotations) {
		for (String type : sortedTypes) {
			out.println("<h2>" + type + "</h2>");
			Set<ConceptAnnotation> typeConceptAnnotations = sortedAnnotations.get(type);
			for (ConceptAnnotation conceptAnnotation : typeConceptAnnotations) {
				writeAnnotationTextSourceHTML(
						out, conceptAnnotation, annotationTexts.get(conceptAnnotation), annotationTextNames.get(conceptAnnotation));
				out.println("<ul><li>");
				printAnnotationHTML(out, conceptAnnotation, annotationTexts.get(conceptAnnotation));

				Set<ConceptAnnotation> comparisons = comparisonAnnotations.get(conceptAnnotation);
				if (comparisons != null) {
					for (ConceptAnnotation comparisonConceptAnnotation : comparisons) {
						if (!comparisonConceptAnnotation.equals(conceptAnnotation)) {
							out.println("<li>");
							printAnnotationHTML(
									out, comparisonConceptAnnotation, annotationTexts.get(comparisonConceptAnnotation));
						}
					}
				}
				out.println("</ul>");
			}
		}
	}

	static Set<ConceptAnnotation> getSingleSet(Map<String, Set<ConceptAnnotation>> annotations) {
		Set<ConceptAnnotation> returnValues = new HashSet<>();
		for (String setName : annotations.keySet()) {
			returnValues.addAll(annotations.get(setName));
		}
		return returnValues;
	}

	private static void initHTML(
			PrintStream html, String title, String link, String linkLabel, String description) {
		html.println("<html>");
		html.println("<head><title>" + title + "</title></head>");
		html.println("<body>");
		if (link != null) html.println("<a href=\"" + link + "\">" + linkLabel + "</a>");
		html.println("<h1>" + title + "</h1>");
		html.println(description);
		html.println("<hr>");
	}

	static Map<String, Set<ConceptAnnotation>> sortByType(
			Set<String> types, Collection<ConceptAnnotation> conceptAnnotations) {
		Map<String, Set<ConceptAnnotation>> sortedAnnotations = new HashMap<>();

		for (String type : types) {
			sortedAnnotations.put(type, new HashSet<>());
		}
		for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
			String type = conceptAnnotation.getOwlClassLabel();
			sortedAnnotations.get(type).add(conceptAnnotation);
		}
		return sortedAnnotations;
	}

	private static void writeAnnotationTextSourceHTML(
            PrintStream out, ConceptAnnotation conceptAnnotation, String annotationText, String annotationTextName) {
		StringBuilder html = new StringBuilder("<hr><p>");
		if (annotationTextName != null)
			html.append("Text source docID = ").append(annotationTextName).append("<p>");

		if (annotationText != null) {
			TreeSet<Span> spans = conceptAnnotation.getSpanCollection().getCollection();
			List<Span> modifiedSpans = new ArrayList<>(spans);

			annotationText = shortenText(annotationText, modifiedSpans);

			int mark = 0;

			for (Span span : modifiedSpans) {
				try {
					//noinspection RedundantStringOperation
					html.append(annotationText.substring(mark, span.getStart())).append("<b>");
					html.append(Span.substring(annotationText, span)).append("</b>");
					mark = span.getEnd();
				} catch (StringIndexOutOfBoundsException sioobe) {
					sioobe.printStackTrace();
				}
			}
			if (mark < annotationText.length()) html.append(annotationText.substring(mark));
		}
		out.println(html.toString());
	}

	private static String shortenText(String text, java.util.List<Span> spans) {
		int frontBuffer = 150;
		int endBuffer = 150;
		if (spans.size() > 0) {
			Span span = spans.get(0);
			int start = Math.max(0, span.getStart() - frontBuffer);
			int end = Math.min(text.length(), span.getEnd() + endBuffer);
			String substring = text.substring(start, end);

			for (int i = 0; i < spans.size(); i++) {
				span = spans.get(i);
				Span offsetSpan = new Span(span.getStart() - start, span.getEnd() - start);
				spans.set(i, offsetSpan);
			}
			return substring;
		}
		return text;
	}

	private static void printAnnotationHTML(
            PrintStream out, ConceptAnnotation conceptAnnotation, String annotationText) {
		StringBuilder html = new StringBuilder();

		if (annotationText != null) {
			String coveredText = IAA.getCoveredText(conceptAnnotation, annotationText, " ... ");
			html.append(coveredText);
		}
		html.append("  ").append(conceptAnnotation.toHTML());
		out.print(html.toString());
	}

	private static void printIntro(
			PrintStream html, IAA iaa, int numberOfDocs, String fileName, Matcher matcher) {
		html.println(
				"<p>For more detailed documentation on IAA please see the <a href=\"http://knowtator.sourceforge.net//iaa.shtml\">"
						+ "IAA documentation</a>.");

		html.println("<p>");
		html.println("<h2>" + iaa.getSetNames().size() + "-way IAA Results</h2>");
		html.println("IAA calculated on " + numberOfDocs + " documents.");
		html.println("<p><a href=\"" + fileName + ".dat\">tabular data</a>");
		html.println("<p>all annotations = matches + non-matches");
		html.println("<br> IAA = matches / all annotations");
		if (matcher.returnsTrivials()) {
			html.println("<br>stingy IAA = non-trivial matches / (matches + non-matches)");
			html.println(
					"<br>respectable IAA = non-trivial matches / (non-trivial matches + non-matches)");
			html.println(
					"<br>non-trivial IAA = non-trivial matches / (non-trivial matches + non-trivial non-matches)");
		}
	}

	private static void printTitleRowForAllwayIAA(PrintStream html, Matcher matcher) {
		html.println("<table border=1><tr><td><b>Type</b></td>" + "<td><b>IAA</b></td>");
		if (matcher.returnsTrivials()) {
			html.println(
					"<td><b>stingy IAA</b></td>"
							+ "<td><b>respectable IAA</b></td>"
							+ "<td><b>non-trivial IAA</b></td>");
		}
		html.println("<td><b>matches</b></td>");
		if (matcher.returnsTrivials()) {
			html.println("<td><b>trivial matches</b></td>" + "<td><b>non-trivial matches</b></td>");
		}
		html.println("<td><b>non-matches</b></td>");
		if (matcher.returnsTrivials()) {
			html.println(
					"<td><b>trivial non-matches</b></td>" + "<td><b>non-trivial non-matches</b></td>");
		}
		html.println("</tr>");
	}

	static String initHTML(String title, String description) {
		return "<html>\n"
				+ "<head><title>"
				+ title
				+ "</title></head>\n"
				+ "<body>\n"
				+ "<h1>"
				+ title
				+ "</h1>\n"
				+ description
				+ " For more detailed documentation on IAA please see the <a href=\"http://knowtator.sourceforge.net//iaa.shtml\">IAA documentation</a>.\n";
	}

	static void printMatchData(
			PrintStream html,
			Set<String> sets,
			String fileName,
			File directory,
			Map<String, Set<ConceptAnnotation>> allwayMatches,
			Map<ConceptAnnotation, String> annotationTexts,
			Map<ConceptAnnotation, String> annotationTextNames,
			Set<String> classes,
			IAA iaa)
			throws IOException {

		html.println("<h2>match data</h2>");
		html.println("<ul>");

		Map<ConceptAnnotation, Set<ConceptAnnotation>> matchSets = iaa.getAllwayMatchSets();
		List<String> sortedTypes = new ArrayList<>(classes);
		Collections.sort(sortedTypes);

		for (String set : sets) {
			String matchesFileName = fileName + ".matches." + set + ".html";
			html.println("<li><a href=\"" + matchesFileName + "\">matches for " + set + "</a></li>");
			PrintStream matchesStream = new PrintStream(new File(directory, matchesFileName));
			Set<ConceptAnnotation> matches = allwayMatches.get(set);
			Map<String, Set<ConceptAnnotation>> sortedMatches = IAA2HTML.sortByType(classes, matches);

			matchesStream.println(
					initHTML(
							"Matches for " + set,
							"Each concept that was considered a match is shown in the text that it was found in.  The matching annotations from the other concept sets are also shown."));
			IAA2HTML.printInstances(
					matchesStream,
					sortedMatches,
					sortedTypes,
					annotationTexts,
					annotationTextNames,
					matchSets);
			matchesStream.flush();
			matchesStream.close();
		}
		html.println("</ul><hr>");
	}

	static void printNonmatchData(
			PrintStream html,
			Set<String> sets,
			String fileName,
			File directory,
			Map<String, Set<ConceptAnnotation>> allwayNonmatches,
			AnnotationSpanIndex spanIndex,
			Map<ConceptAnnotation, String> annotationTexts,
			Map<ConceptAnnotation, String> annotationTextNames,
			Set<String> classes)
			throws IOException {
		html.println("<h2>non-match data</h2>");
		html.println("<ul>");

		List<String> sortedTypes = new ArrayList<>(classes);
		Collections.sort(sortedTypes);

		for (String set : sets) {
			String errorsFileName = fileName + ".nonmatches." + set + ".html";
			html.println("<li><a href=\"" + errorsFileName + "\">non-matches for " + set + "</a></li>");
			PrintStream errors = new PrintStream(new File(directory, errorsFileName));
			Set<ConceptAnnotation> nonmatches = allwayNonmatches.get(set);
			Map<String, Set<ConceptAnnotation>> sortedNonmatches = IAA2HTML.sortByType(classes, nonmatches);

			Map<ConceptAnnotation, Set<ConceptAnnotation>> comparisonAnnotations = new HashMap<>();
			for (ConceptAnnotation nonmatch : nonmatches) {
				comparisonAnnotations.put(nonmatch, IAA2HTML.getCandidateAnnotations(nonmatch, spanIndex));
			}

			errors.println(
					initHTML(
							"Non-matches for " + set,
							"Each concept that was considered a non-match is shown in the text that it was found in.  Overlapping annotations from the other concept sets are also shown."));
			IAA2HTML.printInstances(
					errors,
					sortedNonmatches,
					sortedTypes,
					annotationTexts,
					annotationTextNames,
					comparisonAnnotations);
			errors.flush();
			errors.close();
		}
		html.println("</ul><hr>");
	}

	static void printPairwiseAgreement(
			PrintStream html,
			Set<String> sets,
			Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseMatches,
			Map<String, Map<String, Set<ConceptAnnotation>>> pairwiseNonmatches,
			NumberFormat percentageFormat) {
		html.println("<h2>Pair-wise agreement</h2>");
		html.println(
				"<table border=1><tr><td><b>Gold standard set</b></td>"
						+ "<td><b>compared set</b></td>"
						+ "<td><b>true positives</b></td>"
						+ "<td><b>false positives</b></td>"
						+ "<td><b>false negatives</b></td>"
						+ "<td><b>precision</b></td>"
						+ "<td><b>recall</b></td>"
						+ "<td><b>F-score</b></td></tr>");

		for (String setName : sets) {
			for (String setName2 : sets) {

				if (!setName.equals(setName2)) {
					Set<ConceptAnnotation> truePositives = pairwiseMatches.get(setName).get(setName2);
					Set<ConceptAnnotation> falseNegatives = pairwiseNonmatches.get(setName).get(setName2);
					Set<ConceptAnnotation> falsePositives = pairwiseNonmatches.get(setName2).get(setName);
					double precision =
							(double) truePositives.size()
									/ ((double) truePositives.size() + (double) falsePositives.size());
					double recall =
							(double) truePositives.size()
									/ ((double) truePositives.size() + (double) falseNegatives.size());
					double f_score = ((double) 2 * precision * recall) / (recall + precision);

					html.println(
							"<tr><td>"
									+ setName
									+ "</td>"
									+ "<td>"
									+ setName2
									+ "</td>"
									+ "<td>"
									+ truePositives.size()
									+ "</td>"
									+ "<td>"
									+ falsePositives.size()
									+ "</td>"
									+ "<td>"
									+ falseNegatives.size()
									+ "</td>"
									+ "<td>"
									+ percentageFormat.format(precision)
									+ "</td>"
									+ "<td>"
									+ percentageFormat.format(recall)
									+ "</td>"
									+ "<td>"
									+ percentageFormat.format(f_score)
									+ "</td></tr>");
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
// Set<concept> typeNonmatches = sortedNonmatches.get(type);
// for(concept concept : typeNonmatches)
// {
// String docID = concept.getId();
// writeAnnotationTextSourceHTML(errors, concept,
// annotationTexts.get(concept), annotationTextNames.get(concept));
// errors.println("<ul><li>");
// printAnnotationHTML(errors, concept, annotationTexts.get(concept));
//
// Set<concept> candidateAnnotations =
// spanIndex.getOverlappingConceptAnnotations(concept);
// for(concept candidateAnnotation : candidateAnnotations)
// {
// String candidateAnnotationSet = candidateAnnotation.getId();
// if(!candidateAnnotationSet.equalStartAndEnd(set))
// {
// String candidateDocID = candidateAnnotation.getId();
// if(candidateDocID.equalStartAndEnd(docID))
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
// html.println("<li><a href=\""+matchesFileName+"\">matches for "+set+"</a></li>"
// );
// PrintStream matchesStream = new PrintStream(new File(directory,
// matchesFileName));
// Set<concept> matches = allwayMatches.get(set);
// Map<String, Set<concept>> sortedMatches = sortByType(classes, matches);
// Map<concept, Set<concept>> matchSets = new HashMap<concept,
// Set<concept>>();
// for(concept nonmatch : nonmatches)
// {
// comparisonAnnotations.put(nonmatch, getCandidateAnnotations(nonmatch,
// spanIndex));
// }
//
// initHTML(errors, "Non-matches for "+set, fileName+".html", fileName,
// "Each concept that was considered a non-match is shown in the text that it was found in.
// Overlapping annotations from the other concept sets are also shown."
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
// Map<concept, Set<concept>> matchSets = iaa.getAllwayMatchSets();
// initHTML(matchesStream, "Matches", fileName+".html", fileName,
// "Each concept that was considered a match is shown in the text that it was found in.
// Annotations from each of concept sets are shown because there may be differences in the
// individual annotations if the match criteria ignored those differences.  Only one of the
// concept's spans are bolded in the text."
// );
// if(matcher.returnsTrivials())
// {
// initHTML(trivialMatchesStream, "Trivial matches", fileName+".html", fileName,
// "Each concept that was considered a match is shown in the text that it was found in.
// Annotations from each of concept sets are shown because there may be differences in the
// individual annotations if the match criteria ignored those differences.  Only one of the
// concept's spans are bolded in the text."
// );
// initHTML(nontrivialMatchesStream, "Non-trivial matches", fileName+".html",
// fileName,
// "Each concept that was considered a match is shown in the text that it was found in.
// Annotations from each of concept sets are shown because there may be differences in the
// individual annotations if the match criteria ignored those differences.  Only one of the
// concept's Span is bolded in the text."
// );
// }
//
// Set<concept> printedAnnotations = new HashSet<concept>();
// for(String type : sortedTypes)
// {
// matchesStream.println("<h2>"+type+"</h2>");
// trivialMatchesStream.println("<h2>"+type+"</h2>");
// nontrivialMatchesStream.println("<h2>"+type+"</h2>");
//
// Set<concept> typeTrivialMatches = sortedAllwayTrivialMatches.get(type);
// Set<concept> typeNontrivialMatches =
// sortedAllwayNontrivialMatches.get(type);
// Set<concept> typeMatches = sortedAllwayMatches.get(type);
//
// for(concept concept : typeMatches)
// {
// if(printedAnnotations.contains(concept)) continue;
// Set<concept> matchSet = matchSets.get(concept);
//
// writeAnnotationTextSourceHTML(matchesStream, concept,
// annotationTexts.get(concept), annotationTextNames.get(concept));
// matchesStream.println("<p>");
// printAnnotationHTML(matchesStream, concept,
// annotationTexts.get(concept));
// if(typeTrivialMatches.contains(concept) && matcher.returnsTrivials())
// {
// writeAnnotationTextSourceHTML(trivialMatchesStream, concept,
// annotationTexts.get(concept), annotationTextNames.get(concept));
// trivialMatchesStream.println("<p>");
// printAnnotationHTML(trivialMatchesStream, concept,
// annotationTexts.get(concept));
// }
// else if(typeNontrivialMatches.contains(concept) &&
// matcher.returnsTrivials())
// {
// writeAnnotationTextSourceHTML(nontrivialMatchesStream, concept,
// annotationTexts.get(concept), annotationTextNames.get(concept));
// nontrivialMatchesStream.println("<p>");
// printAnnotationHTML(nontrivialMatchesStream, concept,
// annotationTexts.get(concept));
// }
//
// printedAnnotations.add(concept);
// for(concept matchedAnnotation : matchSet)
// {
// if(!matchedAnnotation.equalStartAndEnd(concept))
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
