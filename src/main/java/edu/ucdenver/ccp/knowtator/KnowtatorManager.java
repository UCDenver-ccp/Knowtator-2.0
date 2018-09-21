package edu.ucdenver.ccp.knowtator;

import java.io.IOException;

public interface KnowtatorManager {
    void dispose();

    void makeDirectory() throws IOException;
}
