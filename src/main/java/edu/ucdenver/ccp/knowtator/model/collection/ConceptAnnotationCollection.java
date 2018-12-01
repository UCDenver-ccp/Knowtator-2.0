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

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.FilterType;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.*;
import org.apache.log4j.Logger;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConceptAnnotationCollection extends KnowtatorCollection<ConceptAnnotation> implements OWLOntologyChangeListener, KnowtatorXMLIO, BratStandoffIO, ModelListener {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ConceptAnnotationCollection.class);

	private final BaseModel model;

	private final TextSource textSource;

	public ConceptAnnotationCollection(BaseModel model, TextSource textSource) {
		super(model);
		this.model = model;
		this.textSource = textSource;

		model.addOntologyChangeListener(this);
//		model.addOWLModelManagerListener(this);
		model.addModelListener(this);

	}

	@Override
	public void remove(ConceptAnnotation conceptAnnotationToRemove) {
		for (GraphSpace graphSpace : textSource.getGraphSpaceCollection()) {
			Object[] cells = graphSpace.getVerticesForAnnotation(conceptAnnotationToRemove).toArray();
			graphSpace.removeCells(cells);

		}
		super.remove(conceptAnnotationToRemove);
	}

	/**
	 * @param loc Location filter
	 */
	public SpanCollection getSpans(Integer loc) {
		SpanCollection allSpans = new SpanCollection(null);
		stream().map(conceptAnnotation -> conceptAnnotation.stream()
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
		boolean filterByOWLClass = model.isFilter(FilterType.OWLCLASS);
		boolean filterByProfile = model.isFilter(FilterType.PROFILE);
		Optional<Profile> activeProfile = model.getSelectedProfile();
		Set<OWLClass> activeOWLClassDescendants = new HashSet<>();

		if (filterByOWLClass) {
			model.getSelectedOWLClass().ifPresent(owlClass -> {
				activeOWLClassDescendants.add(owlClass);
				activeOWLClassDescendants.addAll(model.getOWLCLassDescendants(owlClass));
			});
		}
		return super.stream()
				.filter(conceptAnnotation -> !filterByOWLClass || activeOWLClassDescendants.contains(conceptAnnotation.getOwlClass()))
				.filter(conceptAnnotation -> !filterByProfile || activeProfile.map(activeProfile1 -> conceptAnnotation.getAnnotator().equals(activeProfile1)).orElse(false));
	}

	@Override
	@Nonnull
	public Iterator<ConceptAnnotation> iterator() {
		return stream().iterator();
	}

//	public TreeSet<ConceptAnnotation> getAnnotations(int start, int end) {
//		Supplier<TreeSet<ConceptAnnotation>> supplier = TreeSet::new;
//		return stream()
//				.filter(annotation -> (annotation.contains(start) && annotation.contains(end)))
//				.collect(Collectors.toCollection(supplier));
//	}

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
//                                    toRemove.addProfile(span1);
//                                }
//                            });
//                    overlappingSpans.removeAll(toRemove);
//
//                    overlappingSpans.addProfile(span);
//                });
//    }

	public void selectNextSpan() {
		getSelection().ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
				.map(span -> getSpans(null).getNext(span))
				.ifPresent(nextSpan -> {
					setSelection(nextSpan.getConceptAnnotation());
					nextSpan.getConceptAnnotation().setSelection(nextSpan);
				}));
	}

	public void selectPreviousSpan() {
		getSelection().ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
				.map(span -> getSpans(null).getPrevious(span))
				.ifPresent(nextSpan -> {
					setSelection(nextSpan.getConceptAnnotation());
					nextSpan.getConceptAnnotation().setSelection(nextSpan);
				}));
	}

	public void setSelectedAnnotation(Span newSpan) {

		Optional<Span> newSpanOptional = Optional.ofNullable(newSpan);
		if (newSpanOptional.isPresent()) {
			setSelection(newSpan.getConceptAnnotation());
			newSpan.getConceptAnnotation().setSelection(newSpan);

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
						conceptAnnotation.setSelection(null);
						super.setSelection(selection);

					});
		} else {
			if (conceptAnnotationOptional.isPresent()) {
				super.setSelection(selection);
			}
		}
		getSelection().ifPresent(conceptAnnotation -> model.setSelectedOWLEntity(conceptAnnotation.getOwlClass()));
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
              model.getOWLAPIDataExtractor().getOWLEntityRendering(triple.getProperty());
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

		HashSet<String> classIDs = KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION)).stream().map(node -> (Element) node).map(annotationElement -> ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.ID)).collect(Collectors.toCollection(HashSet::new));

		Map<String, OWLClass> owlClassMap = model.getOWLClassesByIDs(classIDs);

		KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION)).stream().map(node -> (Element) node)
				.map(annotationElement -> {
					String annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
					String profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
					String type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);
					String owlClassID = ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.ID);
