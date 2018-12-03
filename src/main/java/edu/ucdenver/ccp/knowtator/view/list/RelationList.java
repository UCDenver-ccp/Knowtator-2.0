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
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public class RelationList extends KnowtatorList<RelationAnnotation> {
	private boolean shouldReact;

	protected RelationList(KnowtatorView view) {
		super(view);

		shouldReact = true;

		KnowtatorList<RelationAnnotation> list = this;
		al = e -> {
			if (list.getSelectedValue() != null) {
				shouldReact = false;
				RelationAnnotation relationAnnotation = list.getSelectedValue();
				collection.setSelection(relationAnnotation);
				relationAnnotation.getTextSource().setSelectedGraphSpace(relationAnnotation.getGraphSpace());
				relationAnnotation.getGraphSpace().setSelectionCell(relationAnnotation);
				shouldReact = true;
			}

		};

		addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					if (list.getSelectedIndex() != -1) {
						shouldReact = false;
						RelationAnnotation relationAnnotation = list.getSelectedValue();
						collection.setSelection(relationAnnotation);
						relationAnnotation.getTextSource().setSelectedGraphSpace(relationAnnotation.getGraphSpace());
						relationAnnotation.getGraphSpace().setSelectionCell(relationAnnotation);
						shouldReact = true;
					}
				}
			}
		});
	}

	@Override
	public void react() {
		KnowtatorCollection<RelationAnnotation> relationAnnotations = new KnowtatorCollection<RelationAnnotation>(null) {
		};
		view.getModel()
				.filter(model -> shouldReact)
				.ifPresent(model -> {
					model.getTextSources()
							.stream().forEach(textSource ->
							textSource.getGraphSpaces().stream()
									.map(GraphSpace::getRelationAnnotations).forEach(relationAnnotations1 -> relationAnnotations1.forEach(relationAnnotations::add)));
					setCollection(relationAnnotations);
					setSelected();

				});
	}

	@Override
	public void reset() {
		super.reset();
		react();
	}
}
