package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;

import java.util.HashSet;
import java.util.Set;

public class IdentityChainAnnotation extends ConceptAnnotation {

    private Set<String> coreferringAnnotations;

    public IdentityChainAnnotation(String annotationID, TextSource textSource, Profile annotator) {
        super("IDENTITY chain", "IDENTITY chain", annotationID, textSource, annotator);

        coreferringAnnotations = new HashSet<>();
    }

    public void addCoreferringAnnotation(String annotationID) {
        coreferringAnnotations.add(annotationID);
    }

    public Set<String> getCoreferringAnnotations() {
        return coreferringAnnotations;
    }
}
