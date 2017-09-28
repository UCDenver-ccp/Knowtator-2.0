package edu.ucdenver.ccp.knowtator.ui;



import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.ccp.knowtator.iaa.Annotation;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class KnowtatorTextPane extends JTextPane {

	private final KnowtatorView view;
	private final TextAnnotationManager textAnnotationManager;
	private static final Logger log = Logger.getLogger(KnowtatorView.class);

	public KnowtatorTextPane(KnowtatorView view) {
		super();
		this.view = view;
		this.setName("Untitled");
		this.setEditable(false);
		textAnnotationManager = new TextAnnotationManager(view, getName(), this);
		setupListeners(this);
	}

	private void setupListeners(KnowtatorTextPane textViewer) {
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
						release_offset = viewToModel(e.getPoint());
                        JPopupMenu popupMenu = new JPopupMenu();

						int start, end;
						try {
							if (press_offset < release_offset) {

								start = Utilities.getWordStart(textViewer, press_offset);
								end = Utilities.getWordEnd(textViewer, release_offset);
							} else {
								start = Utilities.getWordStart(textViewer, release_offset);
								end = Utilities.getWordEnd(textViewer, press_offset);
							}
                            textViewer.setSelectionStart(start);
                            textViewer.setSelectionEnd(end);

                            // Menu item to create new annotation
                            JMenuItem annotateWithCurrentSelectedClass = new JMenuItem("Annotate with current selected class");
                            annotateWithCurrentSelectedClass.addActionListener(e12 -> addTextAnnotation());
                            popupMenu.add(annotateWithCurrentSelectedClass);

                            // Menu items to select and remove annotations
                            for (Annotation a : textViewer.getTextAnnotationManager().getAnnotationsInRange(start, end)) {
                                JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s", a.getOwlClass()));
                                selectAnnotationMenuItem.addActionListener(e3 -> textAnnotationManager.setSelectedAnnotation(a));
                                popupMenu.add(selectAnnotationMenuItem);

                                JMenuItem removeAnnotationMenuItem = new JMenuItem(String.format("Remove %s", a.getOwlClass()));
                                removeAnnotationMenuItem.addActionListener(e4 -> textAnnotationManager.removeTextAnnotation(a));
                                popupMenu.add(removeAnnotationMenuItem);
                            }

                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
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

	private void addTextAnnotation() {
		OWLClass cls = view.getSelectionModel().getSelectedClass();
		if (cls != null) {
			try {
				textAnnotationManager.addTextAnnotation(cls, getSelectionStart(), getSelectionEnd());
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			log.error("No OWLClass selected");
		}
	}


	public TextAnnotationManager getTextAnnotationManager() {
		return textAnnotationManager;
	}


}
