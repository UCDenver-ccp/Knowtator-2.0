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

package edu.ucdenver.ccp.knowtator.view.table;

import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RelationTable extends KnowtatorTable<RelationAnnotation> {
	private final Set<OWLObjectProperty> activeOWLPropertyDescendants;
	private final JCheckBox includePropertyDescendantsCheckBox;
	private final JLabel owlPropertyLabel;

	public RelationTable(KnowtatorView view, JCheckBox includePropertyDescendantsCheckBox, JLabel owlPropertyLabel) {
		super(view);
		setModel(new DefaultTableModel(new Object[][]{}, new String[]{
				"Subject Text",
				"Subject OWL Class",
				"Property",
				"Object Text",
				"Object OWL Class",
				"Text Source"
		}){
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		this.activeOWLPropertyDescendants = new HashSet<>();
		this.includePropertyDescendantsCheckBox = includePropertyDescendantsCheckBox;
		this.owlPropertyLabel = owlPropertyLabel;
	}

	@Override
	protected Optional<Object> getSelectedFromModel() {
		return view.getModel()
				.filter(BaseModel::isNotLoading)
				.flatMap(BaseModel::getSelectedTextSource)
				.flatMap(TextSource::getSelectedGraphSpace)
				.map(mxGraph::getSelectionCell)
				.filter(cell -> cell instanceof RelationAnnotation);
	}

	@Override
	Optional<RelationAnnotation> getSelectedValue() {
		return Optional.ofNullable((RelationAnnotation) getValueAt(getSelectedRow(), 2));

	}

	@Override
	void addValue(RelationAnnotation modelObject) {
		((DefaultTableModel) getModel()).addRow(new Object[]{
				((AnnotationNode) modelObject.getSource()).getConceptAnnotation(),
				((AnnotationNode) modelObject.getSource()).getConceptAnnotation().getOwlClassRendering(),
				modelObject,
				((AnnotationNode) modelObject.getTarget()).getConceptAnnotation(),
				((AnnotationNode) modelObject.getTarget()).getConceptAnnotation().getOwlClassRendering(),
				modelObject.getTextSource()
		});
	}

	@Override
	public void addElementsFromModel() {
		view.getModel()
				.ifPresent(model -> model.getTextSources()
						.forEach(textSource -> textSource.getGraphSpaces()
								.forEach(graphSpace -> graphSpace.getRelationAnnotations().stream()
										.filter(relationAnnotation -> activeOWLPropertyDescendants.contains(relationAnnotation.getProperty()))
										.forEach(this::addValue))));
	}

	@Override
	public void reactToClick() {
		Optional<RelationAnnotation> relationAnnotationOptional = getSelectedValue();

		relationAnnotationOptional.ifPresent(relationAnnotation -> {
			view.getModel().ifPresent(model -> model.getTextSources().setSelection(relationAnnotation.getTextSource()));
			relationAnnotation.getTextSource().setSelectedGraphSpace(relationAnnotation.getGraphSpace());
			relationAnnotation.getGraphSpace().setSelectionCell(relationAnnotation);
		});
	}

	@Override
	public void reactToModelEvent() {

	}

	@Override
	public void reset() {
		activeOWLPropertyDescendants.clear();

		view.getModel().ifPresent(model -> model.getSelectedOWLObjectProperty()
				.ifPresent(owlObjectProperty -> {
					activeOWLPropertyDescendants.add(owlObjectProperty);
					if (includePropertyDescendantsCheckBox.isSelected()) {
						activeOWLPropertyDescendants.addAll(model.getOWLObjectPropertyDescendants(owlObjectProperty));
					}
					owlPropertyLabel.setText(model.getOWLEntityRendering(owlObjectProperty));
				}));
		super.reset();
	}
}
