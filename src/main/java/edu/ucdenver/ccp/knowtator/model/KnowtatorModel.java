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

import edu.ucdenver.ccp.knowtator.io.XmlUtil;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  public List<String[]> getOWLClassNotFoundAnnotations() {
    return OWLClassNotFoundAnnotations;
  }

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
    super.textSources = new TextSourceCollection(this);
    super.profiles = new ProfileCollection(this);
  }

  public void setLoading(Boolean isLoading) {
    super.loading = isLoading;
  }

  @Override
  public void save() {
    log.info(String.format("Saving project %d", super.getNumberOfTextSources()));

    super.save();
    super.profiles.forEach(Profile::save);
    super.textSources.forEach(
        textSource -> {
          textSource.save();
          log.info(String.format("Saved %s", textSource.getId()));
        });
  }

  @Override
  public void load(File projectLocation) {
    try {
      projectLocation = BaseModel.validateProjectLocation(projectLocation);
    } catch (IOException e) {
      e.printStackTrace();
    }
    super.load(projectLocation);

    setLoading(true);

    log.info("Loading profiles");
    XmlUtil xmlUtil = new KnowtatorXmlUtil();
    ConllUtil conllUtil = new ConllUtil();
    XmlUtil oldXmlUtil = new OldKnowtatorXmlUtil();

    try {
      Files.list(BaseModel.getProfilesLocation(projectLocation).toPath())
          .filter(path -> path.toString().endsWith(".xml"))
          .map(Path::toFile)
          .forEach(file -> xmlUtil.readToProfileCollection(this, file));
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      Files.list(BaseModel.getArticlesLocation(projectLocation).toPath())
          .filter(path -> path.toString().endsWith(".txt"))
          .map(Path::toFile)
          .forEach(
              file -> {
                TextSource newTextSource = new TextSource(this, file, null);
                super.textSources.add(newTextSource);
              });
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {

      log.info("Loading annotations");
      OWLClassNotFoundAnnotations = new ArrayList<>();
      Files.list(BaseModel.getAnnotationsLocation(projectLocation).toPath())
          .filter(path -> path.toString().endsWith(".xml"))
          .map(Path::toFile)
          .peek(file -> xmlUtil.readToTextSourceCollection(this, file))
          .forEach(file -> oldXmlUtil.readToTextSourceCollection(this, file));
    } catch (IOException e) {
      e.printStackTrace();
    }
    super.profiles.first().ifPresent(super.profiles::setSelection);
    super.textSources.first().ifPresent(super.textSources::setSelection);

    Set<String> owlClasses = new HashSet<>();
    super.textSources.forEach(textSource -> textSource.getConceptAnnotations().forEach(conceptAnnotation -> owlClasses.add(conceptAnnotation.getOwlClass())));
    super.profiles.verifyHighlighters(owlClasses);


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
        XmlUtil xmlUtil = new KnowtatorXmlUtil();
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
