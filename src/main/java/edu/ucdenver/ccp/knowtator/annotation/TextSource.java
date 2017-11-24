package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            addAnnotation(null, manager.getProfileManager().getCurrentProfile(), className,
                    OWLAPIDataExtractor.getSelectedClassID(view),
                    new ArrayList<Span>() {{
                        add(new Span(start, end));
                    }});
        }
    }

    public void addAnnotation(String annotationID, Profile profile, String className, String classID, List<Span> spans) {
        Annotation newAnnotation = annotationManager.addAnnotation(annotationID, this, profile, className, classID, spans);
        if (view != null) view.annotationAddedEvent(newAnnotation);
    }

    public void removeAnnotation(Annotation annotation) {
        annotationManager.removeAnnotation(annotation);
        if(view != null) view.annotationRemovedEvent();
    }

    public Span addSpanToSelectedAnnotation(Annotation annotation, int start, int end) {
        return annotationManager.addSpanToAnnotation(annotation, start, end);

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

    public Set<Assertion> getAssertions() {
        return annotationManager.getAssertions();
    }

    public Map<Span, Annotation> getAnnotationMap(Integer loc, Boolean filterByProfile) {
        return annotationManager.getAnnotationMap(loc, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);
    }

    public void addAssertion(String source, String target, String relationship) {
        Assertion assertion = annotationManager.addAssertion(source, target, relationship);
        if(view != null && assertion != null) view.assertionAddedEvent(assertion);
    }
}
