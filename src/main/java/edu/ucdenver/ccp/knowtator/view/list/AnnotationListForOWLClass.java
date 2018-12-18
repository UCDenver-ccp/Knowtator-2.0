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

package edu.ucdenver.ccp.knowtator.view.list;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class AnnotationListForOWLClass extends AnnotationList {
	private final JCheckBox includeClassDescendantsCheckBox;
	private final JLabel owlClassLabel;
	private final Set<OWLClass> activeOWLClassDescendants;

	public AnnotationListForOWLClass(KnowtatorView view, JCheckBox includeClassDescendantsCheckBox, JLabel owlClassLabel) {
		super(view);
		this.includeClassDescendantsCheckBox = includeClassDescendantsCheckBox;
		this.owlClassLabel = owlClassLabel;
		this.activeOWLClassDescendants = new HashSet<>();
	}

	@Override
	void addElementsFromModel() {
		view.getModel().ifPresent(model -> model.getTextSources()
				.forEach(textSource -> textSource.getConceptAnnotations().stream()
						.filter(conceptAnnotation -> activeOWLClassDescendants.contains(conceptAnnotation.getOwlClass()))
						.forEach(conceptAnnotation -> getDefaultListModel().addElement(conceptAnnotation))));
	}

	@Override
	public void reset() {
		activeOWLClassDescendants.clear();
		view.getModel().ifPresent(model -> model.getSelectedOWLClass()
				.ifPresent(owlClass -> {
					activeOWLClassDescendants.add(owlClass);
					if (includeClassDescendantsCheckBox.isSelected()) {
						activeOWLClassDescendants.addAll(model.getOWLCLassDescendants(owlClass));
					}
					owlClassLabel.setText(model.getOWLEntityRendering(owlClass));
				}));

		super.reset();
	}

}
