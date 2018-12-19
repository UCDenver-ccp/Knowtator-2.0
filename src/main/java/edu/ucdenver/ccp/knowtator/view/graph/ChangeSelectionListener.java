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

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;

import java.util.Collection;

class ChangeSelectionListener implements mxEventSource.mxIEventListener {
	private final GraphView graphView;

	ChangeSelectionListener(GraphView graphView) {
		this.graphView = graphView;
	}

	@Override
	public void invoke(Object sender, mxEventObject evt) {
		Collection selectedCells = (Collection) evt.getProperty("removed");
		Collection deselectedCells = (Collection) evt.getProperty("added");
		if (deselectedCells != null && deselectedCells.size() > 0) {
			for (Object cell : deselectedCells) {
				if (cell instanceof AnnotationNode) {
					((AnnotationNode) cell).setStyle("defaultVertex");
				}
			}
		}

		if (selectedCells != null && selectedCells.size() > 0) {
			for (Object cell : selectedCells) {
				if (cell instanceof AnnotationNode) {
					((AnnotationNode) cell).setStyle("selected");
				}
			}
		}

		graphView.reDrawGraph(graphView.getGraphComponent().getGraph());
	}
}