//					String owlClassLabel = ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.LABEL);
					String motivation = annotationElement.getAttribute(KnowtatorXMLAttributes.MOTIVATION);

					Profile profile = model.getProfile(profileID).orElse(model.getDefaultProfile());

					Optional<OWLClass> owlClass = Optional.ofNullable(owlClassMap.get(owlClassID));
					if (owlClass.isPresent()) {
						ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(model, textSource, annotationID, owlClass.get(), profile, type, motivation);
						newConceptAnnotation.readFromKnowtatorXML(null, annotationElement);
						if (newConceptAnnotation.size() == 0) {
							return Optional.empty();
						} else {
							return Optional.of(newConceptAnnotation);
						}
					} else {
						log.warn(String.format("OWL Class: %s not found for concept: %s", owlClassID, annotationID));
						return Optional.empty();

					}
				}).forEach(conceptAnnotationOptional -> conceptAnnotationOptional.map(o -> (ConceptAnnotation) o).ifPresent(this::add));
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {

		Map<String, Element> slotToClassIDMap = KnowtatorXMLUtil.getslotsFromXml(parent);
		Map<String, Element> classMentionToClassIDMap = KnowtatorXMLUtil.getClassIDsFromXml(parent);
		Map<ConceptAnnotation, Element> annotationToSlotMap = new HashMap<>();

		KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION)).stream().map(node -> (Element) node)
				.map(annotationElement -> {
					String annotationID = ((Element) annotationElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
					Element classElement = classMentionToClassIDMap.get(annotationID);

					String owlClassID = ((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
//					String owlClassName = classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0).getTextContent();
					String profileID = null;
					try {
						profileID = annotationElement.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR).item(0).getTextContent();
						model.addProfile(new Profile(model, profileID));
					} catch (NullPointerException npe) {
						try {
							profileID = annotationElement.getAttribute(OldKnowtatorXMLAttributes.ANNOTATOR);
							model.addProfile(new Profile(model, profileID));
						} catch (NullPointerException ignored) {
						}
					}
					Profile profile = model.getProfile(profileID).orElse(model.getDefaultProfile());

					Optional<OWLClass> owlClass = model.getOWLClassByID(owlClassID);
					if (owlClass.isPresent()) {
						ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(model, textSource, annotationID, owlClass.get(), profile, "identity", "");
						if (containsID(annotationID)) model.verifyId(null, newConceptAnnotation, false);
						newConceptAnnotation.readFromOldKnowtatorXML(null, annotationElement);

						// No need to keep annotations with no allSpanCollection
						if (newConceptAnnotation.size() == 0) {
							return Optional.empty();
						} else {
							KnowtatorXMLUtil.asList(classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION)).stream()
									.map(node -> (Element) node).forEach(slotMentionElement -> {
								String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
								Element slotElement = slotToClassIDMap.get(slotMentionID);
								if (slotElement != null) {
									annotationToSlotMap.put(newConceptAnnotation, slotElement);
								}
							});
							return Optional.of(newConceptAnnotation);
						}
					} else {
						log.warn(String.format("OWL Class: %s not found for concept: %s", owlClassID, annotationID));
						return Optional.empty();

					}


				}).forEach(conceptAnnotationOptional -> conceptAnnotationOptional.map(o -> (ConceptAnnotation) o).ifPresent(this::add));


		GraphSpace oldKnowtatorGraphSpace = new GraphSpace(model, textSource, "Old Knowtator Relations");
		textSource.add(oldKnowtatorGraphSpace);

		annotationToSlotMap.forEach((annotation, slot) -> {
					String propertyID = ((Element) slot.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);

					List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);
					AnnotationNode source = oldKnowtatorGraphSpace.makeOrGetAnnotationNode(annotation, vertices);

					for (Node slotMentionValueNode : OldKnowtatorUtil.asList(slot.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
						Element slotMentionValueElement = (Element) slotMentionValueNode;
						String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
						get(value).map(conceptAnnotation -> {
							List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(conceptAnnotation);
							return oldKnowtatorGraphSpace.makeOrGetAnnotationNode(conceptAnnotation, vertices1);
						}).ifPresent(target -> model.getOWLObjectPropertyByID(propertyID).ifPresent(property -> oldKnowtatorGraphSpace.addTriple(source, target, null, model.getDefaultProfile(), property, propertyID, "", "", false, "")));

					}
				}
		);
	}

	@Override
	public void add(ConceptAnnotation conceptAnnotation) {
		super.add(conceptAnnotation);
	}

	@Override
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationCollection, String content) {

		Profile profile = model.getDefaultProfile();

		annotationCollection
				.get(StandoffTags.TEXTBOUNDANNOTATION)
				.forEach(
						annotation -> {
							String owlClassID = annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0];
							model.getOWLClassByID(owlClassID).ifPresent(owlClass -> {
								ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(model, textSource, annotation[0], owlClass, profile, "identity", "");
								add(newConceptAnnotation);
								Map<Character, List<String[]>> map = new HashMap<>();
								List<String[]> list = new ArrayList<>();
								list.add(annotation);
								map.put(StandoffTags.TEXTBOUNDANNOTATION, list);
								newConceptAnnotation.readFromBratStandoff(null, map, content);
							});

						});


		annotationCollection
				.get(StandoffTags.NORMALIZATION)
				.forEach(normalization -> {
//					String[] splitNormalization = normalization[1].split(StandoffTags.relationTripleDelimiter);
//					String annotationID = splitNormalization[1];
//					String owlClassID = splitNormalization[2];
				});

		GraphSpace newGraphSpace = new GraphSpace(model, textSource, "Brat Relation Graph");
		textSource.add(newGraphSpace);
		newGraphSpace.readFromBratStandoff(null, annotationCollection, null);
	}


	@Override
	public void dispose() {
		model.removeOntologyChangeListener(this);
//		model.removeOWLModelManagerListener(this);
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
							forEach(conceptAnnotation -> {
								if (conceptAnnotation.getOwlClass().equals(axiom.getEntity())) {
									annotationsToChangeOrRemove.add(conceptAnnotation);
								}
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

		annotationsToChangeOrRemove.forEach(conceptAnnotationToRemove -> {
			log.warn(String.format("Removing annotation %s", conceptAnnotationToRemove));
			remove(conceptAnnotationToRemove);
		});

	}

	@Override
	public void filterChangedEvent(FilterType filterType, boolean filterValue) {
		if (filterType == FilterType.PROFILE) {
			getSelection()
					.filter(conceptAnnotation -> filterValue)
					.filter(conceptAnnotation -> model.getSelectedProfile()
							.map(profile -> !conceptAnnotation.getAnnotator().equals(profile)).orElse(false))
					.ifPresent(conceptAnnotation -> setSelection(null));
		}
		if (filterType == FilterType.OWLCLASS) {
			getSelection()
					.filter(conceptAnnotation -> filterValue)
					.filter(conceptAnnotation -> model.getSelectedOWLClass()
							.map(owlClass1 -> !owlClass1.equals(conceptAnnotation.getOwlClass()))
							.orElse(false))
					.ifPresent(conceptAnnotation -> setSelection(null));
		}
	}

	@Override
	public void colorChangedEvent() {

	}

	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {

	}
}
