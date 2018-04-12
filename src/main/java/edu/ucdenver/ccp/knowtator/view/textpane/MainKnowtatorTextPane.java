package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.util.Set;

public class MainKnowtatorTextPane extends KnowtatorTextPane {
	public MainKnowtatorTextPane( KnowtatorView view) {
		super(view);
		view.getController().getProjectManager().addListener(this);
		getCaret().setSelectionVisible(true);
	}

	@Override
	void showTextPane(TextSource textSource) {
		setText(textSource.getContent());
		refreshHighlights();
	}

	@Override
	public void highlightSpan(
			int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter)
			throws BadLocationException {
		getHighlighter().addHighlight(start, end, highlighter);
	}

	@Override
	protected Set<Span> getSpans(Integer loc) {
		return view.getController()
				.getSelectionManager()
				.getActiveTextSource()
				.getAnnotationManager()
				.getSpans(loc, 0, getText().length());
	}
}
