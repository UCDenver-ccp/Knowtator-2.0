package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.BaseKnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
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

public class ProfileCollection extends KnowtatorCollection<Profile> implements KnowtatorXMLIO, BaseKnowtatorManager {

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

    @Override
    public Profile getSelection() {
        //Profile should never be null
        try {
            return super.getSelection();
        } catch (NoSelectionException e) {
            setSelection(defaultProfile);
            return defaultProfile;
        }
    }

    public void addColorListener(ColorListener listener) {
        colorListeners.add(listener);
    }

    @Override
    public void add(Profile profile) {
        if (get(profile.getId()) == null) {
            super.add(profile);
        }
    }

    @Override
    public void remove(Profile profile) {
        if (! profile.equals(defaultProfile)) {
            super.remove(profile);
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

            Profile newProfile = new Profile(controller, profileID);
            add(newProfile);
            get(profileID).readFromKnowtatorXML(null, profileElement);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
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
        add(defaultProfile);
    }
}
