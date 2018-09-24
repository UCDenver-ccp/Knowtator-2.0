package edu.ucdenver.ccp.knowtator.view;

import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.owl.OWLClassSelectionListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.view.chooser.ProfileChooser;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.menu.ProjectMenu;
import edu.ucdenver.ccp.knowtator.view.text.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.view.text.concept.InfoPane;
import edu.ucdenver.ccp.knowtator.view.text.graph.GraphViewDialog;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener, KnowtatorViewComponent {

    private static final Logger log = Logger.getLogger(KnowtatorView.class);
    private final Preferences preferences = Preferences.userRoot().node("knowtator");
    private KnowtatorController controller;
    private GraphViewDialog graphViewDialog;
    private ProjectMenu projectMenu;
    private JComponent panel1;
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
    private TextSourceChooser textSourceChooser;
    private ProfileChooser profileChooser;
    private JButton findTextButton;
    private InfoPane infoPane;
    private JButton addProfileButton;
    private JButton button4;
    private JButton button6;
    private JButton openProjectButton;
    private JCheckBox classFilterCheckBox;
    private JButton removeProfileButton;
    private JButton addTextSourceButton;
    private JButton removeTextSourceButton;
    private JPanel textSourceChooserPanel;
    private JPanel profilePanel;
    private JPanel spanPanel;
    private JPanel textSizePanel;
    private JPanel textSourcePanel;
    private JPanel annotationPanel;

    private Map<JButton, ActionListener> textSourceButtons;
    private Map<JCheckBox, ItemListener> textSourceCheckBoxes;
    private Map<JButton, ActionListener> annotationButtons;
    private Map<JButton, ActionListener> spanButtons;
    private Map<JButton, ActionListener> spanSizeButtons;
    private Map<JButton, ActionListener> selectionSizeButtons;

    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
    private SpanCollectionListener spanCollectionListener;
    private HashMap<JButton, ActionListener> profileButtons;


    public KnowtatorView() {
        controller = new KnowtatorController();

        $$$setupUI$$$();

        makeButtons();

        // This is necessary to force OSGI to load the mxGraphTransferable class to allow node dragging.
        // It is kind of a hacky fix, but it works for now.

        log.warn(
                "Don't worry about the following exception. Just forcing loading of a class needed by mxGraph");
        try {
            mxGraphTransferable.dataFlavor =
                    new DataFlavor(
                            DataFlavor.javaJVMLocalObjectMimeType
                                    + "; class=com.mxgraph.swing.util.mxGraphTransferable",
                            null,
                            mxGraphTransferable.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        setUpOWL();
    }

    private Preferences getPreferences() {
        return preferences;
    }

    private void setUpOWL() {
        OWLWorkspace workspace = null;

        try {
            workspace = controller.getOWLManager().getWorkSpace();
        } catch (OWLWorkSpaceNotSetException ignored) {


        }
        if (workspace == null) {
            if (getOWLWorkspace() != null) {
                controller.getOWLManager().setUpOWL(getOWLWorkspace());
                getOWLWorkspace().getOWLModelManager().addListener(infoPane.getAnnotationClassLabel());
            }
        }
    }

    public KnowtatorController getController() {
        return controller;
    }

    @Override
    public void initialiseClassView() {
    }

    private void createUIComponents() {
        infoPane = new InfoPane(this);

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        projectMenu = new ProjectMenu(this);
        knowtatorTextPane = new KnowtatorTextPane(this);
        graphViewDialog = new GraphViewDialog(this);

        textSourceChooser = new TextSourceChooser(this);
        profileChooser = new ProfileChooser(this);

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

    private void makeButtons() {
        openProjectButton.addActionListener(e -> {

            String[] options = new String[]{"Load project", "Create new project"};
            int response = JOptionPane.showOptionDialog(this,
                    "Select an option",
                    "Project",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (response == 0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(getPreferences().get("Last Project", null)).getParentFile());
                FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    reset();
                    try {
                        getController().setSaveLocation(fileChooser.getSelectedFile().getParentFile());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    getController().loadProject();

                    getPreferences().put("Last Project", fileChooser.getSelectedFile().getAbsolutePath());

                    try {
                        getPreferences().flush();
                    } catch (BackingStoreException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                String projectName = JOptionPane.showInputDialog("Enter project name");

                if (projectName != null && !projectName.equals("")) {

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select project root");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
                        reset();
                        getController().newProject(projectDirectory);
                    }
                }
            }


        });

        profileButtons = new HashMap<>();
        profileButtons.put(addProfileButton, e -> {
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Profile name", field1,
            };
            int option =
                    JOptionPane.showConfirmDialog(
                            this, message, "Enter profile name", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String annotator = field1.getText();
                this.getController().getProfileCollection().addProfile(annotator);
            }
        });
        profileButtons.put(removeProfileButton, e -> controller.getProfileCollection().removeActiveProfile());

        textSourceButtons = new HashMap<>();
        textSourceButtons.put(showGraphViewerButton, e -> graphViewDialog.setVisible(true));
        textSourceButtons.put(decreaseFontSizeButton, e -> knowtatorTextPane.decreaseFontSize());
        textSourceButtons.put(increaseFontSizeButton, e -> knowtatorTextPane.increaseFindSize());
        textSourceButtons.put(previousTextSourceButton, e -> getController().getTextSourceCollection().selectPrevious());
        textSourceButtons.put(nextTextSourceButton, e -> getController().getTextSourceCollection().selectNext());
        textSourceButtons.put(addTextSourceButton, e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(controller.getTextSourceCollection().getArticlesLocation());

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                controller.getTextSourceCollection().addDocument(fileChooser.getSelectedFile());
            }
        });
        textSourceButtons.put(removeTextSourceButton, e -> {
            //TODO: add remove text source action
        });

        annotationButtons = new HashMap<>();
        annotationButtons.put(addAnnotationButton, e -> {
            String[] buttons = {"Add new concept", "Add span to concept", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            this,
                            "Choose an option",
                            "Add ConceptAnnotation",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            buttons,
                            2);

            switch (response) {
                case 0:
                    getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection()
                            .addSelectedAnnotation();
                    break;
                case 1:
                    getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection()
                            .addSpanToSelectedAnnotation();
                    break;
                case 2:
                    break;
            }
        });
        annotationButtons.put(removeAnnotationButton, e -> {
            if (getController()
                    .getTextSourceCollection().getSelection()
                    .getConceptAnnotationCollection()
                    .getSelection().getSpanCollection()
                    .size()
                    > 1) {
                String[] buttons = {"Remove concept", "Remove span from concept", "Cancel"};
                int response =
                        JOptionPane.showOptionDialog(
                                this,
                                "Choose an option",
                                "Remove ConceptAnnotation",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                buttons,
                                2);

                switch (response) {
                    case 0:
                        getController()
                                .getTextSourceCollection().getSelection()
                                .getConceptAnnotationCollection()
                                .removeSelectedAnnotation();
                        break;
                    case 1:
                        getController()
                                .getTextSourceCollection().getSelection()
                                .getConceptAnnotationCollection()
                                .removeSpanFromSelectedAnnotation();
                        break;
                    case 2:
                        break;
                }
            } else {
                if (JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to remove the selected concept?",
                        "Remove ConceptAnnotation",
                        JOptionPane.YES_NO_OPTION)
                        == JOptionPane.YES_OPTION) {
                    getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection()
                            .removeSelectedAnnotation();
                }
            }
        });
        annotationButtons.put(assignColorToClassButton, e -> {
            OWLEntity owlClass = getController().getOWLManager().getSelectedOWLEntity();
            if (owlClass == null) {
                owlClass =
                        getController()
                                .getTextSourceCollection().getSelection()
                                .getConceptAnnotationCollection()
                                .getSelection()
                                .getOwlClass();
            }
            if (owlClass instanceof OWLClass) {
                Color c = JColorChooser.showDialog(this, "Pick a color for " + owlClass, Color.CYAN);
                if (c != null) {
                    getController().getProfileCollection().getSelection().addColor(owlClass, c);

                    if (JOptionPane.showConfirmDialog(
                            this, "Assign color to descendants of " + owlClass + "?")
                            == JOptionPane.OK_OPTION) {
                        try {
                            Set<OWLClass> descendants =
                                    getController()
                                            .getOWLManager()
                                            .getDescendants((OWLClass) owlClass);

                            for (OWLClass descendant : descendants) {
                                controller.getProfileCollection()
                                        .getSelection()
                                        .addColor(descendant, c);
                            }
                        } catch (OWLWorkSpaceNotSetException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        spanButtons = new HashMap<>();
        spanButtons.put(nextSpanButton, e -> getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getNextSpan());
        spanButtons.put(previousSpanButton, e -> getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getPreviousSpan());

        spanSizeButtons = new HashMap<>();
        spanSizeButtons.put(shrinkSelectionEndButton, e -> getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().shrinkSelectedSpanEnd());
        spanSizeButtons.put(shrinkSelectionStartButton, e -> getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().shrinkSelectedSpanStart());
        spanSizeButtons.put(growSelectionEndButton, e -> getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().growSelectedSpanEnd());
        spanSizeButtons.put(growSelectionStartButton, e -> getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().growSelectedSpanStart());

        selectionSizeButtons = new HashMap<>();
        selectionSizeButtons.put(growSelectionStartButton, e -> knowtatorTextPane.growStart());
        selectionSizeButtons.put(shrinkSelectionStartButton, e -> knowtatorTextPane.shrinkStart());
        selectionSizeButtons.put(shrinkSelectionEndButton, e -> knowtatorTextPane.shrinkEnd());
        selectionSizeButtons.put(growSelectionEndButton, e -> knowtatorTextPane.growEnd());

        textSourceCheckBoxes = new HashMap<>();
        textSourceCheckBoxes.put(profileFilterCheckBox, e -> controller.getTextSourceCollection().setFilterByProfile(profileFilterCheckBox.isSelected()));
        textSourceCheckBoxes.put(classFilterCheckBox, e -> controller.getTextSourceCollection().setFilterByOWLClass(classFilterCheckBox.isSelected()));

        // Disable
        disableTextSourceButtons();
        disableProfileButtons();

        findTextButton.addActionListener(
                e -> {
                    try {
                        getController()
                                .getOWLManager()
                                .searchForString(knowtatorTextPane.getSelectedText());
                    } catch (OWLWorkSpaceNotSetException e1) {
                        e1.printStackTrace();

                    }
                });
    }

    private void setupListeners() {
        OWLClassSelectionListener owlClassSelectionListener = this::owlEntitySelectionChanged;
        controller.getOWLManager().addOWLEntityListener(owlClassSelectionListener);

        ProfileCollectionListener profileCollectionListener = new ProfileCollectionListener() {
            @Override
            public void updated(Profile updatedItem) {

            }

            @Override
            public void noSelection(Profile previousSelection) {

            }

            @Override
            public void selected(Profile previousSelection, Profile currentSelection) {

            }

            @Override
            public void added(Profile addedObject) {

            }

            @Override
            public void removed(Profile removedObject) {

            }

            @Override
            public void emptied(Profile object) {
                disableProfileButtons();
            }

            @Override
            public void firstAdded(Profile object) {
                enableProfileButtons();
            }
        };

        controller.getProfileCollection().addCollectionListener(profileCollectionListener);

        TextSourceCollectionListener textSourceCollectionListener = new TextSourceCollectionListener() {
            @Override
            public void updated(TextSource updatedItem) {

            }

            @Override
            public void noSelection(TextSource previousSelection) {
                previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
            }

            @Override
            public void selected(TextSource previousSelection, TextSource currentSelection) {
                if (previousSelection != null) {
                    previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
                }
                currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
            }

            @Override
            public void added(TextSource object) {

            }

            @Override
            public void removed(TextSource removedObject) {

            }

            @Override
            public void emptied(TextSource object) {
                disableTextSourceButtons();
            }

            @Override
            public void firstAdded(TextSource object) {
                enableTextSourceButtons();
            }
        };
        conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
            @Override
            public void updated(ConceptAnnotation updatedItem) {

            }

            @Override
            public void noSelection(ConceptAnnotation previousSelection) {
                previousSelection.getSpanCollection().removeCollectionListener(spanCollectionListener);
            }

            @Override
            public void selected(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
                if (previousSelection != null) {
                    previousSelection.getSpanCollection().removeCollectionListener(spanCollectionListener);
                }
                currentSelection.getSpanCollection().addCollectionListener(spanCollectionListener);
            }

            @Override
            public void added(ConceptAnnotation addedObject) {

            }

            @Override
            public void removed(ConceptAnnotation removedObject) {

            }

            @Override
            public void emptied(ConceptAnnotation object) {
                disableAnnotationButtons();
            }

            @Override
            public void firstAdded(ConceptAnnotation object) {
                enableAnnotationButtons();
            }
        };

        spanCollectionListener = new SpanCollectionListener() {
            @Override
            public void updated(Span updatedItem) {

            }

            @Override
            public void noSelection(Span previousSelection) {
            }

            @Override
            public void selected(Span previousSelection, Span currentSelection) {
            }

            @Override
            public void added(Span addedObject) {
            }

            @Override
            public void removed(Span removedObject) {
            }

            @Override
            public void emptied(Span object) {
                disableSpanButtons();
            }

            @Override
            public void firstAdded(Span object) {
                enableSpanButtons();
            }
        };

        controller.getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
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
        if (controller != null) {
            setUpOWL();
            controller.getOWLManager().setSelectedOWLEntity(selectedClass);
        }
        return selectedClass;
    }

    @Override
    public void reset() {
        disposeView();
        setupListeners();
        knowtatorTextPane.reset();
        infoPane.reset();
        textSourceChooser.reset();
        profileChooser.reset();
        graphViewDialog.reset();
        projectMenu.reset();
    }

    @Override
    public void disposeView() {
        controller.dispose();
        graphViewDialog.setVisible(false);
        graphViewDialog.dispose();
        infoPane.dispose();

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

    private void enableProfileButtons() {
        profileButtons.forEach((button, e) -> button.setEnabled(true));
        profileButtons.forEach(AbstractButton::addActionListener);
    }

    private void disableProfileButtons() {
        profileButtons.forEach((button, e) -> button.setEnabled(false));
        profileButtons.forEach(AbstractButton::removeActionListener);
    }

    private void enableTextSourceButtons() {
        textSourceButtons.forEach((button, e) -> button.setEnabled(true));
        textSourceButtons.forEach(AbstractButton::addActionListener);
        textSourceCheckBoxes.forEach((checkBox, e) -> checkBox.setEnabled(true));
        textSourceCheckBoxes.forEach(AbstractButton::addItemListener);
    }

    private void disableTextSourceButtons() {
        textSourceButtons.forEach((button, e) -> button.setEnabled(false));
        textSourceButtons.forEach(AbstractButton::removeActionListener);
        textSourceCheckBoxes.forEach((checkBox, e) -> checkBox.setEnabled(false));
        textSourceCheckBoxes.forEach(AbstractButton::removeItemListener);
        disableAnnotationButtons();
    }

    private void enableAnnotationButtons() {
        annotationButtons.forEach((button, e) -> button.setEnabled(true));
        annotationButtons.forEach(AbstractButton::addActionListener);
    }

    private void disableAnnotationButtons() {
        annotationButtons.forEach((button, e) -> button.setEnabled(false));
        annotationButtons.forEach(AbstractButton::removeActionListener);
        disableSpanButtons();
    }

    private void enableSpanButtons() {
        spanButtons.forEach((button, e) -> button.setEnabled(true));
        selectionSizeButtons.forEach(AbstractButton::removeActionListener);

        spanSizeButtons.forEach(AbstractButton::removeActionListener);
        spanSizeButtons.forEach(AbstractButton::addActionListener);

        spanButtons.forEach(AbstractButton::removeActionListener);
        spanButtons.forEach(AbstractButton::addActionListener);

    }

    private void disableSpanButtons() {
        spanButtons.forEach((button, e) -> button.setEnabled(false));
        spanSizeButtons.forEach(AbstractButton::removeActionListener);
        selectionSizeButtons.forEach(AbstractButton::removeActionListener);
        selectionSizeButtons.forEach(AbstractButton::addActionListener);
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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setPreferredSize(new Dimension(672, 182));
        panel1.add(panel2, BorderLayout.NORTH);
        showGraphViewerButton = new JButton();
        showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
        showGraphViewerButton.setText("");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel2.add(showGraphViewerButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        classFilterCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(classFilterCheckBox, ResourceBundle.getBundle("log4j").getString("owl.class.filter1"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(classFilterCheckBox, gbc);
        profileFilterCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(profileFilterCheckBox, ResourceBundle.getBundle("log4j").getString("profile.filter"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(profileFilterCheckBox, gbc);
        openProjectButton = new JButton();
        this.$$$loadButtonText$$$(openProjectButton, ResourceBundle.getBundle("log4j").getString("open.project"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel2.add(openProjectButton, gbc);
        profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(profilePanel, gbc);
        profilePanel.add(profileChooser, BorderLayout.CENTER);
        button4 = new JButton();
        button4.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
        button4.setText("");
        profilePanel.add(button4, BorderLayout.SOUTH);
        button6 = new JButton();
        button6.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
        button6.setText("");
        profilePanel.add(button6, BorderLayout.NORTH);
        addProfileButton = new JButton();
        addProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addProfileButton.setText("");
        profilePanel.add(addProfileButton, BorderLayout.WEST);
        removeProfileButton = new JButton();
        removeProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeProfileButton.setText("");
        profilePanel.add(removeProfileButton, BorderLayout.EAST);
        textSourcePanel = new JPanel();
        textSourcePanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(textSourcePanel, gbc);
        textSourceChooserPanel = new JPanel();
        textSourceChooserPanel.setLayout(new BorderLayout(0, 0));
        textSourcePanel.add(textSourceChooserPanel, BorderLayout.CENTER);
        textSourceChooserPanel.add(textSourceChooser, BorderLayout.CENTER);
        previousTextSourceButton = new JButton();
        previousTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
        previousTextSourceButton.setText("");
        textSourceChooserPanel.add(previousTextSourceButton, BorderLayout.NORTH);
        nextTextSourceButton = new JButton();
        nextTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
        nextTextSourceButton.setMinimumSize(new Dimension(30, 30));
        nextTextSourceButton.setPreferredSize(new Dimension(30, 30));
        nextTextSourceButton.setText("");
        textSourceChooserPanel.add(nextTextSourceButton, BorderLayout.SOUTH);
        addTextSourceButton = new JButton();
        addTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addTextSourceButton.setText("");
        textSourceChooserPanel.add(addTextSourceButton, BorderLayout.WEST);
        removeTextSourceButton = new JButton();
        removeTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeTextSourceButton.setText("");
        textSourceChooserPanel.add(removeTextSourceButton, BorderLayout.EAST);
        textSizePanel = new JPanel();
        textSizePanel.setLayout(new BorderLayout(0, 0));
        textSourcePanel.add(textSizePanel, BorderLayout.WEST);
        increaseFontSizeButton = new JButton();
        increaseFontSizeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-Increase Font.png")));
        increaseFontSizeButton.setText("");
        textSizePanel.add(increaseFontSizeButton, BorderLayout.NORTH);
        decreaseFontSizeButton = new JButton();
        decreaseFontSizeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-Decrease Font (Custom).png")));
        decreaseFontSizeButton.setText("");
        textSizePanel.add(decreaseFontSizeButton, BorderLayout.SOUTH);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel4, gbc);
        previousSpanButton = new JButton();
        previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
        previousSpanButton.setText("");
        panel4.add(previousSpanButton, BorderLayout.WEST);
        nextSpanButton = new JButton();
        nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
        nextSpanButton.setText("");
        panel4.add(nextSpanButton, BorderLayout.EAST);
        spanPanel = new JPanel();
        spanPanel.setLayout(new BorderLayout(0, 0));
        panel4.add(spanPanel, BorderLayout.CENTER);
        annotationPanel = new JPanel();
        annotationPanel.setLayout(new BorderLayout(0, 0));
        spanPanel.add(annotationPanel, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        annotationPanel.add(panel5, BorderLayout.EAST);
        growSelectionEndButton = new JButton();
        growSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32.png")));
        growSelectionEndButton.setText("");
        panel5.add(growSelectionEndButton, BorderLayout.EAST);
        shrinkSelectionEndButton = new JButton();
        shrinkSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32 (reversed).png")));
        shrinkSelectionEndButton.setText("");
        panel5.add(shrinkSelectionEndButton, BorderLayout.WEST);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        annotationPanel.add(panel6, BorderLayout.WEST);
        shrinkSelectionStartButton = new JButton();
        shrinkSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32.png")));
        shrinkSelectionStartButton.setText("");
        panel6.add(shrinkSelectionStartButton, BorderLayout.CENTER);
        growSelectionStartButton = new JButton();
        growSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32 (reversed).png")));
        growSelectionStartButton.setText("");
        panel6.add(growSelectionStartButton, BorderLayout.WEST);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        annotationPanel.add(panel7, BorderLayout.CENTER);
        addAnnotationButton = new JButton();
        addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addAnnotationButton.setText("");
        panel7.add(addAnnotationButton, BorderLayout.NORTH);
        removeAnnotationButton = new JButton();
        removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeAnnotationButton.setText("");
        panel7.add(removeAnnotationButton, BorderLayout.SOUTH);
        assignColorToClassButton = new JButton();
        assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
        assignColorToClassButton.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel2.add(assignColorToClassButton, gbc);
        findTextButton = new JButton();
        this.$$$loadButtonText$$$(findTextButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology1"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel2.add(findTextButton, gbc);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setContinuousLayout(false);
        splitPane1.setDividerLocation(478);
        panel1.add(splitPane1, BorderLayout.CENTER);
        splitPane1.setRightComponent(infoPane.$$$getRootComponent$$$());
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane1.setLeftComponent(scrollPane1);
        knowtatorTextPane.setEditable(false);
        scrollPane1.setViewportView(knowtatorTextPane);
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
