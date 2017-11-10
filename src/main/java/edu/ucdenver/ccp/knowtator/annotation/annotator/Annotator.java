package edu.ucdenver.ccp.knowtator.annotation.annotator;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;

public class Annotator {
    public String annotatorName;
    public String annotatorID;
    public HashMap<OWLClass, DefaultHighlighter.DefaultHighlightPainter>  highlighters;
    public KnowtatorManager manager;

    public Annotator(KnowtatorManager manager, String annotatorName, String annotatorID) {
        this.annotatorName = annotatorName;
        this.annotatorID = annotatorID;
        this.manager = manager;

        highlighters = new HashMap<>();
    }

    public void addHighlighter(OWLClass cls) {
        Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", cls.toString()), Color.CYAN);
        if (c != null) {
            addHighlighter(cls, c);
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

    public void addHighlighter(String classID, String className, String color) {
        Color c = Color.decode(color);

        c = new Color((float) c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255, 1f);

        manager.loadOntologyFromLocation(classID);

        OWLClass cls = OWLAPIDataExtractor.getOWLClassByID(manager, className);

        addHighlighter(cls, c);
    }

    public void addHighlighter(OWLClass cls, Color c) {
        DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);

        highlighters.put(cls, newHighlighter);
        for(OWLClass decendent: manager.getOwlModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls)) {
            highlighters.put(decendent, newHighlighter);
        }
    }
}
