package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.listeners.DebugListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.KnowtatorTextBoundObject;
import edu.ucdenver.ccp.knowtator.model.owl.OWLManager;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionManager;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class KnowtatorController extends ProjectManager implements ProjectListener {
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
    super();
    idRegistry = new TreeMap<>();
    debugListeners = new ArrayList<>();

    addProjectListener(this);
    selectionManager = new SelectionManager(this);

    owlManager = new OWLManager(this);
    textSourceManager = new TextSourceManager(this);
    profileManager = new ProfileManager(this); // manipulates profiles and colors



    viewListeners = new ArrayList<>();
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
  public void save() {

  }

  @Override
  public void load() {

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

  @Override
  public void makeDirectory() {

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
