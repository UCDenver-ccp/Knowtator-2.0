package edu.ucdenver.ccp.knowtator.iaa;

import org.semanticweb.owlapi.model.OWLObjectProperty;

public class AssertionRelationship {

    public OWLObjectProperty property;
    public String propertyName;

    public AssertionRelationship(OWLObjectProperty property, String propertyName) {
        this.property = property;
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return propertyName;
    }
}
