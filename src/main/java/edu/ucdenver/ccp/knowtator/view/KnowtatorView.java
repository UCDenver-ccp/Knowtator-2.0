package edu.ucdenver.ccp.knowtator.view;

import com.mxgraph.swing.util.mxGraphTransferable;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.OWLClassSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Profile;
import edu.ucdenver.ccp.knowtator.model.ProjectManager;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
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
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

public class KnowtatorView extends AbstractOWLClassViewComponent
		implements DropTargetListener, OWLClassSelectionListener {

	private static final Logger log = Logger.getLogger(KnowtatorView.class);
	private final Preferences prefs = Preferences.userRoot().node("knowtator");
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
	private ProjectManager projectManager;

	public KnowtatorView() {
		projectManager = new ProjectManager();
		//		makeController();
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
	}

	public Preferences getPrefs() {
		return prefs;
	}

	private void makeController() {
		log.warn("KnowtatorView: Making controller");
		controller = new KnowtatorController();
		controller.getSelectionManager().addOWLEntityListener(this);
		controller.setProjectManager(projectManager);
		projectManager.setController(controller);
		infoPane.setController(controller);
		knowtatorTextPane.setController(controller);
		textSourceChooser.setController(controller);
		graphViewDialog.setController(controller);

		profileChooser.setController(controller);
		profileFilterCheckBox.addChangeListener(controller.getSelectionManager());
		setUpOWL();
	}

	private void setUpOWL() {
		OWLWorkspace workspace = null;

		try {
			workspace = controller.getOWLAPIDataExtractor().getWorkSpace();
		} catch (OWLWorkSpaceNotSetException ignored) {

		}
		if (workspace == null) {
			if (getOWLWorkspace() != null) {
				controller.getOWLAPIDataExtractor().setUpOWL(getOWLWorkspace());
				log.warn("Adding class label as renderer listener");
				getOWLWorkspace().getOWLModelManager().addListener(infoPane.getAnnotationClassLabel());
			}
		}
	}

	public KnowtatorController getController() throws ControllerNotSetException {
		if (controller == null) {
			throw new ControllerNotSetException();
		}
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

		findTextButton.addActionListener(
				e -> {
					try {
						getController()
								.getOWLAPIDataExtractor()
								.searchForString(knowtatorTextPane.getSelectedText());
					} catch (OWLWorkSpaceNotSetException | ControllerNotSetException ignored) {

					}
				});

		assignColorToClassButton.addActionListener(
				e -> {
					try {
						OWLEntity owlClass = getController().getSelectionManager().getSelectedOWLEntity();
						if (owlClass == null) {
							try {
								owlClass =
										getController()
												.getSelectionManager()
												.getActiveTextSource()
												.getAnnotationManager()
												.getSelectedAnnotation()
												.getOwlClass();
							} catch (ActiveTextSourceNotSetException ignored) {
							}
						}
						if (owlClass instanceof OWLClass) {
							Color c = JColorChooser.showDialog(this, "Pick a color for " + owlClass, Color.CYAN);
							if (c != null) {
								getController().getSelectionManager().getActiveProfile().addColor(owlClass, c);

								if (JOptionPane.showConfirmDialog(
										this, "Assign color to descendants of " + owlClass + "?")
										== JOptionPane.OK_OPTION) {
									try {
										Set<OWLClass> descendants =
												getController()
														.getOWLAPIDataExtractor()
														.getDescendants((OWLClass) owlClass);

										for (OWLClass descendant : descendants) {
											getController()
													.getSelectionManager()
													.getActiveProfile()
													.addColor(descendant, c);
										}
									} catch (OWLWorkSpaceNotSetException ignored) {
									}
								}
							}
						}
					} catch (ControllerNotSetException ignored) {

					}
				});

		growSelectionStartButton.addActionListener(
				(ActionEvent e) -> {
					try {
						if (getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.getSelectedSpan()
								== null) {
							knowtatorTextPane.growStart();
						} else {
							getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.growSelectedSpanStart();
						}
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {
					}
				});
		shrinkSelectionStartButton.addActionListener(
				(ActionEvent e) -> {
					try {
						if (getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.getSelectedSpan()
								== null) {
							knowtatorTextPane.shrinkStart();
						} else {
							getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.shrinkSelectedSpanStart();
						}
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});
		shrinkSelectionEndButton.addActionListener(
				(ActionEvent e) -> {
					try {
						if (getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.getSelectedSpan()
								== null) {
							knowtatorTextPane.shrinkEnd();
						} else {
							getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.shrinkSelectedSpanEnd();
						}
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});
		growSelectionEndButton.addActionListener(
				(ActionEvent e) -> {
					try {
						if (getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.getSelectedSpan()
								== null) {
							knowtatorTextPane.growEnd();
						} else {
							getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.growSelectedSpanEnd();
						}
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});

		previousSpanButton.addActionListener(
				(ActionEvent e) -> {
					if (projectManager.isProjectLoaded()) {
						try {
							getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.getPreviousSpan();
						} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

						}
					}
				});
		nextSpanButton.addActionListener(
				(ActionEvent e) -> {
					try {
						getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.getNextSpan();
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});

		showGraphViewerButton.addActionListener(
				e -> {
					if (projectManager.isProjectLoaded()) {
						graphViewDialog.setVisible(true);
					}
				});

		removeAnnotationButton.addActionListener(
				e -> {
					try {
						if (projectManager.isProjectLoaded()
								&& getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.getSelectedAnnotation()
								!= null) {
							if (getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.getSelectedAnnotation()
									.getSpanCollection()
									.size()
									> 1) {
								String[] buttons = {"Remove annotation", "Remove span from annotation", "Cancel"};
								int response =
										JOptionPane.showOptionDialog(
												this,
												"Choose an option",
												"Remove Annotation",
												JOptionPane.DEFAULT_OPTION,
												JOptionPane.QUESTION_MESSAGE,
												null,
												buttons,
												2);

								switch (response) {
									case 0:
										getController()
												.getSelectionManager()
												.getActiveTextSource()
												.removeSelectedAnnotation();
										break;
									case 1:
										getController()
												.getSelectionManager()
												.getActiveTextSource()
												.getAnnotationManager()
												.removeSpanFromSelectedAnnotation();
										break;
									case 2:
										break;
								}
							} else {
								if (JOptionPane.showConfirmDialog(
										this,
										"Are you sure you want to remove the selected annotation?",
										"Remove Annotation",
										JOptionPane.YES_NO_OPTION)
										== JOptionPane.YES_OPTION) {
									getController()
											.getSelectionManager()
											.getActiveTextSource()
											.removeSelectedAnnotation();
								}
							}
						}
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

					}
				});
		addAnnotationButton.addActionListener(
				e -> {
					if (projectManager.isProjectLoaded()) {
						try {
							if (getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.getSelectedAnnotation()
									!= null) {
								String[] buttons = {"Add new annotation", "Add span to annotation", "Cancel"};
								int response =
										JOptionPane.showOptionDialog(
												this,
												"Choose an option",
												"Add Annotation",
												JOptionPane.DEFAULT_OPTION,
												JOptionPane.PLAIN_MESSAGE,
												null,
												buttons,
												2);

								switch (response) {
									case 0:
										getController()
												.getSelectionManager()
												.getActiveTextSource()
												.getAnnotationManager()
												.addSelectedAnnotation();
										break;
									case 1:
										getController()
												.getSelectionManager()
												.getActiveTextSource()
												.getAnnotationManager()
												.addSpanToSelectedAnnotation();
										break;
									case 2:
										break;
								}

							} else {
								getController()
										.getSelectionManager()
										.getActiveTextSource()
										.getAnnotationManager()
										.addSelectedAnnotation();
							}
						} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

						}
					}
				});

		previousTextSourceButton.addActionListener(
				e -> {
					try {
						getController().getSelectionManager().getPreviousTextSource();
					} catch (ControllerNotSetException ignored) {

					}
				});
		nextTextSourceButton.addActionListener(
				e -> {
					try {
						getController().getSelectionManager().getNextTextSource();
					} catch (ControllerNotSetException ignored) {
					}
				});

		decreaseFontSizeButton.addActionListener(
				(ActionEvent e) -> knowtatorTextPane.decreaseFontSize());
		increaseFontSizeButton.addActionListener(
				(ActionEvent e) -> knowtatorTextPane.increaseFindSize());
		textSourceChooser.addActionListener(
				e -> {
					JComboBox comboBox = (JComboBox) e.getSource();
					if (comboBox.getSelectedItem() != null) {
						try {
							getController()
									.getSelectionManager()
									.setActiveTextSource((TextSource) comboBox.getSelectedItem());
						} catch (ControllerNotSetException ignored) {

						}
					}
				});
		profileChooser.addActionListener(
				e -> {
					JComboBox comboBox = (JComboBox) e.getSource();
					if (comboBox.getSelectedItem() != null) {
						try {
							getController()
									.getSelectionManager()
									.setSelectedProfile((Profile) comboBox.getSelectedItem());
						} catch (ControllerNotSetException ignored) {

						}
					}
				});
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
		makeController();
	}

	@Override
	public void disposeView() {
		if (projectManager.isProjectLoaded()
				&& JOptionPane.showConfirmDialog(
				this,
				"Save changes to Knowtator project?",
				"Save Project",
				JOptionPane.YES_NO_OPTION)
				== JOptionPane.YES_OPTION) {
			projectManager.saveProject();
		}
		if (controller != null) {
			controller.dispose();
		}
		//		infoPane.dispose();
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

	@Override
	public void owlEntityChanged(OWLEntity owlClass) {
		owlEntitySelectionChanged(owlClass);
	}

	public ProjectManager getProjectManager() {
		return projectManager;
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
		textPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
		textPanel.setMinimumSize(new Dimension(400, 350));
		textPanel.setPreferredSize(new Dimension(800, 200));
		panel1.add(textPanel, BorderLayout.CENTER);
		textPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null));
		textSourceToolBar = new JToolBar();
		textSourceToolBar.setFloatable(false);
		textPanel.add(textSourceToolBar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(2147483647, 50), 0, false));
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
		textPanel.add(annotationToolBar, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(2147483647, 50), 0, false));
		addAnnotationButton = new JButton();
		addAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
		addAnnotationButton.setMaximumSize(new Dimension(50, 50));
		addAnnotationButton.setMinimumSize(new Dimension(50, 50));
		addAnnotationButton.setPreferredSize(new Dimension(50, 50));
		addAnnotationButton.setText("");
		addAnnotationButton.setToolTipText(ResourceBundle.getBundle("ui").getString("add.annotation"));
		annotationToolBar.add(addAnnotationButton);
		removeAnnotationButton = new JButton();
		removeAnnotationButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
		removeAnnotationButton.setMaximumSize(new Dimension(50, 50));
		removeAnnotationButton.setMinimumSize(new Dimension(50, 50));
		removeAnnotationButton.setPreferredSize(new Dimension(50, 50));
		removeAnnotationButton.setText("");
		removeAnnotationButton.setToolTipText(ResourceBundle.getBundle("ui").getString("remove.annotation"));
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
		profileFilterCheckBox.setToolTipText(ResourceBundle.getBundle("ui").getString("filter.annotations.by.profile"));
		annotationToolBar.add(profileFilterCheckBox);
		final JToolBar toolBar1 = new JToolBar();
		toolBar1.setFloatable(false);
		textPanel.add(toolBar1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
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
		textPanel.add(splitPane1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
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
