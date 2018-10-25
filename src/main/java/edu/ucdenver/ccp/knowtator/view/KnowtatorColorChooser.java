/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.profile.KnowtatorColors;

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
 * The standard color swatch chooser.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Steve Wilson
 */
class KnowtatorColorChooser extends JColorChooser {

    KnowtatorColorChooser() {
        super();
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[6];
        panels[0] = new KnowtatorSwatchChooserPanel();
        System.arraycopy(ColorChooserComponentFactory.getDefaultChooserPanels(), 0, panels, 1, 5);


        setChooserPanels(panels);
    }

    class KnowtatorSwatchChooserPanel extends AbstractColorChooserPanel {

        SwatchPanel swatchPanel;
        MouseListener mainSwatchListener;
        private KeyListener mainSwatchKeyListener;

        KnowtatorSwatchChooserPanel() {
            super();
            setInheritsPopupMenu(true);
        }

        public String getDisplayName() {
            return "Recommended Colors";
        }

        public Icon getSmallDisplayIcon() {
            return null;
        }

        public Icon getLargeDisplayIcon() {
            return null;
        }

        /**
         * The background color, foreground color, and font are already set to the
         * defaults from the defaults table before this method is called.
         */
        public void installChooserPanel(JColorChooser enclosingChooser) {
            super.installChooserPanel(enclosingChooser);
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

        public void updateChooser() {

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

    }


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

        protected void initValues() {

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

        protected void initColors() {


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
            numSwatches = new Dimension(3, 3);
            gap = new Dimension(1, 1);
        }

        protected void initColors() {
            colors = KnowtatorColors.COLORS;
        }

    }
}