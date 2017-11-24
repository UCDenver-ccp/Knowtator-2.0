package edu.ucdenver.ccp.knowtator.profile;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Profile {

    private String profileID;
    private HashMap<String, Color> colors;  //<ClassName, Highlighter>

    public String getProfileID() {
        return profileID;
    }

    public Profile(String profileID){
        this.profileID = profileID;

        colors = new HashMap<>();

//        log.warn(String.format("%1$-30s %2$30s", "Profile", toString()));
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

    private void addColor(String className, Color c) {
        colors.put(className, c);
    }


    public String toString() {
        return String.format("ID: %s", profileID);
    }

    public HashMap<String, Color> getColors() {
        return colors;
    }
}
