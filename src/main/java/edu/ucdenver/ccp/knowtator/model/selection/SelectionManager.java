package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;

public class SelectionManager {
    private KnowtatorController controller;

    private Annotation selectedAnnotation;
    private Span selectedSpan;
    private GraphSpace activeGraphSpace;
    private TextSource activeTextSource;

    private Profile activeProfile;

    private boolean focusOnSelectedSpan;
    private boolean filterByProfile;

    public SelectionManager(KnowtatorController knowtatorController) {
        controller = knowtatorController;
        focusOnSelectedSpan = false;
        filterByProfile = false;
    }

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(Profile profile) {
        this.activeProfile = profile;
        controller.profileSelectionChangedEvent(profile);
    }

    public boolean isFilterByProfile() {
        return filterByProfile;
    }

    public void setFilterByProfile(boolean filterByProfile) {
        this.filterByProfile = filterByProfile;
        controller.profileFilterChangedEvent(filterByProfile);
    }

    public KnowtatorController getController() {
        return controller;
    }

    public void setController(KnowtatorController controller) {
        this.controller = controller;
    }

    public boolean isFocusOnSelectedSpan() {
        return focusOnSelectedSpan;
    }

    public void setFocusOnSelectedSpan(boolean focusOnSelectedSpan) {
        this.focusOnSelectedSpan = focusOnSelectedSpan;
    }

    public Annotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    public void setSelectedAnnotation(Annotation annotation, Span span) {
        if (selectedAnnotation != annotation) {
            selectedAnnotation = annotation;

            if (selectedAnnotation != null) {
                setSelectedSpan(span);
            } else if (activeGraphSpace != null) {
                activeGraphSpace.setSelectionCell(null);
            }

            controller.annotationSelectionChangedEvent(annotation);
        }

    }

    public Span getSelectedSpan() {
        return selectedSpan;
    }

    public void setSelectedSpan(Span span) {
        if (span != selectedSpan) {
            this.selectedSpan = span;
            if (span != null) {
                this.focusOnSelectedSpan = true;
                setSelectedAnnotation(span.getAnnotation(), span);
            }
            controller.spanSelectionChangedEvent(span);
        }
    }

    public GraphSpace getActiveGraphSpace() {
        return activeGraphSpace;
    }

    public void setActiveGraphSpace(GraphSpace activeGraphSpace) {
        this.activeGraphSpace = activeGraphSpace;
    }

    public TextSource getActiveTextSource() {
        return activeTextSource;
    }

    public void setActiveTextSource(TextSource textSource) {
        this.activeTextSource = textSource;
        controller.activeTextSourceChangedEvent(textSource);
    }


}
