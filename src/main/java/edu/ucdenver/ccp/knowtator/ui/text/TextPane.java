package edu.ucdenver.ccp.knowtator.ui.text;


import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TextPane extends JTextPane{

	public static final Logger log = Logger.getLogger(KnowtatorManager.class);

	private BasicKnowtatorView view;
	private TextSource textSource;
	private Span selectedSpan;
	private Annotation selectedAnnotation;

	TextPane(BasicKnowtatorView view, TextSource textSource) {
		super();
		this.view = view;
		this.textSource = textSource;

		selectedSpan = Span.makeDefaultSpan();
		this.setEditable(false);

		setupListeners();
	}

	private void setupListeners() {
		addMouseListener(
				new MouseListener() {
					int press_offset;

					@Override
					public void mousePressed(MouseEvent e) {
						press_offset = viewToModel(e.getPoint());
					}

					@Override
					public void mouseReleased(MouseEvent e) { handleMouseRelease(e, press_offset, viewToModel(e.getPoint())); }

					@Override
					public void mouseEntered(MouseEvent e) {

					}

					@Override
					public void mouseExited(MouseEvent e) {

					}

					@Override
					public void mouseClicked(MouseEvent e) {

					}
				}
		);
	}

	private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
		if(e.isPopupTrigger()) {
			showPopUpMenu(e, release_offset);
			return;
		}
		if (press_offset == release_offset) {


			Map<Span, Annotation> spansContainingLocation = textSource.getAnnotationsContainingLocation(press_offset);
			if (spansContainingLocation.size() == 1) {
				Map.Entry<Span, Annotation> entry = spansContainingLocation.entrySet().iterator().next();
				setSelection(entry.getKey(), entry.getValue());

			} else if (spansContainingLocation.size() > 1) {
				showSelectAnnotationPopUpMenu(e, spansContainingLocation);
			}
		} else {
			setSelectionAtWordLimits(press_offset, release_offset);
		}
	}

	private void setSelectedSpan(Span span) {
		selectedSpan = span;
		view.spanSelectionChangedEvent(selectedSpan);
	}

	private void setSelectedAnnotation(Annotation annotation) {
		selectedAnnotation = annotation;
		if (selectedAnnotation != null) view.owlEntitySelectionChanged(OWLAPIDataExtractor.getOWLClassByName(view, selectedAnnotation.getClassName()));
		view.annotationSelectionChangedEvent(selectedAnnotation);
	}

	private void setSelection(Span span, Annotation annotation) {
		setSelectedSpan(span);

		setSelectedAnnotation(annotation);
	}

	private void setSelectionAtWordLimits(int press_offset, int release_offset) {

		try {
			int start = Utilities.getWordStart(this, min(press_offset, release_offset));
			int end = Utilities.getWordEnd(this, max(press_offset, release_offset));

			setSelectedSpan(new Span(start, end));

		} catch (BadLocationException e) {
			e.printStackTrace();
		}


	}

	private void showSelectAnnotationPopUpMenu(MouseEvent e, Map<Span, Annotation> spansContationLocation) {
		JPopupMenu popupMenu = new JPopupMenu();

		// Menu items to select and remove annotations
		spansContationLocation.forEach((span, annotation) -> {
			JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s", annotation.getClassName()));
			selectAnnotationMenuItem.addActionListener(e3 -> setSelection(span, annotation));
			popupMenu.add(selectAnnotationMenuItem);
		});

		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void showPopUpMenu(MouseEvent e, int releaseOffset) {
		JPopupMenu popupMenu = new JPopupMenu();

		// Menu item to create new annotation
		JMenuItem annotateWithCurrentSelectedClass = new JMenuItem("Annotate with current selected class");
		annotateWithCurrentSelectedClass.addActionListener(e12 -> addAnnotation());
		popupMenu.add(annotateWithCurrentSelectedClass);

		if (selectedSpan != null) {

			if (getSelectionStart() != getSelectionEnd()) {
				JMenuItem addSpanToSelectedAnnotation = new JMenuItem("Add span to selected annotation");
				addSpanToSelectedAnnotation.addActionListener(e4 -> addSpan());
				popupMenu.add(addSpanToSelectedAnnotation);

				JMenuItem removeSpanFromSelectedAnnotation = new JMenuItem("Remove span from selected annotation");
				removeSpanFromSelectedAnnotation.addActionListener(e5 -> removeSpan());
			}
		}
		// Menu items to select and remove annotations
		textSource.getAnnotationManager().getAnnotationsContainingLocation(releaseOffset).forEach((span, annotation) -> {
			JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s", annotation.getClassName()));
			selectAnnotationMenuItem.addActionListener(e3 -> setSelection(span, annotation));
			popupMenu.add(selectAnnotationMenuItem);

			JMenuItem removeAnnotationMenuItem = new JMenuItem(String.format("Remove %s", annotation.getClassName()));
			removeAnnotationMenuItem.addActionListener(e4 -> textSource.removeAnnotation(annotation));
			popupMenu.add(removeAnnotationMenuItem);
		});

		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void removeSpan() {
		textSource.getAnnotationManager().removeSpanFromSelectedAnnotation(selectedAnnotation, getSelectionStart(), getSelectionEnd());
	}

	private void addSpan() {
		textSource.addSpanToSelectedAnnotation(selectedAnnotation, getSelectionStart(), getSelectionEnd());
	}

	private void addAnnotation() {
		textSource.addAnnotation(getSelectionStart(), getSelectionEnd());
	}

	public TextSource getTextSource() {
		return textSource;
	}

	public Annotation getSelectedAnnotation() {
		return selectedAnnotation;
	}

	public void previousSpan() {
		Map.Entry<Span, Annotation> previous = textSource.getPreviousSpan(selectedSpan);
		setSelection(previous.getKey(), previous.getValue());
	}

	public void nextSpan() {
		Map.Entry<Span, Annotation> next = textSource.getNextSpan(selectedSpan);
		setSelection(next.getKey(), next.getValue());
	}

	public void shrinkSelectionEnd() {
		textSource.getAnnotationManager().shrinkSpanEnd(selectedSpan);
		setSelectedSpan(selectedSpan);

	}
	public void shrinkSelectionStart() {
		textSource.getAnnotationManager().shrinkSpanStart(selectedSpan);
		setSelectedSpan(selectedSpan);
	}
	public void growSelectionEnd() {
		textSource.getAnnotationManager().growSpanEnd(selectedSpan, getText().length());
		setSelectedSpan(selectedSpan);
	}
	public void growSelectionStart() {
		textSource.getAnnotationManager().growSpanStart(selectedSpan);
		setSelectedSpan(selectedSpan);
	}

	public Span getSelectedSpan() {
		return selectedSpan;
	}
}
