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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.actions.GraphActions;
import edu.ucdenver.ccp.knowtator.actions.KnowtatorCollectionActions;
import edu.ucdenver.ccp.knowtator.model.NoSelectedOWLPropertyException;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.chooser.GraphSpaceChooser;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GraphView extends JPanel implements KnowtatorComponent {
	private final JDialog dialog;
	private final KnowtatorView view;
	private JButton removeCellButton;
	private JButton addAnnotationNodeButton;
	private JButton applyLayoutButton;
	private JButton previousGraphSpaceButton;
	private JButton nextGraphSpaceButton;
	private mxGraphComponent graphComponent;
	private JPanel panel1;
	private GraphSpaceChooser graphSpaceChooser;
	private JButton addGraphSpaceButton;
	private JButton removeGraphSpaceButton;
	private JSlider zoomSlider;
	private JButton graphMenuButton;
	private JButton renameButton;
	private List<JComponent> graphSpaceButtons;
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(GraphView.class);


	GraphView(JDialog dialog, KnowtatorView view) {
		this.dialog = dialog;
		this.view = view;
		$$$setupUI$$$();
		makeButtons();

		new TextBoundModelListener(view.getController()) {
			@Override
			public void respondToConceptAnnotationModification() {

			}

			@Override
			public void respondToSpanModification() {

			}

			@Override
			public void respondToGraphSpaceModification() {

			}

			public void respondToGraphSpaceCollectionFirstAdded() {
				if (isVisible()) {
					graphSpaceButtons.forEach(c -> c.setEnabled(true));
				}
			}

			public void respondToGraphSpaceCollectionEmptied() {
				if (isVisible()) {
					graphSpaceButtons.forEach(c -> c.setEnabled(false));
					addGraphSpaceButton.setEnabled(true);
				}
			}

			public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
				if (isVisible() && event.getNew() != null) {
					try {
						showGraph(event.getNew().getGraphSpaceCollection().getSelection());
					} catch (NoSelectionException e) {
						e.printStackTrace();
					}
				}
			}

			public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {
				if (isVisible() && event.getNew() != null && event.getNew() != graphComponent.getGraph()) {
					showGraph(event.getNew());
				}
			}

			public void respondToGraphSpaceRemoved() {
			}

			public void respondToGraphSpaceAdded() {
			}

			public void respondToConceptAnnotationCollectionEmptied() {
			}

			public void respondToConceptAnnotationRemoved() {
			}

			public void respondToConceptAnnotationAdded() {
			}

			public void respondToConceptAnnotationCollectionFirstAdded() {
			}

			public void respondToSpanCollectionFirstAdded() {

			}

			public void respondToSpanCollectionEmptied() {
			}

			public void respondToSpanRemoved() {
			}

			public void respondToSpanAdded() {
			}

			public void respondToSpanSelection(SelectionEvent<Span> event) {
			}

			public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
			}

			public void respondToTextSourceAdded() {

			}

			public void respondToTextSourceRemoved() {
			}

			public void respondToTextSourceCollectionEmptied() {
			}

			public void respondToTextSourceCollectionFirstAdded() {
			}
		};
	}

	private void makeButtons() {

		graphSpaceButtons = new ArrayList<>();

		graphMenuButton.addActionListener(e -> {
			GraphMenuDialog graphMenuDialog = new GraphMenuDialog(view);
			graphMenuDialog.pack();
			graphMenuDialog.setVisible(true);
		});

		zoomSlider.addChangeListener(e -> graphComponent.zoomTo(zoomSlider.getValue() / 50.0, false));
		renameButton.addActionListener(e -> {
			try {


				TextSource textSource = view.getController().getTextSourceCollection().getSelection();
				String graphName = getGraphNameInput(view, textSource, null);
				if (graphName != null) {
					textSource.getGraphSpaceCollection().getSelection().setId(graphName);
				}
			} catch (NoSelectionException e2) {
				e2.printStackTrace();
			}
		});
		addGraphSpaceButton.addActionListener(e -> {
			try {
				TextSource textSource = view.getController().getTextSourceCollection().getSelection();
				String graphName = getGraphNameInput(view, textSource, null);

				if (graphName != null) {
					AbstractKnowtatorAction action = new KnowtatorCollectionActions.GraphSpaceAction(KnowtatorCollectionActions.ADD, view.getController(), graphName);
					view.getController().registerAction(action);
				}
			} catch (NoSelectionException e2) {
				e2.printStackTrace();
			}
		});
		removeGraphSpaceButton.addActionListener(e -> {
			try {


				if (JOptionPane.showConfirmDialog(view, "Are you sure you want to delete this graph?") == JOptionPane.YES_OPTION) {
					AbstractKnowtatorAction action = new KnowtatorCollectionActions.GraphSpaceAction(KnowtatorCollectionActions.REMOVE, view.getController(), null);
					view.getController().registerAction(action);
				}
			} catch (NoSelectionException e2) {
				e2.printStackTrace();
			}
		});
		previousGraphSpaceButton.addActionListener(e -> {
			try {
				view.getController().getTextSourceCollection().getSelection().getGraphSpaceCollection()
						.selectPrevious();
			} catch (NoSelectionException e2) {
				e2.printStackTrace();
			}
		});
		nextGraphSpaceButton.addActionListener(e -> {
			try {
				view.getController().getTextSourceCollection().getSelection().getGraphSpaceCollection()
						.selectNext();
			} catch (NoSelectionException e2) {
				e2.printStackTrace();
			}
		});
		removeCellButton.addActionListener(e -> {
			try {
				AbstractKnowtatorAction action = new GraphActions.removeCellsAction(view.getController());
				view.getController().registerAction(action);
			} catch (NoSelectionException e1) {
				e1.printStackTrace();
			}
		});
		addAnnotationNodeButton.addActionListener(e -> {
			try {
				AbstractKnowtatorAction action = new GraphActions.AddAnnotationNodeAction(view, view.getController());
				view.getController().registerAction(action);
			} catch (NoSelectionException e1) {
				e1.printStackTrace();
			}
		});
		applyLayoutButton.addActionListener(e -> {
			try {
				AbstractKnowtatorAction action = new GraphActions.applyLayoutAction(view, view.getController());
				view.getController().registerAction(action);
			} catch (NoSelectionException e1) {
				e1.printStackTrace();
			}
		});

		graphSpaceButtons.add(renameButton);
		graphSpaceButtons.add(removeCellButton);
		graphSpaceButtons.add(removeGraphSpaceButton);
		graphSpaceButtons.add(previousGraphSpaceButton);
		graphSpaceButtons.add(nextGraphSpaceButton);
		graphSpaceButtons.add(addAnnotationNodeButton);
		graphSpaceButtons.add(applyLayoutButton);
		graphSpaceButtons.add(zoomSlider);
		graphSpaceButtons.add(addGraphSpaceButton);
	}

	private void showGraph(GraphSpace graphSpace) {
		graphComponent.setGraph(graphSpace);

		graphComponent.setName(graphSpace.getId());

		setupListeners(graphSpace);

		graphSpace.reDrawGraph();
		graphComponent.refresh();
	}

	private void createUIComponents() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		graphSpaceChooser = new GraphSpaceChooser(view);
		mxGraph testGraph = new mxGraph();
		graphComponent = new mxGraphComponent(testGraph);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			graphSpaceChooser.reset();
			try {
				TextSource textSource = view.getController().getTextSourceCollection().getSelection();
				try {
					showGraph(textSource.getGraphSpaceCollection().getSelection());
				} catch (NoSelectionException e) {
					textSource.getGraphSpaceCollection().selectNext();
					try {
						showGraph(textSource.getGraphSpaceCollection().getSelection());
					} catch (NoSelectionException e1) {
						try {
							String graphName = getGraphNameInput(view, textSource, null);

							if (graphName != null) {
								AbstractKnowtatorAction action = new KnowtatorCollectionActions.GraphSpaceAction(KnowtatorCollectionActions.ADD, view.getController(), graphName);
								view.getController().registerAction(action);
							}
						} catch (NoSelectionException e2) {
							e2.printStackTrace();
						}
					}
				}
			} catch (NoSelectionException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getGraphNameInput(KnowtatorView view, TextSource textSource, JTextField field1) {
		if (field1 == null) {
			field1 = new JTextField();

			JTextField finalField = field1;
			field1
					.getDocument()
					.addDocumentListener(
							new DocumentListener() {
								@Override
								public void insertUpdate(DocumentEvent e) {
									warn();
								}

								@Override
								public void removeUpdate(DocumentEvent e) {
									warn();
								}

								@Override
								public void changedUpdate(DocumentEvent e) {
									warn();
								}

								private void warn() {
									if (textSource
											.getGraphSpaceCollection()
											.containsID(finalField.getText())) {
										try {
											finalField
													.getHighlighter()
													.addHighlight(
															0,
															finalField.getText().length(),
															new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
										} catch (BadLocationException e1) {
											e1.printStackTrace();
										}
									} else {
										finalField.getHighlighter().removeAllHighlights();
									}
								}
							});
		}
		Object[] message = {
				"Graph Title", field1,
		};
		field1.addAncestorListener(new GraphView.RequestFocusListener());
		field1.setText("Graph Space " + Integer.toString(textSource.getGraphSpaceCollection().size()));
		int option =
				JOptionPane.showConfirmDialog(
						view,
						message,
						"Enter a name for this graph",
						JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if (textSource
					.getGraphSpaceCollection()
					.containsID(field1.getText())) {
				JOptionPane.showMessageDialog(field1, "Graph name already in use");
				return getGraphNameInput(view, textSource, field1);
			} else {
				return field1.getText();
			}
		}

		return null;
	}

	private void setupListeners(GraphSpace graphSpace) {
		// Handle drag and drop
		// Adds the current selected object property as the edge value
		if (!graphSpace.areListenersSet()) {
			graphSpace.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
				Object[] cells = (Object[]) evt.getProperty("cells");
				if (cells != null && cells.length > 0) {
					Arrays.stream(cells).filter(cell -> graphSpace.getModel().isEdge(cell) && "".equals(((mxCell) cell).getValue())).map(cell -> (mxCell) cell).forEach(edge -> {
						try {
							OWLObjectProperty property = view.getController().getOWLModel().getSelectedOWLObjectProperty();
							String propertyID = view.getController().getOWLModel().getOWLEntityRendering(property);
							RelationOptionsDialog relationOptionsDialog = getRelationOptionsDialog(propertyID);
							if (relationOptionsDialog.getResult() == RelationOptionsDialog.OK_OPTION) {
								try {
									AbstractKnowtatorAction action = new GraphActions.AddTripleAction(view.getController(),
											(AnnotationNode) edge.getSource(),
											(AnnotationNode) edge.getTarget(),
											property, relationOptionsDialog.getPropertyID(),
											relationOptionsDialog.getQuantifier(), relationOptionsDialog.getQuantifierValue(),
											relationOptionsDialog.getNegation());
									view.getController().registerAction(action);
								} catch (NoSelectionException e) {
									e.printStackTrace();
								}

							}
						} catch (NoSelectedOWLPropertyException e) {
							e.printStackTrace();
						}
						graphSpace.getModel().remove(edge);
					});

					graphSpace.reDrawGraph();
				}
			});

			graphSpace.addListener(mxEvent.MOVE_CELLS, (sender, evt) -> graphSpace.reDrawGraph());

			graphSpace.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> graphSpace.reDrawGraph());

			graphSpace.getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
				Collection selectedCells = (Collection) evt.getProperty("removed");
				Collection deselectedCells = (Collection) evt.getProperty("added");
				if (deselectedCells != null && deselectedCells.size() > 0) {
					for (Object cell : deselectedCells) {
						if (cell instanceof AnnotationNode) {
							graphSpace.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "0", new Object[]{cell});
						}
					}
					graphSpace.reDrawGraph();
				}

				if (selectedCells != null && selectedCells.size() > 0) {
					for (Object cell : selectedCells) {
						if (cell instanceof AnnotationNode) {
							try {
								ConceptAnnotation conceptAnnotation = ((AnnotationNode) cell).getConceptAnnotation();
								view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().setSelection(conceptAnnotation);
								graphSpace.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "4", new Object[]{cell});
							} catch (NoSelectionException e) {
								e.printStackTrace();
							}


						}
					}
					graphSpace.reDrawGraph();
				}
			});
			graphSpace.setListenersSet();
		}
	}

	private RelationOptionsDialog getRelationOptionsDialog(String propertyID) {
		RelationOptionsDialog relationOptionsDialog = new RelationOptionsDialog(dialog, propertyID);
		relationOptionsDialog.pack();
		relationOptionsDialog.setAlwaysOnTop(true);
		relationOptionsDialog.setLocationRelativeTo(dialog);
		relationOptionsDialog.requestFocus();
		relationOptionsDialog.setVisible(true);
		return relationOptionsDialog;
	}

	@Override
	public void reset() {
		graphSpaceChooser.reset();
	}

	@Override
	public void dispose() {
		graphSpaceChooser.dispose();
	}

	public mxGraphComponent getGraphComponent() {
		return graphComponent;
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
		panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel2, BorderLayout.NORTH);
		graphMenuButton = new JButton();
		graphMenuButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-menu-24.png")));
		graphMenuButton.setText("");
		panel2.add(graphMenuButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JToolBar toolBar1 = new JToolBar();
		toolBar1.setFloatable(false);
		panel2.add(toolBar1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
		addAnnotationNodeButton = new JButton();
		addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addAnnotationNodeButton.setText("");
		toolBar1.add(addAnnotationNodeButton);
		removeCellButton = new JButton();
		removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeCellButton.setText("");
		toolBar1.add(removeCellButton);
		final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
		toolBar1.add(toolBar$Separator1);
		applyLayoutButton = new JButton();
		applyLayoutButton.setText("Apply Layout");
		toolBar1.add(applyLayoutButton);
		final Spacer spacer1 = new Spacer();
		panel2.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new BorderLayout(0, 0));
		panel1.add(panel3, BorderLayout.CENTER);
		graphComponent.setCenterPage(false);
		graphComponent.setGridVisible(true);
		panel3.add(graphComponent, BorderLayout.CENTER);
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel4, BorderLayout.SOUTH);
		previousGraphSpaceButton = new JButton();
		previousGraphSpaceButton.setText("Previous");
		panel4.add(previousGraphSpaceButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		graphSpaceChooser.setMaximumSize(new Dimension(200, 32767));
		graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
		panel4.add(graphSpaceChooser, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addGraphSpaceButton = new JButton();
		addGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addGraphSpaceButton.setText("");
		panel4.add(addGraphSpaceButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		removeGraphSpaceButton = new JButton();
		removeGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeGraphSpaceButton.setText("");
		panel4.add(removeGraphSpaceButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		zoomSlider = new JSlider();
		zoomSlider.setMaximum(100);
		panel4.add(zoomSlider, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(194, 16), null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel4.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		renameButton = new JButton();
		renameButton.setText("Rename");
		panel4.add(renameButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nextGraphSpaceButton = new JButton();
		nextGraphSpaceButton.setText("Next");
		panel4.add(nextGraphSpaceButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return panel1;
	}

	/**
	 * Taken from https://tips4java.wordpress.com/2010/03/14/dialog-focus/
	 */
	public static class RequestFocusListener implements AncestorListener {
		private final boolean removeListener;

		/*
		 *  Convenience constructor. The listener is only used once and then it is
		 *  removed from the component.
		 */
		RequestFocusListener() {
			this(true);
		}

		/*
		 *  Constructor that controls whether this listen can be used once or
		 *  multiple times.
		 *
		 *  @param removeCollectionListener when true this listener is only invoked once
		 *                        otherwise it can be invoked multiple times.
		 */
		@SuppressWarnings("SameParameterValue")
		RequestFocusListener(boolean removeListener) {
			this.removeListener = removeListener;
		}

		@Override
		public void ancestorAdded(AncestorEvent e) {
			JComponent component = e.getComponent();
			component.requestFocusInWindow();

			if (removeListener) component.removeAncestorListener(this);
		}

		@Override
		public void ancestorRemoved(AncestorEvent e) {
		}

		@Override
		public void ancestorMoved(AncestorEvent e) {
		}
	}


}
