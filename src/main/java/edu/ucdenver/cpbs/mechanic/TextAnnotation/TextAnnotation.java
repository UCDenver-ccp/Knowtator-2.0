package edu.ucdenver.cpbs.mechanic.TextAnnotation;

import org.semanticweb.owlapi.model.OWLClass;

public class TextAnnotation {

    private String textSource;
    private String mentionSource;
    private Integer mentionID;
    private String annotatorID;
    private String annotator;
    private Integer spanStart;
    private Integer spanEnd;
    private String spannedText;
    private String classID;
    private String className;
    private OWLClass owlClass;

    public TextAnnotation(String textSource,
                          String mentionSource,
                          Integer mentionID,
                          String annotatorID,
                          String annotator,
                          Integer spanStart,
                          Integer spanEnd,
                          String spannedText,
                          String classID,
                          String className,
                          OWLClass owlClass) {
        this.textSource = textSource;
        this.mentionSource = mentionSource;
        this.mentionID = mentionID;
        this.annotatorID = annotatorID;
        this.annotator = annotator;
        this.spanStart = spanStart;
        this.spanEnd = spanEnd;
        this.spannedText = spannedText;
        this.classID = classID;
        this.className = className;
        this.owlClass = owlClass;
    }

    public TextAnnotation(String document,
                          String mentionSource,
                          Integer mentionID,
                          String annotatorID,
                          String annotator,
                          Integer spanStart,
                          Integer spanEnd,
                          String spannedText) {
        this.textSource = document;
        this.mentionSource = mentionSource;
        this.mentionID = mentionID;
        this.annotatorID = annotatorID;
        this.annotator = annotator;
        this.spanStart = spanStart;
        this.spanEnd = spanEnd;
        this.spannedText = spannedText;

    }

    public String getSpannedText() {
        return spannedText;
    }

    public String getAnnotatorID() {
        return annotatorID;
    }

    public String getAnnotator() {
        return annotator;
    }

    public Integer getSpanStart() {
        return spanStart;
    }

    public Integer getSpanEnd() {
        return spanEnd;
    }

    public String getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    OWLClass getOwlClass() {
        return owlClass;
    }

    public String getTextSource() {
        return textSource;
    }

    public String getMentionSource() {
        return mentionSource;
    }

    public Integer getMentionID() {
        return mentionID;
    }

    public void setOwlClass(OWLClass OWLClass) {
        this.owlClass = OWLClass;
    }
}
