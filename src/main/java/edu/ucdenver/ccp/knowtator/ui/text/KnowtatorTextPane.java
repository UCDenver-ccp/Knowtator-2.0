package edu.ucdenver.ccp.knowtator.ui.text;


import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.text.Span;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import org.apache.log4j.Logger;
import other.RectanglePainter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

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


								if(e.getClickCount() == 2) {
									List<Annotation> selectedAnnotations = manager.getAnnotationManager().getAnnotationsInRange(getName(), start, end);
									if (selectedAnnotations.size() == 1) {

										Span selectedSpan = selectedAnnotations.get(0).getTextSpanInRange(start, end);

										textPane.setSelectionStart(selectedSpan.getStart());
										textPane.setSelectionEnd(selectedSpan.getEnd());

										manager.getAnnotationManager().setSelectedAnnotation(selectedAnnotations.get(0));

									} else {
										showPopUpMenu(e);
									}
								} else {
									textPane.setSelectionStart(start);
									textPane.setSelectionEnd(end);
								}

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

		// Menu item to create new Annotation
		JMenuItem annotateWithCurrentSelectedClass = new JMenuItem("Annotate with current selected class");
		annotateWithCurrentSelectedClass.addActionListener(e12 -> addTextAnnotation());
		popupMenu.add(annotateWithCurrentSelectedClass);

		// Menu items to select and remove annotations
		for (Annotation a : manager.getAnnotationManager().getAnnotationsInRange(getName(), getSelectionStart(), getSelectionEnd())) {
			JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s", a.getClassName()));
			selectAnnotationMenuItem.addActionListener(e3 -> manager.getAnnotationManager().setSelectedAnnotation(a));
			popupMenu.add(selectAnnotationMenuItem);

			JMenuItem removeAnnotationMenuItem = new JMenuItem(String.format("Remove %s", a.getClassName()));
			removeAnnotationMenuItem.addActionListener(e4 -> manager.getAnnotationManager().removeAnnotation(getName(), a));
			popupMenu.add(removeAnnotationMenuItem);
		}

		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	public void addTextAnnotation() {
		manager.getAnnotationManager().addAnnotation(getName(), OWLAPIDataExtractor.getSelectedClassName(manager), getSelectionStart(), getSelectionEnd());
	}

	public void highlightAnnotation(int spanStart, int spanEnd, String className, Boolean selectAnnotation) {
		DefaultHighlighter.DefaultHighlightPainter highlighter = manager.getAnnotatorManager().getCurrentAnnotator().getHighlighter(className);
		if (selectAnnotation) {
			if (highlighter != null) {
				highlighter = new RectanglePainter(highlighter.getColor().darker());
			}
		}

		try {
			getHighlighter().addHighlight(spanStart, spanEnd, highlighter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
