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

package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.model.FilterModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.collection.ActionParameters;
import edu.ucdenver.ccp.knowtator.view.actions.model.ReassignOWLClassAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction.pickAction;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.ANNOTATION;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.SPAN;

/**
 * The text pane used for annotating and displaying concept annotations in Knowtator projects
 */
public class KnowtatorTextPane extends AnnotatableTextPane implements ColorListener, KnowtatorComponent, FilterModelListener, KnowtatorCollectionListener<Profile> {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

	private final KnowtatorView view;
	private final JCheckBox onlyInAnnotationsCheckBox;
	private final JCheckBox regexCheckBox;
	private final JCheckBox caseSensitiveCheckBox;

	/**
	 * @param view                      A Knowtator view
	 * @param searchTextField           A text field to use to search the text pane
	 * @param onlyInAnnotationsCheckBox A check box specifying whether to search in annotations or all text
	 * @param regexCheckBox             A check box specifying if the search pattern is a regular expression
	 * @param caseSensitiveCheckBox     A check box specifying if the search should be case sensitive
	 */
	public KnowtatorTextPane(KnowtatorView view, JTextField searchTextField, JCheckBox onlyInAnnotationsCheckBox, JCheckBox regexCheckBox, JCheckBox caseSensitiveCheckBox) {
		super(searchTextField);
		this.view = view;
		this.onlyInAnnotationsCheckBox = onlyInAnnotationsCheckBox;
		this.regexCheckBox = regexCheckBox;
		this.caseSensitiveCheckBox = caseSensitiveCheckBox;
		regexCheckBox.addItemListener(e -> makePattern());
		caseSensitiveCheckBox.addItemListener(e -> makePattern());
		onlyInAnnotationsCheckBox.addItemListener(e -> makePattern());
	}

	/**
	 * @return An image of the text pane
	 */
	public BufferedImage getScreenShot() {

		BufferedImage image =
				new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		// call the Component's paint method, using
		// the Graphics object of the image.
		paint(image.getGraphics()); // alternately use .printAll(..)
		return image;
	}


