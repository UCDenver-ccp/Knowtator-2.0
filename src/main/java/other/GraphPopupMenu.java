/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package other;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewer;

import javax.swing.*;

public class GraphPopupMenu extends JPopupMenu
{

    /**
     *
     */
    public static final long serialVersionUID = -3132749140550242191L;

    public GraphPopupMenu(GraphViewer graphViewer) {
        mxGraphComponent graphComponent = (mxGraphComponent) graphViewer.getSelectedComponent();

        boolean selected = !graphComponent.getGraph().isSelectionEmpty();

        add(graphViewer.bind(graphComponent,
                "delete",
                mxGraphActions.getDeleteAction()
        )).setEnabled(selected);

        addSeparator();

        add(graphViewer.bind(graphComponent,
                "edit",
                mxGraphActions.getEditAction()
        )).setEnabled(selected);

        addSeparator();

        add(graphViewer.bind(graphComponent,
                "selectVertices",
                mxGraphActions.getSelectVerticesAction()
        ));

        add(graphViewer.bind(graphComponent,
                "selectEdges",
                mxGraphActions.getSelectEdgesAction()
        ));

        addSeparator();

        add(graphViewer.bind(graphComponent,
                "selectAll",
                mxGraphActions.getSelectAllAction()
        ));
    }

}
