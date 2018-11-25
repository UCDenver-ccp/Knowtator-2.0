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

package edu.ucdenver.ccp.knowtator.model.text.concept;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.FilterModel;
import edu.ucdenver.ccp.knowtator.model.FilterModelListener;
import edu.ucdenver.ccp.knowtator.model.NoSelectedOWLClassException;
import edu.ucdenver.ccp.knowtator.model.collection.CantRemoveException;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConceptAnnotationCollection extends KnowtatorCollection<ConceptAnnotation> implements OWLOntologyChangeListener, KnowtatorXMLIO, BratStandoffIO, FilterModelListener, OWLModelManagerListener {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ConceptAnnotationCollection.class);

	private final KnowtatorController controller;

	private final TextSource textSource;

	public ConceptAnnotationCollection(KnowtatorController controller, TextSource textSource) {
		super();
		this.controller = controller;
		this.textSource = textSource;

		controller.getOWLModel().addOntologyChangeListener(this);
		controller.getOWLModel().addOWLModelManagerListener(this);
		controller.getFilterModel().addFilterModelListener(this);

	}

    /*
    ADDERS
     */


	/*
	REMOVERS
	 */
	@Override
	public void remove(ConceptAnnotation conceptAnnotationToRemove) throws CantRemoveException {
		for (GraphSpace graphSpace : textSource.getGraphSpaceCollection()) {
			Object[] cells = graphSpace.getVerticesForAnnotation(conceptAnnotationToRemove).toArray();
			graphSpace.removeCells(cells);

		}
		super.remove(conceptAnnotationToRemove);
	}

    /*
    GETTERS
     */

	/**
	 * @param loc Location filter
	 */
	public SpanCollection getSpans(Integer loc) {
		SpanCollection allSpans = new SpanCollection(controller);
		stream().map(conceptAnnotation -> conceptAnnotation
				.getSpanCollection().stream()
				.filter(span -> loc == null || span.contains(loc)).iterator())
				.forEach(spanIterator -> {
					while (spanIterator.hasNext()) {
						allSpans.add(spanIterator.next());
					}
				});


		return allSpans;
	}

	@Override
	public Stream<ConceptAnnotation> stream() {
		boolean filterByOWLClass = controller.getFilterModel().isFilter(FilterModel.OWLCLASS);
		boolean filterByProfile = controller.getFilterModel().isFilter(FilterModel.PROFILE);
		Profile activeProfile = controller.getProfileCollection().getSelection();
		Set<OWLClass> activeOWLClassDescendants = new HashSet<>();

		if (filterByOWLClass) {
			try {
				OWLClass owlClass = controller.getOWLModel().getSelectedOWLClass();
				activeOWLClassDescendants.add(owlClass);
				activeOWLClassDescendants.addAll(controller.getOWLModel().getDescendants(owlClass));
			} catch (NoSelectedOWLClassException ignored) {

			}
		}
		return super.stream()
				.filter(conceptAnnotation -> !filterByOWLClass || activeOWLClassDescendants.contains(conceptAnnotation.getOwlClass()))
				.filter(conceptAnnotation -> !filterByProfile || conceptAnnotation.getAnnotator().equals(activeProfile));
	}

	@Override
	@Nonnull
	public Iterator<ConceptAnnotation> iterator() {

		return stream().iterator();
	}

	public TreeSet<ConceptAnnotation> getAnnotations(int start, int end) {
		Supplier<TreeSet<ConceptAnnotation>> supplier = TreeSet::new;
		return stream()
				.filter(annotation -> (annotation.contains(start) && annotation.contains(end)))
				.collect(Collectors.toCollection(supplier));
	}

