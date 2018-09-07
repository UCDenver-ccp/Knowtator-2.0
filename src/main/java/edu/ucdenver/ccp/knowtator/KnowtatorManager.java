package edu.ucdenver.ccp.knowtator;

import java.io.IOException;

abstract public class KnowtatorManager implements Savable {
    protected abstract void dispose();

    abstract public void makeDirectory() throws IOException;
}
