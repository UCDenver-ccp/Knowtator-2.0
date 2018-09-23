package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.SavableKnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ColorListener;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
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

public class ProfileManager extends ProfileCollection implements KnowtatorXMLIO, SavableKnowtatorManager {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(KnowtatorController.class);

    private KnowtatorController controller;
    private List<ColorListener> colorListeners;
    private File profilesLocation;

    public ProfileManager(KnowtatorController controller) {
        super(controller);
        this.controller = controller;
        colorListeners = new ArrayList<>();
        setSelection(getDefaultProfile());
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
        setSelection(iterator().next());
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
        save();
    }

    @Override
    public void dispose() {
        super.dispose();
        colorListeners.clear();
    }

    @Override
    public File getSaveLocation() {
        return profilesLocation;
    }

    @Override
    public void setSaveLocation(File saveLocation) throws IOException {
        this.profilesLocation = saveLocation;
        Files.createDirectories(profilesLocation.toPath());
    }

    @Override
    public void makeDirectory() throws IOException {
        setSaveLocation(new File(controller.getSaveLocation(), "Profiles"));

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
    public void save() {
        getCollection().forEach(Profile::save);
    }
}
