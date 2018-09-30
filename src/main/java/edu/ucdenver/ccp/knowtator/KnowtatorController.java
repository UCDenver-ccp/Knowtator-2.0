package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.FilterModel;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.OWLModel;
import edu.ucdenver.ccp.knowtator.model.SelectionModel;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollection;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class KnowtatorController extends ProjectManager implements KnowtatorObjectInterface {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(KnowtatorController.class);
  private final List<KnowtatorObjectInterface> models;

  public FilterModel getFilterModel() {
    return filterModel;
  }


  private final SelectionModel selectionModel;
  private final FilterModel filterModel;
  private final TextSourceCollection textSourceCollection;
  private final ProfileCollection profileCollection;
  private final OWLModel owlModel;

  private final TreeMap<String, KnowtatorDataObjectInterface> idRegistry;


  public KnowtatorController() {
    super();
    idRegistry = new TreeMap<>();

    models = new ArrayList<>();
    owlModel = new OWLModel();
    textSourceCollection = new TextSourceCollection(this);
    profileCollection = new ProfileCollection(this); // manipulates profiles and colors
    filterModel = new FilterModel();
    selectionModel = new SelectionModel();

    models.add(owlModel);
    models.add(textSourceCollection);
    models.add(profileCollection);
    models.add(filterModel);
    models.add(selectionModel);

  }


  @Override
  List<Savable> getManagers() {
    return models.stream().filter(model -> model instanceof Savable).map(model -> (Savable) model).collect(Collectors.toList());
  }

  @Override
  void importProject(File profilesLocation, File ontologiesLocation, File articlesLocation, File annotationsLocation, File projectLocation) {
    makeProjectStructure(projectLocation);
    try {
      importToManager(profilesLocation, profileCollection, ".xml");
      importToManager(ontologiesLocation, owlModel, ".obo");
      importToManager(articlesLocation, textSourceCollection, ".txt");
      importToManager(annotationsLocation, textSourceCollection, ".xml");

    } catch (IOException e) {
      e.printStackTrace();
    }
    loadProject();
  }

  /*
  GETTERS
   */
  public OWLModel getOWLModel() {
    return owlModel;
  }

  public ProfileCollection getProfileCollection() {
    return profileCollection;
  }

  public SelectionModel getSelectionModel() {
    return selectionModel;
  }

  public TextSourceCollection getTextSourceCollection() {
    return textSourceCollection;
  }

  public void verifyId(String id, KnowtatorDataObjectInterface obj, Boolean hasPriority) {
  	String verifiedId = id;
    if (hasPriority && idRegistry.keySet().contains(id)) {
		verifyId(id, idRegistry.get(id), false);
    } else {
      int i = idRegistry.size();
      while (verifiedId == null || idRegistry.keySet().contains(verifiedId)) {
        if (obj instanceof KnowtatorTextBoundDataObjectInterface && ((KnowtatorTextBoundDataObjectInterface) obj).getTextSource() != null) {
          verifiedId = ((KnowtatorTextBoundDataObjectInterface) obj).getTextSource().getId() + "-" + Integer.toString(i);
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
  public void dispose() {
    models.forEach(KnowtatorObjectInterface::dispose);
    idRegistry.clear();
  }

  public static void main(String[] args) {
    log.warn("Knowtator");
  }
}
