package edu.ucdenver.ccp.knowtator.view;

import org.apache.log4j.Logger;

import javax.swing.text.*;
import java.awt.*;

/*
 *  Implements a simple highlight painter that renders a rectangle around the
 *  area to be highlighted.
 *
 */
class RectanglePainter extends DefaultHighlighter.DefaultHighlightPainter {

	public static Logger log = Logger.getLogger(RectanglePainter.class);

	RectanglePainter(Color color) {
		super(color);
	}

	/**
	 * Paints a portion of a highlight.
	 *
	 * @param g      the graphics context
	 * @param offs0  the starting model offset >= 0
	 * @param offs1  the ending model offset >= offs1
	 * @param bounds the bounding box of the view, which is not necessarily the region to paint.
	 * @param c      the editor
	 * @param view   View painting for
	 * @return region drawing occured in
	 */
	public Shape paintLayer(
			Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
		Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

		if (r == null) return null;

		//  Do your custom painting

		Color color = getColor();
		g.setColor(color == null ? c.getSelectionColor() : color);

		((Graphics2D) g).setStroke(new BasicStroke(4));

		//  Code is the same as the default highlighter except we use drawRect(...)

		//		g.fillRect(r.x, r.y, r.width, r.height);
		g.drawRect(r.x, r.y, r.width - 1, r.height - 1);

		// Return the drawing area

		return r;
	}

	private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
		// Contained in view, can just use bounds.

		if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
			Rectangle alloc;

			if (bounds instanceof Rectangle) {
				alloc = (Rectangle) bounds;
			} else {
				alloc = bounds.getBounds();
			}

			return alloc;
		} else {
			// Should only render part of View.
			try {
				// --- determine locations ---
				Shape shape =
						view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);

				return (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
			} catch (BadLocationException e) {
				// can't render
			}
		}

		// Can't render

		return null;
	}
}
