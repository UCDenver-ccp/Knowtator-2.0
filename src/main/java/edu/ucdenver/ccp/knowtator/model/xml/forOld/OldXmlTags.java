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

package edu.ucdenver.ccp.knowtator.model.xml.forOld;

public class OldXmlTags {
    // ******************OLD Tags*****************
    public static final String ANNOTATIONS = "annotations";
    public static final String ID = "id";
    public static final String ANNOTATOR = "annotator";
    public static final String SPAN = "span";
    public static final String SPAN_START = "start";
    public static final String SPAN_END = "end";
    public static final String ANNOTATION = "annotation";
    static final String TEXT_SOURCE = "textSource";
    // Annotations
    static final String MENTION = "mention";
    static final String SPANNED_TEXT = "spannedText";
    // Class assigned to named entity
    static final String CLASS_MENTION = "classMention";
    static final String MENTION_CLASS = "mentionClass";  // The named entity label
    static final String HAS_SLOT_MENTION = "hasSlotMention";
    // Complex Slot Mentions
    static final String COMPLEX_SLOT_MENTION = "complexSlotMention";
    static final String MENTION_SLOT = "mentionSlot";
    static final String COMPLEX_SLOT_MENTION_VALUE = "complexSlotMentionValue";
    static final String COMPLEX_SLOT_MENTION_VALUE_VALUE = "value";
    // Coreference specific IDs
    static final String MENTION_CLASS_ID_IDENTITY = "IDENTITY chain";
    static final String MENTION_CLASS_IDENTITY = "IDENTITY chain";
    static final String MENTION_SLOT_ID_COREFERENCE = "Coreferring strings";
    static final String COMPLEX_SLOT_MENTION_ID_APPOS_HEAD = "APPOS Head";
    static final String COMPLEX_SLOT_MENTION_ID_APPOS_ATTRIBUTES = "APPOS Attributes";
}
