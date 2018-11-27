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

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.FilterModelListener;
import edu.ucdenver.ccp.knowtator.model.FilterType;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
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

	private final KnowtatorModel controller;

	private final TextSource textSource;

	public ConceptAnnotationCollection(KnowtatorModel controller, TextSource textSource) {
		super();
		this.controller = controller;
		this.textSource = textSource;

		controller.addOntologyChangeListener(this);
		controller.addOWLModelManagerListener(this);
		controller.addFilterModelListener(this);

	}

    /*
    ADDERS
     */


	/*
	REMOVERS
	 */
	@Override
	public void remove(ConceptAnnotation conceptAnnotationToRemove) {
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
		boolean filterByOWLClass = controller.isFilter(FilterType.OWLCLASS);
		boolean filterByProfile = controller.isFilter(FilterType.PROFILE);
		Optional<Profile> activeProfile = controller.getProfileCollection().getSelection();
		Set<OWLClass> activeOWLClassDescendants = new HashSet<>();

		if (filterByOWLClass) {
			controller.getSelectedOWLClass().ifPresent(owlClass -> {
				activeOWLClassDescendants.add(owlClass);
				activeOWLClassDescendants.addAll(controller.getDescendants(owlClass));
			});
		}
		return super.stream()
				.filter(conceptAnnotation -> !filterByOWLClass || conceptAnnotation.getOwlClass().map(activeOWLClassDescendants::contains).orElse(false))
				.filter(conceptAnnotation -> !filterByProfile || activeProfile.map(activeProfile1 -> conceptAnnotation.getAnnotator().equals(activeProfile1)).orElse(false));
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

	public void getNextSpan() {
		getSelection().ifPresent(conceptAnnotation -> conceptAnnotation.getSpanCollection().getSelection()
				.map(span -> getSpans(null).getNext(span))
				.ifPresent(nextSpan -> {
					setSelection(nextSpan.getConceptAnnotation());
					nextSpan.getConceptAnnotation().getSpanCollection().setSelection(nextSpan);
				}));
	}

	public void getPreviousSpan() {
		getSelection().ifPresent(conceptAnnotation -> conceptAnnotation.getSpanCollection().getSelection()
				.map(span -> getSpans(null).getPrevious(span))
				.ifPresent(nextSpan -> {
					setSelection(nextSpan.getConceptAnnotation());
					nextSpan.getConceptAnnotation().getSpanCollection().setSelection(nextSpan);
				}));
	}

    /*
    SETTERS
     */

	public void setSelectedAnnotation(Span newSpan) {

		Optional<Span> newSpanOptional = Optional.ofNullable(newSpan);
		if (newSpanOptional.isPresent()) {
			newSpanOptional.map(Span::getConceptAnnotation)
					.filter(conceptAnnotation -> getSelection()
							.map(conceptAnnotation1 -> !conceptAnnotation1.equals(conceptAnnotation))
							.orElseGet(() -> {
								setSelection(conceptAnnotation);
								conceptAnnotation.getSpanCollection().setSelection(newSpan);
								return false;
							}))
					.ifPresent(conceptAnnotation -> conceptAnnotation.getSpanCollection().setSelection(newSpan));
		} else {
			setSelection(null);
		}
	}

    /*
    SETUP
     */

	@Override
	public void setSelection(ConceptAnnotation selection) {
		Optional<ConceptAnnotation> conceptAnnotationOptional = Optional.ofNullable(selection);
		if (getSelection().isPresent()) {
			getSelection()
					.filter(conceptAnnotation -> conceptAnnotation != selection)
					.ifPresent(conceptAnnotation -> {
						conceptAnnotation.getSpanCollection().setSelection(null);
						super.setSelection(selection);

					});
		} else {
			if (conceptAnnotationOptional.isPresent()) {
				super.setSelection(selection);
			}
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
				remove(newConceptAnnotation);
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
								controller.getProfileCollection().getDefaultProfile(),
								Optional.empty(),
								propertyID,
								"",
								"",
								false,
								"");
					}
				}
		);
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
	}


	@Override
	public void dispose() {
		controller.removeOntologyChangeListener(this);
		controller.removeOWLModelManagerListener(this);
		super.dispose();
	}

	@SuppressWarnings("Duplicates")
	@Override
	public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
		Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
		Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
		OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
		OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

		Set<ConceptAnnotation> annotationsToChangeOrRemove = new HashSet<>();

		for (OWLOntologyChange chg : changes) {
			AxiomType type = chg.getAxiom().getAxiomType();
			if (chg.isAxiomChange()) {
				OWLAxiomChange axChg = (OWLAxiomChange) chg;
				if (AxiomType.DECLARATION == type) {
					OWLDeclarationAxiom axiom = ((OWLDeclarationAxiom) axChg.getAxiom());
					if (axiom.getEntity() instanceof OWLClass) {
						if (axChg instanceof AddAxiom) {
							axiom.accept(addedCollector);
							annotationsToChangeOrRemove.forEach(conceptAnnotation -> conceptAnnotation.setOwlClass((OWLClass) axiom.getEntity()));
							annotationsToChangeOrRemove.clear();
						} else if (axChg instanceof RemoveAxiom) {
							axiom.accept(removedCollector);
							forEach(conceptAnnotation -> conceptAnnotation.getOwlClass()
									.filter(owlClass -> owlClass.equals(axiom.getEntity()))
									.ifPresent(owlClass -> annotationsToChangeOrRemove.add(conceptAnnotation)));
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

		annotationsToChangeOrRemove.forEach(conceptAnnotationToRemove -> {
			log.warn(String.format("Removing annotation %s", conceptAnnotationToRemove));
			remove(conceptAnnotationToRemove);
		});

	}

	@Override
	public void profileFilterChanged(boolean filterValue) {
		getSelection()
				.filter(conceptAnnotation -> filterValue)
				.filter(conceptAnnotation -> controller.getProfileCollection().getSelection()
						.map(profile -> !conceptAnnotation.getAnnotator().equals(profile)).orElse(false))
				.ifPresent(conceptAnnotation -> setSelection(null));
	}

	@Override
	public void owlClassFilterChanged(boolean filterValue) {
		getSelection()
				.filter(conceptAnnotation -> filterValue)
				.filter(conceptAnnotation -> conceptAnnotation.getOwlClass()
						.map(owlClass -> controller.getSelectedOWLClass()
								.map(owlClass1 -> !owlClass1.equals(owlClass))
								.orElse(false))
						.orElse(false))
				.ifPresent(conceptAnnotation -> setSelection(null));
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
			forEach(ConceptAnnotation::setOWLClass);
		}
	}
}
