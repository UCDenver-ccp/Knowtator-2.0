package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.util.Set;

public class MainKnowtatorTextPane extends KnowtatorTextPane {
	public MainKnowtatorTextPane(KnowtatorController controller) {
		super(controller);
		controller.getProjectManager().addListener(this);
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
		return controller
				.getSelectionManager()
				.getActiveTextSource()
				.getAnnotationManager()
				.getSpans(loc, 0, getText().length());
	}
}
