package edu.ucdenver.ccp.knowtator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public interface Savable extends Serializable {
    void save();

    void load();

    File getSaveLocation();

    void setSaveLocation(File saveLocation) throws IOException;

    void finishLoad();
}
