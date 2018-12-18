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

package edu.ucdenver.ccp.knowtator.view.menu;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.list.AnnotationList;
import edu.ucdenver.ccp.knowtator.view.list.AnnotationListForOWLClass;
import edu.ucdenver.ccp.knowtator.view.list.AnnotationListForSpannedText;
import edu.ucdenver.ccp.knowtator.view.list.RelationList;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.ResourceBundle;

public class ReviewPane extends MenuPane {
	private JLabel owlClassLabel;
	private AnnotationList annotationsForClassList;
	private AnnotationList annotationsForSpannedTextList;
	private JPanel contentPane;
	private JPanel panel1;
	private JCheckBox includeClassDescendantsCheckBox;
	private JButton refreshButton;

	private JCheckBox exactMatchCheckBox;
	private JCheckBox includePropertyDescendantsCheckBox;
	private RelationList relationsForPropertyList;
	private JLabel owlPropertyLabel;
	private JTextField annotationsContainingTextTextField;
	private JButton previousAnnotationForOWLClassButton;
	private JButton nextAnnotationForOWLClassButton;
	private JButton previousAnnotationForTextButton;
	private JButton nextAnnotationForTextButton;
	private JButton previousRelationForPropertyButton;
	private JButton nextRelationForPropertyButton;
	private HashSet<OWLClass> activeOWLClassDescendants;
	private HashSet<OWLObjectProperty> activeOWLPropertyDescendants;

	ReviewPane(KnowtatorView view) {
		super(view, "Review");
		activeOWLClassDescendants = new HashSet<>();
		activeOWLPropertyDescendants = new HashSet<>();

		$$$setupUI$$$();
		includeClassDescendantsCheckBox.addActionListener(e -> refresh());
		exactMatchCheckBox.addActionListener(e -> refresh());
		refreshButton.addActionListener(e -> refresh());

		nextAnnotationForOWLClassButton.addActionListener(e -> {
			annotationsForClassList.setSelectedIndex(Math.min(annotationsForClassList.getSelectedIndex() + 1, annotationsForClassList.getModel().getSize() - 1));
			annotationsForClassList.reactToClick();
		});
		previousAnnotationForOWLClassButton.addActionListener(e -> {
			annotationsForClassList.setSelectedIndex(Math.max(annotationsForClassList.getSelectedIndex() - 1, 0));
			annotationsForClassList.reactToClick();
		});

		nextAnnotationForTextButton.addActionListener(e -> {
			annotationsForSpannedTextList.setSelectedIndex(Math.min(annotationsForSpannedTextList.getSelectedIndex() + 1, annotationsForSpannedTextList.getModel().getSize() - 1));
			annotationsForSpannedTextList.reactToClick();
		});
		previousAnnotationForTextButton.addActionListener(e -> {
			annotationsForSpannedTextList.setSelectedIndex(Math.max(annotationsForSpannedTextList.getSelectedIndex() - 1, 0));
			annotationsForSpannedTextList.reactToClick();
		});

		nextRelationForPropertyButton.addActionListener(e -> {
			relationsForPropertyList.setSelectedIndex(Math.min(relationsForPropertyList.getSelectedIndex() + 1, relationsForPropertyList.getModel().getSize() - 1));
			relationsForPropertyList.reactToClick();
		});
		previousRelationForPropertyButton.addActionListener(e -> {
			relationsForPropertyList.setSelectedIndex(Math.max(relationsForPropertyList.getSelectedIndex() - 1, 0));
			relationsForPropertyList.reactToClick();
		});

	}

	@Override
	public void show() {
		refresh();
		annotationsForClassList.reset();
		annotationsForSpannedTextList.reset();
		relationsForPropertyList.reset();

	}

	private void refresh() {
		view.getModel().ifPresent(model -> {
			activeOWLClassDescendants.clear();
			activeOWLPropertyDescendants.clear();


			model.getSelectedOWLClass().ifPresent(owlClass -> {
				activeOWLClassDescendants.add(owlClass);
				if (includeClassDescendantsCheckBox.isSelected()) {
					activeOWLClassDescendants.addAll(model.getOWLCLassDescendants(owlClass));
				}
				owlClassLabel.setText(model.getOWLEntityRendering(owlClass));
			});


			model.getSelectedOWLObjectProperty().ifPresent(owlObjectProperty -> {
				activeOWLPropertyDescendants.add(owlObjectProperty);
				if (includePropertyDescendantsCheckBox.isSelected()) {
					activeOWLPropertyDescendants.addAll(model.getOWLObjectPropertyDescendants(owlObjectProperty));
				}
				owlPropertyLabel.setText(model.getOWLEntityRendering(owlObjectProperty));
			});

			annotationsForClassList.reset();
			annotationsForSpannedTextList.reset();
			relationsForPropertyList.reset();
		});


	}

	@Override
	public JPanel getContentPane() {
		return contentPane;
	}

	@Override
	public void reset() {
		annotationsForSpannedTextList.reset();
		annotationsForSpannedTextList.reset();
		relationsForPropertyList.reset();
	}