	protected void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
		AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e);
		textSourceOptional.ifPresent(textSource -> {

			Set<Span> spansContainingLocation = textSource.getConceptAnnotationCollection().getSpans(press_offset).getCollection();

			if (SwingUtilities.isRightMouseButton(e)) {
				if (spansContainingLocation.size() == 1) {
					Span span = spansContainingLocation.iterator().next();
					textSource.getConceptAnnotationCollection().setSelectedAnnotation(span);
				}
				popupMenu.showPopUpMenu(release_offset);
			} else if (press_offset == release_offset) {
				if (spansContainingLocation.size() == 1) {
					Span span = spansContainingLocation.iterator().next();
					textSource.getConceptAnnotationCollection().setSelectedAnnotation(span);
				} else if (spansContainingLocation.size() > 1) {
					popupMenu.chooseAnnotation(spansContainingLocation);
				}

			} else {
				setSelectionAtWordLimits(press_offset, release_offset);
			}
		});

	}

	@Override
	public void reset() {
		super.reset();
		setupListeners();
	}

	@Override
	public void setupListeners() {
		super.setupListeners();
		KnowtatorView.MODEL.getProfileCollection().addColorListener(this);
		KnowtatorView.MODEL.getProfileCollection().addCollectionListener(this);
		KnowtatorView.MODEL.addFilterModelListener(this);
	}


	@Override
	protected boolean shouldUpdateSearchTextFieldCondition() {
		return !regexCheckBox.isSelected();
	}

	@Override
	protected boolean keepSearchingCondition(Matcher matcher) {
		return textSourceOptional.map(textSource -> (!onlyInAnnotationsCheckBox.isSelected() || !(textSource.getConceptAnnotationCollection().getSpans(matcher.start()).size() == 0)))
				.orElse(false);
	}

	@Override
	protected int getPatternFlags() {
		return (regexCheckBox.isSelected() ? 0 : Pattern.LITERAL) | (caseSensitiveCheckBox.isSelected() ? 0 : Pattern.CASE_INSENSITIVE);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void colorChanged() {
		refreshHighlights();
	}

	@Override
	public void profileFilterChanged(boolean filterValue) {
		refreshHighlights();
	}

	@Override
	public void owlClassFilterChanged(boolean filterVale) {
		refreshHighlights();
	}

	@Override
	public void selected(SelectionEvent<Profile> event) {
		refreshHighlights();
	}

	@Override
	public void added() {
	}

	@Override
	public void removed() {
	}

	@Override
	public void emptied() {

	}

	@Override
	public void firstAdded() {

	}

	class AnnotationPopupMenu extends JPopupMenu {
		private final MouseEvent e;

		AnnotationPopupMenu(MouseEvent e) {
			this.e = e;
		}

		private JMenuItem reassignOWLClassCommand() {
			JMenuItem menuItem = new JMenuItem("Reassign OWL class");
			menuItem.addActionListener(e -> KnowtatorView.MODEL.getTextSource()
					.ifPresent(textSource1 -> textSource1.getConceptAnnotationCollection().getSelection()
							.ifPresent(conceptAnnotation -> {
								KnowtatorView.MODEL.getSelectedOWLClass()
										.ifPresent(owlClass -> KnowtatorView.MODEL
												.registerAction(new ReassignOWLClassAction(conceptAnnotation, owlClass)));
							})));

			return menuItem;
		}

		private JMenuItem addAnnotationCommand() {
			JMenuItem menuItem = new JMenuItem("Add concept");
			menuItem.addActionListener(e12 -> pickAction(view, null, null,
					new ActionParameters(ADD, ANNOTATION),
					new ActionParameters(ADD, SPAN)));

			return menuItem;
		}

		private JMenuItem removeSpanFromAnnotationCommand(ConceptAnnotation conceptAnnotation) {
			JMenuItem removeSpanFromSelectedAnnotation = new JMenuItem(String.format("Delete span from %s", conceptAnnotation.getOwlClass()));
			removeSpanFromSelectedAnnotation.addActionListener(e5 -> pickAction(view, null, null, new ActionParameters(REMOVE, SPAN)));

			return removeSpanFromSelectedAnnotation;
		}

		private JMenuItem selectAnnotationCommand(Span span) {
			JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + span.getConceptAnnotation().getOwlClassRendering());
			selectAnnotationMenuItem.addActionListener(e3 -> textSourceOptional.ifPresent(textSource -> textSource.getConceptAnnotationCollection().setSelectedAnnotation(span)));

			return selectAnnotationMenuItem;
		}

		private JMenuItem removeAnnotationCommand(ConceptAnnotation conceptAnnotation) {
			JMenuItem removeAnnotationMenuItem = new JMenuItem("Delete " + conceptAnnotation.getOwlClass());
			removeAnnotationMenuItem.addActionListener(e4 -> pickAction(view, null, null, new ActionParameters(REMOVE, ANNOTATION)));

			return removeAnnotationMenuItem;
		}

		void chooseAnnotation(Set<Span> spansContainingLocation) {
			// Menu items to select and remove annotations
			spansContainingLocation.forEach(span -> add(selectAnnotationCommand(span)));

			show(e.getComponent(), e.getX(), e.getY());
		}

		void showPopUpMenu(int release_offset) {
			if (getSelectionStart() <= release_offset && release_offset <= getSelectionEnd() && getSelectionStart() != getSelectionEnd()) {
				select(getSelectionStart(), getSelectionEnd());
				add(addAnnotationCommand());

				show(e.getComponent(), e.getX(), e.getY());
			} else {
				textSourceOptional.ifPresent(textSource -> textSource.getConceptAnnotationCollection().getSelection()
						.ifPresent(conceptAnnotation -> conceptAnnotation.getSpanCollection().getSelection()
								.filter(span -> span.getStart() <= release_offset && release_offset <= span.getEnd())
								.ifPresent(span -> clickedInsideSpan(conceptAnnotation))));
			}
		}

		private void clickedInsideSpan(ConceptAnnotation conceptAnnotation) {
			add(removeAnnotationCommand(conceptAnnotation));
			if (conceptAnnotation.getSpanCollection().size() > 1) {
				add(removeSpanFromAnnotationCommand(conceptAnnotation));
			}
			add(reassignOWLClassCommand());
			show(e.getComponent(), e.getX(), e.getY());
		}


	}


}
