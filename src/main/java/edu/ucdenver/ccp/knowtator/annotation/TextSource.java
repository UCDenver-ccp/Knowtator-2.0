package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class TextSource {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);


    private KnowtatorManager manager;
    private BasicKnowtatorView view;
    private AnnotationManager annotationManager;

    private String fileLocation;
    private String docID;
    private String content;

    TextSource(KnowtatorManager manager, BasicKnowtatorView view, String fileLocation, String docID) {
        this.view = view;
        this.manager = manager;
        this.fileLocation = fileLocation;
        this.docID = docID;
        this.annotationManager = new AnnotationManager();
    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
    }

    public String getDocID() {
        return docID;
    }

    public String getContent() {
        return content;
    }

    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }


    //TODO: Add annotations as subclasses of their assigned classes as well as of the AO
    public void addAnnotation(int start, int end) {
        String classID = OWLAPIDataExtractor.getSelectedOwlClassID(view);
        if (classID != null) {
            addAnnotation(null, classID, manager.getProfileManager().getCurrentProfile(),
                    OWLAPIDataExtractor.getSelectedOwlClassName(view),
                    new ArrayList<Span>() {{
                        add(new Span(start, end));
                    }});
        }
    }

    public void addAnnotation(String annotationID, String classID, Profile profile, String className, List<Span> spans) {
        Annotation newAnnotation = annotationManager.addAnnotation(annotationID, this, classID, profile, className, spans);
        if (view != null) view.annotationAddedEvent(newAnnotation);
    }

    public void removeAnnotation(Annotation annotation) {
        annotationManager.removeAnnotation(annotation);
        if(view != null) view.annotationRemovedEvent(annotation);
    }

    public Span addSpanToSelectedAnnotation(Annotation annotation, int start, int end) {
        return annotationManager.addSpanToAnnotation(annotation, start, end);

    }

    public Set<Annotation> getAnnotations(Set<Profile> profileFilters) {
        return annotationManager.getAnnotations(profileFilters);
    }

    public Map.Entry<Span, Annotation> getNextSpan(Span span) {
        return annotationManager.getNextSpan(span);
    }

    public Map.Entry<Span, Annotation> getPreviousSpan(Span span) {
        return annotationManager.getPreviousSpan(span);
    }

    public Set<Assertion> getAssertions(Set<Profile> profileFilters) {
        return annotationManager.getAssertions(profileFilters);
    }

    public Map<Span, Annotation> getAnnotationMap(Integer loc, Boolean filterByProfile) {
        return annotationManager.getAnnotationMap(loc, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);
    }

    public void addAssertion(String assertionID, Profile profile, String source, String target, String relationship) {
        Assertion assertion = annotationManager.addAssertion(assertionID, profile, source, target, relationship);
        if(view != null && assertion != null) view.assertionAddedEvent(assertion);
    }

    public void addAssertion(Annotation source, Annotation target, String relationship) {
        Assertion assertion = annotationManager.addAssertion(null, manager.getProfileManager().getCurrentProfile(), source, target, relationship);
        if(view != null && assertion != null) view.assertionAddedEvent(assertion);
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Annotation> getAnnotations() {
        return annotationManager.getAnnotations(null);
    }

    public void removeSpanFromSelectedAnnotation(Annotation annotation, Span span) {
        annotationManager.removeSpanFromSelectedAnnotation(annotation, span);
        if (view != null) view.spanRemovedEvent();
    }

    String getSpannedText(TreeSet<Span> spans) {
        StringBuilder sb = new StringBuilder();
        spans.forEach(span -> {
            sb.append(content.substring(span.getStart(), span.getEnd()));
            sb.append("\n");
        });
        return sb.toString();
    }
}
