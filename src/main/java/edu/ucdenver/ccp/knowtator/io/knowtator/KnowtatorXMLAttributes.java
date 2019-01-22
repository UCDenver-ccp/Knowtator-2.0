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

class KnowtatorXMLAttributes {
	public static final String ID = "id";

	static final String ANNOTATOR = "annotator"; // also in OLD
	static final String TYPE = "type";
	static final String SPAN_START = "start";
	static final String SPAN_END = "end";
	static final String MOTIVATION = "motivation";

	// Graph data
	static final String TRIPLE_SUBJECT = "subject";
	static final String TRIPLE_OBJECT = "object";
	static final String TRIPLE_PROPERTY = "property";
	static final String TRIPLE_QUANTIFIER = "quantifier";
	static final String TRIPLE_VALUE = "value";
	static final String IS_NEGATED = "polarity";
	static final String IS_NEGATED_FALSE = "positive";
	static final String IS_NEGATED_TRUE = "negative";
	static final String X_LOCATION = "x";
	static final String Y_LOCATION = "y";

	// Used for saving profile settings
	static final String COLOR = "color";
	static final String CLASS_ID = "class";
	static final String FILE = "text-file";
	static final String LABEL = "label";
}
