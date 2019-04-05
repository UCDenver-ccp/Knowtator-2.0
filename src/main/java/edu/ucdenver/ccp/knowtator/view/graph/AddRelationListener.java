/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.Quantifier;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.graph.GraphActions;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.JOptionPane;

/** The type Add relation listener. */
class AddRelationListener implements mxEventSource.mxIEventListener {

  private final GraphView graphView;

  /**
   * Instantiates a new Add relation listener.
   *
   * @param graphView the graph view
   */
  AddRelationListener(GraphView graphView) {
    this.graphView = graphView;
  }

  @Override
  public void invoke(Object sender, mxEventObject evt) {
    GraphSpace graphSpace = (GraphSpace) sender;

    Optional.of(evt)
        .map(evt1 -> (Object[]) evt.getProperty("cells"))
        .filter(cells -> cells.length > 0)
        .ifPresent(
            cells ->
                Arrays.stream(cells)
                    .filter(cell -> graphSpace.getModel().isEdge(cell))
                    .map(cell -> (mxCell) cell)
                    .filter(cell -> "".equals(cell.getValue()))
                    .forEach(edge -> processEdge(edge, graphSpace)));
    graphView.reDrawGraph(graphSpace);
  }

  private void processEdge(mxCell edge, GraphSpace graphSpace) {
    // For some reason the top object property doesn't play nice so don't allow it
    graphView.getView().getModel()
        .ifPresent(
            model ->
                model
                    .getSelectedOwlObjectProperty()
                    .filter(
                        owlObjectProperty ->
                            !owlObjectProperty
                                .getIRI()
                                .getShortForm()
                                .equals("owl:topObjectProperty"))
                    .ifPresent(
                        owlObjectProperty -> {
                          RelationOptionsDialog relationOptionsDialog =
                              graphView.getRelationOptionsDialog(
                                  model.getOwlEntityRendering(owlObjectProperty));
                          if (relationOptionsDialog.getResult()
                              == RelationOptionsDialog.OK_OPTION) {
                            try {
                              AnnotationNode source = (AnnotationNode) edge.getSource();
                              AnnotationNode target = (AnnotationNode) edge.getTarget();
                              Quantifier quantifier = relationOptionsDialog.getQuantifier();
                              String motivation = relationOptionsDialog.getMotivation();
                              Boolean negation = relationOptionsDialog.getNegation();
                              String quantifierValue = relationOptionsDialog.getQuantifierValue();
                              model.registerAction(
                                  new GraphActions.AddTripleAction(
                                      model,
                                      source,
                                      target,
                                      owlObjectProperty,
                                      quantifier,
                                      quantifierValue,
                                      negation,
                                      motivation,
                                      graphSpace));
                            } catch (ActionUnperformable e) {
                              JOptionPane.showMessageDialog(graphView, e.getMessage());
                            }
                          }
                        }));

    graphSpace.getModel().remove(edge);
  }
}
