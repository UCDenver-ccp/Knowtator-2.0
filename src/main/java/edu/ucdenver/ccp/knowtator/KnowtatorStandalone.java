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

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.model.text.Fragment;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


/**
 * Two methods of running Knowtator on its own are provided:
 * 1. From the command line
 * 2. As a Swing application
 * <p>
 * Command line:
 * Knowtator can be used from the command line to convert between various kinds of annotation formats including
 * Brat, UIMA XML, the old Knowtator XML style, and the new Knowtator XML style.
 * <p>
 * Swing application:
 * Knowtator can be run on its own as a Swing application. This is useful for viewing annotations without setting
 * up and running Protege. This is also a useful way to debug some aspects of the GUI. In standalone mode,
 * annotations cannot be created because there is no OWL model to use.
 *
 * @author Harrison Pielke-Lombardo
 */
class KnowtatorStandalone extends JFrame {

	/**
	 * The constructor for Knowtator standalone's GUI. Debug mode can be entered here which will generate some
	 * dummy OWL API variables to test adding of concept annotations and triples.
	 *
	 * @param debug Specifies if debug mode should be entered.
	 */
	private KnowtatorStandalone(boolean debug) {
		final KnowtatorView view = new KnowtatorView();

		setContentPane(view);

		if (debug) {
			view.getController().setDebug();
			view.reset();
			try {
				// Use the last project opened
				File projectFile = new File(KnowtatorView.PREFERENCES.get("Last Project", null));
				view.getController().setSaveLocation(projectFile);
				view.getController().loadProject();
				view.projectLoaded();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				view.disposeView();
				dispose();
			}
		});
	}

	/**
	 * Start a Swing application GUI
	 */
	private static void createGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		KnowtatorStandalone dialog = new KnowtatorStandalone(false);
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Starts a debug GUI
	 */
	private static void debug() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		KnowtatorStandalone dialog = new KnowtatorStandalone(true);
		dialog.pack();
		dialog.setVisible(true);
		//        System.exit(0);
	}

	/**
	 * Counts fragments
	 *
	 * @param cmd A command line object
	 */
	private static void fragmentCount(CommandLine cmd) {
		KnowtatorController conceptsController = new KnowtatorController();
		KnowtatorController fragmentsController = new KnowtatorController();

		loadProjectFromCommandLine(
				cmd,
				conceptsController,
				cmd.getOptionValue("project"),
				cmd.getOptionValue("annotations"));
		loadProjectFromCommandLine(
				cmd,
				fragmentsController,
				cmd.getOptionValue("project"),
				cmd.getOptionValue("fragments"));

		List<Fragment> fragmentList = new ArrayList<>();

		Iterator<TextSource> textSourceIterator1 =
				conceptsController.getTextSourceCollection().iterator();
		Iterator<TextSource> textSourceIterator2 =
				fragmentsController.getTextSourceCollection().iterator();
		while (textSourceIterator1.hasNext()) {
			TextSource textSource1 = textSourceIterator1.next();
			TextSource textSource2 = textSourceIterator2.next();

			for (ConceptAnnotation fragmentConceptAnnotation : textSource2.getConceptAnnotationCollection()) {
				Span fragmentSpan = fragmentConceptAnnotation.getSpanCollection().iterator().next();

				Fragment fragment =
						new Fragment(textSource2, fragmentConceptAnnotation.getId(), fragmentConceptAnnotation.getOwlClassID());

				TreeSet<ConceptAnnotation> conceptAnnotationsInFragment =
						textSource1
								.getConceptAnnotationCollection()
								.getAnnotations(fragmentSpan.getStart(), fragmentSpan.getStart());

				for (ConceptAnnotation conceptAnnotation : conceptAnnotationsInFragment) {
					fragment.add(conceptAnnotation.getOwlClassID());
				}

				fragmentList.add(fragment);
			}

			writeFragments(cmd.getOptionValue("fragments") + File.separator + textSource1.getId() + ".txt", fragmentList);
		}


	}

	/**
	 * Handles output of fragments
	 *
	 * @param outputFileName Name of the file to output to
	 * @param fragmentList   List of fragments
	 */
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

	/**
	 * Loads a Knowtator project
	 *
	 * @param cmd                A command line object
	 * @param controller         A controller to load the project into
	 * @param projectFileName    Location of the project
	 * @param annotationsDirName Name of the directory containing annotation files
	 */
	private static void loadProjectFromCommandLine(
			CommandLine cmd,
			KnowtatorController controller,
			String projectFileName,
			String annotationsDirName) {
		if (projectFileName == null) {
			String ontologiesDirName = cmd.getOptionValue("ontologies");
			String articlesDirName = cmd.getOptionValue("articles");
			String knowtatorOutputDirName = cmd.getOptionValue("knowtator");
			String profilesDirName = cmd.getOptionValue("profiles");

			controller.importProject(
					profilesDirName == null ? null : new File(profilesDirName),
					ontologiesDirName == null ? null : new File(ontologiesDirName),
					articlesDirName == null ? null : new File(articlesDirName),
					annotationsDirName == null ? null : new File(annotationsDirName),
					new File(knowtatorOutputDirName));

		} else {
			try {
				controller.setSaveLocation(new File(projectFileName));
				controller.loadProject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Converts between specified formats from the command line
	 *
	 * @param cmd A command line object
	 */
	private static void commandLineConversion(CommandLine cmd) {
		KnowtatorController controller = new KnowtatorController();

		String projectFileName = cmd.getOptionValue("project");

//        String uimaOutputDirName = cmd.getOptionValue("uima");
		String bratOutputDirName = cmd.getOptionValue("brat");
		String knowtatorOutputDirName = cmd.getOptionValue("knowtator");

		loadProjectFromCommandLine(
				cmd, controller, projectFileName, cmd.getOptionValue("annotations"));

		if (knowtatorOutputDirName != null) {
			controller.save();
		}

//        if (uimaOutputDirName != null) {
//            projectManager.saveToFormat(UIMAXMIUtil.class, new File(uimaOutputDirName));
//        }

		if (bratOutputDirName != null) {
			controller.saveToFormat(BratStandoffUtil.class, controller.getTextSourceCollection(), new File(bratOutputDirName));
		}
	}

	/**
	 * Main method for starting either the command line or GUI modes. Arguments include
	 * --
	 * Commands for starting the GUI
	 * g: Start GUI
	 * d: Start debug GUI
	 * --
	 * Commands for converting annotation formats from the command line
	 * c: Convert formats command
	 * p: Project directory or project file (extension .knowtator)
	 * a: Annotations directory (if not in project directory)
	 * t: Articles directory (if not in project directory)
	 * o: Ontologies directory (if not in project directory)
	 * r: Profiles directory (if not in project directory)
	 * --
	 * Parameters to specify output locations
	 * uima: Directory to output UIMA XML formatted annotations
	 * brat: Directory to output Brat Standoff formatted annotations
	 * knowtator: Directory to output Knowtator project
	 * --
	 * f: Count fragments
	 * Parameters for doing calculations on fragments
	 * fragments: Directory containing fragments
	 *
	 * @param args A list of arguments
	 */
	public static void main(String[] args) {
		Options options = new Options();

		// Make GUI commands
		options.addOption("g", "gui", false, "Start GUI");
		options.addOption("d", "debug", false, "Start debug GUI");

		// Conversion
		options.addOption("c", "convert", false, "Convert formats");
		// Input options
		options.addOption("p", "project", true, "Either a project file with extension "
				+ "\".knowtator\" that indicates a directory containing a Knowtator project or the directory itself.");
		options.addOption("a", "annotations", true, "A directory containing annotations to be loaded");
		options.addOption("t", "articles", true, "A directory containing text articles to be loaded");
		options.addOption("o", "ontologies", true, "A directory containing ontologies to load");
		options.addOption("r", "profiles", true, "A directory containing profiles to load");
		// Output options
		options.addOption(null, "uima", true, "Directory to output UIMA XMI format");
		options.addOption(null, "brat", true, "Directory to output Brat format");
		options.addOption(null, "knowtator", true, "Directory to output Knowtator project format");

		// Fragments
		options.addOption("f", "fragment", false, "Count fragments");
		options.addOption(null, "fragments", true, "Directory containing fragments");

		HelpFormatter formatter = new HelpFormatter();
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			if (cmd.getArgList().contains("g")) {

				createGUI();

			} else if (cmd.getArgList().contains("c")) {

				commandLineConversion(cmd);
			} else if (cmd.getArgList().contains("f")) {
				fragmentCount(cmd);
			} else if (cmd.getArgList().contains("d")) {
				debug();
			}

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}


	}
}
