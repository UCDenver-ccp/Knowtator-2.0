package edu.ucdenver.cpbs.mechanic.iaa;

import org.semanticweb.owlapi.model.OWLObjectProperty;

public class AssertionRelationship {

    private OWLObjectProperty property;
    private String propertyName;

    public AssertionRelationship(OWLObjectProperty property, String propertyName) {
        this.property = property;
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName;
    }
}
