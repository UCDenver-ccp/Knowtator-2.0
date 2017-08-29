package edu.ucdenver.cpbs.mechanic.Profiles;

import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.text.DefaultHighlighter;
import java.util.HashMap;

public class Profile {
    private String annotatorName;
    private String annotatorID;
    private HashMap<OWLClass, DefaultHighlighter.DefaultHighlightPainter>  highlighters;

    public Profile(String annotatorName, String annotatorID) {
        this.annotatorName = annotatorName;
        this.annotatorID = annotatorID;

        highlighters = new HashMap<>();
    }

    public void addHighlighter(OWLClass cls, DefaultHighlighter.DefaultHighlightPainter newHighlighter) {
        highlighters.put(cls, newHighlighter);
    }

    public DefaultHighlighter.DefaultHighlightPainter getHighlighter(OWLClass cls) {
        return highlighters.get(cls);
    }

    public String getAnnotatorID() {
        return annotatorID;
    }

    public String getAnnotatorName() {
        return annotatorName;
    }

}
