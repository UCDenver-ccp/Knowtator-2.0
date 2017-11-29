package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

public class Assertion {

    private Profile annotator;
    private String id;

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

    Assertion(String id, Profile profile, Annotation source, Annotation target, String relationship) {
        this.id = id;
        this.annotator = profile;
        this.source = source;

        this.target = target;
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return String.format("Assertion: %s, %s, %s", source, target, relationship);
    }

    public Profile getAnnotator() {
        return annotator;
    }

    public String getID() {
        return id;
    }
}
