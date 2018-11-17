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

package edu.ucdenver.ccp.knowtator.actions;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;

public class GraphActions {
    public static class removeCellsAction extends AbstractGraphAction {
        private final Object[] cellsToRemove;

        public removeCellsAction(KnowtatorController controller) throws NoSelectionException {
            super(controller, "Remove cells");
            cellsToRemove = graphSpace.getSelectionCells();
        }

        @Override
        public void perform() {
            graphSpace.removeCells(cellsToRemove, true);
        }
    }

    public static class AddAnnotationNodeAction extends AbstractGraphAction {

        private final TextSource textSource;
        private final ConceptAnnotation conceptAnnotation;
        private final KnowtatorView view;

        public AddAnnotationNodeAction(KnowtatorView view, KnowtatorController controller) throws NoSelectionException {
            super(controller, "Add annotation node");
            this.view = view;
            textSource = controller.getTextSourceCollection().getSelection();
            conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();
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

    public static class applyLayoutAction extends AbstractGraphAction {
        private final KnowtatorView view;

        public applyLayoutAction(KnowtatorView view, KnowtatorController controller) throws NoSelectionException {
            super(controller, "Apply layout");
            this.view = view;
        }

        @Override
        public void perform() {
            //		graph.reDrawGraph();
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
                        mxMorphing morph = new mxMorphing(view.getGraphViewDialog().getGraphView().getGraphComponent(), 20, 1.2, 20);

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

    public static class AddTripleAction extends AbstractGraphAction {
        private KnowtatorController controller;
        private final AnnotationNode source;
        private final AnnotationNode target;
        private OWLObjectProperty property;
        private Boolean negation;
        private String quantifier;
        private String guantifierValue;
        private String propertyId;
        private String motivation;

        public AddTripleAction(KnowtatorController controller,
                               AnnotationNode source, AnnotationNode target,
                               OWLObjectProperty property, String propertyId,
                               String quantifier, String guantifierValue,
                               Boolean negation,
                               String motivation) throws NoSelectionException {
            super(controller, "Add triple");
            this.controller = controller;
            this.source = source;
            this.target = target;
            this.property = property;
            this.negation = negation;
            this.quantifier = quantifier;
            this.guantifierValue = guantifierValue;
            this.propertyId = propertyId;
            this.motivation = motivation;
        }

        @Override
        public void perform() {
            graphSpace.addTriple(
                    source, target,
                    null,
                    controller.getProfileCollection().getSelection(),
                    property, propertyId,
                    quantifier, guantifierValue,
                    negation,
                    motivation
            );
        }
    }
}
