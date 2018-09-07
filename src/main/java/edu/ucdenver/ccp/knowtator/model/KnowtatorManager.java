package edu.ucdenver.ccp.knowtator.model;

import java.io.File;
import java.io.IOException;

public interface KnowtatorManager extends Savable {
    void dispose();
    File getSaveLocation(String extension);
    void setSaveLocation(File newSaveLocation, String extension) throws IOException;
}
