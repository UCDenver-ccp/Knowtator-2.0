package edu.ucdenver.ccp.knowtator.annotation.text;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.annotator.Annotator;
import edu.ucdenver.ccp.knowtator.iaa.AssertionRelationship;
import edu.ucdenver.ccp.knowtator.listeners.DocumentListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.text.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.*;


public final class AnnotationManager implements DocumentListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public HashMap<String, List<Annotation>> textAnnotations;
    public Annotation selectedAnnotation;
    public KnowtatorManager manager;
    public List<AssertionRelationship> assertionRelationships;

    public AnnotationManager(KnowtatorManager manager) {
        this.manager = manager;
        selectedAnnotation = null;
        textAnnotations = new HashMap<>();
        assertionRelationships = new ArrayList<>();
    }

    public void addTextSource(String textSource) {
        textAnnotations.put(textSource, new ArrayList<>());
    }

    public void addAnnotation(String textSource, String className, Integer spanStart, Integer spanEnd) {

        Annotator annotator = manager.getAnnotatorManager().getCurrentAnnotator();

        Annotation newAnnotation = new Annotation(manager, textSource, annotator, className);
        newAnnotation.addSpan(new Span(spanStart, spanEnd));

        // Add Annotation to Annotation manager
        textAnnotations.get(textSource).add(newAnnotation);

        setSelectedAnnotation(newAnnotation);
        manager.annotationsChangedEvent(newAnnotation);
    }

    public void addAnnotation(String textSource, Annotator annotator, String className, List<Span> spans) {

        if (!textAnnotations.containsKey(textSource)) {
            textAnnotations.put(textSource, new ArrayList<>());
        }

        Annotation newAnnotation = new Annotation(manager, textSource, annotator, className);
        newAnnotation.addAllSpans(spans);

        // Add Annotation to Annotation manager
        textAnnotations.get(textSource).add(newAnnotation);

        setSelectedAnnotation(newAnnotation);
        manager.annotationsChangedEvent(newAnnotation);
    }

    public void removeAnnotation(String textSource, Annotation textAnnotation) {
        textAnnotations.get(textSource).remove(textAnnotation);

        setSelectedAnnotation(null);
        manager.annotationsChangedEvent(null);
    }


    //TODO hover over annotations in text to see what they are

    public HashMap<String, List<Annotation>> getTextAnnotations() {
        return textAnnotations;
    }

    public Annotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    public List<Annotation> getAnnotationsContainingLocation(String textSource, Integer loc) {
        List<Annotation> annotationsInRange = new ArrayList<>();

        for (Annotation annotation : textAnnotations.get(textSource)) {
            if (annotation.getSpanContainingLocation(loc) != null) {
                annotationsInRange.add(annotation);
            }
        }

        return annotationsInRange;
    }

    public AssertionRelationship addAssertion() {
        OWLObjectProperty property = OWLAPIDataExtractor.getSelectedProperty(manager);

        AssertionRelationship newAssertionRelationship = new AssertionRelationship(property, OWLAPIDataExtractor.getClassNameByOWLClass(manager, property));
        assertionRelationships.add(newAssertionRelationship);
        return newAssertionRelationship;
    }

    public void setSelectedAnnotation(Annotation selectedAnnotation) {
        this.selectedAnnotation = selectedAnnotation;
        if (selectedAnnotation != null) {
            manager.owlSelectionChangedEvent(selectedAnnotation.getOwlClass());
        }
        manager.annotationsChangedEvent(selectedAnnotation);
    }

    @Override
    public void documentChanged(KnowtatorTextPane textPane) {
        addTextSource(textPane.getName());
    }

    public Collection<Annotation> getTextAnnotations(String name) {
        return textAnnotations.get(name);
    }

    public void addSpanToSelectedAnnotation(int selectionStart, int selectionEnd) {
        selectedAnnotation.addSpan(new Span(selectionStart, selectionEnd));
        manager.annotationsChangedEvent(selectedAnnotation);
    }

    public void removeSpanFromSelectedAnnotation(int selectionStart, int selectionEnd) {
        selectedAnnotation.removeSpan(selectionStart, selectionEnd);

        if (selectedAnnotation.getSpans().size() == 0) {
            textAnnotations.get(selectedAnnotation.getTextSource()).remove(selectedAnnotation);
            selectedAnnotation = null;
            manager.annotationsChangedEvent(null);
        }
    }
}
