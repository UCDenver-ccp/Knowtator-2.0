package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.events.GraphSpaceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceListener;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.util.Set;

public class GraphViewKnowtatorTextPane extends KnowtatorTextPane implements GraphSpaceListener, GraphSpaceSelectionListener {

	private int start;
	private int end;

	public GraphViewKnowtatorTextPane(KnowtatorView view) {
		super(view);
		view.getController().getSelectionManager().addGraphSpaceListener(this);
	}

	@Override
	public void showTextPane(TextSource textSource) {
		setText(textSource.getContent().substring(start, end));
		refreshHighlights();
	}

	@Override
	public void highlightSpan(
			int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter)
			throws BadLocationException {
		if (start >= this.start && end <= this.end) {
			getHighlighter().addHighlight(start - this.start, end - this.start, highlighter);
		}
	}

	@Override
	protected Set<Span> getSpans(Integer loc) {
		return view.getController()
				.getSelectionManager()
				.getActiveTextSource()
				.getAnnotationManager()
				.getSpans(loc == null ? null : loc + start, start, end);
	}

	@Override
	public int getSelectionStart() {
		return super.getSelectionStart() + start;
	}

	@Override
	public int getSelectionEnd() {
		return super.getSelectionEnd() + start;
	}

	@Override
	public void graphTextChanged(TextSource textSource, int start, int end) {
		this.start = start;
		this.end = end;
		showTextPane(textSource);
	}

	@Override
	public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
		if (e.getOld() != null) {
			e.getOld().removeGraphSpaceListener(this);
		}
		e.getNew().addGraphSpaceListener(this);
		this.start = e.getNew().getGraphTextStart();
		this.end = e.getNew().getGraphTextEnd();
		showTextPane(view.getController().getSelectionManager().getActiveTextSource());
	}
}
