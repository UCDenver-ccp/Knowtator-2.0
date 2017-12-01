package other;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import edu.ucdenver.ccp.knowtator.ui.graph.GraphViewer;

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