//    public void findOverlaps() {
//        List<Span> overlappingSpans = new ArrayList<>();
//        allSpanCollection.forEach(
//                span -> {
//                    List<Span> toRemove = new ArrayList<>();
//                    overlappingSpans.forEach(
//                            span1 -> {
//                                if (span.intersects(span1)) {
//                                    span.getConceptAnnotation().addOverlappingAnnotation(span1.getConceptAnnotation());
//                                    span1.getConceptAnnotation().addOverlappingAnnotation(span.getConceptAnnotation());
//                                } else {
//                                    toRemove.add(span1);
//                                }
//                            });
//                    overlappingSpans.removeAll(toRemove);
//
//                    overlappingSpans.add(span);
//                });
//    }

	public void getNextSpan() throws NoSelectionException {
		Span nextSpan = getSpans(null).getNext(getSelection().getSpanCollection().getSelection());
		setSelection(nextSpan.getConceptAnnotation());
		nextSpan.getConceptAnnotation().getSpanCollection().setSelection(nextSpan);
	}

	public void getPreviousSpan() throws NoSelectionException {
		Span previousSpan = getSpans(null).getPrevious(getSelection().getSpanCollection().getSelection());
		setSelection(previousSpan.getConceptAnnotation());
		previousSpan.getConceptAnnotation().getSpanCollection().setSelection(previousSpan);

	}

    /*
    SETTERS
     */

	public void setSelectedAnnotation(Span newSpan) {

		try {
			if (newSpan == null) {
				setSelection(null);
			} else if (getSelection() != newSpan.getConceptAnnotation()) {
				setSelection(newSpan.getConceptAnnotation());
				newSpan.getConceptAnnotation().getSpanCollection().setSelection(newSpan);
			}
		} catch (NoSelectionException e) {
			setSelection(newSpan.getConceptAnnotation());
			newSpan.getConceptAnnotation().getSpanCollection().setSelection(newSpan);
		}
	}

    /*
    SETUP
     */

	@Override
	public void setSelection(ConceptAnnotation selection) {
		try {
			if (getSelection() != selection) {
				getSelection().getSpanCollection().setSelection(null);
				super.setSelection(selection);
			}
		} catch (NoSelectionException e) {
			super.setSelection(selection);

		}
	}

	private void setOWLClassForAnnotations() {
		Map<String, List<ConceptAnnotation>> unmatchedAnnotations = new HashMap<>();
		for (ConceptAnnotation conceptAnnotation : this) {
			OWLClass owlClass = controller.getOWLModel().getOWLClassByID(conceptAnnotation.getOwlClassID());
			if (owlClass == null) {
				List<ConceptAnnotation> conceptAnnotationList = unmatchedAnnotations.computeIfAbsent(conceptAnnotation.getOwlClassID(), k -> new ArrayList<>());
				conceptAnnotationList.add(conceptAnnotation);
			} else {
				conceptAnnotation.setOwlClass(owlClass);
			}
		}
		if (!unmatchedAnnotations.isEmpty()) {
			log.warn("The following classes could not be matched to annotations");
			unmatchedAnnotations.forEach((concept, conceptAnnotationList) -> {
				log.warn(String.format("Concept: %s", concept));
				log.warn("Annotations:");
				conceptAnnotationList.forEach(conceptAnnotation -> log.warn(String.format("\t%s", conceptAnnotation.getId())));
			});
		}
	}

    /*
    WRITERS
     */

	@Override
	public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
		forEach(
				annotation -> annotation.writeToKnowtatorXML(dom, textSourceElement));
	}

	@Override
	public void writeToBratStandoff(
			Writer writer,
			Map<String, Map<String, String>> annotationsConfig,
			Map<String, Map<String, String>> visualConfig)
			throws IOException {
		Iterator<ConceptAnnotation> annotationIterator = iterator();
		for (int i = 0; i < size(); i++) {
			ConceptAnnotation conceptAnnotation = annotationIterator.next();
			conceptAnnotation.setBratID(String.format("T%d", i));

			conceptAnnotation.writeToBratStandoff(writer, annotationsConfig, visualConfig);
		}

		// Not adding relations due to complexity of relation types in Brat Standoff
    /*int lastNumTriples = 0;
    for (GraphSpace graphSpace : graphSpaceCollection) {
      Object[] edges = graphSpace.getChildEdges(graphSpace.getDefaultParent());
      int bound = edges.length;
      for (int i = 0; i < bound; i++) {
        Object edge = edges[i];
        RelationAnnotation triple = (RelationAnnotation) edge;
        triple.setBratID(String.format("R%d", lastNumTriples + i));
        String propertyID;
        try {
          propertyID =
              controller.getOWLAPIDataExtractor().getOWLEntityRendering(triple.getProperty());
        } catch (OWLEntityNullException | OWLWorkSpaceNotSetException e) {
          propertyID = triple.getValue().toString();
        }
        writer.append(
            String.format(
                "%s\t%s Arg1:%s Arg2:%s\n",
                triple.getBratID(),
                propertyID,
                ((AnnotationNode) triple.getSource()).getConceptAnnotation().getBratID(),
                ((AnnotationNode) triple.getTarget()).getConceptAnnotation().getBratID()));
      }
    }*/
	}

    /*
    READERS
     */

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
		for (Node annotationNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION))) {
			Element annotationElement = (Element) annotationNode;

			String annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
			String profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
			String type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);

			Profile profile = controller.getProfileCollection().get(profileID);
			profile = profile == null ? controller.getProfileCollection().getDefaultProfile() : profile;
			String owlClassID =
					((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
							.getAttribute(KnowtatorXMLAttributes.ID);
			String owlClassLabel =
					((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
							.getAttribute(KnowtatorXMLAttributes.LABEL);

			String motivation = annotationElement.getAttribute(KnowtatorXMLAttributes.MOTIVATION);


			ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(controller, textSource, annotationID, null, owlClassID, owlClassLabel, profile, type, motivation);
			newConceptAnnotation.readFromKnowtatorXML(null, annotationElement);
			add(newConceptAnnotation);
		}

		setOWLClassForAnnotations();
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {

		Map<String, Element> slotToClassIDMap = KnowtatorXMLUtil.getslotsFromXml(parent);
		Map<String, Element> classMentionToClassIDMap = KnowtatorXMLUtil.getClassIDsFromXml(parent);
		Map<ConceptAnnotation, Element> annotationToSlotMap = new HashMap<>();

		for (Node annotationNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION))) {
			Element annotationElement = (Element) annotationNode;

			String profileID = null;
			try {
				profileID = annotationElement.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR).item(0).getTextContent();
				controller.getProfileCollection().add(new Profile(controller, profileID));
			} catch (NullPointerException npe) {
				try {
					profileID = annotationElement.getAttribute(OldKnowtatorXMLAttributes.ANNOTATOR);
					controller.getProfileCollection().add(new Profile(controller, profileID));
				} catch (NullPointerException ignored) {
				}
			}
			Profile profile = controller.getProfileCollection().get(profileID);
			profile = profile == null ? controller.getProfileCollection().getDefaultProfile() : profile;

			String annotationID =
					((Element) annotationElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION).item(0))
							.getAttribute(OldKnowtatorXMLAttributes.ID);
			Element classElement = classMentionToClassIDMap.get(annotationID);

			String owlClassID =
					((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0))
							.getAttribute(OldKnowtatorXMLAttributes.ID);
			String owlClassName =
					classElement
							.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS)
							.item(0)
							.getTextContent();


			ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(controller, textSource, annotationID, null, owlClassID, owlClassName, profile, "identity", "");
			if (containsID(annotationID)) {
				controller.verifyId(null, newConceptAnnotation, false);
			}
			add(newConceptAnnotation);
			newConceptAnnotation.readFromOldKnowtatorXML(null, annotationElement);

			// No need to keep annotations with no allSpanCollection
			if (newConceptAnnotation.getSpanCollection().size() == 0) {
				try {
					remove(newConceptAnnotation);
				} catch (CantRemoveException e) {
					e.printStackTrace();
				}
			} else {
				for (Node slotMentionNode :
						KnowtatorXMLUtil.asList(
								classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION))) {
					Element slotMentionElement = (Element) slotMentionNode;
					String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
					Element slotElement = slotToClassIDMap.get(slotMentionID);
					if (slotElement != null) {
						annotationToSlotMap.put(newConceptAnnotation, slotElement);
					}
				}
			}
		}


		GraphSpace oldKnowtatorGraphSpace = new GraphSpace(controller, textSource, "Old Knowtator Relations");
		textSource.getGraphSpaceCollection().add(oldKnowtatorGraphSpace);

		annotationToSlotMap.forEach(
				(annotation, slot) -> {
					List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);

					AnnotationNode source = oldKnowtatorGraphSpace.makeOrGetAnnotationNode(annotation, vertices);

					String propertyID =
							((Element) slot.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0))
									.getAttribute(OldKnowtatorXMLAttributes.ID);
					for (Node slotMentionValueNode :
							OldKnowtatorUtil.asList(
									slot.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
						Element slotMentionValueElement = (Element) slotMentionValueNode;
						String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
						ConceptAnnotation conceptAnnotation1 = textSource.getConceptAnnotationCollection().get(value);

						List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(conceptAnnotation1);


						AnnotationNode target = oldKnowtatorGraphSpace.makeOrGetAnnotationNode(conceptAnnotation1, vertices1);

						oldKnowtatorGraphSpace.addTriple(
								source,
								target,
								null,
								controller.getProfileCollection().getSelection(),
								null,
								propertyID,
								"",
								"",
								false,
								"");
					}
				}
		);

		setOWLClassForAnnotations();
	}

	@Override
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationCollection, String content) {

		Profile profile = controller.getProfileCollection().getDefaultProfile();

		annotationCollection
				.get(StandoffTags.TEXTBOUNDANNOTATION)
				.forEach(
						annotation -> {
							ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(controller, textSource, annotation[0], null,
									annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0],
									null, profile, "identity", "");
							add(newConceptAnnotation);
							Map<Character, List<String[]>> map = new HashMap<>();
							List<String[]> list = new ArrayList<>();
							list.add(annotation);
							map.put(StandoffTags.TEXTBOUNDANNOTATION, list);
							newConceptAnnotation.readFromBratStandoff(null, map, content);
						});

		annotationCollection
				.get(StandoffTags.NORMALIZATION)
				.forEach(
						normalization -> {
							String[] splitNormalization =
									normalization[1].split(StandoffTags.relationTripleDelimiter);
							ConceptAnnotation conceptAnnotation = get(splitNormalization[1]);
							conceptAnnotation.setOWLClassID(splitNormalization[2]);
						});

		GraphSpace newGraphSpace = new GraphSpace(controller, textSource, "Brat Relation Graph");
		textSource.getGraphSpaceCollection().add(newGraphSpace);
		newGraphSpace.readFromBratStandoff(null, annotationCollection, null);

		setOWLClassForAnnotations();
	}


	@Override
	public void dispose() {
		controller.getOWLModel().removeOntologyChangeListener(this);
		controller.getOWLModel().removeOWLModelManagerListener(this);
		super.dispose();
	}

	@SuppressWarnings("Duplicates")
	@Override
	public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
		Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
		Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
		OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
		OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

		Set<ConceptAnnotation> nameChange = new HashSet<>();

		for (OWLOntologyChange chg : changes) {
			AxiomType type = chg.getAxiom().getAxiomType();
			if (chg.isAxiomChange()) {
				OWLAxiomChange axChg = (OWLAxiomChange) chg;
				if (AxiomType.DECLARATION == type) {
					OWLDeclarationAxiom axiom = ((OWLDeclarationAxiom) axChg.getAxiom());
					if (axiom.getEntity() instanceof OWLClass) {
						if (axChg instanceof AddAxiom) {
							axiom.accept(addedCollector);
							nameChange.forEach(conceptAnnotation -> conceptAnnotation.setOwlClass((OWLClass) axiom.getEntity()));
						} else if (axChg instanceof RemoveAxiom) {
							axiom.accept(removedCollector);
							forEach(conceptAnnotation -> {
								if (conceptAnnotation.getOwlClass().equals(axiom.getEntity()))
									nameChange.add(conceptAnnotation);
							});
						}
					}

				}
				if (AxiomType.SUBCLASS_OF == type) {
					if (axChg instanceof AddAxiom) {
						axChg.getAxiom().accept(addedCollector);
					} else {
						axChg.getAxiom().accept(removedCollector);
					}
				}
			}

		}


