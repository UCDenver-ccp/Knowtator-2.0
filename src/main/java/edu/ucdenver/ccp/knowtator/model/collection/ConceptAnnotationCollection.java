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

import edu.ucdenver.ccp.knowtator.model.FilterType;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.*;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

public class ConceptAnnotationCollection extends KnowtatorCollection<ConceptAnnotation> implements OWLOntologyChangeListener, ModelListener {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ConceptAnnotationCollection.class);

	private final KnowtatorModel model;

	private final TextSource textSource;

	public ConceptAnnotationCollection(KnowtatorModel model, TextSource textSource) {
		super(model);
		this.model = model;
		this.textSource = textSource;

		model.addOntologyChangeListener(this);
		model.addModelListener(this);
	}

	@Override
	public void remove(ConceptAnnotation conceptAnnotationToRemove) {
		textSource.getGraphSpaces().forEach(graphSpace ->
				graphSpace.removeCells(graphSpace.getAnnotationNodes(conceptAnnotationToRemove).toArray()));
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

	/*
    SETUP
     */

	@Override
	public void setSelection(ConceptAnnotation selection) {
		super.setSelection(selection);

		getSelection().ifPresent(conceptAnnotation -> {
			if (!textSource.getSelectedGraphSpace().isPresent()) {
				textSource.getGraphSpaces().stream()
						.filter(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation))
						.findFirst().ifPresent(textSource::setSelectedGraphSpace);
			}
			textSource.getSelectedGraphSpace()
					.ifPresent(graphSpace ->
							graphSpace.setSelectionCells(graphSpace
									.getAnnotationNodes(conceptAnnotation).toArray()));
		});

		getSelection().ifPresent(conceptAnnotation -> model.setSelectedOWLEntity(conceptAnnotation.getOwlClass()));
	}

	@Override
	public void add(ConceptAnnotation conceptAnnotation) {
		super.add(conceptAnnotation);
	}


	@Override
	public void dispose() {
		model.removeOntologyChangeListener(this);
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
	public void filterChangedEvent() {
		getSelection()
				.filter(conceptAnnotation -> model.isFilter(FilterType.PROFILE))
				.filter(conceptAnnotation -> model.getSelectedProfile()
						.map(profile -> !conceptAnnotation.getAnnotator().equals(profile)).orElse(false))
				.ifPresent(conceptAnnotation -> setSelection(null));
		getSelection()
				.filter(conceptAnnotation -> model.isFilter(FilterType.OWLCLASS))
				.filter(conceptAnnotation -> model.getSelectedOWLClass()
						.map(owlClass1 -> !owlClass1.equals(conceptAnnotation.getOwlClass()))
						.orElse(false))
				.ifPresent(conceptAnnotation -> setSelection(null));
	}

	@Override
	public void colorChangedEvent() {

	}

	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		event.getNew()
				.filter(modelObject -> modelObject instanceof Span)
				.map(modelObject -> (Span) modelObject)
				.filter(span -> span.getTextSource().equals(textSource))
				.ifPresent(span -> setSelection(span.getConceptAnnotation()));
	}
}
