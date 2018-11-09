/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.annotation;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorLabel;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

public class AnnotationClassLabel extends KnowtatorLabel implements OWLModelManagerListener {


    public AnnotationClassLabel(KnowtatorView view) {
        super(view);
    }

    @Override
    protected void react() {
        try {
            displayAnnotation(view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection());
        } catch (NoSelectionException e) {
            setText("");
        }
    }


    private void displayAnnotation(ConceptAnnotation conceptAnnotation) {
        String owlClassRendering = view.getController().getOWLModel().getOWLEntityRendering(conceptAnnotation.getOwlClass());
        setText(owlClassRendering == null ?
                String.format("ID: %s Label: %s",
                        conceptAnnotation.getOwlClassID(),
                        conceptAnnotation.getOwlClassLabel()) :
                owlClassRendering);
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
            try {
                displayAnnotation(view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection());
            } catch (NoSelectionException e) {
                setText("");
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        view.getController().getOWLModel().removeOWLModelManagerListener(this);
    }
}
