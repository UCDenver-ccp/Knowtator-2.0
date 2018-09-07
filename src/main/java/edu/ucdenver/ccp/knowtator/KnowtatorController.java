package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.DebugListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.KnowtatorTextBoundObject;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.owl.OWLManager;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionManager;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KnowtatorController extends ProjectManager implements Savable, ProjectListener {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(KnowtatorController.class);

  private SelectionManager selectionManager;
  private TextSourceManager textSourceManager;
  private ProfileManager profileManager;
  private OWLManager owlManager;

  private TreeMap<String, KnowtatorObject> idRegistry;
  private List<DebugListener> debugListeners;
  private List<ViewListener> viewListeners;


  public KnowtatorController() {
    owlManager = new OWLManager(this);
    textSourceManager = new TextSourceManager(this);
    profileManager = new ProfileManager(this); // manipulates profiles and colors
    idRegistry = new TreeMap<>();
    debugListeners = new ArrayList<>();
    selectionManager = new SelectionManager(this);

    addProjectListener(this);
    addProjectListener(selectionManager);
    addProjectListener(owlManager);

    viewListeners = new ArrayList<>();
  }

  @Override
  void makeProjectStructure(File projectDirectory) {
    try {
      setSaveLocation(projectDirectory, null);
      textSourceManager.setSaveLocation(new File(projectDirectory, "Articles"), ".txt");
      owlManager.setSaveLocation(new File(projectDirectory, "Ontologies"), null);
      textSourceManager.setSaveLocation(new File(projectDirectory, "Annotations"), ".xml");
      profileManager.setSaveLocation(new File(projectDirectory, "Profiles"), null);


      if (FileUtils.listFiles(projectDirectory, new String[]{"knowtator"}, false).size() == 0)
        Files.createFile(
                new File(projectDirectory, projectDirectory.getName() + ".knowtator").toPath());
    } catch (IOException e) {
      System.err.println("Cannot create directories - " + e);
    }
  }

  @Override
  List<KnowtatorManager> getManagers() {
    List<KnowtatorManager> managers = new ArrayList<>();
    managers.add(owlManager);
    managers.add(selectionManager);
    managers.add(textSourceManager);
    managers.add(profileManager);
    return managers;
  }

  @Override
  public void addDocument(File file) {
    if (!file.getParentFile().equals(textSourceManager.getArticlesLocation())) {
      try {
        FileUtils.copyFile(file, new File(textSourceManager.getAnnotationsLocation(), file.getName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    textSourceManager.addTextSource(null, file.getName(), file.getName());
  }

  @Override
  public void saveToFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
    try {
      try {
        owlManager.setRenderRDFSLabel();
      } catch (OWLWorkSpaceNotSetException ignored) {
      }
      BasicIOUtil util = ioClass.getDeclaredConstructor().newInstance();
      util.write(savable != null ? savable : textSourceManager, file);
    } catch (InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException
            | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  void loadProject() {
    projectLoaded = false;
    getProjectListeners().forEach(ProjectListener::projectClosed);

    if (owlManager.getSaveLocation(".obo") != null) {
      log.warn("Loading ontologies");
      try {
        owlManager.read(owlManager.getSaveLocation(".obo"));
      } catch (IOException | OWLWorkSpaceNotSetException e) {
        log.warn("Could not load ontologies");
      }
    }

    if (profileManager.getSaveLocation(null) != null) {
      log.warn("Loading profiles");
      loadFromFormat(KnowtatorXMLUtil.class, profileManager, profileManager.getSaveLocation(null));
    }

    if (textSourceManager.getAnnotationsLocation() != null) {
      log.warn("Loading annotations");
      loadFromFormat(
              KnowtatorXMLUtil.class, textSourceManager, textSourceManager.getAnnotationsLocation());
    }

    if (textSourceManager.getTextSourceCollection().getCollection().isEmpty()) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(textSourceManager.getArticlesLocation());

      JOptionPane.showMessageDialog(null, "Please select a document to annotate");
      if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        addDocument(fileChooser.getSelectedFile());
      }
    }

    projectLoaded = true;

    for (ProjectListener listener : getProjectListeners()) {
      listener.projectLoaded();
    }
  }

  @Override
  void importProject(File profilesLocation, File ontologiesLocation, File articlesLocation, File annotationsLocation, File projectLocation) {
    makeProjectStructure(projectLocation);
    try {
      importToManager(profilesLocation, profileManager, ".xml");
      importToManager(ontologiesLocation, owlManager, ".obo");
      importToManager(ontologiesLocation, textSourceManager, ".txt");
      importToManager(ontologiesLocation, textSourceManager, ".xml");

    } catch (IOException e) {
      e.printStackTrace();
    }
    loadProject();
  }

  public static void main(String[] args) {}


  /*
  GETTERS
   */

  public OWLManager getOWLManager() {
    return owlManager;
  }

  public ProfileManager getProfileManager() {
    return profileManager;
  }

  public TextSourceManager getTextSourceManager() {
    return textSourceManager;
  }



  public SelectionManager getSelectionManager() {
    return selectionManager;
  }

  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    profileManager.writeToKnowtatorXML(dom, parent);
    textSourceManager.writeToKnowtatorXML(dom, parent);
  }

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    profileManager.readFromKnowtatorXML(file, parent);
    textSourceManager.readFromKnowtatorXML(file, parent);
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {
    profileManager.readFromOldKnowtatorXML(file, parent);
    textSourceManager.readFromOldKnowtatorXML(file, parent);
  }

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {}

  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  @Override
  public void save() {

  }

  public void verifyId(String id, KnowtatorObject obj, Boolean hasPriority) {
  	String verifiedId = id;
    if (hasPriority && idRegistry.keySet().contains(id)) {
		verifyId(id, idRegistry.get(id), false);
    } else {
      int i = idRegistry.size();
      while (verifiedId == null || idRegistry.keySet().contains(verifiedId)) {
        if (obj instanceof  KnowtatorTextBoundObject && ((KnowtatorTextBoundObject) obj).getTextSource() != null) {
          verifiedId = ((KnowtatorTextBoundObject) obj).getTextSource().getId() + "-" + Integer.toString(i);
        } else {
          verifiedId = Integer.toString(i);
        }
        i++;
      }
    }
    idRegistry.put(verifiedId, obj);
  	obj.setId(id == null ? verifiedId : id);

  }

  @Override
  public void projectClosed() {
    idRegistry.clear();
  }

  @Override
  public void projectLoaded() {
    selectionManager.projectLoaded();
    owlManager.projectLoaded();
  }

  public void setDebug() {
    debugListeners.forEach(DebugListener::setDebug);
  }

  public void addDebugListener(DebugListener listener) {
    debugListeners.add(listener);
  }

  @Override
  public void dispose() {
    textSourceManager.dispose();
    owlManager.dispose();
    selectionManager.dispose();
    profileManager.dispose();
  }

  public void addViewListener(ViewListener listener) {
    viewListeners.add(listener);
  }

  public void refreshView() {
    if (projectLoaded) {
      viewListeners.forEach(ViewListener::viewChanged);
    }
  }

  public boolean isProjectLoaded() {
    return projectLoaded;
  }
}
