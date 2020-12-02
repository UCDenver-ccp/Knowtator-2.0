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

package edu.ucdenver.ccp.knowtator.view.graph;

import edu.ucdenver.ccp.knowtator.model.object.Quantifier;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * The type Relation options dialog.
 */
class RelationOptionsDialog extends JDialog {
  /**
   * The Ok option.
   */
  static final int OK_OPTION = 1;

  private static final int CANCEL_OPTION = 0;

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JCheckBox negateCheckBox;
  private JTextField quantifierValueTextField;
  private JTextArea previewTextArea;
  private JComboBox<Quantifier> quantifierChooser;
  private JTextArea relationNotes;


  private int result;

  /**
   * Instantiates a new Relation options dialog.
   *
   * @param parent     the parent
   * @param propertyID the property id
   */
  // TODO: Move to graph space info pane
  RelationOptionsDialog(Window parent, String propertyID) {
    super(parent);
//    $$$setupUI$$$();
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);
    previewTextArea.setText(propertyID);
    previewTextArea.setEditable(false);

    result = 0;

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            onCancel();
          }
        });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(
        e -> onCancel(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    quantifierChooser.setModel(
        new DefaultComboBoxModel<>(Quantifier.values()));

    quantifierChooser.addItemListener(
        e -> {
          // Some and only don't allow for a quantifier value, so clear the value and disable input
          if (e.getItem().equals("some") || e.getItem().equals("only")) {
            quantifierValueTextField.setText("");
            quantifierValueTextField.setEnabled(false);
          } else {
            quantifierValueTextField.setEnabled(true);
          }
        });

    quantifierValueTextField.addKeyListener(
        new KeyAdapter() {
          // Only allow typing of numeric values.
          // Add a zero in case the user is typing a float
          public void keyTyped(KeyEvent e) {
            if (!NumberUtils.isNumber(
                String.format("%s%s0", quantifierValueTextField.getText(), e.getKeyChar()))) {
              e.consume(); // ignore event
            }
          }
        });
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public int getResult() {
    return result;
  }

  private void onOK() {
    Quantifier quantifier = (Quantifier) quantifierChooser.getSelectedItem();

    // Correct for missing 0 if a decimal point is present
    if (quantifierValueTextField.getText().endsWith(".")) {
      quantifierValueTextField.setText(String.format("%s0", quantifierValueTextField.getText()));
    }

    // Don't allow non-numeric values of exactly, min, or max are selected
    if (quantifier != null
        && (quantifier.equals(Quantifier.exactly) || quantifier.equals(Quantifier.min) || quantifier.equals(Quantifier.max))
        && !NumberUtils.isNumber(quantifierValueTextField.getText())) {
      JOptionPane.showMessageDialog(this, "Please enter a numeric value");
      return;
    }
    if (previewTextArea.getText().length() == 0) {
      JOptionPane.showMessageDialog(this, "Please enter a name for this relation");
      return;
    }
    result = OK_OPTION;
    dispose();
  }

  private void onCancel() {
    result = CANCEL_OPTION;
    dispose();
  }

  /**
   * Gets negation.
   *
   * @return the negation
   */
  Boolean getNegation() {
    return negateCheckBox.isSelected();
  }

  /**
   * Gets quantifier.
   *
   * @return the quantifier
   */
  public Quantifier getQuantifier() {
    return (Quantifier) quantifierChooser.getSelectedItem();
  }

  /**
   * Gets quantifier value.
   *
   * @return the quantifier value
   */
  String getQuantifierValue() {
    try {
      return NumberUtils.createNumber(quantifierValueTextField.getText()).toString();
    } catch (NumberFormatException e) {
      return quantifierValueTextField.getText();
    }
  }

  /**
   * Gets property id.
   *
   * @return the property id
   */
  String getPropertyID() {
    return previewTextArea.getText();
  }

  /**
   * Gets motivation.
   *
   * @return the motivation
   */
  String getMotivation() {
    return relationNotes.getText();
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
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
    panel1.setLayout(new GridBagLayout());
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel1, gbc);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
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
    buttonCancel = new JButton();
    buttonCancel.setText("Cancel");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(buttonCancel, gbc);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel3, gbc);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(panel4, gbc);
    final JLabel label1 = new JLabel();
    Font label1Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label1.getFont());
    if (label1Font != null) {
      label1.setFont(label1Font);
    }
    label1.setText("Quantifier");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel4.add(label1, gbc);
    final JLabel label2 = new JLabel();
    Font label2Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label2.getFont());
    if (label2Font != null) {
      label2.setFont(label2Font);
    }
    this.$$$loadLabelText$$$(label2, this.$$$getMessageFromBundle$$$("log4j", "value2"));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel4.add(label2, gbc);
    quantifierChooser = new JComboBox();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel4.add(quantifierChooser, gbc);
    quantifierValueTextField = new JTextField();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel4.add(quantifierValueTextField, gbc);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(panel5, gbc);
    final JScrollPane scrollPane1 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel5.add(scrollPane1, gbc);
    previewTextArea = new JTextArea();
    previewTextArea.setMinimumSize(new Dimension(100, 12));
    scrollPane1.setViewportView(previewTextArea);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(panel6, gbc);
    negateCheckBox = new JCheckBox();
    Font negateCheckBoxFont = this.$$$getFont$$$("Verdana", -1, -1, negateCheckBox.getFont());
    if (negateCheckBoxFont != null) {
      negateCheckBox.setFont(negateCheckBoxFont);
    }
    this.$$$loadButtonText$$$(negateCheckBox, this.$$$getMessageFromBundle$$$("log4j", "negate1"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    panel6.add(negateCheckBox, gbc);
    final JLabel label3 = new JLabel();
    Font label3Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label3.getFont());
    if (label3Font != null) {
      label3.setFont(label3Font);
    }
    label3.setText("Notes");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    contentPane.add(label3, gbc);
    final JScrollPane scrollPane2 = new JScrollPane();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(scrollPane2, gbc);
    relationNotes = new JTextArea();
    Font relationNotesFont = this.$$$getFont$$$("Verdana", -1, 10, relationNotes.getFont());
    if (relationNotesFont != null) {
      relationNotes.setFont(relationNotesFont);
    }
    scrollPane2.setViewportView(relationNotes);
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) {
      return null;
    }
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
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
  private void $$$loadLabelText$$$(JLabel component, String text) {
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
      component.setDisplayedMnemonic(mnemonic);
      component.setDisplayedMnemonicIndex(mnemonicIndex);
    }
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
