package edu.ucdenver.ccp.knowtator.annotation.annotator;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Annotator {
    public Logger log = Logger.getLogger(KnowtatorManager.class);

    public String name;
    public String id;
    public HashMap<String, Color> colors;  //<ClassName, Highlighter>
    public KnowtatorManager manager;

    public String getName() {
        return name;
    }

    public Annotator(KnowtatorManager manager, String name, String id) {
        this.name = name;
        this.id = id;
        this.manager = manager;

        colors = new HashMap<>();

        log.warn(String.format("%1$-30s %2$30s", "Annotator", toString()));
    }

    public Color getColor(String className) {
        if (colors.containsKey(className)) {
            return colors.get(className);
        } else {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", className), Color.CYAN);
            if (c != null) {
                addColor(className, c);
            }
            return colors.get(className);
        }

    }

    public void addColor(String className, String color) {
        Color c = Color.decode(color);
        c = new Color((float) c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255, 1f);

        colors.put(className, c);
    }

    public void addColor(String className, Color c) {
        colors.put(className, c);

    }

    public String getID() {
        return id;
    }

    public String toString() {
        return String.format("Name: %s, ID: %s", name, id);
    }

    public HashMap<String, Color> getColors() {
        return colors;
    }
}
