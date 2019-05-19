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

package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class creates an index on a collection of annotations based on the proximity of the spans of
 * the annotations.
 */
public class AnnotationSpanIndex {

  private final Map<Integer, Set<ConceptAnnotation>> window2AnnotationsMap;

  private final int windowSize;

  /**
   * Instantiates a new Annotation span index.
   *
   * @param conceptAnnotations the concept annotations
   */
  public AnnotationSpanIndex(Collection<ConceptAnnotation> conceptAnnotations) {
    this(conceptAnnotations, 20);
  }

  @SuppressWarnings("SameParameterValue")
  private AnnotationSpanIndex(Collection<ConceptAnnotation> conceptAnnotations, int windowSize) {
    this.windowSize = windowSize;
    window2AnnotationsMap = new HashMap<>();
    for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
      for (Span span : conceptAnnotation) {
        int startKey = span.getStart() / windowSize;
        int endKey = span.getEnd() / windowSize;
        for (int key = startKey; key <= endKey; key++) {
          addToMap(key, conceptAnnotation);
        }
      }
    }

    // for(Integer window : window2AnnotationsMap.keySet())
    // {
    // Set<concept> windowAnnotations =
    // window2AnnotationsMap.getAnnotation(window);
    // for(concept windowAnnotation : windowAnnotations)
    // {
    // Collection<Span> spans = windowAnnotation;
    // }
    // }
  }

  private void addToMap(int key, ConceptAnnotation conceptAnnotation) {
    if (!window2AnnotationsMap.containsKey(key)) {
      window2AnnotationsMap.put(key, new HashSet<>());
    }
    window2AnnotationsMap.get(key).add(conceptAnnotation);
  }

  private Set<ConceptAnnotation> getNearbyAnnotations(ConceptAnnotation conceptAnnotation) {
    HashSet<Integer> windows = new HashSet<>();
    for (Span span : conceptAnnotation) {
      windows.add(span.getStart() / windowSize);
      windows.add(span.getEnd() / windowSize);
    }

    HashSet<ConceptAnnotation> returnValues = new HashSet<>();
    for (Integer window : windows) {
      Set<ConceptAnnotation> windowConceptAnnotations = window2AnnotationsMap.get(window);
      if (windowConceptAnnotations != null) {
        returnValues.addAll(windowConceptAnnotations);
      }
    }

    returnValues.remove(conceptAnnotation);
    return returnValues;
  }

  /**
   * Gets overlapping annotations.
   *
   * @param conceptAnnotation the concept annotation
   * @return the overlapping annotations
   */
  public Set<ConceptAnnotation> getOverlappingAnnotations(ConceptAnnotation conceptAnnotation) {
    Set<ConceptAnnotation> nearbyConceptAnnotations = getNearbyAnnotations(conceptAnnotation);
    Set<ConceptAnnotation> returnValues = new HashSet<>();
    for (ConceptAnnotation nearbyConceptAnnotation : nearbyConceptAnnotations) {
      if (Iaa.spansOverlap(conceptAnnotation, nearbyConceptAnnotation)) {
        returnValues.add(nearbyConceptAnnotation);
      }
    }
    return returnValues;
  }

  /**
   * Gets exactly overlapping annotations.
   *
   * @param conceptAnnotation the concept annotation
   * @return the exactly overlapping annotations
   */
  Set<ConceptAnnotation> getExactlyOverlappingAnnotations(ConceptAnnotation conceptAnnotation) {
    Set<ConceptAnnotation> nearbyConceptAnnotations = getNearbyAnnotations(conceptAnnotation);
    Set<ConceptAnnotation> returnValues = new HashSet<>();
    for (ConceptAnnotation nearbyConceptAnnotation : nearbyConceptAnnotations) {
      if (Iaa.spansMatch(conceptAnnotation, nearbyConceptAnnotation)) {
        returnValues.add(nearbyConceptAnnotation);
      }
    }
    return returnValues;
  }
}
