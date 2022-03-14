package edu.ucdenver.ccp.knowtator.io.uima;

import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.uima.annotation.CCPDocumentInformation;
import edu.ucdenver.ccp.knowtator.uima.annotation.CCPSpan;
import edu.ucdenver.ccp.knowtator.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.knowtator.uima.assertion.CCPGraphSpace;
import edu.ucdenver.ccp.knowtator.uima.assertion.CCPTriple;
import edu.ucdenver.ccp.knowtator.uima.assertion.CCPVertex;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.stream.IntStream;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

/** The type Uimaxmi util. */
public class UIMAXMIUtil {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(UIMAXMIUtil.class);

  // private static final File ANNOTATOR_DESCRIPTOR = new
  // File("E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml");


  /**
   * Write.
   *
   * @param standaloneSavable the standalone savable
   * @param file the file
   */
  public void startWrite(Savable standaloneSavable, File file) {
    final URL ANNOTATOR_DESCRIPTOR =
        UIMAXMIUtil.class.getResource("/KnowtatorToUIMAAnnotatorDescriptor.xml");
    try {
      XMLInputSource input = new XMLInputSource(ANNOTATOR_DESCRIPTOR);
      AnalysisEngineDescription description =
          UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input);
      AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(description);

      if (standaloneSavable instanceof TextSourceCollection) {
        ((TextSourceCollection) standaloneSavable)
            .getCollection()
            .forEach(
                textSource -> {
                  try {
                    CAS cas = analysisEngine.newCAS();
                    writeToOutputFile(
                        textSource,
                        new File(
                            file.getAbsolutePath() + File.separator + textSource.getId() + ".xmi"),
                        cas);
                  } catch (IOException | ResourceInitializationException e) {
                    e.printStackTrace();
                  }
                });
      } else if (standaloneSavable instanceof TextSource) {
        CAS cas = analysisEngine.newCAS();
        writeToOutputFile((TextSource) standaloneSavable, file, cas);
      }
    } catch (IOException | InvalidXMLException | ResourceInitializationException e) {
      e.printStackTrace();
    }
  }

  private void writeToOutputFile(TextSource textSource, File file, CAS cas) throws IOException {
    log.warn("Writing to " + file.getAbsolutePath());
    CAS textSourceAsCAS = cas.createView(textSource.getId());
    convertTextSourceToUIMA(textSource, textSourceAsCAS);
    CasIOUtils.save(cas, new FileOutputStream(file), SerialFormat.XMI);
  }

  private void convertTextSourceToUIMA(TextSource textSource, CAS textSourceAsCAS) {
    textSourceAsCAS.setDocumentText(textSource.getContent());
    textSourceAsCAS.setDocumentLanguage("en");

    Type documentType = textSourceAsCAS.getTypeSystem().getType(CCPDocumentInformation._TypeName);
    CCPDocumentInformation documentFS = textSourceAsCAS.createFS(documentType);
    documentFS.setDocumentCollectionID(0);
    documentFS.setDocumentID(textSource.getId());
    documentFS.setDocumentSize(0);
    documentFS.setEncoding("UTF_8");

    textSourceAsCAS.getIndexRepository().addFS(documentFS);

    convertAnnotationManagerToUIMA(textSource, textSourceAsCAS);
  }

  private void convertAnnotationManagerToUIMA(TextSource textSource, CAS textSourceAsCAS) {
    textSource
        .getConceptAnnotations()
        .forEach(
            annotation -> {
              try {
                convertAnnotationToUIMA(annotation, textSourceAsCAS);
              } catch (CASException e) {
                e.printStackTrace();
              }
            });
    textSource
        .getGraphSpaces()
        .forEach(
            graphSpace -> {
              try {
                convertGraphSpaceToUIMA(graphSpace, textSourceAsCAS);
              } catch (CASException e) {
                e.printStackTrace();
              }
            });
  }

  private void convertGraphSpaceToUIMA(GraphSpace graphSpace, CAS textSourceAsCAS)
      throws CASException {
    Type graphSpaceType = textSourceAsCAS.getTypeSystem().getType(CCPGraphSpace._TypeName);
    CCPGraphSpace graphSpaceFS = textSourceAsCAS.createFS(graphSpaceType);
    graphSpaceFS.setGraphSpaceID(graphSpace.getId());

    //        Object[] vertices = graphSpace.getChildVertices(graphSpace.getDefaultParent());
    //        FSArray<CCPVertex> verticesFS = new FSArray<>(textSourceAsCAS.getJCas(),
    // vertices.length);
    //        IntStream.range(0, vertices.length).forEach(i -> {
    //            AnnotationNode vertex = (AnnotationNode) vertices[i];
    //            CCPVertex vertexFS = convertAnnotationNodeToUIMA(vertex, textSourceAsCAS);
    //            verticesFS.set(i, vertexFS);
    //        });

    Object[] triples = graphSpace.getChildEdges(graphSpace.getDefaultParent());
    FSArray<CCPTriple> triplesFS = new FSArray<>(textSourceAsCAS.getJCas(), triples.length);
    IntStream.range(0, triples.length)
        .forEach(
            i -> {
              RelationAnnotation triple = (RelationAnnotation) triples[i];
              CCPTriple tripleFS = convertTripleToUIMA(triple, textSourceAsCAS);
              triplesFS.set(i, tripleFS);
            });

    //        graphSpaceFS.setVertices(verticesFS);
    graphSpaceFS.setTriples(triplesFS);

    textSourceAsCAS.getIndexRepository().addFS(graphSpaceFS);
  }

  private CCPTriple convertTripleToUIMA(RelationAnnotation triple, CAS textSourceAsCAS) {
    Type tripleType = textSourceAsCAS.getTypeSystem().getType(CCPTriple._TypeName);

    CCPTriple tripleFS = textSourceAsCAS.createFS(tripleType);
    tripleFS.setAnnotator(triple.getAnnotator().getId());
    tripleFS.setTripleID(triple.getId());
    tripleFS.setObject(
        convertAnnotationNodeToUIMA((AnnotationNode) triple.getTarget(), textSourceAsCAS));
    tripleFS.setSubject(
        convertAnnotationNodeToUIMA((AnnotationNode) triple.getSource(), textSourceAsCAS));
    tripleFS.setProperty(triple.getValue().toString());
    tripleFS.setQuantifier(triple.getQuantifier().toString());
    tripleFS.setQuantifierValue(triple.getQuantifierValue());

    return tripleFS;
  }

  private CCPVertex convertAnnotationNodeToUIMA(AnnotationNode vertex, CAS textSourceAsCAS) {
    Type annotationNodeType = textSourceAsCAS.getTypeSystem().getType(CCPVertex._TypeName);
    CCPVertex vertexFS = textSourceAsCAS.createFS(annotationNodeType);
    vertexFS.setAnnotation(vertex.getConceptAnnotation().getId());
    vertexFS.setVertexID(vertex.getId());

    return vertexFS;
  }

  private void convertAnnotationToUIMA(ConceptAnnotation annotation, CAS textSourceAsCAS)
      throws CASException {
    Type annotationType = textSourceAsCAS.getTypeSystem().getType(CCPTextAnnotation._TypeName);

    CCPTextAnnotation annotationFS =
        (CCPTextAnnotation)
            textSourceAsCAS.createAnnotation(
                annotationType,
                annotation.getCollection().first().getStart(),
                annotation.getCollection().first().getEnd());
    FSArray<CCPSpan> spansFS =
        new FSArray<>(
            textSourceAsCAS.getJCas(), annotation.getCollection().size());

    Iterator<Span> spanIterator = annotation.getCollection().iterator();
    IntStream.range(0, annotation.getCollection().size())
        .forEach(
            i -> {
              Span span = spanIterator.next();
              CCPSpan spanFS = convertSpanToUIMA(span, textSourceAsCAS);
              spansFS.set(i, spanFS);
            });

    String owlClassID = annotation.getOwlClass();
    annotationFS.setOwlClass(owlClassID);
    annotationFS.setNumberOfSpans(annotation.getCollection().size());
    annotationFS.setSpans(spansFS);

    textSourceAsCAS.getIndexRepository().addFS(annotationFS);
  }

  private CCPSpan convertSpanToUIMA(Span span, CAS textSourceAsCAS) {
    Type spanType = textSourceAsCAS.getTypeSystem().getType(CCPSpan._TypeName);

    CCPSpan spanFS = textSourceAsCAS.createFS(spanType);
    spanFS.setSpanStart(span.getStart());
    spanFS.setSpanEnd(span.getEnd());
    return spanFS;
  }
}