	@Override
	public void dispose() {
		annotationsForClassList.dispose();
		annotationsForSpannedTextList.dispose();
		relationsForPropertyList.dispose();
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
		panel1 = new JPanel();
		panel1.setLayout(new BorderLayout(0, 0));
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
		Font contentPaneFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, contentPane.getFont());
		if (contentPaneFont != null) contentPane.setFont(contentPaneFont);
		panel1.add(contentPane, BorderLayout.CENTER);
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel2, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JSplitPane splitPane1 = new JSplitPane();
		panel2.add(splitPane1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
		Font panel3Font = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, panel3.getFont());
		if (panel3Font != null) panel3.setFont(panel3Font);
		splitPane1.setRightComponent(panel3);
		final JLabel label1 = new JLabel();
		Font label1Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label1.getFont());
		if (label1Font != null) label1.setFont(label1Font);
		this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("log4j").getString("annotations.containing.text"));
		panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel3.add(scrollPane1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane1.setViewportView(annotationsForSpannedTextList);
		panel3.add(annotationsContainingTextTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		previousAnnotationForTextButton = new JButton();
		previousAnnotationForTextButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousAnnotationForTextButton.setText("");
		panel4.add(previousAnnotationForTextButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nextAnnotationForTextButton = new JButton();
		nextAnnotationForTextButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextAnnotationForTextButton.setText("");
		panel4.add(nextAnnotationForTextButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font exactMatchCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, exactMatchCheckBox.getFont());
		if (exactMatchCheckBoxFont != null) exactMatchCheckBox.setFont(exactMatchCheckBoxFont);
		this.$$$loadButtonText$$$(exactMatchCheckBox, ResourceBundle.getBundle("log4j").getString("exact.match"));
		panel3.add(exactMatchCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel3.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel3.add(spacer2, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
		splitPane1.setLeftComponent(panel5);
		final JLabel label2 = new JLabel();
		Font label2Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label2.getFont());
		if (label2Font != null) label2.setFont(label2Font);
		this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("log4j").getString("annotations.for.owl.class"));
		panel5.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		owlClassLabel = new JLabel();
		owlClassLabel.setText("");
		panel5.add(owlClassLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane2 = new JScrollPane();
		panel5.add(scrollPane2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane2.setViewportView(annotationsForClassList);
		final Spacer spacer3 = new Spacer();
		panel5.add(spacer3, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel5.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		previousAnnotationForOWLClassButton = new JButton();
		previousAnnotationForOWLClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousAnnotationForOWLClassButton.setText("");
		panel6.add(previousAnnotationForOWLClassButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nextAnnotationForOWLClassButton = new JButton();
		nextAnnotationForOWLClassButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextAnnotationForOWLClassButton.setText("");
		panel6.add(nextAnnotationForOWLClassButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font includeClassDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includeClassDescendantsCheckBox.getFont());
		if (includeClassDescendantsCheckBoxFont != null)
			includeClassDescendantsCheckBox.setFont(includeClassDescendantsCheckBoxFont);
		this.$$$loadButtonText$$$(includeClassDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants"));
		panel5.add(includeClassDescendantsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		panel5.add(spacer4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		refreshButton = new JButton();
		Font refreshButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, refreshButton.getFont());
		if (refreshButtonFont != null) refreshButton.setFont(refreshButtonFont);
		this.$$$loadButtonText$$$(refreshButton, ResourceBundle.getBundle("log4j").getString("refresh"));
		panel2.add(refreshButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer5 = new Spacer();
		panel2.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final Spacer spacer6 = new Spacer();
		panel7.add(spacer6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		Font label3Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label3.getFont());
		if (label3Font != null) label3.setFont(label3Font);
		this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("log4j").getString("relation.annotations.for.owl.objectproperty"));
		panel7.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		owlPropertyLabel = new JLabel();
		Font owlPropertyLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlPropertyLabel.getFont());
		if (owlPropertyLabelFont != null) owlPropertyLabel.setFont(owlPropertyLabelFont);
		owlPropertyLabel.setText("");
		panel7.add(owlPropertyLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane3 = new JScrollPane();
		panel7.add(scrollPane3, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane3.setViewportView(relationsForPropertyList);
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel7.add(panel8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		previousRelationForPropertyButton = new JButton();
		previousRelationForPropertyButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24 (reversed).png")));
		previousRelationForPropertyButton.setText("");
		panel8.add(previousRelationForPropertyButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nextRelationForPropertyButton = new JButton();
		nextRelationForPropertyButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-advance-24.png")));
		nextRelationForPropertyButton.setText("");
		panel8.add(nextRelationForPropertyButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		Font includePropertyDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includePropertyDescendantsCheckBox.getFont());
		if (includePropertyDescendantsCheckBoxFont != null)
			includePropertyDescendantsCheckBox.setFont(includePropertyDescendantsCheckBoxFont);
		this.$$$loadButtonText$$$(includePropertyDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants1"));
		panel7.add(includePropertyDescendantsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		label1.setLabelFor(scrollPane1);
		label2.setLabelFor(scrollPane2);
	}

	/**
	 * @noinspection ALL
	 */
	private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
		if (currentFont == null) return null;
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
		return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
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
				if (i == text.length()) break;
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
				if (i == text.length()) break;
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
		return panel1;
	}

	private void createUIComponents() {
		includeClassDescendantsCheckBox = new JCheckBox();
		includePropertyDescendantsCheckBox = new JCheckBox();
		annotationsContainingTextTextField = new JTextField();
		exactMatchCheckBox = new JCheckBox();

		annotationsForClassList = new AnnotationListForOWLClass(view, activeOWLClassDescendants);
		annotationsForSpannedTextList = new AnnotationListForSpannedText(view, exactMatchCheckBox, annotationsContainingTextTextField);
		relationsForPropertyList = new RelationList(view, activeOWLPropertyDescendants);
	}

}
