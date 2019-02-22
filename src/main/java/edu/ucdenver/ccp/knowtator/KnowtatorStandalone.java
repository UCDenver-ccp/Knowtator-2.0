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

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Two methods of running Knowtator on its own are provided: 1. From the command line 2. As a Swing
 * application
 *
 * <p>Command line: Knowtator can be used from the command line to convert between various kinds of
 * annotation formats including Brat, UIMA XML, the old Knowtator XML style, and the new Knowtator
 * XML style.
 *
 * <p>Swing application: Knowtator can be run on its own as a Swing application. This is useful for
 * viewing annotations without setting up and running Protege. This is also a useful way to debug
 * some aspects of the GUI. In standalone mode, annotations cannot be created because there is no
 * OWL model to use.
 *
 * @author Harrison Pielke-Lombardo
 */
class KnowtatorStandalone extends JFrame {

  private final KnowtatorView view;

  /**
   * The constructor for Knowtator standalone's GUI. Debug mode can be entered here which will
   * generate some dummy OWL API variables to test adding of concept annotations and triples.
   *
   * @param debug Specifies if debug mode should be entered.
   */
  private KnowtatorStandalone(boolean debug) {
    view = new KnowtatorView();

    setContentPane(view);

    if (debug) {
      try {
        // Use the last project opened
        File projectFile = new File(KnowtatorView.PREFERENCES.get("Last Project", null));
        view.loadProject(projectFile, null);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            view.disposeView();
            dispose();
          }
        });
  }

  /** Start a Swing application GUI. */
  private static void createGui() {
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

  /** Starts a debug GUI. */
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
   * Main method for starting either the command line or GUI modes. Arguments include -- Commands
   * for starting the GUI g: Start GUI d: Start debug GUI -- Commands for converting annotation
   * formats from the command line c: Convert formats command p: Project directory or project file
   * (extension .knowtator) a: Annotations directory (if not in project directory) t: Articles
   * directory (if not in project directory) o: Ontologies directory (if not in project directory)
   * r: Profiles directory (if not in project directory) -- Parameters to specify output locations
   * uima: Directory to output UIMA XML formatted annotations brat: Directory to output Brat
   * Standoff formatted annotations knowtator: Directory to output Knowtator project -- f: Count
   * fragments Parameters for doing calculations on fragments fragments: Directory containing
   * fragments
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
    options.addOption(
        "p",
        "project",
        true,
        "Either a project file with extension \".knowtator\" "
            + "that indicates a directory containing a Knowtator project or the directory itself.");
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

        createGui();

      } else if (cmd.getArgList().contains("d")) {
        debug();
      }

    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp("utility-name", options);

      System.exit(1);
    }
  }

  /**
   * Gets view.
   *
   * @return the view
   */
  public KnowtatorView getView() {
    return view;
  }
}
