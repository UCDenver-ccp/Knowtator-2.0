package edu.ucdenver.cpbs.mechanic.Profiles;

import javafx.scene.control.RadioButton;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile {
    private String annotatorName;
    private String annotatorID;
    private HashMap<String, DefaultHighlighter.DefaultHighlightPainter>  highlighters;
    private ArrayList<JRadioButton> radioButtons;
    private ButtonGroup buttonGroup;

    public Profile(String annotatorName, String annotatorID) {
        this.annotatorName = annotatorName;
        this.annotatorID = annotatorID;

        highlighters = new HashMap<String, DefaultHighlighter.DefaultHighlightPainter>();
        radioButtons = new ArrayList<JRadioButton>();
        buttonGroup = new ButtonGroup();
    }

    public void addHighlighter(String highlighterName, DefaultHighlighter.DefaultHighlightPainter newHighlighter, JRadioButton btn) {

        highlighters.put(highlighterName, newHighlighter);
        radioButtons.add(btn);
    }

    public ArrayList<JRadioButton> getRadioButtons() {
        return radioButtons;
    }

    public DefaultHighlighter.DefaultHighlightPainter getHighlighter(String highlighterName) {
        return highlighters.get(highlighterName);
    }

    public String getAnnotatorID() {
        return annotatorID;
    }

    public void setAnnotatorID(String annotatorID) {
        this.annotatorID = annotatorID;
    }

    public String getAnnotatorName() {
        return annotatorName;
    }

    public void setAnnotatorName(String annotatorName) {
        this.annotatorName = annotatorName;
    }

}
