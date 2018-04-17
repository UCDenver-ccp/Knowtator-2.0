package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.collection.AnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.owl.OWLClassNotFoundException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
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
  private GraphSpaceCollection graphSpaceCollection;

  AnnotationManager(KnowtatorController controller, TextSource textSource) {
    this.controller = controller;
    this.textSource = textSource;
    annotationCollection = new AnnotationCollection(controller);
    allSpanCollection = new SpanCollection(controller);
    graphSpaceCollection = new GraphSpaceCollection(controller);
  }

  public void addAnnotation(Annotation newAnnotation) {
    annotationCollection.add(newAnnotation);

    allSpanCollection.getCollection().addAll(newAnnotation.getSpanCollection().getCollection());

    controller.getSelectionManager().setSelectedAnnotation(newAnnotation, null);
  }

  public void addSpanToAnnotation(Annotation annotation, Span newSpan) {
    annotation.addSpan(newSpan);
    allSpanCollection.add(newSpan);
    controller.getSelectionManager().setSelectedSpan(newSpan);
  }

  public void removeAnnotation(Annotation annotationToRemove) {
    annotationCollection.remove(annotationToRemove);
    for (Span span : annotationToRemove.getSpanCollection()) {
      allSpanCollection.remove(span);
    }
    for (GraphSpace graphSpace : graphSpaceCollection) {
      for (Object vertex : graphSpace.getVerticesForAnnotation(annotationToRemove)) {
        graphSpace.setSelectionCell(vertex);
        graphSpace.removeSelectedCell();
      }
    }
    controller.getSelectionManager().setSelectedAnnotation(null, null);
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

  public void growSelectedSpanStart() {
    Span span = controller.getSelectionManager().getSelectedSpan();
    allSpanCollection.remove(span);
    span.growStart();
    allSpanCollection.add(span);
    controller.getSelectionManager().setSelectedSpan(span);
  }

  public void growSelectedSpanEnd() {
    Span span = controller.getSelectionManager().getSelectedSpan();
    allSpanCollection.remove(span);
    span.growEnd(textSource.getContent().length());
    allSpanCollection.add(controller.getSelectionManager().getSelectedSpan());
    controller.getSelectionManager().setSelectedSpan(span);
  }

  public void shrinkSelectedSpanEnd() {
    Span span = controller.getSelectionManager().getSelectedSpan();
    allSpanCollection.remove(span);
    span.shrinkEnd();
    allSpanCollection.add(span);
    controller.getSelectionManager().setSelectedSpan(span);
  }

  public void shrinkSelectedSpanStart() {
    Span span = controller.getSelectionManager().getSelectedSpan();
    allSpanCollection.remove(span);
    span.shrinkStart();
    allSpanCollection.add(span);
    controller.getSelectionManager().setSelectedSpan(span);
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

  public GraphSpace addGraphSpace(String title) {
    GraphSpace newGraphSpace = new GraphSpace(controller, textSource, title);
    graphSpaceCollection.add(newGraphSpace);
    controller.getSelectionManager().setSelectedGraphSpace(newGraphSpace);
    return newGraphSpace;
  }

  public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
    annotationCollection
        .forEach(annotation -> annotation.writeToKnowtatorXML(dom, textSourceElement));
    graphSpaceCollection
        .forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, textSourceElement));
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
      String owlClassID =
          ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
              .getAttribute(KnowtatorXMLAttributes.ID);

      OWLClass owlClass = null;
      try {
        owlClass = controller.getOWLAPIDataExtractor().getOWLClassByID(owlClassID);
      } catch (OWLClassNotFoundException | OWLWorkSpaceNotSetException e) {
        log.warn("OWL Class not found");
      }

      Annotation newAnnotation =
          new Annotation(controller, annotationID, owlClass, owlClassID, profile, type, textSource);
      newAnnotation.readFromKnowtatorXML(null, annotationElement);

      addAnnotation(newAnnotation);
    }

    for (Node graphSpaceNode :
        KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.GRAPH_SPACE))) {
      Element graphSpaceElem = (Element) graphSpaceNode;

      String id = graphSpaceElem.getAttribute(KnowtatorXMLAttributes.ID);
      GraphSpace graphSpace = addGraphSpace(id);

      graphSpace.readFromKnowtatorXML(null, graphSpaceElem);
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


      OWLClass owlClass = null;
      try {
        owlClass = controller.getOWLAPIDataExtractor().getOWLClassByID(owlClassID);
      } catch (OWLClassNotFoundException | OWLWorkSpaceNotSetException ignored) {
      }

      Annotation newAnnotation =
          new Annotation(
              controller, annotationID, owlClass, owlClassID, profile, "identity", this.textSource);

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

    GraphSpace oldKnowtatorGraphSpace = addGraphSpace("Old Knowtator Relations");

    annotationToSlotMap.forEach(
        (annotation, slot) -> {
          List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);

          AnnotationNode source;
          if (vertices.isEmpty()) {
            source = oldKnowtatorGraphSpace.addNode(null, annotation);
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
            Annotation annotation1 = getAnnotation(value);

            List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation1);

            AnnotationNode target;
            if (vertices1.isEmpty()) {
              target = oldKnowtatorGraphSpace.addNode(null, annotation1);
            } else {
              target = (AnnotationNode) vertices1.get(0);
            }
            oldKnowtatorGraphSpace.addTriple(
                source,
                target,
                null,
                controller.getSelectionManager().getActiveProfile(),
                null, propertyID,
					"", "");
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

    GraphSpace newGraphSpace = addGraphSpace("Brat Relation Graph");
    newGraphSpace.readFromBratStandoff(null, annotationCollection, null);
  }

  @Override
  public void writeToBratStandoff(Writer writer) throws IOException {
    Iterator<Annotation> annotationIterator = annotationCollection.iterator();
    for (int i = 0; i < annotationCollection.size(); i++) {
      Annotation annotation = annotationIterator.next();
      annotation.setBratID(String.format("T%d", i));

      try {
        writer.append(String.format("%s\t%s ", annotation.getBratID(), controller.getOWLAPIDataExtractor().getOWLEntityRendering(annotation.getOwlClass(), true)));
      } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
        writer.append(String.format("%s\t%s ", annotation.getBratID(), annotation.getOwlClassID()));
      }
      annotation.writeToBratStandoff(writer);

      writer.append(
          String.format(
              "\t%s\n", annotation.getSpanCollection().getCollection().first().getSpannedText()));
    }

    int lastNumTriples = 0;
    for (GraphSpace graphSpace : graphSpaceCollection) {
      Object[] edges = graphSpace.getChildEdges(graphSpace.getDefaultParent());
      int bound = edges.length;
      for (int i = 0; i < bound; i++) {
        Object edge = edges[i];
        Triple triple = (Triple) edge;
        triple.setBratID(String.format("R%d", lastNumTriples + i));
        String propertyID;
        try {
          propertyID = controller.getOWLAPIDataExtractor().getOWLEntityRendering(triple.getProperty(), true);
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
    }
  }

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  public GraphSpaceCollection getGraphSpaceCollection() {
    return graphSpaceCollection;
  }

  public void removeGraphSpace(GraphSpace graphSpace) {
    graphSpaceCollection.remove(graphSpace);
  }

  public void removeSelectedAnnotation() {
    removeAnnotation(controller.getSelectionManager().getSelectedAnnotation());
  }

  public void addSelectedAnnotation() {
    OWLClass owlClass = controller.getSelectionManager().getSelectedOWLClass();
    if (owlClass != null) {
      Profile annotator = controller.getSelectionManager().getActiveProfile();
      int start = controller.getSelectionManager().getStart();
      int end = controller.getSelectionManager().getEnd();
      Span newSpan = new Span(null, start, end, textSource, controller);

      Annotation newAnnotation =
          new Annotation(controller, null, owlClass, null, annotator, "identity", textSource);
      addAnnotation(newAnnotation);
      addSpanToAnnotation(newAnnotation, newSpan);
    }
  }

  SpanCollection getAllSpanCollection() {
    return allSpanCollection;
  }

  public void addSpanToSelectedAnnotation() {
    addSpanToAnnotation(
        controller.getSelectionManager().getSelectedAnnotation(),
        new Span(
            null,
            controller.getSelectionManager().getStart(),
            controller.getSelectionManager().getEnd(),
            textSource,
            controller));
  }
}
