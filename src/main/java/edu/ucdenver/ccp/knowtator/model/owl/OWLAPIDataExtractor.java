/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.owl;

import com.google.common.base.Optional;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class OWLAPIDataExtractor {
    private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);
    private OWLWorkspace owlWorkSpace;
    private OWLModelManager owlModelManager;

    private Collection<OWLAnnotation> getOWLObjectAnnotations(OWLEntity ent) {
        if (ent != null) {
            return EntitySearcher.getAnnotations(ent.getIRI(), owlModelManager.getActiveOntology());
        }
        else {
            return null;
        }
    }

    private String extractOWLObjectData(OWLEntity ent) {

        Collection<OWLAnnotation> owlAnnotations = getOWLObjectAnnotations(ent);

        if (owlAnnotations != null) {
            for (OWLAnnotation owlAnnotation : owlAnnotations) {
                if (owlAnnotation.getProperty().isLabel()) {
                    if (owlAnnotation.getValue() instanceof OWLLiteral) {
                        return ((OWLLiteral) owlAnnotation.getValue()).getLiteral();
                    }
                }
            }
        }
        return null;
    }

    public static String getOwlEntID(OWLEntity ent) {
        return ent.getIRI().getShortForm();
    }

    public OWLClass getOWLClassByID(String classID) {
        return owlModelManager == null ? null : owlModelManager.getOWLEntityFinder().getOWLClass(classID);
    }

    public OWLObjectProperty getOWLObjectPropertyByID(String classID) {
        return owlModelManager == null ? null : owlModelManager.getOWLEntityFinder().getOWLObjectProperty(classID);
    }

    private Set<OWLClass> getDecendents(OWLClass cls) {
        return owlModelManager == null ? null : owlModelManager.getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
    }

    private OWLClass getSelectedClass() {
        return owlWorkSpace == null ? null : owlWorkSpace.getOWLSelectionModel().getLastSelectedClass();
    }

    private OWLObjectProperty getSelectedProperty() {
        return owlWorkSpace == null ? null : owlWorkSpace.getOWLSelectionModel().getLastSelectedObjectProperty();
    }

    public Set<OWLClass> getSelectedOWLClassDescendents() {
        OWLClass cls = getSelectedClass();
        return cls == null ? null : getDecendents(cls);

    }

    public String getSelectedPropertyID() {
        OWLObjectProperty property = getSelectedProperty();
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

    public String[] getSelectedOwlClassInfo() {
        OWLClass cls = getSelectedClass();

        String[] clsInfo = new String[2];
        if (cls != null) {
            clsInfo[0] = extractOWLObjectData(cls);
            clsInfo[1] = getOwlEntID(cls);
        } else {
            log.warn("No OWLClass selected");

            JTextField nameField = new JTextField(10);
            JTextField idField = new JTextField(10);
            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(Box.createHorizontalStrut(15));
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);


            int result = JOptionPane.showConfirmDialog(null, inputPanel,
                    "No OWL Class selected", JOptionPane.DEFAULT_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                clsInfo[0] = nameField.getText();
                clsInfo[1] = idField.getText();
            }
        }
        return clsInfo;
    }

    public void loadOntologyFromLocation(String ontologyLocation) {
        if (owlModelManager != null) {
            List<String> ontologies = owlModelManager.getActiveOntologies().stream().map(ontology -> {
                OWLOntologyID ontID = ontology.getOntologyID();
                //noinspection Guava
                Optional<IRI> ontIRI = ontID.getOntologyIRI();
                if (ontIRI.isPresent()) {
                    return ontIRI.get().toURI().toString();
                } else {
                    return null;
                }
            }).collect(Collectors.toList());

//        String ontologyLocation = OntologyTranslator.translate(classID);
            if (!ontologies.contains(ontologyLocation)) {
                try {
                    OWLOntology newOntology = owlModelManager.getOWLOntologyManager().loadOntology((IRI.create(ontologyLocation)));
                    owlModelManager.setActiveOntology(newOntology);
                } catch (OWLOntologyCreationException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setUpOWL(OWLWorkspace owlWorkSpace, OWLModelManager owlModelManager) {
        this.owlWorkSpace = owlWorkSpace;
        this.owlModelManager = owlModelManager;
    }
}
