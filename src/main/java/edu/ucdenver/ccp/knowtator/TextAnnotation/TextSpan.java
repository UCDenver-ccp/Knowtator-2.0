/*
 * ????
 * 1/5/2006 this code was copied from the Knowtator annotation tool
 * for internal use at Mayo.  The Knowtator code was originally copied
 * from the WordFreak library and modified extensively for Knowtator.  
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is the WordFreak annotation tool.
 *
 * The Initial Developer of the Original Code is Thomas S. Morton
 * Copyright (C) 2002.  All Rights Reserved.
 * 
 * Contributor(s):
 *   Thomas S. Morton <tsmorton@cis.upenn.edu> (Original Author)
 *   Jeremy LaCivita <lacivita@linc.cis.upenn.edu>
 */

/*
 *  This code was taken from the wordfreak project and modified for Knowtator.  See
 *  license above for more information.
 *
 */
package edu.ucdenver.ccp.knowtator.TextAnnotation;

import java.util.List;

@SuppressWarnings("unused")
public class TextSpan implements Comparable {
	public int start;

	public int end;

	public TextSpan(int start, int end) {
		this.start = start;
		this.end = end;
		if (start > end) {
			throw new IndexOutOfBoundsException(
					"TextSpan is invalid because the start of the span is greater than the end of it: start=" + start
							+ " end=" + end);
		}
		if (start < 0) {
			throw new IndexOutOfBoundsException(
					"TextSpan is invalid because the start of the span is less than zero: start=" + start);
		}
	}

	public static boolean isValid(int start, int end) {
		return start <= end && start >= 0;
	}

	public int length() {
		return end - start;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	/**
	 * @return the length of the span (end - start)
	 * 
	 */
	public int getSize() {
		return getEnd() - getStart();
	}

	public int compareTo(Object object) {
		TextSpan textSpan = (TextSpan) object;
		if (getStart() < textSpan.getStart()) {
			return -1;
		} else if (getStart() == textSpan.getStart()) {
			return Integer.compare(textSpan.getEnd(), getEnd());
		} else {
			return 1;
		}
	}

	public String toString() {
		return "" + start + "|" + end;
	}

	public boolean equals(Object object) {
		if (object == null || !(object instanceof TextSpan)) {
			return false;
		}
		TextSpan textSpan = (TextSpan) object;
		return getStart() == textSpan.getStart() && getEnd() == textSpan.getEnd();
	}

	public int hashCode() {
		return ((this.start << 16) | (0x0000FFFF | this.end));
	}

	public boolean contains(TextSpan textSpan) {
		return (getStart() <= textSpan.getStart() && textSpan.getEnd() <= getEnd());
	}

	public boolean contains(int i) {
		return (getStart() <= i && i < getEnd());
	}

	/**
	 * we need some junit tests
	 */
	public boolean intersects(TextSpan textSpan) {
		int spanStart = textSpan.getStart();
		// either textSpan's start is in this or this' start is in textSpan
		return this.contains(textSpan)
				|| textSpan.contains(this)
				|| (getStart() <= spanStart && spanStart < getEnd() || spanStart <= getStart()
						&& getStart() < textSpan.getEnd());
	}

	public boolean crosses(TextSpan textSpan) {
		int spanStart = textSpan.getStart();

		// either s's start is in this or this' start is in s
		return !this.contains(textSpan)
				&& !textSpan.contains(this)
				&& (getStart() <= spanStart && spanStart < getEnd() || spanStart <= getStart()
						&& getStart() < textSpan.getEnd());
	}

	public boolean lessThan(TextSpan textSpan) {
		return getStart() < textSpan.getStart() && getEnd() < textSpan.getEnd();
	}

	public boolean greaterThan(TextSpan textSpan) {
		return getStart() > textSpan.getStart() && getEnd() > textSpan.getEnd();
	}

	public static TextSpan parseSpan(String spanString) {
		String startString = spanString.substring(0, spanString.indexOf("|"));
		String endString = spanString.substring(spanString.indexOf("|") + 1);
		int start = Integer.parseInt(startString);
		int end = Integer.parseInt(endString);
		return new TextSpan(start, end);
	}

	static boolean intersects(List<TextSpan> spans1, List<TextSpan> spans2) {
		for (TextSpan textSpan1 : spans1) {
			for (TextSpan textSpan2 : spans2) {
				if (textSpan1.intersects(textSpan2))
					return true;
			}
		}
		return false;
	}

	/**
	 * This method assumes that the both lists of spans are sorted the same way
	 * and that a span in one list at the same index as a span in the other list
	 * should be the same.
	 * 
	 * @param spans1
	 *            sorted list of spans
	 * @param spans2
	 *            sorted list of spans
	 * @return true if the two lists of spans are the same.
	 */
	static boolean spansMatch(List<TextSpan> spans1, List<TextSpan> spans2) {
		if (spans1.size() == spans2.size()) {
			for (int i = 0; i < spans1.size(); i++) {
				if (!spans1.get(i).equals(spans2.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static TextSpan shortest(List<TextSpan> textSpans) {
		if (textSpans.size() == 0)
			return null;
		if (textSpans.size() == 1)
			return textSpans.get(0);

		TextSpan shortestTextSpan = textSpans.get(0);
		int shortestSize = shortestTextSpan.getSize();
		for (int i = 1; i < textSpans.size(); i++) {
			if (textSpans.get(i).getSize() < shortestSize) {
				shortestTextSpan = textSpans.get(i);
				shortestSize = shortestTextSpan.getSize();
			}
		}

		return shortestTextSpan;
	}

	public static String substring(String string, TextSpan textSpan) {
		int start = Math.max(0, textSpan.getStart());
		start = Math.min(start, string.length() - 1);
		int end = Math.max(0, textSpan.getEnd());
		end = Math.min(end, string.length() - 1);
		return string.substring(start, end);
	}
}
