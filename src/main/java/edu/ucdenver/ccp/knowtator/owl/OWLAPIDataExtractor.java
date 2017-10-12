package edu.ucdenver.ccp.knowtator.owl;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Collection;

public class OWLAPIDataExtractor {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);
    public static String ID_NAME_SPACE = "has_obo_namespace";
    public static String ID_CLASS = "id";
    public static String ID_NAME = null;

    public static Collection<OWLAnnotation> getOWLObjectAnnotations(KnowtatorView view, OWLEntity ent) {
        if (ent != null) {
            return EntitySearcher.getAnnotations(ent.getIRI(), view.getOWLModelManager().getActiveOntology());
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static Collection<OWLAnnotationProperty> getOWLAnnotationProperties(OWLOntology ont) {
        return ont.getAnnotationPropertiesInSignature();
    }

    public static String extractOWLObjectData(KnowtatorView view, OWLEntity ent, String annotationPropertyName){

        for (OWLAnnotation anno : getOWLObjectAnnotations(view, ent)){
            if (annotationPropertyName == null) {
                if (anno.getProperty().isLabel()) {
                    if (anno.getValue() instanceof OWLLiteral) {
                        return ((OWLLiteral) anno.getValue()).getLiteral();
                    }
                }
            }
            else {
                if (anno.getProperty() == view.getOWLModelManager().getOWLEntityFinder().getOWLAnnotationProperty("has_obo_namespace")) {
                    if (anno.getValue() instanceof OWLLiteral) {
                        return ((OWLLiteral) anno.getValue()).getLiteral();
                    }
                }
            }

        }

        throw new NullPointerException();
    }

    @SuppressWarnings("unused")
    public String getClassNameSpace(KnowtatorView view, OWLEntity ent) {
        return extractOWLObjectData(view, ent, ID_NAME_SPACE);
    }

    public static String getClassID(KnowtatorView view, OWLEntity ent) {
        return extractOWLObjectData(view, ent, ID_CLASS);
    }

    public static String getClassName(KnowtatorView view, OWLEntity ent) {
        return extractOWLObjectData(view, ent, ID_NAME);
    }

    @SuppressWarnings("unused")
    public static String getClassID(KnowtatorView view, String classID) {
        return extractOWLObjectData(view, getOWLClassByID(view, classID), ID_CLASS);
    }

    @SuppressWarnings("unused")
    public static String getClassName(KnowtatorView view, String classID) {
        return extractOWLObjectData(view, getOWLClassByID(view, classID), ID_NAME);
    }

    public static OWLClass getOWLClassByID(KnowtatorView view, String classID) {
        OWLClass cls = view.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
        if (cls == null) {
            log.warn(String.format("Class %s not found", classID));
        }
        else {
            log.warn(String.format("Class %s found", classID));
        }
        return cls;

    }
}
