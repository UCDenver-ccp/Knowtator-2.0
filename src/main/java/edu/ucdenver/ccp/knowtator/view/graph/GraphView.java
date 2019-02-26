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

package edu.ucdenver.ccp.knowtator.view.graph;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.graph.GraphActions;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.GraphSpaceAction;
import edu.ucdenver.ccp.knowtator.view.chooser.GraphSpaceChooser;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import org.apache.log4j.Logger;

/**
 * The type Graph view.
 */
public class GraphView extends JPanel implements KnowtatorComponent, ModelListener {
  @SuppressWarnings("unused")
  private Logger log = Logger.getLogger(GraphView.class);

  private final JDialog dialog;
  private final KnowtatorView view;
  private final AddRelationListener addRelationListener;
  private JButton removeCellButton;
  private JButton addAnnotationNodeButton;
  private JButton applyLayoutButton;
  private JButton previousGraphSpaceButton;
  private JButton nextGraphSpaceButton;
  private mxGraphComponent graphComponent;
  private JPanel rootPane;
  private GraphSpaceChooser graphSpaceChooser;
  private JButton addGraphSpaceButton;
  private JButton removeGraphSpaceButton;
  private JSlider zoomSlider;
  private JButton renameButton;
  private JButton exportToImagePngButton;
  private JCheckBox negatedCheckBox;
  private JLabel relationIdLabel;
  private JLabel relationOwlObjectPropertyLabel;
  private JComboBox quantifierChooser;
  private JFormattedTextField quantifierValueTextField;
  private JTextArea textArea1;
  private JPanel nodeCard;
  private JPanel relationCard;
  private JLabel nodeIdLabel;
  private JLabel conceptAnnotationLabel;
  private JPanel graphPanel;
  private JPanel graphInfoPanel;
  private JPanel header;
  private JSplitPane body;
  private List<JComponent> graphSpaceButtons;
  private final mxEventSource.mxIEventListener removeCellsListener;
  private final mxEventSource.mxIEventListener moveCellsListener =
      (sender, evt) -> {
        if (sender instanceof GraphSpace) {
          reDrawGraph((GraphSpace) sender);
        }
      };
  private final ChangeSelectionListener changeSelectionListener = new ChangeSelectionListener(this);

  /**
   * Instantiates a new Graph view.
   *
   * @param dialog the dialog
   * @param view   the view
   */
  GraphView(JDialog dialog, KnowtatorView view) {
    this.dialog = dialog;
    this.view = view;
    setVisible(false);
    $$$setupUI$$$();
    makeButtons();

    addRelationListener = new AddRelationListener(view, this);
    removeCellsListener =
        (sender, evt) -> {
          if (sender instanceof GraphSpace) {
            reDrawGraph((GraphSpace) sender);
          }
        };
  }

