package edu.ucdenver.ccp.knowtator.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntitySelectionListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.chooser.ProfileChooser;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import edu.ucdenver.ccp.knowtator.view.text.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.view.text.concept.*;
import edu.ucdenver.ccp.knowtator.view.text.graph.GraphViewDialog;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener, KnowtatorViewComponent {

    private static final Logger log = Logger.getLogger(KnowtatorView.class);
    private final Preferences preferences = Preferences.userRoot().node("knowtator");
    private KnowtatorController controller;
    private GraphViewDialog graphViewDialog;
    private JComponent panel1;
    private JButton showGraphViewerButton;
    private JButton removeAnnotationButton;
    private JButton growSelectionStartButton;
    private JButton shrinkSelectionEndButton;
    private JButton growSelectionEndButton;
    private JButton shrinkSelectionStartButton;
    private JButton addAnnotationButton;
    private JButton previousTextSourceButton;
    private JButton nextTextSourceButton;
    private JButton nextSpanButton;
    private JButton previousSpanButton;
    private JButton assignColorToClassButton;
    private KnowtatorTextPane knowtatorTextPane;
    private TextSourceChooser textSourceChooser;
    private ProfileChooser profileChooser;
    private JButton findTextButton;
    private JButton addTextSourceButton;
    private JButton removeTextSourceButton;
    private JButton menuButton;
    private JButton previousMatchButton;
    private JTextField matchTextField;
    private JButton nextMatchButton;
    private JCheckBox caseSensitiveCheckBox;
    private JCheckBox onlyAnnotationsCheckBox;
    private JSlider fontSizeSlider;
    private SpanList spanList;
    private GraphSpaceList graphSpaceList;
    private AnnotationIDLabel annotationIDLabel;
    private AnnotationAnnotatorLabel annotationAnnotatorLabel;
    private AnnotationClassLabel annotationClassLabel;
    private JPanel infoPane;
    private JPanel searchPanel;
    private JCheckBox regexCheckBox;

    private Map<JButton, ActionListener> textSourceButtons;
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

        log.warn("Don't worry about the following exception. Just forcing loading of a class needed by mxGraph");
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
                getOWLWorkspace().getOWLModelManager().addListener(annotationClassLabel);
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
        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        panel1 = this;

        knowtatorTextPane = new KnowtatorTextPane(this);
        graphViewDialog = new GraphViewDialog(this);

        textSourceChooser = new TextSourceChooser(this);
        profileChooser = new ProfileChooser(this);

        spanList = new SpanList(this);
        graphSpaceList = new GraphSpaceList(this);
        annotationAnnotatorLabel = new AnnotationAnnotatorLabel(this);
        annotationClassLabel = new AnnotationClassLabel(this);
        annotationIDLabel = new AnnotationIDLabel(this);

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
        fontSizeSlider.setValue(knowtatorTextPane.getFont().getSize());

        menuButton.addActionListener(e -> {
            MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(this), this, getFilters());
            menuDialog.pack();
            menuDialog.setVisible(true);
        });

        textSourceButtons = new HashMap<>();
        textSourceButtons.put(showGraphViewerButton, e -> graphViewDialog.setVisible(true));
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

        fontSizeSlider.addChangeListener(e -> knowtatorTextPane.setFontSize(fontSizeSlider.getValue()));

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

        // Disable
        disableTextSourceButtons();

        findTextButton.addActionListener(
                e -> {
                    try {
                        getController()
                                .getOWLManager()
                                .searchForString(matchTextField.getSelectedText());
                    } catch (OWLWorkSpaceNotSetException e1) {
                        e1.printStackTrace();

                    }
                });

        nextMatchButton.addActionListener(
                e -> knowtatorTextPane.search(matchTextField.getText(),
                        caseSensitiveCheckBox.isSelected(),
                        onlyAnnotationsCheckBox.isSelected(), true));
        previousMatchButton.addActionListener(
                e -> knowtatorTextPane.search(matchTextField.getText(),
                        caseSensitiveCheckBox.isSelected(),
                        onlyAnnotationsCheckBox.isSelected(), false));

        graphSpaceList.addListSelectionListener(
                e -> {
                    JComboBox comboBox = (JComboBox) e.getSource();
                    if (comboBox.getSelectedItem() != null
                            && comboBox.getSelectedItem()
                            != controller
                            .getTextSourceCollection().getSelection()
                            .getGraphSpaceCollection().getSelection()) {
                        graphViewDialog.setVisible(true);
                        controller
                                .getTextSourceCollection().getSelection()
                                .getGraphSpaceCollection().setSelection((GraphSpace) comboBox.getSelectedItem());
                    }
                });

        spanList.addListSelectionListener(
                e -> {
                    JList jList = (JList) e.getSource();
                    if (jList.getSelectedValue() != null) {
                        controller
                                .getTextSourceCollection().getSelection()
                                .getConceptAnnotationCollection().getSelection()
                                .getSpanCollection().setSelection((Span) jList.getSelectedValue());
                    }
                });

        knowtatorTextPane.addCaretListener(e -> matchTextField.setText(knowtatorTextPane.getSelectedText()));
    }

    private Map<String, Boolean> getFilters() {
        Map<String, Boolean> filters = new HashMap<>();
        filters.put("owl class", controller.getTextSourceCollection().isFilterByOWLClass());
        filters.put("profile", controller.getTextSourceCollection().isFilterByProfile());
        return filters;
    }

    private void setupListeners() {
        OWLEntitySelectionListener owlEntitySelectionListener = this::owlEntitySelectionChanged;
        controller.getOWLManager().addOWLEntityListener(owlEntitySelectionListener);

        ProfileCollectionListener profileCollectionListener = new ProfileCollectionListener() {
            @Override
            public void updated(Profile updatedItem) {

            }

            @Override
            public void noSelection(Profile previousSelection) {

            }

            @Override
            public void selected(SelectionChangeEvent<Profile> event) {

            }


            @Override
            public void added(AddEvent<Profile> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<Profile> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<Profile> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<Profile> object) {

            }

            @Override
            public void firstAdded(AddEvent<Profile> object) {

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
            public void selected(SelectionChangeEvent<TextSource> event) {
                if (event.getOld() != null) {
                    event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
                }
                event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
            }

            @Override
            public void added(AddEvent<TextSource> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<TextSource> object) {
                disableTextSourceButtons();
            }

            @Override
            public void firstAdded(AddEvent<TextSource> object) {
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
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                if (event.getOld() != null) {
                    event.getOld().getSpanCollection().removeCollectionListener(spanCollectionListener);
                }
                if (event.getNew() != null) {
                    event.getNew().getSpanCollection().addCollectionListener(spanCollectionListener);
                }
            }

            @Override
            public void added(AddEvent<ConceptAnnotation> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<ConceptAnnotation> object) {
                disableAnnotationButtons();
            }

            @Override
            public void firstAdded(AddEvent<ConceptAnnotation> object) {
                enableAnnotationButtons();
            }
        };

        spanCollectionListener = new SpanCollectionListener() {
            @Override
            public void noSelection(Span previousSelection) {

            }

            @Override
            public void selected(SelectionChangeEvent<Span> event) {

            }

            @Override
            public void updated(Span updatedItem) {

            }

            @Override
            public void added(AddEvent<Span> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<Span> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<Span> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<Span> object) {
                disableSpanButtons();
            }

            @Override
            public void firstAdded(AddEvent<Span> object) {
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
        graphSpaceList.reset();
        annotationClassLabel.reset();
        annotationIDLabel.reset();
        annotationAnnotatorLabel.reset();
        spanList.reset();
        textSourceChooser.reset();
        profileChooser.reset();
        graphViewDialog.reset();

        if (getOWLWorkspace() != null) {
            controller.getOWLManager().setUpOWL(getOWLWorkspace());
        }
    }

    @Override
    public void disposeView() {
        controller.dispose();
        graphViewDialog.setVisible(false);
        graphViewDialog.dispose();
        annotationIDLabel.dispose();
        annotationAnnotatorLabel.dispose();
        annotationClassLabel.dispose();
        spanList.dispose();
        graphSpaceList.dispose();

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

    private void enableTextSourceButtons() {
        textSourceButtons.forEach((button, e) -> button.setEnabled(true));
        textSourceButtons.forEach(AbstractButton::addActionListener);
    }

    private void disableTextSourceButtons() {
        textSourceButtons.forEach((button, e) -> button.setEnabled(false));
        textSourceButtons.forEach(AbstractButton::removeActionListener);
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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.setLayout(new BorderLayout(0, 0));
        panel2.add(panel1, BorderLayout.CENTER);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel3.setPreferredSize(new Dimension(672, 150));
        panel1.add(panel3, BorderLayout.NORTH);
        searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayoutManager(3, 8, new Insets(0, 0, 0, 0), -1, -1));
        searchPanel.setAlignmentX(0.0f);
        panel3.add(searchPanel, BorderLayout.CENTER);
        menuButton = new JButton();
        menuButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-menu-24.png")));
        menuButton.setText("");
        menuButton.setVerticalAlignment(0);
        searchPanel.add(menuButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showGraphViewerButton = new JButton();
        showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
        showGraphViewerButton.setText("");
        searchPanel.add(showGraphViewerButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previousMatchButton = new JButton();
        previousMatchButton.setText("Previous");
        searchPanel.add(previousMatchButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        matchTextField = new JTextField();
        searchPanel.add(matchTextField, new GridConstraints(0, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 25), new Dimension(-1, 25), 0, false));
        assignColorToClassButton = new JButton();
        assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
        assignColorToClassButton.setText("");
        searchPanel.add(assignColorToClassButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addAnnotationButton = new JButton();
        addAnnotationButton.setHorizontalTextPosition(0);
        addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addAnnotationButton.setText("");
        addAnnotationButton.setVerticalTextPosition(3);
        searchPanel.add(addAnnotationButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        removeAnnotationButton = new JButton();
        removeAnnotationButton.setHorizontalTextPosition(0);
        removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeAnnotationButton.setText("");
        removeAnnotationButton.setVerticalTextPosition(3);
        searchPanel.add(removeAnnotationButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        onlyAnnotationsCheckBox = new JCheckBox();
        onlyAnnotationsCheckBox.setText("Only in Annotations");
        searchPanel.add(onlyAnnotationsCheckBox, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextMatchButton = new JButton();
        nextMatchButton.setText("Next");
        searchPanel.add(nextMatchButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        findTextButton = new JButton();
        this.$$$loadButtonText$$$(findTextButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology1"));
        searchPanel.add(findTextButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        regexCheckBox = new JCheckBox();
        regexCheckBox.setText("Regex");
        searchPanel.add(regexCheckBox, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        caseSensitiveCheckBox = new JCheckBox();
        caseSensitiveCheckBox.setText("Case Sensitive");
        searchPanel.add(caseSensitiveCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        searchPanel.add(panel4, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        growSelectionStartButton = new JButton();
        growSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32 (reversed).png")));
        growSelectionStartButton.setText("");
        panel4.add(growSelectionStartButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shrinkSelectionStartButton = new JButton();
        shrinkSelectionStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32.png")));
        shrinkSelectionStartButton.setText("");
        panel4.add(shrinkSelectionStartButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shrinkSelectionEndButton = new JButton();
        shrinkSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32 (reversed).png")));
        shrinkSelectionEndButton.setText("");
        panel4.add(shrinkSelectionEndButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        growSelectionEndButton = new JButton();
        growSelectionEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32.png")));
        growSelectionEndButton.setText("");
        panel4.add(growSelectionEndButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        searchPanel.add(spacer1, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, BorderLayout.SOUTH);
        previousTextSourceButton = new JButton();
        previousTextSourceButton.setText("Previous");
        panel5.add(previousTextSourceButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fontSizeSlider = new JSlider();
        fontSizeSlider.setInverted(false);
        fontSizeSlider.setMajorTickSpacing(8);
        fontSizeSlider.setMaximum(28);
        fontSizeSlider.setMinimum(8);
        fontSizeSlider.setMinorTickSpacing(1);
        fontSizeSlider.setSnapToTicks(true);
        fontSizeSlider.setValue(16);
        panel5.add(fontSizeSlider, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel5.add(textSourceChooser, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addTextSourceButton = new JButton();
        addTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addTextSourceButton.setText("");
        panel5.add(addTextSourceButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeTextSourceButton = new JButton();
        removeTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeTextSourceButton.setText("");
        panel5.add(removeTextSourceButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        nextTextSourceButton = new JButton();
        nextTextSourceButton.setText("Next");
        panel5.add(nextTextSourceButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        panel1.add(splitPane1, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setMinimumSize(new Dimension(500, 100));
        scrollPane1.setPreferredSize(new Dimension(500, 100));
        splitPane1.setLeftComponent(scrollPane1);
        knowtatorTextPane.setEditable(false);
        scrollPane1.setViewportView(knowtatorTextPane);
        infoPane = new JPanel();
        infoPane.setLayout(new GridLayoutManager(12, 2, new Insets(0, 0, 0, 0), -1, -1));
        infoPane.setMaximumSize(new Dimension(170, 2147483647));
        splitPane1.setRightComponent(infoPane);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("ID");
        infoPane.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.BOLD, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Annotator");
        infoPane.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, -1, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Class");
        infoPane.add(label3, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.BOLD, 18, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Spans");
        infoPane.add(label4, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        Font label5Font = this.$$$getFont$$$(null, Font.BOLD, 18, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        label5.setText("Graph Spaces");
        infoPane.add(label5, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        infoPane.add(scrollPane2, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setViewportView(spanList);
        final JScrollPane scrollPane3 = new JScrollPane();
        infoPane.add(scrollPane3, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setViewportView(graphSpaceList);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        infoPane.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel spacer3 = new JPanel();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel6.add(spacer4, gbc);
        nextSpanButton = new JButton();
        nextSpanButton.setHorizontalAlignment(0);
        nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
        nextSpanButton.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(nextSpanButton, gbc);
        previousSpanButton = new JButton();
        previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
        previousSpanButton.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel6.add(previousSpanButton, gbc);
        final Spacer spacer5 = new Spacer();
        infoPane.add(spacer5, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        annotationIDLabel.setText("");
        infoPane.add(annotationIDLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoPane.add(annotationAnnotatorLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        annotationClassLabel.setText("");
        infoPane.add(annotationClassLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
}
