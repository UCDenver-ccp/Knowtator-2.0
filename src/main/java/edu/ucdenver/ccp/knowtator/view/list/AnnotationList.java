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

import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public class AnnotationList extends KnowtatorList<ConceptAnnotation> {
	private boolean shouldReact;

	AnnotationList(KnowtatorView view) {
		super(view);

		shouldReact = true;

		removeListSelectionListener(al);
		KnowtatorList<ConceptAnnotation> list = this;
		al = e -> {
			if (list.getSelectedValue() != null) {
				shouldReact = false;
				ConceptAnnotation relationAnnotation = list.getSelectedValue();
				collection.setSelection(relationAnnotation);
				relationAnnotation.getTextSource().setSelectedConceptAnnotation(relationAnnotation);
				shouldReact = true;
			}

		};
		addListSelectionListener(al);

		addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					if (list.getSelectedIndex() != -1) {
						shouldReact = false;
						ConceptAnnotation relationAnnotation = list.getSelectedValue();
						collection.setSelection(relationAnnotation);
						relationAnnotation.getTextSource().setSelectedConceptAnnotation(relationAnnotation);
						shouldReact = true;
					}
				}
			}
		});
	}

	@Override
	public void react() {
		view.getModel()
				.filter(model -> shouldReact)
				.ifPresent(model -> {
					KnowtatorCollection<ConceptAnnotation> conceptAnnotations = new KnowtatorCollection<ConceptAnnotation>(null) {
					};

					model.getTextSources().forEach(textSource ->
							textSource.getConceptAnnotations()
									.forEach(conceptAnnotations::add));
					setCollection(conceptAnnotations);
					setSelected();

				});
	}

	@Override
	public void reset() {
		super.reset();
		react();
	}
}
