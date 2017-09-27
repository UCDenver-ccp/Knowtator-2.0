package edu.ucdenver.cpbs.mechanic.TextAnnotation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.ucdenver.cpbs.mechanic.MechAnICSelectionModel;
import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ProfileManager;
import edu.ucdenver.cpbs.mechanic.iaa.Annotation;
import edu.ucdenver.cpbs.mechanic.iaa.AssertionRelationship;
import edu.ucdenver.cpbs.mechanic.iaa.Span;
import edu.ucdenver.cpbs.mechanic.owl.OWLAPIDataExtractor;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public final class TextAnnotationManager {
    private static final Logger log = Logger.getLogger(MechAnICView.class);

   private ArrayList<Annotation> textAnnotations;
   private Annotation selectedAnnotation;

   private MechAnICView view;
   private MechAnICSelectionModel selectionModel;
   private ProfileManager profileManager;
   private OWLAPIDataExtractor dataExtractor;
   private String textSource;

    private ArrayList<AssertionRelationship> assertionRelationships;

    public TextAnnotationManager(MechAnICView view, String textSource) {
        this.view = view;
        selectionModel = view.getSelectionModel();
        profileManager = view.getProfileManager();
        dataExtractor = new OWLAPIDataExtractor(view.getOWLModelManager());
        this.textSource = textSource;

        selectedAnnotation = null;
        textAnnotations = new ArrayList<>();
        assertionRelationships = new ArrayList<>();
    }



    public void addTextAnnotation(OWLClass cls, Integer spanStart, Integer spanEnd) throws NoSuchFieldException {

        // Extract info from OWLClass
        dataExtractor.extractOWLObjectData(cls);
        String classID = dataExtractor.getClassID();
        String className = dataExtractor.getClassName();

        // Make new annotation
        Annotation newTextAnnotation = new Annotation(textSource, cls, className);

        newTextAnnotation.setSimpleFeature("Annotator ID", profileManager.getCurrentAnnotator().getAnnotatorID());
        newTextAnnotation.setSimpleFeature("Annotator Name", profileManager.getCurrentAnnotator().getAnnotatorName());
        newTextAnnotation.setSimpleFeature("Class ID", classID);
        newTextAnnotation.setSimpleFeature("Class Name", className);

        // Assign new annotation the text span
        Span newSpan = new Span(spanStart, spanEnd);
        newTextAnnotation.setSpan(newSpan);

        // Add annotation to annotation manager
        textAnnotations.add(newTextAnnotation);
        setSelectedAnnotation(newTextAnnotation);
        highlightAnnotation(spanStart, spanEnd, (MechAnICTextViewer) ((JScrollPane) view.getTextViewerTabbedPane().getSelectedComponent()).getViewport().getView(), cls);

        view.getGraphViewer().addAnnotationNode(newTextAnnotation);
    }

    public void removeTextAnnotation(Integer spanStart, Integer spanEnd, MechAnICTextViewer textViewer) {

        //String mentionSource = "Default";
        for (Annotation textAnnotation : textAnnotations) {

            for (Span span : textAnnotation.getSpans()) {
                if (Objects.equals(spanStart, span.getStart()) && Objects.equals(spanEnd, span.getEnd())) {
                    textAnnotations.remove(textAnnotation);
                    highlightAllAnnotations(textViewer);
                    return;
                }
            }
        }

    }


    private void highlightAllAnnotations(MechAnICTextViewer textViewer) {
        textViewer.getHighlighter().removeAllHighlights();
        for (Annotation textAnnotation: textAnnotations ){
            for (Span span : textAnnotation.getSpans()) {
                highlightAnnotation(span.getStart(), span.getEnd(), textViewer, textAnnotation.getOwlClass());
            }
        }
    }

    public void highlightAnnotation(int spanStart, int spanEnd, MechAnICTextViewer textViewer, OWLClass cls) {
        DefaultHighlighter.DefaultHighlightPainter highlighter = profileManager.getCurrentAnnotator().getHighlighter(cls);
        if (highlighter == null) {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", cls.toString()), Color.CYAN);
            if (c != null) {
                profileManager.addHighlighter(cls, c, profileManager.getCurrentAnnotator());
            }
            highlighter = profileManager.getCurrentAnnotator().getHighlighter(cls);
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

    public ArrayList<Annotation> getTextAnnotations() {
        return textAnnotations;
    }

    public Annotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    private void setSelectedAnnotation(Annotation selectedAnnotation) {
        this.selectedAnnotation = selectedAnnotation;
        view.setGlobalSelection1(selectedAnnotation.getOwlClass());
    }

    public void setSelectedAnnotation(Integer spanStart, Integer spanEnd) {
        for (Annotation textAnnotation : textAnnotations) {
            for (Span span : textAnnotation.getSpans()) {
                if (Objects.equals(spanStart, span.getStart()) && Objects.equals(spanEnd, span.getEnd())) {
                    setSelectedAnnotation(textAnnotation);
                    return;
                }
            }
        }
    }

    public AssertionRelationship addAssertion() {
        OWLObjectProperty property = view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();
        dataExtractor.extractOWLObjectData(property);
        AssertionRelationship newAssertionRelationship = new AssertionRelationship(property, dataExtractor.getClassName());
        assertionRelationships.add(newAssertionRelationship);
        return newAssertionRelationship;
    }
}
