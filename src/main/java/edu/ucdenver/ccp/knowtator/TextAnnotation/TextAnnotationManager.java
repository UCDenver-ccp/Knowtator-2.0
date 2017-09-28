package edu.ucdenver.ccp.knowtator.TextAnnotation;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ProfileManager;
import edu.ucdenver.ccp.knowtator.iaa.AssertionRelationship;
import edu.ucdenver.ccp.knowtator.iaa.Span;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.KnowtatorSelectionModel;
import edu.ucdenver.ccp.knowtator.iaa.Annotation;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public final class TextAnnotationManager {
    private static final Logger log = Logger.getLogger(KnowtatorView.class);

    private ArrayList<Annotation> textAnnotations;
    private Annotation selectedAnnotation;
    private KnowtatorView view;
    private KnowtatorSelectionModel selectionModel;
    private ProfileManager profileManager;
    public OWLAPIDataExtractor getDataExtractor() {
        return dataExtractor;
    }
    private OWLAPIDataExtractor dataExtractor;
    private String textSource;
    private KnowtatorTextPane textPane;
    private ArrayList<AssertionRelationship> assertionRelationships;

    public TextAnnotationManager(KnowtatorView view, String textSource, KnowtatorTextPane textPane) {
        this.view = view;
        selectionModel = view.getSelectionModel();
        profileManager = view.getProfileManager();
        dataExtractor = new OWLAPIDataExtractor(view.getOWLModelManager());
        this.textSource = textSource;
        this.textPane = textPane;

        selectedAnnotation = null;
        textAnnotations = new ArrayList<>();
        assertionRelationships = new ArrayList<>();
    }

    public void addTextAnnotation(OWLClass cls, Integer spanStart, Integer spanEnd) throws NoSuchFieldException {

        // Extract info from OWLClass
        dataExtractor.extractOWLObjectData(cls);

        // Make new annotation
        Annotation newTextAnnotation = new Annotation(textSource, cls);
        newTextAnnotation.setClassID(dataExtractor.getClassID());
        newTextAnnotation.setClassName(dataExtractor.getClassName());
        newTextAnnotation.setAnnotatorID(profileManager.getCurrentAnnotator().getAnnotatorID());
        newTextAnnotation.setAnnotatorName(profileManager.getCurrentAnnotator().getAnnotatorName());

        // Assign new annotation the text span
        Span newSpan = new Span(spanStart, spanEnd);
        newTextAnnotation.setSpan(newSpan);

        // Add annotation to annotation manager
        textAnnotations.add(newTextAnnotation);
        setSelectedAnnotation(newTextAnnotation);
        highlightAnnotation(spanStart, spanEnd, cls);

        view.getGraphViewer().addAnnotationNode(newTextAnnotation);
    }

    public void addTextAnnotation(Annotation newTextAnnotation) throws NoSuchFieldException {

        // Add annotation to annotation manager
        textAnnotations.add(newTextAnnotation);
        setSelectedAnnotation(newTextAnnotation);
        for (Span span : newTextAnnotation.getSpans()) {
            highlightAnnotation(span.getStart(), span.getEnd(), newTextAnnotation.getOwlClass());
        }
//        view.getGraphViewer().addAnnotationNode(newTextAnnotation);
    }

    public void removeTextAnnotation(Integer spanStart, Integer spanEnd) {

        //String mentionSource = "Default";
        for (Annotation textAnnotation : textAnnotations) {

            for (Span span : textAnnotation.getSpans()) {
                if (Objects.equals(spanStart, span.getStart()) && Objects.equals(spanEnd, span.getEnd())) {
                    textAnnotations.remove(textAnnotation);
                    highlightAllAnnotations();
                    return;
                }
            }
        }
    }

    public void removeTextAnnotation(Annotation textAnnotation) {

        for (Span span : textAnnotation.getSpans()) {
            textAnnotations.remove(textAnnotation);
            highlightAllAnnotations();
        }
    }

    private void highlightAllAnnotations() {
        textPane.getHighlighter().removeAllHighlights();
        for (Annotation textAnnotation: textAnnotations ){
            for (Span span : textAnnotation.getSpans()) {
                highlightAnnotation(span.getStart(), span.getEnd(), textAnnotation.getOwlClass());
            }
        }
    }

    private void highlightAnnotation(int spanStart, int spanEnd, OWLClass cls) {
        DefaultHighlighter.DefaultHighlightPainter highlighter = profileManager.getCurrentAnnotator().getHighlighter(cls);
        if (highlighter == null) {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", cls.toString()), Color.CYAN);
            if (c != null) {
                profileManager.addHighlighter(cls, c, profileManager.getCurrentAnnotator());
            }
            highlighter = profileManager.getCurrentAnnotator().getHighlighter(cls);
        }

        try {
            textPane.getHighlighter().addHighlight(spanStart, spanEnd, highlighter);
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

    public Set<Annotation> getAnnotationsInRange(Integer spanStart, Integer spanEnd) {
        Set<Annotation> annotationsInRange = new HashSet<>();

        for (Annotation textAnnotation : textAnnotations) {
            for (Span span : textAnnotation.getSpans()) {
                if (spanStart >= span.getStart() && spanEnd <= span.getEnd()) {
                    annotationsInRange.add(textAnnotation);
                }
            }
        }

        return annotationsInRange;
    }

    public AssertionRelationship addAssertion() {
        OWLObjectProperty property = view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();
        dataExtractor.extractOWLObjectData(property);
        AssertionRelationship newAssertionRelationship = new AssertionRelationship(property, dataExtractor.getClassName());
        assertionRelationships.add(newAssertionRelationship);
        return newAssertionRelationship;
    }

    public void setSelectedAnnotation(Annotation selectedAnnotation) {
        this.selectedAnnotation = selectedAnnotation;
        view.setGlobalSelection1(selectedAnnotation.getOwlClass());
    }
}
