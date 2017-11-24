package edu.ucdenver.ccp.knowtator.annotation;

public class Assertion {

    public Annotation getSource() {
        return source;
    }

    public Annotation getTarget() {
        return target;
    }

    public String getRelationship() {
        return relationship;
    }

    private Annotation source;
    private Annotation target;
    private String relationship;

    Assertion(Annotation source, Annotation target, String relationship) {
        this.source = source;

        this.target = target;
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return String.format("Assertion: %s, %s, %s", source, target, relationship);
    }

}
