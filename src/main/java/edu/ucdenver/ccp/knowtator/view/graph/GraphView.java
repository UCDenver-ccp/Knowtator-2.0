package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.ActiveGraphSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.chooser.GraphSpaceChooser;
import edu.ucdenver.ccp.knowtator.view.menu.GraphMenu;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class GraphView extends JPanel implements ViewListener, ProjectListener {
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
	private JRadioButton someRadioButton;
	private JRadioButton onlyRadioButton;
	private JRadioButton exactlyRadioButton;
	private JRadioButton minRadioButton;
	private JRadioButton maxRadioButton;
	private JTextField exactlyValueTextField;
	private JCheckBox negateCheckBox;
	private JPanel propertyValuePanel;
	private JTextField minValueTextField;
	private JTextField maxValueTextField;
	private JDialog dialog;

	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(GraphView.class);

	private KnowtatorView view;

	GraphView(JDialog dialog, KnowtatorView view) {
		this.dialog = dialog;
		this.view = view;
		$$$setupUI$$$();
		makeButtons();
	}

	private void createUIComponents() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		graphSpaceChooser = new GraphSpaceChooser(view);
		mxGraph testGraph = new mxGraph();
		graphComponent = new mxGraphComponent(testGraph);
	}

	private void quantifierButtonAction(String quantifier) {
		try {
			view.getController()
					.getSelectionManager()
					.getActiveTextSource()
					.getGraphSpaceManager()
					.getActiveGraphSpace()
					.getRelationSelectionManager()
					.setSelectedPropertyQuantifer(quantifier);

			if (quantifier.equals("some") || quantifier.equals("only")) {
				view.getController()
						.getSelectionManager()
						.getActiveTextSource()
						.getGraphSpaceManager()
						.getActiveGraphSpace()
						.getRelationSelectionManager()
						.setSelectedRelationQuantifierValue("");
			}
		} catch (ActiveTextSourceNotSetException
				| ControllerNotSetException
				| ActiveGraphSpaceNotSetException ignored) {

		}
	}

	private void quantifierTextFieldAction(String quantifierValue) {
		try {
			view.getController()
					.getSelectionManager()
					.getActiveTextSource()
					.getGraphSpaceManager()
					.getActiveGraphSpace()
					.getRelationSelectionManager()
					.setSelectedRelationQuantifierValue(quantifierValue);
		} catch (ControllerNotSetException
				| ActiveTextSourceNotSetException
				| ActiveGraphSpaceNotSetException ignored) {

		}
	}

	private void makeButtons() {
		someRadioButton.addActionListener(e -> quantifierButtonAction("some"));
		onlyRadioButton.addActionListener(e -> quantifierButtonAction("only"));
		minRadioButton.addActionListener(e -> quantifierButtonAction("min"));
		maxRadioButton.addActionListener(e -> quantifierButtonAction("max"));
		exactlyRadioButton.addActionListener(e -> quantifierButtonAction("exactly"));
		makeQuantifierTextField(minValueTextField, minRadioButton);
		makeQuantifierTextField(maxValueTextField, maxRadioButton);
		makeQuantifierTextField(exactlyValueTextField, exactlyRadioButton);
		negateCheckBox.addActionListener(
				e -> {
					try {
						view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getGraphSpaceManager()
								.getActiveGraphSpace()
								.getRelationSelectionManager()
								.setNegatation(negateCheckBox.isSelected());
					} catch (ActiveTextSourceNotSetException
							| ControllerNotSetException
							| ActiveGraphSpaceNotSetException ignored) {

					}
				});

		graphSpaceChooser.addActionListener(
				e -> {
					JComboBox comboBox = (JComboBox) e.getSource();
					try {
						if (comboBox.getSelectedItem() != null
								&& comboBox.getSelectedItem()
								!= view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getGraphSpaceManager()
								.getActiveGraphSpace()) {
							view.getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getGraphSpaceManager()
									.setSelectedGraphSpace((GraphSpace) comboBox.getSelectedItem());
						}
					} catch (ActiveTextSourceNotSetException
							| ControllerNotSetException
							| ActiveGraphSpaceNotSetException ignored) {

					}
				});

		zoomOutButton.addActionListener(e -> graphComponent.zoomOut());
		zoomInButton.addActionListener(e -> graphComponent.zoomIn());
		previousGraphSpaceButton.addActionListener(
				e -> {
					try {
						view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getGraphSpaceManager()
								.getPreviousGraphSpace();
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});
		nextGraphSpaceButton.addActionListener(
				e -> {
					try {
						view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getGraphSpaceManager()
								.getNextGraphSpace();
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});
		removeCellButton.addActionListener(e -> removeSelectedCell());
		addAnnotationNodeButton.addActionListener(
				e -> {
					try {
						Annotation annotation =
								view.getController()
										.getSelectionManager()
										.getActiveTextSource()
										.getAnnotationManager()
										.getSelectedAnnotation();

						mxCell vertex =
								view.getController()
										.getSelectionManager()
										.getActiveTextSource()
										.getGraphSpaceManager()
										.getActiveGraphSpace()
										.addNode(null, annotation, 20, 20);

						goToVertex(vertex);
					} catch (ActiveTextSourceNotSetException
							| ControllerNotSetException
							| ActiveGraphSpaceNotSetException ignored) {

					}
				});
		applyLayoutButton.addActionListener(e -> applyLayout());
	}

	private void makeQuantifierTextField(JTextField quantifierValueTextField, JRadioButton quantifierRadioButton) {
		quantifierValueTextField
				.getDocument()
				.addDocumentListener(
						new DocumentListener() {
							@Override
							public void insertUpdate(DocumentEvent e) {
								quantifierTextFieldAction(quantifierValueTextField.getText());
								quantifierRadioButton.doClick();
							}

							@Override
							public void removeUpdate(DocumentEvent e) {
								quantifierTextFieldAction(quantifierValueTextField.getText());
								quantifierRadioButton.doClick();
							}

							@Override
							public void changedUpdate(DocumentEvent e) {
							}
						});
	}

	@SuppressWarnings("unused")
	public void goToAnnotationVertex(GraphSpace graphSpace, Annotation annotation) {
		if (annotation != null && graphSpace != null) {
			try {
				view.getController()
						.getSelectionManager()
						.getActiveTextSource()
						.getGraphSpaceManager()
						.setSelectedGraphSpace(graphSpace);
			} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

			}
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
		graphComponent.setGraph(graphSpace);

		graphComponent.setName(graphSpace.getId());
		exactlyValueTextField.setText("");
		minValueTextField.setText("");
		maxValueTextField.setText("");

		graphSpace.setupListeners();

		graphSpace.reDrawGraph();
		graphComponent.refresh();
	}

	private void applyLayout() {
		GraphSpace graph;
		try {
			graph =
					view.getController()
							.getSelectionManager()
							.getActiveTextSource()
							.getGraphSpaceManager()
							.getActiveGraphSpace();

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
		} catch (ActiveTextSourceNotSetException
				| ControllerNotSetException
				| ActiveGraphSpaceNotSetException ignored) {
		}
	}

	private void removeSelectedCell() {
		try {
			view.getController()
					.getSelectionManager()
					.getActiveTextSource()
					.getGraphSpaceManager()
					.getActiveGraphSpace()
					.removeSelectedCell();
		} catch (ActiveTextSourceNotSetException
				| ControllerNotSetException
				| ActiveGraphSpaceNotSetException ignored) {

		}
	}

	@Override
	public void projectClosed() {
		graphComponent.setGraph(new mxGraph());
	}

	@Override
	public void projectLoaded() {
	}

	public void setController(KnowtatorController controller) {
		controller.addViewListener(this);
		controller.addViewListener(graphSpaceChooser);
	}

	@Override
	public void viewChanged() {
		try {
			GraphSpace graphSpace =
					view.getController()
							.getSelectionManager()
							.getActiveTextSource()
							.getGraphSpaceManager()
							.getActiveGraphSpace();
			String quantifier = graphSpace.getRelationSelectionManager().getSelectedRelationQuantifier();
			if (quantifier != null) {
				switch (quantifier) {
					case "only":
						onlyRadioButton.setSelected(true);
						break;
					case "exactly":
						exactlyRadioButton.setSelected(true);
//						exactlyValueTextField.setText(
//								graphSpace.getRelationSelectionManager().getSelectedRelationQuantifierValue());
						break;
					case "min":
						minRadioButton.setSelected(true);
//						minValueTextField.setText(
//								graphSpace.getRelationSelectionManager().getSelectedRelationQuantifierValue());
						break;
					case "max":
						maxRadioButton.setSelected(true);
//						maxValueTextField.setText(
//								graphSpace.getRelationSelectionManager().getSelectedRelationQuantifierValue());
						break;
					default:
						someRadioButton.setSelected(true);
						break;
				}
			}

			negateCheckBox.setSelected(graphSpace.getRelationSelectionManager().isSelectedNegation());
			if (graphComponent.getGraph() != graphSpace) {
				showGraph(graphSpace);
			}
		} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

		} catch (ActiveGraphSpaceNotSetException e) {
			String graphName = GraphMenu.getGraphNameInput(view, null);

			if (graphName != null) {
				try {
					view.getController()
							.getSelectionManager()
							.getActiveTextSource()
							.getGraphSpaceManager()
							.addGraphSpace(graphName);
					someRadioButton.doClick();
					negateCheckBox.setSelected(false);
				} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

				}
			}
		}
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
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new BorderLayout(0, 0));
		panel3.setMaximumSize(new Dimension(100, 100));
		panel3.setMinimumSize(new Dimension(24, 48));
		toolBar1.add(panel3);
		previousGraphSpaceButton = new JButton();
		previousGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
		previousGraphSpaceButton.setPreferredSize(new Dimension(24, 16));
		previousGraphSpaceButton.setText("");
		previousGraphSpaceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.graph.space"));
		panel3.add(previousGraphSpaceButton, BorderLayout.NORTH);
		final JLabel label1 = new JLabel();
		label1.setPreferredSize(new Dimension(24, 16));
		this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("ui").getString("graph.space"));
		panel3.add(label1, BorderLayout.CENTER);
		nextGraphSpaceButton = new JButton();
		nextGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
		nextGraphSpaceButton.setPreferredSize(new Dimension(24, 16));
		nextGraphSpaceButton.setText("");
		nextGraphSpaceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.graph.space"));
		panel3.add(nextGraphSpaceButton, BorderLayout.SOUTH);
		graphSpaceChooser.setMaximumSize(new Dimension(200, 32767));
		graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
		toolBar1.add(graphSpaceChooser);
		final JToolBar toolBar2 = new JToolBar();
		toolBar2.setFloatable(false);
		panel2.add(toolBar2, BorderLayout.CENTER);
		addAnnotationNodeButton = new JButton();
		addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addAnnotationNodeButton.setText("");
		addAnnotationNodeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("add.annotation.node"));
		toolBar2.add(addAnnotationNodeButton);
		removeCellButton = new JButton();
		removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeCellButton.setText("");
		removeCellButton.setToolTipText(ResourceBundle.getBundle("ui").getString("remove.item"));
		toolBar2.add(removeCellButton);
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new BorderLayout(0, 0));
		panel1.add(panel4, BorderLayout.CENTER);
		graphComponent.setCenterPage(false);
		graphComponent.setGridVisible(true);
		panel4.add(graphComponent, BorderLayout.CENTER);
		propertyValuePanel = new JPanel();
		propertyValuePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
		propertyValuePanel.setMaximumSize(new Dimension(2147483647, 2147483647));
		propertyValuePanel.setMinimumSize(new Dimension(30, 55));
		panel1.add(propertyValuePanel, BorderLayout.EAST);
		someRadioButton = new JRadioButton();
		someRadioButton.setSelected(true);
		this.$$$loadButtonText$$$(someRadioButton, ResourceBundle.getBundle("log4j").getString("some1"));
		propertyValuePanel.add(someRadioButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 23), null, 0, false));
		onlyRadioButton = new JRadioButton();
		this.$$$loadButtonText$$$(onlyRadioButton, ResourceBundle.getBundle("log4j").getString("only1"));
		propertyValuePanel.add(onlyRadioButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 23), null, 0, false));
		exactlyRadioButton = new JRadioButton();
		this.$$$loadButtonText$$$(exactlyRadioButton, ResourceBundle.getBundle("log4j").getString("exactly1"));
		propertyValuePanel.add(exactlyRadioButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		minRadioButton = new JRadioButton();
		this.$$$loadButtonText$$$(minRadioButton, ResourceBundle.getBundle("log4j").getString("min1"));
		propertyValuePanel.add(minRadioButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		maxRadioButton = new JRadioButton();
		this.$$$loadButtonText$$$(maxRadioButton, ResourceBundle.getBundle("log4j").getString("max1"));
		propertyValuePanel.add(maxRadioButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		exactlyValueTextField = new JTextField();
		propertyValuePanel.add(exactlyValueTextField, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
		propertyValuePanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		minValueTextField = new JTextField();
		propertyValuePanel.add(minValueTextField, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		maxValueTextField = new JTextField();
		propertyValuePanel.add(maxValueTextField, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		negateCheckBox = new JCheckBox();
		this.$$$loadButtonText$$$(negateCheckBox, ResourceBundle.getBundle("log4j").getString("negate"));
		propertyValuePanel.add(negateCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		ButtonGroup buttonGroup;
		buttonGroup = new ButtonGroup();
		buttonGroup.add(someRadioButton);
		buttonGroup.add(onlyRadioButton);
		buttonGroup.add(exactlyRadioButton);
		buttonGroup.add(minRadioButton);
		buttonGroup.add(maxRadioButton);
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
