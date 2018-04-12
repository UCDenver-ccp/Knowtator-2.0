package edu.ucdenver.ccp.knowtator.view.menu;

import com.mxgraph.model.mxCell;
import edu.ucdenver.ccp.knowtator.model.Annotation;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Set;

public class AnnotationPopupMenu extends JPopupMenu {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AnnotationPopupMenu.class);

	private MouseEvent e;
	private KnowtatorView view;

	public AnnotationPopupMenu(
			MouseEvent e, KnowtatorView view) {
		this.e = e;
		this.view = view;
	}

	private JMenuItem addAnnotationCommand() {
		JMenuItem menuItem = new JMenuItem("Add annotation");
		menuItem.addActionListener(
				e12 ->
						view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.addSelectedAnnotation());

		return menuItem;
	}

	private JMenuItem addSpanToAnnotationCommand() {
		JMenuItem addSpanToAnnotation = new JMenuItem("Add span");
		addSpanToAnnotation.addActionListener(e4 -> view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().addSpanToSelectedAnnotation());

		return addSpanToAnnotation;
	}

	private JMenuItem removeSpanFromAnnotationCommand() {
		JMenuItem removeSpanFromSelectedAnnotation =
				new JMenuItem(
						String.format(
								"Delete span from %s",
								view.getController().getSelectionManager().getSelectedAnnotation().getOwlClass()));
		removeSpanFromSelectedAnnotation.addActionListener(
				e5 ->
						view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.removeSpanFromAnnotation(
										view.getController().getSelectionManager().getSelectedAnnotation(),
										view.getController().getSelectionManager().getSelectedSpan()));

		return removeSpanFromSelectedAnnotation;
	}

	private JMenuItem selectAnnotationCommand(Annotation annotation, Span span) {
		JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + annotation.getOwlClass());
		selectAnnotationMenuItem.addActionListener(
				e3 -> view.getController().getSelectionManager().setSelected(span));

		return selectAnnotationMenuItem;
	}

	private JMenuItem removeAnnotationCommand() {
		JMenuItem removeAnnotationMenuItem =
				new JMenuItem(
						"Delete " + view.getController().getSelectionManager().getSelectedAnnotation().getOwlClass());
		removeAnnotationMenuItem.addActionListener(
				e4 -> {
					if (JOptionPane.showConfirmDialog(
							view,
							"Are you sure you want to remove the selected annotation?",
							"Remove Annotation",
							JOptionPane.YES_NO_OPTION)
							== JOptionPane.YES_OPTION) {
						view.getController()
								.getSelectionManager()
								.getActiveTextSource()
								.getAnnotationManager()
								.addSelectedAnnotation();
					}
				});

		return removeAnnotationMenuItem;
	}

	public void chooseAnnotation(Set<Span> spansContainingLocation) {
		// Menu items to select and remove annotations
		spansContainingLocation.forEach(
				span -> add(selectAnnotationCommand(span.getAnnotation(), span)));

		show(e.getComponent(), e.getX(), e.getY());
	}

	private JMenu goToAnnotationInGraphCommand() {
		JMenu jMenu = new JMenu("Graphs");

		Annotation annotation = view.getController().getSelectionManager().getSelectedAnnotation();
		TextSource textSource = view.getController().getSelectionManager().getActiveTextSource();
		for (GraphSpace graphSpace :
				textSource.getAnnotationManager().getGraphSpaceCollection().getCollection()) {
			mxCell vertex = graphSpace.containsVertexCorrespondingToAnnotation(annotation);
			if (vertex != null) {
				JMenuItem menuItem = new JMenuItem(graphSpace.getId());
				menuItem.addActionListener(
						e1 -> {
							view.getGraphViewDialog().setVisible(true);
							view.getController().getSelectionManager().setSelected(graphSpace);
							view.getController().getSelectionManager().setSelected(null, null);
							view.getController().getSelectionManager().setSelected(annotation, null);
						});
				jMenu.add(menuItem);
			}
		}

		return jMenu;
	}

	public void showPopUpMenu(int release_offset) {

		Annotation selectedAnnotation = view.getController().getSelectionManager().getSelectedAnnotation();
		Span selectedSpan = view.getController().getSelectionManager().getSelectedSpan();

		if (view.getKnowtatorTextPane().getSelectionStart() <= release_offset
				&& release_offset <= view.getKnowtatorTextPane().getSelectionEnd()
				&& view.getKnowtatorTextPane().getSelectionStart() != view.getKnowtatorTextPane().getSelectionEnd()) {
			view.getKnowtatorTextPane().select(
					view.getKnowtatorTextPane().getSelectionStart(), view.getKnowtatorTextPane().getSelectionEnd());
			add(addAnnotationCommand());
			if (view.getController().getSelectionManager().getSelectedAnnotation() != null) {
				add(addSpanToAnnotationCommand());
			}
		} else if (selectedAnnotation != null
				&& selectedSpan.getStart() <= release_offset
				&& release_offset <= selectedSpan.getEnd()) {
			add(removeAnnotationCommand());
			if (view.getController().getSelectionManager().getSelectedSpan() != null
					&& view.getController().getSelectionManager().getSelectedAnnotation() != null
					&& view.getController()
					.getSelectionManager()
					.getSelectedAnnotation()
					.getSpanCollection()
					.getCollection()
					.size()
					> 1) {
				add(removeSpanFromAnnotationCommand());
			}

			if (view.getController().getSelectionManager().getSelectedAnnotation() != null) {
				addSeparator();
				add(goToAnnotationInGraphCommand());
			}
		} else {
			return;
		}

		show(e.getComponent(), e.getX(), e.getY());
	}
}
