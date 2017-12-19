package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

public class CompositionalAnnotation extends Annotation {

    public String getSourceAnnotationID() {
        return sourceAnnotationID;
    }

    public String getTargetAnnotationID() {
        return targetAnnotationID;
    }

    public String getRelationship() {
        return relationship;
    }

    private final String graphTitle;
    private String sourceAnnotationID;
    private String targetAnnotationID;
    private String relationship;

    CompositionalAnnotation(String graphTitle, String sourceAnnotationID, String targetAnnotationID, String relationship, String id, TextSource textSource, Profile annotator) {
        super(id, textSource, annotator);
        this.graphTitle = graphTitle;

        this.sourceAnnotationID = sourceAnnotationID;
        this.targetAnnotationID = targetAnnotationID;
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return relationship;
    }

    public String getGraphTitle() {
        return graphTitle;
    }
}
