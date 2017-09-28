package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.stats.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.stats.SlotMatcherConfig;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class RunIAACommand extends DisposableAction{

    private KnowtatorView view;

    public RunIAACommand(KnowtatorView view) {
        super("Run IAA", KnowtatorIcons.getIcon(KnowtatorIcons.RUN_IAA_ICON));
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
