/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.List;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;

/**
 * Modification of the standard JColorChooser by Steve Wilson to make it easier to choose colors
 * that suit text annotation.
 *
 * @author Harrison Pielke-Lombardo
 * @see JColorChooser
 */
public class KnowtatorColorPalette extends JColorChooser {

  /** Instantiates a new Knowtator color palette. */
  public KnowtatorColorPalette() {
    super();
    AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[6];
    panels[0] = new KnowtatorSwatchChooserPanel();
    System.arraycopy(ColorChooserComponentFactory.getDefaultChooserPanels(), 0, panels, 1, 5);

    setChooserPanels(panels);
    setPreviewPanel(new KnowtatorPreviewPanel());
  }

  /** A modification to the default swatch panel. */
  class KnowtatorSwatchChooserPanel extends AbstractColorChooserPanel {

    /** The Swatch panel. */
    SwatchPanel swatchPanel;
    /** The Main swatch listener. */
    MouseListener mainSwatchListener;

    private KeyListener mainSwatchKeyListener;

    /** Instantiates a new Knowtator swatch chooser panel. */
    KnowtatorSwatchChooserPanel() {
      super();
      setInheritsPopupMenu(true);
    }

    public String getDisplayName() {
      return "Recommended Colors";
    }

    protected void buildChooser() {

      swatchPanel = new MainSwatchPanel();
      swatchPanel.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, getDisplayName());
      swatchPanel.setInheritsPopupMenu(true);

      mainSwatchKeyListener = new MainSwatchKeyListener();
      mainSwatchListener = new MainSwatchListener();
      swatchPanel.addMouseListener(mainSwatchListener);
      swatchPanel.addKeyListener(mainSwatchKeyListener);

      JPanel mainHolder = new JPanel(new BorderLayout());
      Border border = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
      mainHolder.setBorder(border);
      mainHolder.add(swatchPanel, BorderLayout.CENTER);

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.LAST_LINE_START;
      gbc.gridwidth = 1;
      gbc.gridheight = 2;
      Insets oldInsets = gbc.insets;
      gbc.insets = new Insets(0, 0, 0, 10);

      GridBagLayout gb = new GridBagLayout();
      JPanel superHolder = new JPanel(gb);
      superHolder.add(mainHolder, gbc);
      gbc.insets = oldInsets;

      gbc.gridwidth = GridBagConstraints.REMAINDER;

      gbc.weighty = 0;
      gbc.gridheight = GridBagConstraints.REMAINDER;
      gbc.insets = new Insets(0, 0, 0, 2);
      superHolder.setInheritsPopupMenu(true);

      add(superHolder);
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
      super.uninstallChooserPanel(enclosingChooser);
      swatchPanel.removeMouseListener(mainSwatchListener);
      swatchPanel.removeKeyListener(mainSwatchKeyListener);

      swatchPanel = null;
      mainSwatchListener = null;
      mainSwatchKeyListener = null;

      removeAll(); // strip out all the sub-components
    }

    private class MainSwatchKeyListener extends KeyAdapter {

      public void keyPressed(KeyEvent e) {
        if (KeyEvent.VK_SPACE == e.getKeyCode()) {
          Color color = swatchPanel.getSelectedColor();
          getColorSelectionModel().setSelectedColor(color);
        }
      }
    }

    /** The type Main swatch listener. */
    class MainSwatchListener extends MouseAdapter implements Serializable {
      public void mousePressed(MouseEvent e) {
        if (isEnabled()) {
          Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
          getColorSelectionModel().setSelectedColor(color);
          swatchPanel.setSelectedColorFromLocation(e.getX(), e.getY());
          swatchPanel.requestFocusInWindow();
        }
      }
    }

    @Override
    public void updateChooser() {}

