package edu.ucdenver.ccp.knowtator.TextAnnotation;

import edu.ucdenver.ccp.knowtator.listeners.DocumentListener;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.iaa.AssertionRelationship;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.owl.OntologyTranslator;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.*;

public final class TextAnnotationManager implements DocumentListener {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);

    public HashMap<String, Collection<TextAnnotation>> textAnnotations;
    public TextAnnotation selectedTextAnnotation;
    public KnowtatorView view;
    public List<AssertionRelationship> assertionRelationships;

    public TextAnnotationManager(KnowtatorView view) {
        this.view = view;
        selectedTextAnnotation = null;
        textAnnotations = new HashMap<>();
        assertionRelationships = new ArrayList<>();
    }

    public void addTextSource(String textSource) {
        textAnnotations.put(textSource, new ArrayList<>());
    }

    public void addTextAnnotation(String textSource, OWLClass cls, Integer spanStart, Integer spanEnd) throws NoSuchFieldException {

        // Make new annotation
        HashMap<String, String> properties = new HashMap<String, String>() {
            {
                put(TextAnnotationProperties.CLASS_ID, OWLAPIDataExtractor.getClassID(view, cls));
                put(TextAnnotationProperties.CLASS, OWLAPIDataExtractor.getClassName(view, cls));
                put(TextAnnotationProperties.ANNOTATOR_ID, view.getAnnotatorManager().getCurrentAnnotator().getAnnotatorID());
                put(TextAnnotationProperties.ANNOTATOR, view.getAnnotatorManager().getCurrentAnnotator().getAnnotatorName());
                put(TextAnnotationProperties.SPAN, String.format("%s,%s", spanStart, spanEnd));
            }
        };

        TextAnnotation newTextAnnotation = new TextAnnotation(
                cls,
                properties
        );

        // Add annotation to annotation manager
        textAnnotations.get(textSource).add(newTextAnnotation);
        setSelectedTextAnnotation(newTextAnnotation);

        view.textAnnotationsChangedEvent();
    }


    public void addTextAnnotations(HashMap<String, List<HashMap<String, String>>> newTextAnnotations) {
        newTextAnnotations.forEach((textSource, annotations) -> {
            if (!textAnnotations.containsKey(textSource)) {
                textAnnotations.put(textSource, new ArrayList<>());
            }
            for (HashMap<String, String> properties : annotations) {
                OntologyTranslator.translate(properties.get(TextAnnotationProperties.CLASS_ID));
                view.loadOntologyFromLocation(properties.get(TextAnnotationProperties.CLASS_ID));
                OWLClass cls = OWLAPIDataExtractor.getOWLClassByID(view, properties.get(TextAnnotationProperties.CLASS));
                textAnnotations.get(textSource).add(new TextAnnotation(cls, properties));
            }
        });

        view.textAnnotationsChangedEvent();
    }

    @SuppressWarnings("unused")
    public void removeTextAnnotation(Integer spanStart, Integer spanEnd) {

        for (Collection<TextAnnotation> textAnnotations : this.textAnnotations.values()) {
            for (TextAnnotation textTextAnnotation : textAnnotations) {

                for (TextSpan textSpan : textTextAnnotation.getTextSpans()) {
                    if (Objects.equals(spanStart, textSpan.getStart()) && Objects.equals(spanEnd, textSpan.getEnd())) {
                        textAnnotations.remove(textTextAnnotation);
                        return;
                    }
                }
            }
        }

        view.textAnnotationsChangedEvent();
    }

    public void removeTextAnnotation(String textSource, TextAnnotation textTextAnnotation) {
        textAnnotations.get(textSource).remove(textTextAnnotation);

        view.textAnnotationsChangedEvent();
    }


    //TODO hover over annotations in text to see what they are

    public HashMap<String, Collection<TextAnnotation>> getTextAnnotations() {
        return textAnnotations;
    }

    public TextAnnotation getSelectedTextAnnotation() {
        return selectedTextAnnotation;
    }

    public Set<TextAnnotation> getAnnotationsInRange(Integer spanStart, Integer spanEnd) {
        Set<TextAnnotation> annotationsInRange = new HashSet<>();
        for (Collection<TextAnnotation> textAnnotations : this.textAnnotations.values()) {
            for (TextAnnotation textTextAnnotation : textAnnotations) {
                for (TextSpan textSpan : textTextAnnotation.getTextSpans()) {
                    if (spanStart >= textSpan.getStart() && spanEnd <= textSpan.getEnd()) {
                        annotationsInRange.add(textTextAnnotation);
                    }
                }
            }
        }

        return annotationsInRange;
    }

    public AssertionRelationship addAssertion() {
        OWLObjectProperty property = view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();

        AssertionRelationship newAssertionRelationship = new AssertionRelationship(property, OWLAPIDataExtractor.getClassName(view, property));
        assertionRelationships.add(newAssertionRelationship);
        return newAssertionRelationship;
    }

    public void setSelectedTextAnnotation(TextAnnotation selectedTextAnnotation) {
        this.selectedTextAnnotation = selectedTextAnnotation;
        view.setGlobalSelection1(selectedTextAnnotation.getOwlClass());
    }

    @Override
    public void documentChanged(KnowtatorTextPane textPane) {
        addTextSource(textPane.getName());
    }
}
