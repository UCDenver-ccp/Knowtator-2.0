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
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;

public class IAAOptionsDialog extends JDialog implements KnowtatorComponent {
  private final KnowtatorView view;
  private KnowtatorModel iaaModel;
  private final KnowtatorModel model;
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JButton documentsSelectAllButton;
  private JPanel profilesPanel;
  private JPanel documentsPanel;
  private final File outputDirectory;
  private JCheckBox iaaClassCheckBox;
  private JCheckBox iaaSpanCheckBox;
  private JCheckBox iaaClassAndSpanCheckBox;
  private JButton profilesSelectAllButton;
  private JTable profilesTable;
  private JTable documentsTable;
  private JButton owlClassesSelectAllButton;
  private OWLClassesTable owlClassesTable;
  private JButton compareAcrossProjectsButton;
  private JCheckBox includeSubclassesCheckBox;
  IAATableModel profilesTableModel;
  IAATableModel documentsTableModel;
  IAATableModel owlClassesTableModel;

  private static final Logger log = Logger.getLogger(IAAOptionsDialog.class);

  public IAAOptionsDialog(
      Window parent, KnowtatorModel model, KnowtatorView view, File outputDirectory) {
    super(parent);
    this.outputDirectory = outputDirectory;
    this.model = model;
    this.iaaModel = model;
    this.view = view;
    $$$setupUI$$$();

    setContentPane(contentPane);
    setModal(false);
    getRootPane().setDefaultButton(buttonOK);

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

    setModels(model);
    documentsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    owlClassesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    profilesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    owlClassesSelectAllButton.addActionListener(e -> owlClassesTableModel.toggleAll());
    documentsSelectAllButton.addActionListener(e -> documentsTableModel.toggleAll());
    profilesSelectAllButton.addActionListener(e -> profilesTableModel.toggleAll());

    compareAcrossProjectsButton.addActionListener(
        e -> {
          JFileChooser fileChooser = new JFileChooser();
          Optional.ofNullable(KnowtatorView.PREFERENCES.get("Last Project", null))
              .map(File::new)
              .filter(File::exists)
              .map(
                  file -> {
                    fileChooser.setCurrentDirectory(file);
                    return file;
                  })
              .map(File::listFiles)
              .flatMap(
                  files ->
                      Arrays.stream(files)
                          .filter(file1 -> file1.getName().endsWith(".knowtator"))
                          .findAny())
              .ifPresent(fileChooser::setSelectedFile);
          fileChooser.addActionListener(
              e1 ->
                  Optional.ofNullable(e1)
                      .filter(
                          event -> event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
                      .ifPresent(
                          event -> {
                            try {
                              File projectLocation1 = fileChooser.getSelectedFile();
                              KnowtatorModel newModel =
                                  mergeProjects(
                                      projectLocation1,
                                      this.model,
                                      view.getOWLWorkspace(),
                                      false,
                                      true,
                                      this);

                              setModels(newModel);

                            } catch (IOException ioException) {
                              ioException.printStackTrace();
                            }
                          }));

          FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
          fileChooser.setFileFilter(fileFilter);
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

          fileChooser.showOpenDialog(view);
        });

    reset();
  }

  public static KnowtatorModel mergeProjects(
      File projectLocation1,
      KnowtatorModel model2,
      OWLWorkspace owlWorkspace,
      boolean mergeProfiles,
      boolean allowIdOverlap,
      JDialog component)
      throws IOException {
    KnowtatorModel newModel = new KnowtatorModel(projectLocation1, owlWorkspace);
    newModel.load(projectLocation1);
    Set<Profile> profilesInCommon =
        newModel.getProfiles().stream()
            .filter(profile -> model2.getProfiles().contains(profile))
            .collect(Collectors.toSet());

    if (!mergeProfiles) {
      for (Profile profile : profilesInCommon) {
        if (JOptionPane.showConfirmDialog(
            component,
            String.format("Merge %s between projects for IAA?", profile),
            "Profile merge?",
            JOptionPane.YES_NO_OPTION)
            == JOptionPane.NO_OPTION) {
          profile.setId(
              String.format(
                  "%s - %s",
                  profile.getId(), projectLocation1.getName().replace(".knowtator", "")));
        }
      }
    }

    newModel.setAllowIdOverlap(allowIdOverlap);
    newModel.load(model2.getProjectLocation());
    return newModel;
  }

  private void setModels(KnowtatorModel model) {
    this.iaaModel = model;
    documentsTableModel =
        new IAATableModel(
            new Object[][] {},
            "Document",
            model.getTextSources().stream().map(ModelObject::getId).collect(Collectors.toList()));
    documentsTable.setModel(documentsTableModel);

    profilesTableModel =
        new IAATableModel(
            new Object[][] {},
            "Profile",
            model.getProfiles().stream().map(ModelObject::getId).collect(Collectors.toList()));
    profilesTable.setModel(profilesTableModel);

    owlClassesTableModel =
        new IAATableModel(
            new Object[][] {},
            "OWL Classes",
            new ArrayList<>(
                new HashSet<>(
                    model.getTextSources().stream()
                        .flatMap(
                            textSource ->
                                textSource.getConceptAnnotations().stream()
                                    .map(ConceptAnnotation::getOwlClass))
                        .collect(Collectors.toSet()))));
    owlClassesTable.setModel(owlClassesTableModel);
  }

  private void createUIComponents() {
    includeSubclassesCheckBox = new JCheckBox();
    owlClassesTable = new OWLClassesTable(view, includeSubclassesCheckBox);
  }

  private static class OWLClassesTable extends JTable
      implements KnowtatorComponent, OWLSelectionModelListener {

    private final KnowtatorView view;
    private final JCheckBox includeSubclassesCheckBox;

    public OWLClassesTable(KnowtatorView view, JCheckBox includeSubclassesCheckBox) {
      super();
      this.view = view;
      this.includeSubclassesCheckBox = includeSubclassesCheckBox;
    }

    @Override
    public void selectionChanged() {
      view.getModel()
          .ifPresent(
              model -> {
                Optional<String> owlClassOptional = model.getSelectedOwlClass();

                owlClassOptional.ifPresent(
                    owlClass -> {
                      int i = selectOwlClass(owlClass);
                      if (includeSubclassesCheckBox.isSelected()) {
                        for (String d : model.getOwlClassDescendants(owlClass)) {
                          selectOwlClass(d);
                        }
                      }

                      if (-1 < i && i < getModel().getRowCount()) {
                        scrollRectToVisible(getCellRect(i, 0, true));
                      }
                    });
              });
    }

    private int selectOwlClass(String owlClass) {
      int i = getModel().getRowCount() == 0 ? -1 : 0;
      while (i < getModel().getRowCount() && (i != -1 && !getValueAt(i, 0).equals(owlClass))) {
        i++;
      }

      if (-1 < i && i < getModel().getRowCount()) {
        setValueAt(Boolean.TRUE, i, 1);
      }
      return i;
    }

    @Override
    public void reset() {
      view.getModel().ifPresent(model -> model.addOwlSelectionModelListener(this));
    }

    @Override
    public void dispose() {
      view.getModel().ifPresent(model -> model.removeOwlSelectionModelListener(this));
    }
  }

  private static class IAATableModel extends DefaultTableModel {
    private final int checkCol = 1;

    IAATableModel(Object[][] data, String col, List<String> collection) {
      super(data, new String[] {col, "Checkbox"});

      setCollection(collection);
    }

    private void setCollection(List<String> collection) {
      for (int i = 0; i < this.getRowCount(); i++) {
        removeRow(i);
      }
      for (String item : collection) {
        addRow(new Object[] {item, Boolean.FALSE});
      }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 1) {
        return getValueAt(0, checkCol).getClass();
      } else {
        return super.getColumnClass(columnIndex);
      }
    }

    Set<String> getSelectedItems() {
      return IntStream.range(0, getRowCount())
          .filter(i -> (Boolean) getValueAt(i, 1))
          .mapToObj(i -> (String) getValueAt(i, 0))
          .collect(Collectors.toSet());
    }

    boolean allSelected() {
      return IntStream.range(0, getRowCount()).allMatch(i -> (Boolean) getValueAt(i, 1));
    }

    void toggleAll() {
      if (allSelected()) {
        IntStream.range(0, getRowCount()).forEach(i -> setValueAt(Boolean.FALSE, i, checkCol));
      } else {
        IntStream.range(0, getRowCount()).forEach(i -> setValueAt(Boolean.TRUE, i, checkCol));
      }
    }
  }