    @Override
    public Icon getSmallDisplayIcon() {
      return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
      return null;
    }
  }

  /**
   * Reimplementation of the SwatchPanel class used to make the default swatch panel because it
   * wasn't public.
   */
  static class SwatchPanel extends JPanel {

    /** The Colors. */
    List<Color> colors;
    /** The Swatch size. */
    Dimension swatchSize;
    /** The Num swatches. */
    Dimension numSwatches;
    /** The Gap. */
    Dimension gap;

    private int selRow;
    private int selCol;

    /** Instantiates a new Swatch panel. */
    SwatchPanel() {
      initValues();
      initColors();
      setToolTipText(""); // register for events
      setOpaque(true);
      setBackground(Color.white);
      setFocusable(true);
      setInheritsPopupMenu(true);

      addFocusListener(
          new FocusAdapter() {
            public void focusGained(FocusEvent e) {
              repaint();
            }

            public void focusLost(FocusEvent e) {
              repaint();
            }
          });

      addKeyListener(
          new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
              int typed = e.getKeyCode();
              switch (typed) {
                case KeyEvent.VK_UP:
                  if (selRow > 0) {
                    selRow--;
                    repaint();
                  }
                  break;
                case KeyEvent.VK_DOWN:
                  if (selRow < numSwatches.height - 1) {
                    selRow++;
                    repaint();
                  }
                  break;
                case KeyEvent.VK_LEFT:
                  if (selCol > 0 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                    selCol--;
                    repaint();
                  } else if (selCol < numSwatches.width - 1
                      && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                    selCol++;
                    repaint();
                  }
                  break;
                case KeyEvent.VK_RIGHT:
                  if (selCol < numSwatches.width - 1
                      && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                    selCol++;
                    repaint();
                  } else if (selCol > 0
                      && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                    selCol--;
                    repaint();
                  }
                  break;
                case KeyEvent.VK_HOME:
                  selCol = 0;
                  selRow = 0;
                  repaint();
                  break;
                case KeyEvent.VK_END:
                  selCol = numSwatches.width - 1;
                  selRow = numSwatches.height - 1;
                  repaint();
                  break;
                default:
                  break;
              }
            }
          });
    }

    /**
     * Gets selected color.
     *
     * @return the selected color
     */
    Color getSelectedColor() {
      return getColorForCell(selCol, selRow);
    }

    /** Init values. */
    void initValues() {}

    @SuppressWarnings("Duplicates")
    public void paintComponent(Graphics g) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
      for (int row = 0; row < numSwatches.height; row++) {
        int y = row * (swatchSize.height + gap.height);
        for (int column = 0; column < numSwatches.width; column++) {
          Color c = getColorForCell(column, row);
          g.setColor(c);
          int x;
          if (!this.getComponentOrientation().isLeftToRight()) {
            x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
          } else {
            x = column * (swatchSize.width + gap.width);
          }
          g.fillRect(x, y, swatchSize.width, swatchSize.height);
          g.setColor(Color.black);
          g.drawLine(
              x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
          g.drawLine(
              x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);

          if (selRow == row && selCol == column && this.isFocusOwner()) {
            Color c2 =
                new Color(
                    c.getRed() < 125 ? 255 : 0,
                    c.getGreen() < 125 ? 255 : 0,
                    c.getBlue() < 125 ? 255 : 0);
            g.setColor(c2);

            g.drawLine(x, y, x + swatchSize.width - 1, y);
            g.drawLine(x, y, x, y + swatchSize.height - 1);
            g.drawLine(
                x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
            g.drawLine(
                x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
            g.drawLine(x, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
            g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y);
          }
        }
      }
    }

    public Dimension getPreferredSize() {
      int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
      int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
      return new Dimension(x, y);
    }

    /** Init colors. */
    void initColors() {}

    public String getToolTipText(MouseEvent e) {
      Color color = getColorForLocation(e.getX(), e.getY());
      return String.format("%d, %d, %d", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Sets selected color from location.
     *
     * @param x the x
     * @param y the y
     */
    @SuppressWarnings("Duplicates")
    void setSelectedColorFromLocation(int x, int y) {
      if (!this.getComponentOrientation().isLeftToRight()) {
        selCol = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
      } else {
        selCol = x / (swatchSize.width + gap.width);
      }
      selRow = y / (swatchSize.height + gap.height);
      repaint();
    }

    /**
     * Gets color for location.
     *
     * @param x the x
     * @param y the y
     * @return the color for location
     */
    @SuppressWarnings("Duplicates")
    Color getColorForLocation(int x, int y) {
      int column;
      if (!this.getComponentOrientation().isLeftToRight()) {
        column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
      } else {
        column = x / (swatchSize.width + gap.width);
      }
      int row = y / (swatchSize.height + gap.height);
      return getColorForCell(column, row);
    }

    private Color getColorForCell(int column, int row) {
      return colors.get(
          (row * numSwatches.width) + column); // (STEVE) - change data orientation here
    }
  }

  /** The type Main swatch panel. */
  static class MainSwatchPanel extends SwatchPanel {

    protected void initValues() {
      swatchSize = new Dimension(100, 100);
      numSwatches = new Dimension(4, 3);
      gap = new Dimension(1, 1);
    }

    protected void initColors() {
      colors = KnowtatorDefaultSettings.COLORS;
    }
  }

  /**
   * Modification of the default color chooser preview panel by Steve Wilson to only show the
   * highlighted text preview.
   *
   * @author Harrison Pielke-Lombardo
   * @see JColorChooser
   */
  static class KnowtatorPreviewPanel extends JPanel {

    private static final int textGap = 5;
    private String sampleText;

    private Color oldColor = null;

    /** Instantiates a new Knowtator preview panel. */
    KnowtatorPreviewPanel() {
      super();
      setFont(KnowtatorDefaultSettings.FONT);
    }

    private JColorChooser getColorChooser() {
      return (JColorChooser) SwingUtilities.getAncestorOfClass(JColorChooser.class, this);
    }

    public Dimension getPreferredSize() {
      JComponent host = getColorChooser();
      if (host == null) {
        host = this;
      }
      FontMetrics fm = host.getFontMetrics(getFont());

      int height = fm.getHeight();
      int width = fm.stringWidth(getSampleText());

      int y = height * 3 + textGap * 3;
      int x = width + textGap * 3;
      return new Dimension(x, y);
    }

    public void paintComponent(Graphics g) {
      if (oldColor == null) {
        oldColor = getForeground();
      }

      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());

      paintText(g);
    }

    private void paintText(Graphics g) {
      g.setFont(getFont());
      FontMetrics fm = g.getFontMetrics();

      int ascent = fm.getAscent();
      int height = fm.getHeight();
      int width = fm.stringWidth(getSampleText());

      int textXOffset = textGap;

      Color color = getForeground();

      g.setColor(color);

      g.fillRect(textXOffset, (height) + textGap, width + (textGap), height + 2);

      g.setColor(Color.black);
      g.drawString(getSampleText(), textXOffset + (textGap / 2), height + ascent + textGap + 2);
    }

    private String getSampleText() {
      if (this.sampleText == null) {
        this.sampleText = UIManager.getString("ColorChooser.sampleText", getLocale());
      }
      return this.sampleText;
    }
  }
}
