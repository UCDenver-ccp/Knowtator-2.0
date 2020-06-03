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

package edu.ucdenver.ccp.knowtator.view.iaa;

import edu.ucdenver.ccp.knowtator.iaa.IaaException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIaa;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

public class IAAOptionsDialog extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JButton selectAllButton1;
  private JPanel profilesPanel;
  private JPanel documentsPanel;
  private final File outputDirectory;
  private final KnowtatorModel model;
  private JCheckBox iaaClassCheckBox;
  private JCheckBox iaaSpanCheckBox;
  private JCheckBox iaaClassAndSpanCheckBox;
  private JButton selectAllButton;

  public IAAOptionsDialog(Window parent, KnowtatorModel model, File outputDirectory) {
    super(parent);
    this.outputDirectory = outputDirectory;
    this.model = model;
    $$$setupUI$$$();

    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(e -> onCancel(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;

    for (TextSource textSource : model.getTextSources()) {
      gbc.gridy += 1;
      documentsPanel.add(new JCheckBox(textSource.getId()), gbc);
    }

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;

    for (Profile profile : model.getProfiles()) {
      gbc.gridy += 1;
      profilesPanel.add(new JCheckBox(profile.getId()), gbc);
    }
    selectAllButton.addActionListener(e -> {
      List<JCheckBox> checkBoxes = Arrays.stream(profilesPanel.getComponents())
          .filter(component -> component instanceof JCheckBox)
          .map(component -> (JCheckBox) component)
          .collect(Collectors.toList());

      if (checkBoxes.stream().allMatch(AbstractButton::isSelected)) {
        checkBoxes.forEach(jCheckBox -> jCheckBox.setSelected(false));
      } else {
        checkBoxes.forEach(jCheckBox -> jCheckBox.setSelected(true));
      }
    });
    selectAllButton1.addActionListener(e -> {
      List<JCheckBox> checkBoxes = Arrays.stream(documentsPanel.getComponents())
          .filter(component -> component instanceof JCheckBox)
          .map(component -> (JCheckBox) component).collect(Collectors.toList());

      if (checkBoxes.stream().allMatch(AbstractButton::isSelected)) {
        checkBoxes.forEach(jCheckBox -> jCheckBox.setSelected(false));
      } else {
        checkBoxes.forEach(jCheckBox -> jCheckBox.setSelected(true));
      }
    });
  }

  private void onOK() {
    Set<String> profiles = Arrays.stream(profilesPanel.getComponents())
        .filter(component -> component instanceof JCheckBox)
        .map(component -> (JCheckBox) component)
        .filter(AbstractButton::isSelected)
        .map(AbstractButton::getText)
        .filter(text -> text.equals("Select all"))
        .collect(Collectors.toSet());

    Set<String> textSources = Arrays.stream(documentsPanel.getComponents())
        .filter(component -> component instanceof JCheckBox)
        .map(component -> (JCheckBox) component)
        .filter(AbstractButton::isSelected)
        .map(AbstractButton::getText)
        .filter(text -> text.equals("Select all"))
        .collect(Collectors.toSet());

    try {
      KnowtatorIaa knowtatorIaa = new KnowtatorIaa(outputDirectory, model, textSources, profiles);

      if (iaaClassCheckBox.isSelected()) {
        knowtatorIaa.runClassIaa();
      }
      if (iaaSpanCheckBox.isSelected()) {
        knowtatorIaa.runSpanIaa();
      }
      if (iaaClassAndSpanCheckBox.isSelected()) {
        knowtatorIaa.runClassAndSpanIaa();
      }

      knowtatorIaa.closeHtml();
    } catch (IaaException e1) {
      e1.printStackTrace();
    }
    dispose();
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    contentPane = new JPanel();
    contentPane.setLayout(new GridBagLayout());
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new BorderLayout(0, 0));
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel1, gbc);
    panel1.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("ui", "profiles"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(scrollPane1, BorderLayout.NORTH);
    profilesPanel = new JPanel();
    profilesPanel.setLayout(new GridBagLayout());
    scrollPane1.setViewportView(profilesPanel);
    selectAllButton = new JButton();
    this.$$$loadButtonText$$$(selectAllButton, this.$$$getMessageFromBundle$$$("log4j", "select.all"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    profilesPanel.add(selectAllButton, gbc);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout(0, 0));
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel2, gbc);
    panel2.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("log4j", "documents"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel2.add(scrollPane2, BorderLayout.NORTH);
    documentsPanel = new JPanel();
    documentsPanel.setLayout(new GridBagLayout());
    scrollPane2.setViewportView(documentsPanel);
    selectAllButton1 = new JButton();
    this.$$$loadButtonText$$$(selectAllButton1, this.$$$getMessageFromBundle$$$("log4j", "select.all"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    documentsPanel.add(selectAllButton1, gbc);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel3, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel3.add(spacer1, gbc);
    buttonOK = new JButton();
    this.$$$loadButtonText$$$(buttonOK, this.$$$getMessageFromBundle$$$("log4j", "ok"));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel3.add(buttonOK, gbc);
    buttonCancel = new JButton();
    this.$$$loadButtonText$$$(buttonCancel, this.$$$getMessageFromBundle$$$("log4j", "cancel"));
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel3.add(buttonCancel, gbc);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new BorderLayout(0, 0));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel4, gbc);
    panel4.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("log4j", "mode"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridBagLayout());
    panel4.add(panel5, BorderLayout.NORTH);
    iaaClassCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(iaaClassCheckBox, this.$$$getMessageFromBundle$$$("log4j", "class.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel5.add(iaaClassCheckBox, gbc);
    iaaSpanCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(iaaSpanCheckBox, this.$$$getMessageFromBundle$$$("log4j", "span.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel5.add(iaaSpanCheckBox, gbc);
    iaaClassAndSpanCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(iaaClassAndSpanCheckBox, this.$$$getMessageFromBundle$$$("log4j", "class.and.span.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    panel5.add(iaaClassAndSpanCheckBox, gbc);
  }

  private static Method $$$cachedGetBundleMethod$$$ = null;

  private String $$$getMessageFromBundle$$$(String path, String key) {
    ResourceBundle bundle;
    try {
      Class<?> thisClass = this.getClass();
      if ($$$cachedGetBundleMethod$$$ == null) {
        Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
        $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
      }
      bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
    } catch (Exception e) {
      bundle = ResourceBundle.getBundle(path);
    }
    return bundle.getString(key);
  }

  /**
   * @noinspection ALL
   */
  private void $$$loadButtonText$$$(AbstractButton component, String text) {
    StringBuffer result = new StringBuffer();
    boolean haveMnemonic = false;
    char mnemonic = '\0';
    int mnemonicIndex = -1;
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) == '&') {
        i++;
        if (i == text.length()) {
          break;
        }
        if (!haveMnemonic && text.charAt(i) != '&') {
          haveMnemonic = true;
          mnemonic = text.charAt(i);
          mnemonicIndex = result.length();
        }
      }
      result.append(text.charAt(i));
    }
    component.setText(result.toString());
    if (haveMnemonic) {
      component.setMnemonic(mnemonic);
      component.setDisplayedMnemonicIndex(mnemonicIndex);
    }
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return contentPane;
  }

}
