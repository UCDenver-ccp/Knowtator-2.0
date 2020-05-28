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

package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.conll.ConllUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXmlUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.OldKnowtatorXmlUtil;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 * The Knowtator class. Contains all of the model managers. It is used to interface between the view
 * and the model. Also handles loading and saving of the project.
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorModel extends OwlModel {

  private static final Logger log = Logger.getLogger(KnowtatorModel.class);
  private List<String[]> OWLClassNotFoundAnnotations;

  /**
   * The constructor initializes all of the models and managers @param projectLocation the project
   * location.
   *
   * @param owlWorkspace the owl workspace
   * @throws IOException the io exception
   */
  public KnowtatorModel(File projectLocation, OWLWorkspace owlWorkspace) throws IOException {
    super(projectLocation, owlWorkspace);
    textSources = new TextSourceCollection(this);
    profiles = new ProfileCollection(this);
  }

  public void setLoading(Boolean isLoading) {
    this.loading = isLoading;
  }

  @Override
  public void save() {
    log.info(String.format("Saving project %d", getNumberOfTextSources()));

    super.save();
    profiles.forEach(Profile::save);
    textSources.forEach(
        textSource -> {
          textSource.save();
          log.info(String.format("Saved %s", textSource.getId()));
        });
  }

  @Override
  public void load() {
    super.load();

    setLoading(true);

    log.info("Loading profiles");
    KnowtatorXmlUtil xmlUtil = new KnowtatorXmlUtil();
    ConllUtil conllUtil = new ConllUtil();
    OldKnowtatorXmlUtil oldXmlUtil = new OldKnowtatorXmlUtil();

    try {
      Files.list(getProfilesLocation().toPath())
          .filter(path -> path.toString().endsWith(".xml"))
          .map(Path::toFile)
          .forEach(file -> xmlUtil.readToProfileCollection(this, file));

      Files.list(getArticlesLocation().toPath())
          .filter(path -> path.toString().endsWith(".txt"))
          .map(Path::toFile)
          .forEach(
              file -> {
                TextSource newTextSource = new TextSource(this, null, file.getName());
                textSources.add(newTextSource);
              });

      log.info("Loading annotations");
      OWLClassNotFoundAnnotations = new ArrayList<>();
      Files.list(getAnnotationsLocation().toPath())
          .filter(path -> path.toString().endsWith(".xml"))
          .map(Path::toFile)
          .peek(file -> xmlUtil.readToTextSourceCollection(this, file))
          .forEach(file -> oldXmlUtil.readToTextSourceCollection(this, file));

      profiles.first().ifPresent(profiles::setSelection);
      textSources.first().ifPresent(textSources::setSelection);

    } catch (IOException e) {
      e.printStackTrace();
    }

    setLoading(false);
  }

  /**
   * Takes a class capable of IO and a file, and loads it with the appropriate IOUtil for that
   * extension.
   *
   * @param file The file to load
   */
  public void loadWithAppropriateFormat(File file) {
    String[] splitOnDots = file.getName().split("\\.");
    String extension = splitOnDots[splitOnDots.length - 1];

    switch (extension) {
      case "xml":
        KnowtatorXmlUtil xmlUtil = new KnowtatorXmlUtil();
        xmlUtil.readToTextSourceCollection(this, file);
        break;
      case "ann":
        BratStandoffUtil standoffUtil = new BratStandoffUtil();
        standoffUtil.readToTextSourceCollection(this, file);
        break;
      case "a1":
        standoffUtil = new BratStandoffUtil();
        standoffUtil.readToTextSourceCollection(this, file);
        break;
      default:
        break;
    }
  }

  /**
   * The entry point of application.
   *
   * @param args Unused
   */
  public static void main(String[] args) {
    log.info("Knowtator");
  }

  public void addOWLClassNotFoundAnnotations(String annotationID, String owlClassID) {
    OWLClassNotFoundAnnotations.add(new String[]{annotationID, owlClassID});
  }
}