  private void onOK() {
    Set<String> profiles = profilesTableModel.getSelectedItems();
    Set<String> owlClasses = owlClassesTableModel.getSelectedItems();

    Set<String> textSources = documentsTableModel.getSelectedItems();
    if (profiles.size() <= 2
        && !textSources.isEmpty()
        && (iaaClassCheckBox.isSelected()
        || iaaSpanCheckBox.isSelected()
        || iaaClassAndSpanCheckBox.isSelected())) {
      try {
        KnowtatorIaa knowtatorIaa =
            new KnowtatorIaa(outputDirectory, iaaModel, textSources, profiles, owlClasses);

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
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  @Override
  public void reset() {
    view.knowtatorComponents.add(owlClassesTable);
    owlClassesTable.reset();
  }

  @Override
  public void dispose() {
    view.knowtatorComponents.remove(owlClassesTable);
    super.dispose();
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
    final JSplitPane splitPane1 = new JSplitPane();
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(splitPane1, gbc);
    final JSplitPane splitPane2 = new JSplitPane();
    splitPane1.setLeftComponent(splitPane2);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    splitPane2.setLeftComponent(panel1);
    panel1.setBorder(BorderFactory.createTitledBorder(null, "Mode", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    iaaClassCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(iaaClassCheckBox, this.$$$getMessageFromBundle$$$("log4j", "class.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(iaaClassCheckBox, gbc);
    iaaSpanCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(iaaSpanCheckBox, this.$$$getMessageFromBundle$$$("log4j", "span.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(iaaSpanCheckBox, gbc);
    iaaClassAndSpanCheckBox = new JCheckBox();
    this.$$$loadButtonText$$$(iaaClassAndSpanCheckBox, this.$$$getMessageFromBundle$$$("log4j", "class.and.span.iaa"));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(iaaClassAndSpanCheckBox, gbc);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new BorderLayout(0, 0));
    splitPane2.setRightComponent(panel2);
    panel2.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("ui", "profiles"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    profilesPanel = new JPanel();
    profilesPanel.setLayout(new BorderLayout(0, 0));
    panel2.add(profilesPanel, BorderLayout.NORTH);
    final JScrollPane scrollPane1 = new JScrollPane();
    profilesPanel.add(scrollPane1, BorderLayout.CENTER);
    profilesTable = new JTable();
    profilesTable.setAutoCreateRowSorter(true);
    scrollPane1.setViewportView(profilesTable);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new BorderLayout(0, 0));
    profilesPanel.add(panel3, BorderLayout.NORTH);
    profilesSelectAllButton = new JButton();
    this.$$$loadButtonText$$$(profilesSelectAllButton, this.$$$getMessageFromBundle$$$("log4j", "select.all"));
    panel3.add(profilesSelectAllButton, BorderLayout.EAST);
    final JSplitPane splitPane3 = new JSplitPane();
    splitPane1.setRightComponent(splitPane3);
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new BorderLayout(0, 0));
    splitPane3.setLeftComponent(panel4);
    panel4.setBorder(BorderFactory.createTitledBorder(null, this.$$$getMessageFromBundle$$$("log4j", "documents"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    documentsPanel = new JPanel();
    documentsPanel.setLayout(new BorderLayout(0, 0));
    panel4.add(documentsPanel, BorderLayout.NORTH);
    final JScrollPane scrollPane2 = new JScrollPane();
    documentsPanel.add(scrollPane2, BorderLayout.CENTER);
    documentsTable = new JTable();
    scrollPane2.setViewportView(documentsTable);
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new BorderLayout(0, 0));
    documentsPanel.add(panel5, BorderLayout.NORTH);
    documentsSelectAllButton = new JButton();
    this.$$$loadButtonText$$$(documentsSelectAllButton, this.$$$getMessageFromBundle$$$("log4j", "select.all"));
    panel5.add(documentsSelectAllButton, BorderLayout.EAST);
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new BorderLayout(0, 0));
    splitPane3.setRightComponent(panel6);
    panel6.setBorder(BorderFactory.createTitledBorder(null, "OWL Classes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new BorderLayout(0, 0));
    panel6.add(panel7, BorderLayout.CENTER);
    final JScrollPane scrollPane3 = new JScrollPane();
    panel7.add(scrollPane3, BorderLayout.CENTER);
    scrollPane3.setViewportView(owlClassesTable);
    final JPanel panel8 = new JPanel();
    panel8.setLayout(new BorderLayout(0, 0));
    panel7.add(panel8, BorderLayout.NORTH);
    owlClassesSelectAllButton = new JButton();
    owlClassesSelectAllButton.setText("Select all");
    panel8.add(owlClassesSelectAllButton, BorderLayout.EAST);
    final JPanel panel9 = new JPanel();
    panel9.setLayout(new GridBagLayout());
    panel6.add(panel9, BorderLayout.SOUTH);
    includeSubclassesCheckBox.setText("Include subclasses");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel9.add(includeSubclassesCheckBox, gbc);
    final JPanel panel10 = new JPanel();
    panel10.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add(panel10, gbc);
    compareAcrossProjectsButton = new JButton();
    compareAcrossProjectsButton.setText("Compare across projects");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel10.add(compareAcrossProjectsButton, gbc);
    buttonCancel = new JButton();
    this.$$$loadButtonText$$$(buttonCancel, this.$$$getMessageFromBundle$$$("log4j", "cancel"));
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel10.add(buttonCancel, gbc);
    buttonOK = new JButton();
    buttonOK.setText("Run");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel10.add(buttonOK, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel10.add(spacer1, gbc);
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
