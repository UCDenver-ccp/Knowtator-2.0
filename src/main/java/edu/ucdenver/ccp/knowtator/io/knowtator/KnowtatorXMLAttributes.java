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

package edu.ucdenver.ccp.knowtator.io.knowtator;

public class KnowtatorXMLAttributes {
	public static final String ID = "id";

	public static final String ANNOTATOR = "annotator"; // also in OLD
	public static final String TYPE = "type";
	public static final String SPAN_START = "start";
	public static final String SPAN_END = "end";

	// Graph data
	public static final String TRIPLE_SUBJECT = "subject";
	public static final String TRIPLE_OBJECT = "object";
	public static final String TRIPLE_PROPERTY = "property";
	public static final String TRIPLE_QUANTIFIER = "quantifier";
	public static final String TRIPLE_VALUE = "value";
	public static final String IS_NEGATED = "polarity";
	public static final String IS_NEGATED_FALSE = "positive";
	public static final String IS_NEGATED_TRUE = "negative";
	public static final String X_LOCATION = "x";
	public static final String Y_LOCATION = "y";

	// Used for saving profile settings
	public static final String COLOR = "color";
	public static final String CLASS_ID = "class";
	public static final String FILE = "text-file";
	public static final String LABEL = "label";
}
