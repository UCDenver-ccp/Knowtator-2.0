package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

public class CompositionalAnnotation extends Annotation {

    public Annotation getSource() {
        return source;
    }

    public Annotation getTarget() {
        return target;
    }

    public String getRelationship() {
        return relationship;
    }

    private final String graphTitle;
    private Annotation source;
    private Annotation target;
    private String relationship;

    CompositionalAnnotation(String graphTitle, Annotation source, Annotation target, String relationship, String id, TextSource textSource, Profile annotator) {
        super(id, textSource, annotator);
        this.graphTitle = graphTitle;

        this.source = source;
        this.target = target;
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return String.format("CompositionalAnnotation: %s, %s, %s", source, target, relationship);
    }

    public String getGraphTitle() {
        return graphTitle;
    }
}
