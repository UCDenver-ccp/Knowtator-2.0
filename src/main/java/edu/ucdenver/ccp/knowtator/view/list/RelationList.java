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

import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.Optional;
import java.util.Set;

public class RelationList extends KnowtatorList<RelationAnnotation> {
	private final Set<OWLObjectProperty> activeOWLPropertyDescendants;

	public RelationList(KnowtatorView view, Set<OWLObjectProperty> activeOWLPropertyDescendants) {
		super(view);

		this.activeOWLPropertyDescendants = activeOWLPropertyDescendants;
	}

	@Override
	protected Optional<RelationAnnotation> getSelectedFromModel() {
		return view.getModel()
				.filter(BaseModel::isNotLoading)
				.flatMap(BaseModel::getSelectedTextSource)
				.flatMap(TextSource::getSelectedGraphSpace)
				.map(mxGraph::getSelectionCell)
				.filter(cell -> cell instanceof RelationAnnotation)
				.map(cell -> (RelationAnnotation) cell);
	}

	@Override
	public void addElementsFromModel() {
		view.getModel()
				.ifPresent(model -> model.getTextSources()
						.forEach(textSource -> textSource.getGraphSpaces()
								.forEach(graphSpace -> graphSpace.getRelationAnnotations().stream()
										.filter(relationAnnotation -> activeOWLPropertyDescendants.contains(relationAnnotation.getProperty()))
										.forEach(relationAnnotation -> getDefaultListModel()
												.addElement(relationAnnotation)))));
	}

	@Override
	public void react() {
		Optional<RelationAnnotation> relationAnnotationOptional = Optional.ofNullable(getSelectedValue());

		relationAnnotationOptional.ifPresent(relationAnnotation -> {
			view.getModel().ifPresent(model -> model.getTextSources().setSelection(relationAnnotation.getTextSource()));
			relationAnnotation.getTextSource().setSelectedGraphSpace(relationAnnotation.getGraphSpace());
			relationAnnotation.getGraphSpace().setSelectionCell(relationAnnotation);
		});
	}
}
