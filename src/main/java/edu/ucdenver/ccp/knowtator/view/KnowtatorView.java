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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
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
  /** The constant PREFERENCES. */
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

  /** Creates all components and sets up its model. */
  public KnowtatorView() {
    knowtatorComponents = new ArrayList<>();
    spanSizeButtons = new HashMap<>();
    selectionSizeButtons = new HashMap<>();

    //    $$$setupUI$$$();

    // This is necessary to force OSGI to load the mxGraphTransferable class to allow node dragging.
    // It is kind of a hacky fix, but it works for now.
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

  @Override
  public void setView(KnowtatorView view) {
    knowtatorComponents.forEach(component -> component.setView(this));
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

  /** Inherited but not used. */
  @Override
  public void initialiseClassView() {
    changeFont(this, this.getParent().getFont());
  }

  /** Creates custom UI components like chooser boxes and labels that listen to the model. */
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
        new KnowtatorTextPane(
            searchTextField, onlyAnnotationsCheckBox, regexCheckBox, caseSensitiveCheckBox);
    graphViewDialog = new GraphViewDialog();

    annotationAnnotatorLabel = new AnnotationAnnotatorLabel();
    annotationClassLabel = new AnnotationClassLabel();
    annotationIdLabel = new AnnotationIdLabel();
    annotationNotes = new AnnotationNotes();

    textSourceChooser = new TextSourceChooser();

    graphSpaceList = new GraphSpaceList();
    spanList = new SpanList();
    profileList = new ProfileList();
    colorList = new ColorList();

    annotationsContainingTextTextField = new JTextField();

    annotationsForClassTable =
        new AnnotationTableForOwlClass(includeClassDescendantsCheckBox, owlClassLabel);
    conceptAnnotationsForTextTable =
        new AnnotationTableForSpannedText(exactMatchCheckBox, annotationsContainingTextTextField);
    relationsForPropertyList =
        new RelationTable(includePropertyDescendantsCheckBox, owlPropertyLabel);

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
          public void ancestorRemoved(AncestorEvent event) {}

          @Override
          public void ancestorMoved(AncestorEvent event) {}
        });
  }

  /** Makes the buttons in the main display pane. */
  private void makeButtons() {
    structuresRadioButton.addChangeListener(
        e ->
            getModel()
                .ifPresent(model -> model.setStructureMode(structuresRadioButton.isSelected())));

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

  /** Calls dispose on the model and all components. */
  @Override
  public void disposeView() {
    getModel().ifPresent(BaseModel::dispose);
    knowtatorComponents.forEach(KnowtatorComponent::dispose);
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent e) {}

  @Override
  public void dragExit(DropTargetEvent e) {}

  @Override
  public void drop(DropTargetDropEvent e) {}

  @Override
  public void dragEnter(DropTargetDragEvent e) {}

  @Override
  public void dragOver(DropTargetDragEvent e) {}

  /**
   * Load project.
   *
   * @param file the file
   * @param progressListener the progress listener
   * @throws IOException the io exception
   */
  public void loadProject(File file, ModelListener progressListener) throws IOException {
    setView(this);
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
  public void filterChangedEvent() {}

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
  public void colorChangedEvent() {}

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
}
