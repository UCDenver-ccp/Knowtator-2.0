/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 *  This code was taken from the wordfreak project and modified for Knowtator.  See
 *  license above for more information.
 *
 */
package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Span implements Savable {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(KnowtatorManager.class);

	private int start;

	private int end;
	private Annotation annotation;
	private String spannedText;

	public Span(int start, int end, String spannedText) {
		this.start = start;
		this.end = end;
		this.spannedText = spannedText;
		if (start > end) {
			throw new IndexOutOfBoundsException(
					"Span is invalid because the start of the Span is greater than the end of it: start=" + start
							+ " end=" + end);
		}
		if (start < 0) {
			throw new IndexOutOfBoundsException(
					"Span is invalid because the start of the Span is less than zero: start=" + start);
		}
	}

	public static Span makeDefaultSpan() {
		return new Span(0, 0, "");
	}

	public boolean equals(Object object) {
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

	/**
	 * we need some junit tests
	 */
	public boolean intersects(Span span) {
		int spanStart = span.getStart();
		// either Span's start is in this or this' start is in Span
		return this.contains(span)
				|| span.contains(this)
				|| (getStart() <= spanStart && spanStart < getEnd() || spanStart <= getStart()
						&& getStart() < span.getEnd());
	}

	public static boolean intersects(TreeSet<Span> spans1, TreeSet<Span> spans2) {
		for (Span span1 : spans1) {
			for (Span span2 : spans2) {
				if (span1.intersects(span2))
					return true;
			}
		}
		return false;
	}

	/**
	 * This method assumes that the both lists of spans are sorted the same way
	 * and that a Span in one list at the same index as a Span in the other list
	 * should be the same.
	 * 
	 * @param spans1
	 *            sorted list of c
	 * @param spans2
	 *            sorted list of spans
	 * @return true if the two lists of spans are the same.
	 */
	public static boolean spansMatch(TreeSet<Span> spans1, TreeSet<Span> spans2) {
		if (spans1.size() == spans2.size()) {
			Iterator<Span> spans1Iterator = spans1.iterator(), spans2Iterator = spans2.iterator();
			while(spans1Iterator.hasNext()) {
				if (!spans1Iterator.next().equals(spans2Iterator.next())) {
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

	void shrinkEnd() {
		if (end > start) end -= 1;
	}
	void shrinkStart() {
		if (start < end) start += 1;
	}
	void growEnd(int limit) {
		if (end < limit) end += 1;
	}
	void growStart() {
		if (start > 0) start -= 1;
	}

	static int compare(Span span1, Span span2) {
		if (span1 == span2) {
			return 0;
		}

		int compare = span1.getStart().compareTo(span2.getStart());
		if (compare == 0) {
			compare = span1.getEnd().compareTo(span2.getEnd());
		}
		if (compare == 0) {
			return -1;
		}
		return compare;
	}

	public String toString() {
		return String.format("Span: Start: %d, End: %d", start, end);
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	String getSpannedText() {
		return spannedText;
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

	public void writeToKnowtatorXml(Document dom, Element annotationElem) {
		Element spanElement = dom.createElement(KnowtatorXMLTags.SPAN);
		spanElement.setAttribute(KnowtatorXMLTags.SPAN_START, String.valueOf(start));
		spanElement.setAttribute(KnowtatorXMLTags.SPAN_END, String.valueOf(end));
		spanElement.setTextContent(getSpannedText());
		annotationElem.appendChild(spanElement);
	}

	@Override
	public void readFromKnowtatorXml(Element parent, String content) {

	}

	@Override
	public void readFromOldKnowtatorXml(Element parent) {

	}

	@Override
	public void readFromBratStandoff(Map<Character, List<String[]>> annotationMap, String content) {

	}

	@Override
	public void writeToBratStandoff(Writer writer) throws IOException {
		writer.append(String.format("%d %d", start, end));
	}

	@Override
	public void readFromGeniaXml(Element parent, String content) {

	}


	//	public static Span shortest(List<Span> spans) {
//		if (spans.size() == 0)
//			return null;
//		if (spans.size() == 1)
//			return spans.get(0);
//
//		Span shortestSpan = spans.get(0);
//		int shortestSize = shortestSpan.getSize();
//		for (int i = 1; i < spans.size(); i++) {
//			if (spans.get(i).getSize() < shortestSize) {
//				shortestSpan = spans.get(i);
//				shortestSize = shortestSpan.getSize();
//			}
//		}
//
//		return shortestSpan;
//	}

	//	public int compareTo(Span span) {
//		if (getStart() < span.getStart()) {
//			return -1;
//		} else if (getStart() == span.getStart()) {
//			return Integer.compare(span.getEnd(), getEnd());
//		} else {
//			return 1;
//		}
//	}

	//	public boolean crosses(Span span) {
//		int spanStart = span.getStart();
//
//		// either s's start is in this or this' start is in s
//		return !this.contains(span)
//				&& !span.contains(this)
//				&& (getStart() <= spanStart && spanStart < getEnd() || spanStart <= getStart()
//						&& getStart() < span.getEnd());
//	}

//	public boolean lessThan(Span span) {
//		return getStart() < span.getStart() && getEnd() < span.getEnd();
//	}

//	public boolean greaterThan(Span span) {
//		return getStart() > span.getStart() && getEnd() > span.getEnd();
//	}

//	public static Span parseSpan(String spanString) {
//		String startString = spanString.substring(0, spanString.indexOf("|"));
//		String endString = spanString.substring(spanString.indexOf("|") + 1);
//		int start = Integer.parseInt(startString);
//		int end = Integer.parseInt(endString);
//		return new Span(start, end);
//	}

	//	public static boolean isValid(int start, int end) {
//		return start <= end && start >= 0;
//	}
}
