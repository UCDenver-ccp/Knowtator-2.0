/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.io.brat;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.Quantifier;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/** The type Brat standoff util. */
public class BratStandoffUtil {
  private static final Logger log = Logger.getLogger(BratStandoffUtil.class);

  private static Map<Character, List<String[]>> collectAnnotations(Stream<String> standoffStream) {
    Map<Character, List<String[]>> annotationCollector = createAnnotationCollector();

    standoffStream
        .filter(line -> !line.trim().isEmpty())
        .map(line -> line.split(StandoffTags.columnDelimiter))
        .forEach(entries -> annotationCollector.get(entries[0].charAt(0)).add(entries));

    return annotationCollector;
  }

  private static Map<Character, List<String[]>> createAnnotationCollector() {
    Map<Character, List<String[]>> annotationCollector = new HashMap<>();
    IntStream.range(0, StandoffTags.tagList.length)
        .mapToObj(i -> StandoffTags.tagList[i])
        .forEach(tag -> annotationCollector.put(tag, new ArrayList<>()));

    return annotationCollector;
  }

  /**
   * Write.
   *
   * @param textSource the text source
   * @param file the file
   */
  public void write(TextSource textSource, File file) {
    Map<String, Map<String, String>> visualConfig = new HashMap<>();

    visualConfig.put(StandoffTags.visualLabels, new HashMap<>());
    visualConfig.put(StandoffTags.visualDrawing, new HashMap<>());

    Map<String, Map<String, String>> annotationConfig = new HashMap<>();
    annotationConfig.put(StandoffTags.annotationsEvents, new HashMap<>());
    annotationConfig.put(StandoffTags.annotationsRelations, new HashMap<>());
    annotationConfig.put(StandoffTags.annotationsEntities, new HashMap<>());
    annotationConfig.put(StandoffTags.annotationsAttributes, new HashMap<>());

    File outputFile = new File(file.getParentFile(), String.format("%s.ann", file.getName()));
    writeToOutputFile(textSource, outputFile, visualConfig, annotationConfig);

    writeVisualConfiguration(file, visualConfig);
    writeAnnotationsConfiguration(file, annotationConfig);
  }

