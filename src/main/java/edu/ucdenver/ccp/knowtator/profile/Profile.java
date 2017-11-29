package edu.ucdenver.ccp.knowtator.profile;

import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Set;

public class Profile {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(Profile.class);

    private String profileID;
    private HashMap<String, Color> colors;  //<ClassName, Highlighter>
    private BasicKnowtatorView view;

    public String getProfileID() {
        return profileID;
    }

    public Profile(String profileID){
        this.profileID = profileID;

        colors = new HashMap<>();

//        log.warn(String.format("%1$-30s %2$30s", "Profile", toString()));
    }

    public Color getColor(String classID, String className) {
        if (colors.containsKey(classID)) {
            return colors.get(classID);
        } else {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", className == null ? classID : className), Color.CYAN);
            if (c != null) {
                addColor(classID, c);

                if (view != null && JOptionPane.showConfirmDialog(null, String.format("Assign color to descendents of %s?", className == null ? classID : className)) == JOptionPane.OK_OPTION) {
                    Set<OWLClass> decendents = OWLAPIDataExtractor.getSelectedOWLClassDecendents(view);
                    if (decendents != null) {
                        for (OWLClass decendent : decendents) {
                            String decClassID = OWLAPIDataExtractor.getIDByOwlEnt(decendent);

                            addColor(decClassID, c);
                        }
                    }
                }

            }
            return colors.get(classID);
        }

    }

    public void addColor(String classID, String color) {
        Color c = Color.decode(color);
        c = new Color((float) c.getRed()/255, (float)c.getGreen()/255, (float)c.getBlue()/255, 1f);

        colors.put(classID, c);
    }

   private void addColor(String classID, Color c) {
        colors.put(classID, c);
    }

    public String toString() {
        return String.format("ID: %s", profileID);
    }

    public HashMap<String, Color> getColors() {
        return colors;
    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
    }
}
