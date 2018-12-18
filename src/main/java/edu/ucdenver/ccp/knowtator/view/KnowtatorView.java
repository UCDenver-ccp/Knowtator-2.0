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
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.collection.ActionParameters;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.FilterAction;
import edu.ucdenver.ccp.knowtator.view.actions.modelactions.SpanActions;
import edu.ucdenver.ccp.knowtator.view.chooser.TextSourceChooser;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationAnnotatorLabel;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationClassLabel;
import edu.ucdenver.ccp.knowtator.view.label.AnnotationIDLabel;
import edu.ucdenver.ccp.knowtator.view.list.*;
import edu.ucdenver.ccp.knowtator.view.menu.Loader;
import edu.ucdenver.ccp.knowtator.view.textpane.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
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
public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener, KnowtatorComponent, ModelListener {

	private static final Logger log = Logger.getLogger(KnowtatorView.class);
	public static final Preferences PREFERENCES = Preferences.userRoot().node("knowtator");

	private KnowtatorModel model;
	private GraphViewDialog graphViewDialog;
	private JComponent panel1;

	private KnowtatorTextPane knowtatorTextPane;
	private JTabbedPane tabbedPane1;
	private GraphSpaceList graphSpaceList;
	private AnnotationIDLabel annotationIDLabel;
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
	private JProgressBar progressBar1;
	private JList fileList;
	private JButton removeProfileButton;
	private ProfileList profileList;
	private JTextField profileNameField;
	private JButton addProfileButton;
	private ColorList colorList;
	private AnnotationList annotationsForSpannedTextList;
	private JTextField annotationsContainingTextTextField;
	private JCheckBox exactMatchCheckBox;
	private JLabel owlClassLabel;
	private AnnotationList annotationsForClassList;
	private JCheckBox includeClassDescendantsCheckBox;
	private JLabel owlPropertyLabel;
	private RelationList relationsForPropertyList;
	private JButton previousReviewObjectButton;
	private JButton nextReviewObjectButton;
	private JCheckBox includePropertyDescendantsCheckBox;
	private JTextField searchTextField;
	private JButton nextMatchButton;
	private JButton previousMatchButton;
	private JButton findTextInOntologyButton;
	private JCheckBox onlyAnnotationsCheckBox;
	private JCheckBox regexCheckBox;
	private JCheckBox caseSensitiveCheckBox;
	private JTabbedPane reviewTabbedPane;
	private JButton refreshReviewPaneButton;
	private JPanel panel2;
	private JPanel contentPane;
	private JButton backButton;

	private final List<KnowtatorComponent> knowtatorComponents;
	private HashMap<JButton, ActionListener> spanSizeButtons;
	private HashMap<JButton, ActionListener> selectionSizeButtons;


	/**
	 * Creates all components and sets up its model
	 */
	public KnowtatorView() {
		knowtatorComponents = new ArrayList<>();
		spanSizeButtons = new HashMap<>();
		selectionSizeButtons = new HashMap<>();

		$$$setupUI$$$();
		tabbedPane1.setSelectedIndex(1);
		makeButtons();

		// This is necessary to force OSGI to load the mxGraphTransferable class to allow node dragging.
		// It is kind of a hacky fix, but it works for now.
		log.warn("Don't worry about the following exception. Just forcing loading of a class needed by mxGraph");
		try {
			mxGraphTransferable.dataFlavor = new DataFlavor(String.format("%s; class=com.mxgraph.swing.util.mxGraphTransferable", DataFlavor.javaJVMLocalObjectMimeType), null, mxGraphTransferable.class.getClassLoader());
		} catch (ClassNotFoundException ignored) {

		}
	}

	public Optional<KnowtatorModel> getModel() {
		return Optional.ofNullable(model);
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

		searchTextField = new JTextField();
		regexCheckBox = new JCheckBox();
		onlyAnnotationsCheckBox = new JCheckBox();
		caseSensitiveCheckBox = new JCheckBox();
		includeClassDescendantsCheckBox = new JCheckBox();
		includePropertyDescendantsCheckBox = new JCheckBox();
		exactMatchCheckBox = new JCheckBox();
		owlClassLabel = new JLabel();
		owlPropertyLabel = new JLabel();

		knowtatorTextPane = new KnowtatorTextPane(this, searchTextField, onlyAnnotationsCheckBox, regexCheckBox, caseSensitiveCheckBox);
		graphViewDialog = new GraphViewDialog(this);

		annotationAnnotatorLabel = new AnnotationAnnotatorLabel(this);
		annotationClassLabel = new AnnotationClassLabel(this);
		annotationIDLabel = new AnnotationIDLabel(this);
		annotationNotes = new AnnotationNotes(this);

		textSourceChooser = new TextSourceChooser(this);

		graphSpaceList = new GraphSpaceList(this);
		spanList = new SpanList(this);
		profileList = new ProfileList(this);
		colorList = new ColorList(this);

		annotationsContainingTextTextField = new JTextField();

		annotationsForClassList = new AnnotationListForOWLClass(this, includeClassDescendantsCheckBox, owlClassLabel);
		annotationsForSpannedTextList = new AnnotationListForSpannedText(this, exactMatchCheckBox, annotationsContainingTextTextField);
		relationsForPropertyList = new RelationList(this, includePropertyDescendantsCheckBox, owlPropertyLabel);

		knowtatorComponents.addAll(Arrays.asList(
				profileList,
				colorList,
				knowtatorTextPane,
				graphViewDialog,
				annotationNotes,
				annotationIDLabel,
				annotationAnnotatorLabel,
				annotationClassLabel,
				spanList,
				graphSpaceList,
				textSourceChooser,
				annotationsForSpannedTextList,
				annotationsForClassList,
				relationsForPropertyList));


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
		backButton.addActionListener(e -> {
			CardLayout cl = (CardLayout) panel1.getLayout();
			cl.show(panel1, "Main");
			tabbedPane1.setSelectedIndex(1);
		});

		tabbedPane1.addChangeListener(e -> {
			if (tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex()).equals("File")) {
				CardLayout cl = (CardLayout) panel1.getLayout();
				cl.show(panel1, "File");

			}
		});

		findTextInOntologyButton.addActionListener(e ->
				getModel()
						.ifPresent(model1 -> model1.searchForString(searchTextField.getText())));
		nextMatchButton.addActionListener(e -> knowtatorTextPane.searchForward());
		previousMatchButton.addActionListener(e -> knowtatorTextPane.searchPrevious());

		makeReviewPane();

		addProfileButton.addActionListener(e -> {
			pickAction(this, profileNameField.getText(), null, new ActionParameters(ADD, PROFILE));
			profileNameField.setText("");
		});
		removeProfileButton.addActionListener(e -> {
			pickAction(this, profileNameField.getText(), null, new ActionParameters(REMOVE, PROFILE));
			profileNameField.setText("");
		});

		addAnnotationButton.addActionListener(e -> pickAction(this, null, null, new ActionParameters(ADD, ANNOTATION), new ActionParameters(ADD, SPAN)));
		removeAnnotationButton.addActionListener(e -> pickAction(this, null, null, new ActionParameters(REMOVE, ANNOTATION), new ActionParameters(REMOVE, SPAN)));

		makeSpanButtons();

		fontSizeSlider.setValue(knowtatorTextPane.getFont().getSize());
		fontSizeSlider.addChangeListener(e -> knowtatorTextPane.setFontSize(fontSizeSlider.getValue()));
		showGraphViewerButton.addActionListener(e -> graphViewDialog.setVisible(true));
		previousTextSourceButton.addActionListener(e ->
				getModel().ifPresent(BaseModel::selectPreviousTextSource));
		nextTextSourceButton.addActionListener(e ->
				getModel().ifPresent(BaseModel::selectNextTextSource));
		addTextSourceButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			getModel().ifPresent(model1 -> {
				fileChooser.setCurrentDirectory(model1.getArticlesLocation());

				if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					pickAction(this, null, fileChooser.getSelectedFile(), new ActionParameters(ADD, DOCUMENT));
				}
			});

		});

		assignColorToClassButton.addActionListener(e ->
				getModel()
						.flatMap(OWLModel::getSelectedOWLClass)
						.ifPresent(owlClass ->
								assignColorToClass(this, owlClass)));

		removeTextSourceButton.addActionListener(e -> pickAction(this, null, null, new ActionParameters(REMOVE, DOCUMENT)));

		undoButton.addActionListener(e -> getModel()
				.filter(UndoManager::canUndo)
				.ifPresent(UndoManager::undo));
		redoButton.addActionListener(e -> getModel()
				.filter(UndoManager::canRedo)
				.ifPresent(UndoManager::redo));

		owlClassFilterCheckBox.setSelected(false);
		profileFilterCheckBox.setSelected(false);

		profileFilterCheckBox.addItemListener(e ->
				getModel()
						.ifPresent(knowtatorModel ->
								knowtatorModel.registerAction(
										new FilterAction(knowtatorModel, FilterType.PROFILE, profileFilterCheckBox.isSelected()))));
		owlClassFilterCheckBox.addItemListener(e ->
				getModel()
						.ifPresent(knowtatorModel ->
								knowtatorModel.registerAction(
										new FilterAction(knowtatorModel, FilterType.OWLCLASS, owlClassFilterCheckBox.isSelected()))));

		fileList.addListSelectionListener(e -> {
			switch (fileList.getSelectedValue().toString()) {
				case "Open":
					open();
					break;
				case "New":
					executeNew();
					break;
			}
		});
	}

	private void makeSpanButtons() {
		nextSpanButton.addActionListener(e -> getModel()
				.flatMap(BaseModel::getSelectedTextSource)
				.ifPresent(TextSource::selectNextSpan));
		previousSpanButton.addActionListener(e -> getModel()
				.flatMap(BaseModel::getSelectedTextSource)
				.ifPresent(TextSource::selectPreviousSpan));

		spanSizeButtons.put(shrinkEndButton, e -> getModel()
				.ifPresent(model1 -> model1.getSelectedTextSource()
						.ifPresent(textSource -> textSource.getSelectedAnnotation()
								.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
										.ifPresent(span -> model1.registerAction(
												new SpanActions.ModifySpanAction(model1, SpanActions.END, SpanActions.SHRINK, span)))))));

		spanSizeButtons.put(shrinkStartButton, e -> getModel()
				.ifPresent(model1 -> model1.getSelectedTextSource()
						.ifPresent(textSource -> textSource.getSelectedAnnotation()
								.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
										.ifPresent(span -> model1.registerAction(new SpanActions.ModifySpanAction(model1, SpanActions.START, SpanActions.SHRINK, span)))))));
		spanSizeButtons.put(growEndButton, e -> getModel()
				.ifPresent(model1 -> model1.getSelectedTextSource()
						.ifPresent(textSource -> textSource.getSelectedAnnotation()
								.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
										.ifPresent(span -> model1.registerAction(new SpanActions.ModifySpanAction(model1, SpanActions.END, SpanActions.GROW, span)))))));
		spanSizeButtons.put(growStartButton, e -> getModel()
				.ifPresent(model1 -> model1.getSelectedTextSource()
						.ifPresent(textSource -> textSource.getSelectedAnnotation()
								.ifPresent(conceptAnnotation -> conceptAnnotation.getSelection()
										.ifPresent(span -> model1.registerAction(new SpanActions.ModifySpanAction(model1, SpanActions.START, SpanActions.GROW, span)))))));

		selectionSizeButtons.put(shrinkEndButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.SHRINK));
		selectionSizeButtons.put(shrinkStartButton, e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.SHRINK));
		selectionSizeButtons.put(growEndButton, e -> SpanActions.modifySelection(this, SpanActions.END, SpanActions.GROW));
		selectionSizeButtons.put(growStartButton, e -> SpanActions.modifySelection(this, SpanActions.START, SpanActions.GROW));
	}

	private void makeReviewPane() {
		refreshReviewPaneButton.addActionListener(e -> getModel().ifPresent(model -> Arrays.stream(((JPanel) reviewTabbedPane.getSelectedComponent()).getComponents())
				.filter(component -> component instanceof KnowtatorList)
				.findFirst()
				.map(component -> (KnowtatorList) component)
				.ifPresent(KnowtatorList::reset)));

		nextReviewObjectButton.addActionListener(e -> Arrays.stream(((JPanel) reviewTabbedPane.getSelectedComponent()).getComponents())
				.filter(component -> component instanceof KnowtatorList)
				.findFirst()
				.map(component -> (KnowtatorList) component)
				.ifPresent(knowtatorList -> {
					knowtatorList.setSelectedIndex(Math.min(knowtatorList.getSelectedIndex() + 1, knowtatorList.getModel().getSize() - 1));
					knowtatorList.reactToClick();
				}));
		previousReviewObjectButton.addActionListener(e -> Arrays.stream(((JPanel) reviewTabbedPane.getSelectedComponent()).getComponents())
				.filter(component -> component instanceof KnowtatorList)
				.findFirst()
				.map(component -> (KnowtatorList) component)
				.ifPresent(knowtatorList -> {
					knowtatorList.setSelectedIndex(Math.max(knowtatorList.getSelectedIndex() - 1, 0));
					knowtatorList.reactToClick();
				}));
	}


	/**
	 * Makes the menu button
	 */
