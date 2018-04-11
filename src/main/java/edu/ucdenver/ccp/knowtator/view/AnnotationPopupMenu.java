package edu.ucdenver.ccp.knowtator.view;

import com.mxgraph.model.mxCell;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.Annotation;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Set;

class AnnotationPopupMenu extends JPopupMenu {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AnnotationPopupMenu.class);

	private MouseEvent e;
	private KnowtatorTextPane knowtatorTextPane;
	private KnowtatorController controller;

	AnnotationPopupMenu(
			MouseEvent e, KnowtatorTextPane knowtatorTextPane, KnowtatorController controller) {
		this.e = e;
		this.knowtatorTextPane = knowtatorTextPane;
		this.controller = controller;
	}

	private JMenuItem addAnnotationCommand() {
		JMenuItem menuItem = new JMenuItem("Add annotation");
		menuItem.addActionListener(e12 -> knowtatorTextPane.addAnnotation());

		return menuItem;
	}

	private JMenuItem addSpanToAnnotationCommand() {
		JMenuItem addSpanToAnnotation = new JMenuItem("Add span");
		addSpanToAnnotation.addActionListener(e4 -> knowtatorTextPane.addSpanToAnnotation());

		return addSpanToAnnotation;
	}

	private JMenuItem removeSpanFromAnnotationCommand() {
		JMenuItem removeSpanFromSelectedAnnotation =
				new JMenuItem(
						String.format(
								"Delete span from %s",
								controller.getSelectionManager().getSelectedAnnotation().getOwlClass()));
		removeSpanFromSelectedAnnotation.addActionListener(
				e5 ->
						controller
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.removeSpanFromAnnotation(
										controller.getSelectionManager().getSelectedAnnotation(),
										controller.getSelectionManager().getSelectedSpan()));

		return removeSpanFromSelectedAnnotation;
	}

	private JMenuItem selectAnnotationCommand(Annotation annotation, Span span) {
		JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + annotation.getOwlClass());
		selectAnnotationMenuItem.addActionListener(
				e3 -> controller.getSelectionManager().setSelected(span));

		return selectAnnotationMenuItem;
	}

	private JMenuItem removeAnnotationCommand() {
		JMenuItem removeAnnotationMenuItem =
				new JMenuItem(
						"Delete " + controller.getSelectionManager().getSelectedAnnotation().getOwlClass());
		removeAnnotationMenuItem.addActionListener(
				e4 -> {
					if (JOptionPane.showConfirmDialog(
							controller.getView(),
							"Are you sure you want to remove the selected annotation?",
							"Remove Annotation",
							JOptionPane.YES_NO_OPTION)
							== JOptionPane.YES_OPTION) {
						controller
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.addSelectedAnnotation();
					}
				});

		return removeAnnotationMenuItem;
	}

	void chooseAnnotation(Set<Span> spansContainingLocation) {
		// Menu items to select and remove annotations
		spansContainingLocation.forEach(
				span -> add(selectAnnotationCommand(span.getAnnotation(), span)));

		show(e.getComponent(), e.getX(), e.getY());
	}

	private JMenu goToAnnotationInGraphCommand() {
		JMenu jMenu = new JMenu("Graphs");

		Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
		TextSource textSource = controller.getSelectionManager().getActiveTextSource();
		for (GraphSpace graphSpace :
				textSource.getAnnotationManager().getGraphSpaceCollection().getData()) {
			mxCell vertex = graphSpace.containsVertexCorrespondingToAnnotation(annotation);
			if (vertex != null) {
				JMenuItem menuItem = new JMenuItem(graphSpace.getId());
				menuItem.addActionListener(
						e1 -> {
							controller.getView().getGraphViewDialog().setVisible(true);
							controller.getSelectionManager().setSelected(graphSpace);
							controller.getSelectionManager().setSelected(null, null);
							controller.getSelectionManager().setSelected(annotation, null);
						});
				jMenu.add(menuItem);
			}
		}

		return jMenu;
	}

	void showPopUpMenu(int release_offset) {

		Annotation selectedAnnotation = controller.getSelectionManager().getSelectedAnnotation();
		Span selectedSpan = controller.getSelectionManager().getSelectedSpan();

		if (knowtatorTextPane.getSelectionStart() <= release_offset
				&& release_offset <= knowtatorTextPane.getSelectionEnd()
				&& knowtatorTextPane.getSelectionStart() != knowtatorTextPane.getSelectionEnd()) {
			knowtatorTextPane.select(
					knowtatorTextPane.getSelectionStart(), knowtatorTextPane.getSelectionEnd());
			add(addAnnotationCommand());
			if (controller.getSelectionManager().getSelectedAnnotation() != null) {
				add(addSpanToAnnotationCommand());
			}
		} else if (selectedAnnotation != null
				&& selectedSpan.getStart() <= release_offset
				&& release_offset <= selectedSpan.getEnd()) {
			add(removeAnnotationCommand());
			if (controller.getSelectionManager().getSelectedSpan() != null
					&& controller.getSelectionManager().getSelectedAnnotation() != null
					&& controller
					.getSelectionManager()
					.getSelectedAnnotation()
					.getSpanCollection()
					.getData()
					.size()
					> 1) {
				add(removeSpanFromAnnotationCommand());
			}

			if (controller.getSelectionManager().getSelectedAnnotation() != null) {
				addSeparator();
				add(goToAnnotationInGraphCommand());
			}
		} else {
			return;
		}

		show(e.getComponent(), e.getX(), e.getY());
	}
}
