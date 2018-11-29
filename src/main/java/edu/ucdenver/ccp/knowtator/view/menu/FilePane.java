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
import edu.ucdenver.ccp.knowtator.model.collection.event.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.listener.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

class FilePane extends MenuPane {

	private final JDialog parent;
	private final KnowtatorView view;
	private JPanel panel1;
	private JPanel contentPane;
	private JButton openButton;
	private JProgressBar progressBar1;
	private JButton newButton;

	FilePane(JDialog parent, KnowtatorView view) {
		super("File");
		openButton.addActionListener(e -> open());
		newButton.addActionListener(e -> executeNew());
		this.parent = parent;
		this.view = view;
	}

	private void executeNew() {

		String projectName = JOptionPane.showInputDialog(parent, "Enter a name for the project");

		if (!projectName.equals("")) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Select project root");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			fileChooser.addActionListener(e -> {
				if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {

					File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
					try {
						view.loadProject(projectDirectory, null);
						parent.dispose();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			});
		}
	}

	private void open() {
		String lastProjectFileName = KnowtatorView.PREFERENCES.get("Last Project", null);

		JFileChooser fileChooser = new JFileChooser();
		if (lastProjectFileName != null) {
			File lastProjectFile = new File(lastProjectFileName);
			if (lastProjectFile.exists()) {
				fileChooser.setCurrentDirectory(lastProjectFile);
				Optional<File> f = Arrays.stream(Objects.requireNonNull(lastProjectFile.listFiles())).filter(file -> file.getName().endsWith(".knowtator")).findAny();
				f.ifPresent(fileChooser::setSelectedFile);
			}
		}
		FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
		fileChooser.setFileFilter(fileFilter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			File annotationsDir = new File(file.getParentFile(), "Annotations");
			if (annotationsDir.isDirectory()) {
				progressBar1.setMaximum(Objects.requireNonNull(annotationsDir.listFiles()).length);
			}
			progressBar1.setValue(0);
			progressBar1.setStringPainted(true);
			contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			SwingWorker swingWorker = new SwingWorker() {
				@Override
				protected Object doInBackground() throws Exception {
					final int[] progress = {0};
					view.loadProject(file.getParentFile(), new TextSourceCollectionListener() {
						@Override
						public void selected(SelectionEvent<TextSource> event) {

						}

						@Override
						public void added() {
							setProgress(progress[0]++);
						}

						@Override
						public void removed() {

						}

						@Override
						public void emptied() {

						}

						@Override
						public void firstAdded() {

						}
					});
					return null;
				}
			};
			swingWorker.addPropertyChangeListener(evt -> {
				String name = evt.getPropertyName();
				if (name.equals("progress")) {
					int progress = (int) evt.getNewValue();
					progressBar1.setValue(progress);
					contentPane.repaint();
				} else if (name.equals("state")) {
					SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
					if (state == SwingWorker.StateValue.DONE) {
						parent.dispose();

						contentPane.setCursor(null);
					}
				}
			});
			swingWorker.execute();


		}
	}


	@Override
	public void show() {


	}

	@Override
	public JPanel getContentPane() {
		return contentPane;
	}

	@Override
	void dispose() {

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
		panel1 = new JPanel();
		panel1.setLayout(new BorderLayout(0, 0));
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(contentPane, BorderLayout.CENTER);
		openButton = new JButton();
		this.$$$loadButtonText$$$(openButton, ResourceBundle.getBundle("log4j").getString("open1"));
		contentPane.add(openButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		contentPane.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		contentPane.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		progressBar1 = new JProgressBar();
		contentPane.add(progressBar1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		contentPane.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		newButton = new JButton();
		this.$$$loadButtonText$$$(newButton, ResourceBundle.getBundle("log4j").getString("new1"));
		contentPane.add(newButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

}
