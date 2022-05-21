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

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.StructureModeListener;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.StyleContext;
import org.apache.log4j.Logger;

/**
 * The type Graph view.
 */
public class GraphView extends JPanel
    implements KnowtatorComponent, ModelListener, StructureModeListener {
  @SuppressWarnings("unused")
  private final Logger log = Logger.getLogger(KnowtatorView.class);

  private final JDialog dialog;
  private final KnowtatorView view;
  private final AddRelationListener addRelationListener;
  private JButton removeCellButton;
  private JButton addAnnotationNodeButton;
  private JButton applyLayoutButton;
  private JButton previousGraphSpaceButton;
  private JButton nextGraphSpaceButton;
  private mxGraphComponent graphComponent;
  private GraphSpaceChooser graphSpaceChooser;
  private JButton addGraphSpaceButton;
  private JButton removeGraphSpaceButton;
  private JSlider zoomSlider;
  private JButton renameButton;
  private JButton exportToImagePngButton;
  private JRadioButton horizontalRadioButton;
  private JPanel rootPane;
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
   */
  GraphView(KnowtatorView view, JDialog dialog) {

    this.view = view;
    this.dialog = dialog;
    $$$setupUI$$$();
    setVisible(false);
    // $$$setupUI$$$();
    makeButtons();

    addRelationListener = new AddRelationListener(this);
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
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .flatMap(GraphSpaceCollection::getOnlySelected)
                .ifPresent(
                    graphSpace -> {
                      JFileChooser fileChooser = new JFileChooser();
                      fileChooser.setCurrentDirectory(view.getModel().get().getSaveLocation());
                      FileFilter fileFilter = new FileNameExtensionFilter("PNG", "png");
                      fileChooser.setFileFilter(fileFilter);
                      fileChooser.setSelectedFile(
                          new File(
                              String.format(
                                  "%s_%s.png",
                                  graphSpace.getTextSource().getId(),
                                  graphSpace.getId())));
                      if (fileChooser.showSaveDialog(view)
                          == JFileChooser.APPROVE_OPTION) {
                        BufferedImage image =
                            mxCellRenderer.createBufferedImage(
                                graphSpace, null, 1, Color.WHITE, true, null);
                        try {
                          ImageIO.write(
                              image,
                              "PNG",
                              new File(
                                  fileChooser.getSelectedFile().getAbsolutePath()));
                        } catch (IOException e1) {
                          e1.printStackTrace();
                        }
                      }
                    }));

    zoomSlider.addChangeListener(e -> graphComponent.zoomTo(zoomSlider.getValue() / 50.0, false));
    renameButton.addActionListener(
        e ->
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .flatMap(GraphSpaceCollection::getOnlySelected)
                .ifPresent(
                    graphSpace ->
                        getGraphNameInput(view, graphSpace.getTextSource(), null)
                            .ifPresent(graphSpace::setId)));
    addGraphSpaceButton.addActionListener(
        e -> view.getModel()
            .map(BaseModel::getTextSources)
            .flatMap(TextSourceCollection::getOnlySelected)
            .ifPresent(this::makeGraph));
    removeGraphSpaceButton.addActionListener(
        e -> {
          if (JOptionPane.showConfirmDialog(view, "Are you sure you want to delete this graph?")
              == JOptionPane.YES_OPTION) {
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .ifPresent(
                    textSource -> {
                      try {
                        view.getModel().get().registerAction(
                            new GraphSpaceAction(view.getModel().get(), REMOVE, null, textSource));
                      } catch (ActionUnperformable e1) {
                        JOptionPane.showMessageDialog(view, e1.getMessage());
                      }
                    });
          }
        });
    previousGraphSpaceButton.addActionListener(
        e ->
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .ifPresent(GraphSpaceCollection::selectPrevious));
    nextGraphSpaceButton.addActionListener(
        e ->
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .ifPresent(GraphSpaceCollection::selectNext));
    removeCellButton.addActionListener(
        e ->
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .flatMap(GraphSpaceCollection::getOnlySelected)
                .ifPresent(
                    graphSpace -> {
                      try {
                        view.getModel().get().registerAction(
                            new GraphActions.RemoveCellsAction(view.getModel().get(), graphSpace));
                      } catch (ActionUnperformable e1) {
                        JOptionPane.showMessageDialog(view, e1.getMessage());
                      }
                    }));

    addAnnotationNodeButton.addActionListener(
        e ->
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .flatMap(GraphSpaceCollection::getOnlySelected)
                .ifPresent(
                    graphSpace ->
                        graphSpace
                            .getTextSource()
                            .getConceptAnnotations()
                            .getOnlySelected()
                            .ifPresent(
                                conceptAnnotation -> {
                                  try {
                                    view.getModel().get().registerAction(
                                        new GraphActions.AddAnnotationNodeAction(
                                            view,
                                            view.getModel().get(),
                                            graphSpace,
                                            conceptAnnotation));
                                  } catch (ActionUnperformable e1) {
                                    JOptionPane.showMessageDialog(
                                        view, e1.getMessage());
                                  }
                                })));

    applyLayoutButton.addActionListener(
        e ->
            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getGraphSpaces)
                .flatMap(GraphSpaceCollection::getOnlySelected)
                .ifPresent(
                    graphSpace -> {
                      try {
                        view.getModel().get().registerAction(
                            new GraphActions.ApplyLayoutAction(
                                view,
                                view.getModel().get(),
                                graphSpace,
                                horizontalRadioButton.isSelected()));
                      } catch (ActionUnperformable e1) {
                        JOptionPane.showMessageDialog(view, e1.getMessage());
                      }
                    }));

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
          .map(BaseModel::getTextSources)
          .flatMap(TextSourceCollection::getOnlySelected)
          .map(TextSource::getGraphSpaces)
          .flatMap(GraphSpaceCollection::getOnlySelected)
          .ifPresent(this::showGraph);
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
    field1.addAncestorListener(new RequestFocusListener());
    field1.setText(String.format("Graph Space %d", textSource.getGraphSpaces().size()));
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
    view.getModel().ifPresent(model -> model.addStructureModeListener(this));
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
                  .getTextSources()
                  .getOnlySelected()
                  .ifPresent(
                      textSource -> {
                        if (textSource.getGraphSpaces().size() == 0) {
                          graphSpaceButtons.forEach(c -> c.setEnabled(false));
                          addGraphSpaceButton.setEnabled(true);
                        } else {
                          graphSpaceButtons.forEach(c -> c.setEnabled(true));
                        }
                      });
              event
                  .getNew()
                  .forEach(modelObject -> {
                    if (modelObject instanceof GraphSpace) {
                      GraphSpace graphSpace = (GraphSpace) modelObject;
                      if (graphSpace != graphComponent.getGraph()) {
                        showGraph(graphSpace);
                      }
                    }
                    if (modelObject instanceof TextSource) {
                      modelObject.getKnowtatorModel().getTextSources().getOnlySelected()
                          .map(TextSource::getGraphSpaces)
                          .flatMap(GraphSpaceCollection::getOnlySelected)
                          .ifPresent(GraphView.this::showGraph);
                    }
                    if (modelObject instanceof AnnotationNode) {
                      showGraph(((AnnotationNode) modelObject).getGraphSpace());
                    }
                    if (modelObject instanceof RelationAnnotation) {
                      showGraph(((RelationAnnotation) modelObject).getGraphSpace());
                    }
                  });
            });
    if (view.getIsOneClickGraphs()) {
      event
          .getNew()
          .forEach(modelObject -> {
            if (modelObject instanceof ConceptAnnotation) {
              ConceptAnnotation conceptAnnotation = (ConceptAnnotation) modelObject;
              if (conceptAnnotation
                  .getTextSource()
                  .getGraphSpaces()
                  .getOnlySelected()
                  .map(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation))
                  .orElse(false)) {
                conceptAnnotation
                    .getTextSource()
                    .getGraphSpaces()
                    .getOnlySelected()
                    .ifPresent(
                        graphSpace -> {
                          dialog.setVisible(true);
                          showGraph(graphSpace);
                        });
              } else {
                conceptAnnotation.getTextSource().getGraphSpaces().getOnlySelected()
                    .filter(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation))
                    .ifPresent(
                        graphSpace -> {
                          dialog.setVisible(true);
                          conceptAnnotation.getTextSource().getGraphSpaces().selectOnly(graphSpace);
                        });
              }
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
                    .forEach(edge -> graphSpace.getView().validateCell(edge));
              } finally {
                graphSpace.getModel().endUpdate();
                graphSpace.refresh();
              }
            });
  }

  @Override
  public void colorChangedEvent(Profile profile) {
  }

  @Override
  public void structureModeChanged() {
    view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .map(TextSource::getGraphSpaces)
        .flatMap(GraphSpaceCollection::getOnlySelected)
        .ifPresent(this::showGraph);
  }

  public KnowtatorView getView() {
    return view;
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
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new BorderLayout(0, 0));
    rootPane.add(panel1, BorderLayout.NORTH);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    panel1.add(panel2, BorderLayout.WEST);
    panel2.setBorder(BorderFactory.createTitledBorder(null, "Nodes and Edges", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel2.getFont()), null));
    addAnnotationNodeButton = new JButton();
    addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addAnnotationNodeButton.setText("");
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    panel2.add(addAnnotationNodeButton, gbc);
    removeCellButton = new JButton();
    removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeCellButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    panel2.add(removeCellButton, gbc);
    applyLayoutButton = new JButton();
    applyLayoutButton.setText("Apply Layout");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    panel2.add(applyLayoutButton, gbc);
    horizontalRadioButton = new JRadioButton();
    horizontalRadioButton.setText("Horizontal");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel2.add(horizontalRadioButton, gbc);
    final JRadioButton radioButton1 = new JRadioButton();
    radioButton1.setSelected(true);
    radioButton1.setText("Vertical");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel2.add(radioButton1, gbc);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridBagLayout());
    panel1.add(panel3, BorderLayout.EAST);
    panel3.setBorder(BorderFactory.createTitledBorder(null, "Graph Spaces", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel3.getFont()), null));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(panel4, gbc);
    previousGraphSpaceButton = new JButton();
    previousGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousGraphSpaceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel4.add(previousGraphSpaceButton, gbc);
    nextGraphSpaceButton = new JButton();
    nextGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextGraphSpaceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel4.add(nextGraphSpaceButton, gbc);
    graphSpaceChooser.setMaximumSize(new Dimension(200, 32767));
    graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel4.add(graphSpaceChooser, gbc);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(panel5, gbc);
    exportToImagePngButton = new JButton();
    exportToImagePngButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-unsplash-32.png")));
    exportToImagePngButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel5.add(exportToImagePngButton, gbc);
    zoomSlider = new JSlider();
    zoomSlider.setMaximum(100);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel5.add(zoomSlider, gbc);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(panel6, gbc);
    addGraphSpaceButton = new JButton();
    addGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addGraphSpaceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel6.add(addGraphSpaceButton, gbc);
    removeGraphSpaceButton = new JButton();
    removeGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeGraphSpaceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel6.add(removeGraphSpaceButton, gbc);
    renameButton = new JButton();
    renameButton.setText("Rename");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel6.add(renameButton, gbc);
    final JSplitPane splitPane1 = new JSplitPane();
    rootPane.add(splitPane1, BorderLayout.CENTER);
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new BorderLayout(0, 0));
    panel7.setPreferredSize(new Dimension(500, 200));
    splitPane1.setLeftComponent(panel7);
    graphComponent.setCenterPage(false);
    graphComponent.setGridVisible(true);
    panel7.add(graphComponent, BorderLayout.CENTER);
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new CardLayout(0, 0));
    splitPane1.setRightComponent(panel8);
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new GridBagLayout());
    panel8.add(panel9, "Annotation Node Card");
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel9.add(panel10, gbc);
    panel10.setBorder(BorderFactory.createTitledBorder(null, "Node ID", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel10.getFont()), null));
    final JLabel label1 = new JLabel();
    Font label1Font = this.$$$getFont$$$("Verdana", -1, -1, label1.getFont());
    if (label1Font != null) {
      label1.setFont(label1Font);
    }
    label1.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel10.add(label1, gbc);
    final JPanel panel11 = new JPanel();
    panel11.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel9.add(panel11, gbc);
    panel11.setBorder(BorderFactory.createTitledBorder(null, "Concept Annotation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel11.getFont()), null));
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$("Verdana", -1, -1, label2.getFont());
    if (label2Font != null) {
      label2.setFont(label2Font);
    }
    label2.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel11.add(label2, gbc);
    final JPanel panel12 = new JPanel();
    panel12.setLayout(new GridBagLayout());
    panel8.add(panel12, "Relation Annotation Card");
    final JPanel panel13 = new JPanel();
    panel13.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(panel13, gbc);
    panel13.setBorder(BorderFactory.createTitledBorder(null, "Relation ID", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel13.getFont()), null));
    final JLabel label3 = new JLabel();
    label3.setEnabled(false);
    Font label3Font = this.$$$getFont$$$("Verdana", -1, -1, label3.getFont());
    if (label3Font != null) {
      label3.setFont(label3Font);
    }
    label3.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel13.add(label3, gbc);
    final JPanel panel14 = new JPanel();
    panel14.setLayout(new GridBagLayout());
    Font panel14Font = this.$$$getFont$$$("Verdana", -1, 16, panel14.getFont());
    if (panel14Font != null) {
      panel14.setFont(panel14Font);
    }
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(panel14, gbc);
    panel14.setBorder(BorderFactory.createTitledBorder(null, "OWL Object Property", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel14.getFont()), null));
    final JLabel label4 = new JLabel();
    Font label4Font = this.$$$getFont$$$("Verdana", -1, -1, label4.getFont());
    if (label4Font != null) {
      label4.setFont(label4Font);
    }
    label4.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel14.add(label4, gbc);
    final JPanel panel15 = new JPanel();
    panel15.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(panel15, gbc);
    panel15.setBorder(BorderFactory.createTitledBorder(null, "Quantifier", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel15.getFont()), null));
    final JComboBox comboBox1 = new JComboBox();
    comboBox1.setEnabled(false);
    Font comboBox1Font = this.$$$getFont$$$("Verdana", -1, -1, comboBox1.getFont());
    if (comboBox1Font != null) {
      comboBox1.setFont(comboBox1Font);
    }
    final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
    defaultComboBoxModel1.addElement("some");
    defaultComboBoxModel1.addElement("only");
    defaultComboBoxModel1.addElement("min");
    defaultComboBoxModel1.addElement("max");
    defaultComboBoxModel1.addElement("exactly");
    comboBox1.setModel(defaultComboBoxModel1);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel15.add(comboBox1, gbc);
    final JPanel panel16 = new JPanel();
    panel16.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(panel16, gbc);
    panel16.setBorder(BorderFactory.createTitledBorder(null, "Quantifier Value", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel16.getFont()), null));
    final JFormattedTextField formattedTextField1 = new JFormattedTextField();
    Font formattedTextField1Font = this.$$$getFont$$$("Verdana", -1, -1, formattedTextField1.getFont());
    if (formattedTextField1Font != null) {
      formattedTextField1.setFont(formattedTextField1Font);
    }
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel16.add(formattedTextField1, gbc);
    final JCheckBox checkBox1 = new JCheckBox();
    Font checkBox1Font = this.$$$getFont$$$("Verdana", -1, 12, checkBox1.getFont());
    if (checkBox1Font != null) {
      checkBox1.setFont(checkBox1Font);
    }
    checkBox1.setText("Negated");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel12.add(checkBox1, gbc);
    final JPanel panel17 = new JPanel();
    panel17.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(panel17, gbc);
    panel17.setBorder(BorderFactory.createTitledBorder(null, "Notes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, panel17.getFont()), null));
    final JScrollPane scrollPane1 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel17.add(scrollPane1, gbc);
    final JTextArea textArea1 = new JTextArea();
    Font textArea1Font = this.$$$getFont$$$("Verdana", -1, -1, textArea1.getFont());
    if (textArea1Font != null) {
      textArea1.setFont(textArea1Font);
    }
    scrollPane1.setViewportView(textArea1);
    ButtonGroup buttonGroup;
    buttonGroup = new ButtonGroup();
    buttonGroup.add(horizontalRadioButton);
    buttonGroup.add(radioButton1);
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
    Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return rootPane;
  }

  /**
   * Taken from https://tips4java.wordpress.com/2010/03/14/dialog-focus/
   */
  static class RequestFocusListener implements AncestorListener {
    private final boolean removeListener;

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
