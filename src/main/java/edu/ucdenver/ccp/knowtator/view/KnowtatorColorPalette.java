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

package edu.ucdenver.ccp.knowtator.view;

import sun.swing.SwingUtilities2;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * Modification of the standard JColorChooser by Steve Wilson to make it easier to choose
 * colors that suit text annotation.
 * <p>
 *
 * @author Harrison Pielke-Lombardo
 * @see JColorChooser
 */
public class KnowtatorColorPalette extends JColorChooser {

	public KnowtatorColorPalette() {
        super();
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[6];
        panels[0] = new KnowtatorSwatchChooserPanel();
        System.arraycopy(ColorChooserComponentFactory.getDefaultChooserPanels(), 0, panels, 1, 5);


        setChooserPanels(panels);
        setPreviewPanel(new KnowtatorPreviewPanel());
    }

	/**
	 * A modification to the default swatch panel
	 */
	class KnowtatorSwatchChooserPanel extends AbstractColorChooserPanel {

		SwatchPanel swatchPanel;
		MouseListener mainSwatchListener;
		private KeyListener mainSwatchKeyListener;

		KnowtatorSwatchChooserPanel() {
			super();
			setInheritsPopupMenu(true);
		}

		/**
		 * @return recommended colors
		 */
		public String getDisplayName() {
			return "Recommended Colors";
		}


		protected void buildChooser() {

			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			JPanel superHolder = new JPanel(gb);

			swatchPanel = new MainSwatchPanel();
			swatchPanel.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY,
					getDisplayName());
			swatchPanel.setInheritsPopupMenu(true);

			mainSwatchKeyListener = new MainSwatchKeyListener();
			mainSwatchListener = new MainSwatchListener();
			swatchPanel.addMouseListener(mainSwatchListener);
			swatchPanel.addKeyListener(mainSwatchKeyListener);

			JPanel mainHolder = new JPanel(new BorderLayout());
			Border border = new CompoundBorder(new LineBorder(Color.black),
					new LineBorder(Color.white));
			mainHolder.setBorder(border);
			mainHolder.add(swatchPanel, BorderLayout.CENTER);

			gbc.anchor = GridBagConstraints.LAST_LINE_START;
			gbc.gridwidth = 1;
			gbc.gridheight = 2;
			Insets oldInsets = gbc.insets;
			gbc.insets = new Insets(0, 0, 0, 10);
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

			removeAll();  // strip out all the sub-components
		}

		private class MainSwatchKeyListener extends KeyAdapter {

			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_SPACE == e.getKeyCode()) {
					Color color = swatchPanel.getSelectedColor();
					getColorSelectionModel().setSelectedColor(color);
				}
			}
		}

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
		public void updateChooser() {
		}

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
	 * Reimplementation of the SwatchPanel class used to make the default swatch panel because it wasn't public
	 */
	class SwatchPanel extends JPanel {

		ArrayList<Color> colors;
		Dimension swatchSize;
		Dimension numSwatches;
		Dimension gap;

		private int selRow;
		private int selCol;

		SwatchPanel() {
			initValues();
			initColors();
			setToolTipText(""); // register for events
			setOpaque(true);
			setBackground(Color.white);
			setFocusable(true);
			setInheritsPopupMenu(true);

			addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					repaint();
				}

				public void focusLost(FocusEvent e) {
					repaint();
				}
			});

			addKeyListener(new KeyAdapter() {
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
							} else if (selCol > 0 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
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
					}
				}
			});
		}

		Color getSelectedColor() {
			return getColorForCell(selCol, selRow);
		}

		void initValues() {

		}

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
					g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
					g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);

					if (selRow == row && selCol == column && this.isFocusOwner()) {
						Color c2 = new Color(c.getRed() < 125 ? 255 : 0,
								c.getGreen() < 125 ? 255 : 0,
								c.getBlue() < 125 ? 255 : 0);
						g.setColor(c2);

						g.drawLine(x, y, x + swatchSize.width - 1, y);
						g.drawLine(x, y, x, y + swatchSize.height - 1);
						g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
						g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
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

		void initColors() {


		}

		public String getToolTipText(MouseEvent e) {
			Color color = getColorForLocation(e.getX(), e.getY());
			return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
		}

		void setSelectedColorFromLocation(int x, int y) {
			if (!this.getComponentOrientation().isLeftToRight()) {
				selCol = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
			} else {
				selCol = x / (swatchSize.width + gap.width);
			}
			selRow = y / (swatchSize.height + gap.height);
			repaint();
		}

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
			return colors.get((row * numSwatches.width) + column); // (STEVE) - change data orientation here
		}


	}

    class MainSwatchPanel extends SwatchPanel {


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
     * Modification of the default color chooser preview panel by Steve Wilson to only show the highlighted text preview.
     * <p>
     *
     * @author Harrison Pielke-Lombardo
     * @see JColorChooser
     */
    class KnowtatorPreviewPanel extends JPanel {

        private final int textGap = 5;
        private String sampleText;

        private Color oldColor = null;

        KnowtatorPreviewPanel(){
            super();
            setFont(KnowtatorDefaultSettings.FONT);
        }

        private JColorChooser getColorChooser() {
            return (JColorChooser)SwingUtilities.getAncestorOfClass(
                    JColorChooser.class, this);
        }

        public Dimension getPreferredSize() {
            JComponent host = getColorChooser();
            if (host == null) {
                host = this;
            }
            FontMetrics fm = host.getFontMetrics(getFont());

            int height = fm.getHeight();
            int width = SwingUtilities2.stringWidth(host, fm, getSampleText());

            int y = height*3 + textGap*3;
            int x = width + textGap*3;
            return new Dimension( x,y );
        }

        public void paintComponent(Graphics g) {
            if (oldColor == null)
                oldColor = getForeground();

            g.setColor(getBackground());
            g.fillRect(0,0,getWidth(),getHeight());

            if (this.getComponentOrientation().isLeftToRight()) {
                paintText(g);

            } else {

                paintText(g);


            }
        }

        private void paintText(Graphics g) {
            g.setFont(getFont());
            JComponent host = getColorChooser();
            if (host == null) {
                host = this;
            }
            FontMetrics fm = SwingUtilities2.getFontMetrics(host, g);

            int ascent = fm.getAscent();
            int height = fm.getHeight();
            int width = SwingUtilities2.stringWidth(host, fm, getSampleText());

            int textXOffset = textGap;

            Color color = getForeground();

            g.setColor(color);

            g.fillRect(textXOffset,
                    ( height) + textGap,
                    width + (textGap),
                    height +2);

            g.setColor(Color.black);
            SwingUtilities2.drawString(host, g, getSampleText(),
                    textXOffset+(textGap/2),
                    height+ascent+textGap+2);

        }

        private String getSampleText() {
            if (this.sampleText == null) {
                this.sampleText = UIManager.getString("ColorChooser.sampleText", getLocale());
            }
            return this.sampleText;
        }
    }

}