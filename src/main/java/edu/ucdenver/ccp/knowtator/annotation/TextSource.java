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

    private String docID;
    private String content;

    TextSource(KnowtatorManager manager, BasicKnowtatorView view, String docID, String content) {
        this.view = view;
        this.manager = manager;
        this.docID = docID;
        this.content = content;
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

    public void addAnnotation(int start, int end) {
        String className = OWLAPIDataExtractor.getSelectedClassName(view);
        if (className != null) {

            addAnnotation(manager.getProfileManager().getCurrentProfile(), className,
                    OWLAPIDataExtractor.getSelectedClassID(view),
                    new ArrayList<Span>() {{
                        add(new Span(start, end));
                    }});
        }
    }

    public void addAnnotation(Profile profile, String className, String classID, List<Span> spans) {
        Annotation newAnnotation = annotationManager.addAnnotation(this, profile, className, classID, spans);
        if (view != null) view.annotationAddedEvent(newAnnotation);
    }

    public void removeAnnotation(Annotation annotation) {
        annotationManager.removeAnnotation(annotation);
        if(view != null) view.annotationRemovedEvent();
    }

    public void addSpanToSelectedAnnotation(Annotation annotation, int start, int end) {
        annotationManager.addSpanToAnnotation(annotation, start, end);
    }

    public Set<Annotation> getAnnotations() {
        return annotationManager.getAnnotations();
    }

    public Map.Entry<Span, Annotation> getNextSpan(Span span) {
        return annotationManager.getNextSpan(span);
    }

    public Map.Entry<Span, Annotation> getPreviousSpan(Span span) {
        return annotationManager.getPreviousSpan(span);
    }

    public Map<Span, Annotation> getAnnotationsContainingLocation(int loc) {
        return annotationManager.getAnnotationsContainingLocation(loc);
    }

    public Set<Assertion> getAssertions() {
        return annotationManager.getAssertions();
    }
}
