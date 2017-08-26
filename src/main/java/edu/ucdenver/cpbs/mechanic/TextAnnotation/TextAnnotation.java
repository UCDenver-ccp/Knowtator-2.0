package edu.ucdenver.cpbs.mechanic.TextAnnotation;

import org.semanticweb.owlapi.model.OWLClass;

@SuppressWarnings("FieldCanBeLocal")
public class TextAnnotation {


    private String annotatorID;
    private String annotator;
    private Integer spanStart;
    private Integer spanEnd;
    private String spannedText;
    private String classID;
    private String className;
    private OWLClass owlClass;

    public TextAnnotation(String annotatorID,
                          String annotator,
                          Integer spanStart,
                          Integer spanEnd,
                          String spannedText,
                          String classID,
                          String className,
                          OWLClass owlClass) {
        this.annotatorID = annotatorID;
        this.annotator = annotator;
        this.spanStart = spanStart;
        this.spanEnd = spanEnd;
        this.spannedText = spannedText;
        this.classID = classID;
        this.className = className;
        this.owlClass = owlClass;
    }

    public TextAnnotation(String annotatorID,
                          String annotator,
                          Integer spanStart,
                          Integer spanEnd,
                          String spannedText) {
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

    public OWLClass getOwlClass() {
        return owlClass;
    }
}
