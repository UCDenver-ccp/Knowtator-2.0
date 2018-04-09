package edu.ucdenver.ccp.knowtator.model.owl;

import edu.ucdenver.ccp.knowtator.model.Savable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class OWLAPIDataExtractor implements Savable {
    @SuppressWarnings("unused")
    private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);
    private OWLWorkspace owlWorkSpace;


    private OWLAnnotationProperty getOWLAnnotationPropertyByName() {
        return owlWorkSpace.getOWLModelManager().getOWLEntityFinder().getOWLAnnotationProperty("name");
    }

    private String extractAnnotation(OWLEntity ent, OWLAnnotationProperty annotationProperty) {
        String annotationValue;
        try{
            annotationValue = EntitySearcher.getAnnotations(ent, owlWorkSpace.getOWLModelManager().getActiveOntology(), annotationProperty)
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

        return owlWorkSpace == null ? null : owlWorkSpace.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
    }

    public OWLObjectProperty getOWLObjectPropertyByID(String classID) {
        return owlWorkSpace.getOWLModelManager().getOWLEntityFinder().getOWLObjectProperty(classID);
    }

    public Set<OWLClass> getDescendants(OWLClass cls) {
        return owlWorkSpace.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls);
    }

    public OWLClass getSelectedClass() {
        return owlWorkSpace.getOWLSelectionModel().getLastSelectedClass();
    }

    public OWLObjectProperty getSelectedProperty() {
        if (owlWorkSpace != null) {
            return owlWorkSpace.getOWLSelectionModel().getLastSelectedObjectProperty();
        } else {
            return null;
        }
    }

    public String getOWLClassID(OWLClass cls) {
        if (owlWorkSpace != null && cls != null) {
            String annotationValue = extractAnnotation(cls, owlWorkSpace.getOWLModelManager().getOWLDataFactory().getRDFSLabel());
            return annotationValue == null ? getOwlEntID(cls) : annotationValue;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private String getOwlClassName(OWLClass cls) {
        String annotationValue = extractAnnotation(cls, getOWLAnnotationPropertyByName());
        annotationValue = annotationValue == null ? extractAnnotation(cls, owlWorkSpace.getOWLModelManager().getOWLDataFactory().getRDFSLabel()) : annotationValue;
        return annotationValue == null ? getOwlEntID(cls) : annotationValue;
    }


    public Set<OWLClass> getSelectedOwlClassDescendants() {
        OWLClass cls = getSelectedClass();
        if (cls != null) {
            return getDescendants(cls);
        } else {
            return null;
        }
    }

    public void setUpOWL(OWLWorkspace owlWorkSpace) {
        this.owlWorkSpace = owlWorkSpace;
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {

    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {

    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent, String content) {

    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

    }

    @Override
    public void writeToBratStandoff(Writer writer) {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public OWLWorkspace getWorkSpace() {
        return owlWorkSpace;
    }
}
