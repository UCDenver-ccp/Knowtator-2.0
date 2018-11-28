/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.listener.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.listener.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.view.actions.collection.ActionParameters;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.FilterAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.SpanActions;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationAnnotatorLabel;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationClassLabel;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationIDLabel;
import edu.ucdenver.ccp.knowtator.view.list.GraphSpaceList;
import edu.ucdenver.ccp.knowtator.view.list.SpanList;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import edu.ucdenver.ccp.knowtator.view.textpane.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction.pickAction;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.*;
import static edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction.assignColorToClass;

/**
 * Main class for GUI
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener, KnowtatorComponent {

	private static final Logger log = Logger.getLogger(KnowtatorView.class);
	public static final Preferences PREFERENCES = Preferences.userRoot().node("knowtator");

	public static KnowtatorModel MODEL = new KnowtatorModel();
	private GraphViewDialog graphViewDialog;
	private JComponent panel1;
	private JTextField searchTextField;
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
	JButton addTextSourceButton;
	private JButton removeTextSourceButton;
	private JButton menuButton;
	private JButton previousMatchButton;
	private JButton nextMatchButton;
	private JCheckBox caseSensitiveCheckBox;
	private JCheckBox onlyAnnotationsCheckBox;
	private JSlider fontSizeSlider;
	private JPanel searchPanel;
	private JCheckBox regexCheckBox;
	private KnowtatorTextPane knowtatorTextPane;
	private SpanList spanList;
	private GraphSpaceList graphSpaceList;
	private JButton nextSpanButton;
	private JButton previousSpanButton;
	private AnnotationIDLabel annotationIDLabel;
	private AnnotationAnnotatorLabel annotationAnnotatorLabel;
	private AnnotationClassLabel annotationClassLabel;
	private JCheckBox profileFilterCheckBox;
	private JCheckBox owlClassFilterCheckBox;
	private JButton undoButton;
	private JButton redoButton;
	private AnnotationNotes annotationNotes;

	List<JComponent> textSourceButtons;
	private List<JButton> annotationButtons;
	private Map<JButton, ActionListener> spanSizeButtons;
	private Map<JButton, ActionListener> selectionSizeButtons;

	private List<KnowtatorComponent> knowtatorComponents;


	/**
	 * Creates all components and sets up its MODEL
	 */
	public KnowtatorView() {
		$$$setupUI$$$();

		makeButtons();

		// This is necessary to force OSGI to load the mxGraphTransferable class to allow node dragging.
		// It is kind of a hacky fix, but it works for now.
		log.warn("Don't worry about the following exception. Just forcing loading of a class needed by mxGraph");
		try {
			mxGraphTransferable.dataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=com.mxgraph.swing.util.mxGraphTransferable", null, mxGraphTransferable.class.getClassLoader());
		} catch (ClassNotFoundException ignored) {

		}

		setUpOWL();
	}

	/**
	 * Sets OWL workspace for controllers OWL modelactions in order to interface with Protege's OWL workspace
	 */
	private void setUpOWL() {
		if (!MODEL.isWorkSpaceSet()) {
			if (getOWLWorkspace() != null) {
				MODEL.setOwlWorkSpace(getOWLWorkspace());
				MODEL.addOWLModelManagerListener(annotationClassLabel);

			}
		}
	}

	/**
	 * Inherited but not used
	 */
	@Override
	public void initialiseClassView() {
	}

	/**
	 * Creates custum UI components like chooser boxes and labels that listen to the modelactions.
	 */
	private void createUIComponents() {
		DropTarget dt = new DropTarget(this, this);
		dt.setActive(true);

		panel1 = this;

		knowtatorComponents = new ArrayList<>();

		searchTextField = new JTextField();
		regexCheckBox = new JCheckBox();
		onlyAnnotationsCheckBox = new JCheckBox();
		caseSensitiveCheckBox = new JCheckBox();

		knowtatorTextPane = new KnowtatorTextPane(this, searchTextField, onlyAnnotationsCheckBox, regexCheckBox, caseSensitiveCheckBox);
		graphViewDialog = new GraphViewDialog(this);
		textSourceChooser = new TextSourceChooser(this);

		spanList = new SpanList();
		graphSpaceList = new GraphSpaceList();
		annotationAnnotatorLabel = new AnnotationAnnotatorLabel(this);
		annotationClassLabel = new AnnotationClassLabel(this);
		annotationIDLabel = new AnnotationIDLabel(this);
		annotationNotes = new AnnotationNotes();

		knowtatorComponents.addAll(Arrays.asList(annotationNotes,
				spanList,
				graphSpaceList,
				annotationAnnotatorLabel,
				annotationClassLabel,
				annotationIDLabel,
				knowtatorTextPane,
				graphViewDialog,
				textSourceChooser));


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
	 * Makes the buttons in the main display pane
	 */
	private void makeButtons() {
		makeMenuButton();
		makeTextSourceButtons();
		makeAnnotationButtons();
		makeSpanModificationButtons();
		makeSearchButtons();
		makeFilterCheckBoxes();
		makeUndoButtons();

		// Disable
		disableTextSourceButtons();
	}

	/**
	 * Make undo and redo buttons
	 */
	private void makeUndoButtons() {
		undoButton.addActionListener(e -> {
			if (MODEL.canUndo()) {
				MODEL.undo();
			}
		});
		redoButton.addActionListener(e -> {
			if (MODEL.canRedo()) {
				MODEL.redo();
			}
		});
	}

	/**
	 * Make filter check boxes
	 */
	private void makeFilterCheckBoxes() {
		owlClassFilterCheckBox.setSelected(MODEL.isFilter(FilterType.OWLCLASS));
		profileFilterCheckBox.setSelected(MODEL.isFilter(FilterType.PROFILE));

		profileFilterCheckBox.addItemListener(e -> MODEL.registerAction(new FilterAction(FilterType.PROFILE, profileFilterCheckBox.isSelected())));
		owlClassFilterCheckBox.addItemListener(e -> MODEL.registerAction(new FilterAction(FilterType.OWLCLASS, owlClassFilterCheckBox.isSelected())));
	}

	/**
	 * Makes the menu button
	 */
	private void makeMenuButton() {
		menuButton.addActionListener(e -> {
			MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(this), this);
			menuDialog.pack();
			menuDialog.setVisible(true);
		});
	}

	/**
	 * Makes the text source buttons and font getNumberOfGraphSpaces slider
	 */
	private void makeTextSourceButtons() {
		fontSizeSlider.setValue(knowtatorTextPane.getFont().getSize());
		fontSizeSlider.addChangeListener(e -> knowtatorTextPane.setFontSize(fontSizeSlider.getValue()));
		showGraphViewerButton.addActionListener(e -> graphViewDialog.setVisible(true));
		previousTextSourceButton.addActionListener(e -> MODEL.selectPreviousTextSource());
		nextTextSourceButton.addActionListener(e -> MODEL.selectNextTextSource());
		addTextSourceButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(MODEL.getArticlesLocation());

			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				pickAction(this, null, fileChooser.getSelectedFile(), new ActionParameters(ADD, DOCUMENT));
			}
		});
		removeTextSourceButton.addActionListener(e -> pickAction(this, null, null, new ActionParameters(REMOVE, DOCUMENT)));

		textSourceButtons = Arrays.asList(
				fontSizeSlider,
				showGraphViewerButton,
				previousTextSourceButton,
				nextTextSourceButton,
				addTextSourceButton,
				removeTextSourceButton
		);

	}

	/**
	 * Makes the annotation and span selection buttons
	 */
	private void makeAnnotationButtons() {
		assignColorToClassButton.addActionListener(e -> MODEL.getSelectedOWLClass().ifPresent(owlClass -> assignColorToClass(this, owlClass)));

		addAnnotationButton.addActionListener(e -> pickAction(this, null, null, new ActionParameters(ADD, ANNOTATION), new ActionParameters(ADD, SPAN)));
		removeAnnotationButton.addActionListener(e -> pickAction(this, null, null, new ActionParameters(REMOVE, ANNOTATION), new ActionParameters(REMOVE, SPAN)));
		nextSpanButton.addActionListener(e -> MODEL.getSelectedTextSource().ifPresent(TextSource::selectNextSpan));
		previousSpanButton.addActionListener(e -> MODEL.getSelectedTextSource().ifPresent(TextSource::selectPreviousSpan));

		annotationButtons = Arrays.asList(
				addAnnotationButton,
				removeAnnotationButton,
				nextSpanButton,
				previousSpanButton
		);
	}

	/**
	 * Makes the span modification buttons
	 */
	private void makeSpanModificationButtons() {

		spanSizeButtons = new HashMap<>();
		spanSizeButtons.put(shrinkEndButton, e -> MODEL.getSelectedTextSource()
				.ifPresent(textSource -> textSource.getSelectedAnnotation()
						.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
								.ifPresent(span -> MODEL.registerAction(new SpanActions.ModifySpanAction(SpanActions.END, SpanActions.SHRINK, span))))));
		spanSizeButtons.put(shrinkStartButton, e -> MODEL.getSelectedTextSource()
				.ifPresent(textSource -> textSource.getSelectedAnnotation()
						.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
								.ifPresent(span -> MODEL.registerAction(new SpanActions.ModifySpanAction(SpanActions.START, SpanActions.SHRINK, span))))));
		spanSizeButtons.put(growEndButton, e -> MODEL.getSelectedTextSource()
				.ifPresent(textSource -> textSource.getSelectedAnnotation()
						.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
								.ifPresent(span -> MODEL.registerAction(new SpanActions.ModifySpanAction(SpanActions.END, SpanActions.GROW, span))))));
		spanSizeButtons.put(growStartButton, e ->
				MODEL.getSelectedTextSource()
						.ifPresent(textSource -> textSource.getSelectedAnnotation()
								.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
										.ifPresent(span -> MODEL.registerAction(new SpanActions.ModifySpanAction(SpanActions.START, SpanActions.GROW, span))))));

		selectionSizeButtons = new HashMap<>();

		selectionSizeButtons.put(shrinkEndButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.SHRINK));
		selectionSizeButtons.put(shrinkStartButton, e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.SHRINK));
		selectionSizeButtons.put(growEndButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.GROW));
		selectionSizeButtons.put(growStartButton, e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.GROW));
	}

	/**
	 * Makes the search buttons and filter checkboxes
	 */
	private void makeSearchButtons() {
		findTextInOntologyButton.addActionListener(e -> MODEL.searchForString(searchTextField.getText()));
		nextMatchButton.addActionListener(e -> getKnowtatorTextPane().searchForward());
		previousMatchButton.addActionListener(e -> getKnowtatorTextPane().searchPrevious());

	}

	/**
	 * Sets up listeners to detect changes when text sources or concept annotations or spans change
	 */
	@Override
	public void setupListeners() {
		new TextBoundModelListener(MODEL) {
			@Override
			public void respondToTextSourceCollectionEmptied() {
				disableTextSourceButtons();
				addTextSourceButton.setEnabled(true);
			}

			@Override
			public void respondToTextSourceCollectionFirstAdded() {
				enableTextSourceButtons();
				addAnnotationButton.setEnabled(true);
			}

			@Override
			public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
				enableTextSourceButtons();
				if (!event.getNew().isPresent()) {
					removeAnnotationButton.setEnabled(false);
				} else {
					enableAnnotationButtons();
				}
			}

			@Override
			public void respondToConceptAnnotationCollectionEmptied() {
				disableAnnotationButtons();
				addAnnotationButton.setEnabled(true);
			}

			@Override
			public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
				enableTextSourceButtons();
			}

			@Override
			public void respondToTextSourceAdded() {
				enableTextSourceButtons();
			}

			@Override
			public void respondToTextSourceRemoved() {

			}

			@Override
			public void respondToConceptAnnotationModification() {

			}

			@Override
			public void respondToSpanModification() {

			}

			@Override
			public void respondToGraphSpaceModification() {

			}

			@Override
			public void respondToGraphSpaceCollectionFirstAdded() {

			}

			@Override
			public void respondToGraphSpaceCollectionEmptied() {

			}


			@Override
			public void respondToGraphSpaceRemoved() {

			}

			@Override
			public void respondToGraphSpaceAdded() {

			}

			@Override
			public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {

			}

			@Override
			public void respondToConceptAnnotationRemoved() {

			}

			@Override
			public void respondToConceptAnnotationAdded() {

			}

			@Override
			public void respondToConceptAnnotationCollectionFirstAdded() {
				enableAnnotationButtons();
			}

			@Override
			public void respondToSpanSelection(SelectionEvent<Span> event) {
				if (!event.getNew().isPresent()) {
					disableSpanButtons();
				} else {
					enableSpanButtons();
				}
			}

			@Override
			public void respondToSpanCollectionEmptied() {
				disableSpanButtons();
			}

			@Override
			public void respondToSpanRemoved() {

			}

			@Override
			public void respondToSpanAdded() {

			}

			@Override
			public void respondToSpanCollectionFirstAdded() {
				enableSpanButtons();
			}
		};
	}

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		setUpOWL();
		return selectedClass;
	}

	@Override
	public void reset() {
		setupListeners();
		knowtatorComponents.forEach(KnowtatorComponent::reset);
	}

	/**
	 * Enables text source buttons
	 */
	private void enableTextSourceButtons() {
		textSourceButtons.forEach(button -> button.setEnabled(true));
	}

	/**
	 * Disables text source buttons and annotation buttons
	 */
	private void disableTextSourceButtons() {
		textSourceButtons.forEach(button -> button.setEnabled(false));
		disableAnnotationButtons();
	}

	/**
	 * Enables annotation buttons
	 */
	private void enableAnnotationButtons() {
		annotationButtons.forEach(button -> button.setEnabled(true));
	}

	/**
	 * Disables annotation buttons and span buttons
	 */
	private void disableAnnotationButtons() {
		annotationButtons.forEach(button -> button.setEnabled(false));
		disableSpanButtons();
	}

	/**
	 * Enables span buttons
	 */
	private void enableSpanButtons() {
		selectionSizeButtons.forEach(AbstractButton::removeActionListener);
		spanSizeButtons.forEach(AbstractButton::removeActionListener);
		spanSizeButtons.forEach(AbstractButton::addActionListener);
	}

	/**
	 * Disables span buttons
	 */
	private void disableSpanButtons() {
		spanSizeButtons.forEach(AbstractButton::removeActionListener);
		selectionSizeButtons.forEach(AbstractButton::removeActionListener);
		selectionSizeButtons.forEach(AbstractButton::addActionListener);
	}

	/**
	 * @return The graph view dialog
	 */
	public GraphViewDialog getGraphViewDialog() {
		return graphViewDialog;
	}


	/**
	 * @return the Knowtator text pane
	 */
	public KnowtatorTextPane getKnowtatorTextPane() {
		return knowtatorTextPane;
	}

	/**
	 * Calls dispose on the modelactions and all components
	 */
	@Override
	public void disposeView() {
		MODEL.dispose();

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

	public void loadProject(File file, TextSourceCollectionListener listener) throws IOException {
		MODEL.dispose();
		MODEL.reset(getOWLWorkspace());
		MODEL.setSaveLocation(file);
		log.info(String.format("Opening from %s", file.getAbsolutePath()));
		if (listener != null) {
			MODEL.addTextSourceCollectionListener(listener);
		}
		MODEL.loadProject();
		reset();
		MODEL.selectFirstTextSource();
		knowtatorTextPane.refreshHighlights();
		addTextSourceButton.setEnabled(true);

		KnowtatorView.PREFERENCES.put("Last Project", MODEL.getProjectLocation().getAbsolutePath());

		try {
			KnowtatorView.PREFERENCES.flush();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
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
		searchPanel.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
		searchPanel.setAlignmentX(0.0f);
		panel3.add(searchPanel, BorderLayout.CENTER);
		menuButton = new JButton();
		Font menuButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, menuButton.getFont());
		if (menuButtonFont != null) menuButton.setFont(menuButtonFont);
		menuButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-menu-24.png")));
		menuButton.setText("");
		menuButton.setVerticalAlignment(0);
		searchPanel.add(menuButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		previousMatchButton = new JButton();
		Font previousMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, previousMatchButton.getFont());
		if (previousMatchButtonFont != null) previousMatchButton.setFont(previousMatchButtonFont);
		previousMatchButton.setText("Previous");
		searchPanel.add(previousMatchButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nextMatchButton = new JButton();
		Font nextMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, nextMatchButton.getFont());
		if (nextMatchButtonFont != null) nextMatchButton.setFont(nextMatchButtonFont);
		nextMatchButton.setText("Next");
		searchPanel.add(nextMatchButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		searchPanel.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		findTextInOntologyButton = new JButton();
		Font findTextInOntologyButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, findTextInOntologyButton.getFont());
		if (findTextInOntologyButtonFont != null) findTextInOntologyButton.setFont(findTextInOntologyButtonFont);
		this.$$$loadButtonText$$$(findTextInOntologyButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology1"));
		searchPanel.add(findTextInOntologyButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		showGraphViewerButton = new JButton();
		Font showGraphViewerButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, showGraphViewerButton.getFont());
		if (showGraphViewerButtonFont != null) showGraphViewerButton.setFont(showGraphViewerButtonFont);
		showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
		showGraphViewerButton.setText("");
		searchPanel.add(showGraphViewerButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JToolBar toolBar1 = new JToolBar();
		toolBar1.setFloatable(false);
		searchPanel.add(toolBar1, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
		assignColorToClassButton = new JButton();
		assignColorToClassButton.setEnabled(true);
		Font assignColorToClassButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, assignColorToClassButton.getFont());
		if (assignColorToClassButtonFont != null) assignColorToClassButton.setFont(assignColorToClassButtonFont);
		assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
		assignColorToClassButton.setText("");
		toolBar1.add(assignColorToClassButton);
		addAnnotationButton = new JButton();
		Font addAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, addAnnotationButton.getFont());
		if (addAnnotationButtonFont != null) addAnnotationButton.setFont(addAnnotationButtonFont);
		addAnnotationButton.setHorizontalTextPosition(0);
		addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addAnnotationButton.setText("");
		addAnnotationButton.setVerticalAlignment(0);
		addAnnotationButton.setVerticalTextPosition(3);
		toolBar1.add(addAnnotationButton);
		removeAnnotationButton = new JButton();
		Font removeAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeAnnotationButton.getFont());
		if (removeAnnotationButtonFont != null) removeAnnotationButton.setFont(removeAnnotationButtonFont);
		removeAnnotationButton.setHorizontalTextPosition(0);
		removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeAnnotationButton.setText("");
		removeAnnotationButton.setVerticalTextPosition(3);
		toolBar1.add(removeAnnotationButton);
		growStartButton = new JButton();
		Font growStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growStartButton.getFont());
		if (growStartButtonFont != null) growStartButton.setFont(growStartButtonFont);
		growStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32 (reversed).png")));
		growStartButton.setText("");
		toolBar1.add(growStartButton);
		shrinkStartButton = new JButton();
		Font shrinkStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkStartButton.getFont());
		if (shrinkStartButtonFont != null) shrinkStartButton.setFont(shrinkStartButtonFont);
		shrinkStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32.png")));
		shrinkStartButton.setText("");
		toolBar1.add(shrinkStartButton);
		shrinkEndButton = new JButton();
		Font shrinkEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkEndButton.getFont());
		if (shrinkEndButtonFont != null) shrinkEndButton.setFont(shrinkEndButtonFont);
		shrinkEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32 (reversed).png")));
		shrinkEndButton.setText("");
		toolBar1.add(shrinkEndButton);
		growEndButton = new JButton();
		Font growEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growEndButton.getFont());
		if (growEndButtonFont != null) growEndButton.setFont(growEndButtonFont);
		growEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32.png")));
		growEndButton.setText("");
		toolBar1.add(growEndButton);
		profileFilterCheckBox = new JCheckBox();
		Font profileFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, profileFilterCheckBox.getFont());
		if (profileFilterCheckBoxFont != null) profileFilterCheckBox.setFont(profileFilterCheckBoxFont);
		profileFilterCheckBox.setText("Profile");
		toolBar1.add(profileFilterCheckBox);
		owlClassFilterCheckBox = new JCheckBox();
		Font owlClassFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlClassFilterCheckBox.getFont());
		if (owlClassFilterCheckBoxFont != null) owlClassFilterCheckBox.setFont(owlClassFilterCheckBoxFont);
		owlClassFilterCheckBox.setText("OWL Class");
		toolBar1.add(owlClassFilterCheckBox);
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
		searchPanel.add(panel4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		Font searchTextFieldFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, searchTextField.getFont());
		if (searchTextFieldFont != null) searchTextField.setFont(searchTextFieldFont);
		panel4.add(searchTextField, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 25), new Dimension(-1, 25), 0, false));
		Font onlyAnnotationsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, onlyAnnotationsCheckBox.getFont());
		if (onlyAnnotationsCheckBoxFont != null) onlyAnnotationsCheckBox.setFont(onlyAnnotationsCheckBoxFont);
		onlyAnnotationsCheckBox.setText("Only in Annotations");
		panel4.add(onlyAnnotationsCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font caseSensitiveCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, caseSensitiveCheckBox.getFont());
		if (caseSensitiveCheckBoxFont != null) caseSensitiveCheckBox.setFont(caseSensitiveCheckBoxFont);
		caseSensitiveCheckBox.setText("Case Sensitive");
		panel4.add(caseSensitiveCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font regexCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, regexCheckBox.getFont());
		if (regexCheckBoxFont != null) regexCheckBox.setFont(regexCheckBoxFont);
		regexCheckBox.setText("Regex");
		panel4.add(regexCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		searchPanel.add(panel5, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		undoButton = new JButton();
		undoButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-undo-32.png")));
		undoButton.setText("");
		panel5.add(undoButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		redoButton = new JButton();
		redoButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-redo-32.png")));
		redoButton.setText("");
		panel5.add(redoButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel6, BorderLayout.SOUTH);
		previousTextSourceButton = new JButton();
		previousTextSourceButton.setText("Previous");
		panel6.add(previousTextSourceButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		fontSizeSlider = new JSlider();
		fontSizeSlider.setInverted(false);
		fontSizeSlider.setMajorTickSpacing(8);
		fontSizeSlider.setMaximum(28);
		fontSizeSlider.setMinimum(8);
		fontSizeSlider.setMinorTickSpacing(1);
		fontSizeSlider.setSnapToTicks(true);
		fontSizeSlider.setValue(16);
		panel6.add(fontSizeSlider, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		panel6.add(textSourceChooser, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addTextSourceButton = new JButton();
		addTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addTextSourceButton.setText("");
		panel6.add(addTextSourceButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		removeTextSourceButton = new JButton();
		removeTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeTextSourceButton.setText("");
		panel6.add(removeTextSourceButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel6.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		nextTextSourceButton = new JButton();
		nextTextSourceButton.setText("Next");
		panel6.add(nextTextSourceButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSplitPane splitPane1 = new JSplitPane();
		splitPane1.setDividerLocation(648);
		splitPane1.setMinimumSize(new Dimension(0, 0));
		panel1.add(splitPane1, BorderLayout.CENTER);
		final JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setMinimumSize(new Dimension(0, 100));
		scrollPane1.setPreferredSize(new Dimension(500, 100));
		splitPane1.setLeftComponent(scrollPane1);
		knowtatorTextPane.setEditable(false);
		scrollPane1.setViewportView(knowtatorTextPane);
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new GridLayoutManager(10, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel7.setMaximumSize(new Dimension(200, 2147483647));
		panel7.setMinimumSize(new Dimension(200, 158));
		splitPane1.setRightComponent(panel7);
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new GridBagLayout());
		panel7.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nextSpanButton = new JButton();
		nextSpanButton.setHorizontalAlignment(0);
		nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextSpanButton.setText("");
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		panel8.add(nextSpanButton, gbc);
		previousSpanButton = new JButton();
		previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousSpanButton.setText("");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		panel8.add(previousSpanButton, gbc);
		final JLabel label1 = new JLabel();
		Font label1Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label1.getFont());
		if (label1Font != null) label1.setFont(label1Font);
		label1.setText("ID");
		panel7.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		Font label2Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label2.getFont());
		if (label2Font != null) label2.setFont(label2Font);
		label2.setText("Annotator");
		panel7.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		Font label3Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label3.getFont());
		if (label3Font != null) label3.setFont(label3Font);
		label3.setText("Class");
		panel7.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		Font label4Font = this.$$$getFont$$$("Verdana", Font.BOLD, 18, label4.getFont());
		if (label4Font != null) label4.setFont(label4Font);
		label4.setText("Spans");
		panel7.add(label4, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane2 = new JScrollPane();
		panel7.add(scrollPane2, new GridConstraints(7, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		Font spanListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, spanList.getFont());
		if (spanListFont != null) spanList.setFont(spanListFont);
		scrollPane2.setViewportView(spanList);
		final JLabel label5 = new JLabel();
		Font label5Font = this.$$$getFont$$$("Verdana", Font.BOLD, 18, label5.getFont());
		if (label5Font != null) label5.setFont(label5Font);
		label5.setText("Graph Spaces");
		panel7.add(label5, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane3 = new JScrollPane();
		panel7.add(scrollPane3, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		Font graphSpaceListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, graphSpaceList.getFont());
		if (graphSpaceListFont != null) graphSpaceList.setFont(graphSpaceListFont);
		scrollPane3.setViewportView(graphSpaceList);
		final Spacer spacer3 = new Spacer();
		panel7.add(spacer3, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		Font annotationIDLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, annotationIDLabel.getFont());
		if (annotationIDLabelFont != null) annotationIDLabel.setFont(annotationIDLabelFont);
		annotationIDLabel.setText("");
		panel7.add(annotationIDLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font annotationAnnotatorLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, annotationAnnotatorLabel.getFont());
		if (annotationAnnotatorLabelFont != null) annotationAnnotatorLabel.setFont(annotationAnnotatorLabelFont);
		panel7.add(annotationAnnotatorLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		Font annotationClassLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, annotationClassLabel.getFont());
		if (annotationClassLabelFont != null) annotationClassLabel.setFont(annotationClassLabelFont);
		annotationClassLabel.setText("");
		panel7.add(annotationClassLabel, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		Font label6Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label6.getFont());
		if (label6Font != null) label6.setFont(label6Font);
		label6.setText("Notes");
		panel7.add(label6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane4 = new JScrollPane();
		panel7.add(scrollPane4, new GridConstraints(4, 1, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane4.setViewportView(annotationNotes);
		final Spacer spacer4 = new Spacer();
		panel7.add(spacer4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
	}

	/**
	 *
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
