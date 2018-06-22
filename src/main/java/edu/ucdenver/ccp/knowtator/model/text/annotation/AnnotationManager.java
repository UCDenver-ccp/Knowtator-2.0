package edu.ucdenver.ccp.knowtator.model.text.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.events.ChangeEvent;
import edu.ucdenver.ccp.knowtator.events.SpanChangeEvent;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationSelectionListener;
import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.collection.AnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.graph.ActiveGraphSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AnnotationManager implements Savable {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AnnotationManager.class);

  private final KnowtatorController controller;

  private SpanCollection allSpanCollection;
  private TextSource textSource;
  private AnnotationCollection annotationCollection;

  private Annotation selectedAnnotation;
  private Span selectedSpan;

  private List<AnnotationSelectionListener> annotationListeners;

  public AnnotationManager(KnowtatorController controller, TextSource textSource) {
    this.controller = controller;
    this.textSource = textSource;

    annotationCollection = new AnnotationCollection(controller);
    allSpanCollection = new SpanCollection(controller);
    annotationListeners = new ArrayList<>();
  }

  public void addAnnotation(Annotation newAnnotation) {
    annotationCollection.add(newAnnotation);

    allSpanCollection.getCollection().addAll(newAnnotation.getSpanCollection().getCollection());

    setSelectedAnnotation(newAnnotation, null);
  }

  public void addSpanToAnnotation(Annotation annotation, Span newSpan) {
    annotation.addSpan(newSpan);
    allSpanCollection.add(newSpan);
    setSelectedSpan(newSpan);
  }

  public void removeAnnotation(Annotation annotationToRemove) {
    annotationCollection.remove(annotationToRemove);
    for (Span span : annotationToRemove.getSpanCollection()) {
      allSpanCollection.remove(span);
    }

    setSelectedAnnotation(null, null);
  }

  public void removeSpanFromAnnotation(Annotation annotation, Span span) {
    annotation.removeSpan(span);
    allSpanCollection.remove(span);
  }

  public AnnotationCollection getAnnotations() {
    return annotationCollection;
  }

  /** @param loc Location filter */
  public TreeSet<Span> getSpans(Integer loc, int start, int end) {
    Supplier<TreeSet<Span>> supplier = () -> new TreeSet<>(Span::compare);
    return allSpanCollection
        .stream()
        .filter(
            span ->
                (loc == null || span.contains(loc))
                    && (start <= span.getStart() && span.getEnd() <= end)
                    && (!controller.getSelectionManager().isFilterByProfile()
                        || span.getAnnotation()
                            .getAnnotator()
                            .equals(controller.getSelectionManager().getActiveProfile())))
        .collect(Collectors.toCollection(supplier));
  }

  public TreeSet<Annotation> getAnnotations(int start, int end) {
    Supplier<TreeSet<Annotation>> supplier = () -> new TreeSet<>(Annotation::compare);
    return annotationCollection
        .stream()
        .filter(annotation -> (annotation.contains(start) && annotation.contains(end)))
        .collect(Collectors.toCollection(supplier));
  }

  public void growSelectedSpanStart() {
    Span span = getSelectedSpan();
    allSpanCollection.remove(span);
    span.growStart();
    allSpanCollection.add(span);
    setSelectedSpan(span);
  }

  public void growSelectedSpanEnd() {
    Span span = getSelectedSpan();
    allSpanCollection.remove(span);
    span.growEnd(textSource.getContent().length());
    allSpanCollection.add(getSelectedSpan());
    setSelectedSpan(span);
  }

  public void shrinkSelectedSpanEnd() {
    Span span = getSelectedSpan();
    allSpanCollection.remove(span);
    span.shrinkEnd();
    allSpanCollection.add(span);
    setSelectedSpan(span);
  }

  public void shrinkSelectedSpanStart() {
    Span span = getSelectedSpan();
    allSpanCollection.remove(span);
    span.shrinkStart();
    allSpanCollection.add(span);
    setSelectedSpan(span);
  }

  @SuppressWarnings("unused")
  public void findOverlaps() {
    List<Span> overlappingSpans = new ArrayList<>();
    allSpanCollection.forEach(
        span -> {
          List<Span> toRemove = new ArrayList<>();
          overlappingSpans.forEach(
              span1 -> {
                if (span.intersects(span1)) {
                  span.getAnnotation().addOverlappingAnnotation(span1.getAnnotation());
                  span1.getAnnotation().addOverlappingAnnotation(span.getAnnotation());
                } else {
                  toRemove.add(span1);
                }
              });
          overlappingSpans.removeAll(toRemove);

          overlappingSpans.add(span);
        });
  }

  public Annotation getAnnotation(String annotationID) {
    return annotationCollection.get(annotationID);
  }

  @Override
  public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
    annotationCollection.forEach(
        annotation -> annotation.writeToKnowtatorXML(dom, textSourceElement));
  }

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    for (Node annotationNode :
        KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION))) {
      Element annotationElement = (Element) annotationNode;

      String annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
      String profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
      String type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);

      Profile profile = controller.getProfileManager().getProfile(profileID);
      profile = profile == null ? controller.getProfileManager().getDefaultProfile() : profile;
      String owlClassID =
          ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
              .getAttribute(KnowtatorXMLAttributes.ID);
      String owlClassLabel =
          ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
              .getAttribute(KnowtatorXMLAttributes.LABEL);

      Annotation newAnnotation =
          new Annotation(
              controller, annotationID, null, owlClassID, owlClassLabel, profile, type, textSource);
      newAnnotation.readFromKnowtatorXML(null, annotationElement);

      addAnnotation(newAnnotation);
    }


  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {

    Map<String, Element> slotToClassIDMap = KnowtatorXMLUtil.getslotsFromXml(parent);
    Map<String, Element> classMentionToClassIDMap = KnowtatorXMLUtil.getClassIDsFromXml(parent);
    Map<Annotation, Element> annotationToSlotMap = new HashMap<>();

    for (Node annotationNode :
        KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION))) {
      Element annotationElement = (Element) annotationNode;

      Profile profile;
      try {
        String profileID =
            annotationElement
                .getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR)
                .item(0)
                .getTextContent();
        profile = controller.getProfileManager().addProfile(profileID);
      } catch (NullPointerException npe) {
        try {
          String profileID = annotationElement.getAttribute(OldKnowtatorXMLAttributes.ANNOTATOR);
          profile = controller.getProfileManager().addProfile(profileID);
        } catch (NullPointerException npe2) {
          profile = controller.getProfileManager().getDefaultProfile();
        }
      }

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

      Annotation newAnnotation =
          new Annotation(
              controller,
              annotationID,
              null,
              owlClassID,
              owlClassName,
              profile,
              "identity",
              this.textSource);

      newAnnotation.readFromOldKnowtatorXML(null, annotationElement);

      // No need to keep annotations with no allSpanCollection
      if (!newAnnotation.getSpanCollection().getCollection().isEmpty()) {
        addAnnotation(newAnnotation);

        for (Node slotMentionNode :
            KnowtatorXMLUtil.asList(
                classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION))) {
          Element slotMentionElement = (Element) slotMentionNode;
          String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
          Element slotElement = slotToClassIDMap.get(slotMentionID);
          if (slotElement != null) {
            annotationToSlotMap.put(newAnnotation, slotElement);
          }
        }
      }
    }

    GraphSpace oldKnowtatorGraphSpace = textSource.getGraphSpaceManager().addGraphSpace("Old Knowtator Relations");

    annotationToSlotMap.forEach(
            (annotation, slot) -> {
              List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);

              AnnotationNode source;
              if (vertices.isEmpty()) {
                source = oldKnowtatorGraphSpace.addNode(null, annotation, 20, 20);
              } else {
                source = (AnnotationNode) vertices.get(0);
              }

              String propertyID =
                      ((Element) slot.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0))
                              .getAttribute(OldKnowtatorXMLAttributes.ID);
              for (Node slotMentionValueNode :
                      OldKnowatorUtil.asList(
                              slot.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
                Element slotMentionValueElement = (Element) slotMentionValueNode;
                String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
                Annotation annotation1 = textSource.getAnnotationManager().getAnnotation(value);

                List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation1);

                AnnotationNode target;
                if (vertices1.isEmpty()) {
                  target = oldKnowtatorGraphSpace.addNode(null, annotation1, 20, 20);
                } else {
                  target = (AnnotationNode) vertices1.get(0);
                }
                oldKnowtatorGraphSpace.addTriple(
                        source,
                        target,
                        null,
                        controller.getSelectionManager().getActiveProfile(),
                        null,
                        propertyID,
                        "",
                        "",
                        false);
              }
            });


  }

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationCollection, String content) {

    Profile profile = controller.getProfileManager().getDefaultProfile();

    annotationCollection
        .get(StandoffTags.TEXTBOUNDANNOTATION)
        .forEach(
            annotation -> {
              Annotation newAnnotation =
                  new Annotation(
                      controller,
                      annotation[0],
                      null,
                      annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0],
                      null,
                      profile,
                      "identity",
                      textSource);
              Map<Character, List<String[]>> map = new HashMap<>();
              List<String[]> list = new ArrayList<>();
              list.add(annotation);
              map.put(StandoffTags.TEXTBOUNDANNOTATION, list);
              newAnnotation.readFromBratStandoff(null, map, content);

              addAnnotation(newAnnotation);
            });

    annotationCollection
        .get(StandoffTags.NORMALIZATION)
        .forEach(
            normalizaion -> {
              String[] splitNormalization =
                  normalizaion[1].split(StandoffTags.relationTripleDelimiter);
              Annotation annotation = getAnnotation(splitNormalization[1]);
              annotation.setOWLClassID(splitNormalization[2]);
            });

    GraphSpace newGraphSpace = textSource.getGraphSpaceManager().addGraphSpace("Brat Relation Graph");
    newGraphSpace.readFromBratStandoff(null, annotationCollection, null);
  }

  @Override
  public void writeToBratStandoff(
      Writer writer,
      Map<String, Map<String, String>> annotationsConfig,
      Map<String, Map<String, String>> visualConfig)
      throws IOException {
    Iterator<Annotation> annotationIterator = annotationCollection.iterator();
    for (int i = 0; i < annotationCollection.size(); i++) {
      Annotation annotation = annotationIterator.next();
      annotation.setBratID(String.format("T%d", i));

      annotation.writeToBratStandoff(writer, annotationsConfig, visualConfig);
    }

    // Not adding relations due to complexity of relation types in Brat Standoff
    /*int lastNumTriples = 0;
    for (GraphSpace graphSpace : graphSpaceCollection) {
      Object[] edges = graphSpace.getChildEdges(graphSpace.getDefaultParent());
      int bound = edges.length;
      for (int i = 0; i < bound; i++) {
        Object edge = edges[i];
        Triple triple = (Triple) edge;
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
                ((AnnotationNode) triple.getSource()).getAnnotation().getBratID(),
                ((AnnotationNode) triple.getTarget()).getAnnotation().getBratID()));
      }
    }*/
  }

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  public void addSelectedAnnotation() {
    OWLEntity owlClass = controller.getSelectionManager().getSelectedOWLEntity();
    if (owlClass instanceof OWLClass) {
      Profile annotator = controller.getSelectionManager().getActiveProfile();
      int start = controller.getSelectionManager().getStart();
      int end = controller.getSelectionManager().getEnd();
      Span newSpan = new Span(null, start, end, textSource, controller);

      Annotation newAnnotation =
          new Annotation(controller, null, (OWLClass) owlClass, null, null, annotator, "identity", textSource);
      addAnnotation(newAnnotation);
      addSpanToAnnotation(newAnnotation, newSpan);
    }
  }

  private SpanCollection getAllSpanCollection() {
    return allSpanCollection;
  }

  public void addSpanToSelectedAnnotation() {
    addSpanToAnnotation(
        getSelectedAnnotation(),
        new Span(
            null,
            controller.getSelectionManager().getStart(),
            controller.getSelectionManager().getEnd(),
            textSource,
            controller));
  }

  public void removeSpanFromSelectedAnnotation() {
    removeSpanFromAnnotation(getSelectedAnnotation(), getSelectedSpan());
  }

  public void dispose() {
    annotationCollection.forEach(Annotation::dispose);
    annotationCollection.getCollection().clear();
    annotationListeners.clear();
  }



  public Annotation getSelectedAnnotation() {
    return selectedAnnotation;
  }

  public Span getSelectedSpan() {
    return selectedSpan;
  }

  public void setSelectedSpan(Span newSpan) {
    List<ChangeEvent> changeEvents = new ArrayList<>();
    changeEvents.add(new SpanChangeEvent(selectedSpan, newSpan));

    this.selectedSpan = newSpan;
    if (newSpan != null) {
      setSelectedAnnotation(newSpan.getAnnotation(), newSpan);
    }

    controller.refreshView();
  }

  public void setSelectedAnnotation(Annotation newAnnotation, Span newSpan) {
    if (controller.getProjectManager().isProjectLoaded()) {
      if (selectedAnnotation != newAnnotation) {
        AnnotationChangeEvent e = new AnnotationChangeEvent(this.selectedAnnotation, newAnnotation);
        selectedAnnotation = newAnnotation;
        if (selectedAnnotation != null) {
          setSelectedSpan(newSpan);
          controller.getSelectionManager().setSelectedOWLEntity(selectedAnnotation.getOwlClass());
        } try {
          textSource.getGraphSpaceManager().getActiveGraphSpace().setSelectionCell(null);
        } catch (ActiveGraphSpaceNotSetException ignored) {

        }
        annotationListeners.forEach(
            selectionListener -> selectionListener.selectedAnnotationChanged(e));
      }
    }
    controller.refreshView();
  }

  public void getNextSpan() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedSpan(
          textSource.getAnnotationManager().getAllSpanCollection().getNext(selectedSpan));
    }
  }

  public void getPreviousSpan() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedSpan(
          textSource.getAnnotationManager().getAllSpanCollection().getPrevious(selectedSpan));
    }
  }

  public void addAnnotationListener(AnnotationSelectionListener listener) {
    annotationListeners.add(listener);
  }

}
