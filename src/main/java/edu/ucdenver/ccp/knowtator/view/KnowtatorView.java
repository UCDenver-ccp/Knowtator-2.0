package edu.ucdenver.ccp.knowtator.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener, KnowtatorComponent {

    private static final Logger log = Logger.getLogger(KnowtatorView.class);
    private final Preferences preferences = Preferences.userRoot().node("knowtator");
    private KnowtatorController controller;
    private GraphViewDialog graphViewDialog;
    private JComponent panel1;
    private JButton showGraphViewerButton;
    private JButton removeAnnotationButton;
    private JButton growStartButton;
    private JButton shrinkEndButton;
    private JButton growEndButton;
    private JButton shrinkStartButton;
    private JButton addAnnotationButton;
    private JButton previousTextSourceButton;
    private JButton nextTextSourceButton;
    private JButton assignColorToClassButton;
    private TextSourceChooser textSourceChooser;
    private JButton findTextInOntologyButton;
    private JButton addTextSourceButton;
    private JButton removeTextSourceButton;
    private JButton menuButton;
    private JButton previousMatchButton;
    private JTextField matchTextField;
    private JButton nextMatchButton;
    private JCheckBox caseSensitiveCheckBox;
    private JCheckBox onlyAnnotationsCheckBox;
    private JSlider fontSizeSlider;
    private JPanel searchPanel;
    private JCheckBox regexCheckBox;
    private KnowtatorTextPane knowtatorTextPane;
    private AnnotationPane annotationPane;

    private Map<JComponent, EventListener> textSourceButtons;
    private Map<JButton, ActionListener> annotationButtons;
    private Map<JButton, ActionListener> spanButtons;
    private Map<JButton, ActionListener> spanSizeButtons;
    private Map<JButton, ActionListener> selectionSizeButtons;

    private KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
    private KnowtatorCollectionListener<Span> spanCollectionListener;


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
        if (!controller.getOWLModel().isWorkSpaceSet()) {
            if (getOWLWorkspace() != null) {
                controller.getOWLModel().setOwlWorkSpace(getOWLWorkspace());
                annotationPane.setupOWL();

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
        annotationPane = new AnnotationPane(this);
        knowtatorTextPane = new KnowtatorTextPane(this);
        graphViewDialog = new GraphViewDialog(this);

        textSourceChooser = new TextSourceChooser(this);

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
        makeMenuButtons();
        makeTextSourceButtons();
        makeAnnotationButtons();
        makeSpanButtons();
        makeSearchButtons();

        // Disable
        disableTextSourceButtons();
    }

    private void makeMenuButtons() {
        menuButton.addActionListener(e -> KnowtatorActions.showMainMenuDialog(this));
    }

    private void makeTextSourceButtons() {
        fontSizeSlider.setValue(knowtatorTextPane.getFont().getSize());

        KnowtatorView view = this;
        textSourceButtons = new HashMap<>();
        textSourceButtons.put(fontSizeSlider, (ChangeListener) e -> TextSourceActions.setFontSize(view, fontSizeSlider.getValue()));
        textSourceButtons.put(showGraphViewerButton, (ActionListener) e -> KnowtatorActions.showGraphViewer(graphViewDialog));
        textSourceButtons.put(previousTextSourceButton, (ActionListener) e -> TextSourceActions.selectPreviousTextSource(view));
        textSourceButtons.put(nextTextSourceButton, (ActionListener) e -> TextSourceActions.selectNextTextSource(view));
        textSourceButtons.put(addTextSourceButton, (ActionListener) e -> TextSourceActions.addTextSource(view));
        textSourceButtons.put(removeTextSourceButton, (ActionListener) e -> TextSourceActions.removeTextSource(view));
    }

    private void makeAnnotationButtons() {

        annotationButtons = new HashMap<>();
        annotationButtons.put(addAnnotationButton, e -> AnnotationActions.addAnnotation(this));
        annotationButtons.put(removeAnnotationButton, e -> AnnotationActions.removeAnnotation(this));
        annotationButtons.put(assignColorToClassButton, e -> KnowtatorActions.assignColorToClassButton(this));
    }

    private void makeSpanButtons() {

        spanButtons = new HashMap<>();
        spanButtons.put(annotationPane.getNextSpanButton(), e -> SpanActions.selectNextSpan(this));
        spanButtons.put(annotationPane.getPreviousSpanButton(), e -> SpanActions.selectPreviousSpan(this));

        spanSizeButtons = new HashMap<>();
        spanSizeButtons.put(shrinkEndButton, e -> SpanActions.modifySelectedSpan(this, SpanActions.END, SpanActions.SHRINK));
        spanSizeButtons.put(shrinkStartButton, e -> SpanActions.modifySelectedSpan(this, SpanActions.START, SpanActions.SHRINK));
        spanSizeButtons.put(growEndButton, e -> SpanActions.modifySelectedSpan(this, SpanActions.END, SpanActions.GROW));
        spanSizeButtons.put(growStartButton, e -> SpanActions.modifySelectedSpan(this, SpanActions.START, SpanActions.GROW));

        selectionSizeButtons = new HashMap<>();
        selectionSizeButtons.put(growStartButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.SHRINK));
        selectionSizeButtons.put(shrinkStartButton, e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.SHRINK));
        selectionSizeButtons.put(shrinkEndButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.GROW));
        selectionSizeButtons.put(growEndButton, e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.GROW));
    }

    private void makeSearchButtons() {
        findTextInOntologyButton.addActionListener(e -> SearchActions.findText(this, matchTextField.getSelectedText()));
        nextMatchButton.addActionListener(e -> SearchActions.findNextMatch(this, matchTextField.getText(), caseSensitiveCheckBox.isSelected(), onlyAnnotationsCheckBox.isSelected(), regexCheckBox.isSelected()));
        previousMatchButton.addActionListener(e -> SearchActions.findPreviousMatch(this, matchTextField.getText(), caseSensitiveCheckBox.isSelected(), onlyAnnotationsCheckBox.isSelected(), regexCheckBox.isSelected()));
    }

    private void setupListeners() {

        KnowtatorCollectionListener<Profile> profileCollectionListener = new KnowtatorCollectionListener<Profile>() {
            @Override
            public void updated(Profile updatedItem) {

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

        KnowtatorCollectionListener<TextSource> textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void updated(TextSource updatedItem) {

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
        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void updated(ConceptAnnotation updatedItem) {

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

        spanCollectionListener = new KnowtatorCollectionListener<Span>() {

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

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
        if (controller != null) {
            setUpOWL();
        }
        return selectedClass;
    }

    @Override
    public void reset() {
        disposeView();
        setupListeners();
        annotationPane.reset();
        knowtatorTextPane.reset();
        textSourceChooser.reset();
        graphViewDialog.reset();

        controller.getOWLModel().setOwlWorkSpace(getOWLWorkspace());
    }

    @Override
    public void disposeView() {
        controller.dispose();
        graphViewDialog.setVisible(false);
        graphViewDialog.dispose();
        annotationPane.dispose();
        knowtatorTextPane.dispose();

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

    JTextComponent getMatchTextField() {
        return matchTextField;
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
    }

    private void enableTextSourceButtons() {
        textSourceButtons.forEach((button, e) -> button.setEnabled(true));
        textSourceButtons.forEach((component, e) -> {
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).addActionListener((ActionListener) e);
            } else if (component instanceof JSlider) {
                ((JSlider) component).addChangeListener((ChangeListener) e);
            }
        });
    }

    private void disableTextSourceButtons() {
        textSourceButtons.forEach((button, e) -> button.setEnabled(false));
        textSourceButtons.forEach((component, e) -> {
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).removeActionListener((ActionListener) e);
            } else if (component instanceof JSlider) {
                ((JSlider) component).removeChangeListener((ChangeListener) e);
            }
        });
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

    public GraphViewDialog getGraphViewDialog() {
        return graphViewDialog;
    }


    public KnowtatorTextPane getKnowtatorTextPane() {
        return knowtatorTextPane;
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
        findTextInOntologyButton = new JButton();
        this.$$$loadButtonText$$$(findTextInOntologyButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology1"));
        searchPanel.add(findTextInOntologyButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        regexCheckBox = new JCheckBox();
        regexCheckBox.setText("Regex");
        searchPanel.add(regexCheckBox, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        caseSensitiveCheckBox = new JCheckBox();
        caseSensitiveCheckBox.setText("Case Sensitive");
        searchPanel.add(caseSensitiveCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        searchPanel.add(panel4, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        growStartButton = new JButton();
        growStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32 (reversed).png")));
        growStartButton.setText("");
        panel4.add(growStartButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shrinkStartButton = new JButton();
        shrinkStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32.png")));
        shrinkStartButton.setText("");
        panel4.add(shrinkStartButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shrinkEndButton = new JButton();
        shrinkEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32 (reversed).png")));
        shrinkEndButton.setText("");
        panel4.add(shrinkEndButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        growEndButton = new JButton();
        growEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32.png")));
        growEndButton.setText("");
        panel4.add(growEndButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        splitPane1.setDividerLocation(682);
        splitPane1.setMinimumSize(new Dimension(0, 0));
        panel1.add(splitPane1, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setMinimumSize(new Dimension(0, 100));
        scrollPane1.setPreferredSize(new Dimension(500, 100));
        splitPane1.setLeftComponent(scrollPane1);
        knowtatorTextPane.setEditable(false);
        scrollPane1.setViewportView(knowtatorTextPane);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel6.setMaximumSize(new Dimension(262, 2147483647));
        panel6.setMinimumSize(new Dimension(262, 158));
        splitPane1.setRightComponent(panel6);
        panel6.add(annotationPane.$$$getRootComponent$$$(), BorderLayout.CENTER);
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