//	private void makeMenuButton() {
//		menuButton.addActionListener(e -> {
//			MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(this), this);
//			menuDialog.pack();
//			menuDialog.setVisible(true);
//		});
//	}
	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		return selectedClass;
	}

	@Override
	public void reset() {
		knowtatorComponents.forEach(KnowtatorComponent::reset);
		getModel().ifPresent(model1 -> model1.addModelListener(this));
		getModel().ifPresent(model1 -> model1.addOWLModelManagerListener(annotationClassLabel));

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
	 * Calls dispose on the model and all components
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

	public void loadProject(File file, ModelListener progressListener) throws IOException {
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
		getModel().ifPresent(OWLModel::load);
		if (progressListener != null) {
			getModel().ifPresent(model1 -> model1.removeModelListener(progressListener));
		}
		reset();

		getModel().ifPresent(BaseModel::selectFirstTextSource);
		knowtatorTextPane.showTextSource();

		getModel().ifPresent(model1 -> KnowtatorView.PREFERENCES.put("Last Project", model1.getProjectLocation().getAbsolutePath()));

		try {
			KnowtatorView.PREFERENCES.flush();
		} catch (BackingStoreException e1) {
			e1.printStackTrace();
		}
	}

	private void setModel(KnowtatorModel model) {
		this.model = model;
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
		panel1.setLayout(new CardLayout(0, 0));
		panel2 = new JPanel();
		panel2.setLayout(new BorderLayout(0, 0));
		panel1.add(panel2, "Main");
		final JSplitPane splitPane1 = new JSplitPane();
		panel2.add(splitPane1, BorderLayout.CENTER);
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new BorderLayout(0, 0));
		splitPane1.setRightComponent(panel3);
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(9, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel4.setMaximumSize(new Dimension(200, 2147483647));
		panel4.setMinimumSize(new Dimension(200, 158));
		panel3.add(panel4, BorderLayout.CENTER);
		final JLabel label1 = new JLabel();
		Font label1Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label1.getFont());
		if (label1Font != null) label1.setFont(label1Font);
		label1.setText("ID");
		panel4.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		Font label2Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label2.getFont());
		if (label2Font != null) label2.setFont(label2Font);
		label2.setText("Annotator");
		panel4.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		Font label3Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label3.getFont());
		if (label3Font != null) label3.setFont(label3Font);
		label3.setText("Class");
		panel4.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		Font label4Font = this.$$$getFont$$$("Verdana", Font.BOLD, 18, label4.getFont());
		if (label4Font != null) label4.setFont(label4Font);
		label4.setText("Graph Spaces");
		panel4.add(label4, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel4.add(scrollPane1, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		Font graphSpaceListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, graphSpaceList.getFont());
		if (graphSpaceListFont != null) graphSpaceList.setFont(graphSpaceListFont);
		scrollPane1.setViewportView(graphSpaceList);
		Font annotationIDLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, annotationIDLabel.getFont());
		if (annotationIDLabelFont != null) annotationIDLabel.setFont(annotationIDLabelFont);
		annotationIDLabel.setText("");
		panel4.add(annotationIDLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font annotationAnnotatorLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, annotationAnnotatorLabel.getFont());
		if (annotationAnnotatorLabelFont != null) annotationAnnotatorLabel.setFont(annotationAnnotatorLabelFont);
		panel4.add(annotationAnnotatorLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		Font annotationClassLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, annotationClassLabel.getFont());
		if (annotationClassLabelFont != null) annotationClassLabel.setFont(annotationClassLabelFont);
		annotationClassLabel.setText("");
		panel4.add(annotationClassLabel, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label5 = new JLabel();
		Font label5Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label5.getFont());
		if (label5Font != null) label5.setFont(label5Font);
		label5.setText("Notes");
		panel4.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane2 = new JScrollPane();
		panel4.add(scrollPane2, new GridConstraints(4, 1, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane2.setViewportView(annotationNotes);
		final Spacer spacer1 = new Spacer();
		panel4.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel4.add(spacer2, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel4.add(panel5, new GridConstraints(6, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		Font label6Font = this.$$$getFont$$$("Verdana", Font.BOLD, 18, label6.getFont());
		if (label6Font != null) label6.setFont(label6Font);
		label6.setText("Spans");
		panel5.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane3 = new JScrollPane();
		panel5.add(scrollPane3, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		Font spanListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, spanList.getFont());
		if (spanListFont != null) spanList.setFont(spanListFont);
		scrollPane3.setViewportView(spanList);
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
		panel5.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel6.add(panel7, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		growStartButton = new JButton();
		Font growStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growStartButton.getFont());
		if (growStartButtonFont != null) growStartButton.setFont(growStartButtonFont);
		growStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32 (reversed).png")));
		growStartButton.setText("");
		panel7.add(growStartButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shrinkStartButton = new JButton();
		Font shrinkStartButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkStartButton.getFont());
		if (shrinkStartButtonFont != null) shrinkStartButton.setFont(shrinkStartButtonFont);
		shrinkStartButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32.png")));
		shrinkStartButton.setText("");
		panel7.add(shrinkStartButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		shrinkEndButton = new JButton();
		Font shrinkEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, shrinkEndButton.getFont());
		if (shrinkEndButtonFont != null) shrinkEndButton.setFont(shrinkEndButtonFont);
		shrinkEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-enter-32 (reversed).png")));
		shrinkEndButton.setText("");
		panel7.add(shrinkEndButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		growEndButton = new JButton();
		Font growEndButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, growEndButton.getFont());
		if (growEndButtonFont != null) growEndButton.setFont(growEndButtonFont);
		growEndButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-exit-32.png")));
		growEndButton.setText("");
		panel6.add(growEndButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		panel6.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel4.add(panel8, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel9 = new JPanel();
		panel9.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
		panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addAnnotationButton = new JButton();
		Font addAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, addAnnotationButton.getFont());
		if (addAnnotationButtonFont != null) addAnnotationButton.setFont(addAnnotationButtonFont);
		addAnnotationButton.setHorizontalTextPosition(0);
		addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addAnnotationButton.setText("");
		addAnnotationButton.setVerticalAlignment(0);
		addAnnotationButton.setVerticalTextPosition(3);
		panel9.add(addAnnotationButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		removeAnnotationButton = new JButton();
		Font removeAnnotationButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeAnnotationButton.getFont());
		if (removeAnnotationButtonFont != null) removeAnnotationButton.setFont(removeAnnotationButtonFont);
		removeAnnotationButton.setHorizontalTextPosition(0);
		removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeAnnotationButton.setText("");
		removeAnnotationButton.setVerticalTextPosition(3);
		panel9.add(removeAnnotationButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		previousSpanButton = new JButton();
		previousSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousSpanButton.setText("");
		panel9.add(previousSpanButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nextSpanButton = new JButton();
		nextSpanButton.setHorizontalAlignment(0);
		nextSpanButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextSpanButton.setText("");
		panel9.add(nextSpanButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		panel9.add(spacer4, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JScrollPane scrollPane4 = new JScrollPane();
		splitPane1.setLeftComponent(scrollPane4);
		knowtatorTextPane.setMinimumSize(new Dimension(200, 22));
		knowtatorTextPane.setPreferredSize(new Dimension(500, 500));
		scrollPane4.setViewportView(knowtatorTextPane);
		tabbedPane1 = new JTabbedPane();
		Font tabbedPane1Font = this.$$$getFont$$$("Verdana", Font.PLAIN, 14, tabbedPane1.getFont());
		if (tabbedPane1Font != null) tabbedPane1.setFont(tabbedPane1Font);
		tabbedPane1.setPreferredSize(new Dimension(788, 200));
		tabbedPane1.setTabLayoutPolicy(0);
		tabbedPane1.setTabPlacement(1);
		panel2.add(tabbedPane1, BorderLayout.NORTH);
		final JPanel panel10 = new JPanel();
		panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("File", panel10);
		final Spacer spacer5 = new Spacer();
		panel10.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel11 = new JPanel();
		panel11.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("Home", panel11);
		final JPanel panel12 = new JPanel();
		panel12.setLayout(new BorderLayout(0, 0));
		panel11.add(panel12, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		previousTextSourceButton = new JButton();
		previousTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousTextSourceButton.setText("");
		panel12.add(previousTextSourceButton, BorderLayout.WEST);
		textSourceChooser.setPreferredSize(new Dimension(150, 24));
		panel12.add(textSourceChooser, BorderLayout.CENTER);
		nextTextSourceButton = new JButton();
		nextTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextTextSourceButton.setText("");
		panel12.add(nextTextSourceButton, BorderLayout.EAST);
		final JPanel panel13 = new JPanel();
		panel13.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
		panel12.add(panel13, BorderLayout.SOUTH);
		addTextSourceButton = new JButton();
		addTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addTextSourceButton.setText("");
		panel13.add(addTextSourceButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		removeTextSourceButton = new JButton();
		removeTextSourceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeTextSourceButton.setText("");
		panel13.add(removeTextSourceButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer6 = new Spacer();
		panel13.add(spacer6, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer7 = new Spacer();
		panel13.add(spacer7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer8 = new Spacer();
		panel11.add(spacer8, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		fontSizeSlider = new JSlider();
		fontSizeSlider.setInverted(false);
		fontSizeSlider.setMajorTickSpacing(8);
		fontSizeSlider.setMaximum(28);
		fontSizeSlider.setMinimum(8);
		fontSizeSlider.setMinorTickSpacing(1);
		fontSizeSlider.setSnapToTicks(true);
		fontSizeSlider.setValue(16);
		panel11.add(fontSizeSlider, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel14 = new JPanel();
		panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel11.add(panel14, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		undoButton = new JButton();
		undoButton.setText("Undo");
		panel14.add(undoButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		redoButton = new JButton();
		redoButton.setText("Redo");
		panel14.add(redoButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel15 = new JPanel();
		panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel11.add(panel15, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		profileFilterCheckBox = new JCheckBox();
		Font profileFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, profileFilterCheckBox.getFont());
		if (profileFilterCheckBoxFont != null) profileFilterCheckBox.setFont(profileFilterCheckBoxFont);
		profileFilterCheckBox.setText("Profile");
		panel15.add(profileFilterCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		owlClassFilterCheckBox = new JCheckBox();
		Font owlClassFilterCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlClassFilterCheckBox.getFont());
		if (owlClassFilterCheckBoxFont != null) owlClassFilterCheckBox.setFont(owlClassFilterCheckBoxFont);
		owlClassFilterCheckBox.setText("OWL Class");
		panel15.add(owlClassFilterCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel16 = new JPanel();
		panel16.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel11.add(panel16, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		assignColorToClassButton = new JButton();
		assignColorToClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-color-dropper-filled-50 (Custom).png")));
		assignColorToClassButton.setText("");
		panel16.add(assignColorToClassButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		showGraphViewerButton = new JButton();
		Font showGraphViewerButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, showGraphViewerButton.getFont());
		if (showGraphViewerButtonFont != null) showGraphViewerButton.setFont(showGraphViewerButtonFont);
		showGraphViewerButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-tree-structure-32.png")));
		showGraphViewerButton.setText("");
		panel16.add(showGraphViewerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel17 = new JPanel();
		panel17.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("Profile", panel17);
		removeProfileButton = new JButton();
		removeProfileButton.setEnabled(true);
		Font removeProfileButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, removeProfileButton.getFont());
		if (removeProfileButtonFont != null) removeProfileButton.setFont(removeProfileButtonFont);
		removeProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		this.$$$loadButtonText$$$(removeProfileButton, ResourceBundle.getBundle("ui").getString("remove.profile"));
		panel17.add(removeProfileButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane5 = new JScrollPane();
		panel17.add(scrollPane5, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		Font profileListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, profileList.getFont());
		if (profileListFont != null) profileList.setFont(profileListFont);
		scrollPane5.setViewportView(profileList);
		final JLabel label7 = new JLabel();
		Font label7Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label7.getFont());
		if (label7Font != null) label7.setFont(label7Font);
		this.$$$loadLabelText$$$(label7, ResourceBundle.getBundle("ui").getString("profiles"));
		panel17.add(label7, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		profileNameField = new JTextField();
		Font profileNameFieldFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, profileNameField.getFont());
		if (profileNameFieldFont != null) profileNameField.setFont(profileNameFieldFont);
		panel17.add(profileNameField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(148, 24), null, 0, false));
		addProfileButton = new JButton();
		Font addProfileButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, addProfileButton.getFont());
		if (addProfileButtonFont != null) addProfileButton.setFont(addProfileButtonFont);
		addProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addProfileButton.setText("");
		panel17.add(addProfileButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane6 = new JScrollPane();
		panel17.add(scrollPane6, new GridConstraints(1, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		Font colorListFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, colorList.getFont());
		if (colorListFont != null) colorList.setFont(colorListFont);
		scrollPane6.setViewportView(colorList);
		final JLabel label8 = new JLabel();
		Font label8Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label8.getFont());
		if (label8Font != null) label8.setFont(label8Font);
		this.$$$loadLabelText$$$(label8, ResourceBundle.getBundle("ui").getString("colors1"));
		panel17.add(label8, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel18 = new JPanel();
		panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("Search", panel18);
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
		panel18.add(contentPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		Font searchTextFieldFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, searchTextField.getFont());
		if (searchTextFieldFont != null) searchTextField.setFont(searchTextFieldFont);
		contentPane.add(searchTextField, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 25), new Dimension(-1, 25), 0, false));
		final JPanel panel19 = new JPanel();
		panel19.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel19, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nextMatchButton = new JButton();
		Font nextMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, nextMatchButton.getFont());
		if (nextMatchButtonFont != null) nextMatchButton.setFont(nextMatchButtonFont);
		nextMatchButton.setText("Next");
		panel19.add(nextMatchButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		previousMatchButton = new JButton();
		Font previousMatchButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, previousMatchButton.getFont());
		if (previousMatchButtonFont != null) previousMatchButton.setFont(previousMatchButtonFont);
		previousMatchButton.setText("Previous");
		panel19.add(previousMatchButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		findTextInOntologyButton = new JButton();
		Font findTextInOntologyButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, findTextInOntologyButton.getFont());
		if (findTextInOntologyButtonFont != null) findTextInOntologyButton.setFont(findTextInOntologyButtonFont);
		this.$$$loadButtonText$$$(findTextInOntologyButton, ResourceBundle.getBundle("log4j").getString("find.in.ontology1"));
		contentPane.add(findTextInOntologyButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		Font onlyAnnotationsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, onlyAnnotationsCheckBox.getFont());
		if (onlyAnnotationsCheckBoxFont != null) onlyAnnotationsCheckBox.setFont(onlyAnnotationsCheckBoxFont);
		onlyAnnotationsCheckBox.setText("Only in Annotations");
		contentPane.add(onlyAnnotationsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font regexCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, regexCheckBox.getFont());
		if (regexCheckBoxFont != null) regexCheckBox.setFont(regexCheckBoxFont);
		regexCheckBox.setText("Regex");
		contentPane.add(regexCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font caseSensitiveCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, caseSensitiveCheckBox.getFont());
		if (caseSensitiveCheckBoxFont != null) caseSensitiveCheckBox.setFont(caseSensitiveCheckBoxFont);
		caseSensitiveCheckBox.setText("Case Sensitive");
		contentPane.add(caseSensitiveCheckBox, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer9 = new Spacer();
		contentPane.add(spacer9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel20 = new JPanel();
		panel20.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane1.addTab("Review", panel20);
		reviewTabbedPane = new JTabbedPane();
		panel20.add(reviewTabbedPane, new GridConstraints(1, 0, 3, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		final JPanel panel21 = new JPanel();
		panel21.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
		reviewTabbedPane.addTab("Text", panel21);
		final JLabel label9 = new JLabel();
		Font label9Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label9.getFont());
		if (label9Font != null) label9.setFont(label9Font);
		this.$$$loadLabelText$$$(label9, ResourceBundle.getBundle("log4j").getString("annotations.containing.text"));
		panel21.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane7 = new JScrollPane();
		panel21.add(scrollPane7, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane7.setViewportView(annotationsForSpannedTextList);
		panel21.add(annotationsContainingTextTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final Spacer spacer10 = new Spacer();
		panel21.add(spacer10, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		Font exactMatchCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, exactMatchCheckBox.getFont());
		if (exactMatchCheckBoxFont != null) exactMatchCheckBox.setFont(exactMatchCheckBoxFont);
		this.$$$loadButtonText$$$(exactMatchCheckBox, ResourceBundle.getBundle("log4j").getString("exact.match"));
		panel21.add(exactMatchCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel22 = new JPanel();
		panel22.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
		reviewTabbedPane.addTab("Concept", panel22);
		final JLabel label10 = new JLabel();
		Font label10Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label10.getFont());
		if (label10Font != null) label10.setFont(label10Font);
		this.$$$loadLabelText$$$(label10, ResourceBundle.getBundle("log4j").getString("annotations.for.owl.class"));
		panel22.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		owlClassLabel.setText("");
		panel22.add(owlClassLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane8 = new JScrollPane();
		panel22.add(scrollPane8, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane8.setViewportView(annotationsForClassList);
		final Spacer spacer11 = new Spacer();
		panel22.add(spacer11, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		Font includeClassDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includeClassDescendantsCheckBox.getFont());
		if (includeClassDescendantsCheckBoxFont != null)
			includeClassDescendantsCheckBox.setFont(includeClassDescendantsCheckBoxFont);
		this.$$$loadButtonText$$$(includeClassDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants"));
		panel22.add(includeClassDescendantsCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel23 = new JPanel();
		panel23.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		reviewTabbedPane.addTab("Relation", panel23);
		final JPanel panel24 = new JPanel();
		panel24.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
		panel23.add(panel24, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final Spacer spacer12 = new Spacer();
		panel24.add(spacer12, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label11 = new JLabel();
		Font label11Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label11.getFont());
		if (label11Font != null) label11.setFont(label11Font);
		this.$$$loadLabelText$$$(label11, ResourceBundle.getBundle("log4j").getString("relation.annotations.for.owl.objectproperty"));
		panel24.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font owlPropertyLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlPropertyLabel.getFont());
		if (owlPropertyLabelFont != null) owlPropertyLabel.setFont(owlPropertyLabelFont);
		owlPropertyLabel.setText("");
		panel24.add(owlPropertyLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane9 = new JScrollPane();
		panel24.add(scrollPane9, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane9.setViewportView(relationsForPropertyList);
		Font includePropertyDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includePropertyDescendantsCheckBox.getFont());
		if (includePropertyDescendantsCheckBoxFont != null)
			includePropertyDescendantsCheckBox.setFont(includePropertyDescendantsCheckBoxFont);
		this.$$$loadButtonText$$$(includePropertyDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants1"));
		panel24.add(includePropertyDescendantsCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		refreshReviewPaneButton = new JButton();
		refreshReviewPaneButton.setText("Refresh");
		panel20.add(refreshReviewPaneButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nextReviewObjectButton = new JButton();
		nextReviewObjectButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextReviewObjectButton.setText("");
		panel20.add(nextReviewObjectButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		previousReviewObjectButton = new JButton();
		previousReviewObjectButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousReviewObjectButton.setText("");
		panel20.add(previousReviewObjectButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer13 = new Spacer();
		panel20.add(spacer13, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel25 = new JPanel();
		panel25.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel25, "File");
		final Spacer spacer14 = new Spacer();
		panel25.add(spacer14, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer15 = new Spacer();
		panel25.add(spacer15, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer16 = new Spacer();
		panel25.add(spacer16, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		progressBar1 = new JProgressBar();
		panel25.add(progressBar1, new GridConstraints(2, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane10 = new JScrollPane();
		panel25.add(scrollPane10, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		fileList = new JList();
		Font fileListFont = this.$$$getFont$$$("Verdana", Font.BOLD, 14, fileList.getFont());
		if (fileListFont != null) fileList.setFont(fileListFont);
		final DefaultListModel defaultListModel1 = new DefaultListModel();
		defaultListModel1.addElement("Open");
		defaultListModel1.addElement("New");
		fileList.setModel(defaultListModel1);
		scrollPane10.setViewportView(fileList);
		final Spacer spacer17 = new Spacer();
		panel25.add(spacer17, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		backButton = new JButton();
		backButton.setText("Back");
		panel25.add(backButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer18 = new Spacer();
		panel25.add(spacer18, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer19 = new Spacer();
		panel25.add(spacer19, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer20 = new Spacer();
		panel25.add(spacer20, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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

	@Override
	public void filterChangedEvent() {

	}

	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		getModel().ifPresent(model1 -> event.getNew().ifPresent(o -> {
			if (o instanceof GraphSpace && isVisible()) {
				graphViewDialog.setVisible(true);
			}
		}));

		getModel().ifPresent(model1 -> {
			Optional optional = model1.getSelectedTextSource().map(TextSource::getSelectedAnnotation);
			if (optional.isPresent()) {
				spanSizeButtons.forEach(AbstractButton::addActionListener);
				selectionSizeButtons.forEach(AbstractButton::removeActionListener);
			} else {
				spanSizeButtons.forEach(AbstractButton::removeActionListener);
				selectionSizeButtons.forEach(AbstractButton::addActionListener);
			}
		});
	}

	private void open() {
		Optional<String> lastProjectFileNameOptional = Optional.ofNullable(KnowtatorView.PREFERENCES.get("Last Project", null));

		JFileChooser fileChooser = new JFileChooser();
		lastProjectFileNameOptional.ifPresent(lastProjectFileName -> {
			File lastProjectFile = new File(lastProjectFileName);
			if (lastProjectFile.exists()) {
				fileChooser.setCurrentDirectory(lastProjectFile);
				Optional<File[]> filesOptional = Optional.ofNullable(lastProjectFile.listFiles());
				filesOptional.ifPresent(files -> {
					Optional<File> f = Arrays.stream(files).filter(file -> file.getName().endsWith(".knowtator")).findAny();
					f.ifPresent(fileChooser::setSelectedFile);
				});
			}
		});
		FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
		fileChooser.setFileFilter(fileFilter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			progressBar1.setMaximum(100);
			progressBar1.setValue(0);
			progressBar1.setStringPainted(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			SwingWorker swingWorker = new Loader(this, file);
			swingWorker.addPropertyChangeListener(evt -> {
				String name = evt.getPropertyName();
				if (name.equals("progress")) {
					int progress = (int) evt.getNewValue();
					progressBar1.setValue(progress);
					tabbedPane1.repaint();
				} else if (name.equals("state")) {
					SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
					if (state == SwingWorker.StateValue.DONE) {
						setCursor(null);
						CardLayout cl = (CardLayout) panel1.getLayout();
						cl.show(panel1, "Main");
						tabbedPane1.setSelectedIndex(1);

					}
				}
			});
			swingWorker.execute();
		}
	}

	private void executeNew() {

		String projectName = JOptionPane.showInputDialog(this, "Enter a name for the project");

		if (!projectName.equals("")) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Select project root");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			fileChooser.addActionListener(e -> {
				if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {

					File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
					try {
						loadProject(projectDirectory, null);
						CardLayout cl = (CardLayout) panel1.getLayout();
						cl.show(panel1, "Main");
						tabbedPane1.setSelectedIndex(1);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			});
		}
	}


	@Override
	public void colorChangedEvent() {

	}
}
