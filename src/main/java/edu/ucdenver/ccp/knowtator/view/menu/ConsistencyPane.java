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
import edu.ucdenver.ccp.knowtator.model.OWLModel;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.list.AnnotationList;
import edu.ucdenver.ccp.knowtator.view.list.RelationList;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.ResourceBundle;

class ConsistencyPane extends MenuPane {
	private JLabel owlClassLabel;
	private JLabel spanLabel;
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
	private HashSet<OWLClass> activeOWLClassDescendants;
	private HashSet<OWLObjectProperty> activeOWLPropertyDescendants;

	ConsistencyPane() {
		super("Consistency");
		$$$setupUI$$$();
		includeClassDescendantsCheckBox.addActionListener(e -> refresh());
		exactMatchCheckBox.addActionListener(e -> refresh());
		refreshButton.addActionListener(e -> refresh());
		activeOWLClassDescendants = new HashSet<>();
	}

	@Override
	public void show() {
		annotationsForClassList.reset();
		annotationsForSpannedTextList.reset();
		relationsForPropertyList.reset();

		refresh();
	}

	private void refresh() {
		OWLModel owlModel = KnowtatorView.MODEL;

		activeOWLClassDescendants = new HashSet<>();
		owlModel.getSelectedOWLClass().ifPresent(owlClass -> {
			activeOWLClassDescendants.add(owlClass);
			if (includeClassDescendantsCheckBox.isSelected()) {
				activeOWLClassDescendants.addAll(owlModel.getOWLCLassDescendants(owlClass));
			}
			owlClassLabel.setText(owlModel.getOWLEntityRendering(owlClass));
		});

		activeOWLPropertyDescendants = new HashSet<>();
		owlModel.getSelectedOWLObjectProperty().ifPresent(owlObjectProperty -> {
			activeOWLPropertyDescendants.add(owlObjectProperty);
			if (includePropertyDescendantsCheckBox.isSelected()) {
				activeOWLPropertyDescendants.addAll(owlModel.getOWLObjectPropertyDescendants(owlObjectProperty));
			}
			owlPropertyLabel.setText(owlModel.getOWLEntityRendering(owlObjectProperty));
		});

		KnowtatorView.MODEL.getSelectedTextSource()
				.ifPresent(textSource -> textSource.getSelectedAnnotation()
						.ifPresent(conceptAnnotation -> spanLabel.setText(conceptAnnotation.getSpannedText())));
		annotationsForClassList.react();
		annotationsForSpannedTextList.react();
		relationsForPropertyList.react();
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
		panel3.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
		Font panel3Font = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, panel3.getFont());
		if (panel3Font != null) panel3.setFont(panel3Font);
		splitPane1.setRightComponent(panel3);
		final Spacer spacer1 = new Spacer();
		panel3.add(spacer1, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		Font label1Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label1.getFont());
		if (label1Font != null) label1.setFont(label1Font);
		this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("log4j").getString("annotations.containing.text"));
		panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		spanLabel = new JLabel();
		Font spanLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, spanLabel.getFont());
		if (spanLabelFont != null) spanLabel.setFont(spanLabelFont);
		spanLabel.setText("");
		panel3.add(spanLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel3.add(scrollPane1, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane1.setViewportView(annotationsForSpannedTextList);
		exactMatchCheckBox = new JCheckBox();
		Font exactMatchCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, exactMatchCheckBox.getFont());
		if (exactMatchCheckBoxFont != null) exactMatchCheckBox.setFont(exactMatchCheckBoxFont);
		this.$$$loadButtonText$$$(exactMatchCheckBox, ResourceBundle.getBundle("log4j").getString("exact.match"));
		panel3.add(exactMatchCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
		splitPane1.setLeftComponent(panel4);
		final JLabel label2 = new JLabel();
		Font label2Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label2.getFont());
		if (label2Font != null) label2.setFont(label2Font);
		this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("log4j").getString("annotations.for.owl.class"));
		panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		owlClassLabel = new JLabel();
		owlClassLabel.setText("");
		panel4.add(owlClassLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		includeClassDescendantsCheckBox = new JCheckBox();
		Font includeClassDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includeClassDescendantsCheckBox.getFont());
		if (includeClassDescendantsCheckBoxFont != null)
			includeClassDescendantsCheckBox.setFont(includeClassDescendantsCheckBoxFont);
		this.$$$loadButtonText$$$(includeClassDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants"));
		panel4.add(includeClassDescendantsCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane2 = new JScrollPane();
		panel4.add(scrollPane2, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane2.setViewportView(annotationsForClassList);
		final Spacer spacer2 = new Spacer();
		panel4.add(spacer2, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		refreshButton = new JButton();
		Font refreshButtonFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, refreshButton.getFont());
		if (refreshButtonFont != null) refreshButton.setFont(refreshButtonFont);
		this.$$$loadButtonText$$$(refreshButton, ResourceBundle.getBundle("log4j").getString("refresh"));
		panel2.add(refreshButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		panel2.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final Spacer spacer4 = new Spacer();
		panel5.add(spacer4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		Font label3Font = this.$$$getFont$$$("Verdana", Font.BOLD, 12, label3.getFont());
		if (label3Font != null) label3.setFont(label3Font);
		this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("log4j").getString("relation.annotations.for.owl.objectproperty"));
		panel5.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		owlPropertyLabel = new JLabel();
		Font owlPropertyLabelFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 10, owlPropertyLabel.getFont());
		if (owlPropertyLabelFont != null) owlPropertyLabel.setFont(owlPropertyLabelFont);
		owlPropertyLabel.setText("");
		panel5.add(owlPropertyLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		includePropertyDescendantsCheckBox = new JCheckBox();
		Font includePropertyDescendantsCheckBoxFont = this.$$$getFont$$$("Verdana", Font.PLAIN, 12, includePropertyDescendantsCheckBox.getFont());
		if (includePropertyDescendantsCheckBoxFont != null)
			includePropertyDescendantsCheckBox.setFont(includePropertyDescendantsCheckBoxFont);
		this.$$$loadButtonText$$$(includePropertyDescendantsCheckBox, ResourceBundle.getBundle("log4j").getString("include.descendants1"));
		panel5.add(includePropertyDescendantsCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane3 = new JScrollPane();
		panel5.add(scrollPane3, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		scrollPane3.setViewportView(relationsForPropertyList);
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
		ConsistencyPane consistencyPane = this;
		annotationsForClassList = new AnnotationList() {
			@Override
			public void setCollection(KnowtatorCollection<ConceptAnnotation> collection) {
				//clear collection
				((DefaultListModel) getModel()).clear();
				this.collection = collection;
				if (collection.size() == 0) {
					setEnabled(false);
				} else {
					setEnabled(true);
					collection.stream()
							.filter(conceptAnnotation -> consistencyPane.activeOWLClassDescendants.contains(conceptAnnotation.getOwlClass()))
							.forEach(k -> ((DefaultListModel<ConceptAnnotation>) getModel()).addElement(k));
				}
			}
		};
		annotationsForSpannedTextList = new AnnotationList() {
			@Override
			public void setCollection(KnowtatorCollection<ConceptAnnotation> collection) {
				//clear collection
				((DefaultListModel) getModel()).clear();
				this.collection = collection;
				if (collection.size() == 0) {
					setEnabled(false);
				} else {
					setEnabled(true);
					collection.stream()
							.filter(conceptAnnotation -> exactMatchCheckBox.isSelected() ? conceptAnnotation.getSpannedText().equals(spanLabel.getText()) : spanLabel.getText().contains(conceptAnnotation.getSpannedText()))
							.forEach(k -> ((DefaultListModel<ConceptAnnotation>) getModel()).addElement(k));
				}
			}
		};

		relationsForPropertyList = new RelationList() {
			@Override
			public void setCollection(KnowtatorCollection<RelationAnnotation> collection) {
				//clear collection
				((DefaultListModel) getModel()).clear();
				this.collection = collection;
				if (collection.size() == 0) {
					setEnabled(false);
				} else {
					setEnabled(true);
					collection.stream()
							.filter(relationAnnotation -> consistencyPane.activeOWLPropertyDescendants.contains(relationAnnotation.getProperty()))
							.forEach(k -> ((DefaultListModel<RelationAnnotation>) getModel()).addElement(k));
				}
			}
		};
	}
}
