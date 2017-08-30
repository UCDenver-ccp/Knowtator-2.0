package edu.ucdenver.cpbs.mechanic.ui;


import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotationManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MechAnICTextViewer extends JTextPane {
	private TextAnnotationManager textAnnotationManager;

	public MechAnICTextViewer(MechAnICView view) {
		super();
		this.setName("Untitled");
		this.setEditable(false);
		textAnnotationManager = new TextAnnotationManager(view, getName());
		setupListeners(this);
	}

	private void setupListeners(MechAnICTextViewer textViewer) {
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

						int start, end;
						try {
							if (press_offset < release_offset) {

								start = Utilities.getWordStart(textViewer, press_offset);
								end = Utilities.getWordEnd(textViewer, release_offset);
							} else {
								start = Utilities.getWordStart(textViewer, release_offset);
								end = Utilities.getWordEnd(textViewer, press_offset);
							}
							textViewer.getTextAnnotationManager().setSelectedAnnotation(start, end);
							textViewer.setSelectionStart(start);
							textViewer.setSelectionEnd(end);
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


	public TextAnnotationManager getTextAnnotationManager() {
		return textAnnotationManager;
	}


}
