package edu.ucdenver.ccp.knowtator.view;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.events.GraphSpaceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceSelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.Annotation;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.chooser.GraphSpaceChooser;
import edu.ucdenver.ccp.knowtator.view.textpane.GraphViewKnowtatorTextPane;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class GraphView extends JPanel implements GraphSpaceSelectionListener, ProjectListener {
	private JButton removeCellButton;
	private JButton addAnnotationNodeButton;
	private JButton applyLayoutButton;
	private JButton previousGraphSpaceButton;
	private JButton nextGraphSpaceButton;
	private mxGraphComponent graphComponent;
	private JButton zoomOutButton;
	private JButton zoomInButton;
	private JPanel panel1;
	private GraphSpaceChooser graphSpaceChooser;
	private GraphViewKnowtatorTextPane knowtatorTextPane;
	private JRadioButton someRadioButton;
	private JRadioButton allRadioButton;
	private JPanel propertyValuePanel;
	private JRadioButton otherRadioButton;
	private JTextField quantifierTextField;
	private JTextField quantifierValueTextField;
	private JDialog dialog;
	private ButtonGroup propertyQuantifierButtons;

	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(GraphView.class);

	private KnowtatorView view;

	GraphView(JDialog dialog, KnowtatorView view) {
		this.dialog = dialog;
		this.view = view;
		view.getController().getSelectionManager().addGraphSpaceListener(this);
		$$$setupUI$$$();
		makeButtons();
	}

	private void createUIComponents() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		graphSpaceChooser = new GraphSpaceChooser(view);
		mxGraph testGraph = new mxGraph();
		graphComponent = new mxGraphComponent(testGraph);

		knowtatorTextPane = new GraphViewKnowtatorTextPane(view);
	}

	private void makeButtons() {
		someRadioButton.addActionListener(e -> {
			view.getController().getSelectionManager().setSelectedPropertyQuantifer("some");
			view.getController().getSelectionManager().setSelectedPropertyQuantifierValue("");
		});
		allRadioButton.addActionListener(e -> {
			view.getController().getSelectionManager().setSelectedPropertyQuantifer("all");
			view.getController().getSelectionManager().setSelectedPropertyQuantifierValue("");
		});
		otherRadioButton.addActionListener(e -> {
			view.getController().getSelectionManager().setSelectedPropertyQuantifer(quantifierTextField.getText());
			view.getController().getSelectionManager().setSelectedPropertyQuantifierValue(quantifierValueTextField.getText());
		});


		graphSpaceChooser.addActionListener(
				e -> {
					JComboBox comboBox = (JComboBox) e.getSource();
					if (comboBox.getSelectedItem() != null
							&& comboBox.getSelectedItem()
							!= view.getController().getSelectionManager().getActiveTextSource()) {
						view.getController()
								.getSelectionManager()
								.setSelectedGraphSpace((GraphSpace) comboBox.getSelectedItem());
					}
				});

		zoomOutButton.addActionListener(e -> graphComponent.zoomOut());
		zoomInButton.addActionListener(e -> graphComponent.zoomIn());
		previousGraphSpaceButton.addActionListener(
				e -> view.getController().getSelectionManager().getPreviousGraphSpace());
		nextGraphSpaceButton.addActionListener(
				e -> view.getController().getSelectionManager().getNextGraphSpace());
		removeCellButton.addActionListener(e -> removeSelectedCell());
		addAnnotationNodeButton.addActionListener(
				e -> {
					Annotation annotation =
							view.getController().getSelectionManager().getSelectedAnnotation();
					mxCell vertex =
							view.getController()
									.getSelectionManager()
									.getActiveGraphSpace()
									.addNode(null, annotation);

					goToVertex(vertex);
				});
		applyLayoutButton.addActionListener(e -> applyLayout());
	}


	@SuppressWarnings("unused")
	public void goToAnnotationVertex(GraphSpace graphSpace, Annotation annotation) {
		if (annotation != null && graphSpace != null) {
			view.getController().getSelectionManager().setSelectedGraphSpace(graphSpace);
			List<Object> vertices = graphSpace.getVerticesForAnnotation(annotation);
			if (vertices.size() > 0) {
				graphSpace.setSelectionCells(vertices);
				goToVertex(vertices.get(0));
			}
		}
	}

	private void goToVertex(Object vertex) {
		dialog.requestFocusInWindow();
		graphComponent.scrollCellToVisible(vertex, true);
	}

	private void showGraph(GraphSpace graphSpace) {
		knowtatorTextPane.setText(
				graphSpace
						.getTextSource()
						.getContent()
						.substring(graphSpace.getGraphTextStart(), graphSpace.getGraphTextEnd()));
		graphComponent.setGraph(graphSpace);

		graphComponent.setName(graphSpace.getId());

		graphSpace.setupListeners();
		someRadioButton.setSelected(true);

		graphSpace.reDrawGraph();
		applyLayout();
		graphComponent.refresh();
	}

	private void applyLayout() {
		GraphSpace graph = view.getController().getSelectionManager().getActiveGraphSpace();
		//		graph.reDrawGraph();
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setOrientation(SwingConstants.WEST);
		layout.setIntraCellSpacing(50);
		layout.setInterRankCellSpacing(125);
		layout.setOrientation(SwingConstants.NORTH);

		try {
			graph.getModel().beginUpdate();
			try {
				layout.execute(graph.getDefaultParent());
			} finally {
				mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

				morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

				morph.startAnimation();
			}
		} finally {
			graph.getModel().endUpdate();
			graphComponent.zoomAndCenter();
		}
	}

	private void removeSelectedCell() {
		view.getController().getSelectionManager().getActiveGraphSpace().removeSelectedCell();
	}

	@Override
	public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
		showGraph(view.getController().getSelectionManager().getActiveGraphSpace());
		applyLayout();
	}

	@Override
	public void projectClosed() {
		graphComponent.setGraph(new mxGraph());
	}

	@Override
	public void projectLoaded() {
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		createUIComponents();
		panel1 = new JPanel();
		panel1.setLayout(new BorderLayout(0, 0));
		panel1.setAlignmentX(0.0f);
		panel1.setAlignmentY(0.0f);
		panel1.setMinimumSize(new Dimension(400, 400));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout(0, 0));
		panel1.add(panel2, BorderLayout.NORTH);
		final JToolBar toolBar1 = new JToolBar();
		toolBar1.setAlignmentX(0.0f);
		toolBar1.setAlignmentY(0.0f);
		toolBar1.setFloatable(false);
		toolBar1.setMinimumSize(new Dimension(630, 100));
		panel2.add(toolBar1, BorderLayout.NORTH);
		zoomOutButton = new JButton();
		zoomOutButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-zoom-out-filled-50 (Custom).png")));
		zoomOutButton.setText("");
		zoomOutButton.setToolTipText(ResourceBundle.getBundle("ui").getString("zoom.out"));
		toolBar1.add(zoomOutButton);
		zoomInButton = new JButton();
		zoomInButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-zoom-in-filled-50 (Custom).png")));
		zoomInButton.setText("");
		zoomInButton.setToolTipText(ResourceBundle.getBundle("ui").getString("zoom.in"));
		toolBar1.add(zoomInButton);
		applyLayoutButton = new JButton();
		this.$$$loadButtonText$$$(applyLayoutButton, ResourceBundle.getBundle("ui").getString("apply.layout"));
		applyLayoutButton.setToolTipText(ResourceBundle.getBundle("ui").getString("apply.layout1"));
		toolBar1.add(applyLayoutButton);
		previousGraphSpaceButton = new JButton();
		previousGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/Previous Graph Space (Custom).png")));
		previousGraphSpaceButton.setText("");
		previousGraphSpaceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.graph.space"));
		toolBar1.add(previousGraphSpaceButton);
		nextGraphSpaceButton = new JButton();
		nextGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/Next Graph Space (Custom).png")));
		nextGraphSpaceButton.setText("");
		nextGraphSpaceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.graph.space"));
		toolBar1.add(nextGraphSpaceButton);
		final JLabel label1 = new JLabel();
		this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("ui").getString("graph.space"));
		toolBar1.add(label1);
		graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
		toolBar1.add(graphSpaceChooser);
		final JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setMaximumSize(new Dimension(32767, 15));
		panel2.add(scrollPane1, BorderLayout.SOUTH);
		knowtatorTextPane.setEditable(false);
		knowtatorTextPane.setText("");
		scrollPane1.setViewportView(knowtatorTextPane);
		final JToolBar toolBar2 = new JToolBar();
		panel2.add(toolBar2, BorderLayout.CENTER);
		addAnnotationNodeButton = new JButton();
		addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/Add annotation Node (Custom).png")));
		addAnnotationNodeButton.setText("");
		addAnnotationNodeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("add.annotation.node"));
		toolBar2.add(addAnnotationNodeButton);
		removeCellButton = new JButton();
		removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/Remove Annotation Node (Custom).png")));
		removeCellButton.setText("");
		removeCellButton.setToolTipText(ResourceBundle.getBundle("ui").getString("remove.item"));
		toolBar2.add(removeCellButton);
		propertyValuePanel = new JPanel();
		propertyValuePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
		propertyValuePanel.setMaximumSize(new Dimension(2147483647, 2147483647));
		propertyValuePanel.setMinimumSize(new Dimension(30, 55));
		toolBar2.add(propertyValuePanel);
		someRadioButton = new JRadioButton();
		someRadioButton.setSelected(true);
		this.$$$loadButtonText$$$(someRadioButton, ResourceBundle.getBundle("log4j").getString("some1"));
		propertyValuePanel.add(someRadioButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 23), null, 0, false));
		allRadioButton = new JRadioButton();
		this.$$$loadButtonText$$$(allRadioButton, ResourceBundle.getBundle("log4j").getString("all1"));
		propertyValuePanel.add(allRadioButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 23), null, 0, false));
		otherRadioButton = new JRadioButton();
		otherRadioButton.setSelected(false);
		this.$$$loadButtonText$$$(otherRadioButton, ResourceBundle.getBundle("log4j").getString("other1"));
		propertyValuePanel.add(otherRadioButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		quantifierTextField = new JTextField();
		propertyValuePanel.add(quantifierTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		quantifierValueTextField = new JTextField();
		propertyValuePanel.add(quantifierValueTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label2 = new JLabel();
		this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("log4j").getString("quantifier1"));
		propertyValuePanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("log4j").getString("value1"));
		propertyValuePanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		graphComponent.setGridVisible(true);
		panel1.add(graphComponent, BorderLayout.CENTER);
		label2.setLabelFor(quantifierTextField);
		label3.setLabelFor(quantifierValueTextField);
		ButtonGroup buttonGroup;
		buttonGroup = new ButtonGroup();
		buttonGroup.add(someRadioButton);
		buttonGroup.add(allRadioButton);
		buttonGroup.add(otherRadioButton);
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadLabelText$$$(JLabel component, String text) {
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setDisplayedMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadButtonText$$$(AbstractButton component, String text) {
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '&') {
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&') {
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic) {
			component.setMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return panel1;
	}
}