  private void makeButtons() {

    exportToImagePngButton.addActionListener(
        e ->
            view.getModel()
                .ifPresent(
                    model ->
                        model
                            .getSelectedTextSource()
                            .ifPresent(
                                textSource ->
                                    textSource
                                        .getSelectedGraphSpace()
                                        .ifPresent(
                                            graphSpace -> {
                                              JFileChooser fileChooser = new JFileChooser();
                                              fileChooser.setCurrentDirectory(
                                                  model.getSaveLocation());
                                              FileFilter fileFilter =
                                                  new FileNameExtensionFilter("PNG", "png");
                                              fileChooser.setFileFilter(fileFilter);
                                              fileChooser.setSelectedFile(
                                                  new File(
                                                      String.format(
                                                          "%s_%s.png",
                                                          textSource.getId(), graphSpace.getId())));
                                              if (fileChooser.showSaveDialog(view)
                                                  == JFileChooser.APPROVE_OPTION) {
                                                BufferedImage image =
                                                    mxCellRenderer.createBufferedImage(
                                                        graphSpace,
                                                        null,
                                                        1,
                                                        Color.WHITE,
                                                        true,
                                                        null);
                                                try {
                                                  ImageIO.write(
                                                      image,
                                                      "PNG",
                                                      new File(
                                                          fileChooser
                                                              .getSelectedFile()
                                                              .getAbsolutePath()));
                                                } catch (IOException e1) {
                                                  e1.printStackTrace();
                                                }
                                              }
                                            }))));

    zoomSlider.addChangeListener(e -> graphComponent.zoomTo(zoomSlider.getValue() / 50.0, false));
    renameButton.addActionListener(
        e ->
            view.getModel()
                .flatMap(BaseModel::getSelectedTextSource)
                .ifPresent(
                    textSource ->
                        textSource
                            .getSelectedGraphSpace()
                            .ifPresent(
                                graphSpace ->
                                    getGraphNameInput(view, textSource, null)
                                        .ifPresent(graphSpace::setId))));
    addGraphSpaceButton.addActionListener(
        e -> view.getModel().flatMap(BaseModel::getSelectedTextSource).ifPresent(this::makeGraph));
    removeGraphSpaceButton.addActionListener(
        e -> {
          if (JOptionPane.showConfirmDialog(view, "Are you sure you want to delete this graph?")
              == JOptionPane.YES_OPTION) {
            view.getModel()
                .ifPresent(
                    model ->
                        model
                            .getSelectedTextSource()
                            .ifPresent(
                                textSource -> {
                                  try {
                                    model.registerAction(
                                        new GraphSpaceAction(model, REMOVE, null, textSource));
                                  } catch (ActionUnperformable e1) {
                                    JOptionPane.showMessageDialog(view, e1.getMessage());
                                  }
                                }));
          }
        });
    previousGraphSpaceButton.addActionListener(
        e ->
            view.getModel()
                .flatMap(BaseModel::getSelectedTextSource)
                .ifPresent(TextSource::selectPreviousGraphSpace));
    nextGraphSpaceButton.addActionListener(
        e ->
            view.getModel()
                .flatMap(BaseModel::getSelectedTextSource)
                .ifPresent(TextSource::selectNextGraphSpace));
    removeCellButton.addActionListener(
        e ->
            view.getModel()
                .ifPresent(
                    model ->
                        model
                            .getSelectedTextSource()
                            .ifPresent(
                                textSource ->
                                    textSource
                                        .getSelectedGraphSpace()
                                        .ifPresent(
                                            graphSpace -> {
                                              try {
                                                model.registerAction(
                                                    new GraphActions.RemoveCellsAction(
                                                        model, graphSpace));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    view, e1.getMessage());
                                              }
                                            }))));

