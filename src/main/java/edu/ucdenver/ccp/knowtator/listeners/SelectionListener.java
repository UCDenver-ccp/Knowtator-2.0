package edu.ucdenver.ccp.knowtator.listeners;

public interface SelectionListener {

    void selectedAnnotationChanged();

    void selectedSpanChanged();

    void activeGraphSpaceChanged();

    void activeTextSourceChanged();

    void currentProfileChange();
}