  private void writeAnnotationsConfiguration(
      File file, Map<String, Map<String, String>> annotationConfig) {
    try {
      BufferedWriter annotationConfigWriter =
          new BufferedWriter(
              new FileWriter(
                  String.format("%s%sconcept.conf", file.getAbsolutePath(), File.separator)));

      annotationConfig.forEach(
          (key, map) -> {
            try {
              annotationConfigWriter.append(String.format("[%s]\n", key));
              if (key.equals(StandoffTags.annotationsEntities)) {
                map.forEach(
                    (classID, value) -> {
                      try {
                        annotationConfigWriter.append(classID).append("\n");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          });

      annotationConfigWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeVisualConfiguration(File file, Map<String, Map<String, String>> visualConfig) {
    try {
      BufferedWriter visualConfigWriter =
          new BufferedWriter(
              new FileWriter(
                  String.format("%s%svisual.conf", file.getAbsolutePath(), File.separator)));
      visualConfig.forEach(
          (key, map) -> {
            try {
              visualConfigWriter.append(String.format("[%s]\n", key));
              if (key.equals(StandoffTags.visualLabels)) {
                map.forEach(
                    (classID, label) -> {
                      try {
                        visualConfigWriter.append(String.format("%s | %s\n", classID, label));
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
              } else if (key.equals(StandoffTags.visualDrawing)) {
                map.forEach(
                    (classID, color) -> {
                      try {
                        visualConfigWriter.append(String.format("%s \t %s\n", classID, color));
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          });

      visualConfigWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeToOutputFile(
      TextSource textSource,
      File file,
      Map<String, Map<String, String>> annotationConfig,
      Map<String, Map<String, String>> visualConfig) {
    try {
      log.info(String.format("Writing to %s", file.getAbsolutePath()));
      BufferedWriter bw =
          new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
      writeFromTextSource(textSource, bw, annotationConfig, visualConfig);

      bw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Read to text source collection.
   *
   * @param model the model
   * @param file the file
   */
  public void readToTextSourceCollection(KnowtatorModel model, File file) {
    try {
      Stream<String> standoffStream = Files.lines(Paths.get(file.toURI()));

      Map<Character, List<String[]>> annotationMap = collectAnnotations(standoffStream);

      annotationMap
          .get(StandoffTags.DOCID)
          .add(new String[] {FilenameUtils.getBaseName(file.getName())});

      String textSourceId = annotationMap.get(StandoffTags.DOCID).get(0)[0];

      TextSource newTextSource = new TextSource(model, file, textSourceId);
      model.getTextSources().add(newTextSource);
      readToTextSource(newTextSource, annotationMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void readToTextSource(
      TextSource textSource, Map<Character, List<String[]>> annotationMap) {
    readToConceptAnnotationCollection(
        textSource.getKnowtatorModel(),
        textSource,
        textSource.getConceptAnnotations(),
        annotationMap);
    //  readToGraphSpaceCollection(textSource, textSource.getGraphSpaces(), annotationMap);
  }

  private void readToConceptAnnotationCollection(
      KnowtatorModel model,
      TextSource textSource,
      ConceptAnnotationCollection conceptAnnotationCollection,
      Map<Character, List<String[]>> annotationMap) {
    Profile profile = model.getDefaultProfile();

    annotationMap
        .get(StandoffTags.TEXTBOUNDANNOTATION)
        .forEach(
            annotation -> {
              String owlClassID =
                  annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0];
              ConceptAnnotation newConceptAnnotation =
                  new ConceptAnnotation(
                      textSource,
                      annotation[0],
                      owlClassID,
                      profile,
                      "identity",
                      "",
                      BaseModel.DEFAULT_LAYERS);
              conceptAnnotationCollection.add(newConceptAnnotation);
              Map<Character, List<String[]>> map = new HashMap<>();
              List<String[]> list = new ArrayList<>();
              list.add(annotation);
              map.put(StandoffTags.TEXTBOUNDANNOTATION, list);
              readToConceptAnnotation(newConceptAnnotation, map);
            });

    annotationMap
        .get(StandoffTags.NORMALIZATION)
        .forEach(
            normalization -> {
              //              String[] splitNormalization =
              //                  normalization[1].split(StandoffTags.relationTripleDelimiter);
              //              String annotationID = splitNormalization[1];
              //              String owlClassID = splitNormalization[2];
            });

    GraphSpace newGraphSpace = new GraphSpace(textSource, "Brat Relation Graph");
    textSource.add(newGraphSpace);
    readToGraphSpace(newGraphSpace, annotationMap);
  }

  private void readToConceptAnnotation(
      ConceptAnnotation conceptAnnotation, Map<Character, List<String[]>> annotationMap) {
    String[] triple =
        annotationMap
            .get(StandoffTags.TEXTBOUNDANNOTATION)
            .get(0)[1]
            .split(StandoffTags.textBoundAnnotationTripleDelimiter);
    int spanStart = Integer.parseInt(triple[1]);
    for (int i = 2; i < triple.length; i++) {
      int spanEnd = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[0]);

      Span span = new Span(conceptAnnotation, null, spanStart, spanEnd);
      conceptAnnotation.add(span);

      if (i != triple.length - 1) {
        spanStart = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[1]);
      }
    }
  }

  private void readToGraphSpace(
      GraphSpace graphSpace, Map<Character, List<String[]>> annotationMap) {
    annotationMap
        .get(StandoffTags.RELATION)
        .forEach(
            annotation -> {
              String id = annotation[0];
              String[] relationTriple = annotation[1].split(StandoffTags.relationTripleDelimiter);
              String propertyID = relationTriple[0];
              String subjectAnnotationID =
                  relationTriple[1].split(StandoffTags.relationTripleRoleIDDelimiter)[1];
              String objectAnnotationID =
                  relationTriple[2].split(StandoffTags.relationTripleRoleIDDelimiter)[1];

              Profile annotator = graphSpace.getKnowtatorModel().getDefaultProfile();

              graphSpace
                  .getTextSource()
                  .getConceptAnnotations()
                  .get(subjectAnnotationID)
                  .ifPresent(
                      subjectConceptAnnotation -> {
                        AnnotationNode source =
                            graphSpace.getAnnotationNodeForConceptAnnotation(
                                subjectConceptAnnotation);

                        graphSpace
                            .getTextSource()
                            .getConceptAnnotations()
                            .get(objectAnnotationID)
                            .ifPresent(
                                objectConceptAnnotation -> {
                                  AnnotationNode target =
                                      graphSpace.getAnnotationNodeForConceptAnnotation(
                                          objectConceptAnnotation);

                                  graphSpace.addTriple(
                                      source,
                                      target,
                                      id,
                                      annotator,
                                      propertyID,
                                      Quantifier.some,
                                      null,
                                      false,
                                      "");
                                });
                      });
            });
  }

  private void writeFromTextSource(
      TextSource textSource,
      Writer writer,
      Map<String, Map<String, String>> annotationConfig,
      Map<String, Map<String, String>> visualConfig)
      throws IOException {
    writeFromConceptAnnotationCollection(
        textSource.getConceptAnnotations(), writer, annotationConfig, visualConfig);
    //        writeFromGraphSpaceCollection(textSource.getGraphSpaces(), writer, annotationConfig,
    // visualConfig);
  }

  private void writeFromConceptAnnotationCollection(
      ConceptAnnotationCollection conceptAnnotationCollection,
      Writer writer,
      Map<String, Map<String, String>> annotationsConfig,
      Map<String, Map<String, String>> visualConfig)
      throws IOException {
    Iterator<ConceptAnnotation> annotationIterator = conceptAnnotationCollection.iterator();
    for (int i = 0; i < conceptAnnotationCollection.size(); i++) {
      ConceptAnnotation conceptAnnotation = annotationIterator.next();
      conceptAnnotation.setBratID(String.format("T%d", i));
      writeFromConceptAnnotation(conceptAnnotation, writer, annotationsConfig, visualConfig);
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
              model.getOWLAPIDataExtractor().getOwlEntityRendering(triple.getProperty());
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

  private void writeFromConceptAnnotation(
      ConceptAnnotation conceptAnnotation,
      Writer writer,
      Map<String, Map<String, String>> annotationsConfig,
      Map<String, Map<String, String>> visualConfig)
      throws IOException {
    String renderedOwlClassID =
        conceptAnnotation
            .getKnowtatorModel()
            .getOwlEntityRendering(conceptAnnotation.getOwlClass())
            .replace(":", "_")
            .replace(" ", "_");
    annotationsConfig.get(StandoffTags.annotationsEntities).put(renderedOwlClassID, "");

    writer.append(String.format("%s\t%s ", conceptAnnotation.getBratID(), renderedOwlClassID));

    visualConfig.get("labels").put(renderedOwlClassID, conceptAnnotation.getOwlClassRendering());
    visualConfig
        .get("drawing")
        .put(
            renderedOwlClassID,
            String.format("bgColor:%s", Profile.convertToHex(conceptAnnotation.getColor())));

    Iterator<Span> spanIterator = conceptAnnotation.iterator();
    StringBuilder spannedText = new StringBuilder();
    for (int i = 0; i < conceptAnnotation.size(); i++) {
      Span span = spanIterator.next();
      writeFromSpan(span, writer);
      String[] spanLines = span.getSpannedText().split("\n");
      for (int j = 0; j < spanLines.length; j++) {
        spannedText.append(spanLines[j]);
        if (j != spanLines.length - 1) {
          spannedText.append(" ");
        }
      }
      if (i != conceptAnnotation.size() - 1) {
        writer.append(";");
        spannedText.append(" ");
      }
    }
    writer.append(String.format("\t%s%n", spannedText.toString()));
  }

  private void writeFromSpan(Span span, Writer writer) throws IOException {
    String[] spanLines = span.getSpannedText().split("\n");
    int spanStart = span.getStart();
    for (int j = 0; j < spanLines.length; j++) {
      writer.append(String.format("%d %d", spanStart, spanStart + spanLines[j].length()));
      if (j != spanLines.length - 1) {
        writer.append(";");
      }
      spanStart += spanLines[j].length() + 1;
    }
  }
}
