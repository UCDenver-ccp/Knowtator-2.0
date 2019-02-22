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

package edu.ucdenver.ccp.knowtator.view.actions.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import javax.swing.SwingConstants;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/** The type Graph actions. */
public class GraphActions {
  /** The type Remove cells action. */
  public static class RemoveCellsAction extends AbstractGraphAction {
    private final Object[] cellsToRemove;

    /**
     * Instantiates a new Remove cells action.
     *
     * @param model the model
     * @param graphSpace the graph space
     */
    public RemoveCellsAction(KnowtatorModel model, GraphSpace graphSpace) {
      super(model, "Remove cells", graphSpace);
      cellsToRemove = graphSpace.getSelectionCells();
    }

    @Override
    public void perform() {
      graphSpace.removeCells(cellsToRemove, true);
    }
  }

  /** The type Add annotation node action. */
  public static class AddAnnotationNodeAction extends AbstractGraphAction {

    private final ConceptAnnotation conceptAnnotation;
    private final KnowtatorView view;

    /**
     * Instantiates a new Add annotation node action.
     *
     * @param view the view
     * @param model the model
     * @param graphSpace the graph space
     * @param conceptAnnotation the concept annotation
     */
    public AddAnnotationNodeAction(
        KnowtatorView view,
        KnowtatorModel model,
        GraphSpace graphSpace,
        ConceptAnnotation conceptAnnotation) {
      super(model, "Add annotation node", graphSpace);
      this.view = view;
      this.conceptAnnotation = conceptAnnotation;
    }

    @Override
    public void perform() {
      AnnotationNode vertex = graphSpace.makeAnnotationNode(conceptAnnotation);
      if (view != null) {
        goToVertex(view, vertex);
      }
    }
  }

  private static void goToVertex(KnowtatorView view, AnnotationNode vertex) {
    view.getGraphViewDialog().requestFocusInWindow();
    view.getGraphViewDialog().getGraphView().getGraphComponent().scrollCellToVisible(vertex, true);
  }

  /** The type Apply layout action. */
  public static class ApplyLayoutAction extends AbstractGraphAction {
    private final KnowtatorView view;

    /**
     * Instantiates a new Apply layout action.
     *
     * @param view the view
     * @param model the model
     * @param graphSpace the graph space
     */
    public ApplyLayoutAction(KnowtatorView view, KnowtatorModel model, GraphSpace graphSpace) {
      super(model, "Apply layout", graphSpace);
      this.view = view;
    }

    @Override
    public void perform() {
      mxHierarchicalLayout layout = new mxHierarchicalLayout(graphSpace);
      layout.setOrientation(SwingConstants.WEST);
      layout.setIntraCellSpacing(50);
      layout.setInterRankCellSpacing(125);
      layout.setOrientation(SwingConstants.NORTH);

      try {
        graphSpace.getModel().beginUpdate();
        try {
          layout.execute(graphSpace.getDefaultParent());
        } finally {
          if (view != null) {
            mxMorphing morph =
                new mxMorphing(
                    view.getGraphViewDialog().getGraphView().getGraphComponent(), 20, 1.2, 20);

            morph.addListener(mxEvent.DONE, (arg0, arg1) -> graphSpace.getModel().endUpdate());

            morph.startAnimation();
          }
        }
      } finally {
        graphSpace.getModel().endUpdate();
        if (view != null) {
          view.getGraphViewDialog().getGraphView().getGraphComponent().zoomAndCenter();
        }
      }
    }
  }

  /** The type Add triple action. */
  public static class AddTripleAction extends AbstractGraphAction {
    private final AnnotationNode source;
    private final AnnotationNode target;
    private final OWLObjectProperty property;
    private final Boolean negation;
    private final String quantifier;
    private final String quantifierValue;
    private final String motivation;

    /**
     * Instantiates a new Add triple action.
     *
     * @param model the model
     * @param source the source
     * @param target the target
     * @param property the property
     * @param quantifier the quantifier
     * @param quantifierValue the quantifier value
     * @param negation the negation
     * @param motivation the motivation
     * @param graphSpace the graph space
     */
    public AddTripleAction(
        KnowtatorModel model,
        AnnotationNode source,
        AnnotationNode target,
        OWLObjectProperty property,
        String quantifier,
        String quantifierValue,
        Boolean negation,
        String motivation,
        GraphSpace graphSpace) {
      super(model, "Add triple", graphSpace);
      this.source = source;
      this.target = target;
      this.property = property;
      this.negation = negation;
      this.quantifier = quantifier;
      this.quantifierValue = quantifierValue;
      this.motivation = motivation;
    }

    @Override
    public void perform() {
      graphSpace.addTriple(
          source, target, null, null, property, quantifier, quantifierValue, negation, motivation);
    }
  }
}
