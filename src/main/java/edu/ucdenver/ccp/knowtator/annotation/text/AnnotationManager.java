package edu.ucdenver.ccp.knowtator.annotation.text;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.iaa.AssertionRelationship;
import edu.ucdenver.ccp.knowtator.listeners.DocumentListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.owl.OntologyTranslator;
import edu.ucdenver.ccp.knowtator.ui.text.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.*;

public final class AnnotationManager implements DocumentListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public HashMap<String, Collection<Annotation>> textAnnotations;
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

    public void addTextAnnotation(String textSource, OWLClass cls, Integer spanStart, Integer spanEnd) throws NoSuchFieldException {



        // Make new Annotation
        HashMap<String, String> properties = new HashMap<>() {
            {
                put(AnnotationProperties.CLASS_ID, OWLAPIDataExtractor.getClassID(manager, cls));
                put(AnnotationProperties.CLASS_NAME, OWLAPIDataExtractor.getClassName(manager, cls));
                put(AnnotationProperties.SPAN, String.format("%s,%s", spanStart, spanEnd));
            }
        };

        String annotator = manager.getAnnotatorManager().getCurrentAnnotator().getAnnotatorName();
        String annotatorID = manager.getAnnotatorManager().getCurrentAnnotator().getAnnotatorID();

        Annotation newAnnotation = new Annotation(manager,
                cls,
                properties,
                textSource,
                annotator,
                annotatorID
        );

        // Add Annotation to Annotation manager
        textAnnotations.get(textSource).add(newAnnotation);
        setSelectedAnnotation(newAnnotation);

        manager.annotationsChangedEvent();
    }


    public void addTextAnnotations(HashMap<String, List<HashMap<String, String>>> newTextAnnotations) {
        newTextAnnotations.forEach((textSource, annotations) -> {
            if (!textAnnotations.containsKey(textSource)) {
                textAnnotations.put(textSource, new ArrayList<>());
            }
            for (HashMap<String, String> properties : annotations) {
                if(manager.getConfigProperties().getAutoLoadOntologies()) {
                    OntologyTranslator.translate(properties.get(AnnotationProperties.CLASS_ID));
                    manager.loadOntologyFromLocation(properties.get(AnnotationProperties.CLASS_ID));
                    OWLClass cls = OWLAPIDataExtractor.getOWLClassByID(manager, properties.get(AnnotationProperties.CLASS_NAME));
                    textAnnotations.get(textSource).add(new Annotation(manager, cls, properties, textSource));
                } else {
                    textAnnotations.get(textSource).add(new Annotation(manager, null, properties, textSource));
                }


            }
        });

        manager.annotationsChangedEvent();
    }

    @SuppressWarnings("unused")
    public void removeTextAnnotation(Integer spanStart, Integer spanEnd) {

        for (Collection<Annotation> annotations : this.textAnnotations.values()) {
            for (Annotation textAnnotation : annotations) {

                for (Span span : textAnnotation.getSpans()) {
                    if (Objects.equals(spanStart, span.getStart()) && Objects.equals(spanEnd, span.getEnd())) {
                        annotations.remove(textAnnotation);
                        return;
                    }
                }
            }
        }

        manager.annotationsChangedEvent();
    }

    public void removeTextAnnotation(String textSource, Annotation textAnnotation) {
        textAnnotations.get(textSource).remove(textAnnotation);

        manager.annotationsChangedEvent();
    }


    //TODO hover over annotations in text to see what they are

    public HashMap<String, Collection<Annotation>> getTextAnnotations() {
        return textAnnotations;
    }

    public Annotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    public List<Annotation> getAnnotationsInRange(String textSource, Integer start, Integer end) {
        List<Annotation> annotationsInRange = new ArrayList<>();

        for (Annotation annotation : textAnnotations.get(textSource)) {
            if (annotation.getTextSpanInRange(start, end) != null) {
                annotationsInRange.add(annotation);
            }
        }

        return annotationsInRange;
    }

    public AssertionRelationship addAssertion() {
        OWLObjectProperty property = manager.getOwlWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();

        AssertionRelationship newAssertionRelationship = new AssertionRelationship(property, OWLAPIDataExtractor.getClassName(manager, property));
        assertionRelationships.add(newAssertionRelationship);
        return newAssertionRelationship;
    }

    public void setSelectedAnnotation(Annotation selectedAnnotation) {
        this.selectedAnnotation = selectedAnnotation;
        manager.owlSelectionChangedEvent(selectedAnnotation.getOwlClass());
        manager.annotationsChangedEvent(selectedAnnotation);
    }

    @Override
    public void documentChanged(KnowtatorTextPane textPane) {
        addTextSource(textPane.getName());
    }

    public Collection<Annotation> getTextAnnotations(String name) {
        return textAnnotations.get(name);
    }
}
