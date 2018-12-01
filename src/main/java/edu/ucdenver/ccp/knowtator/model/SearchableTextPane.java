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

package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A text pane that has search functionality
 */
public abstract class SearchableTextPane extends JTextPane implements KnowtatorComponent, ModelListener {
	private Pattern pattern;
	private Matcher matcher;
	private final JTextField searchTextField;

	/**
	 * @param searchTextField A text field used to search the text pane
	 */
	SearchableTextPane(JTextField searchTextField) {
		super();
		this.searchTextField = searchTextField;
		addCaretListener(e -> {
			if (shouldUpdateSearchTextFieldCondition()) {
				searchTextField.setText(this.getSelectedText());
			}
		});


		pattern = Pattern.compile("");
		matcher = pattern.matcher("");
		searchTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				makePattern();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				makePattern();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});


	}

	/**
	 * @return boolean value. True if the text field should be updated
	 */
	protected abstract boolean shouldUpdateSearchTextFieldCondition();

	/**
	 * Searches from the end of the selection forward, wrapping at the end
	 */
	public void searchForward() {
		if (matcher != null) {
			matcher.reset();
			int matchStart = -1;
			int matchEnd = 0;
			int startBound = getSelectionEnd();
			while (matcher.find()) {
				if (matcher.start() > startBound && keepSearchingCondition(matcher)) {
					matchStart = matcher.start();
					matchEnd = matcher.end();
					break;
				}
			}
			if (matcher.hitEnd()) {
				matcher.reset();
				//noinspection ResultOfMethodCallIgnored
				matcher.find();
				if (keepSearchingCondition(matcher)) {
					matchStart = matcher.start();
					matchEnd = matcher.end();
				}
			}
			requestFocusInWindow();
			select(matchStart, matchEnd);
		}
	}

	/**
	 * @param matcher A matcher
	 * @return Boolean value. True if should keep searching
	 */
	protected abstract boolean keepSearchingCondition(Matcher matcher);

	/**
	 * Searches the text pane backward from the selection start, wrapping at the beginning
	 */
	public void searchPrevious() {
		if (matcher != null) {
			matcher.reset();
			int matchStart = -1;
			int matchEnd = 0;
			int endBound = getSelectionStart();
			while (matcher.find()) {
				if (matcher.start() < endBound || matcher.hitEnd() && keepSearchingCondition(matcher)) {
					matchStart = matcher.start();
					matchEnd = matcher.end();
				} else if (matchStart == -1) {
					endBound = getText().length();
				} else {
					break;
				}
			}
			requestFocusInWindow();
			select(matchStart, matchEnd);
		}
	}

	/**
	 * Resets the matcher to the pattern specified by the text field and pattern flags
	 */
	void makePattern() {
		//noinspection MagicConstant
		pattern = Pattern.compile(searchTextField.getText(), getPatternFlags());
		matcher.usePattern(pattern);
	}

	/**
	 * @return An int representing the flags for the pattern
	 */
	protected abstract int getPatternFlags();


	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		KnowtatorView.MODEL.getSelectedTextSource()
				.ifPresent(textSource -> textSource.getSelectedAnnotation()
						.ifPresent(conceptAnnotation -> conceptAnnotation
								.getSelection().ifPresent(span -> searchTextField
										.setText(span.getSpannedText()))));
		event.getNew().ifPresent(modelObject -> {
			if (modelObject instanceof TextSource) {
				matcher = pattern.matcher(((TextSource) modelObject).getContent());
			}
		});
	}
}