    addAnnotationNodeButton.addActionListener(
        e ->
            view.getModel()
                .ifPresent(
                    model ->
                        model
                            .getSelectedGraphSpace()
                            .ifPresent(
                                graphSpace ->
                                    graphSpace
                                        .getTextSource()
                                        .getSelectedAnnotation()
                                        .ifPresent(
                                            conceptAnnotation -> {
                                              try {
                                                model.registerAction(
                                                    new GraphActions.AddAnnotationNodeAction(
                                                        view,
                                                        model,
                                                        graphSpace,
                                                        conceptAnnotation));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    view, e1.getMessage());
                                              }
                                            }))));

    applyLayoutButton.addActionListener(
        e ->
            view.getModel()
                .ifPresent(
                    model ->
                        model
                            .getSelectedTextSource()
                            .ifPresent(
                                textSource ->
                                    textSource
                                        .getSelectedGraphSpace()
                                        .ifPresent(
                                            graphSpace -> {
                                              try {
                                                model.registerAction(
                                                    new GraphActions.ApplyLayoutAction(
                                                        view, model, graphSpace));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    view, e1.getMessage());
                                              }
                                            }))));
    graphSpaceButtons =
        Arrays.asList(
            renameButton,
            removeCellButton,
            removeGraphSpaceButton,
            previousGraphSpaceButton,
            nextGraphSpaceButton,
            addAnnotationNodeButton,
            applyLayoutButton,
            zoomSlider,
            addGraphSpaceButton);
  }

  private void showGraph(GraphSpace graphSpace) {
    graphSpace.removeListener(addRelationListener);
    graphSpace.removeListener(moveCellsListener);
    graphSpace.removeListener(removeCellsListener);
    graphSpace.getSelectionModel().removeListener(changeSelectionListener);

    graphComponent.setGraph(graphSpace);
    graphComponent.setName(graphSpace.getId());

    graphSpace.addListener(mxEvent.ADD_CELLS, addRelationListener);
    graphSpace.addListener(mxEvent.MOVE_CELLS, moveCellsListener);
    graphSpace.addListener(mxEvent.REMOVE_CELLS, removeCellsListener);
    graphSpace.getSelectionModel().addListener(mxEvent.CHANGE, changeSelectionListener);

    reDrawGraph(graphSpace);
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
      view.getModel()
          .flatMap(BaseModel::getSelectedTextSource)
          .ifPresent(
              textSource -> {
                Optional<GraphSpace> graphSpaceOptional = textSource.getSelectedGraphSpace();
                if (graphSpaceOptional.isPresent()) {
                  graphSpaceOptional.ifPresent(this::showGraph);
                } else {
                  textSource.selectNextGraphSpace();
                  graphSpaceOptional = textSource.getSelectedGraphSpace();
                  if (graphSpaceOptional.isPresent()) {
                    graphSpaceOptional.ifPresent(this::showGraph);
                  } else {
                    makeGraph(textSource);
                  }
                }
              });
    }
  }

  private void makeGraph(TextSource textSource) {
    view.getModel()
        .ifPresent(
            model ->
                getGraphNameInput(view, textSource, null)
                    .ifPresent(
                        graphName -> {
                          try {
                            model.registerAction(
                                new GraphSpaceAction(model, ADD, graphName, textSource));
                          } catch (ActionUnperformable e) {
                            JOptionPane.showMessageDialog(view, e.getMessage());
                          }
                        }));
  }

  private static Optional<String> getGraphNameInput(
      KnowtatorView view, TextSource textSource, JTextField field1) {
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
                  if (textSource.containsID(finalField.getText())) {
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
    field1.setText(String.format("Graph Space %d", textSource.getNumberOfGraphSpaces()));
    int option =
        JOptionPane.showConfirmDialog(
            view, message, "Enter a name for this graph", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      if (textSource.containsID(field1.getText())) {
        JOptionPane.showMessageDialog(field1, "Graph name already in use");
        return getGraphNameInput(view, textSource, field1);
      } else {
        return Optional.of(field1.getText());
      }
    }

    return Optional.empty();
  }

  /**
   * Gets relation options dialog.
   *
   * @param propertyID the property id
   * @return the relation options dialog
   */
  RelationOptionsDialog getRelationOptionsDialog(String propertyID) {
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
    view.getModel().ifPresent(model -> model.addModelListener(this));
    graphSpaceChooser.reset();
  }

  @Override
  public void dispose() {
    graphSpaceChooser.dispose();
  }

  /**
   * Gets graph component.
   *
   * @return the graph component
   */
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
    rootPane = new JPanel();
    rootPane.setLayout(new BorderLayout(0, 0));
    rootPane.setAlignmentX(0.0f);
    rootPane.setAlignmentY(0.0f);
    rootPane.setMinimumSize(new Dimension(400, 400));
    header = new JPanel();
    header.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    rootPane.add(header, BorderLayout.NORTH);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    header.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel1.setBorder(BorderFactory.createTitledBorder(null, "Graph Spaces", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel1.getFont())));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    previousGraphSpaceButton = new JButton();
    previousGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousGraphSpaceButton.setText("");
    panel2.add(previousGraphSpaceButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    nextGraphSpaceButton = new JButton();
    nextGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextGraphSpaceButton.setText("");
    panel2.add(nextGraphSpaceButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    graphSpaceChooser.setMaximumSize(new Dimension(200, 32767));
    graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
    panel2.add(graphSpaceChooser, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    exportToImagePngButton = new JButton();
    exportToImagePngButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-unsplash-32.png")));
    exportToImagePngButton.setText("");
    panel3.add(exportToImagePngButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    zoomSlider = new JSlider();
    zoomSlider.setMaximum(100);
    panel3.add(zoomSlider, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(194, 16), null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    addGraphSpaceButton = new JButton();
    addGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addGraphSpaceButton.setText("");
    panel4.add(addGraphSpaceButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    removeGraphSpaceButton = new JButton();
    removeGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeGraphSpaceButton.setText("");
    panel4.add(removeGraphSpaceButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    renameButton = new JButton();
    renameButton.setText("Rename");
    panel4.add(renameButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    header.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel5.setBorder(BorderFactory.createTitledBorder(null, "Nodes and Edges", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel5.getFont())));
    addAnnotationNodeButton = new JButton();
    addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addAnnotationNodeButton.setText("");
    panel5.add(addAnnotationNodeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    removeCellButton = new JButton();
    removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeCellButton.setText("");
    panel5.add(removeCellButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    applyLayoutButton = new JButton();
    applyLayoutButton.setText("Apply Layout");
    panel5.add(applyLayoutButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    header.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    body = new JSplitPane();
    rootPane.add(body, BorderLayout.CENTER);
    graphPanel = new JPanel();
    graphPanel.setLayout(new BorderLayout(0, 0));
    graphPanel.setPreferredSize(new Dimension(500, 200));
    body.setLeftComponent(graphPanel);
    graphComponent.setCenterPage(false);
    graphComponent.setGridVisible(true);
    graphPanel.add(graphComponent, BorderLayout.CENTER);
    graphInfoPanel = new JPanel();
    graphInfoPanel.setLayout(new CardLayout(0, 0));
    body.setRightComponent(graphInfoPanel);
    nodeCard = new JPanel();
    nodeCard.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    graphInfoPanel.add(nodeCard, "Annotation Node Card");
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    nodeCard.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel6.setBorder(BorderFactory.createTitledBorder(null, "Node ID", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel6.getFont())));
    nodeIdLabel = new JLabel();
    Font nodeIdLabelFont = this.$$$getFont$$$("Verdana", -1, -1, nodeIdLabel.getFont());
    if (nodeIdLabelFont != null) {
      nodeIdLabel.setFont(nodeIdLabelFont);
    }
    nodeIdLabel.setText("");
    panel6.add(nodeIdLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    nodeCard.add(panel7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel7.setBorder(BorderFactory.createTitledBorder(null, "Concept Annotation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel7.getFont())));
    conceptAnnotationLabel = new JLabel();
    Font conceptAnnotationLabelFont = this.$$$getFont$$$("Verdana", -1, -1, conceptAnnotationLabel.getFont());
    if (conceptAnnotationLabelFont != null) {
      conceptAnnotationLabel.setFont(conceptAnnotationLabelFont);
    }
    conceptAnnotationLabel.setText("");
    panel7.add(conceptAnnotationLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    nodeCard.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    relationCard = new JPanel();
    relationCard.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
    graphInfoPanel.add(relationCard, "Relation Annotation Card");
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    relationCard.add(panel8, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel8.setBorder(BorderFactory.createTitledBorder(null, "Relation ID", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel8.getFont())));
    relationIdLabel = new JLabel();
    relationIdLabel.setEnabled(false);
    Font relationIdLabelFont = this.$$$getFont$$$("Verdana", -1, -1, relationIdLabel.getFont());
    if (relationIdLabelFont != null) {
      relationIdLabel.setFont(relationIdLabelFont);
    }
    relationIdLabel.setText("");
    panel8.add(relationIdLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    relationCard.add(spacer3, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    Font panel9Font = this.$$$getFont$$$("Verdana", -1, 16, panel9.getFont());
    if (panel9Font != null) {
      panel9.setFont(panel9Font);
    }
    relationCard.add(panel9, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel9.setBorder(BorderFactory.createTitledBorder(null, "OWL Object Property", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel9.getFont())));
    relationOwlObjectPropertyLabel = new JLabel();
    Font relationOwlObjectPropertyLabelFont = this.$$$getFont$$$("Verdana", -1, -1, relationOwlObjectPropertyLabel.getFont());
    if (relationOwlObjectPropertyLabelFont != null) {
      relationOwlObjectPropertyLabel.setFont(relationOwlObjectPropertyLabelFont);
    }
    relationOwlObjectPropertyLabel.setText("");
    panel9.add(relationOwlObjectPropertyLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    relationCard.add(panel10, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel10.setBorder(BorderFactory.createTitledBorder(null, "Quantifier", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel10.getFont())));
    quantifierChooser = new JComboBox();
    quantifierChooser.setEnabled(false);
    Font quantifierChooserFont = this.$$$getFont$$$("Verdana", -1, -1, quantifierChooser.getFont());
    if (quantifierChooserFont != null) {
      quantifierChooser.setFont(quantifierChooserFont);
    }
    final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
    defaultComboBoxModel1.addElement("some");
    defaultComboBoxModel1.addElement("only");
    defaultComboBoxModel1.addElement("min");
    defaultComboBoxModel1.addElement("max");
    defaultComboBoxModel1.addElement("exactly");
    quantifierChooser.setModel(defaultComboBoxModel1);
    panel10.add(quantifierChooser, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel11 = new JPanel();
    panel11.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    relationCard.add(panel11, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel11.setBorder(BorderFactory.createTitledBorder(null, "Quantifier Value", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel11.getFont())));
    quantifierValueTextField = new JFormattedTextField();
    Font quantifierValueTextFieldFont = this.$$$getFont$$$("Verdana", -1, -1, quantifierValueTextField.getFont());
    if (quantifierValueTextFieldFont != null) {
      quantifierValueTextField.setFont(quantifierValueTextFieldFont);
    }
    panel11.add(quantifierValueTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    final Spacer spacer4 = new Spacer();
    panel11.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    negatedCheckBox = new JCheckBox();
    Font negatedCheckBoxFont = this.$$$getFont$$$("Verdana", -1, 12, negatedCheckBox.getFont());
    if (negatedCheckBoxFont != null) {
      negatedCheckBox.setFont(negatedCheckBoxFont);
    }
    negatedCheckBox.setText("Negated");
    relationCard.add(negatedCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel12 = new JPanel();
    panel12.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    relationCard.add(panel12, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    panel12.setBorder(BorderFactory.createTitledBorder(null, "Notes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel12.getFont())));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel12.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    textArea1 = new JTextArea();
    Font textArea1Font = this.$$$getFont$$$("Verdana", -1, -1, textArea1.getFont());
    if (textArea1Font != null) {
      textArea1.setFont(textArea1Font);
    }
    scrollPane1.setViewportView(textArea1);
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) {
      return null;
    }
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return rootPane;
  }

  @Override
  public void filterChangedEvent() {
  }

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    view.getModel()
        .filter(model -> isVisible())
        .ifPresent(
            model -> {
              model
                  .getSelectedTextSource()
                  .ifPresent(
                      textSource -> {
                        if (textSource.getNumberOfGraphSpaces() == 0) {
                          graphSpaceButtons.forEach(c -> c.setEnabled(false));
                          addGraphSpaceButton.setEnabled(true);
                        } else {
                          graphSpaceButtons.forEach(c -> c.setEnabled(true));
                        }
                      });
              event
                  .getNew()
                  .filter(modelObject -> modelObject instanceof GraphSpace)
                  .map(modelObject -> (GraphSpace) modelObject)
                  .filter(graphSpace -> graphSpace != graphComponent.getGraph())
                  .ifPresent(GraphView.this::showGraph);
              event
                  .getNew()
                  .filter(modelObject -> modelObject instanceof TextSource)
                  .map(modelObject -> (TextSource) modelObject)
                  .ifPresent(
                      textSource ->
                          textSource.getSelectedGraphSpace().ifPresent(GraphView.this::showGraph));
              event
                  .getNew()
                  .filter(modelObject -> modelObject instanceof AnnotationNode)
                  .map(modelObject -> (AnnotationNode) modelObject)
                  .ifPresent(annotationNode -> this.showGraph(annotationNode.getGraphSpace()));
              event
                  .getNew()
                  .filter(modelObject -> modelObject instanceof RelationAnnotation)
                  .map(modelObject -> (RelationAnnotation) modelObject)
                  .ifPresent(
                      relationAnnotation -> this.showGraph(relationAnnotation.getGraphSpace()));
            });
    if (view.getIsOneClickGraphs()) {
      event
          .getNew()
          .filter(modelObject -> modelObject instanceof ConceptAnnotation)
          .map(modelObject -> (ConceptAnnotation) modelObject)
          .ifPresent(
              conceptAnnotation -> {
                if (conceptAnnotation
                    .getTextSource()
                    .getGraphSpaces()
                    .getSelection()
                    .map(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation))
                    .orElse(false)) {
                  conceptAnnotation
                      .getTextSource()
                      .getGraphSpaces()
                      .getSelection()
                      .ifPresent(
                          graphSpace -> {
                            dialog.setVisible(true);
                            showGraph(graphSpace);
                          });
                } else {
                  conceptAnnotation.getTextSource().getGraphSpaces().stream()
                      .filter(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation))
                      .findFirst()
                      .ifPresent(
                          graphSpace -> {
                            dialog.setVisible(true);
                            conceptAnnotation.getTextSource().setSelectedGraphSpace(graphSpace);
                          });
                }
              });
    }
  }

  /**
   * Re draw graph.
   *
   * @param graphSpace the graph space
   */
  void reDrawGraph(@Nonnull mxGraph graphSpace) {
    view.getModel()
        .filter(model -> view.isVisible())
        .filter(BaseModel::isNotLoading)
        .ifPresent(
            model -> {
              graphSpace.getModel().beginUpdate();
              try {
                Arrays.stream(graphSpace.getChildVertices(graphSpace.getDefaultParent()))
                    .forEach(
                        vertex -> {
                          if (vertex instanceof AnnotationNode) {
                            String colorString =
                                Integer.toHexString(
                                    ((AnnotationNode) vertex)
                                        .getConceptAnnotation()
                                        .getColor()
                                        .getRGB())
                                    .substring(2);
                            graphSpace.setCellStyles(
                                mxConstants.STYLE_FILLCOLOR, colorString, new Object[] {vertex});
                          }
                          graphSpace.updateCellSize(vertex);

                          graphSpace.getView().validateCell(vertex);
                        });
                Arrays.stream(graphSpace.getChildEdges(graphSpace.getDefaultParent()))
                    .forEach(
                        edge -> graphSpace.getView().validateCell(edge));
              } finally {
                graphSpace.getModel().endUpdate();
                graphSpace.refresh();
              }
            });
  }

  @Override
  public void colorChangedEvent() {
  }

  /**
   * Taken from https://tips4java.wordpress.com/2010/03/14/dialog-focus/
   */
  static class RequestFocusListener implements AncestorListener {
    private final boolean removeListener;

    /**
     * Instantiates a new Request focus listener.
     */
    /*
     *  Convenience constructor. The listener is only used once and then it is
     *  removed from the component.
     */
    RequestFocusListener() {
      this(true);
    }

    /**
     * Instantiates a new Request focus listener.
     *
     * @param removeListener the remove listener
     */
    /*
     *  Constructor that controls whether this listen can be used once or
     *  multiple times.
     *
     *  @param removeListener when true this listener is only invoked once
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

      if (removeListener) {
        component.removeAncestorListener(this);
      }
    }

    @Override
    public void ancestorRemoved(AncestorEvent e) {
    }

    @Override
    public void ancestorMoved(AncestorEvent e) {
    }
  }
}
