package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.genia.GeniaXMLUtil;
import edu.ucdenver.ccp.knowtator.model.ProjectManager;
import edu.ucdenver.ccp.knowtator.model.text.Fragment;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class KnowtatorStandalone extends JFrame {
    private JComponent contentPane;

    private KnowtatorStandalone(KnowtatorView view) {
        contentPane = view;
        setContentPane(contentPane);
        $$$setupUI$$$();
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("g", "gui", false, "Start GUI");
        options.addOption("c", "convert", false, "Convert formats");
        options.addOption("f", "fragment", false, "Count fragments");

        options.addOption(
                "p",
                "project",
                true,
                "Either a project file with extension "
                        + "\".knowtator\" that indicates a directory containing a Knowtator project or the directory itself.");
        options.addOption("a", "annotations", true, "A directory containing annotations to be loaded");
        options.addOption("t", "articles", true, "A directory containing text articles to be loaded");
        options.addOption("o", "ontologies", true, "A directory containing ontologies to load");
        options.addOption("r", "profiles", true, "A directory containing profiles to load");

        options.addOption(null, "genia", true, "Directory to output GENIA format");
        options.addOption(null, "uima", true, "Directory to output UIMA XMI format");
        options.addOption(null, "brat", true, "Directory to output Brat format");
        options.addOption(null, "knowtator", true, "Directory to output Knowtator project format");
        options.addOption(null, "fragments", true, "Directory containing framgments");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        if (cmd.getArgList().contains("g")) {

            createGUI();

        } else if (cmd.getArgList().contains("c")) {

            commandLineConversion(cmd);
        } else if (cmd.getArgList().contains("f")) {
            fragmentCount(cmd);
        }
    }

    private static void fragmentCount(CommandLine cmd) {
        KnowtatorController conceptsController = new KnowtatorController();
        KnowtatorController fragmentsController = new KnowtatorController();

        loadProjectFromCommandLine(
                cmd,
                conceptsController.getProjectManager(),
                cmd.getOptionValue("project"),
                cmd.getOptionValue("annotations"));
        loadProjectFromCommandLine(
                cmd,
                fragmentsController.getProjectManager(),
                cmd.getOptionValue("project"),
                cmd.getOptionValue("fragments"));

        List<Fragment> fragmentList = new ArrayList<>();

        Iterator<TextSource> textSourceIterator1 =
                conceptsController.getTextSourceManager().getTextSourceCollection().iterator();
        Iterator<TextSource> textSourceIterator2 =
                fragmentsController.getTextSourceManager().getTextSourceCollection().iterator();
        while (textSourceIterator1.hasNext()) {
            TextSource textSource1 = textSourceIterator1.next();
            TextSource textSource2 = textSourceIterator2.next();

            for (Annotation fragmentAnnotation : textSource2.getAnnotationManager().getAnnotations()) {
                Span fragmentSpan = fragmentAnnotation.getSpanCollection().iterator().next();

                Fragment fragment =
                        new Fragment(fragmentAnnotation.getId(), fragmentAnnotation.getOwlClassID());

                TreeSet<Annotation> conceptAnnotationsInFragmet =
                        textSource1
                                .getAnnotationManager()
                                .getAnnotations(fragmentSpan.getStart(), fragmentSpan.getStart());

                for (Annotation conceptAnnotation : conceptAnnotationsInFragmet) {
                    fragment.add(conceptAnnotation.getOwlClassID());
                }

                fragmentList.add(fragment);
            }

            writeFragments(cmd.getOptionValue("fragments") + File.separator + textSource1.getId() + ".txt", fragmentList);
        }


    }

    private static void writeFragments(String outputFileName, List<Fragment> fragmentList) {
        File outputFile = new File(outputFileName);

        try {
            BufferedWriter bw = new BufferedWriter(new PrintWriter(outputFile));

            for (Fragment fragment : fragmentList) {
                try {
                    bw.write(String.format("%s\t%s\n", fragment.getId(), fragment.getType()));
                    fragment.getConceptCountMap().forEach((concept, count) -> {
                        try {
                            bw.write(String.format("\t%s\t%d\n", concept, count));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProjectFromCommandLine(
            CommandLine cmd,
            ProjectManager projectManager,
            String projectFileName,
            String annotationsDirName) {
        if (projectFileName == null) {
            String ontologiesDirName = cmd.getOptionValue("ontologies");
            String articlesDirName = cmd.getOptionValue("articles");
            String knowtatorOutputDirName = cmd.getOptionValue("knowtator");
            String profilesDirName = cmd.getOptionValue("profiles");

            projectManager.importProject(
                    profilesDirName == null ? null : new File(profilesDirName),
                    ontologiesDirName == null ? null : new File(ontologiesDirName),
                    articlesDirName == null ? null : new File(articlesDirName),
                    annotationsDirName == null ? null : new File(annotationsDirName),
                    new File(knowtatorOutputDirName));

        } else {
            projectManager.loadProject(new File(projectFileName));
        }
    }

    private static void commandLineConversion(CommandLine cmd) {
        KnowtatorController controller = new KnowtatorController();
        ProjectManager projectManager = controller.getProjectManager();

        String projectFileName = cmd.getOptionValue("project");

        String geniaOutputDirName = cmd.getOptionValue("genia");
//        String uimaOutputDirName = cmd.getOptionValue("uima");
        String bratOutputDirName = cmd.getOptionValue("brat");
        String knowtatorOutputDirName = cmd.getOptionValue("knowtator");

        loadProjectFromCommandLine(
                cmd, projectManager, projectFileName, cmd.getOptionValue("annotations"));

        if (knowtatorOutputDirName != null) {
            projectManager.saveProject();
        }

        if (geniaOutputDirName != null) {
            projectManager.saveToFormat(GeniaXMLUtil.class, new File(geniaOutputDirName));
        }

//        if (uimaOutputDirName != null) {
//            projectManager.saveToFormat(UIMAXMIUtil.class, new File(uimaOutputDirName));
//        }

        if (bratOutputDirName != null) {
            projectManager.saveToFormat(BratStandoffUtil.class, new File(bratOutputDirName));
        }
    }

    private static void createGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        KnowtatorView view = new KnowtatorView();
        KnowtatorStandalone dialog = new KnowtatorStandalone(view);

        if (dialog.getJMenuBar() == null) {
            JMenuBar menuBar = new JMenuBar();
            dialog.setJMenuBar(menuBar);
        }
        dialog.getJMenuBar().add(view.getProjectMenu());
        dialog.pack();
        dialog.setVisible(true);
        //        System.exit(0);
    }

    private void createUIComponents() {

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
        contentPane.setAutoscrolls(true);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
