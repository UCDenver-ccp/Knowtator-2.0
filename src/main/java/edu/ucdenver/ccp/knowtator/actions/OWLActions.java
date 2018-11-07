package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.undo.UndoableEdit;

public class OWLActions {
    public static class ReassignOWLClassAction extends AbstractKnowtatorAction {

        private final OWLClass oldOwlClass;
        private ConceptAnnotation conceptAnnotation;
        private final OWLClass newOwlClass;

        public ReassignOWLClassAction(KnowtatorController controller) throws NoSelectionException, ActionUnperformableException {
            super("Reassign OWL class");

            this.conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();

            oldOwlClass = conceptAnnotation.getOwlClass();
            OWLEntity owlEntity = controller.getOWLModel().getSelectedOWLEntity();
            if (owlEntity instanceof OWLClass) {
                this.newOwlClass = (OWLClass) owlEntity;
            } else {
                throw new ActionUnperformableException();
            }
        }

        @Override
        public void execute() {
            conceptAnnotation.setOwlClass(newOwlClass);
        }

        @Override
        public UndoableEdit getEdit() {
            return new KnowtatorEdit(getPresentationName()) {
                @Override
                public void undo() {
                    super.undo();
                    conceptAnnotation.setOwlClass(oldOwlClass);
                }

                @Override
                public void redo() {
                    super.redo();
                    conceptAnnotation.setOwlClass(newOwlClass);
                }

            };
        }
    }
}
