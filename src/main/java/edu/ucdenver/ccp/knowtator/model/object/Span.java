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

package edu.ucdenver.ccp.knowtator.model.object;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Span implements TextBoundModelObject<Span>, KnowtatorXMLIO, BratStandoffIO {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(KnowtatorModel.class);

  private int start;
  private int end;
  private ConceptAnnotation conceptAnnotation;
  private BaseModel model;
  private String id;

  public Span(@Nonnull BaseModel model, @Nonnull ConceptAnnotation conceptAnnotation, String id, int start, int end) {
    this.model = model;

    this.id = id;
    this.start = start;
    this.end = end;
    this.conceptAnnotation = conceptAnnotation;

    model.verifyId(id, this, false);

    if (start > end) {
      throw new IndexOutOfBoundsException(
              String.format("Span is invalid because the start of the Span is greater than the end of it: start=%d end=%d", start, end));
    }
    if (start < 0) {
      throw new IndexOutOfBoundsException(
              String.format("Span is invalid because the start of the Span is less than zero: start=%d", start));
    }
  }

  public Span(int start, int end) {
    this.start = start;
    this.end = end;
    if (start > end) {
      throw new IndexOutOfBoundsException(
              String.format("Span is invalid because the start of the Span is greater than the end of it: start=%d end=%d", start, end));
    }
    if (start < 0) {
      throw new IndexOutOfBoundsException(
              String.format("Span is invalid because the start of the Span is less than zero: start=%d", start));
    }
  }


  public TextSource getTextSource() {
    return conceptAnnotation.getTextSource();
  }

	/*
  COMPARISON
   */

  public static boolean intersects(TreeSet<Span> spans1, TreeSet<Span> spans2) {
    for (Span span1 : spans1) {
      for (Span span2 : spans2) {
        if (span1.intersects(span2)) return true;
      }
    }
    return false;
  }

  /**
   * This method assumes that the both lists of spans are sorted the same way and that a Span in one
   * list at the same index as a Span in the other list should be the same.
   *
   * @param spans1 sorted list of c
   * @param spans2 sorted list of spans
   * @return true if the two lists of spans are the same.
   */
  public static boolean spansMatch(TreeSet<Span> spans1, TreeSet<Span> spans2) {
    if (spans1.size() == spans2.size()) {
      Iterator<Span> spans1Iterator = spans1.iterator(), spans2Iterator = spans2.iterator();
      while (spans1Iterator.hasNext()) {
        if (!spans1Iterator.next().equalStartAndEnd(spans2Iterator.next())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public static String substring(String string, Span span) {
    int start = Math.max(0, span.getStart());
    start = Math.min(start, string.length() - 1);
    int end = Math.max(0, span.getEnd());
    end = Math.min(end, string.length() - 1);
    return string.substring(start, end);
  }

  @Override
  public int compareTo(Span span2) {
    if (span2 == null) {
      return 1;
    }
    int compare = getStart().compareTo(span2.getStart());
    if (compare == 0) {
      compare = getEnd().compareTo(span2.getEnd());
    }
    if (compare == 0) {
      compare = id.compareTo(span2.getId());
    }
    return compare;
  }

  private boolean equalStartAndEnd(Object object) {
    if (!(object instanceof Span)) {
      return false;
    }
    Span span = (Span) object;
    return Objects.equals(getStart(), span.getStart()) && Objects.equals(getEnd(), span.getEnd());
  }

  public int hashCode() {
    return ((this.start << 16) | (0x0000FFFF | this.end));
  }

  private boolean contains(Span span) {
    return (getStart() <= span.getStart() && span.getEnd() <= getEnd());
  }

  public boolean contains(int i) {
    return (getStart() <= i && i < getEnd());
  }

  /** we need some junit tests */
  public boolean intersects(Span span) {
    int spanStart = span.getStart();
    // either Span's start is in this or this' start is in Span
    return this.contains(span)
        || span.contains(this)
        || (getStart() <= spanStart && spanStart < getEnd()
            || spanStart <= getStart() && getStart() < span.getEnd());
  }

  /*
  MODIFIERS
   */

  public void modify(int startModification, int endModification) {
    int limit = getTextSource().getContent().length();

    start += startModification;
    if (end < start) {
      start = end;
    }
    if (start < 0) {
      start = 0;
    }
    end += endModification;
    if (end < start) {
      end = start;
    }
    if (end > limit) {
      end = limit;
    }

    model.fireModelEvent(new ChangeEvent<>(model, null, this));
  }

  /*
  TRANSLATORS
   */

  public String toString() {

	  return String.format("%s (%d : %d)", getSpannedText(), start, end);
  }

  /*
  GETTERS
   */

  public ConceptAnnotation getConceptAnnotation() {
    return conceptAnnotation;
  }

  public String getSpannedText() {
    try {
      return getTextSource().getContent().substring(start, end);
    } catch (StringIndexOutOfBoundsException e) {
      log.warn(String.format("The span %d, %d for %s is out of bounds", start, end, conceptAnnotation.getId()));
      return "";
    }
  }

  public Integer getStart() {
    return start;
  }

  public Integer getEnd() {
    return end;
  }

  public int getSize() {
    return getEnd() - getStart();
  }


  /**
   These methods are intended to correct for Java's handling of supplementary unicode characters.
   */
//  public int getStartCodePoint() { return Character.codePointCount(textSource.getContent(), 0, start); }
//
//  public int getEndCodePoint() { return Character.codePointCount(textSource.getContent(), 0, end); }

  /*
  SETTERS
   */

  /*
  READERS
   */

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {}

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}


  /*
  WRITERS
   */

  @Override
  public void writeToKnowtatorXML(Document dom, Element annotationElem) {
    Element spanElement = dom.createElement(KnowtatorXMLTags.SPAN);
    spanElement.setAttribute(KnowtatorXMLAttributes.SPAN_START, String.valueOf(getStart()));
    spanElement.setAttribute(KnowtatorXMLAttributes.SPAN_END, String.valueOf(getEnd()));
    spanElement.setAttribute(KnowtatorXMLAttributes.ID, id);
    spanElement.setTextContent(getSpannedText());
    annotationElem.appendChild(spanElement);
  }

  @Override
  public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

  }

  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
    String[] spanLines = getSpannedText().split("\n");
    int spanStart = getStart();
    for (int j = 0; j < spanLines.length; j++) {
      writer.append(String.format("%d %d", spanStart, spanStart + spanLines[j].length()));
      if (j != spanLines.length -1) {
        writer.append(";");
      }
      spanStart += spanLines[j].length() + 1;
    }

  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public void dispose() {

  }
}
