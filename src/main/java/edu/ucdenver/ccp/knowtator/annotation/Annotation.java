package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

import java.util.Date;

public class Annotation {

    public String id;
    private final Date date;
    TextSource textSource;
    Profile annotator;


    Annotation(String id, TextSource textSource, Profile annotator) {
        this.textSource = textSource;
        this.annotator = annotator;
        this.date = new Date();
        this.id = id;
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

    @Override
    public String toString() {
        return  String.format("%s", id);
    }

    void setID(String id) {
        this.id = id;
    }
}
