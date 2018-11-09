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

import edu.ucdenver.ccp.knowtator.model.collection.ListenableCollection;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import java.util.stream.Collector;

public class GraphSpaceList extends KnowtatorList<GraphSpace> {
    public GraphSpaceList(KnowtatorView view) {
        super(view);

    }

    @Override
    protected void react() {
        try {
            setCollection(view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection());
            setSelected();
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    private void setCollection(ConceptAnnotation conceptAnnotation) throws NoSelectionException {
        if (conceptAnnotation == null) {
            dispose();
        } else {
            setCollection(conceptAnnotation.getTextSource().getGraphSpaceCollection()
                    .stream().filter(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation)).collect(Collector.of(
                            () -> new GraphSpaceCollection(conceptAnnotation.getController(), conceptAnnotation.getTextSource()),
                            ListenableCollection::add,
                            (graphSpace1, graphSpace2) -> graphSpace1)));

            setSelected();
        }

    }
}
