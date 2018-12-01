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

public class RelationList extends KnowtatorList<RelationAnnotation> {
	private boolean shouldReact;

	protected RelationList() {
		super();

		shouldReact = true;

		al = e -> {
			JList jList = (JList) e.getSource();
			if (jList.getSelectedValue() != null) {
				shouldReact = false;
				RelationAnnotation relationAnnotation = (RelationAnnotation) jList.getSelectedValue();
				collection.setSelection(relationAnnotation);
				relationAnnotation.getTextSource().setSelectedGraphSpace(relationAnnotation.getGraphSpace());
				relationAnnotation.getGraphSpace().setSelectionCell(relationAnnotation);
				shouldReact = true;
			}

		};
	}

	@Override
	public void react() {
		if (shouldReact) {
			KnowtatorView.MODEL.getSelectedTextSource()
					.ifPresent(textSource -> {
						KnowtatorCollection<RelationAnnotation> relationAnnotations = new KnowtatorCollection<RelationAnnotation>(KnowtatorView.MODEL) {

						};
						textSource.getGraphSpaceCollection().stream()
								.map(GraphSpace::getRelationAnnotations).forEach(relationAnnotations1 -> relationAnnotations1.forEach(relationAnnotations::add));
						super.setCollection(relationAnnotations);
					});
			setSelected();
		}
	}
}
