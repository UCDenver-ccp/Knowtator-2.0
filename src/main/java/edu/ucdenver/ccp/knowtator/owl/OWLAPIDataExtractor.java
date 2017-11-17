package edu.ucdenver.ccp.knowtator.owl;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.swing.*;
import java.util.Set;
import java.util.stream.Stream;


public class OWLAPIDataExtractor {
    public static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);
//    public static String ID_NAME_SPACE = "has_obo_namespace";
    public static String ID_CLASS = "id";
    public static String ID_NAME = null;

    public static Stream<OWLAnnotation> getOWLObjectAnnotations(KnowtatorManager manager, OWLEntity ent) {
        if (ent != null) {
            return EntitySearcher.getAnnotations(ent.getIRI(), manager.getOwlModelManager().getActiveOntology());
        }
        else {
            return null;
        }
    }

    public static String extractOWLObjectData(KnowtatorManager manager, OWLEntity ent, String annotationPropertyName){

        Stream<OWLAnnotation> owlAnnotations = getOWLObjectAnnotations(manager, ent);

        if (owlAnnotations != null) {
            for (OWLAnnotation anno : (Iterable<OWLAnnotation>) owlAnnotations::iterator) {
                if (annotationPropertyName == null) {
                    if (anno.getProperty().isLabel()) {
                        if (anno.getValue() instanceof OWLLiteral) {
                            return ((OWLLiteral) anno.getValue()).getLiteral();
                        }
                    }
                } else {
                    if (anno.getProperty() == manager.getOwlModelManager().getOWLEntityFinder().getOWLAnnotationProperty("has_obo_namespace")) {
                        if (anno.getValue() instanceof OWLLiteral) {
                            return ((OWLLiteral) anno.getValue()).getLiteral();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getClassNameByOWLClass(KnowtatorManager manager, OWLEntity ent) {
        return extractOWLObjectData(manager, ent, ID_NAME);
    }

    public static String getClassIDByOWLClass(KnowtatorManager manager, OWLEntity ent) {
        return extractOWLObjectData(manager, ent, ID_CLASS);
    }

    public static String getClassIDByName(KnowtatorManager manager, String className) {
        OWLClass cls = getOWLClassByName(manager, className);
        return getClassIDByOWLClass(manager, cls);
    }

    public static OWLClass getOWLClassByName(KnowtatorManager manager, String className) {
        try {
            return manager.getOwlModelManager().getOWLEntityFinder().getOWLClass(className);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static Set<OWLClass> getDecendents(KnowtatorManager manager, OWLClass cls) {
        return manager.getOwlModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
    }

    public static Set<OWLClass> getDecendents(KnowtatorManager manager, String className) {
        OWLClass cls = getOWLClassByName(manager, className);
        if (cls != null) {
            return getDecendents(manager, cls);
        }
        return null;
    }

    public static OWLClass getSelectedClass(KnowtatorManager manager) {
        return manager.getOwlWorkspace().getOWLSelectionModel().getLastSelectedClass();
    }

    public static String getSelectedClassName(KnowtatorManager manager) {
        OWLClass cls = getSelectedClass(manager);

        if (cls != null) {
            return getClassNameByOWLClass(manager, cls);
        } else {
            log.warn("No OWLClass selected");
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Class name", field1,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter a name for this class", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                return field1.getText();

            }
            return null;
        }
    }

    public static OWLObjectProperty getSelectedProperty(KnowtatorManager manager) {
        return manager.getOwlWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();
    }
}
