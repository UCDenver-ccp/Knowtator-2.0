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

package edu.ucdenver.ccp.knowtator.model.object;

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;

/** The type Span. */
public class Span implements ConceptAnnotationBoundModelObject<Span> {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(KnowtatorModel.class);

  private int start;
  private int end;
  private ConceptAnnotation conceptAnnotation;
  private KnowtatorModel model;
  private String id;

  /**
   * Instantiates a new Span.
   *
   * @param conceptAnnotation the concept annotation
   * @param id the id
   * @param start the start
   * @param end the end
   */
  public Span(@Nonnull ConceptAnnotation conceptAnnotation, String id, int start, int end) {
    this.model = conceptAnnotation.getKnowtatorModel();

    this.id = id;
    this.start = start;
    this.end = end;
    this.conceptAnnotation = conceptAnnotation;

    model.verifyId(id, this, false);

    if (start > end) {
      throw new IndexOutOfBoundsException(
          String.format(
              "Span is invalid because "
                  + "the start of the Span is greater than the end of it: start=%d end=%d",
              start, end));
    }
    if (start < 0) {
      throw new IndexOutOfBoundsException(
          String.format(
              "Span is invalid because the start of the Span is less than zero: start=%d", start));
    }
  }

  /**
   * Instantiates a new Span.
   *
   * @param start the start
   * @param end the end
   */
  public Span(int start, int end) {
    this.start = start;
    this.end = end;
    if (start > end) {
      throw new IndexOutOfBoundsException(
          String.format(
              "Span is invalid because "
                  + "the start of the Span is greater than the end of it: start=%d end=%d",
              start, end));
    }
    if (start < 0) {
      throw new IndexOutOfBoundsException(
          String.format(
              "Span is invalid because the start of the Span is less than zero: start=%d", start));
    }
  }

  public TextSource getTextSource() {
    return conceptAnnotation.getTextSource();
  }

  /*
  COMPARISON
   */

  /**
   * Intersects boolean.
   *
   * @param spans1 the spans 1
   * @param spans2 the spans 2
   * @return the boolean
   */
  public static boolean intersects(TreeSet<Span> spans1, TreeSet<Span> spans2) {
    for (Span span1 : spans1) {
      for (Span span2 : spans2) {
        if (span1.intersects(span2)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * We need some junit tests .
   *
   * @param span the span.
   * @return the boolean
   */
  public boolean intersects(Span span) {
    int spanStart = span.getStart();
    // either Span's start is in this or this' start is in Span
    return this.contains(span)
        || span.contains(this)
        || (getStart() <= spanStart && spanStart < getEnd()
        || spanStart <= getStart() && getStart() < span.getEnd());
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
      Iterator<Span> spans1Iterator = spans1.iterator();
      Iterator<Span> spans2Iterator = spans2.iterator();
      while (spans1Iterator.hasNext()) {
        if (!spans1Iterator.next().equalStartAndEnd(spans2Iterator.next())) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Substring string.
   *
   * @param string the string
   * @param span the span
   * @return the string
   */
  public static String substring(String string, Span span) {
    int start = Math.max(0, span.getStart());
    start = Math.min(start, string.length() - 1);
    int end = Math.max(0, span.getEnd());
    end = Math.min(end, string.length() - 1);
    return string.substring(start, end);
  }

  @Override
  public int compareTo(Span span2) {
    int result = ConceptAnnotationBoundModelObject.super.compareTo(span2);
    if (result == 0) {
      if (span2 == null) {
        result = 1;
      }
      if (result == 0) {
        result = getStart().compareTo(span2.getStart());
        if (result == 0) {
          result = getEnd().compareTo(span2.getEnd());
        }
        if (result == 0) {
          result = id.compareTo(span2.getId());
        }
      }
    }
    return result;
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

  /**
   * Contains boolean.
   *
   * @param i the
   * @return the boolean
   */
  public boolean contains(int i) {
    return (getStart() <= i && i < getEnd());
  }



  /*
  MODIFIERS
   */

  /**
   * Modify.
   *
   * @param startModification the start modification
   * @param endModification the end modification
   */
  public void modify(int startModification, int endModification) {


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

    int limit = getTextSource().getContent().length();
    if (end > limit) {
      end = limit;
    }

    model.fireModelEvent(new ChangeEvent<>(model, null, this));
  }

  public String toString() {
    return String.format("%s", getSpannedText());
  }

  public ConceptAnnotation getConceptAnnotation() {
    return conceptAnnotation;
  }

  /**
   * Gets spanned text.
   *
   * @return the spanned text
   */
  public String getSpannedText() {
    try {
      return getTextSource().getContent().substring(start, end);
    } catch (StringIndexOutOfBoundsException e) {
      log.warn(
          String.format(
              "The span %d, %d for %s is out of bounds", start, end, conceptAnnotation.getId()));
      return "";
    }
  }

  /**
   * Gets start.
   *
   * @return the start
   */
  public Integer getStart() {
    return start;
  }

  /**
   * Gets end.
   *
   * @return the end
   */
  public Integer getEnd() {
    return end;
  }

  /**
   * Gets size.
   *
   * @return the size
   */
  public int getSize() {
    return getEnd() - getStart();
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
  public void dispose() {}

  @Override
  public KnowtatorModel getKnowtatorModel() {
    return model;
  }
}
