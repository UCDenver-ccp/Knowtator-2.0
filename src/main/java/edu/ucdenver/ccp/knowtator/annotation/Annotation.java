package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

import java.awt.*;
import java.util.Date;

public class Annotation {

    public String id;
    private final Date date;
    TextSource textSource;
    Profile annotator;


    Annotation(String annotationID, TextSource textSource, Profile annotator) {
        this.textSource = textSource;
        this.annotator = annotator;
        this.date = new Date();
        this.id = annotationID;
    }

    public TextSource getTextSource() {
        return textSource;
    }
    public Profile getAnnotator() {
        return annotator;
    }
    public Date getDate() {
        return date;
    }
    public String getID() {
        return id;
    }
    public Color getColor () {
        return annotator.getColor(id, null);
    }

    @Override
    public String toString() {
        return  String.format("%s", id);
    }

    void setID(String id) {
        this.id = id;
    }
}
