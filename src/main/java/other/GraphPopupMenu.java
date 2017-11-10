package other;

import com.mxgraph.swing.util.mxGraphActions;
import edu.ucdenver.ccp.knowtator.ui.graph.KnowtatorGraphViewer;

import javax.swing.*;

public class GraphPopupMenu extends JPopupMenu
{

	/**
	 * 
	 */
	public static final long serialVersionUID = -3132749140550242191L;

	public GraphPopupMenu(KnowtatorGraphViewer graphViewer)
	{
		boolean selected = !graphViewer.getGraph().isSelectionEmpty();

//		add(graphViewer.bind(
//				"cut",
//				TransferHandler.getCutAction(),
//				"/com/mxgraph/examples/swing/images/cut.gif"
//		)).setEnabled(selected);
//
//		add(graphViewer.bind(
//				"copy",
//				TransferHandler.getCopyAction(),
//				"/com/mxgraph/examples/swing/images/copy.gif"
//		)).setEnabled(selected);
//
//		add(graphViewer.bind(
//				"paste",
//				TransferHandler.getPasteAction(),
//				"/com/mxgraph/examples/swing/images/paste.gif"
//		));

		addSeparator();

		add(graphViewer.bind(
				"delete",
				mxGraphActions.getDeleteAction(),
				"/com/mxgraph/examples/swing/images/delete.gif"
		)).setEnabled(selected);

		addSeparator();

		add(graphViewer.bind(
				"edit",
				mxGraphActions.getEditAction()
		)).setEnabled(selected);

		addSeparator();

		add(graphViewer.bind(
				"selectVertices",
				mxGraphActions.getSelectVerticesAction()
		));

		add(graphViewer.bind(
				"selectEdges",
				mxGraphActions.getSelectEdgesAction()
		));

		addSeparator();

		add(graphViewer.bind(
				"selectAll",
				mxGraphActions.getSelectAllAction()
		));
	}

}
