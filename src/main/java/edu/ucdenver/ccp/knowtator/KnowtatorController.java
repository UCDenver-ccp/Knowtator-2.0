package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.listeners.DebugListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.owl.OWLAPIDataExtractor;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KnowtatorController implements Savable, ProjectListener {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(KnowtatorController.class);

  private ProjectManager projectManager;
  private TextSourceManager textSourceManager;
  private ProfileManager profileManager;
  private SelectionManager selectionManager;
  private OWLAPIDataExtractor owlDataExtractor;
  private TreeMap<String, KnowtatorObject> idRegistry;
  private List<DebugListener> debugListeners;

  public KnowtatorController() {
    idRegistry = new TreeMap<>();
    debugListeners = new ArrayList<>();
    projectManager = new ProjectManager(this); // reads and writes to XML
    owlDataExtractor = new OWLAPIDataExtractor(this);
    selectionManager = new SelectionManager(this);
    textSourceManager = new TextSourceManager(this);
    profileManager = new ProfileManager(this); // manipulates profiles and colors

    projectManager.addListener(this);
  }

  public void setProjectManager(ProjectManager projectManager) {
    this.projectManager = projectManager;
    projectManager.addListener(this);
    projectManager.addListener(selectionManager);
    projectManager.addListener(owlDataExtractor);
  }

  public static void main(String[] args) {}

  public ProfileManager getProfileManager() {
    return profileManager;
  }

  public ProjectManager getProjectManager() {
    return projectManager;
  }

  /** GETTERS */
  public OWLAPIDataExtractor getOWLAPIDataExtractor() {
    return owlDataExtractor;
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
    owlDataExtractor.projectLoaded();
  }

  public void setDebug() {
    debugListeners.forEach(DebugListener::setDebug);
  }

  public void addDebugListener(DebugListener listener) {
    debugListeners.add(listener);
  }

  public void dispose() {
    textSourceManager.dispose();
    owlDataExtractor.dispose();
    selectionManager.dispose();
    profileManager.dispose();
    projectManager.dispose();
  }
}
