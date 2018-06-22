package edu.ucdenver.ccp.knowtator.view.menu;

import com.mxgraph.util.mxCellRenderer;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.graph.ActiveGraphSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphMenu extends JMenu {


	private KnowtatorView view;

	public GraphMenu( KnowtatorView view) {
		super("Graph");
		this.view = view;

		add(addNewGraphCommand());
		add(renameGraphCommand());
		add(saveToImageCommand());
		add(deleteGraphCommand());
	}

	private JMenuItem renameGraphCommand() {
		JMenuItem menuItem = new JMenuItem("Rename Graph");
		menuItem.addActionListener(
				e -> {
					String graphName = getGraphNameInput(view, null);
					if (graphName != null) {
						try {
							view.getController().getSelectionManager().getActiveTextSource().getGraphSpaceManager().getActiveGraphSpace().setId(graphName);
						} catch (ActiveTextSourceNotSetException | ControllerNotSetException | ActiveGraphSpaceNotSetException ignored) {

						}
					}
				});

		return menuItem;
	}

	private JMenuItem saveToImageCommand() {
		JMenuItem menuItem = new JMenuItem("Save as PNG");
		menuItem.addActionListener(
				e -> {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(view.getProjectManager().getProjectLocation());
					FileFilter fileFilter = new FileNameExtensionFilter("PNG", "png");
					fileChooser.setFileFilter(fileFilter);
					try {
						fileChooser.setSelectedFile(new File(view.getController().getSelectionManager().getActiveTextSource().getId() + "_" + view.getController().getSelectionManager().getActiveTextSource().getGraphSpaceManager().getActiveGraphSpace().getId() + ".png"));

					if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
						BufferedImage image =
								mxCellRenderer.createBufferedImage(
										view.getController().getSelectionManager().getActiveTextSource().getGraphSpaceManager().getActiveGraphSpace(),
										null,
										1,
										Color.WHITE,
										true,
										null);
						try {
							ImageIO.write(
									image, "PNG", new File(fileChooser.getSelectedFile().getAbsolutePath()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					} catch (ActiveTextSourceNotSetException | ControllerNotSetException | ActiveGraphSpaceNotSetException ignored) {
					}
				});

		return menuItem;
	}

	private JMenuItem deleteGraphCommand() {
		JMenuItem deleteGraphMenuItem = new JMenuItem("Delete graph");
		deleteGraphMenuItem.addActionListener(
				e -> {
					if (JOptionPane.showConfirmDialog(
							view, "Are you sure you want to delete this graph?")
							== JOptionPane.YES_OPTION) {
						try {
							view.getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getGraphSpaceManager().removeGraphSpace(view.getController().getSelectionManager().getActiveTextSource().getGraphSpaceManager().getActiveGraphSpace());
						} catch (ActiveTextSourceNotSetException | ControllerNotSetException | ActiveGraphSpaceNotSetException ignored) {

						}
					}
				});

		return deleteGraphMenuItem;
	}

	private JMenuItem addNewGraphCommand() {
		JMenuItem addNewGraphMenuItem = new JMenuItem("Create new graph");
		addNewGraphMenuItem.addActionListener(
				e -> {
					String graphName = getGraphNameInput(view, null);

					if (graphName != null) {
						try {
							view.getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getGraphSpaceManager().addGraphSpace(graphName);
						} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

						}
					}
				});

		return addNewGraphMenuItem;
	}

	public static String getGraphNameInput(KnowtatorView view, JTextField field1) {
		if (field1 == null) {
			field1 = new JTextField();

			JTextField finalField = field1;
			field1
					.getDocument()
					.addDocumentListener(
							new DocumentListener() {
								@Override
								public void insertUpdate(DocumentEvent e) {
									warn();
								}

								@Override
								public void removeUpdate(DocumentEvent e) {
									warn();
								}

								@Override
								public void changedUpdate(DocumentEvent e) {
									warn();
								}

								private void warn() {
									try {
										if (view.getController()
												.getSelectionManager()
												.getActiveTextSource()
												.getGraphSpaceManager().getGraphSpaceCollection()
												.containsID(finalField.getText())) {
											try {
												finalField
														.getHighlighter()
														.addHighlight(
																0,
																finalField.getText().length(),
																new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
											} catch (BadLocationException e1) {
												e1.printStackTrace();
											}
										} else {
											finalField.getHighlighter().removeAllHighlights();
										}
									} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

									}
								}
							});
		}
		Object[] message = {
				"Graph Title", field1,
		};
		field1.addAncestorListener(new RequestFocusListener());
		try {
			field1.setText("Graph Space " + Integer.toString(view.getController().getSelectionManager().getActiveTextSource().getGraphSpaceManager().getGraphSpaceCollection().size()));
		int option =
				JOptionPane.showConfirmDialog(
						view,
						message,
						"Enter a name for this graph",
						JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if (view.getController()
					.getSelectionManager()
					.getActiveTextSource()
					.getGraphSpaceManager().getGraphSpaceCollection()
					.containsID(field1.getText())) {
				JOptionPane.showMessageDialog(field1, "Graph name already in use");
				return getGraphNameInput(view, field1);
			} else {
				return field1.getText();
			}
		}

		} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {
		}
		return null;
	}

	/**
	 * Taken from https://tips4java.wordpress.com/2010/03/14/dialog-focus/
	 */
	private static class RequestFocusListener implements AncestorListener {
		private boolean removeListener;

		/*
		 *  Convenience constructor. The listener is only used once and then it is
		 *  removed from the component.
		 */
		RequestFocusListener() {
			this(true);
		}

		/*
		 *  Constructor that controls whether this listen can be used once or
		 *  multiple times.
		 *
		 *  @param removeListener when true this listener is only invoked once
		 *                        otherwise it can be invoked multiple times.
		 */
		RequestFocusListener(boolean removeListener) {
			this.removeListener = removeListener;
		}

		@Override
		public void ancestorAdded(AncestorEvent e) {
			JComponent component = e.getComponent();
			component.requestFocusInWindow();

			if (removeListener) component.removeAncestorListener(this);
		}

		@Override
		public void ancestorMoved(AncestorEvent e) {
		}

		@Override
		public void ancestorRemoved(AncestorEvent e) {
		}
	}
}
