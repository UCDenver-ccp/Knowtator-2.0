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
package edu.ucdenver.cpbs.mechanic.stats;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.iaa.Annotation;
import edu.ucdenver.cpbs.mechanic.iaa.IAA;
import edu.ucdenver.cpbs.mechanic.iaa.IAAException;
import edu.ucdenver.cpbs.mechanic.iaa.html.IAA2HTML;
import edu.ucdenver.cpbs.mechanic.iaa.matcher.*;
import org.semanticweb.owlapi.model.OWLClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.*;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection", "SameParameterValue"})
public class KnowtatorIAA {
	private File outputDirectory;
//TODO Determine what commentented code should be
//	SimpleInstance filter;

//	Collection<SimpleInstance> textSources;

//	Collection<Slot> simpleFeatureSlots;

//	Collection<Slot> complexFeatureSlots;

//	Project project;

//	KnowtatorManager manager;

//	KnowtatorProjectUtil kpu;

//	TextSourceCollection tsc;

//	AnnotationUtil annotationUtil;

//	MentionUtil mentionUtil;

//	FilterUtil filterUtil;

	private Map<Annotation, String> annotationTexts;

	private Map<Annotation, String> annotationTextNames;

	private Map<String, Set<Annotation>> textSourceAnnotationsMap;

	private PrintStream html;

	private boolean setNameDeterminedByAnnotators;

	private Set<String> setNames;
	private MechAnICView view;

	public KnowtatorIAA(File outputDirectory, MechAnICView view) throws IAAException

	{
		this.outputDirectory = outputDirectory;
		this.view = view;
//		this.filter = filter;
//		this.textSources = textSources;
//		if (simpleFeatureSlots != null)
//			this.simpleFeatureSlots = simpleFeatureSlots;
//		else
//			this.simpleFeatureSlots = new HashSet<Slot>();
//		if (complexFeatureSlots != null)
//			this.complexFeatureSlots = complexFeatureSlots;
//		else
//			this.complexFeatureSlots = new HashSet<Slot>();
//
//		this.project = project;
//		this.manager = manager;
//		this.tsc = tsc;
//		this.annotationUtil = annotationUtil;
//		this.mentionUtil = mentionUtil;
//		this.filterUtil = filterUtil;
//
//		kpu = manager.getKnowtatorProjectUtil();
		annotationTexts = new HashMap<>();
		annotationTextNames = new HashMap<>();

//		setNameDeterminedByAnnotators = FilterUtil.getAnnotators(filter).size() > 1 ? true : false;
		initSetNames();
		initTextSourceAnnotations();
		initHTML();
	}

