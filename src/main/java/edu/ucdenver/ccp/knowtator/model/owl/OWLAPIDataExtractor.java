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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class OWLAPIDataExtractor {
    private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);
    private OWLWorkspace owlWorkSpace;
    private OWLModelManager owlModelManager;


    private OWLAnnotationProperty getOWLAnnotationPropertyByName(String annotationPropertyName) {
        return owlModelManager.getOWLEntityFinder().getOWLAnnotationProperty(annotationPropertyName);
    }

    private String extractAnnotation(OWLEntity ent, OWLAnnotationProperty annotationProperty) {
        String annotationValue;
        try{
            annotationValue = EntitySearcher.getAnnotations(ent, owlModelManager.getActiveOntology(), annotationProperty)
                    .stream().map(owlAnnotation -> ((OWLLiteral) owlAnnotation.getValue()).getLiteral())
                    .collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            annotationValue = null;
        }
        return annotationValue;
    }

    private String getOwlEntID(OWLEntity ent) {
//        log.warn("Knowtator: OWLAPIDataExtractor: " + ent.getIRI().getShortForm());

        return ent.getIRI().getShortForm();
    }

    public OWLClass getOWLClassByID(String classID) {
        return owlModelManager.getOWLEntityFinder().getOWLClass(classID);
    }

    public OWLObjectProperty getOWLObjectPropertyByID(String classID) {
        return owlModelManager.getOWLEntityFinder().getOWLObjectProperty(classID);
    }

    private String[] getDescendants(OWLClass cls) {
        Set<OWLClass> descendants = owlModelManager.getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
        return descendants.stream().map(this::getOwlEntID).toArray(String[]::new);
    }

    private OWLClass getSelectedClass() {
        return owlWorkSpace.getOWLSelectionModel().getLastSelectedClass();
    }

    private OWLObjectProperty getSelectedProperty() {
        return owlWorkSpace.getOWLSelectionModel().getLastSelectedObjectProperty();
    }

    public String getSelectedPropertyID() {
        OWLObjectProperty property = getSelectedProperty();
        return property == null ? null : getOwlEntID(property);
    }

    public String getSelectedOwlClassID() {
        OWLClass cls = getSelectedClass();
        String annotationValue = extractAnnotation(cls, owlModelManager.getOWLDataFactory().getRDFSLabel());
        return annotationValue == null ? getOwlEntID(cls) : annotationValue;
    }

    public String getSelectedOwlClassName() {
        OWLClass cls = getSelectedClass();
        String annotationValue = extractAnnotation(cls, getOWLAnnotationPropertyByName( "name"));
        annotationValue = annotationValue == null ? extractAnnotation(cls, owlModelManager.getOWLDataFactory().getRDFSLabel()) : annotationValue;
        return annotationValue == null ? getOwlEntID(cls) : annotationValue;
    }

    public String[] getSelectedOwlClassDescendants() {
        OWLClass cls = getSelectedClass();
        return getDescendants(cls);
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
                log.warn("Loading ontology: " + ontologyLocation);
                try {
                    OWLOntology newOntology = owlModelManager.getOWLOntologyManager().loadOntology((IRI.create(ontologyLocation)));
                    owlModelManager.setActiveOntology(newOntology);
                } catch (OWLOntologyCreationException e) {
                    log.warn("Knowtator: OWLAPIDataExtractor: Ontology already loaded");
                }
            }
        }

    }

    public void setUpOWL(OWLWorkspace owlWorkSpace, OWLModelManager owlModelManager) {
        this.owlWorkSpace = owlWorkSpace;
        this.owlModelManager = owlModelManager;
    }
}
