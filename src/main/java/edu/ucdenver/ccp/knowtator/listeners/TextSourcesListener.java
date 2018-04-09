package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;

public interface TextSourcesListener {
    void textSourcesChanged(TextSource textSource, Boolean wasAdded);
}
