package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorObjectInterface;

public interface BaseKnowtatorModel extends KnowtatorObjectInterface {
    void reset();
    void finishLoad();
}
