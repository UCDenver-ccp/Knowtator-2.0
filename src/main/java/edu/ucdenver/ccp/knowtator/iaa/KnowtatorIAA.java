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
package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.ccp.knowtator.iaa.html.IAA2HTML;
import edu.ucdenver.ccp.knowtator.iaa.html.SpanMatcherHTML;
import edu.ucdenver.ccp.knowtator.iaa.matcher.ClassAndSpanMatcher;
import edu.ucdenver.ccp.knowtator.iaa.matcher.ClassMatcher;
import edu.ucdenver.ccp.knowtator.iaa.matcher.FeatureMatcher;
import edu.ucdenver.ccp.knowtator.iaa.matcher.SpanMatcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class KnowtatorIAA {
	File outputDirectory;

	//KnowtatorFilter filter;

	Set<String> textSources;

	//Project project;

	KnowtatorManager manager;

	//KnowtatorProjectUtil kpu;

	TextAnnotationManager textAnnotationManager;

	//MentionUtil mentionUtil;

//	FilterUtil filterUtil;

	Map<TextAnnotation, String> annotationTexts;

	Map<TextAnnotation, String> annotationTextNames;

	Map<String, Set<TextAnnotation>> textSourceAnnotationsMap;

	PrintStream html;

	boolean setNameDeterminedByAnnotators;

	Set<String> setNames;

	@SuppressWarnings("unused")
	public KnowtatorIAA(File outputDirectory,
						//KnowtatorFilter filter,
						//Project project,
						KnowtatorManager manager
						//MentionUtil mentionUtil,
						//FilterUtil filterUtil
						) throws IAAException {

		this.outputDirectory = outputDirectory;
		//this.filter = filter;
		this.textSources = manager.getTextAnnotationManager().getTextAnnotations().keySet();

		this.manager = manager;
		this.textAnnotationManager = manager.getTextAnnotationManager();
		annotationTexts = new HashMap<>();
		annotationTextNames = new HashMap<>();

		setNameDeterminedByAnnotators = manager.getAnnotatorManager().getAnnotatorNames().length > 1;


		initSetNames();
		initTextSourceAnnotations();
		initHTML();
	}

	private void initSetNames() {
		setNames = new HashSet<>(Arrays.asList(manager.getAnnotatorManager().getAnnotatorNames()));
	}

	private void initHTML() throws IAAException {
		try {
			html = new PrintStream(new File(outputDirectory, "index.html"));
			html.println("<html><head><title>Inter-Annotator Agreement</title></head>");
			html.println("<body><ul>");
		} catch (IOException ioe) {
			throw new IAAException(ioe);
		}
	}

	public void closeHTML() {
		html.println("</ul>");
		html.println("</body></html>");
		html.flush();
		html.close();
	}

	private void initTextSourceAnnotations() throws IAAException {
		textSourceAnnotationsMap = new HashMap<>();
		for (String textPane : textSources) {
			Collection<TextAnnotation> tsAnnotations = textAnnotationManager.getTextAnnotations(textPane);
			Set<TextAnnotation> annotations = new HashSet<>(tsAnnotations);
			//tsAnnotations = filterUtil.filterAnnotations(tsAnnotations, filter);
			textSourceAnnotationsMap.put(textPane, annotations);
		}
	}


	@SuppressWarnings("SameParameterValue")
	private static int convertMatchSpans(String matchSpans) throws IAAException {
		switch (matchSpans) {
			case "SpansMatchExactly":
				return TextAnnotation.SPANS_EXACT_COMPARISON;
			case "SpansOverlap":
				return TextAnnotation.SPANS_OVERLAP_COMPARISON;
			case "IgnoreSpans":
				return TextAnnotation.IGNORE_SPANS_COMPARISON;
			default:
				throw new IAAException(
						"TextSpan match criteria of slot matcher must be one of SpansMatchExactly, SpansOverlap, or IgnoreSpans");
		}
	}

	public static FeatureMatcher createFeatureMatcher(String matcherName) throws IAAException {
		FeatureMatcher featureMatcher = new FeatureMatcher(matcherName);

		//TODO: Provide matchClasses option
		featureMatcher.setMatchClasses(true);

		//TODO: Provide options for how to match spans
		featureMatcher.setMatchSpans(convertMatchSpans("SpansMatchExactly"));


//		Collection<SimpleInstance> slotMatchCriteria = (Collection<SimpleInstance>) slotMatcherConfig
//				.getOwnSlotValues(kpu.getSlotMatchCriteriaSlot());
//
//		for (SimpleInstance slotMatchCriterium : slotMatchCriteria) {
//			if (slotMatchCriterium.getDirectType().equals(kpu.getSimpleSlotMatchCriteriaCls())) {
//				Slot slotMatcherSlot = (Slot) slotMatchCriterium.getOwnSlotValue(kpu.getSlotMatcherSlotSlot());
//				featureMatcher.addComparedSimpleFeatures(slotMatcherSlot.getBrowserText());
//			} else if (slotMatchCriterium.getDirectType().equals(kpu.getComplexSlotMatchCriteriaCls())) {
//				Slot slotMatcherSlot = (Slot) slotMatchCriterium.getOwnSlotValue(kpu.getSlotMatcherSlotSlot());
//				Boolean b = (Boolean) slotMatchCriterium.getOwnSlotValue(kpu.getClassMatchCriteriaSlot());
//				boolean matchSlotClasses = b != null ? b.booleanValue() : false;
//
//				String str = (String) slotMatchCriterium.getOwnSlotValue(kpu.getSpanMatchCriteriaSlot());
//				if (str == null)
//					throw new IAAException("Slot matcher must specify how to compare spans of complex slot "
//							+ slotMatcherSlot.getBrowserText());
//				int matchSlotSpans = convertMatchSpans(str);
//
//				Collection<Slot> comparedSimpleSlots = (Collection<Slot>) slotMatchCriterium.getOwnSlotValues(kpu
//						.getSlotMatcherSimpleSlotsSlot());
//				Set<String> comparedSimpleFeatures = new HashSet<>();
//				for (Slot comparedSimpleSlot : comparedSimpleSlots) {
//					comparedSimpleFeatures.add(comparedSimpleSlot.getBrowserText());
//				}
//
//				Boolean propogateTrivialMatch = (Boolean) slotMatchCriterium.getOwnSlotValue(kpu
//						.getPropogateTrivialMatchSlot());
//				boolean trivialSimpleFeatureMatchesCauseTrivialMatch = propogateTrivialMatch != null ? propogateTrivialMatch
//						.booleanValue()
//						: false;
//
//				ComplexFeatureMatchCriteria matchCriteria = new ComplexFeatureMatchCriteria(matchSlotClasses,
//						matchSlotSpans, comparedSimpleFeatures, trivialSimpleFeatureMatchesCauseTrivialMatch);
//				featureMatcher.addComparedComplexFeature(slotMatcherSlot.getBrowserText(), matchCriteria);
//			}
//		}

		return featureMatcher;
	}

	public void runFeatureMatcherIAA() throws IAAException {
		runFeatureMatcherIAA("Feature Matcher");
	}

	public void runFeatureMatcherIAA(String matcherName) throws IAAException {
		try {
			FeatureMatcher featureMatcher = createFeatureMatcher(matcherName);
			IAA featureIAA = new IAA(setNames);
			for (Set<TextAnnotation> annotations : textSourceAnnotationsMap.values()) {
				featureIAA.setAnnotations(annotations);
				featureIAA.allwayIAA(featureMatcher);
				featureIAA.pairwiseIAA(featureMatcher);
			}

			IAA2HTML.printIAA(featureIAA, featureMatcher, outputDirectory, textSources.size(), annotationTexts,
					annotationTextNames);
			html.println("<li><a href=\"" + featureMatcher.getName() + ".html\">" + featureMatcher.getName()
					+ "</a></li>");
		} catch (Exception exception) {
			throw new IAAException(exception);
		}
	}

	public void runClassIAA() throws IAAException {
		try {
			ClassMatcher classMatcher = new ClassMatcher();
			IAA classIAA = new IAA(setNames);

			for (Set<TextAnnotation> annotations : textSourceAnnotationsMap.values()) {
				classIAA.setAnnotations(annotations);
				classIAA.allwayIAA(classMatcher);
				classIAA.pairwiseIAA(classMatcher);
			}

			IAA2HTML.printIAA(classIAA, classMatcher, outputDirectory, textSources.size(), annotationTexts,
					annotationTextNames);
			html.println("<li><a href=\"" + classMatcher.getName() + ".html\">" + classMatcher.getName() + "</a></li>");
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

	public void runSpanIAA() throws IAAException {
		try {
			SpanMatcher spanMatcher = new SpanMatcher();
			IAA spanIAA = new IAA(setNames);

			for (Set<TextAnnotation> annotations : textSourceAnnotationsMap.values()) {
				spanIAA.setAnnotations(annotations);
				spanIAA.allwayIAA(spanMatcher);
				spanIAA.pairwiseIAA(spanMatcher);
			}
			SpanMatcherHTML.printIAA(spanIAA, spanMatcher, outputDirectory, textSources.size(), annotationTexts,
					annotationTextNames);
			html.println("<li><a href=\"" + spanMatcher.getName() + ".html\">" + spanMatcher.getName() + "</a></li>");
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

	public void runClassAndSpanIAA() throws IAAException {
		try {
			ClassAndSpanMatcher classAndSpanMatcher = new ClassAndSpanMatcher();
			IAA classAndSpanIAA = new IAA(setNames);

			for (Set<TextAnnotation> annotations : textSourceAnnotationsMap.values()) {
				classAndSpanIAA.setAnnotations(annotations);
				classAndSpanIAA.allwayIAA(classAndSpanMatcher);
				classAndSpanIAA.pairwiseIAA(classAndSpanMatcher);
			}
			IAA2HTML.printIAA(classAndSpanIAA, classAndSpanMatcher, outputDirectory, textSources.size(),
					annotationTexts, annotationTextNames);
			html.println("<li><a href=\"" + classAndSpanMatcher.getName() + ".html\">" + classAndSpanMatcher.getName()
					+ "</a></li>");
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

//	public void runSubclassIAA() throws IAAException {
//		try {
//			Set<Cls> topLevelClses = getTopLevelClses();
//			Set<Cls> parentClses = new HashSet<Cls>();
//			for (Cls topLevelCls : topLevelClses) {
//				parentClses.add(topLevelCls);
//				Collection subclasses = topLevelCls.getSubclasses();
//				if (subclasses != null) {
//					Iterator subclassesItr = subclasses.iterator();
//					while (subclassesItr.hasNext()) {
//						Cls subclass = (Cls) subclassesItr.next();
//						Collection subsubclasses = subclass.getSubclasses();
//						if (subsubclasses != null && subsubclasses.size() > 0) {
//							parentClses.add(subclass);
//						}
//					}
//				}
//			}
//
//			html.println("<li><a href=\"subclassMatcher.html\">subclass matcher</a></li>");
//
//			PrintStream subclassHTML = new PrintStream(new File(outputDirectory, "subclassMatcher.html"));
//			subclassHTML.println(IAA2HTML.initHTML("Subclass Matcher", ""));
//			subclassHTML.println("Subclass matcher");
//			subclassHTML.println("<table border=1>\n");
//			subclassHTML
//					.println("<tr><td><b>Class</b></td><td><b>IAA</b></td><td><b>matches</b></td><td><b>non-matches</b></td></tr>");
//
//			SubclassMatcher subclassMatcher = new SubclassMatcher(createClassHierarchy(topLevelClses));
//			IAA subclassIAA = new IAA(setNames);
//
//			NumberFormat percentageFormat = NumberFormat.getPercentInstance();
//			percentageFormat.setMinimumFractionDigits(2);
//
//			for (Cls parentCls : parentClses) {
//				calculateSubclassIAA(parentCls, subclassMatcher, subclassIAA, textSourceAnnotationsMap);
//				SubclassMatcherHTML.printIAA(subclassIAA, subclassMatcher, outputDirectory, textSources.size(),
//						annotationTexts, annotationTextNames);
//
//				Map<String, Set<TextAnnotation>> allwayMatches = subclassIAA.getNontrivialAllwayMatches();
//				Set<TextAnnotation> matches = IAA2HTML.getSingleSet(allwayMatches);
//
//				Map<String, Set<TextAnnotation>> allwayNonmatches = subclassIAA.getNontrivialAllwayNonmatches();
//				Set<TextAnnotation> nonmatches = IAA2HTML.getSingleSet(allwayNonmatches);
//
//				double subclsIAA = (double) matches.size() / ((double) matches.size() + (double) nonmatches.size());
//
//				subclassHTML.println("<tr><td><a href=\"" + subclassMatcher.getName() + ".html\">"
//						+ parentCls.getName() + "</a></td>" + "<td>" + percentageFormat.format(subclsIAA) + "</td><td>"
//						+ matches.size() + "</td><td>" + nonmatches.size() + "</td></tr>");
//			}
//			subclassHTML.println("</table>");
//			subclassHTML.println("</body></html>");
//			subclassHTML.flush();
//			subclassHTML.close();
//		} catch (Exception e) {
//			throw new IAAException(e);
//		}
//	}

//	private static void calculateSubclassIAA(Cls cls, SubclassMatcher subclassMatcher,
//			IAA subclassIAA, Map<String, Set<TextAnnotation>> textSourceAnnotationsMap)
//			throwsedu.ucdenver.ccp.knowtator.iaa_original.IAAException {
//		subclassIAA.reset();
//		subclassMatcher.setIAAClass(cls.getName());
//		for (Set<TextAnnotation> annotations : textSourceAnnotationsMap.values()) {
//			subclassIAA.setAnnotations(annotations);
//			subclassIAA.allwayIAA(subclassMatcher);
//			subclassIAA.pairwiseIAA(subclassMatcher);
//		}
//	}

//	public static Set<Slot> getSimpleSlotsFromMatcherConfig(SimpleInstance slotMatcherConfig, KnowtatorProjectUtil kpu) {
//		Set<Slot> returnValues = new HashSet<Slot>();
//
//		Collection<SimpleInstance> slotMatchCriteria = (Collection<SimpleInstance>) slotMatcherConfig
//				.getOwnSlotValues(kpu.getSlotMatchCriteriaSlot());
//
//		for (SimpleInstance slotMatchCriterium : slotMatchCriteria) {
//			if (slotMatchCriterium.getDirectType().equals(kpu.getSimpleSlotMatchCriteriaCls())) {
//				Slot slotMatcherSlot = (Slot) slotMatchCriterium.getOwnSlotValue(kpu.getSlotMatcherSlotSlot());
//				returnValues.add(slotMatcherSlot);
//			} else if (slotMatchCriterium.getDirectType().equals(kpu.getComplexSlotMatchCriteriaCls())) {
//				Collection<Slot> comparedSimpleSlots = (Collection<Slot>) slotMatchCriterium.getOwnSlotValues(kpu
//						.getSlotMatcherSimpleSlotsSlot());
//				if (comparedSimpleSlots != null)
//					returnValues.addAll(comparedSimpleSlots);
//			}
//		}
//		return returnValues;
//	}

//	public static Set<Slot> getComplexSlotsFromMatcherConfig(SimpleInstance slotMatcherConfig, KnowtatorProjectUtil kpu) {
//		Set<Slot> returnValues = new HashSet<Slot>();
//
//		Collection<SimpleInstance> slotMatchCriteria = (Collection<SimpleInstance>) slotMatcherConfig
//				.getOwnSlotValues(kpu.getSlotMatchCriteriaSlot());
//
//		for (SimpleInstance slotMatchCriterium : slotMatchCriteria) {
//			if (slotMatchCriterium.getDirectType().equals(kpu.getComplexSlotMatchCriteriaCls())) {
//				Slot slotMatcherSlot = (Slot) slotMatchCriterium.getOwnSlotValue(kpu.getSlotMatcherSlotSlot());
//				returnValues.add(slotMatcherSlot);
//			}
//		}
//		return returnValues;
//	}

	public static void main(String[] args) {
		KnowtatorManager manager = new KnowtatorManager();
		manager.getXmlUtil().read("file/CHEBI batch 1 IAA/Kristin/11319941.txt.knowtator.xml", true);
		manager.getXmlUtil().read("file/CHEBI batch 1 IAA/Kristin+Mike/11319941.txt.knowtator.xml", true);
		manager.getXmlUtil().write(String.format("%s %s", manager.getConfigProperties().getDefaultSaveLocation(), "11319941.txt.knowtator2.xml"));

		try {
			KnowtatorIAA knowtatorIAA = new KnowtatorIAA(new File(manager.getConfigProperties().getDefaultSaveLocation()), manager);
			knowtatorIAA.runClassIAA();
			knowtatorIAA.closeHTML();
		} catch (IAAException e) {
			e.printStackTrace();
		}



	}

}
