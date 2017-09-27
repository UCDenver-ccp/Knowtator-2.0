package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.iaa.IAAException;
import edu.ucdenver.cpbs.mechanic.stats.KnowtatorIAA;
import edu.ucdenver.cpbs.mechanic.stats.SlotMatcherConfig;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;

public class RunIAACommand extends DisposableAction{

    private MechAnICView view;

    public RunIAACommand(MechAnICView view) {
        super("Run IAA", MechAnICIcons.getIcon(MechAnICIcons.RUN_IAA_ICON));
        this.view = view;

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Run Inter-annotator agreement");

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File outputDirectory = fileChooser.getSelectedFile();

            try {
                KnowtatorIAA knowtatorIAA = new KnowtatorIAA(outputDirectory, view);
                SlotMatcherConfig slotMatcherConfig = null; // getSlotMatcherConfig();

                Object[] options = { "Class IAA", "Span IAA", "Class and Span IAA", "Subclass IAA", "Feature Matcher IAA" };
                int response = JOptionPane.showOptionDialog(null, "Choose which type of IAA to perform", "Run IAA",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                //TODO Should not be cases but checkboxes so multiple IAAs can be selected
//                switch (response) {
//                    case 0:
//                        knowtatorIAA.runClassIAA();
//                    case 1:
//                        knowtatorIAA.runSpanIAA();
//                    case 2:
//                        knowtatorIAA.runClassAndSpanIAA();
//                    case 3:
//                        knowtatorIAA.runSubclassIAA();
//                }
//                if (slotMatcherConfig != null)
//                    knowtatorIAA.runFeatureMatcherIAA(slotMatcherConfig);

                knowtatorIAA.closeHTML();

            } catch (IAAException iaae) {
                iaae.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {

    }
}
