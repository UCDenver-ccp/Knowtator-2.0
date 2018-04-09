package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.util.mxCellRenderer;
import edu.ucdenver.ccp.knowtator.KnowtatorController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class GraphMenu extends JMenu {

    private KnowtatorController controller;

    GraphMenu(KnowtatorController controller) {
        super("Graph");
        this.controller = controller;

        add(addNewGraphCommand());
        add(renameGraphCommand());
        add(saveToImageCommand());
        add(deleteGraphCommand());
    }

    private JMenuItem renameGraphCommand() {
        JMenuItem menuItem = new JMenuItem("Rename Graph");
        menuItem.addActionListener(e -> {
            JTextField nameField = new JTextField(10);
            nameField.setText(controller.getSelectionManager().getActiveGraphSpace().getId());
            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("New Name:"));
            inputPanel.add(nameField);


            int result = JOptionPane.showConfirmDialog(controller.getView(), inputPanel,
                    "Enter a new name for this graph space", JOptionPane.DEFAULT_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                controller.getSelectionManager().getActiveGraphSpace().setId(nameField.getText());
            }
        });

        return menuItem;
    }

    private JMenuItem saveToImageCommand() {
        JMenuItem menuItem = new JMenuItem("Save as PNG");
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(controller.getProjectManager().getProjectLocation());
            FileFilter fileFilter = new FileNameExtensionFilter("PNG", "png");
            fileChooser.setFileFilter(fileFilter);
            if (fileChooser.showSaveDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                BufferedImage image = mxCellRenderer.createBufferedImage(controller.getSelectionManager().getActiveGraphSpace(), null, 1, Color.WHITE, true, null);
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
            if (JOptionPane.showConfirmDialog(controller.getView(), "Are you sure you want to delete this graph?") == JOptionPane.YES_OPTION) {
                controller.getSelectionManager().getActiveTextSource().getAnnotationManager().removeGraphSpace(controller.getSelectionManager().getActiveGraphSpace());
            }
        });

        return deleteGraphMenuItem;
    }

    private JMenuItem addNewGraphCommand() {
        JMenuItem addNewGraphMenuItem = new JMenuItem("Create new graph");
        addNewGraphMenuItem.addActionListener(e -> {
            if (controller.getSelectionManager().getActiveTextSource() != null) {
                JTextField field1 = new JTextField();
                Object[] message = {
                        "Graph Title", field1,
                };
                int option = JOptionPane.showConfirmDialog(controller.getView(), message, "Enter a name for this graph", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    controller.getSelectionManager().getActiveTextSource().getAnnotationManager().addGraphSpace(field1.getText());
                }
            }

        });

        return addNewGraphMenuItem;
    }
}
