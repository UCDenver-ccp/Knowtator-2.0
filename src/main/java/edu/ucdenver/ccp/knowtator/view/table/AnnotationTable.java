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

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.table.DefaultTableModel;
import java.util.Optional;

public class AnnotationTable extends KnowtatorTable<ConceptAnnotation> {

	AnnotationTable(KnowtatorView view) {
		super(view);
		setModel(new DefaultTableModel(new Object[][]{}, new String[]{
				"Spanned Text",
				"OWL Entity",
				"Text Source"
		}) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
	}

	@Override
	public void reactToClick() {
		Optional<ConceptAnnotation> conceptAnnotationOptional = getSelectedValue();
		conceptAnnotationOptional.ifPresent(conceptAnnotation -> {
			view.getModel().ifPresent(model -> {
				if (!model.getSelectedTextSource().map(textSource -> textSource.equals(conceptAnnotation.getTextSource())).orElse(false)) {
					model.getTextSources()
							.setSelection(conceptAnnotation.getTextSource());
				}
			});
			conceptAnnotation.getTextSource().setSelectedConceptAnnotation(conceptAnnotation);
		});
	}

	@Override
	public void reactToModelEvent() {

	}

	@Override
	protected Optional<Object> getSelectedFromModel() {
		return view.getModel().flatMap(BaseModel::getSelectedTextSource)
				.flatMap(TextSource::getSelectedAnnotation)
				.map(conceptAnnotation -> conceptAnnotation);
	}

	@Override
	Optional<ConceptAnnotation> getSelectedValue() {
		return Optional.ofNullable((ConceptAnnotation) getValueAt(getSelectedRow(), 0));

	}

	@Override
	void addValue(ConceptAnnotation modelObject) {
		((DefaultTableModel) getModel()).addRow(new Object[]{
				modelObject,
				modelObject.getOwlClassRendering(),
				modelObject.getTextSource()
		});
	}

	@Override
	public void addElementsFromModel() {
		view.getModel().ifPresent(model -> model.getTextSources()
				.forEach(textSource -> textSource.getConceptAnnotations()
						.forEach(this::addValue)));
	}
}
