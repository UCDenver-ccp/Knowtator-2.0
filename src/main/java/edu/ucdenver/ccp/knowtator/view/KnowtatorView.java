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
import edu.ucdenver.ccp.knowtator.view.table.AnnotationTable;
import edu.ucdenver.ccp.knowtator.view.table.AnnotationTableForOwlClass;
import edu.ucdenver.ccp.knowtator.view.table.AnnotationTableForSpannedText;
import edu.ucdenver.ccp.knowtator.view.table.KnowtatorTable;
import edu.ucdenver.ccp.knowtator.view.table.RelationTable;
import edu.ucdenver.ccp.knowtator.view.table.SpanTable;
import edu.ucdenver.ccp.knowtator.view.textpane.KnowtatorTextPane;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.lang.reflect.Method;
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
  private GraphSpaceList graphSpaceList;
  private AnnotationIdLabel annotationIdLabel;
  private AnnotationAnnotatorLabel annotationAnnotatorLabel;
  private AnnotationClassLabel annotationClassLabel;
  private AnnotationNotes annotationNotes;
  private SpanTable spanTable;
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
  private JPanel graphSpacePane;
  private JComponent rootPane;
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
  private JTabbedPane header;

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
            spanTable,
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
    spanTable = new SpanTable(this);
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
                        header.setSelectedIndex(0);
                      }
                    });
          } else {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "Main");
            header.setSelectedIndex(0);
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
                            .getSelectedConceptAnnotation().flatMap(SelectableCollection::getSelection).ifPresent(span -> {
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
                        })));

    spanSizeButtons.put(
        shrinkStartButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation().flatMap(SelectableCollection::getSelection).ifPresent(span -> {
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
                        })));
    spanSizeButtons.put(
        growEndButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation().flatMap(SelectableCollection::getSelection).ifPresent(span -> {
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
                        })));
    spanSizeButtons.put(
        growStartButton,
        e ->
            getModel()
                .ifPresent(
                    model1 ->
                        model1
                            .getSelectedConceptAnnotation().flatMap(SelectableCollection::getSelection).ifPresent(span -> {
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
                        })));

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
    getModel().ifPresent(model1 -> model1.addOwlSelectionModelListener(colorList));
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
    getModel().flatMap(model1 -> event
        .getNew()).ifPresent(o -> {
      if (o instanceof GraphSpace && isVisible()) {
        graphViewDialog.setVisible(true);
      }
    });

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
    header = new JTabbedPane();
    Font headerFont = this.$$$getFont$$$(null, -1, 14, header.getFont());
    if (headerFont != null) {
      header.setFont(headerFont);
    }
    panel1.add(header, BorderLayout.CENTER);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout(0, 0));
    header.addTab("Annotation", panel2);
    infoPane = new JPanel();
    infoPane.setLayout(new GridBagLayout());
    infoPane.setMaximumSize(new Dimension(200, 2147483647));
    infoPane.setMinimumSize(new Dimension(200, 158));
    panel2.add(infoPane, BorderLayout.CENTER);
    final JTabbedPane tabbedPane1 = new JTabbedPane();
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(tabbedPane1, gbc);
    spanPane = new JPanel();
    spanPane.setLayout(new GridBagLayout());
    tabbedPane1.addTab("Spans", spanPane);
    spanPane.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, spanPane.getFont()), null));
    final JScrollPane scrollPane1 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    spanPane.add(scrollPane1, gbc);
    Font spanTableFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, spanTable.getFont());
    if (spanTableFont != null) {
      spanTable.setFont(spanTableFont);
    }
    scrollPane1.setViewportView(spanTable);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    spanPane.add(panel3, gbc);
    shrinkStartButton = new JButton();
    Font shrinkStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkStartButton.getFont());
    if (shrinkStartButtonFont != null) {
      shrinkStartButton.setFont(shrinkStartButtonFont);
    }
    shrinkStartButton.setText("Shrink Start");
    panel3.add(shrinkStartButton);
    shrinkEndButton = new JButton();
    Font shrinkEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkEndButton.getFont());
    if (shrinkEndButtonFont != null) {
      shrinkEndButton.setFont(shrinkEndButtonFont);
    }
    shrinkEndButton.setText("Shrink End");
    panel3.add(shrinkEndButton);
    growEndButton = new JButton();
    Font growEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growEndButton.getFont());
    if (growEndButtonFont != null) {
      growEndButton.setFont(growEndButtonFont);
    }
    growEndButton.setText("Grow End");
    panel3.add(growEndButton);
    growStartButton = new JButton();
    Font growStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growStartButton.getFont());
    if (growStartButtonFont != null) {
      growStartButton.setFont(growStartButtonFont);
    }
    growStartButton.setText("Grow Start");
    panel3.add(growStartButton);
    graphSpacePane = new JPanel();
    graphSpacePane.setLayout(new GridBagLayout());
    tabbedPane1.addTab("Graph Spaces", graphSpacePane);
    graphSpacePane.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 16, graphSpacePane.getFont()), null));
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
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridBagLayout());
    tabbedPane1.addTab("Notes", panel4);
    final JScrollPane scrollPane3 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel4.add(scrollPane3, gbc);
    scrollPane3.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, scrollPane3.getFont()), null));
    Font annotationNotesFont = this.$$$getFont$$$("Verdana", -1, 12, annotationNotes.getFont());
    if (annotationNotesFont != null) {
      annotationNotes.setFont(annotationNotesFont);
    }
    annotationNotes.setText("");
    scrollPane3.setViewportView(annotationNotes);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(panel5, gbc);
    panel5.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel5.getFont()), null));
    final JLabel label1 = new JLabel();
    Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 12, label1.getFont());
    if (label1Font != null) {
      label1.setFont(label1Font);
    }
    label1.setText("OWL Class:");
    panel5.add(label1);
    Font annotationClassLabelFont = this.$$$getFont$$$("Verdana", -1, -1, annotationClassLabel.getFont());
    if (annotationClassLabelFont != null) {
      annotationClassLabel.setFont(annotationClassLabelFont);
    }
    annotationClassLabel.setText("");
    panel5.add(annotationClassLabel);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(panel6, gbc);
    panel6.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel6.getFont()), null));
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$(null, Font.BOLD, 12, label2.getFont());
    if (label2Font != null) {
      label2.setFont(label2Font);
    }
    label2.setText("Annotator:");
    panel6.add(label2);
    Font annotationAnnotatorLabelFont = this.$$$getFont$$$("Verdana", -1, -1, annotationAnnotatorLabel.getFont());
    if (annotationAnnotatorLabelFont != null) {
      annotationAnnotatorLabel.setFont(annotationAnnotatorLabelFont);
    }
    panel6.add(annotationAnnotatorLabel);
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    infoPane.add(panel7, gbc);
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel7.add(panel8, gbc);
    panel8.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel8.getFont()), null));
    final JLabel label3 = new JLabel();
    Font label3Font = this.$$$getFont$$$(null, Font.BOLD, 12, label3.getFont());
    if (label3Font != null) {
      label3.setFont(label3Font);
    }
    label3.setText("ID:");
    panel8.add(label3);
    Font annotationIdLabelFont = this.$$$getFont$$$("Verdana", -1, -1, annotationIdLabel.getFont());
    if (annotationIdLabelFont != null) {
      annotationIdLabel.setFont(annotationIdLabelFont);
    }
    annotationIdLabel.setText("");
    panel8.add(annotationIdLabel);
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new GridBagLayout());
    header.addTab("Review", panel9);
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel9.add(panel10, gbc);
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
    panel10.add(reviewTabbedPane, gbc);
    final JPanel panel11 = new JPanel();
    panel11.setLayout(new BorderLayout(0, 0));
    reviewTabbedPane.addTab("Text", panel11);
    final JPanel panel12 = new JPanel();
    panel12.setLayout(new GridBagLayout());
    panel11.add(panel12, BorderLayout.NORTH);
    panel12.setBorder(BorderFactory.createTitledBorder(null, "Text", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel12.add(annotationsContainingTextTextField, gbc);
    final JLabel label4 = new JLabel();
    label4.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-search-52.png")));
    label4.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel12.add(label4, gbc);
    previousTextReviewButton = new JButton();
    previousTextReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
    previousTextReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel12.add(previousTextReviewButton, gbc);
    nextTextReviewButton = new JButton();
    nextTextReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
    nextTextReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel12.add(nextTextReviewButton, gbc);
    Font exactMatchCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, exactMatchCheckBox.getFont());
    if (exactMatchCheckBoxFont != null) {
      exactMatchCheckBox.setFont(exactMatchCheckBoxFont);
    }
    this.$$$loadButtonText$$$(exactMatchCheckBox, this.$$$getMessageFromBundle$$$("log4j", "exact.match"));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel12.add(exactMatchCheckBox, gbc);
    refreshTextReviewButton = new JButton();
    refreshTextReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-synchronize-32.png")));
    refreshTextReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel12.add(refreshTextReviewButton, gbc);
    final JScrollPane scrollPane4 = new JScrollPane();
    panel11.add(scrollPane4, BorderLayout.CENTER);
    Font conceptAnnotationsForTextTableFont = this.$$$getFont$$$("Verdana", -1, 12, conceptAnnotationsForTextTable.getFont());
    if (conceptAnnotationsForTextTableFont != null) {
      conceptAnnotationsForTextTable.setFont(conceptAnnotationsForTextTableFont);
    }
    scrollPane4.setViewportView(conceptAnnotationsForTextTable);
    final JPanel panel13 = new JPanel();
    panel13.setLayout(new GridBagLayout());
    reviewTabbedPane.addTab("Concept", panel13);
    final JScrollPane scrollPane5 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel13.add(scrollPane5, gbc);
    Font annotationsForClassTableFont = this.$$$getFont$$$("Verdana", -1, 12, annotationsForClassTable.getFont());
    if (annotationsForClassTableFont != null) {
      annotationsForClassTable.setFont(annotationsForClassTableFont);
    }
    scrollPane5.setViewportView(annotationsForClassTable);
    final JPanel panel14 = new JPanel();
    panel14.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel13.add(panel14, gbc);
    panel14.setBorder(BorderFactory.createTitledBorder(null, "OWL Class", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel14.getFont()), null));
    previousConceptReviewButton = new JButton();
    previousConceptReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
    previousConceptReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel14.add(previousConceptReviewButton, gbc);
    nextConceptReviewButton = new JButton();
    nextConceptReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
    nextConceptReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel14.add(nextConceptReviewButton, gbc);
    owlClassLabel.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel14.add(owlClassLabel, gbc);
    Font includeClassDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includeClassDescendantsCheckBox.getFont());
    if (includeClassDescendantsCheckBoxFont != null) {
      includeClassDescendantsCheckBox.setFont(includeClassDescendantsCheckBoxFont);
    }
    this.$$$loadButtonText$$$(includeClassDescendantsCheckBox, this.$$$getMessageFromBundle$$$("log4j", "include.descendants"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel14.add(includeClassDescendantsCheckBox, gbc);
    refreshConceptReviewButton = new JButton();
    refreshConceptReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-synchronize-32.png")));
    refreshConceptReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel14.add(refreshConceptReviewButton, gbc);
    final JPanel panel15 = new JPanel();
    panel15.setLayout(new GridBagLayout());
    reviewTabbedPane.addTab("Relation", panel15);
    final JScrollPane scrollPane6 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel15.add(scrollPane6, gbc);
    Font relationsForPropertyListFont = this.$$$getFont$$$("Verdana", -1, 12, relationsForPropertyList.getFont());
    if (relationsForPropertyListFont != null) {
      relationsForPropertyList.setFont(relationsForPropertyListFont);
    }
    scrollPane6.setViewportView(relationsForPropertyList);
    final JPanel panel16 = new JPanel();
    panel16.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel15.add(panel16, gbc);
    panel16.setBorder(BorderFactory.createTitledBorder(null, "OWL Object Property", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel16.getFont()), null));
    previousRelationReviewButton = new JButton();
    previousRelationReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
    previousRelationReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel16.add(previousRelationReviewButton, gbc);
    nextRelationReviewButton = new JButton();
    nextRelationReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
    nextRelationReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel16.add(nextRelationReviewButton, gbc);
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
    panel16.add(owlPropertyLabel, gbc);
    Font includePropertyDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includePropertyDescendantsCheckBox.getFont());
    if (includePropertyDescendantsCheckBoxFont != null) {
      includePropertyDescendantsCheckBox.setFont(includePropertyDescendantsCheckBoxFont);
    }
    this.$$$loadButtonText$$$(includePropertyDescendantsCheckBox, this.$$$getMessageFromBundle$$$("log4j", "include.descendants1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel16.add(includePropertyDescendantsCheckBox, gbc);
    refreshRelationReviewButton = new JButton();
    refreshRelationReviewButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-synchronize-32.png")));
    refreshRelationReviewButton.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel16.add(refreshRelationReviewButton, gbc);
    final JPanel panel17 = new JPanel();
    panel17.setLayout(new BorderLayout(0, 0));
    header.addTab("Profile", panel17);
    final JSplitPane splitPane1 = new JSplitPane();
    panel17.add(splitPane1, BorderLayout.CENTER);
    final JPanel panel18 = new JPanel();
    panel18.setLayout(new GridBagLayout());
    splitPane1.setLeftComponent(panel18);
    panel18.setBorder(BorderFactory.createTitledBorder(null, "Profiles", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, panel18.getFont()), null));
    final JScrollPane scrollPane7 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel18.add(scrollPane7, gbc);
    Font profileListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, profileList.getFont());
    if (profileListFont != null) {
      profileList.setFont(profileListFont);
    }
    scrollPane7.setViewportView(profileList);
    final JPanel panel19 = new JPanel();
    panel19.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel18.add(panel19, gbc);
    addProfileButton = new JButton();
    Font addProfileButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, addProfileButton.getFont());
    if (addProfileButtonFont != null) {
      addProfileButton.setFont(addProfileButtonFont);
    }
    addProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addProfileButton.setText("");
    panel19.add(addProfileButton);
    removeProfileButton = new JButton();
    removeProfileButton.setEnabled(true);
    Font removeProfileButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeProfileButton.getFont());
    if (removeProfileButtonFont != null) {
      removeProfileButton.setFont(removeProfileButtonFont);
    }
    removeProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeProfileButton.setText("");
    panel19.add(removeProfileButton);
    iaaPane = new JPanel();
    iaaPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel18.add(iaaPane, gbc);
    iaaPane.setBorder(BorderFactory.createTitledBorder(null, "Inter-Annotator Agreement", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Verdana", -1, 14, iaaPane.getFont()), null));
    iaaSpanCheckBox = new JCheckBox();
    Font iaaSpanCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, iaaSpanCheckBox.getFont());
    if (iaaSpanCheckBoxFont != null) {
      iaaSpanCheckBox.setFont(iaaSpanCheckBoxFont);
    }
    this.$$$loadButtonText$$$(iaaSpanCheckBox, this.$$$getMessageFromBundle$$$("ui", "span1"));
    iaaPane.add(iaaSpanCheckBox);
    iaaClassAndSpanCheckBox = new JCheckBox();
    Font iaaClassAndSpanCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, iaaClassAndSpanCheckBox.getFont());
    if (iaaClassAndSpanCheckBoxFont != null) {
      iaaClassAndSpanCheckBox.setFont(iaaClassAndSpanCheckBoxFont);
    }
    this.$$$loadButtonText$$$(iaaClassAndSpanCheckBox, this.$$$getMessageFromBundle$$$("ui", "class.and.span"));
    iaaPane.add(iaaClassAndSpanCheckBox);
    iaaClassCheckBox = new JCheckBox();
    Font iaaClassCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, iaaClassCheckBox.getFont());
    if (iaaClassCheckBoxFont != null) {
      iaaClassCheckBox.setFont(iaaClassCheckBoxFont);
    }
    this.$$$loadButtonText$$$(iaaClassCheckBox, this.$$$getMessageFromBundle$$$("log4j", "class1"));
    iaaPane.add(iaaClassCheckBox);
    runIaaButton = new JButton();
    Font runIaaButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, runIaaButton.getFont());
    if (runIaaButtonFont != null) {
      runIaaButton.setFont(runIaaButtonFont);
    }
    this.$$$loadButtonText$$$(runIaaButton, this.$$$getMessageFromBundle$$$("log4j", "run.iaa"));
    iaaPane.add(runIaaButton);
    final JPanel panel20 = new JPanel();
    panel20.setLayout(new BorderLayout(0, 0));
    splitPane1.setRightComponent(panel20);
    panel20.setBorder(BorderFactory.createTitledBorder(null, "Colors", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 14, panel20.getFont()), null));
    final JScrollPane scrollPane8 = new JScrollPane();
    panel20.add(scrollPane8, BorderLayout.CENTER);
    Font colorListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, colorList.getFont());
    if (colorListFont != null) {
      colorList.setFont(colorListFont);
    }
    colorList.setToolTipText("Click to set OWL Class color");
    scrollPane8.setViewportView(colorList);
    final JPanel panel21 = new JPanel();
    panel21.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    panel20.add(panel21, BorderLayout.NORTH);
    final JPanel panel22 = new JPanel();
    panel22.setLayout(new GridBagLayout());
    header.addTab("Settings", panel22);
    oneClickGraphsCheckBox = new JCheckBox();
    oneClickGraphsCheckBox.setText("One Click Graphs");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel22.add(oneClickGraphsCheckBox, gbc);
    snapToWordsCheckBox = new JCheckBox();
    snapToWordsCheckBox.setSelected(true);
    snapToWordsCheckBox.setText("Snap to words");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel22.add(snapToWordsCheckBox, gbc);
    final JPanel panel23 = new JPanel();
    panel23.setLayout(new GridBagLayout());
    header.addTab("File", panel23);
    final JPanel panel24 = new JPanel();
    panel24.setLayout(new BorderLayout(0, 0));
    body.setLeftComponent(panel24);
    final JScrollPane scrollPane9 = new JScrollPane();
    panel24.add(scrollPane9, BorderLayout.CENTER);
    textPane.setMinimumSize(new Dimension(200, 22));
    textPane.setPreferredSize(new Dimension(500, 500));
    textPane.setText("");
    scrollPane9.setViewportView(textPane);
    final JPanel panel25 = new JPanel();
    panel25.setLayout(new GridBagLayout());
    panel24.add(panel25, BorderLayout.SOUTH);
    final JPanel panel26 = new JPanel();
    panel26.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel25.add(panel26, gbc);
    profileFilterCheckBox = new JCheckBox();
    Font profileFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, profileFilterCheckBox.getFont());
    if (profileFilterCheckBoxFont != null) {
      profileFilterCheckBox.setFont(profileFilterCheckBoxFont);
    }
    profileFilterCheckBox.setText("Current Profile");
    panel26.add(profileFilterCheckBox);
    owlClassFilterCheckBox = new JCheckBox();
    Font owlClassFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlClassFilterCheckBox.getFont());
    if (owlClassFilterCheckBoxFont != null) {
      owlClassFilterCheckBox.setFont(owlClassFilterCheckBoxFont);
    }
    owlClassFilterCheckBox.setText("Current OWL Class");
    panel26.add(owlClassFilterCheckBox);
    captureImageButton = new JButton();
    captureImageButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-unsplash-32.png")));
    captureImageButton.setText("");
    panel26.add(captureImageButton);
    showGraphViewerButton = new JButton();
    Font showGraphViewerButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, showGraphViewerButton.getFont());
    if (showGraphViewerButtonFont != null) {
      showGraphViewerButton.setFont(showGraphViewerButtonFont);
    }
    showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
    showGraphViewerButton.setText("");
    panel26.add(showGraphViewerButton);
    final JPanel panel27 = new JPanel();
    panel27.setLayout(new GridBagLayout());
    panel26.add(panel27);
    panel27.setBorder(BorderFactory.createTitledBorder(null, "Font Size", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
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
    panel27.add(fontSizeSlider, gbc);
    final JPanel panel28 = new JPanel();
    panel28.setLayout(new BorderLayout(0, 0));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    panel25.add(panel28, gbc);
    final JPanel panel29 = new JPanel();
    panel29.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel28.add(panel29, BorderLayout.WEST);
    addTextSourceButton = new JButton();
    addTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addTextSourceButton.setText("");
    panel29.add(addTextSourceButton);
    removeTextSourceButton = new JButton();
    removeTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeTextSourceButton.setText("");
    panel29.add(removeTextSourceButton);
    final JPanel panel30 = new JPanel();
    panel30.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel28.add(panel30, BorderLayout.EAST);
    previousTextSourceButton = new JButton();
    previousTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousTextSourceButton.setText("");
    panel30.add(previousTextSourceButton);
    nextTextSourceButton = new JButton();
    nextTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextTextSourceButton.setText("");
    panel30.add(nextTextSourceButton);
    textSourceChooser.setPreferredSize(new Dimension(150, 24));
    panel30.add(textSourceChooser);
    final JPanel panel31 = new JPanel();
    panel31.setLayout(new BorderLayout(0, 0));
    panel24.add(panel31, BorderLayout.NORTH);
    final JPanel panel32 = new JPanel();
    panel32.setLayout(new BorderLayout(0, 0));
    panel31.add(panel32, BorderLayout.CENTER);
    final JPanel panel33 = new JPanel();
    panel33.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel32.add(panel33, BorderLayout.WEST);
    panel33.setBorder(BorderFactory.createTitledBorder(null, "Annotation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, 14, panel33.getFont()), null));
    previousSpanButton = new JButton();
    previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
    previousSpanButton.setText("");
    panel33.add(previousSpanButton);
    nextSpanButton = new JButton();
    nextSpanButton.setHorizontalAlignment(0);
    nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
    nextSpanButton.setText("");
    panel33.add(nextSpanButton);
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
    panel33.add(addAnnotationButton);
    removeAnnotationButton = new JButton();
    Font removeAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeAnnotationButton.getFont());
    if (removeAnnotationButtonFont != null) {
      removeAnnotationButton.setFont(removeAnnotationButtonFont);
    }
    removeAnnotationButton.setHorizontalTextPosition(0);
    removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeAnnotationButton.setText("");
    removeAnnotationButton.setVerticalTextPosition(3);
    panel33.add(removeAnnotationButton);
    final JPanel panel34 = new JPanel();
    panel34.setLayout(new GridBagLayout());
    panel32.add(panel34, BorderLayout.CENTER);
    Font searchTextFieldFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, searchTextField.getFont());
    if (searchTextFieldFont != null) {
      searchTextField.setFont(searchTextFieldFont);
    }
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel34.add(searchTextField, gbc);
    final JLabel label5 = new JLabel();
    label5.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-search-52.png")));
    label5.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel34.add(label5, gbc);
    final JPanel panel35 = new JPanel();
    panel35.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    panel34.add(panel35, gbc);
    Font onlyAnnotationsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, onlyAnnotationsCheckBox.getFont());
    if (onlyAnnotationsCheckBoxFont != null) {
      onlyAnnotationsCheckBox.setFont(onlyAnnotationsCheckBoxFont);
    }
    onlyAnnotationsCheckBox.setText("Only in Annotations");
    panel35.add(onlyAnnotationsCheckBox);
    Font regexCheckBoxFont = this.$$$getFont$$$(null, Font.BOLD, 14, regexCheckBox.getFont());
    if (regexCheckBoxFont != null) {
      regexCheckBox.setFont(regexCheckBoxFont);
    }
    regexCheckBox.setText(".*");
    panel35.add(regexCheckBox);
    Font caseSensitiveCheckBoxFont = this.$$$getFont$$$(null, Font.BOLD, 12, caseSensitiveCheckBox.getFont());
    if (caseSensitiveCheckBoxFont != null) {
      caseSensitiveCheckBox.setFont(caseSensitiveCheckBoxFont);
    }
    caseSensitiveCheckBox.setText("Aa");
    panel35.add(caseSensitiveCheckBox);
    findTextInOntologyButton = new JButton();
    Font findTextInOntologyButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, findTextInOntologyButton.getFont());
    if (findTextInOntologyButtonFont != null) {
      findTextInOntologyButton.setFont(findTextInOntologyButtonFont);
    }
    this.$$$loadButtonText$$$(findTextInOntologyButton, this.$$$getMessageFromBundle$$$("log4j", "find.in.ontology1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    panel34.add(findTextInOntologyButton, gbc);
    final JPanel panel36 = new JPanel();
    panel36.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    panel34.add(panel36, gbc);
    previousMatchButton = new JButton();
    Font previousMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, previousMatchButton.getFont());
    if (previousMatchButtonFont != null) {
      previousMatchButton.setFont(previousMatchButtonFont);
    }
    previousMatchButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
    previousMatchButton.setText("");
    panel36.add(previousMatchButton);
    nextMatchButton = new JButton();
    Font nextMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, nextMatchButton.getFont());
    if (nextMatchButtonFont != null) {
      nextMatchButton.setFont(nextMatchButtonFont);
    }
    nextMatchButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
    nextMatchButton.setText("");
    panel36.add(nextMatchButton);
    final JPanel panel37 = new JPanel();
    panel37.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    panel31.add(panel37, BorderLayout.NORTH);
    undoButton = new JButton();
    undoButton.setHorizontalAlignment(0);
    undoButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-undo-52.png")));
    undoButton.setText("");
    undoButton.setToolTipText("Undo");
    panel37.add(undoButton);
    redoButton = new JButton();
    redoButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-redo-52.png")));
    redoButton.setText("");
    redoButton.setToolTipText("Redo");
    panel37.add(redoButton);
    filePanel = new JPanel();
    filePanel.setLayout(new BorderLayout(0, 0));
    cardPanel.add(filePanel, "File");
    final JScrollPane scrollPane10 = new JScrollPane();
    filePanel.add(scrollPane10, BorderLayout.CENTER);
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
    final JPanel panel38 = new JPanel();
    panel38.setLayout(new BorderLayout(0, 0));
    filePanel.add(panel38, BorderLayout.NORTH);
    backButton = new JButton();
    backButton.setText("Back");
    panel38.add(backButton, BorderLayout.EAST);
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    panel38.add(progressBar, BorderLayout.CENTER);
    label5.setLabelFor(searchTextField);
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

  private static Method $$$cachedGetBundleMethod$$$ = null;

  private String $$$getMessageFromBundle$$$(String path, String key) {
    ResourceBundle bundle;
    try {
      Class<?> thisClass = this.getClass();
      if ($$$cachedGetBundleMethod$$$ == null) {
        Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
        $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
      }
      bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
    } catch (Exception e) {
      bundle = ResourceBundle.getBundle(path);
    }
    return bundle.getString(key);
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
