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

}
