package edu.ucdenver.ccp.knowtator.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.selection.OWLClassSelectionListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.chooser.ProfileChooser;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.ProjectMenu;
import edu.ucdenver.ccp.knowtator.view.text.InfoPane;
import edu.ucdenver.ccp.knowtator.view.text.textpane.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    private static final Logger log = Logger.getLogger(KnowtatorView.class);
    private final Preferences preferences = Preferences.userRoot().node("knowtator");
    private KnowtatorController controller;
    private GraphViewDialog graphViewDialog;
    private JMenu projectMenu;
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
    private JPanel textPanel;
    private JToolBar textSourceToolBar;
    private JToolBar annotationToolBar;
    private InfoPane infoPane;
    private JCheckBox classFilterCheckBox;

    private Map<JButton, ActionListener> textSourceButtons;
    private Map<JCheckBox, ItemListener> textSourceCheckBoxes;
    private Map<JButton, ActionListener> annotationButtons;
    private Map<JButton, ActionListener> spanButtons;
    private Map<JButton, ActionListener> spanSizeButtons;
    private Map<JButton, ActionListener> selectionSizeButtons;

    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
    private SpanCollectionListener spanCollectionListener;


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

    public Preferences getPreferences() {
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

        panel1 = this;
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
        textSourceButtons = new HashMap<>();
        textSourceButtons.put(showGraphViewerButton, e -> graphViewDialog.setVisible(true));
        textSourceButtons.put(decreaseFontSizeButton, e -> knowtatorTextPane.decreaseFontSize());
        textSourceButtons.put(increaseFontSizeButton, e -> knowtatorTextPane.increaseFindSize());
        textSourceButtons.put(previousTextSourceButton, e -> getController().getTextSourceCollection().selectPrevious());
        textSourceButtons.put(nextTextSourceButton, e -> getController().getTextSourceCollection().selectNext());

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
            OWLEntity owlClass = getController().getSelectionManager().getSelectedOWLEntity();
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
                        } catch (OWLWorkSpaceNotSetException ignored) {
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
        textSourceCheckBoxes.put(profileFilterCheckBox, e -> controller.getSelectionManager().setFilterByProfile(profileFilterCheckBox.isSelected()));
        textSourceCheckBoxes.put(classFilterCheckBox, e -> controller.getSelectionManager().setFilterByOWLClass(classFilterCheckBox.isSelected()));

        // Disable
        disableTextSourceButtons();

        findTextButton.addActionListener(
                e -> {
                    try {
                        getController()
                                .getOWLManager()
                                .searchForString(knowtatorTextPane.getSelectedText());
                    } catch (OWLWorkSpaceNotSetException ignored) {

                    }
                });

        profileChooser.addActionListener(
                e -> {
                    JComboBox comboBox = (JComboBox) e.getSource();
                    if (comboBox.getSelectedItem() != null) {
                        getController()
                                .getProfileCollection().setSelection((Profile) comboBox.getSelectedItem());
                    }
                });
    }

    private void setupListeners() {
        OWLClassSelectionListener owlClassSelectionListener = this::owlEntitySelectionChanged;
        controller.getSelectionManager().addOWLEntityListener(owlClassSelectionListener);

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
            controller.getSelectionManager().setSelectedOWLEntity(selectedClass);
        }
        return selectedClass;
    }

    public void reset() {
        disposeView();
        setupListeners();
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
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setMinimumSize(new Dimension(450, 375));
        panel1.setPreferredSize(new Dimension(900, 800));
        final JMenuBar menuBar1 = new JMenuBar();
        menuBar1.setLayout(new BorderLayout(0, 0));
        menuBar1.setMaximumSize(new Dimension(2147483647, 25));
        menuBar1.setMinimumSize(new Dimension(-1, -1));
        menuBar1.setPreferredSize(new Dimension(900, 25));
        panel1.add(menuBar1, BorderLayout.NORTH);
        projectMenu.setMaximumSize(new Dimension(120, 20));
        projectMenu.setMinimumSize(new Dimension(-1, -1));
        projectMenu.setPreferredSize(new Dimension(120, 20));
        projectMenu.setSelected(false);
        this.$$$loadButtonText$$$(projectMenu, ResourceBundle.getBundle("ui").getString("knowator.project"));
        menuBar1.add(projectMenu, BorderLayout.WEST);
        textPanel = new JPanel();
        textPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        textPanel.setMinimumSize(new Dimension(400, 350));
        textPanel.setPreferredSize(new Dimension(800, 200));
        panel1.add(textPanel, BorderLayout.CENTER);
        textPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null));
        textSourceToolBar = new JToolBar();
        textSourceToolBar.setFloatable(false);
        textPanel.add(textSourceToolBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(2147483647, 50), 0, false));
        decreaseFontSizeButton = new JButton();
        decreaseFontSizeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-Decrease Font (Custom).png")));
        decreaseFontSizeButton.setMaximumSize(new Dimension(50, 50));
        decreaseFontSizeButton.setMinimumSize(new Dimension(50, 50));
        decreaseFontSizeButton.setPreferredSize(new Dimension(50, 50));
        decreaseFontSizeButton.setText("");
        decreaseFontSizeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("decrease.font.size"));
        textSourceToolBar.add(decreaseFontSizeButton);
        increaseFontSizeButton = new JButton();
        increaseFontSizeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-Increase Font (Custom).png")));
        increaseFontSizeButton.setMaximumSize(new Dimension(50, 50));
        increaseFontSizeButton.setMinimumSize(new Dimension(50, 50));
        increaseFontSizeButton.setPreferredSize(new Dimension(72, 72));
        increaseFontSizeButton.setText("");
        increaseFontSizeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("increase.font.size"));
        textSourceToolBar.add(increaseFontSizeButton);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel2.setMaximumSize(new Dimension(64, 50));
        panel2.setMinimumSize(new Dimension(64, 50));
        panel2.setPreferredSize(new Dimension(64, 50));
        textSourceToolBar.add(panel2);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setMaximumSize(new Dimension(64, 24));
        label1.setMinimumSize(new Dimension(64, 24));
        label1.setPreferredSize(new Dimension(64, 24));
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("ui").getString("document"));
        panel2.add(label1, BorderLayout.CENTER);
        nextTextSourceButton = new JButton();
        nextTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-down-24.png")));
        nextTextSourceButton.setMaximumSize(new Dimension(100, 100));
        nextTextSourceButton.setMinimumSize(new Dimension(16, 16));
        nextTextSourceButton.setPreferredSize(new Dimension(24, 16));
        nextTextSourceButton.setText("");
        nextTextSourceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.text.source"));
        panel2.add(nextTextSourceButton, BorderLayout.SOUTH);
        previousTextSourceButton = new JButton();
        previousTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-sort-up-24.png")));
        previousTextSourceButton.setMaximumSize(new Dimension(100, 100));
        previousTextSourceButton.setMinimumSize(new Dimension(16, 16));
        previousTextSourceButton.setPreferredSize(new Dimension(24, 16));
        previousTextSourceButton.setText("");
        previousTextSourceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.text.source"));
        panel2.add(previousTextSourceButton, BorderLayout.NORTH);
        textSourceChooser.setMaximumSize(new Dimension(200, 50));
        textSourceChooser.setMinimumSize(new Dimension(200, 50));
        textSourceChooser.setPreferredSize(new Dimension(200, 50));
        textSourceToolBar.add(textSourceChooser);
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(0);
        label2.setHorizontalTextPosition(0);
        label2.setMaximumSize(new Dimension(50, 50));
        label2.setMinimumSize(new Dimension(50, 50));
        label2.setPreferredSize(new Dimension(50, 50));
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("ui").getString("profile"));
        textSourceToolBar.add(label2);
        profileChooser.setMaximumSize(new Dimension(200, 50));
        profileChooser.setMinimumSize(new Dimension(200, 50));
        profileChooser.setPreferredSize(new Dimension(200, 50));
        textSourceToolBar.add(profileChooser);
        annotationToolBar = new JToolBar();
        annotationToolBar.setFloatable(false);
        textPanel.add(annotationToolBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(2147483647, 50), 0, false));
        addAnnotationButton = new JButton();
        addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addAnnotationButton.setMaximumSize(new Dimension(50, 50));
        addAnnotationButton.setMinimumSize(new Dimension(50, 50));
        addAnnotationButton.setPreferredSize(new Dimension(50, 50));
        addAnnotationButton.setText("");
        addAnnotationButton.setToolTipText(ResourceBundle.getBundle("ui").getString("add.conceptAnnotation"));
        annotationToolBar.add(addAnnotationButton);
        removeAnnotationButton = new JButton();
        removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeAnnotationButton.setMaximumSize(new Dimension(50, 50));
        removeAnnotationButton.setMinimumSize(new Dimension(50, 50));
        removeAnnotationButton.setPreferredSize(new Dimension(50, 50));
        removeAnnotationButton.setText("");
        removeAnnotationButton.setToolTipText(ResourceBundle.getBundle("ui").getString("remove.conceptAnnotation"));
        annotationToolBar.add(removeAnnotationButton);
        previousSpanButton = new JButton();
        previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
        previousSpanButton.setMaximumSize(new Dimension(50, 50));
        previousSpanButton.setMinimumSize(new Dimension(50, 50));
        previousSpanButton.setPreferredSize(new Dimension(50, 50));
        previousSpanButton.setText("");
        previousSpanButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.span"));
        annotationToolBar.add(previousSpanButton);
        growSelectionStartButton = new JButton();
        growSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32 (reversed).png")));
        growSelectionStartButton.setMaximumSize(new Dimension(50, 50));
        growSelectionStartButton.setMinimumSize(new Dimension(72, 72));
        growSelectionStartButton.setPreferredSize(new Dimension(50, 50));
        growSelectionStartButton.setText("");
        growSelectionStartButton.setToolTipText(ResourceBundle.getBundle("ui").getString("grow.selection.start"));
        annotationToolBar.add(growSelectionStartButton);
        shrinkSelectionStartButton = new JButton();
        shrinkSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32.png")));
        shrinkSelectionStartButton.setMaximumSize(new Dimension(50, 50));
        shrinkSelectionStartButton.setMinimumSize(new Dimension(50, 50));
        shrinkSelectionStartButton.setPreferredSize(new Dimension(50, 50));
        shrinkSelectionStartButton.setText("");
        shrinkSelectionStartButton.setToolTipText(ResourceBundle.getBundle("ui").getString("shrink.selection.start"));
        annotationToolBar.add(shrinkSelectionStartButton);
        shrinkSelectionEndButton = new JButton();
        shrinkSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32 (reversed).png")));
        shrinkSelectionEndButton.setMaximumSize(new Dimension(50, 50));
        shrinkSelectionEndButton.setMinimumSize(new Dimension(50, 50));
        shrinkSelectionEndButton.setPreferredSize(new Dimension(50, 50));
        shrinkSelectionEndButton.setText("");
        shrinkSelectionEndButton.setToolTipText(ResourceBundle.getBundle("ui").getString("shrink.selection.end"));
        annotationToolBar.add(shrinkSelectionEndButton);
        growSelectionEndButton = new JButton();
        growSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32.png")));
        growSelectionEndButton.setMaximumSize(new Dimension(50, 50));
        growSelectionEndButton.setMinimumSize(new Dimension(50, 50));
        growSelectionEndButton.setPreferredSize(new Dimension(50, 50));
        growSelectionEndButton.setText("");
        growSelectionEndButton.setToolTipText(ResourceBundle.getBundle("ui").getString("grow.selection.end"));
        annotationToolBar.add(growSelectionEndButton);
        nextSpanButton = new JButton();
        nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
        nextSpanButton.setMaximumSize(new Dimension(50, 50));
        nextSpanButton.setMinimumSize(new Dimension(50, 50));
        nextSpanButton.setPreferredSize(new Dimension(50, 50));
        nextSpanButton.setText("");
        nextSpanButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.span"));
        annotationToolBar.add(nextSpanButton);
        assignColorToClassButton = new JButton();
        assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
        assignColorToClassButton.setMaximumSize(new Dimension(50, 50));
        assignColorToClassButton.setMinimumSize(new Dimension(50, 50));
        assignColorToClassButton.setPreferredSize(new Dimension(50, 50));
        assignColorToClassButton.setText("");
        assignColorToClassButton.setToolTipText(ResourceBundle.getBundle("ui").getString("assign.color.to.class"));
        annotationToolBar.add(assignColorToClassButton);
        profileFilterCheckBox = new JCheckBox();
        profileFilterCheckBox.setMaximumSize(new Dimension(100, 50));
        profileFilterCheckBox.setMinimumSize(new Dimension(100, 50));
        profileFilterCheckBox.setPreferredSize(new Dimension(100, 50));
        this.$$$loadButtonText$$$(profileFilterCheckBox, ResourceBundle.getBundle("ui").getString("profile.filter"));
        profileFilterCheckBox.setToolTipText(ResourceBundle.getBundle("ui").getString("filter.conceptAnnotations.by.profile"));
        annotationToolBar.add(profileFilterCheckBox);
        classFilterCheckBox = new JCheckBox();
        classFilterCheckBox.setText("OWL Class Filter");
        annotationToolBar.add(classFilterCheckBox);
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        textPanel.add(toolBar1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        showGraphViewerButton = new JButton();
        showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
        showGraphViewerButton.setMaximumSize(new Dimension(50, 50));
        showGraphViewerButton.setMinimumSize(new Dimension(50, 50));
        showGraphViewerButton.setPreferredSize(new Dimension(50, 50));
        showGraphViewerButton.setText("");
        showGraphViewerButton.setToolTipText(ResourceBundle.getBundle("ui").getString("show.graph.viewer"));
        toolBar1.add(showGraphViewerButton);
        findTextButton = new JButton();
        findTextButton.setMaximumSize(new Dimension(100, 50));
        findTextButton.setMinimumSize(new Dimension(100, 50));
        findTextButton.setPreferredSize(new Dimension(100, 50));
        this.$$$loadButtonText$$$(findTextButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology"));
        toolBar1.add(findTextButton);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(800);
        textPanel.add(splitPane1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane1.setLeftComponent(scrollPane1);
        scrollPane1.setViewportView(knowtatorTextPane);
        splitPane1.setRightComponent(infoPane.$$$getRootComponent$$$());
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
