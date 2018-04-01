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

package edu.ucdenver.ccp.knowtator.model.io.uima;

public class UIMAXMITags {

    // Under xmi namespace
    static final String XMI_XMI = "xmi:XMI";

    public static final String XMI_VERSION = "xmi:version";

    // Under cas namespace
    public static final String CAS_NULL = "cas:NULL";
    public static final String CAS_SOFA = "cas:Sofa";
    public static final String CAS_VIEW = "cas:View";

    // Under tcas namespace
    public static final String TCAS_DOCUMENT_ANNOTATION = "tcas:DocumentAnnotation";


    // Under xmlns namespace
    public static final String XMLNS_ANNOTATION = "xmlns:annotation";
    public static final String XMLNS_MENTION = "xmlns:mention";
    public static final String XMLNS_METADATA = "xmlns:metadata";
    public static final String XMLNS_TCAS = "xmlns:tcas";
    public static final String XMLNS_CAS = "xmlns:cas";
    public static final String XMLNS_XMI = "xmlns:xmi";

    // Under annotation namespace
    public static final String ANNOTATION_DOCUMENT_INFORMATION = "annotation:CCPDocumentInformation";
    public static final String ANNOTATION_TEXT_ANNOTATION = "annotation:CCPTextAnnotation";
    public static final String ANNOTATION_SPAN = "annotation:CCPSpan";
    public static final String ANNOTATION_ANNOTATOR = "annotation:CCPAnnotator";


    // Under mention namespace
    public static final String MENTION_CLASS_MENTION = "mention:CCPClassMention";
    public static final String CCP_TEXT_ANNOTATION = "ccpTextAnnotation";

}
