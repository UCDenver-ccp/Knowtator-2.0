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

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import java.util.Optional;

public abstract class AbstractConceptAnnotationLabel extends KnowtatorLabel {

	AbstractConceptAnnotationLabel(KnowtatorView view) {
		super(view);
	}

	@Override
	public void react() {
		Optional<TextSource> textSourceOptional = KnowtatorView.MODEL.getSelectedTextSource();
		if (textSourceOptional.isPresent()) {
			textSourceOptional.ifPresent(textSource -> {
				Optional<ConceptAnnotation> conceptAnnotationOptional = textSource.getSelectedAnnotation();
				if (conceptAnnotationOptional.isPresent()) {
					conceptAnnotationOptional.ifPresent(this::displayConceptAnnotation);
				} else {
					setText("");
				}
			});
		} else {
			setText("");
		}
	}

	public abstract void displayConceptAnnotation(ConceptAnnotation conceptAnnotation);
}
