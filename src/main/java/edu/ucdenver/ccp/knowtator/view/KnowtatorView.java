package edu.ucdenver.ccp.knowtator.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;
import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.view.chooser.ProfileChooser;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.menu.ProjectMenu;
import edu.ucdenver.ccp.knowtator.view.textpane.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.view.textpane.MainKnowtatorTextPane;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.Set;

public class KnowtatorView extends AbstractOWLClassViewComponent
        implements DropTargetListener, SelectionListener {

  private KnowtatorController controller;
  private GraphViewDialog graphViewDialog;
  private JMenu projectMenu;
  private JComponent panel1;
  private JButton previousMatchButton;
  private JButton nextMatchButton;
  private JTextField matchTextField;
  private JCheckBox caseSensitiveCheckBox;
  private JCheckBox regexCheckBox;
  private JButton showGraphViewerButton;
  private JButton removeAnnotationButton;
  private JButton growSelectionStartButton;
  private JButton shrinkSelectionEndButton;
  private JButton growSelectionEndButton;
  private JButton shrinkSelectionStartButton;
  private JButton addAnnotationButton;
  private JButton decreaseFontSizeButton;
  private JButton increaseFontSizeButton;
  private JButton previousTextSourceButton;
  private JButton nextTextSourceButton;
  private JButton nextSpanButton;
  private JButton previousSpanButton;
  private JButton assignColorToClassButton;
  private JCheckBox profileFilterCheckBox;
  private KnowtatorTextPane knowtatorTextPane;
  private JToolBar textSourceToolBar;
  private JToolBar annotationToolBar;
  private JPanel findPanel;
  private JPanel textPanel;
  private JSplitPane infoPane;
  private JSplitPane mainPane;
  private JPanel infoPanel;
  private JLabel infoPanelTitleLabel;
  private SpanList spanList;
  private AnnotatorLabel annotatorLabel;
  private AnnotationIDLabel annotationIDLabel;
  private AnnotationClassLabel annotationClassLabel;
  private TextSourceChooser textSourceChooser;
  private ProfileChooser profileChooser;
  private JButton textToGraphButton;

  public KnowtatorView() {
    makeController();
    $$$setupUI$$$();
    makeButtons();
  }

  private void makeController() {
    controller = new KnowtatorController();

    if (getOWLWorkspace() != null) {
      controller.getOWLAPIDataExtractor().setUpOWL(getOWLWorkspace());
      getOWLWorkspace()
              .getOWLModelManager()
              .addOntologyChangeListener(controller.getTextSourceManager());
    }
  }

  public KnowtatorController getController() {
    return controller;
  }

  @Override
  public void initialiseClassView() {
  }

  private void createUIComponents() {
    DropTarget dt = new DropTarget(this, this);
    dt.setActive(true);

    panel1 = this;
    projectMenu = new ProjectMenu(this);
    knowtatorTextPane = new MainKnowtatorTextPane(this);
    graphViewDialog = new GraphViewDialog(this);
    annotationIDLabel = new AnnotationIDLabel(this);
    annotationClassLabel = new AnnotationClassLabel(this);
    annotatorLabel = new AnnotatorLabel(this);
    spanList = new SpanList(this);
    textSourceChooser = new TextSourceChooser(this);
    profileChooser = new ProfileChooser(this);
  }

  private void makeButtons() {

    assignColorToClassButton.addActionListener(
            e -> {
              Object owlClass = controller.getSelectionManager().getSelectedOWLClass();
              if (owlClass == null) {
                if (controller.getProjectManager().isProjectLoaded()) {
                  owlClass = controller.getSelectionManager().getSelectedAnnotation().getOwlClassID();
                }
              }
              if (owlClass != null) {
                Color c = JColorChooser.showDialog(this, "Pick a color for " + owlClass, Color.CYAN);
                if (c != null) {
                  controller.getSelectionManager().getActiveProfile().addColor(owlClass, c);

                  if (owlClass instanceof OWLClass) {
                    if (JOptionPane.showConfirmDialog(
                            this, "Assign color to descendants of " + owlClass + "?")
                            == JOptionPane.OK_OPTION) {
                      try {
                        Set<OWLClass> descendants =
                                controller.getOWLAPIDataExtractor().getDescendants((OWLClass) owlClass);

                        for (OWLClass descendant : descendants) {
                          controller.getSelectionManager().getActiveProfile().addColor(descendant, c);
                        }
                      } catch (OWLWorkSpaceNotSetException ignored) {
                      }
                    }
                  }
                }
              }
            });

    profileFilterCheckBox.addChangeListener(controller.getSelectionManager());

    growSelectionStartButton.addActionListener(
            (ActionEvent e) -> {
              if (controller.getSelectionManager().getSelectedSpan() == null) {
                knowtatorTextPane.growStart();
              } else {
                controller
                        .getSelectionManager()
                        .getActiveTextSource()
                        .getAnnotationManager()
                        .growSelectedSpanStart();
              }
            });
    shrinkSelectionStartButton.addActionListener(
            (ActionEvent e) -> {
              if (controller.getSelectionManager().getSelectedSpan() == null) {
                knowtatorTextPane.shrinkStart();
              } else {
                controller
                        .getSelectionManager()
                        .getActiveTextSource()
                        .getAnnotationManager()
                        .shrinkSelectedSpanStart();
              }
            });
    shrinkSelectionEndButton.addActionListener(
            (ActionEvent e) -> {
              if (controller.getSelectionManager().getSelectedSpan() == null) {
                knowtatorTextPane.shrinkEnd();
              } else {
                controller
                        .getSelectionManager()
                        .getActiveTextSource()
                        .getAnnotationManager()
                        .shrinkSelectedSpanEnd();
              }
            });
    growSelectionEndButton.addActionListener(
            (ActionEvent e) -> {
              if (controller.getSelectionManager().getSelectedSpan() == null) {
                knowtatorTextPane.growEnd();
              } else {
                controller
                        .getSelectionManager()
                        .getActiveTextSource()
                        .getAnnotationManager()
                        .growSelectedSpanEnd();
              }
            });

    previousSpanButton.addActionListener(
            (ActionEvent e) -> controller.getSelectionManager().getPreviousSpan());
    nextSpanButton.addActionListener(
            (ActionEvent e) -> controller.getSelectionManager().getNextSpan());

    showGraphViewerButton.addActionListener(e -> graphViewDialog.setVisible(true));

    removeAnnotationButton.addActionListener(
            e -> {
              if (controller.getProjectManager().isProjectLoaded()
                      && JOptionPane.showConfirmDialog(
                      this,
                      "Are you sure you want to remove the selected annotation?",
                      "Remove Annotation",
                      JOptionPane.YES_NO_OPTION)
                      == JOptionPane.YES_OPTION) {
                controller
                        .getSelectionManager()
                        .getActiveTextSource()
                        .getAnnotationManager()
                        .removeSelectedAnnotation();
              }
            });
    addAnnotationButton.addActionListener(
            e -> {
              if (controller.getProjectManager().isProjectLoaded()) {
                controller
                        .getSelectionManager()
                        .getActiveTextSource()
                        .getAnnotationManager()
                        .addSelectedAnnotation();
              }
            });

    previousTextSourceButton.addActionListener(
            e -> controller.getSelectionManager().getPreviousTextSource());
    nextTextSourceButton.addActionListener(
            e -> controller.getSelectionManager().getNextTextSource());

    decreaseFontSizeButton.addActionListener(
            (ActionEvent e) -> knowtatorTextPane.decreaseFontSize());
    increaseFontSizeButton.addActionListener(
            (ActionEvent e) -> knowtatorTextPane.increaseFindSize());
    textSourceChooser.addActionListener(
            e -> {
              JComboBox comboBox = (JComboBox) e.getSource();
              if (comboBox.getSelectedItem() != null) {
                controller.getSelectionManager().setSelected((TextSource) comboBox.getSelectedItem());
              }
            });
    profileChooser.addActionListener(
            e -> {
              JComboBox comboBox = (JComboBox) e.getSource();
              if (comboBox.getSelectedItem() != null) {
                controller.getSelectionManager().setSelected((Profile) comboBox.getSelectedItem());
              }
            });
    spanList.addListSelectionListener(
            e -> {
              JList jList = (JList) e.getSource();
              if (jList.getSelectedValue() != null) {
                controller.getSelectionManager().setSelected((Span) jList.getSelectedValue());
              }
            });
    nextMatchButton.addActionListener(
            e -> {
              String textToFind = matchTextField.getText();
              KnowtatorTextPane currentKnowtatorTextPane = getKnowtatorTextPane();
              String textToSearch = currentKnowtatorTextPane.getText();
              if (!caseSensitiveCheckBox.isSelected()) {
                textToSearch = textToSearch.toLowerCase();
              }
              int matchLoc =
                      textToSearch.indexOf(textToFind, currentKnowtatorTextPane.getSelectionStart() + 1);
              if (matchLoc != -1) {
                currentKnowtatorTextPane.requestFocusInWindow();
                currentKnowtatorTextPane.select(matchLoc, matchLoc + textToFind.length());
              } else {
                currentKnowtatorTextPane.setSelectionStart(textToSearch.length());
              }
            });
    previousMatchButton.addActionListener(
            e -> {
              String textToFind = matchTextField.getText();
              String textToSearch = knowtatorTextPane.getText();
              if (!caseSensitiveCheckBox.isSelected()) {
                textToSearch = textToSearch.toLowerCase();
              }
              int matchLoc =
                      textToSearch.lastIndexOf(textToFind, knowtatorTextPane.getSelectionStart() - 1);
              if (matchLoc != -1) {
                knowtatorTextPane.requestFocusInWindow();
                knowtatorTextPane.select(matchLoc, matchLoc + textToFind.length());
              } else {
                knowtatorTextPane.setSelectionStart(-1);
              }
            });

    textToGraphButton.addActionListener(
            e -> {
              if (controller.getProjectManager().isProjectLoaded()) {
                controller
                        .getSelectionManager()
                        .getActiveGraphSpace()
                        .setGraphText(
                                knowtatorTextPane.getSelectionStart(), knowtatorTextPane.getSelectionEnd());
              }
            });

    controller.getSelectionManager().addListener(annotationIDLabel);
    controller.getSelectionManager().addListener(annotationClassLabel);
    controller.getSelectionManager().addListener(annotatorLabel);
  }

  private void owlEntitySelectionChanged(OWLEntity owlEntity) {
    if (getView() != null) {
      if (getView().isSyncronizing()) {
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(owlEntity);
      }
    }
  }

  @Override
  protected OWLClass updateView(OWLClass selectedClass) {
    controller.getSelectionManager().setSelectedOWLClass(selectedClass);
    return selectedClass;
  }

  @Override
  public void disposeView() {
    if (controller.getProjectManager().isProjectLoaded()
            && JOptionPane.showConfirmDialog(
            this,
            "Save changes to Knowtator project?",
            "Save Project",
            JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) {
      controller.getProjectManager().saveProject();
    }
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent e) {
  }

  @Override
  public void dragExit(DropTargetEvent e) {
  }

  @Override
  public void drop(DropTargetDropEvent e) {
  }

  public KnowtatorTextPane getKnowtatorTextPane() {
    return knowtatorTextPane;
  }

  @Override
  public void dragEnter(DropTargetDragEvent e) {
  }

  @Override
  public void dragOver(DropTargetDragEvent e) {
  }

  public JMenu getProjectMenu() {
    return projectMenu;
  }

  public GraphViewDialog getGraphViewDialog() {
    return graphViewDialog;
  }

  @Override
  public void selectedAnnotationChanged(AnnotationChangeEvent e) {
    owlEntitySelectionChanged(e.getNew().getOwlClass());
  }

  @Override
  public void selectedSpanChanged(SpanChangeEvent e) {
  }

  @Override
  public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
  }

  @Override
  public void activeTextSourceChanged(TextSourceChangeEvent e) {
  }

  @Override
  public void activeProfileChange(ProfileChangeEvent e) {
  }

  @Override
  public void owlPropertyChangedEvent(OWLObjectProperty value) {
    owlEntitySelectionChanged(value);
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
    panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    mainPane = new JSplitPane();
    mainPane.setDividerLocation(1536);
    mainPane.setOneTouchExpandable(true);
    panel1.add(mainPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
    infoPane = new JSplitPane();
    infoPane.setOrientation(0);
    mainPane.setRightComponent(infoPane);
    findPanel = new JPanel();
    findPanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
    infoPane.setLeftComponent(findPanel);
    findPanel.setBorder(BorderFactory.createTitledBorder(ResourceBundle.getBundle("ui").getString("find")));
    nextMatchButton = new JButton();
    this.$$$loadButtonText$$$(nextMatchButton, ResourceBundle.getBundle("ui").getString("next"));
    findPanel.add(nextMatchButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    previousMatchButton = new JButton();
    this.$$$loadButtonText$$$(previousMatchButton, ResourceBundle.getBundle("ui").getString("previous"));
    findPanel.add(previousMatchButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    matchTextField = new JTextField();
    findPanel.add(matchTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    caseSensitiveCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(caseSensitiveCheckBox, ResourceBundle.getBundle("ui").getString("case.sensitive"));
    findPanel.add(caseSensitiveCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    regexCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(regexCheckBox, ResourceBundle.getBundle("ui").getString("regex"));
    findPanel.add(regexCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    infoPanel = new JPanel();
    infoPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
    infoPane.setRightComponent(infoPanel);
    infoPanelTitleLabel = new JLabel();
    Font infoPanelTitleLabelFont = this.$$$getFont$$$(null, Font.BOLD, 18, infoPanelTitleLabel.getFont());
    if (infoPanelTitleLabelFont != null) infoPanelTitleLabel.setFont(infoPanelTitleLabelFont);
    infoPanelTitleLabel.setHorizontalAlignment(0);
    infoPanelTitleLabel.setHorizontalTextPosition(0);
    this.$$$loadLabelText$$$(infoPanelTitleLabel, ResourceBundle.getBundle("ui").getString("annotation.information"));
    infoPanelTitleLabel.setVerticalAlignment(0);
    infoPanelTitleLabel.setVerticalTextPosition(0);
    infoPanel.add(infoPanelTitleLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    infoPanel.add(scrollPane1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    scrollPane1.setViewportView(spanList);
    annotationIDLabel.setHorizontalAlignment(2);
    annotationIDLabel.setHorizontalTextPosition(2);
    annotationIDLabel.setVerticalAlignment(1);
    annotationIDLabel.setVerticalTextPosition(1);
    infoPanel.add(annotationIDLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    annotationClassLabel.setHorizontalAlignment(2);
    annotationClassLabel.setHorizontalTextPosition(2);
    annotationClassLabel.setVerticalAlignment(1);
    annotationClassLabel.setVerticalTextPosition(1);
    infoPanel.add(annotationClassLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    annotatorLabel.setHorizontalAlignment(2);
    annotatorLabel.setHorizontalTextPosition(2);
    annotatorLabel.setVerticalAlignment(1);
    annotatorLabel.setVerticalTextPosition(1);
    infoPanel.add(annotatorLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    textPanel = new JPanel();
    textPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
    mainPane.setLeftComponent(textPanel);
    textPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null));
    textSourceToolBar = new JToolBar();
    textSourceToolBar.setFloatable(false);
    textPanel.add(textSourceToolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
    decreaseFontSizeButton = new JButton();
    decreaseFontSizeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-Decrease Font (Custom).png")));
    decreaseFontSizeButton.setText("");
    decreaseFontSizeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("decrease.font.size"));
    textSourceToolBar.add(decreaseFontSizeButton);
    increaseFontSizeButton = new JButton();
    increaseFontSizeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-Increase Font (Custom).png")));
    increaseFontSizeButton.setText("");
    increaseFontSizeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("increase.font.size"));
    textSourceToolBar.add(increaseFontSizeButton);
    previousTextSourceButton = new JButton();
    previousTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-up-filled-50 (Custom).png")));
    previousTextSourceButton.setText("");
    previousTextSourceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.text.source"));
    textSourceToolBar.add(previousTextSourceButton);
    nextTextSourceButton = new JButton();
    nextTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-down-filled-50 (Custom).png")));
    nextTextSourceButton.setText("");
    nextTextSourceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.text.source"));
    textSourceToolBar.add(nextTextSourceButton);
    final JLabel label1 = new JLabel();
    this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("ui").getString("document"));
    textSourceToolBar.add(label1);
    textSourceToolBar.add(textSourceChooser);
    final JLabel label2 = new JLabel();
    this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("ui").getString("profile"));
    textSourceToolBar.add(label2);
    textSourceToolBar.add(profileChooser);
    annotationToolBar = new JToolBar();
    annotationToolBar.setFloatable(false);
    textPanel.add(annotationToolBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
    showGraphViewerButton = new JButton();
    showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-edit-node-50 (Custom).png")));
    showGraphViewerButton.setText("");
    showGraphViewerButton.setToolTipText(ResourceBundle.getBundle("ui").getString("show.graph.viewer"));
    annotationToolBar.add(showGraphViewerButton);
    textToGraphButton = new JButton();
    this.$$$loadButtonText$$$(textToGraphButton, ResourceBundle.getBundle("ui").getString("text.to.graph"));
    textToGraphButton.setToolTipText(ResourceBundle.getBundle("ui").getString("send.selected.text.to.graph"));
    annotationToolBar.add(textToGraphButton);
    addAnnotationButton = new JButton();
    addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-50 (Custom).png")));
    addAnnotationButton.setText("");
    addAnnotationButton.setToolTipText(ResourceBundle.getBundle("ui").getString("add.annotation"));
    annotationToolBar.add(addAnnotationButton);
    removeAnnotationButton = new JButton();
    removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-minus-50 (Custom).png")));
    removeAnnotationButton.setText("");
    removeAnnotationButton.setToolTipText(ResourceBundle.getBundle("ui").getString("remove.annotation"));
    annotationToolBar.add(removeAnnotationButton);
    previousSpanButton = new JButton();
    previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-left-filled-50 (Custom).png")));
    previousSpanButton.setText("");
    previousSpanButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.span"));
    annotationToolBar.add(previousSpanButton);
    growSelectionStartButton = new JButton();
    growSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-back-50 (Custom).png")));
    growSelectionStartButton.setText("");
    growSelectionStartButton.setToolTipText(ResourceBundle.getBundle("ui").getString("grow.selection.start"));
    annotationToolBar.add(growSelectionStartButton);
    shrinkSelectionStartButton = new JButton();
    shrinkSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-forward-50 (Custom).png")));
    shrinkSelectionStartButton.setText("");
    shrinkSelectionStartButton.setToolTipText(ResourceBundle.getBundle("ui").getString("shrink.selection.start"));
    annotationToolBar.add(shrinkSelectionStartButton);
    shrinkSelectionEndButton = new JButton();
    shrinkSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-back-50 (Custom).png")));
    shrinkSelectionEndButton.setText("");
    shrinkSelectionEndButton.setToolTipText(ResourceBundle.getBundle("ui").getString("shrink.selection.end"));
    annotationToolBar.add(shrinkSelectionEndButton);
    growSelectionEndButton = new JButton();
    growSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-forward-50 (Custom).png")));
    growSelectionEndButton.setText("");
    growSelectionEndButton.setToolTipText(ResourceBundle.getBundle("ui").getString("grow.selection.end"));
    annotationToolBar.add(growSelectionEndButton);
    nextSpanButton = new JButton();
    nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-right-filled-50 (Custom).png")));
    nextSpanButton.setText("");
    nextSpanButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.span"));
    annotationToolBar.add(nextSpanButton);
    assignColorToClassButton = new JButton();
    assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
    assignColorToClassButton.setText("");
    assignColorToClassButton.setToolTipText(ResourceBundle.getBundle("ui").getString("assign.color.to.class"));
    annotationToolBar.add(assignColorToClassButton);
    profileFilterCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(profileFilterCheckBox, ResourceBundle.getBundle("ui").getString("profile.filter"));
    profileFilterCheckBox.setToolTipText(ResourceBundle.getBundle("ui").getString("filter.annotations.by.profile"));
    annotationToolBar.add(profileFilterCheckBox);
    final JScrollPane scrollPane2 = new JScrollPane();
    textPanel.add(scrollPane2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    knowtatorTextPane.setBackground(new Color(-1));
    knowtatorTextPane.setEditable(false);
    knowtatorTextPane.setFocusTraversalPolicyProvider(true);
    knowtatorTextPane.setFocusable(false);
    knowtatorTextPane.setForeground(new Color(-16777216));
    knowtatorTextPane.setText("");
    scrollPane2.setViewportView(knowtatorTextPane);
    final JMenuBar menuBar1 = new JMenuBar();
    menuBar1.setLayout(new GridBagLayout());
    panel1.add(menuBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    projectMenu.setSelected(false);
    projectMenu.setText("Knowator Project");
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    menuBar1.add(projectMenu, gbc);
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
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
