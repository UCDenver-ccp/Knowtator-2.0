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

package edu.ucdenver.ccp.knowtator.view.label;

import edu.ucdenver.ccp.knowtator.model.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

public class AnnotationClassLabel extends AbstractConceptAnnotationLabel implements OWLModelManagerListener {


	public AnnotationClassLabel(KnowtatorView view) {
		super(view);
	}

	@Override
	public void displayConceptAnnotation(ConceptAnnotation conceptAnnotation) {
		setText(KnowtatorView.MODEL.getOWLEntityRendering(conceptAnnotation.getOwlClass()));
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
			react();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		KnowtatorView.MODEL.removeOWLModelManagerListener(this);
	}
}
