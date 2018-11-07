package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.io.BasicIO;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollection;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class KnowtatorController extends ProjectManager implements KnowtatorObjectInterface {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(KnowtatorController.class);
    private final List<BaseKnowtatorModel> models;

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
        textSourceCollection = new TextSourceCollection(this);
        owlModel = new OWLModel(this);
        profileCollection = new ProfileCollection(this); // manipulates profiles and colors
        filterModel = new FilterModel();
        selectionModel = new SelectionModel();

        models.add(owlModel);
        models.add(profileCollection);
        models.add(textSourceCollection);
        models.add(filterModel);
        models.add(selectionModel);

    }


    @Override
    List<BaseKnowtatorManager> getManagers() {
        return models.stream().filter(model -> model instanceof BaseKnowtatorManager).map(model -> (BaseKnowtatorManager) model).collect(Collectors.toList());
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

    public void registerAction(AbstractKnowtatorAction action) {
        if (action != null) {
            try {
                action.execute();
                addEdit(action.getEdit());
            } catch (ActionUnperformableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        models.forEach(KnowtatorObjectInterface::dispose);
        idRegistry.clear();
    }

    public static void main(String[] args) {
        log.warn("Knowtator");
    }

    public void reset(OWLWorkspace owlWorkspace) {
        owlModel.setOwlWorkSpace(owlWorkspace);
        models.forEach(BaseKnowtatorModel::reset);
    }

    @Override
    public <I extends BasicIO> void saveToFormat(Class<? extends BasicIOUtil<I>> ioClass, I basicIO, File file) {
        if (isNotLoading()){
            owlModel.save();
        }
        super.saveToFormat(ioClass, basicIO, file);
    }
}
