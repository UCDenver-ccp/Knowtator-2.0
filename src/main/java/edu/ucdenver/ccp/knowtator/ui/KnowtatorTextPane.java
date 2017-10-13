package edu.ucdenver.ccp.knowtator.ui;



import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class KnowtatorTextPane extends JTextPane {

	public final KnowtatorManager manager;
	public static final Logger log = Logger.getLogger(KnowtatorManager.class);

	public KnowtatorTextPane(KnowtatorManager manager) {
		super();
		this.manager = manager;
		this.setName("Untitled");
		this.setEditable(false);
		setupListeners();
	}

	public void setupListeners() {
		KnowtatorTextPane textPane = this;
		addMouseListener(
				new MouseListener() {
					int press_offset;
					int release_offset;
					@Override
					public void mouseClicked(MouseEvent e) {

					}

					@Override
					public void mousePressed(MouseEvent e) {
						press_offset = viewToModel(e.getPoint());
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						if(e.isPopupTrigger()) {
							showPopUpMenu(e);
						}
						else {

							release_offset = viewToModel(e.getPoint());

							int start, end;
							try {
								if (press_offset < release_offset) {

									start = Utilities.getWordStart(textPane, press_offset);
									end = Utilities.getWordEnd(textPane, release_offset);
								} else {
									start = Utilities.getWordStart(textPane, release_offset);
									end = Utilities.getWordEnd(textPane, press_offset);
								}
								textPane.setSelectionStart(start);
								textPane.setSelectionEnd(end);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {

					}

					@Override
					public void mouseExited(MouseEvent e) {

					}
				}
		);
	}

	public void showPopUpMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();

		// Menu item to create new annotation
		JMenuItem annotateWithCurrentSelectedClass = new JMenuItem("Annotate with current selected class");
		annotateWithCurrentSelectedClass.addActionListener(e12 -> addTextAnnotation());
		popupMenu.add(annotateWithCurrentSelectedClass);

		// Menu items to select and remove annotations
		for (TextAnnotation a : manager.getTextAnnotationManager().getAnnotationsInRange(getSelectionStart(), getSelectionEnd())) {
			JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s", a.getOwlClass()));
			selectAnnotationMenuItem.addActionListener(e3 -> manager.getTextAnnotationManager().setSelectedTextAnnotation(a));
			popupMenu.add(selectAnnotationMenuItem);

			JMenuItem removeAnnotationMenuItem = new JMenuItem(String.format("Remove %s", a.getOwlClass()));
			removeAnnotationMenuItem.addActionListener(e4 -> manager.getTextAnnotationManager().removeTextAnnotation(getName(), a));
			popupMenu.add(removeAnnotationMenuItem);
		}

		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void addTextAnnotation() {
		OWLClass cls = manager.getOwlWorkspace().getOWLSelectionModel().getLastSelectedClass();
		if (cls != null) {
			try {
				manager.getTextAnnotationManager().addTextAnnotation(getName(), cls, getSelectionStart(), getSelectionEnd());
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			log.error("No OWLClass selected");
		}
	}

	public void highlightAnnotation(int spanStart, int spanEnd, OWLClass cls) {
		DefaultHighlighter.DefaultHighlightPainter highlighter = manager.getAnnotatorManager().getCurrentAnnotator().getHighlighter(cls);

		try {
			getHighlighter().addHighlight(spanStart, spanEnd, highlighter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
