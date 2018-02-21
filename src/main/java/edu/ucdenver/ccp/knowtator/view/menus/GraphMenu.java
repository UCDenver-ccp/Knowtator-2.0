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

package edu.ucdenver.ccp.knowtator.view.menus;

import com.mxgraph.util.mxCellRenderer;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.view.graph.GraphDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphMenu extends JMenu {

    private KnowtatorManager manager;
    private GraphDialog graphDialog;

    public GraphMenu(KnowtatorManager manager, GraphDialog graphDialog) {
        super("Graph");
        this.manager = manager;
        this.graphDialog = graphDialog;

        add(addNewGraphCommand());
        add(saveToImageCommand());
        add(deleteGraphCommand());
    }

    private JMenuItem saveToImageCommand() {
        JMenuItem menuItem = new JMenuItem("Save as PNG");
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(manager.getProjectManager().getProjectLocation());
            FileFilter fileFilter = new FileNameExtensionFilter("PNG", "png");
            fileChooser.setFileFilter(fileFilter);
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                BufferedImage image = mxCellRenderer.createBufferedImage(graphDialog.getGraphViewer().getSelectedGraphComponent().getGraph(), null, 1, Color.WHITE, true, null);
                try {
                    ImageIO.write(image, "PNG", new File(fileChooser.getSelectedFile().getAbsolutePath()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        return menuItem;
    }

    private JMenuItem deleteGraphCommand() {
        JMenuItem deleteGraphMenuItem = new JMenuItem("Delete graph");
        deleteGraphMenuItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this graph?") == JOptionPane.YES_OPTION) {
                graphDialog.getGraphViewer().deleteSelectedGraph();
            }
        });

        return deleteGraphMenuItem;
    }

    private JMenuItem addNewGraphCommand() {
        JMenuItem addNewGraphMenuItem = new JMenuItem("Create new graph");
        addNewGraphMenuItem.addActionListener(e -> {
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Graph Title", field1,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter a name for this graph", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                graphDialog.getGraphViewer().addNewGraph(field1.getText());

            }

        });

        return addNewGraphMenuItem;
    }
}
