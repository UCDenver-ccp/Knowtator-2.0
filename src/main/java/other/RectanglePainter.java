/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package other;

import org.apache.log4j.Logger;

import javax.swing.text.*;
import java.awt.*;

/*
 *  Implements a simple highlight painter that renders a rectangle around the
 *  area to be highlighted.
 *
 */
public class RectanglePainter extends DefaultHighlighter.DefaultHighlightPainter {

    public static Logger log = Logger.getLogger(RectanglePainter.class);

    public RectanglePainter(Color color) {
        super( color );
    }

    /**
     * Paints a portion of a highlight.
     *
     * @param  g the graphics context
     * @param  offs0 the starting model offset >= 0
     * @param  offs1 the ending model offset >= offs1
     * @param  bounds the bounding box of the view, which is not
     *	       necessarily the region to paint.
     * @param  c the editor
     * @param  view View painting for
     * @return region drawing occured in
     */
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
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
                alloc = (Rectangle)bounds;
            } else {
                alloc = bounds.getBounds();
            }

            return alloc;
        } else {
            // Should only render part of View.
            try {
                // --- determine locations ---
                Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,Position.Bias.Backward, bounds);

                return (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
            } catch (BadLocationException e) {
                // can't render
            }
        }

        // Can't render

        return null;
    }
}
