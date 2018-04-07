package edu.ucdenver.ccp.knowtator.model.owl;

import edu.ucdenver.ccp.knowtator.model.Savable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
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
    private OWLModelManager owlModelManager;


    private OWLAnnotationProperty getOWLAnnotationPropertyByName() {
        return owlModelManager.getOWLEntityFinder().getOWLAnnotationProperty("name");
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

        return owlModelManager == null ? null : owlModelManager.getOWLEntityFinder().getOWLClass(classID);
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

    public OWLObjectProperty getSelectedProperty() {
        return owlWorkSpace.getOWLSelectionModel().getLastSelectedObjectProperty();
    }

    private String getOwlClassID(OWLClass cls) {
        String annotationValue = extractAnnotation(cls, owlModelManager.getOWLDataFactory().getRDFSLabel());
        return annotationValue == null ? getOwlEntID(cls) : annotationValue;
    }

    public String getSelectedOwlClassID() {
        OWLClass cls = getSelectedClass();
        if (cls != null) {
            return getOwlClassID(cls);
        } else {
            return null;
        }

    }

    @SuppressWarnings("unused")
    private String getOwlClassName(OWLClass cls) {
        String annotationValue = extractAnnotation(cls, getOWLAnnotationPropertyByName());
        annotationValue = annotationValue == null ? extractAnnotation(cls, owlModelManager.getOWLDataFactory().getRDFSLabel()) : annotationValue;
        return annotationValue == null ? getOwlEntID(cls) : annotationValue;
    }


    public String[] getSelectedOwlClassDescendants() {
        OWLClass cls = getSelectedClass();
        if (cls != null) {
            return getDescendants(cls);
        } else {
            return null;
        }
    }

    public void setUpOWL(OWLWorkspace owlWorkSpace, OWLModelManager owlModelManager) {
        this.owlWorkSpace = owlWorkSpace;
        this.owlModelManager = owlModelManager;
    }

    public OWLModelManager getOwlModelManager() {
        return owlModelManager;
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
}