//		OWLModel.processOntologyChanges(changes, possiblyAddedEntities, possiblyRemovedEntities);

    /*
    For now, I will assume that entity removed is the one that existed and the one
    that is added is the new name for it.
     */
//		log.warn("Added");
//		possiblyAddedEntities.forEach(log::warn);
//
//		log.warn("Removed");
//		possiblyRemovedEntities.forEach(log::warn);
//		if (!possiblyAddedEntities.isEmpty() && !possiblyRemovedEntities.isEmpty()) {
//			OWLClass oldOWLClass = (OWLClass) possiblyRemovedEntities.iterator().next();
//			OWLClass newOWLClass = (OWLClass) possiblyAddedEntities.iterator().next();
//
////            try {
////                log.warn(String.format("Old: %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(oldOWLClass)));
////                log.warn(String.format("New: %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(newOWLClass)));
////            } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
////                e.printStackTrace();
////            }
//			ConceptAnnotationCollection annotationsForOwlClass = new ConceptAnnotationCollection(controller, textSource);
//
//			String owlClassID = controller.getOWLModel().getOWLEntityRendering(oldOWLClass);
//
//			for (ConceptAnnotation conceptAnnotation : this) {
//				if (conceptAnnotation.getOwlClass() == oldOWLClass) {
//					annotationsForOwlClass.add(conceptAnnotation);
//				} else if (conceptAnnotation.getOwlClassID().equals(owlClassID)) {
//					conceptAnnotation.setOwlClass(oldOWLClass);
//					annotationsForOwlClass.add(conceptAnnotation);
//				}
//			}
//
//			if (newOWLClass == null) {
//				for (ConceptAnnotation conceptAnnotation : annotationsForOwlClass) {
//					try {
//						remove(conceptAnnotation);
//					} catch (CantRemoveException e) {
//						e.printStackTrace();
//					}
//				}
//			} else {
//				for (ConceptAnnotation conceptAnnotation : annotationsForOwlClass) {
//					conceptAnnotation.setOwlClass(newOWLClass);
//				}
//			}
//		}
	}

	@Override
	public void profileFilterChanged(boolean filterValue) {
		try {
			if (filterValue && getSelection().getAnnotator() != controller.getProfileCollection().getSelection()) {
				setSelection(null);
			}
		} catch (NoSelectionException ignored) {
		}
	}

	@Override
	public void owlClassFilterChanged(boolean filterValue) {
		try {
			if (filterValue && getSelection().getOwlClass() != controller.getOWLModel().getSelectedOWLClass()) {
				setSelection(null);
			}
		} catch (NoSelectionException | NoSelectedOWLClassException ignored) {
		}
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
			setOWLClassForAnnotations();
		}
	}
}
