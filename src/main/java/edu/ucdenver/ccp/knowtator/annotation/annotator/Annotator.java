package edu.ucdenver.ccp.knowtator.annotation.annotator;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;

public class Annotator {
    public Logger log = Logger.getLogger(KnowtatorManager.class);

    public String name;
    public String id;
    public HashMap<String, DefaultHighlighter.DefaultHighlightPainter>  highlighters;  //<ClassName, Highlighter>
    public KnowtatorManager manager;

    public String getName() {
        return name;
    }

    public Annotator(KnowtatorManager manager, String name, String id) {
        this.name = name;
        this.id = id;
        this.manager = manager;

        highlighters = new HashMap<>();

        log.warn(String.format("%1$-30s %2$30s", "Annotator", toString()));
    }

    public DefaultHighlighter.DefaultHighlightPainter getHighlighter(String className) {
        if (highlighters.containsKey(className)) {
            return highlighters.get(className);
        } else {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", className), Color.CYAN);
            if (c != null) {
                addHighlighter(className, c);
            }
            return highlighters.get(className);
        }

    }

    public void addHighlighter(String className, String color) {
        Color c = Color.decode(color);
        c = new Color((float) c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255, 1f);

        addHighlighter(className, c);
    }

    public void addHighlighter(String className, Color c) {
        DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
        highlighters.put(className, newHighlighter);

    }

    public String getID() {
        return id;
    }

    public String toString() {
        return String.format("Name: %s, ID: %s", name, id);
    }
}
