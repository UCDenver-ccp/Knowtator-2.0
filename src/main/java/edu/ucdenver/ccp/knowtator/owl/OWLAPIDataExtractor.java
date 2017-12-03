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

    private static String extractOWLObjectData(BasicKnowtatorView view, OWLEntity ent){

        Collection<OWLAnnotation> owlAnnotations = getOWLObjectAnnotations(view, ent);

        if (owlAnnotations != null) {
            for (OWLAnnotation anno : owlAnnotations) {
                if (anno.getProperty().isLabel()) {
                    if (anno.getValue() instanceof OWLLiteral) {
                        return ((OWLLiteral) anno.getValue()).getLiteral();
                    }
                }
            }
        }
        return null;
    }

    public static String getOwlEntID(OWLEntity ent) {
        return ent.getIRI().getShortForm();
    }

    public static OWLClass getOWLClassByID(BasicKnowtatorView view, String classID) {
        try {
            return view.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static OWLObjectProperty getOWLObjectPropertyByID(BasicKnowtatorView view, String classID) {
        try {
            return view.getOWLModelManager().getOWLEntityFinder().getOWLObjectProperty(classID);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static Set<OWLClass> getDecendents(BasicKnowtatorView view, OWLClass cls) {
        return view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
    }

    private static OWLClass getSelectedClass(BasicKnowtatorView view) {
        return view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
    }

    public static String getSelectedOwlClassID(BasicKnowtatorView view) {
        OWLClass cls = getSelectedClass(view);

        if (cls != null) {
            return getOwlEntID(cls);
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

    public static String getSelectedPropertyID(BasicKnowtatorView view) {
        OWLObjectProperty property = view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();
        if (property == null) {
            log.warn("No Object property selected");
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Relationship ID", field1,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter an ID for this property", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                return field1.getText();

            }
            return null;
        } else {
            return getOwlEntID(property);
        }
    }

    private static String getOwlEntName(BasicKnowtatorView view, OWLEntity ent) {
        return extractOWLObjectData(view, ent);
    }

    public static String getSelectedOwlClassName(BasicKnowtatorView view) {
        OWLClass cls = getSelectedClass(view);

        if (cls != null) {
            return getOwlEntName(view, cls);
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

    public static Set<OWLClass> getSelectedOWLClassDecendents(BasicKnowtatorView view) {
        OWLClass cls = getSelectedClass(view);
        return getDecendents(view, cls);
    }
}