	private void initHTML() throws IAAException {
		try {
			System.out.println("Making IAA HTML in " + outputDirectory);
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

		System.out.println("Closing IAA HTML");
	}

	private void initTextSourceAnnotations() throws IAAException {
		textSourceAnnotationsMap = new HashMap<>();
//		for (SimpleInstance textSourceInstance : textSources) {
//			Collection<SimpleInstance> tsAnnotations = annotationUtil.getAnnotations(textSourceInstance);
//			tsAnnotations = filterUtil.filterAnnotations(tsAnnotations, filter);
//			Set<Annotation> annotations = convertAnnotations(tsAnnotations);
//			textSourceAnnotationsMap.put(textSourceInstance.getName(), annotations);
//		}
	}

	private void initSetNames() {
		setNames = new HashSet<>();
//		Set<SimpleInstance> setNameInstances;
//		if (setNameDeterminedByAnnotators)
//			setNameInstances = new HashSet<SimpleInstance>(FilterUtil.getAnnotators(filter));
//		else
//			setNameInstances = FilterUtil.getSets(filter);
//
//		for (SimpleInstance setNameInstance : setNameInstances) {
//			setNames.add(setNameInstance.getBrowserText());
//		}
	}

//	private String getAnnotationSetName(Annotation knowtatorAnnotation) {
//		if (setNameDeterminedByAnnotators) {
//			String annotatorName = annotationUtil.getAnnotator(knowtatorAnnotation).getBrowserText();
//			return annotatorName;
//		} else {
//			Set<SimpleInstance> sets = annotationUtil.getSets(knowtatorAnnotation);
//			for (SimpleInstance set : sets) {
//				String setName = set.getBrowserText();
//				if (setNames.contains(setName))
//					return setName;
//			}
//		}
//		return null;
//	}

	public Map<Annotation, String> getAnnotationTexts() {
		return annotationTexts;
	}

//	public Annotation convertAnnotation(Annotation knowtatorAnnotation, boolean convertComplexFeatures)
//			throws IAAException {
//		try {
//			Annotation annotation = new Annotation();
//
//			SimpleInstance textSourceInstance = annotationUtil.getTextSource(knowtatorAnnotation);
//			TextSource textSource = tsc.get(textSourceInstance.getName());
//
//			annotationTexts.put(annotation, textSource.getText());
//			annotationTextNames.put(annotation, textSource.getName());
//			annotation.setDocID(textSource.getName());
//
//			List<Span> knowtatorSpans = annotationUtil.getSpans(knowtatorAnnotation);
//			List<Span> iaaSpans = new ArrayList<Span>(knowtatorSpans.size());
//			for (Span knowtatorSpan : knowtatorSpans) {
//				Span iaaSpan = new Span(knowtatorSpan.getStart(), knowtatorSpan
//						.getEnd());
//				iaaSpans.add(iaaSpan);
//			}
//			annotation.setSpans(iaaSpans);
//
//			String annotationSetName = getAnnotationSetName(knowtatorAnnotation);
//			annotation.setSetName(annotationSetName);
//
//			SimpleInstance mention = annotationUtil.getMention(knowtatorAnnotation);
//			Cls mentionType = mentionUtil.getMentionCls(mention);
//			if (mentionType != null)
//				annotation.setAnnotationClass(mentionType.getBrowserText());
//
//			for (Slot simpleFeatureSlot : simpleFeatureSlots) {
//				SimpleInstance slotMention = mentionUtil.getSlotMention(mention, simpleFeatureSlot);
//				if (slotMention != null && mentionUtil.isSimpleSlotMention(slotMention)) {
//					List<Object> values = mentionUtil.getSlotMentionValues(slotMention);
//					Set<Object> valuesSet = new HashSet<>(values);
//					annotation.setSimpleFeature(simpleFeatureSlot.getBrowserText(), valuesSet);
//				}
//				if (slotMention != null && !mentionUtil.isSimpleSlotMention(slotMention)) {
//					throw new IAAException("The slot " + simpleFeatureSlot.getBrowserText()
//							+ " in slot matcher config is not a 'simple' slot.");
//				}
//			}
//
//			if (convertComplexFeatures) {
//				for (Slot complexFeatureSlot : complexFeatureSlots) {
//					List<SimpleInstance> relatedMentions = mentionUtil.getRelatedMentions(mention, complexFeatureSlot);
//					Set<Annotation> featureAnnotations = new HashSet<>();
//					for (SimpleInstance relatedMention : relatedMentions) {
//						SimpleInstance relatedAnnotation = mentionUtil.getMentionAnnotation(relatedMention);
//						featureAnnotations.add(convertAnnotation(relatedAnnotation, false));
//					}
//					annotation.setComplexFeature(complexFeatureSlot.getBrowserText(), featureAnnotations);
//				}
//			}
//			return annotation;
//		} catch (TextSourceAccessException tsae) {
//			throw new IAAException(tsae);
//		}
//	}

//	private Set<Annotation> convertAnnotations(Collection<SimpleInstance> knowtatorAnnotations) throws IAAException {
//		Set<Annotation> annotations = new HashSet<>();
//		for (SimpleInstance knowtatorAnnotation : knowtatorAnnotations) {
//			annotations.add(convertAnnotation(knowtatorAnnotation, true));
//		}
//		return annotations;
//	}

	private static int convertMatchSpans(String matchSpans) throws IAAException {
		switch (matchSpans) {
			case "SpansMatchExactly":
				return Annotation.SPANS_EXACT_COMPARISON;
			case "SpansOverlap":
				return Annotation.SPANS_OVERLAP_COMPARISON;
			case "IgnoreSpans":
				return Annotation.IGNORE_SPANS_COMPARISON;
			default:
				throw new IAAException(
						"Span match criteria of slot matcher must be one of SpansMatchExactly, SpansOverlap, or IgnoreSpans");
		}
	}

//	private static FeatureMatcher createFeatureMatcher(SlotMatcherConfig slotMatcherConfig,
//													   String matcherName) throws IAAException {
//		FeatureMatcher featureMatcher = new FeatureMatcher(matcherName);
//		if (!slotMatcherConfig.getDirectType().equals(kpu.getSlotMatcherConfigCls()))
//			throw new IAAException("Unable to create slot matcher from instance='" + slotMatcherConfig.getBrowserText()
//					+ "'");

//		Boolean matchClasses = (Boolean) slotMatcherConfig.getOwnSlotValue(kpu.getClassMatchCriteriaSlot());
//		if (matchClasses != null)
//			featureMatcher.setMatchClasses(matchClasses.booleanValue());
//		else
//			featureMatcher.setMatchClasses(false);

//		String matchSpans = (String) slotMatcherConfig.getOwnSlotValue(kpu.getSpanMatchCriteriaSlot());
//		if (matchSpans != null) {
//			featureMatcher.setMatchSpans(convertMatchSpans(matchSpans));
//		} else
//			throw new IAAException("Slot matcher must specify how to compare spans.");

//		Collection<SimpleInstance> slotMatchCriteria = (Collection<SimpleInstance>) slotMatcherConfig
//				.getOwnSlotValues(kpu.getSlotMatchCriteriaSlot());

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
//
//		return featureMatcher;
//	}

	public IAA runFeatureMatcherIAA(SlotMatcherConfig slotMatcherConfig) throws IAAException {
		return runFeatureMatcherIAA(slotMatcherConfig, "Feature Matcher");
	}

	private IAA runFeatureMatcherIAA(SlotMatcherConfig slotMatcherConfig, String matcherName) throws IAAException {
		try {
//			FeatureMatcher featureMatcher = createFeatureMatcher(slotMatcherConfig, matcherName);
			IAA featureIAA = new IAA(setNames);
			for (Set<Annotation> annotations : textSourceAnnotationsMap.values()) {
				featureIAA.setAnnotations(annotations);
//				featureIAA.allwayIAA(featureMatcher);
//				featureIAA.pairwiseIAA(featureMatcher);
			}

//			IAA2HTML.printIAA(featureIAA, featureMatcher, outputDirectory, textSources.size(), annotationTexts,
//					annotationTextNames);
//			html.println("<li><a href=\"" + featureMatcher.getName() + ".html\">" + featureMatcher.getName()
//					+ "</a></li>");
			return featureIAA;
		} catch (Exception exception) {
			throw new IAAException(exception);
		}
	}

	public IAA runClassIAA() throws IAAException {
		try {
			ClassMatcher classMatcher = new ClassMatcher();
			IAA classIAA = new IAA(setNames);

			for (Set<Annotation> annotations : textSourceAnnotationsMap.values()) {
				classIAA.setAnnotations(annotations);
				classIAA.allwayIAA(classMatcher);
				classIAA.pairwiseIAA(classMatcher);
			}

//			IAA2HTML.printIAA(classIAA, classMatcher, outputDirectory, textSources.size(), annotationTexts,
//					annotationTextNames);
			html.println("<li><a href=\"" + classMatcher.getName() + ".html\">" + classMatcher.getName() + "</a></li>");
			return classIAA;
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

	public IAA runSpanIAA() throws IAAException {
		try {
			SpanMatcher spanMatcher = new SpanMatcher();
			IAA spanIAA = new IAA(setNames);

			for (Set<Annotation> annotations : textSourceAnnotationsMap.values()) {
				spanIAA.setAnnotations(annotations);
				spanIAA.allwayIAA(spanMatcher);
				spanIAA.pairwiseIAA(spanMatcher);
			}
//			SpanMatcherHTML.printIAA(spanIAA, spanMatcher, outputDirectory, textSources.size(), annotationTexts,
//					annotationTextNames);
			html.println("<li><a href=\"" + spanMatcher.getName() + ".html\">" + spanMatcher.getName() + "</a></li>");
			return spanIAA;
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

	public IAA runClassAndSpanIAA() throws IAAException {
		try {
			ClassAndSpanMatcher classAndSpanMatcher = new ClassAndSpanMatcher();
			IAA classAndSpanIAA = new IAA(setNames);

			for (Set<Annotation> annotations : textSourceAnnotationsMap.values()) {
				classAndSpanIAA.setAnnotations(annotations);
				classAndSpanIAA.allwayIAA(classAndSpanMatcher);
				classAndSpanIAA.pairwiseIAA(classAndSpanMatcher);
			}
//			IAA2HTML.printIAA(classAndSpanIAA, classAndSpanMatcher, outputDirectory, textSources.size(),
//					annotationTexts, annotationTextNames);
			html.println("<li><a href=\"" + classAndSpanMatcher.getName() + ".html\">" + classAndSpanMatcher.getName()
					+ "</a></li>");
			return classAndSpanIAA;
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

	public void runSubclassIAA() throws IAAException {
		try {
			Set<OWLClass> topLevelClses = getTopLevelClses();
			Set<OWLClass> parentClses = new HashSet<>();
			for (OWLClass topLevelCls : topLevelClses) {
				parentClses.add(topLevelCls);
//				Collection subclasses = topLevelCls.getSubclasses();
				Collection<OWLClass> subclasses = view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(topLevelCls);
				if (subclasses != null) {
					for (OWLClass subclass : subclasses) {
						Collection subsubclasses = view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(subclass);
						if (subsubclasses != null && subsubclasses.size() > 0) {
							parentClses.add(subclass);
						}
					}
				}
			}

			html.println("<li><a href=\"subclassMatcher.html\">subclass matcher</a></li>");

			PrintStream subclassHTML = new PrintStream(new File(outputDirectory, "subclassMatcher.html"));
			subclassHTML.println(IAA2HTML.initHTML("Subclass Matcher", ""));
			subclassHTML.println("Subclass matcher");
			subclassHTML.println("<table border=1>\n");
			subclassHTML
					.println("<tr><td><b>Class</b></td><td><b>IAA</b></td><td><b>matches</b></td><td><b>non-matches</b></td></tr>");

			SubclassMatcher subclassMatcher = new SubclassMatcher(createClassHierarchy(topLevelClses));
			IAA subclassIAA = new IAA(setNames);

			NumberFormat percentageFormat = NumberFormat.getPercentInstance();
			percentageFormat.setMinimumFractionDigits(2);

			for (OWLClass parentCls : parentClses) {
				calculateSubclassIAA(parentCls, subclassMatcher, subclassIAA, textSourceAnnotationsMap);
//				SubclassMatcherHTML.printIAA(subclassIAA, subclassMatcher, outputDirectory, textSources.size(),
//						annotationTexts, annotationTextNames);

				Map<String, Set<Annotation>> allwayMatches = subclassIAA.getNontrivialAllwayMatches();
				Set<Annotation> matches = IAA2HTML.getSingleSet(allwayMatches);

				Map<String, Set<Annotation>> allwayNonmatches = subclassIAA.getNontrivialAllwayNonmatches();
				Set<Annotation> nonmatches = IAA2HTML.getSingleSet(allwayNonmatches);

				double subclsIAA = (double) matches.size() / ((double) matches.size() + (double) nonmatches.size());

				subclassHTML.println("<tr><td><a href=\"" + subclassMatcher.getName() + ".html\">"
						+ parentCls.toStringID() + "</a></td>" + "<td>" + percentageFormat.format(subclsIAA) + "</td><td>"
						+ matches.size() + "</td><td>" + nonmatches.size() + "</td></tr>");
			}
			subclassHTML.println("</table>");
			subclassHTML.println("</body></html>");
			subclassHTML.flush();
			subclassHTML.close();
		} catch (Exception e) {
			throw new IAAException(e);
		}
	}

	private static void calculateSubclassIAA(OWLClass cls, SubclassMatcher subclassMatcher,
											 IAA subclassIAA, Map<String, Set<Annotation>> textSourceAnnotationsMap)
			throws IAAException {
		subclassIAA.reset();
		subclassMatcher.setIAAClass(cls.toStringID());
		for (Set<Annotation> annotations : textSourceAnnotationsMap.values()) {
			subclassIAA.setAnnotations(annotations);
			subclassIAA.allwayIAA(subclassMatcher);
			subclassIAA.pairwiseIAA(subclassMatcher);
		}
	}

	private Set<OWLClass> getTopLevelClses() {
		//		Set<OWLClass> topLevelClses = new HashSet<OWLClass>(FilterUtil.getTypes(filter));
//		if (topLevelClses.size() == 0) {
//			topLevelClses.addAll(manager.getRootClses());
//		}
		return view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getRoots();
	}

	private ClassHierarchy createClassHierarchy(Set<OWLClass> topLevelClses) {
		Map<String, Set<String>> subclassMap = new HashMap<>();
		for (OWLClass topLevelCls : topLevelClses) {
			populateSubclassMap(topLevelCls, subclassMap);
		}
		return new ClassHierarchyImpl(subclassMap);
	}

	private void populateSubclassMap(OWLClass cls, Map<String, Set<String>> subclassMap) {
		String clsName = cls.toStringID();
		if (!subclassMap.containsKey(clsName)) {
			Collection<OWLClass> subclses = view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
			if (subclses != null && subclses.size() > 0) {
				subclassMap.put(clsName, new HashSet<>());
				for (OWLClass subcls : subclses) {
					String subclsName = subcls.toStringID();
					subclassMap.get(clsName).add(subclsName);
					populateSubclassMap(subcls, subclassMap);
				}
			}
		}
	}

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

	public Map<String, Set<Annotation>> getTextSourceAnnotationsMap() {
		return textSourceAnnotationsMap;
	}

}
