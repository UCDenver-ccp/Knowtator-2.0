package edu.ucdenver.cpbs.mechanic.TextAnnotation;

import edu.ucdenver.cpbs.mechanic.MechAnICSelectionModel;
import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ProfileManager;
import edu.ucdenver.cpbs.mechanic.owl.OWLAPIDataExtractor;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public final class TextAnnotationManager {
    private static final Logger log = Logger.getLogger(MechAnICView.class);

   private HashMap<Integer, TextAnnotation> textAnnotations;
   private TextAnnotation selectedAnnotation;

   private MechAnICView view;
    private MechAnICSelectionModel selectionModel;
    private ProfileManager profileManager;
   private OWLAPIDataExtractor dataExtractor;
    private String textSource;

    public TextAnnotationManager(MechAnICView view, String textSource) {
        this.view = view;
        selectionModel = view.getSelectionModel();
        profileManager = view.getProfileManager();
        dataExtractor = new OWLAPIDataExtractor(view.getOWLModelManager());
        this.textSource = textSource;

        textAnnotations = new HashMap<>();
    }



    public void addTextAnnotation(OWLClass cls, Integer spanStart, Integer spanEnd, String spannedText) throws NoSuchFieldException {

        dataExtractor.extractOWLClassData(cls);

        //TODO: Figure out what mention is
        String mentionSource = "Default";
        int mentionID = textAnnotations.size();
        String classID = dataExtractor.getClassID();
        String className = dataExtractor.getClassName();

        TextAnnotation newTextAnnotation = new TextAnnotation(
                textSource,
                mentionSource,
                mentionID,
                profileManager.getCurrentProfile().getAnnotatorID(),
                profileManager.getCurrentProfile().getAnnotatorName(),
                spanStart,
                spanEnd,
                spannedText,
                classID,
                className,
                cls
        );
        textAnnotations.put(mentionID, newTextAnnotation);
        setSelectedAnnotation(newTextAnnotation);
    }

    public void removeTextAnnotation(Integer spanStart, Integer spanEnd, MechAnICTextViewer textViewer) {

        //String mentionSource = "Default";
        for (Map.Entry<Integer, TextAnnotation> instance : textAnnotations.entrySet()) {
            int mentionID = instance.getKey();
            TextAnnotation textAnnotation = instance.getValue();
            if (Objects.equals(spanStart, textAnnotation.getSpanStart()) && Objects.equals(spanEnd, textAnnotation.getSpanEnd())) {
                textAnnotations.remove(mentionID);
                highlightAllAnnotations(textViewer);
                return;
            }
        }

    }


    private void highlightAllAnnotations(MechAnICTextViewer textViewer) {
        textViewer.getHighlighter().removeAllHighlights();
        for (Map.Entry<Integer, TextAnnotation> instance2 : textAnnotations.entrySet() ){
            TextAnnotation textAnnotation = instance2.getValue();
            highlightAnnotation(textAnnotation.getSpanStart(), textAnnotation.getSpanEnd(), textViewer, textAnnotation.getOwlClass());
        }
    }

    public void highlightAnnotation(int spanStart, int spanEnd, MechAnICTextViewer textViewer, OWLClass cls) {
        DefaultHighlighter.DefaultHighlightPainter highlighter = profileManager.getCurrentProfile().getHighlighter(cls);
        if (highlighter == null) {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", cls.toString()), Color.CYAN);
            if (c != null) {
                profileManager.addHighlighter(cls, c, profileManager.getCurrentProfile());
            }
            highlighter = profileManager.getCurrentProfile().getHighlighter(cls);
        }

        try {
            textViewer.getHighlighter().addHighlight(spanStart, spanEnd, highlighter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    //TODO hover over annotations in text to see what they are

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public HashMap<Integer, TextAnnotation> getTextAnnotations() {
        return textAnnotations;
    }

    public TextAnnotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    private void setSelectedAnnotation(TextAnnotation selectedAnnotation) {
        this.selectedAnnotation = selectedAnnotation;
        view.setGlobalSelection1(selectedAnnotation.getOwlClass());
    }

    public void setSelectedAnnotation(Integer spanStart, Integer spanEnd) {
        for (Map.Entry<Integer, TextAnnotation> instance : textAnnotations.entrySet()) {
            TextAnnotation textAnnotation = instance.getValue();
            if (Objects.equals(spanStart, textAnnotation.getSpanStart()) && Objects.equals(spanEnd, textAnnotation.getSpanEnd())) {
                setSelectedAnnotation(textAnnotation);
                return;
            }
        }
    }
}
