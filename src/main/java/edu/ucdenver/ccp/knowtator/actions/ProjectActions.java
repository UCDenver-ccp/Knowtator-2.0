package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.profile.Profile;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ProjectActions {

    public static void loadProject(KnowtatorManager manager) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
        FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
        fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            ProjectActions.loadProject(manager, fileChooser.getSelectedFile().getAbsolutePath(), false);
        }
    }

    private static void loadProject(KnowtatorManager manager, String fileName, @SuppressWarnings("SameParameterValue") Boolean fromResources) {
        manager.getXmlUtil().read(fileName, fromResources);
    }

    public static void saveProject(KnowtatorManager manager) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
        FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
        fileChooser.setFileFilter(fileFilter);

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            List<JCheckBox> textSourceOptions = new ArrayList<>(); // Which textSources to save
            List<JCheckBox> profileOptions = new ArrayList<>();  // Annotations for which profiles to save
            List<JCheckBox> annotationChoiceOptions = new ArrayList<>();  // Which types of annotations to save

            List<TextSource> textSourceList = new ArrayList<>(manager.getTextSourceManager().getTextSources());
            List<Profile> profileList = new ArrayList<>(manager.getProfileManager().getProfiles().values());
            List<String> annotationChoiceList = Arrays.asList("Concept Annotations", "Compositional Annotations");

            JPanel optionPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 1;
            optionPanel.add(new JLabel("TextSources"), gbc);
            for (TextSource textSource : textSourceList) {
                JCheckBox checkBox = new JCheckBox(textSource.getDocID());
                checkBox.setSelected(true);
                textSourceOptions.add(checkBox);
                optionPanel.add(checkBox, gbc);
            }

            gbc.gridx = 2;
            optionPanel.add(new JLabel("Profiles"), gbc);
            for (Profile profile : profileList) {
                JCheckBox checkBox = new JCheckBox(profile.getProfileID());
                checkBox.setSelected(true);
                profileOptions.add(checkBox);
                optionPanel.add(checkBox, gbc);
            }

            gbc.gridx = 3;
            optionPanel.add(new JLabel("Annotation Options"), gbc);
            for (String annotationOption : annotationChoiceList) {
                JCheckBox checkBox = new JCheckBox(annotationOption);
                checkBox.setSelected(true);
                annotationChoiceOptions.add(checkBox);
                optionPanel.add(checkBox, gbc);
            }

            if (JOptionPane.showConfirmDialog(null, optionPanel) == JOptionPane.OK_OPTION) {
                Set<Profile> profileFilters = new HashSet<>();
                Set<TextSource> textSourceFilters = new HashSet<>();
                for (int i = 0; i < profileList.size(); i++) {
                    if (profileOptions.get(i).isSelected()) {
                        profileFilters.add(profileList.get(i));
                    }
                }

                for (int i = 0; i < textSourceList.size(); i++) {
                    if (textSourceOptions.get(i).isSelected()) {
                        textSourceFilters.add(textSourceList.get(i));
                    }
                }

                boolean saveConceptAnnotations = annotationChoiceOptions.get(0).isSelected();
                boolean saveCompositionalAnnotations = annotationChoiceOptions.get(1).isSelected();

                manager.getConfigProperties().setDefaultSaveLocation(fileChooser.getSelectedFile().getAbsolutePath());
                manager.getConfigProperties().setProfileFilters(profileFilters);
                manager.getConfigProperties().setTextSourceFilters(textSourceFilters);
                manager.getConfigProperties().setSaveConceptAnnotations(saveConceptAnnotations);
                manager.getConfigProperties().setSaveCompositionalAnnotations(saveCompositionalAnnotations);

                ProjectActions.saveProject(manager, fileChooser.getSelectedFile().getAbsolutePath());

            }
        }
    }

    private static void saveProject(KnowtatorManager manager, String fileName) {
        manager.getXmlUtil().write(fileName);
    }
}
