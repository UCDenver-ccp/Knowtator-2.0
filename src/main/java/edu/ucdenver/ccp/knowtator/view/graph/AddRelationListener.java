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

package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.graph.GraphActions;

import java.util.Arrays;

public class AddRelationListener implements mxEventSource.mxIEventListener {

	private final KnowtatorView view;
	private GraphView graphView;

	AddRelationListener(KnowtatorView view, GraphView graphView) {
		this.view = view;
		this.graphView = graphView;
	}

	@Override
	public void invoke(Object sender, mxEventObject evt) {
		if (sender instanceof GraphSpace) {
			GraphSpace graphSpace = (GraphSpace) sender;
			Object[] cells = (Object[]) evt.getProperty("cells");
			if (cells != null && cells.length > 0) {
				Arrays.stream(cells)
						.filter(cell -> graphSpace.getModel().isEdge(cell))
						.map(cell -> (mxCell) cell)
						.filter(cell -> "".equals(cell.getValue()))
						.forEach(edge -> {
							view.getModel().ifPresent(model ->
									model.getSelectedOWLObjectProperty()
											.filter(owlObjectProperty -> !owlObjectProperty.getIRI().getShortForm().equals("owl:topObjectProperty"))
											.ifPresent(owlObjectProperty -> {
												// For some reason the top object property doesn't play nice so don't allow it
												RelationOptionsDialog relationOptionsDialog = graphView.getRelationOptionsDialog(model.getOWLEntityRendering(owlObjectProperty));
												if (relationOptionsDialog.getResult() == RelationOptionsDialog.OK_OPTION) {
													model.registerAction(
															new GraphActions.AddTripleAction(
																	model,
																	(AnnotationNode) edge.getSource(),
																	(AnnotationNode) edge.getTarget(),
																	owlObjectProperty, relationOptionsDialog.getPropertyID(),
																	relationOptionsDialog.getQuantifier(), relationOptionsDialog.getQuantifierValue(),
																	relationOptionsDialog.getNegation(),
																	relationOptionsDialog.getMotivation(),
																	graphSpace));
												}
											}));

							graphSpace.getModel().remove(edge);
						});

				graphView.reDrawGraph(graphSpace);
			}
		}

	}
}
