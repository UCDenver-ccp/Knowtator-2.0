package edu.ucdenver.ccp.knowtator.model.io;

import edu.ucdenver.ccp.knowtator.model.Savable;

import java.io.File;
import java.io.IOException;

public interface BasicIOUtil {
    void read(Savable savable, File file) throws IOException;
    void write(Savable savable, File file) throws IOException;
}
