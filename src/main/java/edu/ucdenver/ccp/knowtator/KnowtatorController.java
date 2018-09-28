package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.owl.OWLModel;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollection;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class KnowtatorController extends ProjectManager {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(KnowtatorController.class);
  private SelectionModel selectionModel;

  public FilterModel getFilterModel() {
    return filterModel;
  }

  private final FilterModel filterModel;

  private TextSourceCollection textSourceCollection;
  private ProfileCollection profileCollection;
  private OWLModel owlModel;

  private TreeMap<String, KnowtatorObject> idRegistry;
  private List<DebugListener> debugListeners;


  public KnowtatorController() {
    super();
    idRegistry = new TreeMap<>();
    debugListeners = new ArrayList<>();

    owlModel = new OWLModel(this);
    textSourceCollection = new TextSourceCollection(this);
    profileCollection = new ProfileCollection(this); // manipulates profiles and colors
    filterModel = new FilterModel();
    selectionModel = new SelectionModel();

  }


  @Override
  List<Savable> getManagers() {
    List<Savable> managers = new ArrayList<>();
    managers.add(owlModel);
    managers.add(textSourceCollection);
    managers.add(profileCollection);
    return managers;
  }

  @Override
  void importProject(File profilesLocation, File ontologiesLocation, File articlesLocation, File annotationsLocation, File projectLocation) {
    makeProjectStructure(projectLocation);
    try {
      importToManager(profilesLocation, profileCollection, ".xml");
      importToManager(ontologiesLocation, owlModel, ".obo");
      importToManager(ontologiesLocation, textSourceCollection, ".txt");
      importToManager(ontologiesLocation, textSourceCollection, ".xml");

    } catch (IOException e) {
      e.printStackTrace();
    }
    loadProject();
  }

  public static void main(String[] args) {}


  /*
  GETTERS
   */

  public OWLModel getOWLModel() {
    return owlModel;
  }

  public ProfileCollection getProfileCollection() {
    return profileCollection;
  }

  public TextSourceCollection getTextSourceCollection() {
    return textSourceCollection;
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

  public void setDebug() {
    debugListeners.forEach(DebugListener::setDebug);
  }

  public void addDebugListener(DebugListener listener) {
    debugListeners.add(listener);
  }


  public void dispose() {
    textSourceCollection.dispose();
    owlModel.dispose();
    profileCollection.dispose();
    idRegistry.clear();
  }

  public SelectionModel getSelectionModel() {
    return selectionModel;
  }
}
