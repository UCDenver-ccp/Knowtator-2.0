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

package edu.ucdenver.ccp.knowtator.view.profile;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction.pickAction;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.PROFILE;
import static edu.ucdenver.ccp.knowtator.view.actions.modelactions.ProfileAction.assignColorToClass;

import edu.ucdenver.ccp.knowtator.model.OwlModel;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.collection.ActionParameters;
import edu.ucdenver.ccp.knowtator.view.iaa.IAAOptionsDialog;
import edu.ucdenver.ccp.knowtator.view.list.ColorList;
import edu.ucdenver.ccp.knowtator.view.list.ProfileList;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

public class ProfileDialog extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  ProfileList profileList;
  public ColorList colorList;
  private JButton addProfileButton;
  private JButton removeProfileButton;
  private JButton addColorButton;
  private JButton runIaaButton;
  private final KnowtatorView view;

  public ProfileDialog(Window parent, KnowtatorView view) {
    super(parent);
    this.view = view;
    $$$setupUI$$$();
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    view.knowtatorComponents.addAll(
        Arrays.asList(
            profileList,
            colorList));

    addProfileButton.addActionListener(
        e ->
            Optional.ofNullable(JOptionPane.showInputDialog(this, "Enter a name for the profile"))
                .ifPresent(
                    profileName ->
                        pickAction(this.view, profileName, null, new ActionParameters(ADD, PROFILE))));
    removeProfileButton.addActionListener(
        e ->
            Optional.of(
                JOptionPane.showConfirmDialog(
                    this, "Are you sure you wish to remove this profile?"))
                .filter(result -> JOptionPane.OK_OPTION == result)
                .ifPresent(
                    result -> pickAction(this.view, null, null, new ActionParameters(REMOVE, PROFILE))));

    addColorButton.addActionListener(
        e ->
            // TODO: This could be removed if class selection were reflected in color list
            this.view.getModel()
                .flatMap(OwlModel::getSelectedOwlClass)
                .ifPresent(owlClass -> assignColorToClass(this.view, owlClass)));
    runIaaButton.addActionListener(
        e ->
            this.view.getModel()
                .ifPresent(
                    model -> {
                      JFileChooser fileChooser = new JFileChooser();
                      fileChooser.setCurrentDirectory(model.getSaveLocation());
                      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                      //
                      // disable the "All files" option.
                      //
                      fileChooser.setAcceptAllFileFilterUsed(false);
                      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File outputDirectory = new File(fileChooser.getSelectedFile(), "IAA_results");
                        try {
                          Files.createDirectories(outputDirectory.toPath());
                        } catch (IOException ioException) {
                          ioException.printStackTrace();
                        }
                        JDialog iaaDialog = new IAAOptionsDialog(JOptionPane.getFrameForComponent(this), model, outputDirectory);
                        iaaDialog.pack();
                        iaaDialog.setVisible(true);
                      }
                    }));

    buttonOK.addActionListener(e -> onOK());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


    profileList.reset();
    colorList.reset();
  }

  private void onOK() {
    // add your code here
    this.view.knowtatorComponents.remove(profileList);
    this.view.knowtatorComponents.remove(colorList);
    dispose();
  }

  void onCancel() {
    // add your code here if necessary
    this.view.knowtatorComponents.remove(profileList);
    this.view.knowtatorComponents.remove(colorList);
    dispose();
  }

  private void createUIComponents() {
    profileList = new ProfileList(this.view);
    colorList = new ColorList(this.view);
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    contentPane = new JPanel();
    contentPane.setLayout(new GridBagLayout());
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel1, gbc);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel1.add(panel2, gbc);
    buttonOK = new JButton();
    buttonOK.setText("OK");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(buttonOK, gbc);
    runIaaButton = new JButton();
    runIaaButton.setText("Run IAA");
    runIaaButton.setToolTipText("Run inter-annotator agreement calculation");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(runIaaButton, gbc);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel3, gbc);
    final JSplitPane splitPane1 = new JSplitPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(splitPane1, gbc);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new BorderLayout(0, 0));
    splitPane1.setLeftComponent(panel4);
    panel4.setBorder(BorderFactory.createTitledBorder(null, "Profiles", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel4.add(panel5, BorderLayout.NORTH);
    addProfileButton = new JButton();
    addProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addProfileButton.setText("");
    addProfileButton.setToolTipText("Add a profile");
    panel5.add(addProfileButton);
    removeProfileButton = new JButton();
    removeProfileButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
    removeProfileButton.setText("");
    removeProfileButton.setToolTipText("Delete a profile");
    panel5.add(removeProfileButton);
    final JScrollPane scrollPane1 = new JScrollPane();
    panel4.add(scrollPane1, BorderLayout.CENTER);
    final DefaultListModel defaultListModel1 = new DefaultListModel();
    profileList.setModel(defaultListModel1);
    profileList.setToolTipText("Click to activate a profile.");
    scrollPane1.setViewportView(profileList);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new BorderLayout(0, 0));
    splitPane1.setRightComponent(panel6);
    panel6.setBorder(BorderFactory.createTitledBorder(null, "Colors", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel6.add(scrollPane2, BorderLayout.CENTER);
    colorList.setToolTipText("Click to select color for OWL class.");
    scrollPane2.setViewportView(colorList);
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panel6.add(panel7, BorderLayout.NORTH);
    addColorButton = new JButton();
    addColorButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
    addColorButton.setText("");
    addColorButton.setToolTipText("Add color for current OWL class");
    panel7.add(addColorButton);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return contentPane;
  }

}
