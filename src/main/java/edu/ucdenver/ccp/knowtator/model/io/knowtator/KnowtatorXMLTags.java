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

package edu.ucdenver.ccp.knowtator.model.io.knowtator;

public class KnowtatorXMLTags {

    public static final String ID = "id";
    // For each document in the project
    public static final String DOCUMENT = "document";
    // Annotation meta-data
    public static final String ANNOTATION = "annotation";
    public static final String ANNOTATOR = "annotator";  // also in OLD
    public static final String TYPE = "type";
    public static final String SPAN = "span";
    public static final String SPAN_START = "start";
    public static final String SPAN_END = "end";
    public static final String CLASS = "class";
    // Graph data
    public static final String GRAPH = "graph-space";
    public static final String VERTEX = "vertex";
    public static final String TRIPLE = "triple";
    public static final String TRIPLE_SUBJECT = "subject";
    public static final String TRIPLE_OBJECT = "object";
    public static final String TRIPLE_PROPERTY = "property";
    public static final String TRIPLE_QUANTIFIER = "quantifier";
    public static final String TRIPLE_VALUE = "value";
    // Used for saving profile settings
    public static final String PROFILE = "profile";
    public static final String HIGHLIGHTER = "highlighter";
    public static final String COLOR = "color";
    // ***************NEW Tags*************
    // Start of every project
    static final String KNOWTATOR_PROJECT = "knowtator-project";
    // Identity annotation specific
    static final String IDENTITY_ANNOTATION = "identity-annotation";
    static final String COREFERRENCE = "coreferrence";
    // User settings
    static final String CONFIG = "config";
    static final String AUTO_LOAD_ONTOLOGIES = "auto-load-ontologies";
    static final String FORMAT = "format";
    static final String DEFAULT_SAVE_LOCATION = "default-save-location";

}
