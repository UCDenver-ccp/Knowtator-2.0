package edu.ucdenver.ccp.knowtator.owl;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.swing.*;
import java.util.Collection;
import java.util.Set;


public class OWLAPIDataExtractor {
    private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);

    private static Collection<OWLAnnotation> getOWLObjectAnnotations(BasicKnowtatorView view, OWLEntity ent) {
        if (ent != null) {
            return EntitySearcher.getAnnotations(ent.getIRI(), view.getOWLModelManager().getActiveOntology());
        }
        else {
            return null;
        }
    }

    private static String extractOWLObjectData(BasicKnowtatorView view, OWLEntity ent, String annotationPropertyName){

        Collection<OWLAnnotation> owlAnnotations = getOWLObjectAnnotations(view, ent);

        if (owlAnnotations != null) {
            for (OWLAnnotation anno : owlAnnotations) {
                if (annotationPropertyName == null) {
                    if (anno.getProperty().isLabel()) {
                        if (anno.getValue() instanceof OWLLiteral) {
                            return ((OWLLiteral) anno.getValue()).getLiteral();
                        }
                    }
                } else {
                    if (anno.getProperty() == view.getOWLModelManager().getOWLEntityFinder().getOWLAnnotationProperty("has_obo_namespace")) {
                        if (anno.getValue() instanceof OWLLiteral) {
                            return ((OWLLiteral) anno.getValue()).getLiteral();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getClassNameByOWLClass(BasicKnowtatorView view, OWLEntity ent) {
        return extractOWLObjectData(view, ent, null);
    }

    private static String getClassIDByOWLClass(BasicKnowtatorView view, OWLEntity ent) {
        return extractOWLObjectData(view, ent, "id");
    }

    public static String getClassIDByName(BasicKnowtatorView view, String className) {
        OWLClass cls = getOWLClassByName(view, className);
        return getClassIDByOWLClass(view, cls);
    }

    public static OWLClass getOWLClassByName(BasicKnowtatorView view, String className) {
        try {
            return view.getOWLModelManager().getOWLEntityFinder().getOWLClass(className);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static Set<OWLClass> getDecendents(BasicKnowtatorView view, OWLClass cls) {
        return view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
    }

    public static Set<OWLClass> getDecendents(BasicKnowtatorView view, String className) {
        OWLClass cls = getOWLClassByName(view, className);
        if (cls != null) {
            return getDecendents(view, cls);
        }
        return null;
    }

    private static OWLClass getSelectedClass(BasicKnowtatorView view) {
        return view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
    }

    public static String getSelectedClassID(BasicKnowtatorView view) {
        OWLClass cls = getSelectedClass(view);

        if (cls != null) {
            return getClassIDByOWLClass(view, cls);
        } else {
            log.warn("No OWLClass selected");
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Class ID", field1,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter an ID for this class", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                return field1.getText();

            }
            return null;
        }
    }

    public static String getSelectedClassName(BasicKnowtatorView view) {
        OWLClass cls = getSelectedClass(view);

        if (cls != null) {
            return getClassNameByOWLClass(view, cls);
        } else {
            log.warn("No OWLClass selected");
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Class Name", field1,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter a name for this class", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                return field1.getText();

            }
            return null;
        }
    }

    public static OWLObjectProperty getSelectedProperty(BasicKnowtatorView view) {

        return view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();
    }


}
