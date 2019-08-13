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

package edu.ucdenver.ccp.knowtator.view;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction.pickAction;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.ANNOTATION;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.DOCUMENT;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.PROFILE;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.SPAN;
import static edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction.assignColorToClass;

import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.iaa.IaaException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIaa;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.FilterType;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.OwlModel;
import edu.ucdenver.ccp.knowtator.model.collection.SelectableCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import edu.ucdenver.ccp.knowtator.view.actions.collection.ActionParameters;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.FilterAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.SpanActions;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationAnnotatorLabel;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationClassLabel;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationIdLabel;
import edu.ucdenver.ccp.knowtator.view.list.ColorList;
import edu.ucdenver.ccp.knowtator.view.list.GraphSpaceList;
import edu.ucdenver.ccp.knowtator.view.list.ProfileList;
import edu.ucdenver.ccp.knowtator.view.list.SpanList;
import edu.ucdenver.ccp.knowtator.view.table.AnnotationTable;
import edu.ucdenver.ccp.knowtator.view.table.AnnotationTableForOwlClass;
import edu.ucdenver.ccp.knowtator.view.table.AnnotationTableForSpannedText;
import edu.ucdenver.ccp.knowtator.view.table.KnowtatorTable;
import edu.ucdenver.ccp.knowtator.view.table.RelationTable;
import edu.ucdenver.ccp.knowtator.view.textpane.KnowtatorTextPane;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.undo.UndoManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * Main class for GUI.
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorView extends AbstractOWLClassViewComponent
    implements DropTargetListener, KnowtatorComponent, ModelListener {

  private static final Logger log = Logger.getLogger(KnowtatorView.class);
  /**
   * The constant PREFERENCES.
   */
  public static final Preferences PREFERENCES = Preferences.userRoot().node("knowtator");

  private KnowtatorModel model;
  private GraphViewDialog graphViewDialog;
  private JComponent cardPanel;

  private KnowtatorTextPane textPane;
  private JTabbedPane header;
  private GraphSpaceList graphSpaceList;
  private AnnotationIdLabel annotationIdLabel;
  private AnnotationAnnotatorLabel annotationAnnotatorLabel;
  private AnnotationClassLabel annotationClassLabel;
  private AnnotationNotes annotationNotes;
  private SpanList spanList;
  private JButton shrinkEndButton;
  private JButton growEndButton;
  private JButton growStartButton;
  private JButton shrinkStartButton;
  private JButton previousSpanButton;
  private JButton nextSpanButton;
  private JButton addAnnotationButton;
  private JButton removeAnnotationButton;
  private JCheckBox profileFilterCheckBox;
  private JCheckBox owlClassFilterCheckBox;
  private JButton showGraphViewerButton;
  private JButton previousTextSourceButton;
  private JButton nextTextSourceButton;
  private JButton addTextSourceButton;
  private JButton removeTextSourceButton;
  private TextSourceChooser textSourceChooser;
  private JSlider fontSizeSlider;
  private JButton undoButton;
  private JButton redoButton;
  private JButton assignColorToClassButton;
  private JProgressBar progressBar;
  private JList<String> fileList;
  private JButton removeProfileButton;
  private ProfileList profileList;
  private JButton addProfileButton;
  private ColorList colorList;
  private JTextField annotationsContainingTextTextField;
  private JCheckBox exactMatchCheckBox;
  private JLabel owlClassLabel;
  private AnnotationTable annotationsForClassTable;
  private JCheckBox includeClassDescendantsCheckBox;
  private JLabel owlPropertyLabel;
  private RelationTable relationsForPropertyList;
  private JButton previousTextReviewButton;
  private JButton nextTextReviewButton;
  private JCheckBox includePropertyDescendantsCheckBox;
  private JTextField searchTextField;
  private JButton nextMatchButton;
  private JButton previousMatchButton;
  private JButton findTextInOntologyButton;
  private JCheckBox onlyAnnotationsCheckBox;
  private JCheckBox regexCheckBox;
  private JCheckBox caseSensitiveCheckBox;
  private JTabbedPane reviewTabbedPane;
  private JButton refreshTextReviewButton;
  private JPanel mainPanel;
  private JButton backButton;
  private JPanel filePanel;
  private JSplitPane body;
  private JPanel infoPane;
  private JPanel spanPane;
  private JPanel annotationPaneButtons;
  private JPanel graphSpacePane;
  private JPanel undoPane;
  private JPanel filterPane;
  private JComponent rootPane;
  private JPanel textSourcePane;
  private JPanel iaaPane;
  private JButton runIaaButton;
  private JCheckBox iaaSpanCheckBox;
  private JCheckBox iaaClassAndSpanCheckBox;
  private JCheckBox iaaClassCheckBox;
  private JButton captureImageButton;
  private JCheckBox oneClickGraphsCheckBox;
  private JTable conceptAnnotationsForTextTable;
  private JButton refreshConceptReviewButton;
  private JButton nextConceptReviewButton;
  private JButton previousConceptReviewButton;
  private JButton previousRelationReviewButton;
  private JButton nextRelationReviewButton;
  private JButton refreshRelationReviewButton;
  private JCheckBox snapToWordsCheckBox;
  private JRadioButton structuresRadioButton;
  private JRadioButton conceptsRadioButton;

  private final List<KnowtatorComponent> knowtatorComponents;
  private final HashMap<JButton, ActionListener> spanSizeButtons;
  private final HashMap<JButton, ActionListener> selectionSizeButtons;

  /**
   * Creates all components and sets up its model.
   */
  public KnowtatorView() {
    knowtatorComponents = new ArrayList<>();
    spanSizeButtons = new HashMap<>();
    selectionSizeButtons = new HashMap<>();

    //    $$$setupUI$$$();

    // This is necessary to force OSGI to load the mxGraphTransferable class to allow node dragging.
    // It is kind of a hacky fix, but it works for now.
    $$$setupUI$$$();
    log.warn(
        "Don't worry about the following exception. "
            + "Just forcing loading of a class needed by mxGraph");
    try {
      mxGraphTransferable.dataFlavor =
          new DataFlavor(
              String.format(
                  "%s; class=com.mxgraph.swing.util.mxGraphTransferable",
                  DataFlavor.javaJVMLocalObjectMimeType),
              null,
              mxGraphTransferable.class.getClassLoader());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    header.setSelectedIndex(1);
    makeButtons();
    knowtatorComponents.addAll(
        Arrays.asList(
            profileList,
            colorList,
            textPane,
            graphViewDialog,
            annotationNotes,
            annotationIdLabel,
            annotationAnnotatorLabel,
            annotationClassLabel,
            spanList,
            graphSpaceList,
            textSourceChooser,
            (KnowtatorComponent) conceptAnnotationsForTextTable,
            annotationsForClassTable,
            relationsForPropertyList));
  }

  private static void changeFont(Component component, Font font) {
    component.setFont(font);
    if (component instanceof Container) {
      for (Component child : ((Container) component).getComponents()) {
        changeFont(child, font);
      }
    }
  }

  /**
   * Gets model.
   *
   * @return the model
   */
  public Optional<KnowtatorModel> getModel() {
    return Optional.ofNullable(model);
  }

  /**
   * Inherited but not used.
   */
  @Override
  public void initialiseClassView() {
    changeFont(this, this.getParent().getFont());
  }

  /**
   * Creates custom UI components like chooser boxes and labels that listen to the model.
   */
  private void createUIComponents() {
    DropTarget dt = new DropTarget(this, this);
    dt.setActive(true);
    this.rootPane = this;

    searchTextField = new JTextField();
    regexCheckBox = new JCheckBox();
    onlyAnnotationsCheckBox = new JCheckBox();
    caseSensitiveCheckBox = new JCheckBox();
    includeClassDescendantsCheckBox = new JCheckBox();
    includePropertyDescendantsCheckBox = new JCheckBox();
    exactMatchCheckBox = new JCheckBox();
    owlClassLabel = new JLabel();
    owlPropertyLabel = new JLabel();

    textPane =
        new KnowtatorTextPane(this,
            searchTextField, onlyAnnotationsCheckBox, regexCheckBox, caseSensitiveCheckBox);
    graphViewDialog = new GraphViewDialog(this);

    annotationAnnotatorLabel = new AnnotationAnnotatorLabel(this);
    annotationClassLabel = new AnnotationClassLabel(this);
    annotationIdLabel = new AnnotationIdLabel(this);
    annotationNotes = new AnnotationNotes(this);

    textSourceChooser = new TextSourceChooser(this);

    graphSpaceList = new GraphSpaceList(this);
    spanList = new SpanList(this);
    profileList = new ProfileList(this);
    colorList = new ColorList(this);

    annotationsContainingTextTextField = new JTextField();

    annotationsForClassTable =
        new AnnotationTableForOwlClass(this, includeClassDescendantsCheckBox, owlClassLabel);
    conceptAnnotationsForTextTable =
        new AnnotationTableForSpannedText(this, exactMatchCheckBox, annotationsContainingTextTextField);
    relationsForPropertyList =
        new RelationTable(this, includePropertyDescendantsCheckBox, owlPropertyLabel);

    // The following methods keep the graph view dialog on top only when the view is active.
    KnowtatorView view = this;
    addAncestorListener(
        new AncestorListener() {
          @Override
          public void ancestorAdded(AncestorEvent event) {
            Window ancestor = SwingUtilities.getWindowAncestor(view);
            ancestor.addWindowFocusListener(
                new WindowFocusListener() {
                  @Override
                  public void windowGainedFocus(WindowEvent e) {
                    graphViewDialog.setAlwaysOnTop(true);
                  }

                  @Override
                  public void windowLostFocus(WindowEvent e) {
                    if (e.getOppositeWindow() != graphViewDialog) {
                      graphViewDialog.setAlwaysOnTop(false);
                      graphViewDialog.toBack();
                    }
                  }
                });
          }

          @Override
          public void ancestorRemoved(AncestorEvent event) {
          }

          @Override
          public void ancestorMoved(AncestorEvent event) {
          }
        });
  }

  /**
   * Makes the buttons in the main display pane.
   */
  private void makeButtons() {

    captureImageButton.addActionListener(
        e ->
            getModel()
                .ifPresent(
                    model ->
                        model
                            .getSelectedTextSource()
                            .ifPresent(
                                textSource -> {
                                  JFileChooser fileChooser =
                                      new JFileChooser(model.getSaveLocation());
                                  fileChooser.setFileFilter(
                                      new FileNameExtensionFilter("PNG", "png"));
                                  fileChooser.setSelectedFile(
                                      new File(
                                          String.format("%s_annotations.png", textSource.getId())));
                                  if (fileChooser.showSaveDialog(this)
                                      == JFileChooser.APPROVE_OPTION) {
                                    textSource.setSelectedConceptAnnotation(null);
                                    BufferedImage image = textPane.getScreenShot();
                                    try {
                                      ImageIO.write(image, "png", fileChooser.getSelectedFile());
                                    } catch (IOException e1) {
                                      e1.printStackTrace();
                                    }
                                  }
                                })));

    backButton.addActionListener(
        e -> {
          if (getModel().isPresent()) {
            getModel()
                .ifPresent(
                    model1 -> {
                      if (model1.isNotLoading()) {
                        CardLayout cl = (CardLayout) cardPanel.getLayout();
                        cl.show(cardPanel, "Main");
                        header.setSelectedIndex(1);
                      }
                    });
          } else {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "Main");
            header.setSelectedIndex(1);
          }
        });

    header.addChangeListener(
        e -> {
          if (header.getTitleAt(header.getSelectedIndex()).equals("File")) {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "File");
          }
        });

    findTextInOntologyButton.addActionListener(
        e -> getModel().ifPresent(model1 -> model1.searchForString(searchTextField.getText())));
    nextMatchButton.addActionListener(e -> textPane.searchForward());
    previousMatchButton.addActionListener(e -> textPane.searchPrevious());
    searchTextField.addActionListener(e -> textPane.searchForward());

    makeReviewPane();

    addProfileButton.addActionListener(
        e ->
            Optional.ofNullable(JOptionPane.showInputDialog(this, "Enter a name for the profile"))
                .ifPresent(
                    profileName ->
                        pickAction(this, profileName, null, new ActionParameters(ADD, PROFILE))));
    removeProfileButton.addActionListener(
        e ->
            Optional.of(
                JOptionPane.showConfirmDialog(
                    this, "Are you sure you wish to remove this profile?"))
                .filter(result -> JOptionPane.OK_OPTION == result)
                .ifPresent(
                    result -> pickAction(this, null, null, new ActionParameters(REMOVE, PROFILE))));

    addAnnotationButton.addActionListener(
        e ->
            pickAction(
                this,
                null,
                null,
                new ActionParameters(ADD, ANNOTATION),
                new ActionParameters(ADD, SPAN)));
    removeAnnotationButton.addActionListener(
        e ->
            pickAction(
                this,
                null,
                null,
                new ActionParameters(REMOVE, ANNOTATION),
                new ActionParameters(REMOVE, SPAN)));

    makeSpanButtons();

    fontSizeSlider.setValue(textPane.getFont().getSize());
    fontSizeSlider.addChangeListener(e -> textPane.setFontSize(fontSizeSlider.getValue()));
    showGraphViewerButton.addActionListener(e -> graphViewDialog.setVisible(true));
    previousTextSourceButton.addActionListener(
        e -> {
          getModel().ifPresent(BaseModel::selectPreviousTextSource);
          SwingUtilities.invokeLater(
              () -> {
                if (!getModel()
                    .flatMap(
                        model1 ->
                            model1
                                .getSelectedConceptAnnotation()
                                .map(SelectableCollection::getSelection))
                    .isPresent()) {
                  try {
                    textPane.scrollRectToVisible(textPane.modelToView(0));
                  } catch (BadLocationException e1) {
                    e1.printStackTrace();
                  }
                }
              });
        });

    nextTextSourceButton.addActionListener(
        e -> {
          getModel().ifPresent(BaseModel::selectNextTextSource);
          SwingUtilities.invokeLater(
              () -> {
                if (!getModel()
                    .flatMap(
                        model1 ->
                            model1
                                .getSelectedConceptAnnotation()
                                .map(SelectableCollection::getSelection))
                    .isPresent()) {
                  try {
                    textPane.scrollRectToVisible(textPane.modelToView(0));
                  } catch (BadLocationException e1) {
                    e1.printStackTrace();
                  }
                }
              });
        });
    addTextSourceButton.addActionListener(
        e -> {
          JFileChooser fileChooser = new JFileChooser();
          getModel()
              .ifPresent(
                  model1 -> {
                    fileChooser.setCurrentDirectory(model1.getArticlesLocation());

                    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                      pickAction(
                          this,
                          null,
                          fileChooser.getSelectedFile(),
                          new ActionParameters(ADD, DOCUMENT));
                    }
                  });
        });

    removeTextSourceButton.addActionListener(
        e -> pickAction(this, null, null, new ActionParameters(REMOVE, DOCUMENT)));

    assignColorToClassButton.addActionListener(
        e ->
            // TODO: This could be removed if class selection were reflected in color list
            getModel()
                .flatMap(OwlModel::getSelectedOwlClass)
                .ifPresent(owlClass -> assignColorToClass(this, owlClass)));

    undoButton.addActionListener(
        e -> getModel().filter(UndoManager::canUndo).ifPresent(UndoManager::undo));
    redoButton.addActionListener(
        e -> getModel().filter(UndoManager::canRedo).ifPresent(UndoManager::redo));

    owlClassFilterCheckBox.setSelected(false);
    profileFilterCheckBox.setSelected(false);

    profileFilterCheckBox.addItemListener(
        e ->
            getModel()
                .ifPresent(
                    knowtatorModel -> {
                      try {
                        knowtatorModel.registerAction(
                            new FilterAction(
                                knowtatorModel,
                                FilterType.PROFILE,
                                profileFilterCheckBox.isSelected()));
                      } catch (ActionUnperformable e1) {
                        JOptionPane.showMessageDialog(this, e1.getMessage());
                      }
                    }));
    owlClassFilterCheckBox.addItemListener(
        e ->
            getModel()
                .ifPresent(
                    knowtatorModel -> {
                      try {
                        knowtatorModel.registerAction(
                            new FilterAction(
                                knowtatorModel,
                                FilterType.OWLCLASS,
                                owlClassFilterCheckBox.isSelected()));
                      } catch (ActionUnperformable e1) {
                        JOptionPane.showMessageDialog(this, e1.getMessage());
                      }
                    }));

    fileList.addListSelectionListener(
        e -> {
          JFileChooser fileChooser = new JFileChooser();
          Optional.ofNullable(KnowtatorView.PREFERENCES.get("Last Project", null))
              .map(File::new)
              .filter(File::exists)
              .map(
                  file -> {
                    fileChooser.setCurrentDirectory(file);
                    return file;
                  })
              .map(File::listFiles)
              .flatMap(
                  files ->
                      Arrays.stream(files)
                          .filter(file1 -> file1.getName().endsWith(".knowtator"))
                          .findAny())
              .ifPresent(fileChooser::setSelectedFile);
          fileChooser.addActionListener(
              e1 ->
                  Optional.ofNullable(e1)
                      .filter(
                          event -> event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
                      .ifPresent(event -> KnowtatorView.this.open(fileChooser)));

          switch (fileList.getSelectedValue()) {
            case "Open":
              FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
              fileChooser.setFileFilter(fileFilter);
              fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
              break;
            case "New":
              fileChooser.setFileFilter(null);
              fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
              break;
            case "Import":
              fileFilter =
                  new FileNameExtensionFilter(
                      "ConceptAnnotation File (XML, ann, a1)", "xml", "ann", "a1");
              fileChooser.setFileFilter(fileFilter);
              fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
              break;
            case "Export":
              fileChooser.setFileFilter(null);
              fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
              break;
            default:
              break;
          }

          fileChooser.showOpenDialog(KnowtatorView.this);
        });

    runIaaButton.addActionListener(
        e ->
            getModel()
                .ifPresent(
                    model -> {
                      JFileChooser fileChooser = new JFileChooser();
                      fileChooser.setCurrentDirectory(model.getSaveLocation());
                      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                      //
                      // disable the "All files" option.
                      //
                      fileChooser.setAcceptAllFileFilterUsed(false);
                      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File outputDirectory = fileChooser.getSelectedFile();

                        try {
                          KnowtatorIaa knowtatorIaa = new KnowtatorIaa(outputDirectory, model);

                          if (iaaClassCheckBox.isSelected()) {
                            knowtatorIaa.runClassIaa();
                          }
                          if (iaaSpanCheckBox.isSelected()) {
                            knowtatorIaa.runSpanIaa();
                          }
                          if (iaaClassAndSpanCheckBox.isSelected()) {
                            knowtatorIaa.runClassAndSpanIaa();
                          }

                          knowtatorIaa.closeHtml();
                        } catch (IaaException e1) {
                          e1.printStackTrace();
                        }
                      }
                    }));
  }

  private void open(JFileChooser fileChooser) {
    switch (fileList.getSelectedValue()) {
      case "Open":
        new Loader(this, fileChooser.getSelectedFile(), progressBar, header, cardPanel).execute();
        break;
      case "New":
        Optional.ofNullable(JOptionPane.showInputDialog(this, "Enter a name for the project"))
            .filter(projectName -> !projectName.equals(""))
            .ifPresent(
                projectName -> {
                  File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);

                  new Loader(this, projectDirectory, progressBar, header, cardPanel).execute();
                });
        break;
      case "Import":
        getModel()
            .ifPresent(model -> model.loadWithAppropriateFormat(fileChooser.getSelectedFile()));
        break;
      case "Export":
        // TODO
        // getModel().ifPresent(model ->
        // model.writeWithAppropriateFormat(fileChooser.getSelectedFile()));
        break;
      default:
        break;
    }
  }

  private void makeSpanButtons() {
    nextSpanButton.addActionListener(
        e ->
            getModel()
                .flatMap(BaseModel::getSelectedTextSource)
                .ifPresent(TextSource::selectNextSpan));
    previousSpanButton.addActionListener(
        e ->
            getModel()
                .flatMap(BaseModel::getSelectedTextSource)
                .ifPresent(TextSource::selectPreviousSpan));

    spanSizeButtons.put(
        shrinkEndButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation()
                            .ifPresent(
                                conceptAnnotation ->
                                    conceptAnnotation
                                        .getSelection()
                                        .ifPresent(
                                            span -> {
                                              try {
                                                model1.registerAction(
                                                    new SpanActions.ModifySpanAction(
                                                        model1,
                                                        SpanActions.END,
                                                        SpanActions.SHRINK,
                                                        span));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    this, e1.getMessage());
                                              }
                                            }))));

    spanSizeButtons.put(
        shrinkStartButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation()
                            .ifPresent(
                                conceptAnnotation ->
                                    conceptAnnotation
                                        .getSelection()
                                        .ifPresent(
                                            span -> {
                                              try {
                                                model1.registerAction(
                                                    new SpanActions.ModifySpanAction(
                                                        model1,
                                                        SpanActions.START,
                                                        SpanActions.SHRINK,
                                                        span));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    this, e1.getMessage());
                                              }
                                            }))));
    spanSizeButtons.put(
        growEndButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation()
                            .ifPresent(
                                conceptAnnotation ->
                                    conceptAnnotation
                                        .getSelection()
                                        .ifPresent(
                                            span -> {
                                              try {
                                                model1.registerAction(
                                                    new SpanActions.ModifySpanAction(
                                                        model1,
                                                        SpanActions.END,
                                                        SpanActions.GROW,
                                                        span));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    this, e1.getMessage());
                                              }
                                            }))));
    spanSizeButtons.put(
        growStartButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation()
                            .ifPresent(
                                conceptAnnotation ->
                                    conceptAnnotation
                                        .getSelection()
                                        .ifPresent(
                                            span -> {
                                              try {
                                                model1.registerAction(
                                                    new SpanActions.ModifySpanAction(
                                                        model1,
                                                        SpanActions.START,
                                                        SpanActions.GROW,
                                                        span));
                                              } catch (ActionUnperformable e1) {
                                                JOptionPane.showMessageDialog(
                                                    this, e1.getMessage());
                                              }
                                            }))));

    selectionSizeButtons.put(
        shrinkEndButton,
        e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.SHRINK));
    selectionSizeButtons.put(
        shrinkStartButton,
        e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.SHRINK));
    selectionSizeButtons.put(
        growEndButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.GROW));
    selectionSizeButtons.put(
        growStartButton,
        e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.GROW));
  }

  private void makeReviewPane() {
    // TODO The Knowtator list doesn't seem to be being found so none of these methods are working
    ActionListener refreshTableAction = e -> getKnowtatorTable().ifPresent(KnowtatorTable::reset);

    refreshTextReviewButton.addActionListener(refreshTableAction);
    refreshConceptReviewButton.addActionListener(refreshTableAction);
    refreshRelationReviewButton.addActionListener(refreshTableAction);

    ActionListener nextTableAction =
        e ->
            getKnowtatorTable()
                .ifPresent(
                    knowtatorTable -> {
                      if (knowtatorTable.getRowCount() > 0) {
                        int nextRowIndex =
                            Math.min(
                                knowtatorTable.getSelectedRow() + 1,
                                knowtatorTable.getRowCount() - 1);
                        knowtatorTable.setRowSelectionInterval(nextRowIndex, nextRowIndex);
                        knowtatorTable.scrollRectToVisible(
                            knowtatorTable.getCellRect(nextRowIndex, 0, true));
                        knowtatorTable.reactToClick();
                      }
                    });

    nextTextReviewButton.addActionListener(nextTableAction);
    nextConceptReviewButton.addActionListener(nextTableAction);
    nextRelationReviewButton.addActionListener(nextTableAction);

    ActionListener previousTableAction =
        e ->
            getKnowtatorTable()
                .ifPresent(
                    knowtatorTable -> {
                      if (knowtatorTable.getRowCount() > 0) {
                        int previousRowIndex = Math.max(knowtatorTable.getSelectedRow() - 1, 0);
                        knowtatorTable.setRowSelectionInterval(previousRowIndex, previousRowIndex);
                        knowtatorTable.scrollRectToVisible(
                            knowtatorTable.getCellRect(previousRowIndex, 0, true));
                        knowtatorTable.reactToClick();
                      }
                    });

    previousTextReviewButton.addActionListener(previousTableAction);
    previousConceptReviewButton.addActionListener(previousTableAction);
    previousRelationReviewButton.addActionListener(previousTableAction);
  }

  private Optional<KnowtatorTable> getKnowtatorTable() {
    return Arrays.stream(((JPanel) reviewTabbedPane.getSelectedComponent()).getComponents())
        .filter(component -> component instanceof JScrollPane)
        .findFirst()
        .map(component -> (JScrollPane) component)
        .map(scrollPane -> scrollPane.getViewport().getComponent(0))
        .map(component -> (KnowtatorTable) component);
  }

  @Override
  protected OWLClass updateView(OWLClass selectedClass) {
    return selectedClass;
  }

  @Override
  public void reset() {
    knowtatorComponents.forEach(KnowtatorComponent::reset);
    getModel().ifPresent(model1 -> model1.addModelListener(this));
    getModel().ifPresent(model1 -> model1.addOwlModelManagerListener(annotationClassLabel));
  }

  /**
   * Gets graph view dialog.
   *
   * @return The graph view dialog
   */
  public GraphViewDialog getGraphViewDialog() {
    return graphViewDialog;
  }

  /**
   * Gets text pane.
   *
   * @return the Knowtator text pane
   */
  public KnowtatorTextPane getTextPane() {
    return textPane;
  }

  /**
   * Calls dispose on the model and all components.
   */
  @Override
  public void disposeView() {
    getModel().ifPresent(BaseModel::dispose);
    knowtatorComponents.forEach(KnowtatorComponent::dispose);
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

  @Override
  public void dragEnter(DropTargetDragEvent e) {
  }

  @Override
  public void dragOver(DropTargetDragEvent e) {
  }

  /**
   * Load project.
   *
   * @param file             the file
   * @param progressListener the progress listener
   * @throws IOException the io exception
   */
  public void loadProject(File file, ModelListener progressListener) throws IOException {
    if (!getModel().isPresent() || getModel().get().isNotLoading()) {
      getModel().ifPresent(BaseModel::dispose);
      if (getOWLWorkspace() != null) {
        setModel(new KnowtatorModel(file, getOWLWorkspace()));
      } else {
        setModel(new KnowtatorModel(file, null));
      }
      if (progressListener != null) {
        getModel().ifPresent(model1 -> model1.addModelListener(progressListener));
      }
      log.info(String.format("Opening from %s", file.getAbsolutePath()));
      getModel().ifPresent(OwlModel::load);
      if (progressListener != null) {
        getModel().ifPresent(model1 -> model1.removeModelListener(progressListener));
      }
      reset();

      getModel().ifPresent(BaseModel::selectFirstTextSource);
      textPane.showTextSource();

      getModel()
          .ifPresent(
              model1 ->
                  KnowtatorView.PREFERENCES.put(
                      "Last Project", model1.getProjectLocation().getAbsolutePath()));

      try {
        KnowtatorView.PREFERENCES.flush();
      } catch (BackingStoreException e1) {
        e1.printStackTrace();
      }
    }
  }

  private void setModel(KnowtatorModel model) {
    this.model = model;
  }

  @Override
  public void filterChangedEvent() {
  }

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    getModel()
        .ifPresent(
            model1 ->
                event
                    .getNew()
                    .ifPresent(
                        o -> {
                          if (o instanceof GraphSpace && isVisible()) {
                            graphViewDialog.setVisible(true);
                          }
                        }));

    getModel()
        .ifPresent(
            model1 -> {
              Optional optional =
                  model1.getSelectedTextSource().flatMap(TextSource::getSelectedAnnotation);
              if (optional.isPresent()) {
                spanSizeButtons
                    .keySet()
                    .forEach(
                        button ->
                            Arrays.stream(button.getActionListeners())
                                .forEach(button::removeActionListener));
                spanSizeButtons.forEach(AbstractButton::addActionListener);

              } else {
                spanSizeButtons
                    .keySet()
                    .forEach(
                        button ->
                            Arrays.stream(button.getActionListeners())
                                .forEach(button::removeActionListener));
                selectionSizeButtons.forEach(AbstractButton::addActionListener);
              }
            });
  }

  @Override
  public void colorChangedEvent(Profile profile) {
  }

  /**
   * Gets is one click graphs.
   *
   * @return the is one click graphs
   */
  public boolean getIsOneClickGraphs() {
    return oneClickGraphsCheckBox.isSelected();
  }

  /**
   * Is snap to words boolean.
   *
   * @return the boolean
   */
  public boolean isSnapToWords() {
    return snapToWordsCheckBox.isSelected();
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
    rootPane.setLayout(new BorderLayout(0, 0));
    cardPanel = new JPanel();
    cardPanel.setLayout(new CardLayout(0, 0));
    rootPane.add(cardPanel, BorderLayout.CENTER);
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout(0, 0));
    cardPanel.add(mainPanel, "Main");
    body = new JSplitPane();
    mainPanel.add(body, BorderLayout.CENTER);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new BorderLayout(0, 0));
    body.setRightComponent(panel1);
    infoPane = new JPanel();
    infoPane.setLayout(new GridBagLayout());
    infoPane.setMaximumSize(new Dimension(200, 2147483647));
    infoPane.setMinimumSize(new Dimension(200, 158));
    panel1.add(infoPane, BorderLayout.CENTER);
    infoPane.setBorder(BorderFactory.createTitledBorder(null, "Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 18, infoPane.getFont())));
    spanPane = new JPanel();
    spanPane.setLayout(new GridBagLayout());
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(spanPane, gbc);
    spanPane.setBorder(BorderFactory.createTitledBorder(null, "Spans", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, spanPane.getFont())));
    final JScrollPane scrollPane1 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    spanPane.add(scrollPane1, gbc);
    Font spanListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, spanList.getFont());
    if (spanListFont != null) {
      spanList.setFont(spanListFont);
    }
    scrollPane1.setViewportView(spanList);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    spanPane.add(panel2, gbc);
    growStartButton = new JButton();
    Font growStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growStartButton.getFont());
    if (growStartButtonFont != null) {
      growStartButton.setFont(growStartButtonFont);
    }
    growStartButton.setText("Grow Start");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    panel2.add(growStartButton, gbc);
    shrinkStartButton = new JButton();
    Font shrinkStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkStartButton.getFont());
    if (shrinkStartButtonFont != null) {
      shrinkStartButton.setFont(shrinkStartButtonFont);
    }
    shrinkStartButton.setText("Shrink Start");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    panel2.add(shrinkStartButton, gbc);
    shrinkEndButton = new JButton();
    Font shrinkEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkEndButton.getFont());
    if (shrinkEndButtonFont != null) {
      shrinkEndButton.setFont(shrinkEndButtonFont);
    }
    shrinkEndButton.setText("Shrink End");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    panel2.add(shrinkEndButton, gbc);
    growEndButton = new JButton();
    Font growEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growEndButton.getFont());
    if (growEndButtonFont != null) {
      growEndButton.setFont(growEndButtonFont);
    }
    growEndButton.setText("Grow End");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    panel2.add(growEndButton, gbc);
    graphSpacePane = new JPanel();
    graphSpacePane.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.gridwidth = 2;
    gbc.gridheight = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(graphSpacePane, gbc);
    graphSpacePane.setBorder(BorderFactory.createTitledBorder(null, "Graph Spaces", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, graphSpacePane.getFont())));
    final JScrollPane scrollPane2 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    graphSpacePane.add(scrollPane2, gbc);
    Font graphSpaceListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, graphSpaceList.getFont());
    if (graphSpaceListFont != null) {
      graphSpaceList.setFont(graphSpaceListFont);
    }
    scrollPane2.setViewportView(graphSpaceList);
    final JScrollPane scrollPane3 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 5;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(scrollPane3, gbc);
    scrollPane3.setBorder(BorderFactory.createTitledBorder(null, "Notes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, scrollPane3.getFont())));
    Font annotationNotesFont = this.$$$getFont$$$("Verdana", -1, 12, annotationNotes.getFont());
    if (annotationNotesFont != null) {
      annotationNotes.setFont(annotationNotesFont);
    }
    scrollPane3.setViewportView(annotationNotes);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(panel3, gbc);
    panel3.setBorder(BorderFactory.createTitledBorder(null, "ID", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel3.getFont())));
    Font annotationIdLabelFont = this.$$$getFont$$$("Verdana", -1, -1, annotationIdLabel.getFont());
    if (annotationIdLabelFont != null) {
      annotationIdLabel.setFont(annotationIdLabelFont);
    }
    annotationIdLabel.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel3.add(annotationIdLabel, gbc);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(panel4, gbc);
    panel4.setBorder(BorderFactory.createTitledBorder(null, "Annotator", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel4.getFont())));
    Font annotationAnnotatorLabelFont = this.$$$getFont$$$("Verdana", -1, -1, annotationAnnotatorLabel.getFont());
    if (annotationAnnotatorLabelFont != null) {
      annotationAnnotatorLabel.setFont(annotationAnnotatorLabelFont);
    }
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel4.add(annotationAnnotatorLabel, gbc);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(panel5, gbc);
    panel5.setBorder(BorderFactory.createTitledBorder(null, "OWL CLass", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel5.getFont())));
    Font annotationClassLabelFont = this.$$$getFont$$$("Verdana", -1, -1, annotationClassLabel.getFont());
    if (annotationClassLabelFont != null) {
      annotationClassLabel.setFont(annotationClassLabelFont);
    }
    annotationClassLabel.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel5.add(annotationClassLabel, gbc);
    final JScrollPane scrollPane4 = new JScrollPane();
    body.setLeftComponent(scrollPane4);
    textPane.setMinimumSize(new Dimension(200, 22));
    textPane.setPreferredSize(new Dimension(500, 500));
    scrollPane4.setViewportView(textPane);
    header = new JTabbedPane();
    Font headerFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 14, header.getFont());
    if (headerFont != null) {
      header.setFont(headerFont);
    }
    header.setPreferredSize(new Dimension(788, 200));
    header.setTabLayoutPolicy(0);
    header.setTabPlacement(1);
    mainPanel.add(header, BorderLayout.NORTH);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridBagLayout());
    header.addTab("File", panel6);
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new GridBagLayout());
    header.addTab("Home", panel7);
    textSourcePane = new JPanel();
    textSourcePane.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 3;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel7.add(textSourcePane, gbc);
    textSourcePane.setBorder(BorderFactory.createTitledBorder(null, "Document", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, textSourcePane.getFont())));
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    textSourcePane.add(panel8, gbc);
    previousTextSourceButton = new JButton();
    previousTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousTextSourceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    panel8.add(previousTextSourceButton, gbc);
    nextTextSourceButton = new JButton();
    nextTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextTextSourceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    panel8.add(nextTextSourceButton, gbc);
    addTextSourceButton = new JButton();
    addTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addTextSourceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel8.add(addTextSourceButton, gbc);
    removeTextSourceButton = new JButton();
    removeTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeTextSourceButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel8.add(removeTextSourceButton, gbc);
    textSourceChooser.setPreferredSize(new Dimension(150, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    panel8.add(textSourceChooser, gbc);
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    textSourcePane.add(panel9, gbc);
    panel9.setBorder(BorderFactory.createTitledBorder("Font Size"));
    fontSizeSlider = new JSlider();
    fontSizeSlider.setInverted(false);
    fontSizeSlider.setMajorTickSpacing(8);
    fontSizeSlider.setMaximum(28);
    fontSizeSlider.setMinimum(8);
    fontSizeSlider.setMinorTickSpacing(1);
    fontSizeSlider.setSnapToTicks(true);
    fontSizeSlider.setValue(16);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel9.add(fontSizeSlider, gbc);
    captureImageButton = new JButton();
    captureImageButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-unsplash-32.png")));
    captureImageButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    textSourcePane.add(captureImageButton, gbc);
    undoPane = new JPanel();
    undoPane.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridheight = 4;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel7.add(undoPane, gbc);
    undoButton = new JButton();
    undoButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-undo-32.png")));
    undoButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    undoPane.add(undoButton, gbc);
    showGraphViewerButton = new JButton();
    Font showGraphViewerButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, showGraphViewerButton.getFont());
    if (showGraphViewerButtonFont != null) {
      showGraphViewerButton.setFont(showGraphViewerButtonFont);
    }
    showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
    showGraphViewerButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    undoPane.add(showGraphViewerButton, gbc);
    filterPane = new JPanel();
    filterPane.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    undoPane.add(filterPane, gbc);
    owlClassFilterCheckBox = new JCheckBox();
    Font owlClassFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlClassFilterCheckBox.getFont());
    if (owlClassFilterCheckBoxFont != null) {
      owlClassFilterCheckBox.setFont(owlClassFilterCheckBoxFont);
    }
    owlClassFilterCheckBox.setText("OWL Class");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    filterPane.add(owlClassFilterCheckBox, gbc);
    profileFilterCheckBox = new JCheckBox();
    Font profileFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, profileFilterCheckBox.getFont());
    if (profileFilterCheckBoxFont != null) {
      profileFilterCheckBox.setFont(profileFilterCheckBoxFont);
    }
    profileFilterCheckBox.setText("Profile");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    filterPane.add(profileFilterCheckBox, gbc);
    redoButton = new JButton();
    redoButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-redo-32.png")));
    redoButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    undoPane.add(redoButton, gbc);
    assignColorToClassButton = new JButton();
    assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
    assignColorToClassButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    undoPane.add(assignColorToClassButton, gbc);
    annotationPaneButtons = new JPanel();
    annotationPaneButtons.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridheight = 3;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel7.add(annotationPaneButtons, gbc);
    annotationPaneButtons.setBorder(BorderFactory.createTitledBorder(null, "Annotation", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, annotationPaneButtons.getFont())));
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    annotationPaneButtons.add(panel10, gbc);
    previousSpanButton = new JButton();
    previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousSpanButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel10.add(previousSpanButton, gbc);
    nextSpanButton = new JButton();
    nextSpanButton.setHorizontalAlignment(0);
    nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextSpanButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel10.add(nextSpanButton, gbc);
    final JPanel panel11 = new JPanel();
    panel11.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    annotationPaneButtons.add(panel11, gbc);
    addAnnotationButton = new JButton();
    Font addAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, addAnnotationButton.getFont());
    if (addAnnotationButtonFont != null) {
      addAnnotationButton.setFont(addAnnotationButtonFont);
    }
    addAnnotationButton.setHorizontalTextPosition(0);
    addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addAnnotationButton.setText("");
    addAnnotationButton.setVerticalAlignment(0);
    addAnnotationButton.setVerticalTextPosition(3);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    panel11.add(addAnnotationButton, gbc);
    removeAnnotationButton = new JButton();
    Font removeAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeAnnotationButton.getFont());
    if (removeAnnotationButtonFont != null) {
      removeAnnotationButton.setFont(removeAnnotationButtonFont);
    }
    removeAnnotationButton.setHorizontalTextPosition(0);
    removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeAnnotationButton.setText("");
    removeAnnotationButton.setVerticalTextPosition(3);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    panel11.add(removeAnnotationButton, gbc);
    oneClickGraphsCheckBox = new JCheckBox();
    oneClickGraphsCheckBox.setText("One Click Graphs");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.gridheight = 2;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel7.add(oneClickGraphsCheckBox, gbc);
    snapToWordsCheckBox = new JCheckBox();
    snapToWordsCheckBox.setSelected(true);
    snapToWordsCheckBox.setText("Snap to words");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel7.add(snapToWordsCheckBox, gbc);
    structuresRadioButton = new JRadioButton();
    structuresRadioButton.setText("Structures");
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel7.add(structuresRadioButton, gbc);
    conceptsRadioButton = new JRadioButton();
    conceptsRadioButton.setText("Concepts");
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel7.add(conceptsRadioButton, gbc);
    final JPanel panel12 = new JPanel();
    panel12.setLayout(new GridBagLayout());
    header.addTab("Profile", panel12);
    final JScrollPane scrollPane5 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(scrollPane5, gbc);
    scrollPane5.setBorder(BorderFactory.createTitledBorder(null, "Colors", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, scrollPane5.getFont())));
    Font colorListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, colorList.getFont());
    if (colorListFont != null) {
      colorList.setFont(colorListFont);
    }
    scrollPane5.setViewportView(colorList);
    final JPanel panel13 = new JPanel();
    panel13.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel12.add(panel13, gbc);
    panel13.setBorder(BorderFactory.createTitledBorder(null, "Profiles", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel13.getFont())));
    final JScrollPane scrollPane6 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel13.add(scrollPane6, gbc);
    Font profileListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, profileList.getFont());
    if (profileListFont != null) {
      profileList.setFont(profileListFont);
    }
    scrollPane6.setViewportView(profileList);
    final JPanel panel14 = new JPanel();
    panel14.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel13.add(panel14, gbc);
    addProfileButton = new JButton();
    Font addProfileButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, addProfileButton.getFont());
    if (addProfileButtonFont != null) {
      addProfileButton.setFont(addProfileButtonFont);
    }
    addProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addProfileButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel14.add(addProfileButton, gbc);
    removeProfileButton = new JButton();
    removeProfileButton.setEnabled(true);
    Font removeProfileButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeProfileButton.getFont());
    if (removeProfileButtonFont != null) {
      removeProfileButton.setFont(removeProfileButtonFont);
    }
    removeProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeProfileButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel14.add(removeProfileButton, gbc);
    iaaPane = new JPanel();
    iaaPane.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    panel12.add(iaaPane, gbc);
    iaaPane.setBorder(BorderFactory.createTitledBorder(null, "IAA", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, iaaPane.getFont())));
    runIaaButton = new JButton();
    Font runIaaButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, runIaaButton.getFont());
    if (runIaaButtonFont != null) {
      runIaaButton.setFont(runIaaButtonFont);
    }
    this.$$$loadButtonText$$$(runIaaButton, ResourceBundle.getBundle("log4j").getString("run.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    iaaPane.add(runIaaButton, gbc);
    iaaSpanCheckBox = new JCheckBox();
    Font iaaSpanCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, iaaSpanCheckBox.getFont());
    if (iaaSpanCheckBoxFont != null) {
      iaaSpanCheckBox.setFont(iaaSpanCheckBoxFont);
    }
    this.$$$loadButtonText$$$(iaaSpanCheckBox, ResourceBundle.getBundle("ui").getString("span1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    iaaPane.add(iaaSpanCheckBox, gbc);
    iaaClassAndSpanCheckBox = new JCheckBox();
    Font iaaClassAndSpanCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, iaaClassAndSpanCheckBox.getFont());
    if (iaaClassAndSpanCheckBoxFont != null) {
      iaaClassAndSpanCheckBox.setFont(iaaClassAndSpanCheckBoxFont);
    }
    this.$$$loadButtonText$$$(iaaClassAndSpanCheckBox, ResourceBundle.getBundle("ui").getString("class.and.span"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    iaaPane.add(iaaClassAndSpanCheckBox, gbc);
    iaaClassCheckBox = new JCheckBox();
    Font iaaClassCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, iaaClassCheckBox.getFont());
    if (iaaClassCheckBoxFont != null) {
      iaaClassCheckBox.setFont(iaaClassCheckBoxFont);
    }
    this.$$$loadButtonText$$$(iaaClassCheckBox, ResourceBundle.getBundle("log4j").getString("class1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    iaaPane.add(iaaClassCheckBox, gbc);
    final JPanel panel15 = new JPanel();
    panel15.setLayout(new GridBagLayout());
    header.addTab("Search", panel15);
    Font searchTextFieldFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, searchTextField.getFont());
    if (searchTextFieldFont != null) {
      searchTextField.setFont(searchTextFieldFont);
    }
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel15.add(searchTextField, gbc);
    final JPanel panel16 = new JPanel();
    panel16.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel15.add(panel16, gbc);
    nextMatchButton = new JButton();
    Font nextMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, nextMatchButton.getFont());
    if (nextMatchButtonFont != null) {
      nextMatchButton.setFont(nextMatchButtonFont);
    }
    nextMatchButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextMatchButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel16.add(nextMatchButton, gbc);
    previousMatchButton = new JButton();
    Font previousMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, previousMatchButton.getFont());
    if (previousMatchButtonFont != null) {
      previousMatchButton.setFont(previousMatchButtonFont);
    }
    previousMatchButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousMatchButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel16.add(previousMatchButton, gbc);
    findTextInOntologyButton = new JButton();
    Font findTextInOntologyButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, findTextInOntologyButton.getFont());
    if (findTextInOntologyButtonFont != null) {
      findTextInOntologyButton.setFont(findTextInOntologyButtonFont);
    }
    this.$$$loadButtonText$$$(findTextInOntologyButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel15.add(findTextInOntologyButton, gbc);
    Font onlyAnnotationsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, onlyAnnotationsCheckBox.getFont());
    if (onlyAnnotationsCheckBoxFont != null) {
      onlyAnnotationsCheckBox.setFont(onlyAnnotationsCheckBoxFont);
    }
    onlyAnnotationsCheckBox.setText("Only in Annotations");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    panel15.add(onlyAnnotationsCheckBox, gbc);
    Font regexCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, regexCheckBox.getFont());
    if (regexCheckBoxFont != null) {
      regexCheckBox.setFont(regexCheckBoxFont);
    }
    regexCheckBox.setText("Regex");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel15.add(regexCheckBox, gbc);
    Font caseSensitiveCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, caseSensitiveCheckBox.getFont());
    if (caseSensitiveCheckBoxFont != null) {
      caseSensitiveCheckBox.setFont(caseSensitiveCheckBoxFont);
    }
    caseSensitiveCheckBox.setText("Case Sensitive");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel15.add(caseSensitiveCheckBox, gbc);
    final JPanel panel17 = new JPanel();
    panel17.setLayout(new GridBagLayout());
    header.addTab("Review", panel17);
    final JPanel panel18 = new JPanel();
    panel18.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel17.add(panel18, gbc);
    reviewTabbedPane = new JTabbedPane();
    Font reviewTabbedPaneFont = this.$$$getFont$$$("Verdana", -1, 14, reviewTabbedPane.getFont());
    if (reviewTabbedPaneFont != null) {
      reviewTabbedPane.setFont(reviewTabbedPaneFont);
    }
    reviewTabbedPane.setTabPlacement(2);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel18.add(reviewTabbedPane, gbc);
    final JPanel panel19 = new JPanel();
    panel19.setLayout(new GridBagLayout());
    reviewTabbedPane.addTab("Text", panel19);
    final JScrollPane scrollPane7 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 5;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel19.add(scrollPane7, gbc);
    Font conceptAnnotationsForTextTableFont = this.$$$getFont$$$("Verdana", -1, 12, conceptAnnotationsForTextTable.getFont());
    if (conceptAnnotationsForTextTableFont != null) {
      conceptAnnotationsForTextTable.setFont(conceptAnnotationsForTextTableFont);
    }
    scrollPane7.setViewportView(conceptAnnotationsForTextTable);
    Font exactMatchCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, exactMatchCheckBox.getFont());
    if (exactMatchCheckBoxFont != null) {
      exactMatchCheckBox.setFont(exactMatchCheckBoxFont);
    }
    this.$$$loadButtonText$$$(exactMatchCheckBox, ResourceBundle.getBundle("log4j").getString("exact.match"));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel19.add(exactMatchCheckBox, gbc);
    final JPanel panel20 = new JPanel();
    panel20.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel19.add(panel20, gbc);
    panel20.setBorder(BorderFactory.createTitledBorder(null, "Text", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel20.getFont())));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel20.add(annotationsContainingTextTextField, gbc);
    final JPanel panel21 = new JPanel();
    panel21.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel19.add(panel21, gbc);
    previousTextReviewButton = new JButton();
    previousTextReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousTextReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel21.add(previousTextReviewButton, gbc);
    nextTextReviewButton = new JButton();
    nextTextReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextTextReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel21.add(nextTextReviewButton, gbc);
    refreshTextReviewButton = new JButton();
    refreshTextReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-synchronize-32.png")));
    refreshTextReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel21.add(refreshTextReviewButton, gbc);
    final JPanel panel22 = new JPanel();
    panel22.setLayout(new GridBagLayout());
    reviewTabbedPane.addTab("Concept", panel22);
    final JScrollPane scrollPane8 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel22.add(scrollPane8, gbc);
    Font annotationsForClassTableFont = this.$$$getFont$$$("Verdana", -1, 12, annotationsForClassTable.getFont());
    if (annotationsForClassTableFont != null) {
      annotationsForClassTable.setFont(annotationsForClassTableFont);
    }
    scrollPane8.setViewportView(annotationsForClassTable);
    Font includeClassDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includeClassDescendantsCheckBox.getFont());
    if (includeClassDescendantsCheckBoxFont != null) {
      includeClassDescendantsCheckBox.setFont(includeClassDescendantsCheckBoxFont);
    }
    this.$$$loadButtonText$$$(includeClassDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants"));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel22.add(includeClassDescendantsCheckBox, gbc);
    final JPanel panel23 = new JPanel();
    panel23.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel22.add(panel23, gbc);
    panel23.setBorder(BorderFactory.createTitledBorder(null, "OWL Class", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel23.getFont())));
    owlClassLabel.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel23.add(owlClassLabel, gbc);
    final JPanel panel24 = new JPanel();
    panel24.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel22.add(panel24, gbc);
    previousConceptReviewButton = new JButton();
    previousConceptReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousConceptReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel24.add(previousConceptReviewButton, gbc);
    nextConceptReviewButton = new JButton();
    nextConceptReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextConceptReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel24.add(nextConceptReviewButton, gbc);
    refreshConceptReviewButton = new JButton();
    refreshConceptReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-synchronize-32.png")));
    refreshConceptReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel24.add(refreshConceptReviewButton, gbc);
    final JPanel panel25 = new JPanel();
    panel25.setLayout(new GridBagLayout());
    reviewTabbedPane.addTab("Relation", panel25);
    final JScrollPane scrollPane9 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel25.add(scrollPane9, gbc);
    Font relationsForPropertyListFont = this.$$$getFont$$$("Verdana", -1, 12, relationsForPropertyList.getFont());
    if (relationsForPropertyListFont != null) {
      relationsForPropertyList.setFont(relationsForPropertyListFont);
    }
    scrollPane9.setViewportView(relationsForPropertyList);
    Font includePropertyDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includePropertyDescendantsCheckBox.getFont());
    if (includePropertyDescendantsCheckBoxFont != null) {
      includePropertyDescendantsCheckBox.setFont(includePropertyDescendantsCheckBoxFont);
    }
    this.$$$loadButtonText$$$(includePropertyDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel25.add(includePropertyDescendantsCheckBox, gbc);
    final JPanel panel26 = new JPanel();
    panel26.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel25.add(panel26, gbc);
    panel26.setBorder(BorderFactory.createTitledBorder(null, "OWL Object Property", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel26.getFont())));
    Font owlPropertyLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlPropertyLabel.getFont());
    if (owlPropertyLabelFont != null) {
      owlPropertyLabel.setFont(owlPropertyLabelFont);
    }
    owlPropertyLabel.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel26.add(owlPropertyLabel, gbc);
    final JPanel panel27 = new JPanel();
    panel27.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel25.add(panel27, gbc);
    previousRelationReviewButton = new JButton();
    previousRelationReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousRelationReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel27.add(previousRelationReviewButton, gbc);
    nextRelationReviewButton = new JButton();
    nextRelationReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextRelationReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel27.add(nextRelationReviewButton, gbc);
    refreshRelationReviewButton = new JButton();
    refreshRelationReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-synchronize-32.png")));
    refreshRelationReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel27.add(refreshRelationReviewButton, gbc);
    filePanel = new JPanel();
    filePanel.setLayout(new GridBagLayout());
    cardPanel.add(filePanel, "File");
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    filePanel.add(progressBar, gbc);
    final JScrollPane scrollPane10 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    filePanel.add(scrollPane10, gbc);
    fileList = new JList();
    Font fileListFont = this.$$$getFont$$$("Verdana", Font.BOLD, 14, fileList.getFont());
    if (fileListFont != null) {
      fileList.setFont(fileListFont);
    }
    final DefaultListModel defaultListModel1 = new DefaultListModel();
    defaultListModel1.addElement("Open");
    defaultListModel1.addElement("New");
    defaultListModel1.addElement("Import");
    defaultListModel1.addElement("Export");
    fileList.setModel(defaultListModel1);
    scrollPane10.setViewportView(fileList);
    backButton = new JButton();
    backButton.setText("Back");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    filePanel.add(backButton, gbc);
    ButtonGroup buttonGroup;
    buttonGroup = new ButtonGroup();
    buttonGroup.add(structuresRadioButton);
    buttonGroup.add(conceptsRadioButton);
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
  private void $$$loadButtonText$$$(AbstractButton component, String text) {
    StringBuffer result = new StringBuffer();
    boolean haveMnemonic = false;
    char mnemonic = '\0';
    int mnemonicIndex = -1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '&') {
        i++;
        if (i == text.length()) {
          break;
        }
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
    return rootPane;
  }

}
