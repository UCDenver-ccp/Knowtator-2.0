package edu.ucdenver.ccp.knowtator.Profiles;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;

public class Annotator {
    public String annotatorName;
    public String annotatorID;
    public HashMap<OWLClass, DefaultHighlighter.DefaultHighlightPainter>  highlighters;
    public KnowtatorView view;

    public Annotator(KnowtatorView view, String annotatorName, String annotatorID) {
        this.annotatorName = annotatorName;
        this.annotatorID = annotatorID;
        this.view = view;

        highlighters = new HashMap<>();
    }

    public void addHighlighter(OWLClass cls) {
        Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", cls.toString()), Color.CYAN);
        if (c != null) {
            DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
            highlighters.put(cls, newHighlighter);
            for(OWLClass decendent: view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls)) {
                highlighters.put(decendent, newHighlighter);
            }
        }

    }

    public DefaultHighlighter.DefaultHighlightPainter getHighlighter(OWLClass cls) {
        if (highlighters.containsKey(cls)) {
            return highlighters.get(cls);
        } else {
            addHighlighter(cls);
            return highlighters.get(cls);
        }

    }

    public String getAnnotatorID() {
        return annotatorID;
    }

    public String getAnnotatorName() {
        return annotatorName;
    }

}
