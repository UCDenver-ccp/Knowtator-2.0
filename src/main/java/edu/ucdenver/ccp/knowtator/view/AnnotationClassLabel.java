package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

public class AnnotationClassLabel extends KnowtatorLabel implements OWLModelManagerListener {

    private ConceptAnnotation conceptAnnotation;

    AnnotationClassLabel(KnowtatorView view) {
        super(view);
        this.conceptAnnotation = null;
    }

    @Override
    protected void reactToConceptAnnotationUpdated() {
        displayAnnotation();
    }

    @Override
    protected void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {

    }

    @Override
    void reactToConceptAnnotationChange(SelectionChangeEvent<ConceptAnnotation> event) {
        conceptAnnotation = event.getNew();
        displayAnnotation();
    }

    private void displayAnnotation() {
        if (conceptAnnotation != null) {
            String owlClassRendering = view.getController().getOWLModel().getOWLEntityRendering(conceptAnnotation.getOwlClass());
            setText(owlClassRendering == null ?
                    String.format("ID: %s Label: %s",
                            conceptAnnotation.getOwlClassID(),
                            conceptAnnotation.getOwlClassLabel()) :
                    owlClassRendering);
        } else {
            setText("");
        }
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
            displayAnnotation();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        view.getController().getOWLModel().removeOWLModelManagerListener(this);
    }
}
