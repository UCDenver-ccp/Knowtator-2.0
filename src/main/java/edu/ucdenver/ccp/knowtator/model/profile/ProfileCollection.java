package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.Savable;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.BaseKnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfileCollection extends KnowtatorCollection<Profile> implements KnowtatorXMLIO, Savable, BaseKnowtatorModel {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(KnowtatorController.class);

    private final KnowtatorController controller;
    private final List<ColorListener> colorListeners;
    private File profilesLocation;
    private final Profile defaultProfile;

    public ProfileCollection(KnowtatorController controller) {
        super(controller);
        defaultProfile = new Profile(controller, "Default");
        add(defaultProfile);
        this.controller = controller;
        colorListeners = new ArrayList<>();
    }

    public Profile getDefaultProfile() {
        return defaultProfile;
    }

    @Override
    public void save() {
        if (controller.isNotLoading()) {
            forEach(Profile::save);
        }
    }

    public void addColorListener(ColorListener listener) {
        colorListeners.add(listener);
    }

    public Profile addProfile(String profileID) {
        Profile newProfile = get(profileID);
        if (newProfile == null) {
            newProfile = new Profile(controller, profileID);
            add(newProfile);
        }
        setSelection(newProfile);
        return newProfile;
    }

    private void removeProfile(Profile profile) {
        remove(profile);
        if (size() > 0) {
            setSelection(iterator().next());
        }
    }

    public void writeToKnowtatorXML(Document dom, Element root) {
        forEach(profile -> profile.writeToKnowtatorXML(dom, root));
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        for (Node profileNode :
                KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
            Element profileElement = (Element) profileNode;
            String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

            Profile newProfile = addProfile(profileID);
            newProfile.readFromKnowtatorXML(null, profileElement);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
    }

    public void removeActiveProfile() {
        if (getSelection() != getDefaultProfile()) {
            removeProfile(getSelection());
        }
    }

    void fireColorChanged() {
        colorListeners.forEach(ColorListener::colorChanged);
    }

    @Override
    public void dispose() {
        colorListeners.clear();
        super.dispose();
    }

    @Override
    public File getSaveLocation() {
        return profilesLocation;
    }

    @Override
    public void setSaveLocation(File saveLocation) throws IOException {
        profilesLocation = (new File(controller.getSaveLocation(), "Profiles"));
        Files.createDirectories(profilesLocation.toPath());
    }

    @Override
    public Profile get(String id) {
        if (id.equals("Default")) {
            return defaultProfile;
        }
        else {
            return super.get(id);
        }
    }

    @Override
    public void finishLoad() {
        if (size() > 1) {
            setSelection(first());
        } else {
            setSelection(getDefaultProfile());
        }
    }

    @Override
    public void load() {
        if (getSaveLocation() != null) {
            try {
                log.warn("Loading profiles");
                KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
                Files.newDirectoryStream(Paths.get(profilesLocation.toURI()), path -> path.toString().endsWith(".xml"))
                        .forEach(inputFile -> xmlUtil.read(this, inputFile.toFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reset() {

    }
}
