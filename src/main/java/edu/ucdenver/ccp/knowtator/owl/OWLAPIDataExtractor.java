package edu.ucdenver.ccp.knowtator.owl;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Collection;

public class OWLAPIDataExtractor {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public static String ID_NAME_SPACE = "has_obo_namespace";
    public static String ID_CLASS = "id";
    public static String ID_NAME = null;

    public static Collection<OWLAnnotation> getOWLObjectAnnotations(KnowtatorManager manager, OWLEntity ent) {
        if (ent != null) {
            return EntitySearcher.getAnnotations(ent.getIRI(), manager.getOwlModelManager().getActiveOntology());
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static Collection<OWLAnnotationProperty> getOWLAnnotationProperties(OWLOntology ont) {
        return ont.getAnnotationPropertiesInSignature();
    }

    public static String extractOWLObjectData(KnowtatorManager manager, OWLEntity ent, String annotationPropertyName){

        for (OWLAnnotation anno : getOWLObjectAnnotations(manager, ent)){
            if (annotationPropertyName == null) {
                if (anno.getProperty().isLabel()) {
                    if (anno.getValue() instanceof OWLLiteral) {
                        return ((OWLLiteral) anno.getValue()).getLiteral();
                    }
                }
            }
            else {
                if (anno.getProperty() == manager.getOwlModelManager().getOWLEntityFinder().getOWLAnnotationProperty("has_obo_namespace")) {
                    if (anno.getValue() instanceof OWLLiteral) {
                        return ((OWLLiteral) anno.getValue()).getLiteral();
                    }
                }
            }

        }

        throw new NullPointerException();
    }

    @SuppressWarnings("unused")
    public String getClassNameSpace(KnowtatorManager manager, OWLEntity ent) {
        return extractOWLObjectData(manager, ent, ID_NAME_SPACE);
    }

    public static String getClassID(KnowtatorManager manager, OWLEntity ent) {
        return extractOWLObjectData(manager, ent, ID_CLASS);
    }

    public static String getClassName(KnowtatorManager manager, OWLEntity ent) {
        return extractOWLObjectData(manager, ent, ID_NAME);
    }

    @SuppressWarnings("unused")
    public static String getClassID(KnowtatorManager manager, String classID) {
        return extractOWLObjectData(manager, getOWLClassByID(manager, classID), ID_CLASS);
    }

    @SuppressWarnings("unused")
    public static String getClassName(KnowtatorManager manager, String classID) {
        return extractOWLObjectData(manager, getOWLClassByID(manager, classID), ID_NAME);
    }

    public static OWLClass getOWLClassByID(KnowtatorManager manager, String className) {
        OWLClass cls = manager.getOwlModelManager().getOWLEntityFinder().getOWLClass(className);
        if (cls == null) {
            log.warn(String.format("Class %s not found", className));
        }
        else {
            log.warn(String.format("Class %s found", className));
        }
        return cls;

    }
}
